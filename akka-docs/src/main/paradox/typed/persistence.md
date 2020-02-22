---
project.description: Event Sourcing with Akka Persistence enables actors to persist your events for recovery on failure or when migrated within a cluster.
---
<a id="event-sourcing"></a>
# 事件溯源

有关此功能的Akka经典文档，请参阅 @ref:[经典Akka持久化](../persistence.md).

## 模块信息

要使用Akka持久化，请将模块添加到您的项目中：

@@dependency[sbt,Maven,Gradle] {
  group=com.typesafe.akka
  artifact=akka-persistence-typed_$scala.binary_version$
  version=$akka.version$
}

您还必须选择日志插件和可选的快照存储插件，请参阅 @ref:[持久性插件](../persistence-plugins.md)。

@@project-info{ projectId="akka-persistence-typed" }

<a id="introduction"></a>
## 介绍

Akka持久化使有状态的actor能够保持其状态，以便当actor重新启动时恢复，例如，在JVM崩溃后，由监督者或手动停止-启动，或者在集群中迁移。Akka持久化背后的关键概念是，仅存储由actor保留的_事件_，而不存储actor的实际状态(尽管也提供了actor状态快照支持)。通过将事件附加到存储中(不会发生任何变化)来持久存储事件，这允许非常高的事务率和有效的复制。有状态的actor通过将存储的事件重新播放给actor来恢复，从而允许它重新构建状态。这可以是变动的完整历史记录，也可以是从快照中的检查点开始，从而大大减少恢复时间。

@@@ note

