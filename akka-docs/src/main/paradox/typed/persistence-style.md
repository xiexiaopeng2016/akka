<a id="style-guide"></a>
# 风格指南

<a id="event-handlers-in-the-state"></a>
## 状态中的事件处理程序

关于 @ref:[更改行为](persistence.md#changing-behavior)的部分描述了如何根据状态来不同地处理命令和事件。可以更进一步，在状态类中定义事件处理程序。下一节的 @ref:[命令处理程序](#command-handlers-in-the-state)也定义在状态中。

状态可以看作是您的领域对象，并且它应该包含核心业务逻辑。此外，事件处理程序和命令处理程序是应该定义在状态中，还是应该定义在状态之外，这只是一个喜好问题了。

在这里，我们使用一个银行帐户作为示例领域。它具有3个状态类，它们代表帐户的生命周期；`EmptyAccount`，`OpenedAccount`，和`ClosedAccount`。

Scala
:  @@snip [AccountExampleWithEventHandlersInState.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleWithEventHandlersInState.scala) { #account-entity }

Java
:  @@snip [AccountExampleWithEventHandlersInState.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleWithEventHandlersInState.java) { #account-entity }

请注意`eventHandler`如何在`Account`(状态)中委托给`applyEvent`，这是在具体的`EmptyAccount`，`OpenedAccount`和`ClosedAccount`实现的。

@@@ div { .group-scala }
<a id="command-handlers-in-the-state"></a>
## 状态中的命令处理程序

我们还可以通过在状态中处理命令进一步处理前面的银行帐户示例。

Scala
:  @@snip [AccountExampleWithCommandHandlersInState.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleWithCommandHandlersInState.scala) { #account-entity }

注意命令处理程序是如何在`Account`(状态)中委托给`applyCommand`的，它是在具体的`EmptyAccount`，`OpenedAccount`和`ClosedAccount`中实现的。

@@@

<a id="optional-initial-state"></a>
## 可选的初始状态

有时不希望为空的初始状态使用单独的状态类，而是将其视为还没有状态。`Option[State]`可以被用作状态类型和`None`作为`emptyState`。然后，在委托给状态或其他方法之前，将模式匹配用于外层的命令和事件处理程序中。


Scala
:  @@snip [AccountExampleWithOptionState.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleWithOptionState.scala) { #account-entity }

Java
:  @@snip [AccountExampleWithNullState.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleWithNullState.java) { #account-entity }

@@@ div { .group-java }
## Mutable state

The state can be mutable or immutable. When it is immutable the event handler returns a new instance of the state
for each change.

When using mutable state it's important to not send the full state instance as a message to another actor,
e.g. as a reply to a command. Messages must be immutable to avoid concurrency problems.

The above examples are using immutable state classes and below is corresponding example with mutable state.

Java
:  @@snip [AccountExampleWithNullState.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleWithMutableState.java) { #account-entity }

@@@
