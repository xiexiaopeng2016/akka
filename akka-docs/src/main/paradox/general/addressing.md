---
project.description: Local and remote Akka Actor references, locating Actors, Actor paths and addresses.
---
<a id="actor-references-paths-and-addresses"></a>
# Actor引用，路径和地址

本章介绍如何识别actor并将其放置在一个可能的分布式Akka应用程序中。

![actor-paths-overview.png](../images/actor-paths-overview.png)

上图显示了actor系统中最重要实体之间的关系，请继续阅读以获取详细信息。

<a id="what-is-an-actor-reference"></a>
## 什么是一个演员引用

一个actor引用是一个`ActorRef`的子类型，其首要用途是支持向其表示的actor发送消息。每个actor都可以通过`ActorContext.self`字段访问它的标准(本地)引用。此引用可以包含在发给其他actor的消息中用来获得回复。

根据actor系统的配置，支持几种不同类型的actor引用：

 * 未配置为支持联网功能的actor系统使用的是纯本地actor引用。如果通过网络连接发送到远程JVM，则这些actor引用将不起作用。
 * 启用远程处理后，本地actor引用将由actor系统使用，它支持那些表示相同JVM中的actor的引用的网络功能。为了在发送到其他网络节点时也可以访问，这些引用包括协议和远程寻址信息。
 * 远程actor引用表示使用远程通信可到达的actor，即向它们发送消息将透明地序列化消息并将它们发送到远程JVM。
 * 有几种特殊类型的actor引用，它们的行为类似于本地actor引用，用于所有实际用途：
    * `PromiseActorRef`是一个`Promise`的特殊表示形式，用途是通过一个actor的响应来完成。`akka.pattern.ask`创建这个actor引用。
    * `DeadLetterActorRef`是死信服务的默认实现，Akka将所有目标已关闭或不存在的消息路由到死信服务。
    * `EmptyLocalActorRef`是Akka在查找不存在的本地actor路径时返回的结果：它等效于一个`DeadLetterActorRef`，但是会保留其路径，以便Akka可以通过网络发送它，并将它与其他现有actor引用的路径进行比较。其中一些可能是在actor死前获得的。。
 * 然后有一些一次性的内部实现，您应该永远不会真正看到它们：
    * 有一个actor引用，它不代表一个actor，而只是作为根守护者的伪监督者，我们称之为“在时空的气泡中行走的人”。
    * 在实际启动actor创建工具之前启动的第一个日志服务是一个伪actor引用，它接受日志事件并将其直接打印到标准输出；它是`Logging.StandardOutLogger`。

<a id="what-is-an-actor-path-"></a>
## 什么是一个actor路径？

由于actor是按照严格的分层方式创建的，存在一个唯一的actor名称序号，由通过递归地跟踪子actor和父actor之间的监控链接，向下到达actor系统的根给出的。actor路径由一个锚组成，锚标识actor系统，随后是path元素的连接，从根监督者到指定的actor；路径元素是被遍历的actor的名称，并由斜线分隔。

<a id="what-is-the-difference-between-actor-reference-and-path-"></a>
### Actor引用和路径之间有什么区别？

一个actor引用指定一个单独的actor，而且引用的生命周期与该actor的生命周期相匹配；一个actor路径代表一个名称，该名称可能由一个actor占用也可能没有，并且路径本身没有生命周期，因此永远不会无效。您可以在不创建一个actor的情况下创建一个actor路径。但是，如果不创建相应的actor，就不能创建一个actor引用。

您可以创建一个actor，将其终止，然后使用相同的actor路径创建一个新actor。新创建的actor是actor的新替身。不是同一位actor。一个旧替身的actor引用对新替身无效。发送到旧actor引用的消息即使路径相同，也不会传递到新actor。

<a id="actor-path-anchors"></a>
### actor路径锚

每个actor路径都有一个地址组件，描述了可通过其访问相应actor的协议和位置，按照层次结构中从根开始的actor名称。例如：

