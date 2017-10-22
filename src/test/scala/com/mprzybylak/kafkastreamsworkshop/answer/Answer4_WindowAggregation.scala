package com.mprzybylak.kafkastreamsworkshop.answer

import com.madewithtea.mockedstreams.MockedStreams
import com.mprzybylak.kafkastreamsworkshop.internals.{KafkaStreamsTest, TemperatureMeasureTimestampExtractor}
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream._

class Answer4_WindowAggregation extends KafkaStreamsTest {

  val strings: Serde[String] = Serdes.String()
  val integers: Serde[Integer] = Serdes.Integer()
  val longs: Serde[java.lang.Long] = Serdes.Long()
  val doubles: Serde[java.lang.Double] = Serdes.Double()
  val temperatures: TemperatiureMeasureSerde = new TemperatiureMeasureSerde
  val aggregatedTemperatures: AggregatedTemperatureSerde = new AggregatedTemperatureSerde

  private val BCN = "Barcelona"
  private val MUN = "Munich"

  it should "calculate average temperature for 5 hours tumbling window" in {

    val inputTopic = Seq[(String, TemperatureMeasure)](

      // FIRST WINDOW
      (BCN, TemperatureMeasure(temperature = 24, hour = 0)),
      (MUN, TemperatureMeasure(15, 1)),
      (BCN, TemperatureMeasure(24, 2)),
      (MUN, TemperatureMeasure(17, 3)),

      // SECOND WINDOW
      (BCN, TemperatureMeasure(22, 4)),
      (MUN, TemperatureMeasure(18, 5)),
      (BCN, TemperatureMeasure(24, 6)),
      (MUN, TemperatureMeasure(15, 7)),
    )

    val outputTopic = Seq[(String, Integer)](

      // FIRST WINDOW
      (BCN, 24),
      (MUN, 15),
      (BCN, 24),
      (MUN, 16),

      // SECOND WINDOW
      (BCN, 22),
      (MUN, 18),
      (BCN, 23),
      (MUN, 16),
    )

    MockedStreams()
      .topology(
        builder => {

          // create KStream from input topic
          val source: KStream[String, TemperatureMeasure] = builder.stream(strings, temperatures, INPUT_TOPIC_NAME)

          // groupByKey applied in order to be able to run aggregate operations
          val group: KGroupedStream[String, TemperatureMeasure] = source.groupByKey(strings, temperatures)

          // aggregation method
          val aggregator: Aggregator[String, TemperatureMeasure, AggregatedTemperature] = (k: String, v: TemperatureMeasure, a: AggregatedTemperature) => {
            a.add(v.temperature)
          }

          // aggregate method allows you to perform aggregation operations in quite flexible way
          // basically you need to provide two things:
          // 1. initial value for aggregate (here: creation of new instance of AggregatedTemperature)
          // 2. aggregator function (here: function stored in aggregator value)
          //
          // the algorithm of aggregate looks like this:
          // 1. create new initial value
          // 2. take initial value and the first value from stream and pass it to aggregator function
          // 2. take initial value and the second value from stream and pass it to aggregator function
          // 3. continue as long as there are something in stream
          //
          // the most important part here is that this aggregate method works on windows
          // that means that messages from stream will be grouped together based on timestamp
          // in our case all messages in 5h windows will be processed together
          // and this is so called tumbling window - that means, that new window opens the moment when
          // the old one closes.
          //
          // it is worth to remind that windows are aligned with epoch - not with the time of application start
          val aggregate: KTable[Windowed[String], AggregatedTemperature] = group.aggregate(

            // supplier that provides initial value
            () => new AggregatedTemperature(),

            // aggregation function
            aggregator,

            // definition of 5h window: windows are specified in milliseconds
            TimeWindows.of(18000000),

            new AggregatedTemperatureSerde(),
            "temperature-store"
          )

          // we are transforming table into stream
          val aggregatedStream: KStream[String, AggregatedTemperature] = aggregate.toStream((k: Windowed[String], v: AggregatedTemperature) => k.key())

          // another transformation to take average out of AggregatedTemperature object that we've created in previous step
          val aggregatedStreamMapped: KStream[String, Integer] = aggregatedStream.mapValues((t: AggregatedTemperature) => t.average())

          // send results into the output topic
          aggregatedStreamMapped.to(strings, integers, OUTPUT_TOPIC_NAME)

        }
      )
      .config(config(strings, integers, classOf[TemperatureMeasureTimestampExtractor].getName))
      .input(INPUT_TOPIC_NAME, strings, temperatures, inputTopic)
      .output(OUTPUT_TOPIC_NAME, strings, integers, outputTopic.size) shouldEqual outputTopic

  }

