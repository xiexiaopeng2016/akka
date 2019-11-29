# fromCompletionStage

`fromCompletionStage` 在2.6.0中已弃用，请使用 `Source.completionStage`

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #fromCompletionStage }

@@@

## 描述

`fromCompletionStage` has been deprecated in 2.6.0, use @ref:[completionStage](completionStage.md) instead.

Send the single value of the `CompletionStage` when it completes and there is demand.
If the `CompletionStage` completes with `null` stage is completed without emitting a value.
If the `CompletionStage` fails the stream is failed with that exception.

## 响应流语义

@@@div { .callout }

**emits** the future completes

**completes** after the future has completed

@@@

