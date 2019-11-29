# actorRef

将元素从流发送到一个`ActorRef`。

@ref[Sink operators](../index.md#sink-operators)

@@@ div { .group-scala }
## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #actorRef }
@@@

## 描述

将元素从流发送到一个`ActorRef`。没有背压，所以必须小心不要溢出收件箱。

## 响应流语义

@@@div { .callout }

**cancels** when the actor terminates

**backpressures** never

@@@


