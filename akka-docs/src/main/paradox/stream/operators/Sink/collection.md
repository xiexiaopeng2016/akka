# Sink.collection

将从流中发出的所有值收集到一个集合中。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #collection }

## 描述

@scala[Collect values emitted from the stream into an arbitrary collection `That`. The resulting collection is available through a `Future[That]` or when the stream completes. Note that the collection boundaries are those defined in the `CanBuildFrom` associated with the chosen collection. See [The Architecture of Scala 2.13's Collections](https://docs.scala-lang.org/overviews/core/architecture-of-scala-213-collections.html) for more info. The [`seq`](seq.html) operator is a shorthand for `Sink.collection[T, Vector[T]]`.]@java[Operator only available in the Scala API. The closest operator in the Java API is [`Sink.seq`](seq.html).]

## 响应流语义

@@@

@@@div { .group-scala .callout }

**cancels** If too many values are collected

@@@
