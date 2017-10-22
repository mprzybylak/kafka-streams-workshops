package com.mprzybylak.kafkastreamsworkshop.ex

import com.madewithtea.mockedstreams.MockedStreams
import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest
import org.apache.kafka.common.serialization.{Serde, Serdes}

class Exercise1_HelloWorld extends KafkaStreamsTest {

  val strings: Serde[String] = Serdes.String()

  it should "connect two streams with kafka stream api" in {

    // GIVEN
    val inputTopic = Seq(("1", "x"))
    val expectedOutputTopic = Seq(("1", "x"))

    MockedStreams()

      /// WHEN
      .topology(builder => {
        // FILL ME
        // INPUT_TOPIC_NAME / OUTPUT_TOPIC_NAME values contains names of topic to use
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

      /// WHEN
      .topology(builder => {
        // FILL ME
      })
      .input(INPUT_TOPIC_NAME, strings, strings, inputTopic)

      // THEN
      .output(OUTPUT_TOPIC_NAME, strings, strings, expectedOutputTopic.size) shouldEqual expectedOutputTopic
  }

}
