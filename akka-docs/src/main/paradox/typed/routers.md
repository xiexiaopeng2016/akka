# 路由器

有关此功能的Akka经典文档，请参阅 @ref:[Classic Routing](../routing.md)。

## 依赖

要使用Akka Actor Typed，必须在项目中添加以下依赖项：

@@dependency[sbt,Maven,Gradle] {
  group=com.typesafe.akka
  artifact=akka-actor-typed_$scala.binary_version$
  version=$akka.version$
}

## 介绍

在某些情况下，将相同类型的消息分发在一组actor上很有用，以便可以并行处理消息 - 一个actor一次只能处理一条消息。

路由器本身是一种行为，它会产生一个正在运行的actor，然后它将发送给它的所有消息转发到路由集合之外的一个最终接收者。

Akka Typed中包含两种路由器 - Pool 路由器和 Group 路由器。

<a id="pool-router"></a>"
## Pool 路由器

pool路由器是由一个 routee `Behavior` 创建的，并产生许多具有该行为的后代，然后它将向其转发消息。

如果一个子actor停止运行，则pool路由器会将其从其路由集合中删除。当最后一个子actor停止时，路由器本身也会停止。为了使弹性路由器能够处理故障，routee `Behavior` 必须被监督。


Scala
:  @@snip [RouterSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/RouterSpec.scala) { #pool }

Java
:  @@snip [RouterTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/RouterTest.java) { #pool }

<a id="group-router"></a>
## Group 路由器

组路由器是使用 `ServiceKey` 来创建的，并使用接收者(请参阅 @ref:[Receptionist](actor-discovery.md#receptionist))发现该key的可用actor，并将消息路由到该key的当前已知已注册actor之一。

由于使用了接收者，这意味着组路由器是开箱即用的感知集群。路由器将消息发送到群集中任何可达节点上的已注册actor。如果不存在可到达的actor，则路由器将回退并将消息路由到节点上的标记为不可到达的actor。

使用接收者还意味着路由集合最终是一致的，并且当组路由器一启动，它就知道的routee集合为空，直到它从接收者那里看到列表，然后将传入的消息存储，并在获得接收者的列表后立即转发。

当路由器从接收者那里收到列表，并且已注册的actors的集合为空时，路由器会将其删除(将它们发布到事件流中 `akka.actor.Dropped`)。

Scala
:  @@snip [RouterSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/RouterSpec.scala) { #group }

Java
:  @@snip [RouterTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/RouterTest.java) { #group }


## 路由策略

有两种不同的策略来选择将消息转发给哪个routee，其可以在生成之前从路由器中选择：

Scala
:  @@snip [RouterSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/RouterSpec.scala) { #strategy }

Java
:  @@snip [RouterTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/RouterTest.java) { #strategy } 

### 轮询

遍历一组路由，以确保如果存在 `n` 个routee，那么对于通过路由器发送的`n`条消息，每个actor都会转发一条消息。

轮询机制提供了公平的路由，只要routee集合保持相对稳定，每个可用的routee都会收到相同数量的消息，但是如果routee集合变化很大，这可能是不公平的。

这是pool路由器的默认设置，因为路由池预计将保持不变。

### 随机

通过路由器发送消息时随机选择一个routee。

这是group路由器的默认设置，因为routee组将随着节点加入和离开群集而改变。

## 路由器和性能

请注意，如果routee共享一个资源，则该资源将判定增加actor的数量是否实际上会提供更高的吞吐量或更快的应答。例如，如果routee是受CPU限制的actor，那么创建更多routee不会比线程执行actor提供更好的性能。

由于路由器本身是一个actor，并且有一个邮箱，这意味着消息被依次路由到routee，在那里它可以被并行处理(取决于dispatcher中的可用线程)。在高吞吐量的用例中，顺序路由可能会成为瓶颈。Akka Typed没有为此提供优化的工具。