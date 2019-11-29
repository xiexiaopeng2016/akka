# Flow.fromSinkAndSource

从一个`Sink`和一个`Source`创建一个`Flow`，其中Flow的输入将被发送到`Sink`，而`Flow`的输出将来自`Source`。

@ref[Flow operators composed of Sinks and Sources](../index.md#flow-operators-composed-of-sinks-and-sources)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #fromSinkAndSource }

@@@

## 描述

Creates a `Flow` from a `Sink` and a `Source` where the Flow's input will be sent to the `Sink`
and the `Flow` 's output will come from the Source.

Note that termination events, like completion and cancelation is not automatically propagated through to the "other-side"
of the such-composed Flow. Use `Flow.fromSinkAndSourceCoupled` if you want to couple termination of both of the ends,
for example most useful in handling websocket connections.
