# limitWeighted

Ensure stream boundedness by evaluating the cost of incoming elements using a cost function.

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #limitWeighted }

@@@

## 描述

Ensure stream boundedness by evaluating the cost of incoming elements using a cost function.
Evaluated cost of each element defines how many elements will be allowed to travel downstream.

## 响应流语义

@@@div { .callout }

**emits** when upstream emits and the number of emitted elements has not reached max

**backpressures** when downstream backpressures

**completes** when upstream completes and the number of emitted elements has not reached max

@@@

