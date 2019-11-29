# Source.asSourceWithContext

将一个`Source`转换为一个`SourceWithContext`，这可以在整个流中传播一个上下文每伴随每个元素。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #asSourceWithContext }

@@@

## 描述

将一个`Source`转换为一个`SourceWithContext`，这可以在整个流中传播一个上下文每伴随每个元素。传递给`asSourceWithContext`的函数必须将元素转换为上下文，每个元素一个上下文。
