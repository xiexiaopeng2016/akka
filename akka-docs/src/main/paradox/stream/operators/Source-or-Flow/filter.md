# filter

使用一个谓词过滤传入元素。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #filter }

@@@

## 描述

使用一个谓词过滤传入元素。如果谓词返回true，则元素将向下游传递，如果返回false，则该元素将被丢弃。

## 响应流语义

@@@div { .callout }

**emits** when the given predicate returns true for the element

**backpressures** when the given predicate returns true for the element and downstream backpressures

**completes** when upstream completes

@@@

