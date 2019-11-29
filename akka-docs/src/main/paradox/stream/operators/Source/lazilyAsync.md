# lazilyAsync

`lazilyAsync` 在2.6.0中已弃用，请用 `Source.lazyFutureSource` 代替。

@ref[Source operators](../index.md#source-operators)

## 签名

## 描述

`lazilyAsync` has been deprecated in 2.6.0, use @ref:[lazyFutureSource](lazyFutureSource.md) instead.

Defers creation and materialization of a `CompletionStage` until there is demand.

## 响应流语义

@@@div { .callout }

**emits** the future completes

**completes** after the future has completed

@@@

