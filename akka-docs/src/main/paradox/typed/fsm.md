---
project.description: Finite State Machines (FSM) with Akka Actors.
---
<a id="behaviors-as-finite-state-machines"></a>
# 作为有限状态机的行为

有关此功能的Akka经典文档，请参阅 @ref:[经典FSM](../fsm.md)。

一个actor可用于建模有限状态机(FSM)。

为了演示这一点，考虑一个actor，它将在消息到达时接收消息并对消息进行排队，并在突发事件结束或收到刷新请求后发送。

本示例演示如何：

* 使用不同行为建模状态
* 模型在每个状态存储数据，通过将行为表示为一个方法
* 实施状态超时

FSM可以接收的事件成为Actor可以接收的消息的类型：

Scala
:  @@snip [FSMSocSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/FSMDocSpec.scala) { #simple-events }

Java
:  @@snip [FSMSocTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/FSMDocTest.java) { #simple-events }

`SetTarget`是用来启动的，设置要传递的`Batches`的目的地；`Queue`将添加到内部队列中，同时`Flush`将标记突发结束。

Scala
:  @@snip [FSMSocSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/FSMDocSpec.scala) { #simple-events }

Java
:  @@snip [FSMSocTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/FSMDocTest.java) { #simple-events }

`SetTarget`是用来启动的，设置要传递的`Batches`的目的地；`Queue`将添加到内部队列中，同时`Flush`将标记突发结束。

Scala
:  @@snip [FSMSocSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/FSMDocSpec.scala) { #storing-state }

Java
:  @@snip [FSMSocTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/FSMDocTest.java) { #storing-state }

每个状态都变成不同的行为，并且在处理一条消息后，将返回`Behavior`形式的下一个状态。

Scala
:  @@snip [FSMSocSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/FSMDocSpec.scala) { #simple-state }

Java
:  @@snip [FSMSocTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/FSMDocTest.java) { #simple-state}

上面的`idle`方法使用`Behaviors.unhandled`，它通知系统重用以前的行为，包括提示消息尚未处理。有两种相关的行为：

- 返回`Behaviors.empty`作为下一个行为，假如您达成一个状态，不希望再收到任何消息。例如，如果一个actor只等待所有衍生的子actor停止。未处理的消息仍然使用此行为进行日志记录。
- 返回`Behaviors.ignore`作为下一个行为，假如您不关心未处理的消息。发送给具有这种行为的actor的所有消息都会被删除和忽略(不进行日志记录)

要设置状态超时，请与`Behaviors.withTimers`一起使用`startSingleTimer`。

<a id="example-project"></a>
## 示例项目

@extref[FSM示例项目](samples:akka-samples-fsm-scala)是一个示例项目，可以下载并说明如何运行。

该项目包含一个Dining Hakkers样本，演示了如何使用actor对有限状态机(FSM)进行建模。
