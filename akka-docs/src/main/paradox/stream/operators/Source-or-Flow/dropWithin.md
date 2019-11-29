# dropWithin

到了已触发超时的时候，就删除元素

@ref[Timer driven operators](../index.md#timer-driven-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #dropWithin }

@@@

## 描述

Drop elements until a timeout has fired

## 响应流语义

@@@div { .callout }

**emits** after the timer fired and a new upstream element arrives

**backpressures** when downstream backpressures

**completes** upstream completes

@@@

