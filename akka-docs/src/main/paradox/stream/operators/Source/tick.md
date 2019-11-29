# Source.tick

一个任意对象的一个定期的重复。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #tick }

@@@

## 描述

一个任意对象的一个定期的重复。第一次tick的延迟与后续tick的间隔是分别指定的。

## 响应流语义

@@@div { .callout }

**emits** periodically, if there is downstream backpressure ticks are skipped

**completes** never

@@@

