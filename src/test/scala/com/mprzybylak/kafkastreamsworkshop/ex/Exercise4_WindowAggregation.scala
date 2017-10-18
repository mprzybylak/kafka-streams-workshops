package com.mprzybylak.kafkastreamsworkshop.ex

import com.madewithtea.mockedstreams.MockedStreams
import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream._

class Exercise4_WindowAggregation extends KafkaStreamsTest {

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
      (BCN, TemperatureMeasure(24, 1)),
      (MUN, TemperatureMeasure(15, 2)),
      (BCN, TemperatureMeasure(24, 3)),
      (MUN, TemperatureMeasure(17, 4)),
      (BCN, TemperatureMeasure(22, 5)),

      // SECOND WINDOW
      (MUN, TemperatureMeasure(18, 6)),
      (BCN, TemperatureMeasure(24, 7)),
      (MUN, TemperatureMeasure(15, 8)),
      (BCN, TemperatureMeasure(26, 9)),
      (MUN, TemperatureMeasure(13, 10)),

      // THIRD WINDOW
      (BCN, TemperatureMeasure(28, 11)),
      (MUN, TemperatureMeasure(15, 12)),
      (BCN, TemperatureMeasure(25, 13)),
      (MUN, TemperatureMeasure(12, 14)),
      (BCN, TemperatureMeasure(24, 15)),

      // FOURTH WINDOW
      (MUN, TemperatureMeasure(14, 16)),
      (BCN, TemperatureMeasure(26, 17)),
      (MUN, TemperatureMeasure(15, 18)),
      (BCN, TemperatureMeasure(21, 19)),
      (MUN, TemperatureMeasure(11, 20)),
    )

    val outputTopic = Seq[(String, java.lang.Double)](
      ("Barcelona", 24D)
    )

    MockedStreams()
      .topology(
        builder =>
        {
          val source: KStream[String, java.lang.Integer] = builder.stream(INPUT_TOPIC_NAME)
          val group: KGroupedStream[String, java.lang.Integer] = source.groupByKey(strings, integers)

          val aggregator: Aggregator[String, Integer, AggregatedTemperature] = (k: String, v: Integer, a: AggregatedTemperature) => {a.add(v); a}

          val aggregate: KTable[Windowed[String], AggregatedTemperature] = group.aggregate(
            () => new AggregatedTemperature(),
            aggregator,
            TimeWindows.of(18000000),
            new AggregatedTemperatureSerde(),
            "temperature-store"
          )

          val aggregatedStream: KStream[String, AggregatedTemperature] = aggregate.toStream((k:Windowed[String], v:AggregatedTemperature) => k.key())
          val aggregatedStreamMapped: KStream[String, Integer] = aggregatedStream.mapValues((t:AggregatedTemperature) => t.average())
          aggregatedStreamMapped.to(strings, integers, OUTPUT_TOPIC_NAME)

        }
      )
      .config(config(strings, integers, classOf[TemperatureMeasureTimestampExtractor].getName))
      .input(INPUT_TOPIC_NAME, strings, temperatures, inputTopic)
      .output(OUTPUT_TOPIC_NAME, strings, integers, outputTopic.size) shouldEqual outputTopic

  }

  it should "calculate average temperature for 5 hours window hopping each 2 hours" in {
    // TODO implement
  }

  it should "calculate average temperature for 5 hours sliding window" in {
    // TODO how to prepare sliding window for kafka streams?
  }

}
