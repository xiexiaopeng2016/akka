# combine

使用用户指定的一个策略将多个接收器组合成一个接收器

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #combine }

@@@

## 描述

使用用户指定的一个策略将多个接收器组合成一个接收器

## 响应流语义

@@@div { .callout }

**cancels** depends on the strategy

**backpressures** depends on the strategy

@@@

