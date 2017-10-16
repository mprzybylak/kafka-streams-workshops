package com.mprzybylak.kafkastreamsworkshop.ex

import com.madewithtea.mockedstreams.MockedStreams
import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.KStream

class Exercise2_BasicFunctions extends KafkaStreamsTest {

  val strings: Serde[String] = Serdes.String()
  val integers: Serde[Integer] = Serdes.Integer()

  it should "capitalize first letter of words from input topic and pass it to output topic" in {

    val inputTopic = Seq(("1", "lorem"), ("2", "ipsum"), ("3", "dolor"), ("4", "sit"), ("5", "amet"))
    val outputTopic = Seq(("1", "Lorem"), ("2", "Ipsum"), ("3", "Dolor"), ("4", "Sit"), ("5", "Amet"))

    // `capitalize` method inputTopic class String allows to capitalize first letter
    MockedStreams()
      .topology(
        builder => {
          val source: KStream[String, String] = builder.stream("topic-input")
          val map = source.mapValues(_.capitalize)
          map.to("topic-output")
        }
      )
      .config(config(strings, strings))
      .input("topic-inputTopic", strings, strings, inputTopic)
      .output("topic-outputTopic", strings, strings, outputTopic.size) shouldEqual outputTopic
  }

  it should "filter out odd numbers from input topic" in {
    val inputTopic = Seq[(String, Integer)](("1", 1), ("2", 2), ("3", 3), ("4", 4), ("5", 5))
    val outputTopic = Seq(("2", 2), ("4", 4))

    MockedStreams()
      .topology(
        builder => {
          val source: KStream[String, Integer] = builder.stream("topic-input")
          val map: KStream[String, Integer] = source.filter((k, v) => v % 2 == 0)
          map.to("topic-out")
        }
      )
      .config(config(strings, integers))
      .input("topic-inputTopic", strings, integers, inputTopic)
      .output("topic-out", strings, integers, outputTopic.size) shouldEqual outputTopic
  }


}