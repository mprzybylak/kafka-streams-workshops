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

### Peek method

There is other useful method that you can use for debuging. It is called `peek` and allows you to apply some operation on key-value pairs, so you can perform more advanced debuging - better printing, logging, applying some conditional logic, etc.

## Application scaffolding

TODO: How to create topology. start/stop. handle errors, etc.

## Serialization and deserialization

TODO: Serde classes - predefined, and writing custom ones
- Serde - short name for **ser**ializers / **de**serializers
