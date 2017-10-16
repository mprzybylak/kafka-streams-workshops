package com.mprzybylak.kafkastreamsworkshop.ex

import com.madewithtea.mockedstreams.MockedStreams
import org.apache.kafka.common.serialization.Serdes
import org.scalatest._

class Exercise1_HelloWorld extends FlatSpec with Matchers {

  it should "connect two streams with kafka stream api" in {

    val strings = Serdes.String()

    val in = Seq(("1", "x"))
    val out = Seq(("1", "x"))

    MockedStreams()
      .topology(
        builder => builder // ???
          //builder.stream("topic-in").to("topic-out")
      )
      .input("topic-in", strings, strings, in)
      .output("topic-out", strings, strings, out.size) shouldEqual out
  }


}
