# Sink.lazyInitAsync

`lazyInitAsync` 在2.6.0中已弃用，请用 `Sink.lazyFutureSink` 

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #lazyInitAsync }

@@@

## 描述

`lazyInitAsync` 在2.6.0中已弃用，请用 @ref:[lazyFutureSink](lazyFutureSink.md) 代替。

Creates a real `Sink` upon receiving the first element. Internal `Sink` will not be created if there are no elements,
because of completion or error.

- If upstream completes before an element was received then the @scala[`Future`]@java[`CompletionStage`] is completed with @scala[`None`]@java[an empty `Optional`].
- If upstream fails before an element was received, `sinkFactory` throws an exception, or materialization of the internal
  sink fails then the @scala[`Future`]@java[`CompletionStage`] is completed with the exception.
- Otherwise the @scala[`Future`]@java[`CompletionStage`] is completed with the materialized value of the internal sink.

## 响应流语义

@@@div { .callout }

**cancels** never

**backpressures** when initialized and when created sink backpressures

@@@


