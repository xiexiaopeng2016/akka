# fromFuture

当`Future`完成并且有需求时，发送它的单个值。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #future }

@@@

## 描述

Send the single value of the `Future` when it completes and there is demand.
If the future fails the stream is failed with that exception.

For the corresponding operator for the Java standard library `CompletionStage` see @ref:[completionStage](completionStage.md).

## 响应流语义

@@@div { .callout }

**emits** the future completes

**completes** after the future has completed

@@@

## 示例
Scala
:  @@snip [SourceFromFuture.scala](/akka-docs/src/test/scala/docs/stream/operators/SourceOperators.scala) { #sourceFromFuture }

