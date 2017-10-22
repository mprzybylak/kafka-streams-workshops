package com.mprzybylak.kafkastreamsworkshop.ex

import com.madewithtea.mockedstreams.MockedStreams
import com.mprzybylak.kafkastreamsworkshop.internals.{KafkaStreamsTest, TemperatureMeasureTimestampExtractor}
import org.apache.kafka.common.serialization.{Serde, Serdes}

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
          // FILL ME
        }
      )
      .config(config(strings, integers, classOf[TemperatureMeasureTimestampExtractor].getName))
      .input(INPUT_TOPIC_NAME, strings, temperatures, inputTopic)
      .output(OUTPUT_TOPIC_NAME, strings, integers, outputTopic.size) shouldEqual outputTopic

  }

  it should "calculate average temperature for 5 hours window hopping each 2 hours" in {

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
          // FILL ME
        }
      )
      .config(config(strings, integers, classOf[TemperatureMeasureTimestampExtractor].getName))
      .input(INPUT_TOPIC_NAME, strings, temperatures, inputTopic)
      .output(OUTPUT_TOPIC_NAME, strings, integers, outputTopic.size) shouldEqual outputTopic
  }
}
