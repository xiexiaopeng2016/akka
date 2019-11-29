# drop

删除`n`个元素，然后向下游传递任何后续元素。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #drop }

@@@

## 描述

删除`n`个元素，然后向下游传递任何后续元素。

## 响应流语义

@@@div { .callout }

**emits** when the specified number of elements has been dropped already

**backpressures** when the specified number of elements has been dropped and downstream backpressures

**completes** when upstream completes

@@@

