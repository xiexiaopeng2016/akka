# foreach

为接收到的每个元素调用一个给定的过程。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #forEach }

@@@

## 描述

为接收到的每个元素调用一个给定的过程。注意，从过程中改变共享状态是不安全的。

The sink materializes into a  @scala[`Future[Option[Done]]`] @java[`CompletionStage<Optional<Done>`] which completes when the
stream completes, or fails if the stream fails.

Note that it is not safe to mutate state from the procedure.

## 响应流语义

@@@div { .callout }

**cancels** never

**backpressures** when the previous procedure invocation has not yet completed

@@@


