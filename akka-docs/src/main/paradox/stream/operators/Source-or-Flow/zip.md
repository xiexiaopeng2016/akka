# zip

将来自多个源的每个元素组合到 @scala[元组]@java[*Pair*] 中，并向下游传递 @scala[元组]@java[pairs]。

@ref[Fan-in operators](../index.md#fan-in-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #zip }

@@@

## 描述

将来自多个源的每个元素组合到 @scala[元组]@java[*Pair*] 中，并向下游传递 @scala[元组]@java[pairs]。

## 响应流语义

@@@div { .callout }

**emits** 当所有的输入都有一个元素可用时

**backpressures** 当下游背压时

**completes** 当任何上游完成时

@@@

## 示例
Scala
:   @@snip [FlowZipSpec.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/FlowZipSpec.scala) { #zip }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #zip }