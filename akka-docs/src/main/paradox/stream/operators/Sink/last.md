# Sink.last

物化到一个 @scala[`Future`]@java[`CompletionStage`] 里面，它将在流完成时，使用最后发出的值来完成。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #last }

@@@

## 描述

物化到一个 @scala[`Future`]@java[`CompletionStage`] 里面，它将在流完成时，使用最后发出的值来完成。如果流完成时不包含元素，则 @scala[`Future`] @java[`CompletionStage`] 是失败的。

## 响应流语义

@@@div { .callout }

**cancels** 从不

**backpressures** 从不

@@@

## 示例

Scala
:   @@snip [LastSinkSpec.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/LastSinkSpec.scala) { #last-operator-example }

Java
:   @@snip [SinkDocExamples.java](/akka-docs/src/test/java/jdocs/stream/operators/SinkDocExamples.java) { #last-operator-example }