# collectType 

通过测试每个元素的类型来转换这个流，当元素通过此处理步骤时，它们是所提供类型的实例。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #collectType }

@@@

## 描述

Filter elements that is of a given type.

## 示例

Given stream element classes `Message`, `Ping`, and `Pong`, where `Ping` extends `Message` and `Pong` is an
unrelated class.

Scala
:   @@snip [Collect.scala](/akka-docs/src/test/scala/docs/stream/operators/sourceorflow/Collect.scala) { #collect-elements }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #collect-elements }


From a stream of `Message` elements we would like to collect all elements of type `Ping` that have an `id != 0`,
and then covert to `Pong` with same id.

Scala
:   @@snip [Collect.scala](/akka-docs/src/test/scala/docs/stream/operators/sourceorflow/Collect.scala) { #collectType }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #collectType }

## 响应流语义

@@@div { .callout }

**emits** when the element is of the given type

**backpressures** the element is of the given type and downstream backpressures

**completes** when upstream completes

@@@
