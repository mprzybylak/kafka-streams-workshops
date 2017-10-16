package com.mprzybylak.kafkastreamsworkshop.answer

import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest

class Answer1_HelloWorld extends KafkaStreamsTest {

  it should "connect two streams with kafka stream api" in {

    val inputTopic = Seq(("1", "x"))
    val expectedOutputTopic = Seq(("1", "x"))

    test(
      builder => {
        // ANSWER
        builder.stream("topic-in").to("topic-out")
      },
      inputTopic, expectedOutputTopic)
  }
}
