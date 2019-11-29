# concat

在原始上游完成后，给定源的元素将被发射。

@ref[Fan-in operators](../index.md#fan-in-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #concat }

@@@

## 描述

After completion of the original upstream the elements of the given source will be emitted.

## 响应流语义

@@@div { .callout }

**emits** when the current stream has an element available; if the current input completes, it tries the next one

**backpressures** when downstream backpressures

**completes** when all upstreams complete

@@@


## 示例
Scala
:   @@snip [FlowConcatSpec.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/FlowConcatSpec.scala) { #concat }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #concat }