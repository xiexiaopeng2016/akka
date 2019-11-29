# foreachAsync

为接收到的每个元素异步调用一个给定的过程。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #foreachAsync }

@@@

## 描述

为接收到的每个元素异步调用一个给定的过程。注意，如果共享状态从过程中发生了变化，这必须以线程安全的方式完成。

The sink materializes into a  @scala[`Future[Done]`] @java[`CompletionStage<Done>`] which completes when the
stream completes, or fails if the stream fails.

## 示例

Scala
:   @@snip [SinkRecipeDocSpec.scala](/akka-docs/src/test/scala/docs/stream/SinkRecipeDocSpec.scala) { #forseachAsync-processing }

Java
:   @@snip [SinkRecipeDocTest.java](/akka-docs/src/test/java/jdocs/stream/SinkRecipeDocTest.java) { #forseachAsync-processing }

## 响应流语义

@@@div { .callout }

**cancels** when a @scala[`Future`] @java[`CompletionStage`] fails

**backpressures** when the number of @scala[`Future`s] @java[`CompletionStage`s] reaches the configured parallelism

@@@


