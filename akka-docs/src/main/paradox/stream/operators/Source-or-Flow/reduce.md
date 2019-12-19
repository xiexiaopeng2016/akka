# reduce

开始第一个元素，然后将当前值和下一个值应用于给定函数，当上游完成时，当前值向下游发出。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #reduce }

@@@

## 描述

开始第一个元素，然后将当前值和下一个值应用于给定函数，当上游完成时，当前值向下游发出。类似于`fold`。

## 响应流语义

@@@div { .callout }

**emits** when upstream completes

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@

