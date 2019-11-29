# Flow.completionStageFlow

流动元素通过给定的future flow，一旦它成功完成。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #futureFlow }

@@@

## 描述

Streams the elements through the given flow once the `CompletionStage` successfully completes. 
If the future fails the stream is failed.

## 示例

A deferred creation of the stream based on the initial element by combining `completionStageFlow`
with `prefixAndTail` like so:

Scala
:   @@snip [FutureFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/flow/FutureFlow.java) { #base-on-first-element }


## 响应流语义

@@@div { .callout }

**emits** when the internal flow is successfully created and it emits

**backpressures** when the internal flow is successfully created and it backpressures

**completes** when upstream completes and all elements have been emitted from the internal flow

**completes** when upstream completes and all futures have been completed and all elements have been emitted

@@@

