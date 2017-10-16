package com.mprzybylak.kafkastreamsworkshop.ex

import com.madewithtea.mockedstreams.MockedStreams
import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.scalatest._

class Exercise1_HelloWorld extends KafkaStreamsTest {

  it should "connect two streams with kafka stream api" in {

    val inputTopic = Seq(("1", "x"))
    val expectedOutputTopic = Seq(("1", "x"))

    test(
      builder => {
        // INPUT_TOPIC_NAME / OUTPUT_TOPIC_NAME values contains names of topic to use
        // FILE ME
      },
      inputTopic, expectedOutputTopic)
  }

}
