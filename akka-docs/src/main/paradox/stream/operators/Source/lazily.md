# lazily

`lazily` 在2.6.0中已弃用，请用 `Source.lazySource` 代替。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #lazily }

@@@

## 描述

`lazily` has been deprecated in 2.6.0, use @ref:[lazySource](lazySource.md) instead.

Defers creation and materialization of a `Source` until there is demand.

## 响应流语义

@@@div { .callout }

**emits** depends on the wrapped `Source`

**completes** depends on the wrapped `Source`

@@@

