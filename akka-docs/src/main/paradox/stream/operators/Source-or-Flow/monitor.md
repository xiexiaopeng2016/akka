# monitor

Materializes to a `FlowMonitor` that monitors messages flowing through or completion of the operators.

@ref[Watching status operators](../index.md#watching-status-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #monitor }

@@@

## 描述

Materializes to a `FlowMonitor` that monitors messages flowing through or completion of the operators. The operators otherwise
passes through elements unchanged. Note that the `FlowMonitor` inserts a memory barrier every time it processes an
event, and may therefore affect performance.

## 响应流语义

@@@div { .callout }

**emits** when upstream emits an element

**backpressures** when downstream **backpressures**

**completes** when upstream completes

@@@