通用数据保护条例(GDPR)要求必须根据用户的要求删除个人信息。删除或修改包含个人信息的事件将是困难的。数据粉碎可以用来忘记信息，而不是删除或修改它。这是通过使用给定数据主体id(人)的密钥加密数据，并在要忘记该数据主体时删除该密钥来实现的。Lightbend的[用于Akka持久化的GDPR](https://doc.akka.io/docs/akka-enhancements/current/gdpr/index.html)提供了一些工具，以帮助构建具有GDPR功能的系统。

@@@

<a id="event-sourcing-concepts"></a>
### 事件溯源概念

请参阅MSDN上的[事件溯源介绍](https://msdn.microsoft.com/en-us/library/jj591559.aspx)。

关于"在事件中思考"的另一篇优秀文章是Randy Shoup撰写的[事件作为头等公民](https://hackernoon.com/events-as-first-class-citizens-8633e8479493) 。如果您正开始开发基于事件的应用程序，这是一本简短的推荐读物。

接下来是Akka实现的事件溯源Actor。

事件溯源的actor(也称为持久性actor)接收(非持久性)命令，如果可以将其应用于当前状态，则首先对其进行验证。在这里，验证可以是任何东西，从简单地检查命令消息的字段到与几个外部服务的对话。如果验证成功，则从命令生成事件，表示命令的效果。然后，这些事件被持久化，并在成功持久化后用于更改actor的状态。当需要恢复事件溯源的actor时，只有我们知道可以成功应用的持久化事件才会被重新播放。换句话说，相对于命令，事件在重放给一个持久化actor时不会失败。事件溯源的actor也可以处理不改变应用程序状态的命令，例如查询命令。

<a id="example-and-core-api"></a>
## 示例和核心API

让我们从一个简单的例子开始。 @apidoc[EventSourcedBehavior]的最低要求是：

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #structure }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #structure }

要注意的第一件重要事情是持久化actor的`Behavior`是`Command`类型的类型化，因为这是持久化actor应该接收的消息类型。在Akka中，这现在由类型系统强制执行。

组成一个`EventSourcedBehavior`的组件有:

* `persistenceId` 是持久化actor的稳定唯一标识符。
* `emptyState` 首次创建实体时定义`State`，例如，一个计数器将从0开始作为状态。 
* `commandHandler` 定义如何通过产生效果来处理命令，例如，持久化事件，停止持久化actor。
* `eventHandler` 当一个事件已持久化时，根据当前状态返回新状态。

接下来，我们将详细讨论其中的每一个。

### PersistenceId

@apidoc[akka.persistence.typed.PersistenceId]是后端事件日志和快照存储中持久化actor的稳定唯一标识符。

@ref:[群集分片](cluster-sharding.md)通常与`EventSourcedBehavior`一起使用，以确保每个`PersistenceId`(`entityId`)仅存在一个活动实体。

集群分片中的`entityId`是实体的业务域标识符。`entityId`可能不够惟一，不能单独用作`PersistenceId`。例如，两个不同类型的实体可能具有相同的`entityId`。

要创建一个唯一的`PersistenceId`，应该在`entityId`前面加上实体类型的稳定名称，它通常与集群分片中使用的`EntityTypeKey.name`相同。有`PersistenceId.apply`工厂方法来帮助从一个`entityTypeHint`和`entityId`构造这样的`PersistenceId`。

连接`entityTypeHint`和`entityId`时的默认分隔符是`|`，但是支持自定义分隔符。

@@@ note

这个`|`分隔符还用于Lagom的`scaladsl.PersistentEntity`，但是在Lagom的`javadsl.PersistentEntity`中没有使用任何分隔符。为了与Lagom的`javadsl.PersistentEntity`兼容，你应该使用`""`作为分隔符。

@@@

@ref:[集群分片文档中的持久化示例](cluster-sharding.md#persistence-example)演示了如何从`entityTypeKey`和`entityId`提供的`EntityContext`构造`PersistenceId`。

可以使用`PersistenceId.ofUniqueId`创建自定义标识符。

<a id="command-handler"></a>
### 命令处理程序

命令处理程序是一个具有2个参数的函数，当前的`State`和传入的`Command`。

命令处理程序返回一个`Effect`指令，该指令定义了一个或多个什么事件，如果有的话，要持久化。效果是使用`Effect`工厂创建的。

最常用的两种效果是:

* `persist` 将持久地保留一个事件或多个事件，将存储所有事件或不存储任何事件，假如发生错误
* `none` 没有事件被持久化，例如一个读命令

更多效果在 @ref:[效果和副作用](#effects-and-side-effects)中进行了说明。

除了为命令`EventSourcedBehavior`返回主要`Effect`外，还可以链式副作用，这些副作用将在持久化成功后执行，这是通过`thenRun`函数完成的，例如`Effect.persist(..).thenRun`。

<a id="event-handler"></a>
### 事件处理程序

当一个事件被持久化成功后，通过使用`eventHandler`将事件应用到当前状态来创建新状态。

状态通常定义为不可变类，然后事件处理程序返回状态的新实例。您可以选择为状态使用一个可变类，然后事件处理程序可能更新状态实例并返回相同的实例。不可变和可变状态均受支持。

当实体启动以从存储的事件中恢复其状态时，也使用相同的事件处理程序。

事件处理程序必须只更新状态，而不执行副作用，因为这些副作用也将在持久化actor恢复期间执行。副作用应该在`thenRun`中执行，它来自 @ref:[事件处理程序](#command-handler)，在持久化事件之后，或来自`RecoveryCompleted`，在 @ref:[恢复](#recovery) 之后。

<a id="completing-the-example"></a>
### 完成示例

让我们填写示例的细节。

命令和事件：

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #command }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #command }

状态是包含5个最新项目的列表：

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #state }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #state }

命令处理程序在一个`Added`事件中持久化`Add`有效负载：

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #command-handler }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #command-handler }

事件处理程序将项目追加到状态并保留5个项目。这是在成功地将事件持久化到数据库后调用的:

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #event-handler }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #event-handler }

这些用于创建一个`EventSourcedBehavior`：

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #behavior }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #behavior }

<a id="effects-and-side-effects"></a>
## 效果和副作用

命令处理程序返回一个`Effect`指令，它定义了一个或多个什么样的事件，如果有，要持久化。效果是使用`Effect`工厂创建的，可以是以下之一：

* `persist` 将持久化一个事件或多个事件，将存储所有事件或不存储任何事件，如果发生错误
* `none` 没有事件会被持久化，例如，只一个只读命令
* `unhandled` 当前状态下未处理(不支持)该命令
* `stop` 停止这个actor
* `stash` 当前命令已被存储
* `unstashAll` 处理被`Effect.stash`存储的命令
* `reply` 向给定的`ActorRef`发送一条回复消息

请注意，每个传入命令只能选择其中之一。不可能既persist又说none/unhandled。

除了为命令`EventSourcedBehavior`返回主要`Effect`外，还可以链式副作用，这些副作用将在持久化成功后执行，这是通过`thenRun`函数完成的，例如`Effect.persist(..).thenRun`。

在下面的示例中，状态被发送到`subscriber`ActorRef。请注意，应用事件后的新状态将作为`thenRun`函数的参数传递。

在成功执行持久化语句后，所有`thenRun`已注册的回调将顺序执行(假如使用`none`和`unhandled`，则立即执行)。

除了`thenRun`之外，成功持久化后还可以执行以下操作：

* `thenStop` actor将被停止
* `thenUnstashAll` 处理被`Effect.stash`存储的命令
* `thenReply` 向给定的`ActorRef`发送一条回复消息

效果的示例：

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #effects }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #effects }

大多数情况下，将使用上述`Effect`上的`thenRun`方法完成此操作。你可以把常见的副作用分解成函数，并重复使用几个命令。例如：

Scala
:  @@snip [PersistentActorCompileOnlyTest.scala](/akka-persistence-typed/src/test/scala/akka/persistence/typed/scaladsl/PersistentActorCompileOnlyTest.scala) { #commonChainedEffects }

Java
:  @@snip [PersistentActorCompileOnlyTest.java](/akka-persistence-typed/src/test/java/akka/persistence/typed/javadsl/PersistentActorCompileOnlyTest.java) { #commonChainedEffects }

<a id="side-effects-ordering-and-guarantees"></a>
### 副作用排序和保证

任何副作用最多只能执行一次，并且如果持久化失败将不会执行任何副作用。

当actor重新启动或停止后再次启动时，不会运行副作用。您可以在接收`RecoveryCompleted`信号时检查状态，并执行此时尚未确认的副作用。这可能会导致多次执行副作用。

副作用是按顺序执行的，不可能同时执行副作用，除非它们调用并发运行的东西(例如，向其他actor发送消息)。

可以在持久化事件之前执行一个副作用，但是这可能导致执行了副作用，但事件没有存储，如果持久化失败的话。

<a id="atomic-writes"></a>
### 原子写入

可以原子地存储多个事件，通过在一系列事件上使用`persist`效果。这意味着传递给该方法的所有事件都将被存储，如果出现错误，则不存储任何事件。

因此，如果仅使用单个`persist`效果持久化的事件子集，则永远无法部分地恢复持久化actor。

有些日志可能不支持对多个事件进行原子写入，因此它们将拒绝`persist`具有多个事件的事件。这通过EventRejectedException（通常带有UnsupportedOperationException）发出信号到，并可以由主管处理。

这是通过一个`EventRejectedException`向一个`EventSourcedBehavior`发出信号的(通常使用一个`UnsupportedOperationException`)并且可以通过一个 @ref[监督者](fault-tolerance.md)来处理。

<a id="cluster-sharding-and-eventsourcedbehavior"></a>
## 群集分片和EventSourcedBehavior

In a use case where the number of persistent actors needed is higher than what would fit in the memory of one node or where resilience is important so that if a node crashes the persistent actors are quickly started on a new node and can resume operations @ref:[Cluster Sharding](cluster-sharding.md) is an excellent fit to spread persistent actors over a cluster and address them by id.

The `EventSourcedBehavior` can then be run as with any plain actor as described in @ref:[actors documentation](actors.md), but since Akka Persistence is based on the single-writer principle the persistent actors are typically used together with Cluster Sharding. For a particular `persistenceId` only one persistent actor instance should be active at one time. If multiple instances were to persist events at the same time, the events would be interleaved and might not be interpreted correctly on replay. Cluster Sharding ensures that there is only one active entity for each id. The @ref:[Cluster Sharding example](cluster-sharding.md#persistence-example) illustrates this common combination.

<a id="accessing-the-actorcontext"></a>
## 访问ActorContext

如果 @apidoc[EventSourcedBehavior] 需要使用@apidoc[typed.*.ActorContext]，例如生成子actor，它可以通过使用`Behaviors.setup`来包装构造来获得。

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #actor-context }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #actor-context }

<a id="changing-behavior"></a>
## 改变行为

处理完一条消息后，actor可以返回用于下一条消息的`Behavior`。

正如您在上面的示例中看到的那样，持久化actor不支持此功能。而是由`eventHandler`返回状态。无法返回一个新行为的原因是，行为是actor状态的一部分，并且在恢复过程中也必须仔细地进行重构。如果它是受支持的，这就意味着在回放事件时必须恢复行为，并且在使用快照时也必须在状态中进行编码。这很容易出错，因此在Akka持久化中是不允许的。

对于基本actor，您可以使用与实体处于什么状态无关的同一组命令处理程序，如上面的示例所示。对于更复杂的actor，可以根据actor所处的状态定义不同的处理命令功能的意义上说，能够更改行为是有用的。这在实现有限状态机(FSM)之类的实体时非常有用。

下一个示例演示如何基于当前`State`定义不同的行为。它是一个代表博客发表状态的actor。在一个发表启动之前，它唯一能处理的命令是`AddPost`。它一旦启动，便可以使用`GetPost`进行查找，使用`ChangeBody`修改或使用进行`Publish`发布。

通过以下方式捕获状态：

Scala
:  @@snip [BlogPostEntity.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BlogPostEntity.scala) { #state }

Java
:  @@snip [BlogPostEntity.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BlogPostEntity.java) { #state }

这些命令，取决于状态其中只有一个子集是有效的:

Scala
:  @@snip [BlogPostEntity.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BlogPostEntity.scala) { #commands }

Java
:  @@snip [BlogPostEntity.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BlogPostEntity.java) { #commands }

处理每个命令的命令处理程序是通过先查看状态然后查看命令来确定的。通常，它变成模式匹配的两个级别，首先是状态，然后是命令。委托给方法是一种很好的实践，因为单行的case很好地概述了消息的分发。

Scala
:  @@snip [BlogPostEntity.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BlogPostEntity.scala) { #command-handler }

Java
:  @@snip [BlogPostEntity.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BlogPostEntity.java) { #command-handler }

事件处理程序：

Scala
:  @@snip [BlogPostEntity.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BlogPostEntity.scala) { #event-handler }

Java
:  @@snip [BlogPostEntity.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BlogPostEntity.java) { #event-handler }

最后，行为是从`EventSourcedBehavior.apply`创建的：

Scala
:  @@snip [BlogPostEntity.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BlogPostEntity.scala) { #behavior }

Java
:  @@snip [BlogPostEntity.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BlogPostEntity.java) { #behavior }

这可以采取一个或两个步骤，通过在state类中定义事件和命令处理程序，正如在 @ref:[状态中的事件处理程序](persistence-style.md#event-handlers-in-the-state)和 @ref:[状态中的命令处理程序](persistence-style.md#command-handlers-in-the-state)所示。

还有一个示例演示了一个 @ref:[可选的初始状态](persistence-style.md#optional-initial-state)。

<a id="replies"></a>
## 回复

对于持久化actor，@ref:[请求-响应交互模式](interaction-patterns.md#request-response)是很常见的，因为你通常想知道，假如拒绝了该命令是由于验证错误，并且在接受该命令时，您希望在事件已成功存储时得到一个确认。

因此，您通常会在命令中包含一个`ActorRef[ReplyMessageType]`。在验证错误或事件持久化之后，使用一个`thenRun`副作用，回复消息可以被发送到`ActorRef`。

Scala
:  @@snip [BlogPostEntity.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BlogPostEntity.scala) { #reply-command }

Java
:  @@snip [BlogPostEntity.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BlogPostEntity.java) { #reply-command }


Scala
:  @@snip [BlogPostEntity.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BlogPostEntity.scala) { #reply }

Java
:  @@snip [BlogPostEntity.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BlogPostEntity.java) { #reply }

由于这是一种常见的模式，这里有一个回复效果对于这个目的。它具有很好的属性，它可以用来强制执行，在实现`EventSourcedBehavior`时不会忘记回复。

如果它是用`EventSourcedBehavior.withEnforcedReplies`定义的，如果返回的效果不是`ReplyEffect`，会有编译错误，那些可用`Effect.reply`，`Effect.noReply`，`Effect.thenReply`，或`Effect.thenNoReply`创建。

Scala
:  @@snip [AccountExampleWithEventHandlersInState.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleWithEventHandlersInState.scala) { #withEnforcedReplies }

Java
:  @@snip [AccountExampleWithNullState.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleWithEventHandlersInState.java) { #withEnforcedReplies }

这些命令必须具有一个`ActorRef[ReplyMessageType]`字段，该字段然后可以用于发送一个答复。

Scala
:  @@snip [AccountExampleWithEventHandlersInState.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleWithEventHandlersInState.scala) { #reply-command }

Java
:  @@snip [AccountExampleWithNullState.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleWithEventHandlersInState.java) { #reply-command }

`ReplyEffect`是使用`Effect.reply`，`Effect.noReply`，`Effect.thenReply`，或`Effect.thenNoReply`创建的。

Scala
:  @@snip [AccountExampleWithEventHandlersInState.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleWithEventHandlersInState.scala) { #reply }

Java
:  @@snip [AccountExampleWithNullState.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleWithEventHandlersInState.java) { #reply }

即使没有使用`EventSourcedBehavior.withEnforcedReplies`，这些效果也会发送回复消息，但是不会有编译错误，如果不考虑应答决策。

请注意，`noReply`是一种有意识地决定不应该针对特定命令发送应答，或者稍后将发送应答的方式，可能在与其他actor或服务进行异步交互之后。

<a id="serialization"></a>
## 序列化

与actor消息相同的 @ref:[序列化](../serialization.md)机制也用于持久化actor。在为事件选择序列化解决方案时，您还应该考虑当应用程序已经发展时，必须能够读取旧事件。可以在 @ref:[模式演变](../persistence-schema-evolution.md)中找到相应的策略。

您需要为命令(消息)，事件和状态(快照)启用 @ref:[序列化](../serialization.md)。在许多情况下，使用 @ref:[使用Jackson序列化](../serialization-jackson.md)是一个不错的选择，如果您没有其他选择，我们建议您这样做。

<a id="recovery"></a>
## 恢复

事件溯源的actor在启动和重新启动时通过重播日志事件自动恢复。恢复期间发送给actor的新消息不会干扰重播事件。在恢复阶段完成之后，`EventSourcedBehavior`将它们存储起来并接收。

可以同时进行的并发恢复的数量是受限制的，为不使系统和后端数据存储超载。当超出限制时，actor将等到其他恢复完成。这是由以下配置的：

```
akka.persistence.max-concurrent-recoveries = 50
```

@ref:[事件处理程序](#event-handler)用于重播日志事件时更新状态。

强烈建议在事件处理程序中执行副作用，因此，一旦恢复完成后就应该执行副作用，作为对`receiveSignal`处理程序中的`RecoveryCompleted`信号的响应。

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #recovery }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #recovery }

`RecoveryCompleted`包含当前`State`。

actor总是会收到一个`RecoveryCompleted`的信号，即使日志中没有事件并且快照存储为空，或者它是一个新的持久化actor，之前没有使用过`PersistenceId`。

@ref[快照](persistence-snapshot.md) 可用于优化恢复时间。

<a id="replay-filter"></a>
### 重播过滤器

在某些情况下，事件流可能会损坏，并且多个写入者(即，多个持久化Actor实例)使用相同序列号记录不同的消息。在这种情况下，您可以配置如何在恢复时过滤来自多个写入器的重播消息。

在您的配置中，在`akka.persistence.journal.xxx.replay-filter`部分(那里`xxx`是日志插件ID)下面，您可以从以下值中选择重一个播过滤器`mode`：

 * repair-by-discard-old
 * fail
 * warn
 * off

例如，如果您为leveldb插件配置了重播过滤器，则它看起来像这样：

```
# The replay filter can detect a corrupt event stream by inspecting
# sequence numbers and writerUuid when replaying events.
akka.persistence.journal.leveldb.replay-filter {
  # What the filter should do when detecting invalid events.
  # Supported values:
  # `repair-by-discard-old` : discard events from old writers,
  #                           warning is logged
  # `fail` : fail the replay, error is logged
  # `warn` : log warning but emit events untouched
  # `off` : disable this feature completely
  mode = repair-by-discard-old
}
```

<a id="tagging"></a>
## 标记

持久化允许您使用事件标签，而无需使用 @ref[`EventAdapter`](../persistence.md#event-adapters)：

Scala
:  @@snip [BasicPersistentActorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #tagging }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #tagging }

<a id="event-adapters"></a>
## 事件适配器

事件适配器可以以编程方式添加到您的`EventSourcedBehavior`中，该行为可以将您的`Event`类型转换为另一种类型，然后传递给日志。

可以以编程方式将事件适配器添加到您EventSourcedBehavior的，可以将您的Event类型转换为其他类型，然后将其传递给日志。

通过扩展`EventAdapter`来定义一个事件适配器：

Scala
:  @@snip [x](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #event-wrapper }

Java
:  @@snip [x](/akka-persistence-typed/src/test/java/akka/persistence/typed/javadsl/PersistentActorCompileOnlyTest.java) { #event-wrapper }

然后将其安装在一个`EventSourcedBehavior`：

Scala
:  @@snip [x](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #install-event-adapter }

Java
:  @@snip [x](/akka-persistence-typed/src/test/java/akka/persistence/typed/javadsl/PersistentActorCompileOnlyTest.java) { #install-event-adapter }

<a id="wrapping-eventsourcedbehavior"></a>
## 包装EventSourcedBehavior

在创建一个`EventSourcedBehavior`时，可以将`EventSourcedBehavior`封装到其他行为中，比如`Behaviors.setup`，以便访问`ActorContext`对象。例如，在为调试目的获取快照时访问actor日志记录。

Scala
:  @@snip [BasicPersistentActorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #wrapPersistentBehavior }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #wrapPersistentBehavior }

<a id="journal-failures"></a>
## 日志失败

默认情况下，如果从日志中抛出一个异常，`EventSourcedBehavior`将停止。可以用任何`BackoffSupervisorStrategy`覆盖它。对此无法使用正常的监督包装，因为在失败日志上`resume`一个的行为无效的，因为尚不清楚事件是否已经持久化。

Scala
:  @@snip [BasicPersistentBehaviorSpec.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #supervision }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #supervision }

如果从日志中恢复actor的状态存在问题，则会向`receiveSignal`处理程序发出`RecoveryFailed`信号，并且actor将停止(或通过backoff重新启动)。

<a id="journal-rejections"></a>
### 日志拒绝

日志可以拒绝事件。与失败的区别在于，日志必须在尝试持久化事件之前决定拒绝一个事件，例如由于一个序列化异常。如果一个事件被拒绝，它肯定不会出现在日志中。这是通过一个`EventRejectedException`发给一个`EventSourcedBehavior`，并可以由 @ref[监督者](fault-tolerance.md)处理。并非所有日志实现都使用拒绝并将此类问题也视为日志失败。

<a id="stash"></a>
## 存储

当使用`persist`或`persistAll`来持久化事件时，能够确保在确认事件已持久化并且运行了其他副作用之前，`EventSourcedBehavior`不会接收进一步的命令。传入的消息会自动存储，直到`persist`完成。

在恢复过程中，命令也会存储起来，并且不会干扰重播的事件。恢复完成后，命令将被接收。

上面描述的存储是自动处理的，但是也有可能在收到命令时将命令存储起来，以将其处理推迟到以后。一个例子是，可以等待某些外部条件或交互完成后再处理其他命令。这是通过返回一个`stash`效果并随后使用`thenUnstashAll`来实现的。

让我们使用一个任务管理器示例来说明如何使用存储效果。它处理三个命令；`StartTask`，`NextStep`和`EndTask`。这些命令与给定的`taskId`相关联，并且管理器一次处理一个`taskId`。一个任务在接收`StartTask`时启动，在接收`NextStep`命令时继续，直到接收到最终任务`EndTask`为止。使用另一个`taskId`而不是正在执行的`taskId`发出命令将通过存储它们来延迟执行。当`EndTask`被处理，一个新任务可以启动，并且存储命令已经处理。

Scala
:  @@snip [StashingExample.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/StashingExample.scala) { #stashing }

Java
:  @@snip [StashingExample.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/StashingExample.java) { #stashing }

您应该小心，不要向持久化actor发送超过其存储能力的消息，否则存储缓冲区将被填满，并且当达到最大容量时，命令将被丢弃。容量可以配置为：

```
akka.persistence.typed.stash-capacity = 10000
```

请注意，存储命令将保留在内存缓冲区中，因此如果发生崩溃，将不会对其进行处理。

* 如果actor(实体)被集群分片钝化或重新平衡，则存储的命令将被丢弃。
* 如果在持久化后处理一个命令或副作用时，由于抛出异常而导致actor重新启动(或停止)，则存储的命令将被丢弃
* 当存储事件失败时，隐藏的命令将被保留并在稍后进行处理
* 如果定义了一个`onPersistFailure`回退(backoff)监督者策略，在存储事件失败的情况下，存储命令被保存，并稍后被处理。

那些新添加的命令将不会被正在执行的`unstashAll`效果处理，而必须被另一个`unstashAll`取消存储。

<a id="scaling-out"></a>
## 横向扩展

在一个用例中，需要持久化actor的数量比一个节点的内存中的容量大，或者说弹性很重要，因此，如果一个节点崩溃，持久化actor将在一个新节点上快速启动，并可以恢复运行。@ref:[集群分片](cluster-sharding.md)是非常适合在集群上分布持久化actor并通过id对其进行寻址。

Akka持久化基于单个写入者原则。对于一个特定的`PersistenceId`，一次只能激活一个`EventSourcedBehavior`实例。如果多个实例要同时持久化事件，则事件将被交错处理，并且在重播时可能无法正确解释。集群分片确保数据中心中的每个id只有一个实体(`EventSourcedBehavior`)。Lightbend的[Multi-DC持久化](https://doc.akka.io/docs/akka-enhancements/current/persistence-dc/index.html)支持active-active跨数据中心的持久化实体.

<a id="configuration"></a>
## 配置

持久化模块有多个配置属性，请参考 @ref:[参考配置](../general/configuration-reference.md#config-akka-persistence)。

@ref:[日志和快照存储插件](../persistence-plugins.md)具有特定的配置，请参见所选择的插件的参考文档。

<a id="example-project"></a>
## 示例项目

@extref[持久化示例项目](samples:akka-samples-persistence-scala)是一个示例项目，可以被下载并带有如何运行的说明。该项目包含一个购物车样本，说明如何使用Akka持久化。

在 @extref[CQRS示例项目](samples:akka-samples-cqrs-scala)示例中进一步扩展了购物车示例。在该示例中，事件被标记为甚至由处理器使用，以根据事件构建其他表示，或将事件发布到其他服务。

@extref[Multi-DC持久化示例项目](samples:akka-samples-persistence-dc-scala)说明了如何将Lightbend的[Multi-DC持久化](https://doc.akka.io/docs/akka-enhancements/current/persistence-dc/index.html)与跨数据中心的active-active持久化实体一起使用。
