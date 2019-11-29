# initialTimeout

If the first element has not passed through this operators before the provided timeout, the stream is failed with a `TimeoutException`.

@ref[Time aware operators](../index.md#time-aware-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #initialTimeout }

@@@

## 描述

If the first element has not passed through this operators before the provided timeout, the stream is failed
with a `TimeoutException`.

## 响应流语义

@@@div { .callout }

**emits** when upstream emits an element

**backpressures** when downstream backpressures

**completes** when upstream completes or fails if timeout elapses before first element arrives

**cancels** when downstream cancels

@@@

