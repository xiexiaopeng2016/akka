# mapConcat

将每个元素转换为零个或多个元素，它们会被逐个地传递给下游。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #mapConcat }

@@@

## 描述

将每个元素转换为零个或多个元素，它们会被逐个地传递给下游。

## 响应流语义

@@@div { .callout }

**emits** when the mapping function returns an element or there are still remaining elements from the previously calculated collection

**backpressures** when downstream backpressures or there are still available elements from the previously calculated collection

**completes** when upstream completes and all remaining elements has been emitted

@@@

