# Source.zipWithN

使用一个组合器函数将多个流的元素组合成一个序列流。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #zipWithN }

@@@

## 描述

使用一个组合器函数将多个流的元素组合成一个序列流。

## 响应流语义

@@@div { .callout }

**emits** when all of the inputs has an element available

**completes** when any upstream completes

@@@


