package com.mprzybylak.kafkastreamsworkshop.ex

import java.util.Properties

import com.madewithtea.mockedstreams.MockedStreams
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams
import org.apache.kafka.streams.{KeyValue, StreamsConfig}
import org.apache.kafka.streams.kstream.KStream
import org.scalatest.{FlatSpec, Matchers}

class Exercise2_BasicFunctions extends FlatSpec with Matchers {

  val strings: Serde[String] = Serdes.String()
  val integers: Serde[Integer] = Serdes.Integer()

  it should "capitalize first letter of words from input stream and pass it to output stream" in {

    // this is needed in order to test to work without class cast exception
    val props = new Properties()
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

    val in = Seq(("1", "lorem"), ("2", "ipsum"), ("3", "dolor"), ("4", "sit"), ("5", "amet"))
    val out = Seq(("1", "Lorem"), ("2", "Ipsum"), ("3", "Dolor"), ("4", "Sit"), ("5", "Amet"))

    // `capitalize` method in class String allows to capitalize first letter
    MockedStreams()
      .topology(
        builder => {
          val source:KStream[String, String] = builder.stream("topic-in")
          val map = source.mapValues(_.capitalize)
          map.to("topic-out")
        }
      )
       .config(props)
      .input("topic-in", strings, strings, in)
      .output("topic-out", strings, strings, out.size) shouldEqual out
  }


  it should "filter out odd numbers from input stream" in {

    // this is needed in order to test to work without class cast exception
    val props = new Properties()
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer.getClass.getName)

    val in = Seq[(String, Integer)](("1", 1), ("2", 2), ("3", 3), ("4", 4), ("5", 5))
    val out = Seq(("2", 2), ("4", 4))

    MockedStreams()
      .topology(
        builder => {
          val source:KStream[String, Integer] = builder.stream("topic-in")
          val map:KStream[String, Integer] = source.filter((k,v) => v % 2 == 0)
          map.to("topic-out")
        }
      )
      .config(props)
      .input("topic-in", strings, integers, in)
      .output("topic-out", strings, integers, out.size) shouldEqual out
  }




}