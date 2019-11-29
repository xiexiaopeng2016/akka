# delay

使用特定的时长，延迟通过的每个元素。

@ref[Timer driven operators](../index.md#timer-driven-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #delay }

@@@

## 描述

使用特定的时长，延迟通过的每个元素。

## 响应流语义

@@@div { .callout }

**emits** there is a pending element in the buffer and configured time for this element elapsed

**backpressures** differs, depends on `OverflowStrategy` set

**completes** when upstream completes and buffered elements has been drained


@@@

