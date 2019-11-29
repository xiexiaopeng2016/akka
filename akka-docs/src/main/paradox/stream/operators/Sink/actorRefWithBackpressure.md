# Sink.actorRefWithBackpressure

将元素从流发送到一个`ActorRef`，它必须在完成一个消息后确认接收，以向接收器提供背压。

@ref[Sink operators](../index.md#sink-operators)

## 描述

Send the elements from the stream to an `ActorRef` which must then acknowledge reception after completing a message,
to provide back pressure onto the sink.

## 示例

Actor to be interacted with: 

Scala
:   @@snip [IntegrationDocSpec.scala](/akka-docs/src/test/scala/docs/stream/IntegrationDocSpec.scala) { #actorRefWithBackpressure-actor }

Java
:   @@snip [IntegrationDocTest.java](/akka-docs/src/test/java/jdocs/stream/IntegrationDocTest.java) { #actorRefWithBackpressure-actor }

Using the `actorRefWithBackpressure` operator with the above actor:

Scala
:   @@snip [IntegrationDocSpec.scala](/akka-docs/src/test/scala/docs/stream/IntegrationDocSpec.scala) { #actorRefWithBackpressure }

Java
:   @@snip [IntegrationDocTest.java](/akka-docs/src/test/java/jdocs/stream/IntegrationDocTest.java) { #actorRefWithBackpressure }

## 响应流语义 

@@@div { .callout }

**cancels** when the actor terminates

**backpressures** when the actor acknowledgement has not arrived

@@@

