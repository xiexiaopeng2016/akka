# maybe

创建一个源，它一旦已物化的 @scala[`Promise`]@java[`CompletableFuture`] 带有一个值完成，就发出。

@ref[Source operators](../index.md#source-operators)

## 签名

Scala
 :   @@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #maybe }

Java
:   @@snip [SourceDocExamples.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceDocExamples.java) { #maybe-signature }

## 描述

Create a source with a materialized @scala[`Promise[Option[T]]`] @java[`CompletableFuture<Optional<T>>`] which
controls what element will be emitted by the Source. This makes it possible to inject a value into a stream
after creation.

* If the materialized promise is completed with a @scala[`Some`]@java[non-empty `Optional`],
  that value will be produced downstream, followed by completion.
* If the materialized promise is completed with a @scala[`None`]@java[empty `Optional`],
  no value will be produced downstream and completion will be signalled immediately.
* If the materialized promise is completed with a failure, then the source will fail with that error.
* If the downstream of this source cancels or fails before the promise has been completed, then the promise will be completed
  with @scala[`None`]@java[empty `Optional`].

`Source.maybe` has some similarities with @scala[@ref:[`Source.fromFuture`](fromFuture.md)]@java[@ref:[`Source.fromCompletionStage`](fromCompletionStage.md)].
One difference is that a new @scala[`Promise`]@java[`CompletableFuture`] is materialized from `Source.maybe` each time
the stream is run while the @scala[`Future`]@java[`CompletionStage`] given to 
@scala[`Source.fromFuture`]@java[`Source.fromCompletionStage`] can only be completed once.

@ref:[`Source.queue`](queue.md) is an alternative for emitting more than one element. 

## 示例

Scala
:   @@snip [SourceOperators.scala](/akka-docs/src/test/scala/docs/stream/operators/SourceOperators.scala) { #maybe }

Java
:   @@snip [SourceDocExamples.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceDocExamples.java) { #maybe }

The `Source.maybe[Int]` will return a @scala[`Promise[Option[Int]]`]@java[`CompletableFuture<Optional<Integer>>`]
materialized value. That @scala[`Promise`]@java[`CompletableFuture`] can be completed later. Each time the stream
is run a new @scala[`Promise`]@java[`CompletableFuture`] is returned. 

## 响应流语义

@@@div { .callout }

**emits** when the returned promise is completed with some value

**completes** after emitting some value, or directly if the promise is completed with no value

@@@

