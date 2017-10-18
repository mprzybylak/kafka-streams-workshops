package com.mprzybylak.kafkastreamsworkshop.internals

import java.time.{LocalDateTime, Month, ZoneId}
import java.util
import java.util.{Date, Properties}

import com.google.gson.{Gson, GsonBuilder}
import com.madewithtea.mockedstreams.MockedStreams
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes, Serializer}
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.processor.TimestampExtractor
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

  protected def config(key: Serde[_], value: Serde[_], timestampExtractor: String): Properties = {
    val props = config(key, value)
    props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, timestampExtractor)
    props
  }

  protected def test(topologyBuilder: KStreamBuilder => Unit, in: Seq[(String, String)], out: Seq[(String, String)]): Unit = {
    val strings: Serde[String] = Serdes.String()
    MockedStreams()
      .topology(topologyBuilder)
      .input(INPUT_TOPIC_NAME, strings, strings, in)
      .output(OUTPUT_TOPIC_NAME, strings, strings, out.size) shouldEqual out
  }

  protected case class TemperatureMeasure(temperature: Int, timestamp: Long)

  protected object TemperatureMeasure {
    def apply(temperature: Int, hour: Int): TemperatureMeasure = {
      val timestamp = Date.from(LocalDateTime.of(2017, Month.OCTOBER, 18, hour, 0, 0).atZone(ZoneId.systemDefault()).toInstant).getTime
      new TemperatureMeasure(temperature, timestamp)
    }
  }

  protected class TemperatureMeasureTimestampExtractor() extends TimestampExtractor {

    override def extract(record: ConsumerRecord[AnyRef, AnyRef], previousTimestamp: Long): Long = {
      record.value() match {
        case v: TemperatureMeasure => v.timestamp
        case _ => throw new ClassCastException
      }
    }
  }

  protected class TemperatiureMeasureSerde extends Serde[TemperatureMeasure] {

    val builder: Gson = new GsonBuilder().create()

    override def deserializer(): Deserializer[TemperatureMeasure] = {
      new Deserializer[TemperatureMeasure] {
        override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

        override def close(): Unit = {}

        override def deserialize(topic: String, data: Array[Byte]): TemperatureMeasure = {
          builder.fromJson(new String(data), TemperatureMeasure.getClass)
        }
      }
    }

    override def serializer(): Serializer[TemperatureMeasure] = {
      new Serializer[TemperatureMeasure] {
        override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

        override def close(): Unit = {}

        override def serialize(topic: String, data: TemperatureMeasure): Array[Byte] = {
          builder.toJson(data).getBytes
        }
      }
    }

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

    override def close(): Unit = {}
  }


  protected class AggregatedTemperatureSerde extends Serde[AggregatedTemperature] {

    val builder: Gson = new GsonBuilder().create()

    override def deserializer(): Deserializer[AggregatedTemperature] = {
      new Deserializer[AggregatedTemperature] {
        override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

        override def close(): Unit = {}

        override def deserialize(topic: String, data: Array[Byte]): AggregatedTemperature = {
          builder.fromJson(new String(data), classOf[AggregatedTemperature])
        }
      }
    }

    override def serializer(): Serializer[AggregatedTemperature] = {
      new Serializer[AggregatedTemperature] {
        override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

        override def serialize(topic: String, data: AggregatedTemperature): Array[Byte] = {
          builder.toJson(data, classOf[AggregatedTemperature]).getBytes()
        }

        override def close(): Unit = {}
      }
    }

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

    override def close(): Unit = {}
  }

  protected class AggregatedTemperature {
    var temps: List[Int] = List()

    def add(temperature: Int) = {
      temps = temperature :: temps
    }

    def average() = {
      temps.sum / temps.length
    }
  }

}
