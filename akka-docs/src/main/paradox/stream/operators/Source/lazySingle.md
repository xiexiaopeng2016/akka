# lazySingle

将一个单个元素的源的创建延迟到有需求时。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #lazySingle }

@@@

## 描述

Invokes the user supplied factory when the first downstream demand arrives, then emits the returned single value 
downstream and completes the stream.

Note that asynchronous boundaries (and other operators) in the stream may do pre-fetching which counter acts
the laziness and will trigger the factory immediately.

## 响应流语义

@@@div { .callout }

**emits** when there is downstream demand and the element factory has completed

**completes** after emitting the single element

@@@

