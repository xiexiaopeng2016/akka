# fromSourceCompletionStage

`fromSourceCompletionStage` 在2.6.0中已弃用，请用 `Source.completionStageSource` 代替.

@ref[Source operators](../index.md#source-operators)

## 签名

## 描述

`fromSourceCompletionStage` has been deprecated in 2.6.0, use @ref:[completionStageSource](completionStageSource.md) instead.

Streams the elements of an asynchronous source once its given *completion* operator completes.
If the *completion* fails the stream is failed with that exception.

## 响应流语义

@@@div { .callout }

**emits** the next value from the asynchronous source, once its *completion operator* has completed

**completes** after the asynchronous source completes

@@@

