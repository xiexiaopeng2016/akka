# fromFutureSource

`fromFutureSource` 在2.6.0中已弃用，请用 `Source.futureSource` 代替。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #fromFutureSource }

@@@

## 描述

`fromFutureSource` has been deprecated in 2.6.0, use @ref:[futureSource](futureSource.md) instead.

Streams the elements of the given future source once it successfully completes. 
If the future fails the stream is failed.

## 响应流语义

@@@div { .callout }

**emits** the next value from the *future* source, once it has completed

**completes** after the *future* source completes

@@@

