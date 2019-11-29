# watch

Watch a specific `ActorRef` and signal a failure downstream once the actor terminates.

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #watch }

@@@

## 描述

Watch a specific `ActorRef` and signal a failure downstream once the actor terminates.
The signaled failure will be an @java[@javadoc:[WatchedActorTerminatedException](akka.stream.WatchedActorTerminatedException)]
@scala[@scaladoc[WatchedActorTerminatedException](akka.stream.WatchedActorTerminatedException)].

## 响应流语义

@@@div { .callout }

**emits** when upstream emits

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@

