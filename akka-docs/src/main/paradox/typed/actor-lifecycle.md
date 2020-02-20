---
project.description: The Akka Actor lifecycle.
---
<a id="actor-lifecycle"></a>
# Actor生命周期

有关此功能的Akka经典文档，请参阅 @ref:[经典Actor](../actors.md)。

## 依赖

要使用类型化Akka Actor，必须在项目中添加以下依赖项：

@@dependency[sbt,Maven,Gradle] {
  group=com.typesafe.akka
  artifact=akka-actor-typed_$scala.binary_version$
  version=$akka.version$
}

## 介绍

一个actor是必须显式启动和停止的有状态资源。

需要注意的是，当不再引用Actor时，Actor不会自动停止，创建的每个Actor也必须显式地销毁。惟一的简化是，停止父Actor也将递归地停止父Actor创建的所有子Actor。`ActorSystem`关闭时，所有actor也将自动停止。

@@@ note
一个`ActorSystem`是一个重量级结构，它将分配线程，因此请为每个逻辑应用程序创建一个。通常每个JVM进程一个`ActorSystem`。
@@@

<a id="creating-actors"></a>
## 创建Actor

一个actor可以创建，或 _spawn_，任意数量的子actor，这些反过来又可以产生它们自己的后代，从而形成一个actor层次结构。@apidoc[akka.actor.typed.ActorSystem] 托管层次结构，并且只能有 _一个根actor，actor位于`ActorSystem`层次结构的顶部。子actor的生命周期与父actor绑定在一起 – 子actor可以自己停止或者在任何时候被停止，但它永远不会比它的父actor活得更久。

<a id="the-actorcontext"></a>
### ActorContext

可以出于多种目的访问ActorContext，例如：

* 产生子actor和监督
* 监控其它actor，为了接收`Terminated(otherActor)`事件，当监控的actor永久停止时
* 日志记录
* 创建消息适配器
* 与其它actor请求-响应交互(ask)
* 访问`self` ActorRef

