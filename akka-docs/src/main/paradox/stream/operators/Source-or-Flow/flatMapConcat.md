# flatMapConcat

Transform each input element into a `Source` whose elements are then flattened into the output stream through concatenation.

@ref[Nesting and flattening operators](../index.md#nesting-and-flattening-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #flatMapConcat }

@@@

## 描述

Transform each input element into a `Source` whose elements are then flattened into the output stream through
concatenation. This means each source is fully consumed before consumption of the next source starts.

## 响应流语义

@@@div { .callout }

**emits** when the current consumed substream has an element available

**backpressures** when downstream backpressures

**completes** when upstream completes and all consumed substreams complete

@@@

