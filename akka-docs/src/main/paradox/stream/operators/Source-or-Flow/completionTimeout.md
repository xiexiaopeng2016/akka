# completionTimeout

如果流的完成直到提供的timeout还没有发生，则流将失败，并包含一个`TimeoutException`。

@ref[Time aware operators](../index.md#time-aware-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #completionTimeout }

@@@

## 描述

If the completion of the stream does not happen until the provided timeout, the stream is failed
with a `TimeoutException`.

## 响应流语义

@@@div { .callout }

**emits** when upstream emits an element

**backpressures** when downstream backpressures

**completes** when upstream completes or fails if timeout elapses before upstream completes

**cancels** when downstream cancels

@@@

