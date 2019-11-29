# head

物化到一个 @scala[`Future`]@java[`CompletionStage`] 里面，它将使用第一个到达的值完成，在此之后，流被取消。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #head }

@@@

## 描述

物化到一个 @scala[`Future`]@java[`CompletionStage`] 里面，它将使用第一个到达的值完成，在此之后，流被取消。如果没有元素发出， @scala[`Future`]@java[`CompletionStage`] 就失败。

## 响应流语义

@@@div { .callout }

**cancels** 接收到一个元素后

**backpressures** 从不

@@@

