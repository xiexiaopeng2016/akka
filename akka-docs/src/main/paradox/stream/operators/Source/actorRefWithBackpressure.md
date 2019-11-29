# actorRefWithBackpressure

物化一个`ActorRef`；发送消息给它将在流上发射这些消息。发送消息后，源确认接收，以提供来自源的背压。

@ref[Source operators](../index.md#source-operators)

@@@ div { .group-scala }
## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #actorRefWithBackpressure }
@@@

## 描述

Materialize an `ActorRef`, sending messages to it will emit them on the stream. The actor responds with the provided ack message
once the element could be emitted allowing for backpressure from the source. Sending another message before the previous one has been acknowledged will fail the stream.

## 响应流语义

@@@div { .callout }

**emits** when there is demand and there are messages in the buffer or a message is sent to the `ActorRef`

**completes** when the `ActorRef` is sent `akka.actor.Status.Success`

@@@

## 示例


Scala
:  @@snip [actorRef.scala](/akka-docs/src/test/scala/docs/stream/operators/SourceOperators.scala) { #actorRefWithBackpressure }

Java
:  @@snip [actorRef.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceDocExamples.java) { #actor-ref-imports #actorRefWithBackpressure }
