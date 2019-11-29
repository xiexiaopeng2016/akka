# fold

使用一个函数折叠已发出的元素，每次调用都将获得新元素和前一次折叠调用的结果。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #fold }

@@@

## 描述

使用一个函数折叠已发出的元素，每次调用都将获得新元素和前一次折叠调用的结果。第一次调用将被提供`zero`值。

物化到一个 @scala[`Future`]@java[`CompletionStage`] 里面，当流完成时，它将包括最后一个状态完成。

此操作符允许将值组合成一个结果，不需要一个全局可变状态，而是在调用之间传递状态。

## 响应流语义

@@@div { .callout }

**cancels** never

**backpressures** when the previous fold function invocation has not yet completed

@@@

