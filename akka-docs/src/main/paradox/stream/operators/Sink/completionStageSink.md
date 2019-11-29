# Sink.completionStageSink

将元素流动到指定的future接收器，一旦它成功完成。

@ref[Sink operators](../index.md#sink-operators)


## 描述

将元素流经给定的future flow，一旦它成功完成。如果future失败了，流也就失败了。

## 响应流语义

@@@div { .callout }

**cancels** 如果future失败或创建的接收器取消

**backpressures** 在已初始化时和在已创建时汇背压

@@@


