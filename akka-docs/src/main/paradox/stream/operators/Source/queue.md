# Source.queue

物化一个`SourceQueue`，那些元素可被推到其上，用于从源发出。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #queue }

@@@

## 描述

Materialize a `SourceQueue` onto which elements can be pushed for emitting from the source. The queue contains
a buffer, if elements are pushed onto the queue faster than the source is consumed the overflow will be handled with
a strategy specified by the user. Functionality for tracking when an element has been emitted is available through
`SourceQueue.offer`.

Using `Source.queue` you can push elements to the queue and they will be emitted to the stream if there is
demand from downstream, otherwise they will be buffered until request for demand is received. Elements in the buffer
will be discarded if downstream is terminated.

In combination with the queue, the @ref[`throttle`](./../Source-or-Flow/throttle.md) operator can be used to control the processing to a given limit, e.g. `5 elements` per `3 seconds`.

## 示例

Scala
:   @@snip [IntegrationDocSpec.scala](/akka-docs/src/test/scala/docs/stream/IntegrationDocSpec.scala) { #source-queue }

Java
:   @@snip [IntegrationDocTest.java](/akka-docs/src/test/java/jdocs/stream/IntegrationDocTest.java) { #source-queue }

## 响应流语义

@@@div { .callout }

**emits** when there is demand and the queue contains elements

**completes** when downstream completes

@@@

