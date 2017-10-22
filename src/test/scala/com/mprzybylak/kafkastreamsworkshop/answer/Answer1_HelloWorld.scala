package com.mprzybylak.kafkastreamsworkshop.answer

import com.madewithtea.mockedstreams.MockedStreams
import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest
import org.apache.kafka.common.serialization.{Serde, Serdes}

class Answer1_HelloWorld extends KafkaStreamsTest {

  val strings: Serde[String] = Serdes.String()

  it should "connect two streams with kafka stream api" in {

    // GIVEN
    val inputTopic = Seq(("1", "x"))
    val expectedOutputTopic = Seq(("1", "x"))

    MockedStreams()

      // WHEN
      .topology(builder => {
        builder.stream(INPUT_TOPIC_NAME).to(OUTPUT_TOPIC_NAME)
      })
      .input(INPUT_TOPIC_NAME, strings, strings, inputTopic)

      // THEN
      .output(OUTPUT_TOPIC_NAME, strings, strings, expectedOutputTopic.size) shouldEqual expectedOutputTopic
  }

  it should "show debug data on console" in {

    // GIVEN
val inputTopic = Seq(("1", "a"), ("2", "b"), ("3", "c"))
val expectedOutputTopic = Seq(("1", "a"), ("2", "b"), ("3", "c"))

MockedStreams()
  .topology(
    builder => {
      // FILL ME
    })
  .input(INPUT_TOPIC_NAME, strings, strings, inputTopic)
  .output(OUTPUT_TOPIC_NAME, strings, strings, expectedOutputTopic.size) shouldEqual expectedOutputTopic
  }

}
