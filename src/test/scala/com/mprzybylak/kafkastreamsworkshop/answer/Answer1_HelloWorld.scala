package com.mprzybylak.kafkastreamsworkshop.answer

import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest

class Answer1_HelloWorld extends KafkaStreamsTest {

  it should "connect two streams with kafka stream api" in {

    val inputTopic = Seq(("1", "x"))
    val expectedOutputTopic = Seq(("1", "x"))

    test(
      builder => {
        // ANSWER
        builder
          .stream("topic-in") // this is "source" processor - it subscribes to some topic
          .to("topic-out") // this is "sink" processor - it push result of computation to some topic
      },
      inputTopic, expectedOutputTopic)
  }
}
