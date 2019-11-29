# Sink.setup

延迟一个`Sink`的创建，直到物化和访问`ActorMaterializer`和`Attributes`

@ref[Sink operators](../index.md#sink-operators)

@@@ warning

The `setup` operator has been deprecated, use @ref:[fromMaterializer](./fromMaterializer.md) instead. 

@@@

@@@ div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #setup }
@@@

## 描述

Typically used when access to materializer is needed to run a different stream during the construction of a sink.
Can also be used to access the underlying `ActorSystem` from `ActorMaterializer`.