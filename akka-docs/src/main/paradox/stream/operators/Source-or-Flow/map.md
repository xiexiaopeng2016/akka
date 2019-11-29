# map

转换流中的每个元素，通过对它调用流中的一个映射函数，并将返回的值传递到下游。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #map }

@@@

## 描述

转换流中的每个元素，通过对它调用流中的一个映射函数，并将返回的值传递到下游。

## 响应流语义

@@@div { .callout }

**emits** when the mapping function returns an element

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@

## 示例


Scala
:  @@snip [Flow.scala](/akka-docs/src/test/scala/docs/stream/operators/Map.scala) { #imports #map }



