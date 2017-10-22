# Workshops: Kafka Streams

The goal of this workshops is to cover important concepts of Kafka Stream library and to "experience" some of those concepts with bunch of simple rather than hard exercises. Some parts of Kafka Stream is intentionaly omitted (low level processor API, interactive queries, admin stuff)

## General concepts

Before we will start the real workshops - let's start with some concepts that would be nice to know.

### Streams

In most general terms - stream is abstraction over unbound (infinite) data set. The most core features of streams are:
- Streams are ordered
- Immutable data
- Replayable

It is curcial to understand difference between processing data that is stream from processing data that looks like regular collection. First of all - in stream you don't know how much elements you have. Second thing is that you don't know when messages will appear on stream. Because of that you will need differently about some operations over stream than on the same operations over collections that you've already know.

Especially aggregation operation looks totaly different - in case of collection you can calculate aggregated value right away. In case of streams you will need update your results when new input message will appear.

### Techniques of processing streams

There are multiple types of stream processing applications:

**Single event processing**. Also refered as a map-filter operation. It is that type of application when you are processing messages one by one - filtering out not needed and applying transformation on the rest. For those type of messages it is easy to recover after crash. Application need just to start processing data from last processed one.

**Processing with local state**. This kind of applications allows us to do so called windowed aggregations (we will cover concept of *aggregation* and *windows* later). Application need to have some sort of local state that allows to remember message values from the past in order to combine those with new messages. It might be tricky to recover after crash, because we will neet to restore local state somehow.

**Combine results from multiple sub processing** This kind of processing might be considered similar to map-reduce type of algorithms. We process small chunks of data and later we will combine results.

**Stream-Table joins** In other words - we are processing stream and we need to lookup some other resources (like database) in order to enrich messages. This approach is challenging because stream processing is fast, but database lookup is slow. It is crucial to have some sort of caching, or ability to process database in some other form.

**Stream joins** This is the kind of application when we need to join multiple streams in order to calculate results. It is non trivial tasks, because we need to answer lots of difficult questions like - how to match data from both streams? 

There are some common problems that we will writing stream processing apps:
- how to handle out of sequence events?
- how to handle reprocessing?

### Kafka streams ###

Kafka streams API allows us to build stream processing application on the top of existing Kafka infrastrudcture. Big advantage of kafka streams API is that it can be distributed as regular java application - when it will be started it will recognize itself current kafka setup and if there are more instances of application and everything will coordinate itself.

Kafka streams features:
- scalable
- support for exactly once delivery
- stateful/stateless processing
- event-time processing
- you need only kafka cluster running somewhere
- high throughput

There are two APIs for Kafka Streams
- Streams DSL (high level API)
- Processor API (low level - not covered by this workshops)

### Kafka streams scalability ###

How kafka streams scale the topology
- Kafka streams scale application either by spliting into multiple threads or to scale between multiple machines
- Kafka streams divide stream into tasks - task contain group of procesor that can work in parallel
- Number of tasks depends on number of partitions

## Exercises - Introduction

