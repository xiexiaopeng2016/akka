# foreachParallel

类似于`foreach`，但允许通过`parallellism`过程调用来并行发生。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #foreachParallel }

@@@

## 描述

类似于`foreach`，但允许通过`parallellism`过程调用来并行发生。

## 响应流语义

@@@div { .callout }

**cancels** 从不

**backpressures** 当前面的并行过程调用尚未完成时

@@@

