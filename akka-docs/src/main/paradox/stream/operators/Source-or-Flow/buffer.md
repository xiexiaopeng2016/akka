# buffer

通过缓冲`size`个元素来允许一个暂时更快的上游事件。

@ref[Backpressure aware operators](../index.md#backpressure-aware-operators)

@@@ div { .group-scala }
## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #buffer }
@@@


## 描述

通过缓冲`size`个元素来允许一个暂时更快的上游事件。当缓冲区已满时，一个新元素将根据指定的`OverflowStrategy`进行处理：

 * `backpressure` 在上游施加背压
 * `dropHead` 删除缓冲区中最老的元素，为新元素腾出空间
 * `dropTail` 删除缓冲区中最新的元素，为新元素腾出空间
 * `dropBuffer` 删除整个缓冲区并缓冲新元素
 * `dropNew` 删除新元素
 * `fail` 用一个`BufferOverflowException`使流失败

## 响应流语义

@@@div { .callout }

**emits** 当下游停止背压且缓冲区中有挂起的元素

**backpressures** 当`OverflowStrategy`是`backpressure`且缓冲区已满

**completes** 当上游完成，缓冲元素已被耗尽，或当`OverflowStrategy`是`fail`，缓冲区已满，一个新元素到达

@@@


