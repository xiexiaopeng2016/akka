# Sink.lazySink

延迟一个`Sink`的创建和物化直到第一个元素。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #lazySink }

@@@

## 描述

When the first element comes from upstream the actual `Sink` is created and materialized.
The internal `Sink` will not be created if the stream completes of fails before any element got through.

The materialized value of the `Sink` will be the materialized value of the created internal flow if it is materialized
and failed with a `akka.stream.NeverMaterializedException` if the stream fails or completes without the flow being materialized.

Can be combined with @ref[prefixAndTail](../Source-or-Flow/prefixAndTail.md) to base the sink on the first element.

See also @ref:[lazyFutureSink](lazyFutureSink.md) and @ref:[lazyCompletionStageSink](lazyCompletionStageSink.md).

## 响应流语义

@@@div { .callout }

**cancels** if the future fails or if the created sink cancels 

**backpressures** when initialized and when created sink backpressures

@@@


