# zipWithIndex

Zips elements of current flow with its indices.

@ref[Fan-in operators](../index.md#fan-in-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #zipWithIndex }

@@@

## 描述

Zips elements of current flow with its indices.

## 响应流语义

@@@div { .callout }

**emits** upstream emits an element and is paired with their index

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@

## 示例

Scala
:   @@snip [FlowZipWithIndexSpec.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/FlowZipWithIndexSpec.scala) { #zip-with-index }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #zip-with-index }
