# fromFuture

`fromFuture` 在2.6.0中已弃用，请用 `Source.future` 代替。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #fromFuture }

@@@

## 描述

`fromFuture` has been deprecated in 2.6.0, use @ref:[future](future.md) instead.

Send the single value of the `Future` when it completes and there is demand.
If the future fails the stream is failed with that exception.

## 响应流语义

@@@div { .callout }

**emits** the future completes

**completes** after the future has completed

@@@

## 示例