Exercises in this workshops are designed as series of unit tests when there is a blank spot where one should provides implementation that allows test to pass. From technical point of view those are tests that are using MockStreams (https://github.com/jpzk/mockedstreams) library that allows you to unit test your topologies.

```scala
val inputTopic = Seq(("1", "a"), ("2", "b"), ("3", "c"))
val expectedOutputTopic = Seq(("1", "a"), ("2", "b"), ("3", "c"))

MockedStreams()
  .topology(
    builder => {
      // FILL ME
    })
  .input(INPUT_TOPIC_NAME, strings, strings, inputTopic)
  .output(OUTPUT_TOPIC_NAME, strings, strings, expectedOutputTopic.size) shouldEqual expectedOutputTopic
  }
```
In general you should not overthink this syntax. There are two places that you should be focussed on:
- inputTopic / expectedOutput topic - the core of the exercises. You will need to provide topology that transform inputTopic into expectedOutputTopic. Take couple of moment to analyse those sequences
- the "FILL ME" comment - this the place where you will be providing your topology

Everything else should be more or less transparent for you. In some exercises you might see additional comments that will help you with tasks, and also instances of some classes - it can be variables in tests or fields in class - and it probably means that you should use it to pass the tests.

Exercises are designed as a series of tasks - each task is separate unit test scenario in code and have separate description here. The name of the test and description from here should be clear enough for you to start to work on tasks. Before each exercise there will be (in this document) description of concepts that you need to know to pass tests. After each exercise it might be additional description of some other topics like things to remember, other api methods that is nice to know, etc.

If you have problems with resolving exercises there is allso "answers" package in project where you can take a look for an example of how to resolve problem with additional comments explaining what is happening line by line

## Exercise 1 - Hello world

TODO: Update link to unit test with exercise

There are two simple tasks:
- create the simplest possible topology
- get to know print method

### Topologies

From the most general perspective - there are two things to do in order to create stream processing application with kafka streams:
- Build the topology
- Execute the topology

Topology is set of computing steps (called processors), and transition between them. In oder words we can say that topology is a directed acyclic graph (DAG). We have three types of processors:
- regular processors - those are the computing steps - for given input those will apply some logic and pass output to next processor.
- source processors - those are the processors that will connect to input topic, consume messages from it and pass it to the next processor.
- sink processors - those are the processors that will take input from another processor and write it to output topic.

### Creation of simple topology in Kafka Streams

The most important class for Kafka Streams DSL API is the `KStreamBuilder` class. It allows you to create your first source processor. In the scope of this exercise we will use the `builder.stream(...)` method. It will build instance of `KStream[K,V]` class that basically will read from topic that you will provide as an argument to stream method.

On instance of `KStream` you can execute other methods in order to create processor that will read from this `KStream` - for example it might be some transformation on values. For now let's only know about method `to(...)` this method will create sink processor that will write stream to topic with name given as an argument to the method.

It is not easy to debug streaming applications but kafka streams helps you with that by giving you some useful methods. Right know the only important one is `print` - it allows you to see at console what is happening inside given processor. And almost all processors have this method.

### Exercise 1.1 - Hello world - simple topology

GOAL: Get to know how building topologies looks like in kafka-streams by building the most simple possible topology

In order to do that you will need to use two methods:
- `builder.stream(...)` that creates source processor that reads from given topic
- `stream.to(...)` that creates sink processor that writes to given topic

### Exercise 1.1 - Hello world - debuging

GOAL: Be aware about `print` method that will help you with debuging.

Almost all "nodes" of topology allows you to execute `print` method on it. Build the same topology as in previous task, but put print method in various processors and see what will happen at console.

### Worth to know: Peek method

There is other useful method that you can use for debuging. It is called `peek` and allows you to apply some operation on key-value pairs, so you can perform more advanced debuging - better printing, logging, applying some conditional logic, etc.

## Exercise 2 - Basic functions

TODO: Update link to class with exercise

There are four tasks:
- Capitalize first letters of words
- Filter our odd numbers
- Execute external logic for each element in stream
- Split sentences into the words

### Function operations over streams

There are couple of function operation that we need to understand in order to finish this exercise:
- `map` / `mapValues`
- `filter`
- `foreach`
- `flatMapValues`

One of the most wide used thing in functional world is `map` semantic - it allows one to transform one form into another. It can be simple transformtion like multiply value by 2, or it can be transformation that change type - for example change from 2 to word "two". In Kafka streams we have two methods that allows us to use this semantic: `map` and `mapValues`. 

The `mapValues` is very simple method that allows you transform only values. The benefit of this method is that it will never trigger repartioning (because keys will be not touched). Simple transformation of multiplying integer by two might looks like this: `map(i => i * 2)` 

If we need to transform keys or both keys and values we should use `map` method. If key will be changed it will trigger repartitioning.

The next important semantic is `filter` - it allows you to get rid of unwanted entries. The syntax of this method is very simple - we need to provide function that will take key and value and returns boolean value - if we return true - key-value pair will be pomoted to next processor. If it will be false - the pair will be dropped. example of filtering out numbers less than 10 would look like this: `filter((k,v) => x > 10)`

If we want to apply some "external" logic on entries we can use `foreach` method. This method will execute function for every element in stream. This is so called terminal operation so you cannot attach next processor after that operation. The exmaple of using this function to save data into some database might looks like this: `foreach((k,v) => db.store(v))` where db would be some kind of class that handles real storing operation.

`FlatMap` semantic looks very similar to map. The difference is that the result of transformation would be additionaly unpacked. That means if as a result of flat map like method we will get as a result `List[String]` it will be at the end unpacked - so the real return type will be just `String`

### Exercise 2.1 - Basic functions - Capitalize

GOAL: get to know with `map`/`mapValues` functions

In order to finish this task you need to apply transformation on each element in input stream that will capitalize first letter and write result to output stream

### Exercise 2.2 - Basic functions - Filtering odd numbers

GOAL: get to know with `filter` method

In order to finish this task you need to apply filtering on each element to determine if this is the odd or even number. All odd numbers should be removed from stream.

### Exercise 2.3 - Basic functions - Applying logic for each element in stream

GOAL: get to know with `foreach` method

In order to finish this task you need to call `log` method on provided instance of fake `RequestLogger` class. You need to call this method for each records in input stream

### Exercise 2.4 - Basic functions - Spliting sentences into the words

GOAL: get to know with `flatMap` / `flatMapValues` 

In order to finish this task you will need to apply transformation on each element of the stream. Transformation need to transform from string containing sentence into the collection of strings.

### Good to know: Other methods

- `filterNot` it acts like `filter` but applies negation on return value
- `selectKey` allows you to assign new key to existing values
- `branch` allows you to send subset of values to one processor and others to another. In fact you can pass multiple predicates which will split stream into multiple branches. It is not possibe to have the same entry in couple of branches - it can be always in only one branch
- `writeAsText` it is the terminal operation that stores records in file

## Exercise 3 - Aggregation

TODO: Update link to unit test with exercise

There are two simple tasks:
- Get to know how counting aggregation works
- Get to know how perform more general aggregation

### Serdes

Each time when data needs to be materialized in any way (eg read / write topic, transform data from one type to another) we need to provide a way to serialize / deserialize certain types. In kafka streams such operation are handled by so called "serdes" classes (**SER**ialize / **DE**serialize)

The simplest scenario is to setup default key and value serdes with topology configuration with `default.key.serde` and `default.value.serde` configuration entries. In scope of this exercise each unit tests already specifies those values (that is way you was not bothered about serialization during first two exercises). And in case that every transformation in topology results with types that matches default serdes - there is no need to specify it manualy.

In case if we have types that are not matching default serdes we need to specify them - many of the methods that creates processor allows you to pass key and value serdes - just take a look into method signature.

Kafka streams allows you to use some set of serdes for most basic types by calling Serdes methods:
- `Serdes.ByteArray()`, `Serdes.Bytes()` 
- `Serdes.ByteBuffer()`
- `Serdes.Double()`
- `Serdes.Integer()`
- `Serdes.Long()`
- `Serdes.String()`

In case if you would like to have serialization/deserialization over your custom type you need to implements your own serdes. It is outside of the scope of this exercise, since all custom types and serdes are provided, but just to let you know how to do that: You need to have a class that implemnts `org.apache.kafka.common.serialization.Serde` interface. This class will act like factory for serializers and deserializers - you need also provide those by implementing interfaces: `org.apache.kafka.common.serialization.Serializer` and `org.apache.kafka.common.serialization.Deserializer`

### Tables

Streams is an abstraction over unbounded set of data that comes over time. Tables are other way of looking at data. Tables contain current state of data at some point of the time. There is very close relationship between tables and streams. We called that "Duality of streams and tables".

### Aggregation operations 

To be able to perform aggregate operations you need to call one of the grouping method - more generic `group` or more specific `groupByKey`. With those method we will achieve two things:
- We will distribute things that should be aggregated to correct partitions (which for example in case of `groupByKey` means nothing because data is already aligned this way, but with `groupBy` we can be more flexible)
- We will get as a result `KGroupedStream[K,V]` that gives you an API for various aggregation operations

Methods that will be needed for finish exercise are:
- `count`
- `reduce`

The method `count` is very simple it will create `KTable` that will store pair key-count. There is nothing fancy about that.

The method `reduce` is more general abstraction about aggregation operations. You need to pass function that will perform aggregation operation. It is how reduce method will use this function:
- First first element from stream and second element from the stream will be taken and those will be pass to function.
- Function will calculate result
- Result of previous step and the next element from the stream will be taken and those will be pass to function
- The previous step will be continued as long as there are elements in stream

As a result `reduce` will create `KTable` that will store pair key-result or reduction.

The most important thing about aggregation operation here is to understand how they works for streams as opose to regular collections. In case of collection the whole collection will be iterated and aggregated into the single aggregation result. For example let's imagine that we have collection of names and we would like to count each of them:

Input collection will looks like this: `["John", "Jane", "Jane", "Jane", "John"]`
The result of counting will looks like this: `[["John", 2], ["Jane", 3]]`

But it is important to remember that **we cannot do that** in case of stream! Stream is unbounded, so potentialy infinite. We don't know if and when it will finish. So for stream we need to emit result of current state of aggregation for each new message. So in this case:

Values in input streams will come in that order: `["John", "Jane", "Jane", "Jane", "John"]`
We will emit result like this: `[["John", 1], ["Jane", 1], ["Jane", 2], ["Jane", 3], ["John, 2"]]`

As mentioned before result of aggregation is stored inside `KTable` which basically means that we can see current state of aggreegation at some point of time.

### Stores

The aggregation methods returns tables and those tables are stored in so called state stores. It can be anything from in memory hashmap up to local instance of RocksDB. Kafka streams takes care about fault tolerance - change log of stores will be propagated to special topics in kafka and by that it will be replicated accross the cluster. 

### Exercise 3.1 - Aggregation - Counting sold tickets

GOAL: Get to know how aggregation works in general. Create most simple count aggregation

In this task as input stream you will have list of events when someone ("value") is buying ticket to concert of some band ("key"). Our task is to count how much ticket for each band was bought.

There are lots of new concepts here so it is important to remembers during resolving this task that:
- Serdes - we will change types here so it is possible that you might need to provide serdes in some methods
- In order to start aggregation at all `group` or `groupByKey` have to be called first
- To count tickets at some point using `count()` 

### Exercise 3.2 - Aggregation - Find maximum score

GOAL: Get to know how to build more general aggregations

The more general method to use for aggregation is `reduce` it is good when we would like to have aggregation value of the same type like the message's value in stream. In case of this tasks - value in stream is user's score, and we would like to return maxium score.

### Worth to know

There are libraries designed to serialize/deserialize different types that already have support for kafka streams - for example Apache Avro

We can convert table into the stream by calling `table.toStream` method

## Exercise 4 - Windowed Aggregation

TODO: Update link to unit test with exercise

There is one task of calculation windowed average in two flavors:
- using tumbling window
- using hopping window

### Time notion in Kafka-Streams

There are two types of time in kafka streams:
- Timestamp inside the record (needs to be extracted)
- Kafka timestamp 

When Kafka streams produce message it can process timestamp like this:
- if message is result of simple transformation of other message - timestamp will be preserved
- if message is generated by kafka streams - new timestamp will be generated
- for aggregation - timestamp of newest message that update aggregate will be used

### Timestamp Extractors

In order to extract data from record kafka stream will use object called TimestampExtractor. Timestamp extractor provides by kafka streams will read kafka timestamp. What time will be return depends on what are the settings:
- if `log.message.timestamp.type` = `CreateTime` timestamp will be the time of message production (so called producer time)
- if ``log.message.timestamp.type`` = `LogAppendTime` timestamp will be the time when broker stored message (so called broker time)

There are couple of implementation that we can use:

- `FailOnInvalidTimestamp` (default) - If timestamp will be invalid this extractor will throw exception
- `LogAndSkipOnInvalidTimestamp` - in case of incorrect timestamp this extractor will log that event and drop the invalid message
- `UsePreviousTimeOnInvalidTimestamp` - It will use last known correct timestamp in case of incorrect timestamp
- `WallclockTimestampExtractor` - It will not extract any time at all - it will return current system time

We can create our own timestamp extract which is usefull if timestamp that we want to use is embedded inside of record. In order to create our own timestamp extractor we need to implement `org.apache.kafka.streams.processor.TimestampExtractor` and either register it as default extractor or use it in method that allows to create windows.

There are couple of option for dealing with invalid timestamp:
- We can just throw exception
- Return negative timestamp - what will casue drop of message
- Use previous correct timestamp - it is available for us inside extractor
- Do something else - like return current system time

### Windows

Windows allows you to run aggregation operations on some subset of data. The decision about which messages should be considered is based on time. If the time of message is in between windows open and close time - it will be processed in the scope of this window. It is falls outside the windows open and close time - it will not be processed by this window, but it should exist other window that can catch this message.

Features of windows:
- Windows size - how much data we want to look at 
- Advance interval - how often windows will move (eg. Each second, minute, each new event - still you can have window with size = 5 minutes, but different advance interval)
- How long windows is updatable (retention period)? In case of so called late arrivals - window might be updated if still exists. The default value is one day and can be changed via Windows#until() and SessionWindows#until().
- Window alignment - we can align window with time (eg. 00:00 is start of window), or not - new window will start when application starts. Sliding windows are not aligned by very definition

Types of windows:
- Tubmbling window - windows are not overlaping. windows are aligned to epoch with lower bound inclusive and upper bound exclusive
- Hopping window - some subset of window is overlaping. windows are aligned to epoch with lower bound inclusive and upper bound exclusive
- Sliding window - each new event provides new window. Sliding windows are used only for joins. it is not aligned to epoch and both lower and upper bound are inclusive

### Exercise 4.1 - Windowed Aggregation - Average temperature (Tumbling windows)

In this exercise we will use of some custom types, so be aware of using custom serdes. Don't be worry about creation of those, because those are already there:
- for `TemperatureMeasure` class there is `TemperatureMeasureSerde`
- for `AggregatedTemperature` class there is `AggregatedTemperatureSerde`

In order to create windowed aggregate you will need to pass window to aggregate function. There is a factory method `TimeWindows.of(...)` that you will use for creation of tumbling window. It takes value in miliseconds that says how big window is.

As a result of windowed aggregation you will get `KTable[Windowed[K], V]` object. Probably you would like to transform it later in some way to get something that is not typed with `Windowed[K]`

### Exercise 4.2 - Windowed Aggregation - Average temperature (Hopping windows)

This task is basically the same as previous, but here we will count averages for 5h hopping windows with interval of 4h (so so part of windows will be overlaping). It is just a matter of create window with `TimeWindows.of(...).advanceBy(...)` **instead** of just calling `TimeWindows.of(...)`. 

It is much much more important to understand overlaping of the windows, so try to add `print()` or `peek()` call at some point that allows you to see not only data but allso information about windows

## Exercise 5 - Joins

### Joins

Joins operation allows to combine data from two sources - streams or tables (or combination of both).

For joins you need to co-partition data to be sure that the same keys are going to the same partitions for both sources. Because join will take message from the same partition from both topics. If data would be in different partitions - the different tasks will get the data and correct join will be impossible. The only one case when co-partitioning is not needed is the both topics have the same number of topics and the same partitioning strategy. Kafka streams is able to verify only that both topics have the same number of partitions and can throw exception on incorrect setup, but it cannot verify partition strategies

Type of joins:
- **KStream-KStream** - This is always windowed join, because unbounded join on streams will cause that internal store will grow indefinitely.
- **KTable-KTable** - This is allways non-windowed join to mimic database join
- **KStream-KTable** - This is allways non-windowed join. It is basically lookup for table on each new record in KStream.

### TODO Exercises

TODO exercises description

## Further readings

- T. Akidau - The world beyond batch: Streaming 101 (https://www.oreilly.com/ideas/the-world-beyond-batch-streaming-101)
- T. Akidau - The world beyond batch: Streaming 102 (https://www.oreilly.com/ideas/the-world-beyond-batch-streaming-102)
- M. Noll - Distributed, Real-time Joins and Aggregations on User Activity Events using Kafka Streams (https://www.confluent.io/blog/distributed-real-time-joins-and-aggregations-on-user-activity-events-using-kafka-streams/)
- N. Narkhede - Exactly-once Semantics are Possible: Here’s How Kafka Does it(https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/)
- all the things under the "sources"

## Sources
- G. Shapira, T. Palino, N. Narkhede - Kafka: The Definitive Guide (ch.11 - Stream Processing)
- Kafka Streams documentation on confluent.io (https://docs.confluent.io/current/streams/index.html)
