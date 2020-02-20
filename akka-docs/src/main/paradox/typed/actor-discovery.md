# Actor发现

有关此功能的Akka经典文档，请参阅 @ref:[经典Actor](../actors.md)。

## 依赖

要使用Akka Actor Typed，必须在项目中添加以下依赖项：

@@dependency[sbt,Maven,Gradle] {
  group=com.typesafe.akka
  artifact=akka-actor-typed_$scala.binary_version$
  version=$akka.version$
}

<a id="obtaining-actor-references"></a>
## 获取actor引用

有两种获取 @ref:[Actor引用](../general/addressing.md#what-is-an-actor-reference)的常规方法：通过 @ref:[创建actor](actor-lifecycle.md#creating-actors)和通过使用 @ref:[总机(Receptionist)](#receptionist)发现。

您可以在actor之间传递actor引用作为构造函数参数或消息的一部分。

有时，您需要一些东西来引导交互，例如，当actor在集群中的不同节点上运行时，或者用构造函数参数的"依赖注入"不适用时。

<a id="receptionist"></a>
## 传达者

当一个actor需要被另一个actor发现，但是您无法在传入消息中放置一个引用给它时，你可以使用`Receptionist`。您可以在本地`Receptionist`实例上注册特定的actor，它们应该可以从其它节点发现。传达者的API也是基于actor消息。然后，这个actor引用的注册将自动分布到集群中的所有其他节点。您可以使用注册时使用的键来查找这些actor。对一个这样的`Find`请求的回复是一个`Listing`，其中包含一`Set`的actor引用，它们是用键注册的。请注意，可以将多个actor注册到相同的键。

注册是动态的。可以在系统的生命周期中注册新的actor。当已注册的actor已经停止，手动注销它们或从 @ref:[集群](cluster.md)中移除他们所在的节点时，条目将被删除。

为了促进这一动态方面，您还可以用`Receptionist.Subscribe`消息订阅变更。键的条目被更改时，它将向订阅者发送`Listing`消息。

以下示例中使用了这些导入:

Scala
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/scala/docs/akka/cluster/typed/ReceptionistExample.scala) { #import }

Java
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/java/jdocs/akka/cluster/typed/ReceptionistExample.java) { #import }

首先，我们创建一个`PingService`actor，并且`Receptionist`针对一个`ServiceKey`进行注册，随后将用于查找引用:

Scala
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/scala/docs/akka/cluster/typed/ReceptionistExample.scala) { #ping-service }

Java
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/java/jdocs/akka/cluster/typed/ReceptionistExample.java) { #ping-service }

然后我们有另一个actor， 它需要构造一个`PingService`：

Scala
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/scala/docs/akka/cluster/typed/ReceptionistExample.scala) { #pinger }

Java
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/java/jdocs/akka/cluster/typed/ReceptionistExample.java) { #pinger }

最后，在监督者actor中，我们不但生成了服务，而且根据`ServiceKey`订阅任何注册的actor。订阅意味着，将通过一个`Listing`消息将任何新的注册通知监督者actor：

Scala
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/scala/docs/akka/cluster/typed/ReceptionistExample.scala) { #pinger-guardian }

Java
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/java/jdocs/akka/cluster/typed/ReceptionistExample.java) { #pinger-guardian }

每次注册一个新(在此示例中仅为一次)`PingService`，监督者actor都会为每个当前已知的`PingService`生成一个`Pinger`。`Pinger`发送一条`Ping`消息，且当接收`Pong`时回复其停止。

在上面的示例中，我们使用了`Receptionist.Subscribe`，但也可以请求当前状态的一个单独的`Listing`，而不是通过发送`Receptionist.Find`消息给传达者来接收进一步的更新。使用`Receptionist.Find`的示例：

Scala
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/scala/docs/akka/cluster/typed/ReceptionistExample.scala) { #find }

Java
:  @@snip [ReceptionistExample](/akka-cluster-typed/src/test/java/jdocs/akka/cluster/typed/ReceptionistExample.java) { #find }

还要注意一个`messageAdapter`是如何用于将`Receptionist.Listing`转换为一个`PingManager`理解的消息类型。

<a id="cluster-receptionist"></a>
## 集群传达者

`Receptionist`也工作于一个集群中，注册到传达者的actor将出现在集群中的其他节点的传达者。

传达者的状态是通过 @ref:[分布式数据](distributed-data.md)传播的，这意味着每个节点每个`ServiceKey`最终将到达的相同的actor集。

对集群传达者的`Subscription`和`Find`查询将跟踪集群的可达性，只列出可访问的已注册actor。

`Subscription`和`Find`向集群接待员的查询将跟踪集群的可达性，并且仅列出可达的已注册参与者。完整的actor集合，包括无法联系到的，可以通过`Listing.allServiceInstances`访问。

与本地仅接收的一个重要区别是涉及序列化，从另一个节点上的actor发送和返回的所有消息都必须是可序列化的，请参见 @ref:[序列化](../serialization.md)。

<a id="receptionist-scalability"></a>
## 传达者可扩展性

传达者无法扩展到任何数量的服务或服务周转率很高。它可能会处理成千上万的服务。对于节点上的actor之间的初始连接有更高传达需求的用例，那些的实际逻辑取决于应用程序自己的actor。
