# zipAll

Combines all elements from each of multiple sources into @scala[tuples] @java[*Pair*] and passes the @scala[tuples] @java[pairs] downstream.

@ref[Fan-in operators](../index.md#fan-in-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #zipAll }

@@@

## 描述

Combines all elements from each of multiple sources into @scala[tuples] @java[*Pair*] and passes the @scala[tuples] @java[pairs] downstream.

## 响应流语义

@@@div { .callout }

**emits** at first emits when both inputs emit, and then as long as any input emits (coupled to the default value of the completed input).

**backpressures** when downstream backpressures

**completes** when all upstream completes

@@@

## 示例
Scala
:   @@snip [FlowZipSpec.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/FlowZipSpec.scala) { #zip }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #zip }