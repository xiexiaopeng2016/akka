# throttle

将吞吐量限制为每个时间单位特定数量的元素，或每个时间单位特定总成本，这时必须提供一个函数来计算每个元素的单个成本。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #throttle }

@@@

## 描述

将吞吐量限制为每个时间单位特定数量的元素，或每个时间单位特定总成本，这时必须提供一个函数来计算每个元素的单个成本。

## 响应流语义

@@@div { .callout }

**emits** 上游发出一个元素并为每个元素配置消耗时间

**backpressures** 当下游背压

**completes** 当上游完成

@@@

