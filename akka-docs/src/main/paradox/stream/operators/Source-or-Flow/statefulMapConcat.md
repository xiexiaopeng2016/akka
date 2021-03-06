# statefulMapConcat

Transform each element into zero or more elements that are individually passed downstream.

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #statefulMapConcat }

@@@

## 描述

Transform each element into zero or more elements that are individually passed downstream. The difference to `mapConcat` is that
the transformation function is created from a factory for every materialization of the flow.

## 响应流语义

@@@div { .callout }

**emits** when the mapping function returns an element or there are still remaining elements from the previously calculated collection

**backpressures** when downstream backpressures or there are still available elements from the previously calculated collection

**completes** when upstream completes and all remaining elements has been emitted

@@@

