# Sink.takeLast

收集从流中发出的最后一个`n`值到集合中。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #takeLast }

@@@

## 描述

Materializes into a @scala[`Future`] @java[`CompletionStage`] of @scala[`immutable.Seq[T]`] @java[`List<In>`] containing the last `n` collected elements when the stream completes.
If the stream completes before signaling at least n elements, the @scala[`Future`] @java[`CompletionStage`]  will complete with the number
of elements taken at that point. 
If the stream never completes, the @scala[`Future`] @java[`CompletionStage`] will never complete.
If there is a failure signaled in the stream the @scala[`Future`] @java[`CompletionStage`] will be completed with failure.

## 响应流语义

@@@div { .callout }

**cancels** never

**backpressures** never

@@@

## 示例

Scala
:   @@snip [TakeLastSinkSpec.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/TakeLastSinkSpec.scala) { #takeLast-operator-example }

Java
:   @@snip [SinkDocExamples.java](/akka-docs/src/test/java/jdocs/stream/operators/SinkDocExamples.java) { #takeLast-operator-example }
