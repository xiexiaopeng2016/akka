# Sink.preMaterialize

物化这个接收器，立即返回 (1) 它的物化值，(2) 一个新的Sink，它可以消费元素'到'的一个预物化SinK。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #preMaterialize }

@@@

## 描述

Materializes this Sink, immediately returning (1) its materialized value, and (2) a new Sink that can be consume elements 'into' the pre-materialized one. Useful for when you need a materialized value of a Sink when handing it out to someone to materialize it for you.

