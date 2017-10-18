package com.mprzybylak.kafkastreamsworkshop.ex

import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest

class Exercise1_HelloWorld extends KafkaStreamsTest {

  it should "connect two streams with kafka stream api" in {

    val inputTopic = Seq(("1", "x"))
    val expectedOutputTopic = Seq(("1", "x"))

    test(
      builder => {
        // INPUT_TOPIC_NAME / OUTPUT_TOPIC_NAME values contains names of topic to use
        // FILL ME
      },
      inputTopic, expectedOutputTopic)
  }

  it should "show debug data on console" in {

    val inputTopic = Seq(("1", "a"), ("2", "b"), ("3", "c"))
    val expectedOutputTopic = Seq(("1", "a"), ("2", "b"), ("3", "c"))

    test(
      builder => {
        // FILL ME
      }, inputTopic, expectedOutputTopic
    )

  }

}
