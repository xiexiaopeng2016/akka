# Sink.ignore

消耗所有元素但丢弃它们。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #ignore }

@@@

## 描述

消耗所有元素但丢弃它们。当一个流必须被消耗，但实际上不需要对元素执行任何操作时，它非常有用。

## 响应流语义

@@@div { .callout }

**cancels** 从不

**backpressures** 从不

@@@