  it should "calculate average temperature for 5 hours window hopping each 4 hours" in {

    val inputTopic = Seq[(String, TemperatureMeasure)](
      (BCN, TemperatureMeasure(temperature = 24, hour = 0)),
      (MUN, TemperatureMeasure(15, 1)),
      (BCN, TemperatureMeasure(24, 2)),
      (MUN, TemperatureMeasure(17, 3)),
      (BCN, TemperatureMeasure(22, 4)),
      (MUN, TemperatureMeasure(18, 5)),
      (BCN, TemperatureMeasure(24, 6)),
      (MUN, TemperatureMeasure(15, 7)),
    )

    val outputTopic = Seq[(String, Integer)](
      (BCN, 24),
      (MUN, 15),
      (BCN, 24),
      (BCN, 24),
      (MUN, 17),
      (BCN, 23),
      (MUN, 17),
      (BCN, 23),
      (BCN, 24),
      (MUN, 15),
    )

    MockedStreams()
      .topology(
        builder => {

          // create KStream from input topic
          val source: KStream[String, TemperatureMeasure] = builder.stream(strings, temperatures, INPUT_TOPIC_NAME)

          // groupByKey applied in order to be able to run aggregate operations
          val group: KGroupedStream[String, TemperatureMeasure] = source.groupByKey(strings, temperatures)

          // aggregation method
          val aggregator: Aggregator[String, TemperatureMeasure, AggregatedTemperature] = (k: String, v: TemperatureMeasure, a: AggregatedTemperature) => {
            a.add(v.temperature)
          }

          // aggregate method allows you to perform aggregation operations in quite flexible way
          // basically you need to provide two things:
          // 1. initial value for aggregate (here: creation of new instance of AggregatedTemperature)
          // 2. aggregator function (here: function stored in aggregator value)
          //
          // the algorithm of aggregate looks like this:
          // 1. create new initial value
          // 2. take initial value and the first value from stream and pass it to aggregator function
          // 2. take initial value and the second value from stream and pass it to aggregator function
          // 3. continue as long as there are something in stream
          //
          // the most important part here is that this aggregate method works on windows
          // that means that messages from stream will be grouped together based on timestamp
          // in our case all messages in 5h windows will be processed together
          // and this is so called hopping window with interval equals 4h.
          // that means that after 4h when first window is still open, new window will be created
          // so some messages will go to both first and second window
          //
          // it is worth to remind that windows are aligned with epoch - not with the time of application start
          val aggregate: KTable[Windowed[String], AggregatedTemperature] = group.aggregate(
            () => new AggregatedTemperature(),
            aggregator,
            TimeWindows.of(18000000).advanceBy(14400000),
            new AggregatedTemperatureSerde(),
            "temperature-store"
          )

          // we are transforming table into stream
          val aggregatedStream: KStream[String, AggregatedTemperature] = aggregate.toStream((k: Windowed[String], v: AggregatedTemperature) => k.key())
          val aggregatedStreamMapped: KStream[String, Integer] = aggregatedStream.mapValues((t: AggregatedTemperature) => t.average())
          aggregatedStreamMapped.to(strings, integers, OUTPUT_TOPIC_NAME)

          // for this exercise this step is most important - becasue everything else is almost the same as in previous example
          // we will do additional transformation and we will create stream where key is windows
          // in next step we will print this kstream - notice that results are assigned to different hopping window
          val aggregatedStreamToPrint: KStream[Window, AggregatedTemperature] = aggregate.toStream((k: Windowed[String], v: AggregatedTemperature) => k.window())
          aggregatedStreamToPrint.print()
        }
      )
      .config(config(strings, integers, classOf[TemperatureMeasureTimestampExtractor].getName))
      .input(INPUT_TOPIC_NAME, strings, temperatures, inputTopic)
      .output(OUTPUT_TOPIC_NAME, strings, integers, outputTopic.size) shouldEqual outputTopic
  }
}
