# Sink.futureSink

将元素流动到指定的future接收器，一旦它成功完成后。

@ref[Sink operators](../index.md#sink-operators)

@@@div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #futureSink }

@@@

## 描述

元素将流经给定的future flow，一旦它成功完成。如果future失败了，流也就失败了。

## 响应流语义

@@@div { .callout }

**cancels** 如果future失败或创建的接收器取消

**backpressures** 当已初始化时和已创建时接收器背压

@@@


