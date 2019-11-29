# Sink.headOption

物化到一个 @scala[`Future[Option[T]]`] @java[`CompletionStage<Optional<T>>`] 里面，它将使用包裹在 @scala[`Some`]@java[`Optional`] 里面的第一个到达的值完成，或者 @scala[一个`None`]@java[一个空的可选]，如果流在没有发出任何元素的情况下完成。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #headOption }

@@@

## 描述

物化到一个 @scala[`Future[Option[T]]`] @java[`CompletionStage<Optional<T>>`] 里面，它将使用包裹在 @scala[`Some`]@java[`Optional`] 里面的第一个到达的值完成，或者 @scala[一个`None`]@java[一个空的可选]，如果流在没有发出任何元素的情况下完成。

## 响应流语义

@@@div { .callout }

**cancels** 接收到一个元素之后

**backpressures** 从不

@@@


