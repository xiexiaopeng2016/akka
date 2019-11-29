# ActorSink.actorRefWithBackpressure

Sends the elements of the stream to the given @java[`ActorRef<T>`]@scala[`ActorRef[T]`] with backpressure, to be able to signal demand when the actor is ready to receive more elements.

@ref[Actor interop operators](../index.md#actor-interop-operators)

## 依赖

This operator is included in:

@@dependency[sbt,Maven,Gradle] {
  group="com.typesafe.akka"
  artifact="akka-stream-typed_$scala.binary_version$"
  version="$akka.version$"
}

@@@div { .group-scala }

## 签名

@@signature [ActorSink.scala](/akka-stream-typed/src/main/scala/akka/stream/typed/scaladsl/ActorSink.scala) { #actorRefWithBackpressure }

@@@

## 描述

Sends the elements of the stream to the given @java[`ActorRef<T>`]@scala[`ActorRef[T]`] with backpressure, to be able to signal demand when the actor is ready to receive more elements.

## 示例

Scala
:  @@snip [ActorSourceSinkExample.scala](/akka-stream-typed/src/test/scala/docs/akka/stream/typed/ActorSourceSinkExample.scala) { #actor-sink-ref-with-backpressure }

Java
:  @@snip [ActorSinkWithAckExample.java](/akka-stream-typed/src/test/java/docs/akka/stream/typed/ActorSinkWithAckExample.java) { #actor-sink-ref-with-backpressure }
