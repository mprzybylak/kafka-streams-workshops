package com.mprzybylak.kafkastreamsworkshop.ex

import com.madewithtea.mockedstreams.MockedStreams
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.scalatest._

class Exercise1_HelloWorld extends FlatSpec with Matchers {

  val strings: Serde[String] = Serdes.String()

  it should "connect two streams with kafka stream api" in {

    val in = Seq(("1", "x"))
    val out = Seq(("1", "x"))

    MockedStreams()
      .topology(
        builder => builder
        // ???
      )
      .input("topic-in", strings, strings, in)
      .output("topic-out", strings, strings, out.size) shouldEqual out
  }

}
