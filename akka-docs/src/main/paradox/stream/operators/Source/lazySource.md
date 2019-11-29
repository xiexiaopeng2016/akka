# lazySource

将一个`Source`的创建和物化推迟到有需求的时候。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #lazySource }

@@@

## 描述

Defers creation and materialization of a `Source` until there is demand, then emits the elements from the source
downstream just like if it had been created up front.

See also @ref:[lazyFutureSource](lazyFutureSource.md).

Note that asynchronous boundaries (and other operators) in the stream may do pre-fetching which counter acts
the laziness and will trigger the factory immediately.


## 响应流语义

@@@div { .callout }

**emits** depends on the wrapped `Source`

**completes** depends on the wrapped `Source`

@@@

