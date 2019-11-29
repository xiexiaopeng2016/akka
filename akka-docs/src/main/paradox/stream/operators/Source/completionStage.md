# completionStage

当`CompletionStage`完成并且有需求时，发送它的单个值。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #completionStage }

@@@

## 描述

Send the single value of the `CompletionStage` when it completes and there is demand.
If the `CompletionStage` completes with `null` stage is completed without emitting a value.
If the `CompletionStage` fails the stream is failed with that exception.

For the corresponding operator for the Scala standard library `Future` see @ref:[future](future.md).

## 响应流语义

@@@div { .callout }

**emits** the future completes

**completes** after the future has completed

@@@

