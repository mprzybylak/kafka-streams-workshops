# kafka-streams-worksops

TODO: Before ex1. Explanation of topology, processor, source, sink

## Exercises - Introduction

TODO

## Exercise 1 - Hello world

TODO: Update link to unit test with exercise

There are two simple tasks:
- create the simplest possible topology
- be aware of existance of method print that helps you with debuging

### Exercise 1.1 - Hello world - simple topology

GOAL: Get to know how building topologies looks like in kafka-streams by building the most simple possible topology

In order to do that you will need to use two methods:
- `builder.stream(...)` that creates source processor that reads from given topic
- `stream.to(...)` that creates sink processor that writes to given topic

### Exercise 1.1 - Hello world - debuging

GOAL: Be aware about `print` method that will help you with debuging.

Almost all "nodes" of topology allows you to execute `print` method on it. This method allows you to see at console what is currently happening in given node of topology. You can use it on input node to see the input from topic, but you can use it for example on processor that transform stream in order to see results.  

### Exercise 1 - Hello world - other useful things

TODO: peek method description

## Application scaffolding

TODO: How to create topology. start/stop. handle errors, etc.

## Serialization and deserialization

TODO: Serde classes - predefined, and writing custom ones
- Serde - short name for **ser**ializers / **de**serializers
