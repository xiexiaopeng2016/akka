# mapAsyncUnordered

与`mapAsync`类似，但是 @scala[`Future`]@java[`CompletionStage`] 的结果在到达时向下游传递，而不考虑触发元素的顺序。

@ref[Asynchronous operators](../index.md#asynchronous-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #mapAsyncUnordered }

@@@

## 描述

Like `mapAsync` but @scala[`Future`] @java[`CompletionStage`] results are passed downstream as they arrive regardless of the order of the elements
that triggered them.

If a @scala[`Future`] @java[`CompletionStage`] completes with `null`, element is not passed downstream.
If a @scala[`Future`] @java[`CompletionStage`] fails, the stream also fails (unless a different supervision strategy is applied)

## 响应流语义

@@@div { .callout }

**emits** any of the @scala[`Future` s] @java[`CompletionStage` s] returned by the provided function complete

**backpressures** when the number of @scala[`Future` s] @java[`CompletionStage` s] reaches the configured parallelism and the downstream backpressures

**completes** upstream completes and all @scala[`Future` s] @java[`CompletionStage` s] has been completed  and all elements has been emitted

@@@