```
"akka://my-sys/user/service-a/worker1"               // purely local
"akka://my-sys@host.example.com:5678/user/service-b" // remote
```

主机和端口部分的解释(即例子中的`host.example.com:5678`)取决于所使用的传输机制，但是必须遵守URI结构规则。

<a id="logical-actor-paths"></a>
### 逻辑actor路径

通过跟踪父监督者链接直到根监督者，获得的唯一路径称为逻辑actor路径。该路径与actor的创建祖先完全匹配，因此，只要设置了actor系统的远程配置(以及路径的地址组件)，它就是完全确定的。

<a id="actor-path-alias-or-symbolic-link-"></a>
### actor路径别名或符号链接？

就像在某些实际文件系统中一样，您可能会想到一个actor的“路径别名”或“符号链接”，即，一个参与者可以使用多个路径来访问。但是，您应该注意到actor层次结构与文件系统层次结构不同。您不能自由创建诸如符号链接之类的actor路径来引用任意actor。

<a id="how-are-actor-references-obtained-"></a>
## 如何获得actor引用？

关于如何获得actor引用的方法，大致分为两类：通过 @ref:[创建actor](../typed/actor-lifecycle.md#creating-actors)或通过 @ref:[传达者](../typed/actor-discovery.md#receptionist)查找他们。

<a id="actor-reference-and-path-equality"></a>
## actor引用和路径平等

`ActorRef`匹配的相等性，意图是一个`ActorRef`对应于目标actor的化身。当两个actor引用具有相同的路径并指向相同的actor化身时，它们对比起来是相等的。一个指向终止的actor的引用与指向具有相同路径的另一个(重新创建的)actor的引用是不同的。注意，由于失败而导致的actor重新启动仍然意味着它是相同的actor化身，也就是说，对于`ActorRef`的消费者来说，重新启动是不可见的。

如果你需要跟踪集合中的actor引用，而不关心确切的actor化身，你可以使用`ActorPath`作为键，因为在比较actor路径时不会考虑目标actor的标识符。

<a id="reusing-actor-paths"></a>
## 重用actor路径

当一个actor终止时，它的引用会指向死信邮箱，DeathWatch将发布其最终转变，并且通常不希望它重新恢复生命(因为actor生命周期不允许这样做)。

<a id="what-is-the-address-part-used-for-"></a>
## 地址部分是用来做什么的？

当通过网络发送一个actor引用时，它由它的路径表示。因此，该路径必须完全编码将消息发送到底层参与者所需的所有信息。这是通过在路径字符串的地址部分对协议、主机和端口进行编码来实现的。当一个actor系统从远程节点接收一个actor路径时，它检查该路径的地址是否与此actor系统的地址匹配，在这种情况下，它将解析为actor的本地引用。否则，它将由远程actor引用表示。

<a id="toplevel-paths"></a>
## actor路径的顶层范围

在路径层次结构的根部是根监督者，在该根监督者之上可以找到所有其他actor。它的名字是"/"。下一级别包括以下内容：

 * `"/user"` 是所有用户创建的顶级actor的监督者actor；使用`ActorSystem.actorOf`创建的actor可以在下面找到。
 * `"/system"` 是所有系统的监督者actor - 创建顶层actor，例如，日志监听器或在actor系统启动时通过配置自动部署的actor。
 * `"/deadLetters"` 是死信actor，所有发送给已停止或不存在的actor的消息都将在这里重新路由(尽了最大努力：即使在本地JVM中，消息也可能丢失)。
 * `"/temp"` 是所有临时系统创建角色的监督者 - 创建actor，例如在执行`ActorRef.ask`时所使用的。
 * `"/remote"` 这是一个人工路径，所有actor都位于其下，其监督者是远程actor的引用

为这样的actor组织名称空间的需求源于一个核心的、非常简单的设计目标：层次结构中的所有内容都是一个actor，并且所有actor以相同的方式起作用。
