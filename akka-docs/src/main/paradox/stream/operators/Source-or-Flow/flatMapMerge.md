# flatMapMerge

Transform each input element into a `Source` whose elements are then flattened into the output stream through merging.

@ref[Nesting and flattening operators](../index.md#nesting-and-flattening-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #flatMapMerge }

@@@

## 描述

Transform each input element into a `Source` whose elements are then flattened into the output stream through
merging. The maximum number of merged sources has to be specified.

## 响应流语义

@@@div { .callout }

**emits** when one of the currently consumed substreams has an element available

**backpressures** when downstream backpressures

**completes** when upstream completes and all consumed substreams complete

@@@


