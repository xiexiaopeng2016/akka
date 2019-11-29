# filterNot

使用一个谓词过滤传入元素。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #filterNot }

@@@

## 描述

使用一个谓词过滤传入元素。如果谓词返回false，则将元素传递给下游，如果返回true，则丢弃该元素。

## 响应流语义

@@@div { .callout }

**emits** when the given predicate returns false for the element

**backpressures** when the given predicate returns false for the element and downstream backpressures

**completes** when upstream completes

@@@