如果一个行为需要使用`ActorContext`，例如产生子actor或使用`context.self`，则可以用`Behaviors.setup`包装构造得到：

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/IntroSpec.scala) { #hello-world-main }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/IntroTest.java) { #hello-world-main-setup }

<a id="actorcontext-thread-safety"></a>
#### ActorContext线程安全

`ActorContext`中的许多方法都不是线程安全的，并且

* 不能被`scala.concurrent.Future`回调中的线程访问
* 不能在多个actor实例之间共享
* 必须仅在普通actor消息处理线程中使用

<a id="the-guardian-actor"></a>
### 守护者Actor

顶级actor(也称为用户监督者actor)与`ActorSystem`一起创建。发送到actor系统的消息被定向到根actor。根actor由用于创建`ActorSystem`的行为定义，在以下示例中被命名为`HelloWorldMain`：

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/IntroSpec.scala) { #hello-world }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/IntroTest.java) { #hello-world }

对于非常简单的应用程序，监督者可能包含实际的应用程序逻辑并处理消息。一旦应用程序处理了不止一个问题，监督者就应该引导应用程序，将各种子系统作为后代生成，并监视它们的生命周期。

当监督者actor停止时，这将停止`ActorSystem`。

当`ActorSystem.terminate`被调用， @ref:[联动关闭](../coordinated-shutdown.md)过程将停止actor和服务，按一个特定的顺序。

@@@ Note

In the classic counter part, the @apidoc[akka.actor.ActorSystem], the root actor was provided out of the box and you
could spawn top-level actors from the outside of the `ActorSystem` using `actorOf`. @ref:[SpawnProtocol](#spawnprotocol)
is a tool that mimics the old style of starting up actors.

@@@

<a id="spawning-children"></a>
### 产生后代

子actor由 @apidoc[typed.*.ActorContext]的`spawn`创建和启动。在下面的示例中，当根actor启动时，它生成一个由`HelloWorld`行为描述的子actor。此外，当根actor接收到一条`Start`消息时，它将创建一个由`HelloWorldBot`行为定义的子actor:

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/IntroSpec.scala) { #hello-world-main }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/IntroTest.java) { #hello-world-main }

要在生成actor时指定调度程序，请使用 @apidoc[DispatcherSelector]。如果未指定，则actor将使用默认调度程序，有关详细信息，请参见 @ref:[默认调度程序](dispatchers.md#default-dispatcher)。

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/IntroSpec.scala) { #hello-world-main-with-dispatchers }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/IntroTest.java) { #hello-world-main-with-dispatchers }

有关上述示例的演练，请参阅 @ref:[Actors](actors.md#first-example)。

<a id="spawnprotocol"></a>
### Spawn协议

监督者actor应负责任务的初始化并创建应用程序的初始actor，但是有时您可能想从监督者外部产生新的actor。例如，每个HTTP请求创建一个actor。

这并不难在你的行为中实现，但是，由于这是一种常见的模式，因此有一个预定义的消息协议和实现行为。它可以作为`ActorSystem`的监督者actor，也可以与`Behaviors.setup`结合使用，设置启动一些初始任务或actor。然后，可以通过向系统的actor引用告知或询问`SpawnProtocol.Spawn`，从外部启动子actor。衍生到系统的参与者引用。在使用`ask`的时候，这类似于`ActorSystem.actorOf`如何可以用在经典的actor中，不同之处在于返回的是一个`ActorRef`的`Future`。

监护人的行为可以定义为：

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/SpawnProtocolDocSpec.scala) { #imports1 #main }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/SpawnProtocolDocTest.java) { #imports1 #main }

并且`ActorSystem`可以用`main`行为创建，并要求生成其他actor：

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/SpawnProtocolDocSpec.scala) { #imports2 #system-spawn }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/SpawnProtocolDocTest.java) { #imports2 #system-spawn }

`SpawnProtocol`也可以在actor层次结构的其他地方使用。它不必是根监督者actor。

@ref:[Actor发现](actor-discovery.md)中介绍了一种查找正在运行的actor的方法。

<a id="stopping-actors"></a>
## 停止Actor

Actor可以通过返回`Behaviors.stopped`作为下一个行为来停止自己。

通过使用来自父actor的`ActorContext`的`stop`方法，可以迫使子actor在处理完当前消息后停止。这种方式只能停止子actor。

当其父actor被停止时，所有子actor也将被停止。

当一个actor停止时，它会收到可用于清理资源的`PostStop`信号。可以将一个回调函数作为参赛指定给`Behaviors.stopped`，用于在正常停止时处理`PostStop`信号。这允许在突然停止时应用不同的动作。

这是一个说明性的示例：

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/GracefulStopDocSpec.scala) {
    #imports
    #master-actor
    #worker-actor
    #graceful-shutdown
  }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/GracefulStopDocTest.java)  {
   #imports
   #master-actor
   #worker-actor
   #graceful-shutdown
 }

从`PostStop`清除资源时，您还应该考虑对`PreRestart`信号执行相同的操作，该信号在 @ref:[actor重新启动后](fault-tolerance.md#the-prerestart-signal)发出。请注意，重新启动时不会发出`PostStop`。

<a id="watching-actors"></a>
## 监视Actor

为了在另一个actor终止时(即永久停止，不是暂时性的故障并重新启动)得到通知，一个actor可以选择`watch`另一个actor。当被监视的actor终止(请参阅 @ref:[停止Actor](#stopping-actors))后，它将接收 @apidoc[akka.actor.typed.Terminated]信号。

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/GracefulStopDocSpec.scala) { #master-actor-watch }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/GracefulStopDocTest.java)  { #master-actor-watch }

`watch`的另一种替代方法是`watchWith`，它允许指定自定义消息，而不是`Terminted`。这通常比使用`watch`和`Terminated`信号更可取，因为消息中可以包含附加信息，这可以在后面接收消息时使用。

与上面类似的示例，但是使用`watchWith`并在作业完成后回复原始请求者。

Scala
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/GracefulStopDocSpec.scala) { #master-actor-watchWith }

Java
:  @@snip [IntroSpec.scala](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/GracefulStopDocTest.java)  { #master-actor-watchWith }

请注意`replyToWhenDone`如何包含在`watchWith`消息中，然后在接收`JobTerminated`消息时使用。

受监视的actor可以是任何`ActorRef`，它不必是上面例子中的子actor。

应当注意，终止消息的产生与注册和终止发生的顺序无关。特别地，即使被监视actor已经被终止，监视actor也将在注册时接收到终止消息。

多次注册并不一定会导致生成多条消息，但是不能保证仅接收到一条这样的消息：如果被监视的actor的终止已经生成，并排队了消息，并且在此消息被处理之前进行了另一次注册，此时第二条消息将排队，因为注册监视已经终止的actor会直接生成终止的消息。

也可以使用`context.unwatch(target)`取消监视其他actor的活跃状态。即使已终止的消息已在邮箱中排队，也可以这样做；在调用`unwatch`后，该actor的终止消息将不再被处理。

当被监视的actor从 @ref:[Cluster](cluster.md)节点上删除时，也会发送终止的消息。
