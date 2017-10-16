package com.mprzybylak.kafkastreamsworkshop.internals

import java.util.Properties

import com.madewithtea.mockedstreams.MockedStreams
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.scalatest.{FlatSpec, Matchers}

class KafkaStreamsTest extends FlatSpec with Matchers {

  protected val INPUT_TOPIC_NAME = "topic-in"
  protected val OUTPUT_TOPIC_NAME = "topic-out"

  protected def config(key: Serde[_], value: Serde[_]): Properties = {
    // this is needed in order to test to work without class cast exception
    val props = new Properties()
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, key.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, value.getClass.getName)
    props
  }

  protected def test(topologyBuilder : KStreamBuilder => Unit, in: Seq[(String, String)], out: Seq[(String, String)]): Unit = {
    val strings: Serde[String] = Serdes.String()
    MockedStreams()
      .topology(topologyBuilder)
      .input(INPUT_TOPIC_NAME, strings, strings, in)
      .output(OUTPUT_TOPIC_NAME, strings, strings, out.size) shouldEqual out
  }
}
