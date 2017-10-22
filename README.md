# kafka-streams-worksops

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

### Methods to know

### EX

### Worth to know

There are libraries designed to serialize/deserialize different types that already have support for kafka streams - for example Apache Avro

## Application scaffolding

TODO: How to create topology. start/stop. handle errors, etc.

## Serialization and deserialization

TODO: Serde classes - predefined, and writing custom ones
- Serde - short name for **ser**ializers / **de**serializers
