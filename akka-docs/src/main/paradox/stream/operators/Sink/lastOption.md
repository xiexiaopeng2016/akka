# Sink.lastOption

物化一个 @scala[`Future[Option[T]]`]@java[`CompletionStage<Optional<T>>`]，当流完成时，它将使用包裹在一个 @scala[`Some`]@java[`Optional`] 里面的最后一个值完成。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #lastOption }

@@@

## 描述

物化一个 @scala[`Future[Option[T]]`]@java[`CompletionStage<Optional<T>>`]，当流完成时，它将使用包裹在一个 @scala[`Some`]@java[`Optional`] 里面的最后一个值完成。如果流不包含元素就完成，则使用 @scala[`None`]@java[一个空的`Optional`] 完成`CompletionStage`。

## 响应流语义

@@@div { .callout }

**cancels** never

**backpressures** never

@@@

## 示例

Scala
:   @@snip [LastSinkSpec.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/LastSinkSpec.scala) { #lastOption-operator-example }

Java
:   @@snip [SinkDocExamples.java](/akka-docs/src/test/java/jdocs/stream/operators/SinkDocExamples.java) { #lastOption-operator-example }