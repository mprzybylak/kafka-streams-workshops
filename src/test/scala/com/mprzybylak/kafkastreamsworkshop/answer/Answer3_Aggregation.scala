package com.mprzybylak.kafkastreamsworkshop.answer

import com.madewithtea.mockedstreams.MockedStreams
import com.mprzybylak.kafkastreamsworkshop.internals.KafkaStreamsTest
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.{KGroupedStream, KStream, KTable}

class Answer3_Aggregation extends KafkaStreamsTest {

  val strings: Serde[String] = Serdes.String()
  val integers: Serde[Integer] = Serdes.Integer()
  val longs: Serde[java.lang.Long] = Serdes.Long()

  private val BCN = "Barcelona"
  private val MUN = "Munich"

  it should "group input stream of concert ticket sold" in {

    // key = band name
    // value = first and last name of ticket buyer
    val inputTopic = Seq[(String, String)](
      ("Metallica", "Yaniv Jelinek"), ("Rammstein", "Anthea Escamilla"), ("Metallica", "Satu Sachs"), ("Luis Fonsi", "Pilar Rademakers"),
      ("Metallica", "Augusta Dorsey"), ("Rammstein", "Ghislain Kovachev"), ("Rammstein", "Jaffar Hoedemaekers"), ("Metallica", "Samson Kozel"),
      ("Luis Fonsi", "Rosana Meggyesfalvi"), ("Rammstein", "Irmgard Borchard")
    )

    val outputTopic = Seq[(String, Long)](
      ("Metallica", 1), ("Rammstein", 1), ("Metallica", 2), ("Luis Fonsi", 1),
      ("Metallica", 3), ("Rammstein", 2), ("Rammstein", 3), ("Metallica", 4),
      ("Luis Fonsi", 2), ("Rammstein", 4)
    )

    MockedStreams()
      .topology(builder => {

        // create kstream from input topic
        val source: KStream[String, String] = builder.stream(INPUT_TOPIC_NAME)

        // in order to perform aggregation operation we always need to start from call to group or groupByKey methods
        // those methods will gather entries with the same key to the same partition. so we can work on that later
        val group: KGroupedStream[String, String] = source.groupByKey()

        // count method will create so called KTable - we cat treat it as a view of current state in some point of time
        // (as a result of aggregation of events from stream to this point of time). In this particular case - count
        // method creates KTable that stores pair - key - and count (how much events with given key arrived up to the
        // some specific point of time)
        val count: KTable[String, java.lang.Long] = group.count("ticketCount")

        // just a transformation from Long to Integer - nothing fancy here
        val intCount: KTable[String, Integer] = count.mapValues(v => v.toInt)

        // write result to output stream
        intCount.toStream().to(strings, integers, OUTPUT_TOPIC_NAME)
      })
      .config(config(strings, strings))
      .input(INPUT_TOPIC_NAME, strings, strings, inputTopic)
      .output(OUTPUT_TOPIC_NAME, strings, integers, outputTopic.size) shouldEqual outputTopic
  }

  it should "find maximum high score on flipper machine per user" in {

    // GIVEN
    val inputTopic = Seq[(String, Integer)](
      ("user1", 101), // key = player name; value = score
      ("johny16", 212),
      ("johny16", 512),
      ("johny16", 300),
      ("user1", 1000),
      ("asdf", 24),
      ("user1", 1112),
      ("asdf", 0),
      ("johny16", 680),
      ("user1", 900)
    )

    val outputTopic = Seq[(String, Integer)](
      ("user1", 101),
      ("johny16", 212),
      ("johny16", 512),
      ("johny16", 512),
      ("user1", 1000),
      ("asdf", 24),
      ("user1", 1112),
      ("asdf", 24),
      ("johny16", 680),
      ("user1", 1112),
    )

    MockedStreams()

      //WHEN
      .topology(builder => {

        // create KStream from input topic
        val source: KStream[String, Integer] = builder.stream(INPUT_TOPIC_NAME)

        // as mentioned in previous exercise - we need start aggregation with grouping operation
        val group: KGroupedStream[String, Integer] = source.groupByKey(strings, integers)

        // reduce method is method that allows us to create results over some sets of data in such way that:
        // reduce will take first and second item in data set and combine it with given lambda (in our case lambda
        // calculates max value).
        // from this point reduce will take result of previous step and the next element of data set - and combine it
        // with the same passed lambda
        // reduce will continue with this algorithm until reach end of dataset
        // in our case it will be currently available data in stream
        val max: KTable[String, Integer] = group.reduce((val1, val2) => if (val1 >= val2) val1 else val2)

        // store results in output topic
        max.toStream.to(strings, integers, OUTPUT_TOPIC_NAME)
      })
      .config(config(strings, integers))
      .input(INPUT_TOPIC_NAME, strings, integers, inputTopic)

      // THEN
      .output(OUTPUT_TOPIC_NAME, strings, integers, outputTopic.size) shouldEqual outputTopic
  }
}
