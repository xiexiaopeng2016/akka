# fromIterator

从一个`Iterator`中流动值，当有需求时请求下一个值。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #fromIterator }

@@@

## 描述

Stream the values from an `Iterator`, requesting the next value when there is demand. The iterator will be created anew
for each materialization, which is the reason the @scala[`method`] @java[`factory`] takes a @scala[`function`] @java[`Creator`] rather than an `Iterator` directly.

If the iterator perform blocking operations, make sure to run it on a separate dispatcher.

## 响应流语义

@@@div { .callout }

**emits** the next value returned from the iterator

**completes** when the iterator reaches its end

@@@

