# Sink.queue

物化一个`SinkQueue`，它可以通过接收器'拉'来触发需求。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #queue }

@@@

## 描述

Materialize a `SinkQueue` that can be pulled to trigger demand through the sink. The queue contains a buffer in case stream emitting elements faster than queue pulling them.


## 响应流语义

@@@div { .callout }

**cancels** when  `SinkQueue.cancel` is called

**backpressures** when buffer has some space

@@@

