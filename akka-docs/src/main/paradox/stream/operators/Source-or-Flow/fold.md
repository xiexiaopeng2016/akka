# fold

从当前值`zero`开始，然后将当前值和下一个值应用于给定函数。当上游完成时，当前值向下游发出。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #fold }

@@@

## 描述

从当前值`zero`开始，然后将当前值和下一个值应用于给定函数。当上游完成时，当前值向下游发出。

@@@ warning

Note that the `zero` value must be immutable, because otherwise
the same mutable instance would be shared across different threads
when running the stream more than once.

@@@

## 示例

`fold` is typically used to 'fold up' the incoming values into an aggregate. For example, you might want to summarize the incoming values into a histogram:

Scala
:   @@snip [Fold.scala](/akka-docs/src/test/scala/docs/stream/operators/sourceorflow/Fold.scala) { #imports #histogram #fold }

Java
:   @@snip [Fold.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #fold }

## 响应流语义

@@@div { .callout }

**emits** when upstream completes

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@

