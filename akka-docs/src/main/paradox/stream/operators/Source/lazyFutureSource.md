# lazyFutureSource

将一个`Source`的创造和物化推迟到有需求的时候。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #lazyFutureSource }

@@@

## 描述

Invokes the user supplied factory when the first downstream demand arrives. When the returned future completes 
successfully the source switches over to the new source and emits downstream just like if it had been created up front.

Note that asynchronous boundaries (and other operators) in the stream may do pre-fetching which counter acts
the laziness and will trigger the factory immediately.

See also @ref:[lazySource](lazySource.md).

## 响应流语义

@@@div { .callout }

**emits** depends on the wrapped `Source`

**completes** depends on the wrapped `Source`

@@@

