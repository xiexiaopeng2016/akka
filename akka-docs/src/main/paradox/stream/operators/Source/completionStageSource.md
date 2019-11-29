# completionStageSource

流动一个异步源的元素，一旦它给定的 *completion* 运算符完成

@ref[Source operators](../index.md#source-operators)

## 签名

## 描述

Streams the elements of an asynchronous source once its given *completion* operator completes.
If the *completion* fails the stream is failed with that exception.

## 响应流语义

@@@div { .callout }

**emits** the next value from the asynchronous source, once its *completion operator* has completed

**completes** after the asynchronous source completes

@@@

