# alsoTo

将给定的`Sink`附加到这个`Flow`，这意味着通过这个`Flow`的元素也将被发送到`Sink`。

@ref[Fan-out operators](../index.md#fan-out-operators)

@@@ div { .group-scala }
## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #alsoTo }
@@@

## 描述

Attaches the given `Sink` to this `Flow`, meaning that elements that pass through this `Flow` will also be sent to the `Sink`.

## 响应流语义

@@@div { .callout }

**emits** when an element is available and demand exists both from the `Sink` and the downstream

**backpressures** when downstream or `Sink` backpressures

**completes** when upstream completes

**cancels** when downstream or `Sink` cancels

@@@


