# takeWithin

Pass elements downstream within a timeout and then complete.

@ref[Timer driven operators](../index.md#timer-driven-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #takeWithin }

@@@

## 描述

Pass elements downstream within a timeout and then complete.

## 响应流语义

@@@div { .callout }

**emits** when an upstream element arrives

**backpressures** downstream backpressures

**completes** upstream completes or timer fires

@@@

