# futureSource

流动给定的future源的元素，一旦它成功完成。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #futureSource }

@@@

## 描述

流动给定的future源的元素，一旦它成功完成。如果future失败了，流也就失败了。

## 响应流语义

@@@div { .callout }

**emits** the next value from the *future* source, once it has completed

**completes** after the *future* source completes

@@@

