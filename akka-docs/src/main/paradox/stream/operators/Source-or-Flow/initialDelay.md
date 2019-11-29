# initialDelay

Delays the initial element by the specified duration.

@ref[Timer driven operators](../index.md#timer-driven-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #initialDelay }

@@@

## 描述

Delays the initial element by the specified duration.

## 响应流语义

@@@div { .callout }

**emits** when upstream emits an element if the initial delay is already elapsed

**backpressures** when downstream backpressures or initial delay is not yet elapsed

**completes** when upstream completes

**cancels** when downstream cancels

@@@

