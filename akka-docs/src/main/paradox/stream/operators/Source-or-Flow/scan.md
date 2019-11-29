# scan

Emit its current value, which starts at `zero`, and then apply the current and next value to the given function, emitting the next current value.

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #scan }

@@@

## 描述

Emit its current value, which starts at `zero`, and then apply the current and next value to the given function,
emitting the next current value. This means that `scan` emits one element downstream before, and upstream elements
will not be requested until, the second element is required from downstream.

Note that the `zero` value must be immutable.

## 响应流语义

@@@div { .callout }

**emits** when the function scanning the element returns a new element

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@

## 示例

Scala
:  @@snip [SourceOrFlow.scala](/akka-docs/src/test/scala/docs/stream/operators/sourceorflow/Scan.scala) { #scan }

Java
:  @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #scan }
