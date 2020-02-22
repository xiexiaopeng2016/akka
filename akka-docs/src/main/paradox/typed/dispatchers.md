---
project.description: Akka dispatchers and how to choose the right ones.
---
<a id="dispatchers"></a>
# 调度器

有关此功能的Akka经典文档，请参阅 @ref:[经典调度器](../dispatchers.md)。

## 依赖

调度器是Akka核心的一部分，这意味着它们是`akka-actor`依赖项的一部分。这个页面描述了如何通过`akka-actor-typed`使用调度程序，它依赖：

@@dependency[sbt,Maven,Gradle] {
  group="com.typesafe.akka"
  artifact="akka-actor-typed_$scala.binary_version$"
  version="$akka.version$"
}

<a id="introduction"></a>
## 介绍 

一个Akka `MessageDispatcher`是使Akka Actors变得"滴答作响(tick)"的原因，这可以说是这台机器的引擎。所有`MessageDispatcher`实现也是一个`ExecutionContext`，这意味着它们可用于执行任意代码，例如`Future`。

<a id="default-dispatcher"></a>
## 默认调度器

每个`ActorSystem`都将有一个默认的调度器，如果没有为一个`Actor`配置任何其他内容，则将使用它。可以配置默认的调度器，默认情况下，它是一个配置了`akka.actor.default-dispatcher.executor`的`Dispatcher`。如果未选择执行器，则将选择一个"fork-join-executor"，这在大多数情况下均具有出色的性能。

<a id="internal-dispatcher"></a>
## 内部调度器

为了保护由各种Akka模块生成的内部Actor，默认情况下使用单独的内部调度器。可以通过设置`akka.actor.internal-dispatcher`以细粒度的方式调优内部调度器，它也可以被其他调度器取代，通过给`akka.actor.internal-dispatcher`取一个 @ref[别名](#dispatcher-aliases)。

<a id="dispatcher-lookup"></a>
## 查找调度器

调度器实现了`ExecutionContext`接口，因此可以用来运行`Future`调用等。

Scala
:  @@snip [DispatcherDocSpec.scala](/akka-docs/src/test/scala/docs/actor/typed/DispatcherDocSpec.scala) { #lookup }

Java
:  @@snip [DispatcherDocTest.java](/akka-docs/src/test/java/jdocs/actor/typed/DispatcherDocTest.java) { #lookup }

<a id="selecting-a-dispatcher"></a>
## 选择一个调度器

默认的调度器用于在不指定自定义调度器的情况下生成的所有actor。这适用于所有不阻塞的actor。需要仔细管理actor中的阻塞，更多详细信息请参见 @ref:[此处](#blocking-needs-careful-management)。

要选择一个调度器，请使用`DispatcherSelector`创建一个`Props`实例来生成actor：

Scala
:  @@snip [DispatcherDocSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/DispatchersDocSpec.scala) { #spawn-dispatcher }

Java
:  @@snip [DispatcherDocTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/DispatchersDocTest.java) { #spawn-dispatcher }

`DispatcherSelector`有几种方便的方法：

* `DispatcherSelector.default`用来查找默认调度器
* `DispatcherSelector.blocking` 可用于执行阻塞的actor，例如不支持`Future`的遗留数据库API
* `DispatcherSelector.sameAsParent` 使用与父actor相同的调度器

最后一个示例显示如何从配置中加载自定义调度器，并依赖于`application.conf`：

<!-- Same between Java and Scala -->
@@snip [DispatcherDocSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/DispatchersDocSpec.scala) { #config }

<a id="types-of-dispatchers"></a>
## 调度器的类型

有两种不同类型的消息调度器：

* **Dispatcher**
	
	这是基于事件的调度器，它将一组Actor绑定到线程池。如果未指定其他调度器，则使用默认调度器。

    * 可共享: 无限
    * 邮箱: 任意，每个Actor创建一个
    * 用例: 默认调度程序，隔板(Bulkheading)
    * 驱动: `java.util.concurrent.ExecutorService`。指定使用"executor" 使用 "fork-join-executor"，"thread-pool-executor"或完全限定类名`akka.dispatcher.ExecutorServiceConfigurator`实现。

* **PinnedDispatcher**
	
	该调度器为使用它的每个actor分配一个唯一的线程；也就是说，每个actor将拥有自己的线程池，并且该池中只有一个线程。

    * 可共享: 无
    * 邮箱: 任意，每个Actor创建一个
    * 用例: 隔板(Bulkheading)
    * 驱动: 任何`akka.dispatch.ThreadPoolExecutorConfigurator`。
      默认一个"thread-pool-executor"。

下面是一个Fork连接池调度器的配置示例:

<!--same config text for Scala & Java-->
@@snip [DispatcherDocSpec.scala](/akka-docs/src/test/scala/docs/dispatcher/DispatcherDocSpec.scala) { #my-dispatcher-config }

有关更多配置选项，请参阅 @ref:[更多调度器配置示例](#more-dispatcher-configuration-examples)部分和`default-dispatcher` @ref:[配置](../general/configuration.md)部分。

@@@ note

在`fork-join-executor`的`parallelism-max`不设置由ForkJoinPool分配的总线程数的上限。此设置专门讨论池将保持运行的 *热* 线程数，以减少处理新传入任务的延迟。您可以在JDK的[ForkJoinPool文档](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html)中阅读有关并行性的更多信息。

@@@

@@@ note

`thread-pool-executor`调度器通过使用`java.util.concurrent.ThreadPoolExecutor`实现。您可以在JDK的[ThreadPoolExecutor文档](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html)中阅读有关它的更多信息。

@@@

<a id="dispatcher-aliases"></a>
## 调度器别名

当查找调度器时，且给定设置包含字符串而不是调度器配置块，查找会将其视为一个别名，然后根据该字符串找到调度器配置的另一个位置。如果通过别名和绝对路径引用调度器配置，那么两个id之间将只使用和共享一个调度器。

示例：配置`internal-dispatcher`为`default-dispatcher`的别名：

```
akka.actor.internal-dispatcher = akka.actor.default-dispatcher
```

<a id="blocking-needs-careful-management"></a>
## 阻塞需要仔细管理

在某些情况下，进行阻塞操作是不可避免的，例如，让一个线程休眠不确定的时间，等待外部事件发生。示例是旧式RDBMS驱动程序或消息传递API，其根本原因通常是(网络)I/O发生在幕后。

<a id="problem-blocking-on-default-dispatcher"></a>
### 问题：默认调度器上的阻塞

像这样简单地将阻塞调用添加到actor消息处理中是有问题的：

Scala
:   @@snip [BlockingDispatcherSample.scala](/akka-docs/src/test/scala/docs/actor/typed/BlockingActor.scala) { #blocking-in-actor }

Java
:   @@snip [BlockingActor.java](/akka-docs/src/test/java/jdocs/actor/typed/BlockingActor.java) { #blocking-in-actor }

无需任何进一步的配置，默认调度器就可以将此actor与所有其他actor一起运行。当所有actor消息处理都是非阻塞的时候，这是非常有效的。但是，如果所有可用线程都被阻塞，则同一调度器上的所有actor都将饿死线程，并且将无法处理传入消息。

@@@ note

如果可能，也应避免使用阻塞API。尝试查找或构建响应API，以使阻塞最小化，或移至专用调度器。

通常，在与现有库或系统集成时，无法避免阻塞API。以下解决方案说明了如何正确处理阻塞操作。

请注意，同样的提示适用于在Akka中的任何地方管理阻塞操作，包括Streams，Http和在其之上构建的其他反应式库。

@@@

为了演示这个问题，让我们使用上述`BlockingActor`和以下`PrintActor`设置应用程序：

Scala
:   @@snip [PrintActor.scala](/akka-docs/src/test/scala/docs/actor/typed/PrintActor.scala) { #print-actor }

Java
:   @@snip [PrintActor.java](/akka-docs/src/test/java/jdocs/actor/typed/PrintActor.java) { #print-actor }


Scala
:   @@snip [BlockingDispatcherSample.scala](/akka-docs/src/test/scala/docs/actor/typed/BlockingDispatcherSample.scala) { #blocking-main }

Java
:   @@snip [BlockingDispatcherTest.java](/akka-docs/src/test/java/jdocs/actor/typed/BlockingDispatcherTest.java) { #blocking-main }

该应用程序正在向`BlockingActor`和`PrintActor`发送100条消息，并且大量`akka.actor.default-dispatcher`线程正在处理请求。当你运行上面的代码时，你会看到整个应用程序被卡在这样的地方：

```
>　PrintActor: 44
>　PrintActor: 45
```

`PrintActor`被认为是非阻塞的，但是，它不能继续处理剩余的消息，因为所有线程都被其他阻塞actor占用和阻塞 - 从而导致线程饥饿。

在下面的线程状态图中，颜色具有以下含义：

 * 绿松石 - 睡眠状态
 * 橙色 - 等待状态
 * 绿色 - 可运行状态

线程信息是使用YourKit分析工具记录的，但是任何不错的JVM分析工具都具有此功能(包括免费的和捆绑的Oracle JDK [VisualVM](https://visualvm.github.io/)以及 [Java Mission Control](https://openjdk.java.net/projects/jmc/))。

线程的橙色部分表明它处于空闲状态。空闲线程很好 - 他们准备接受新工作。但是，大量的绿松石(在我们的示例中为阻塞或休眠)线程导致线程饥饿。

@@@ note

如果您拥有Lightbend订阅，则可以使用商用[线程饥饿探测器](https://doc.akka.io/docs/akka-enhancements/current/starvation-detector.html)，它将发出警告日志语句，如果它检测到任何您的调度器遭受饥饿和其他。这是确定生产系统中正在发生问题的第一步，非常有用，然后您可以应用下面所介绍的解决方案。

@@@

![dispatcher-behaviour-on-bad-code.png](../images/dispatcher-behaviour-on-bad-code.png)

在上面的示例中，我们通过向阻塞actor发送数百条消息来使代码处于负载状态，这导致默认调度器的线程被阻止。然后，Akka中基于fork联接池的调度器会尝试通过向池中添加更多线程(`default-akka.actor.default-dispatcher 18,19,20,...`)来弥补这种阻塞。但是，如果这些操作也会立即被阻塞，并且阻塞操作最终会控制整个调度器，这将无济于事。

从本质上讲，该`Thread.sleep`操作控制了所有线程，并导致在默认调度器上执行的任何东西都在渴求资源(包括尚未配置显式调度器的任何actor)。

@@@ div { .group-scala }

<a id="non-solution-wrapping-in-a-future"></a>
### 非解决方案：包装在一个Future中

<!--
  A CompletableFuture by default on ForkJoinPool.commonPool(), so
  because that is already separate from the default dispatcher
  the problem described in these sections do not apply:
-->

面对这种情况，你可能会忍不住把阻塞调用封装在一个`Future`中，转而使用它，但是这种策略过于简单：当应用程序在增加的负载下运行时，您很可能会发现瓶颈或耗尽内存或线程。

Scala
:   @@snip [BlockingDispatcherSample.scala](/akka-docs/src/test/scala/docs/actor/typed/BlockingDispatcherSample.scala) { #blocking-in-future }

这里的关键问题行是：

```scala
implicit val executionContext: ExecutionContext = context.executionContext
```

使用`context.executionContext`作为阻塞`Future`的调度程序执行，仍然可能是一个问题，因为这个调度器默认用于所有其他actor处理，除非你 @ref:[为actor设置一个单独的调度器](../dispatchers.md#setting-the-dispatcher-for-an-actor)。

@@@

<a id="solution-dedicated-dispatcher-for-blocking-operations"></a>
### 解决方案：专门的调度器用于阻塞操作

隔离阻塞行为的一种有效方法是为所有这些阻塞操作准备并使用一个专用的调度器，这样它就不会影响系统的其余部分。

隔离阻塞行为以使其不影响系统其余部分的有效方法是为所有这些阻塞操作准备并使用专用的调度程序。这种技术通常被称为"bulk-heading"或简称为"隔离阻塞"。

在`application.conf`中，专门用于阻塞行为的调度器应配置如下：

<!--same config text for Scala & Java-->
@@snip [BlockingDispatcherSample.scala](/akka-docs/src/test/scala/docs/actor/typed/BlockingDispatcherSample.scala) { #my-blocking-dispatcher-config }

一个基于`thread-pool-executor`的调度器允许我们限制它将托管的线程数，通过这种方式，我们可以严格控制系统可能使用的阻塞线程的最大数量。

确切的大小应该根据您期望在这个调度器上运行的工作负载进行调优。

当需要进行阻塞时，请使用上面配置的调度器，而不是默认的那个：

Scala
:   @@snip [BlockingDispatcherSample.scala](/akka-docs/src/test/scala/docs/actor/typed/BlockingDispatcherSample.scala) { #separate-dispatcher }

Java
:   @@snip [SeparateDispatcherCompletionStageActor.java](/akka-docs/src/test/java/jdocs/actor/typed/SeparateDispatcherCompletionStageActor.java) { #separate-dispatcher }

下图显示了线程池行为。

![dispatcher-behaviour-on-good-code.png](../images/dispatcher-behaviour-on-good-code.png)

发送到`SeparateDispatcherFutureActor`和`PrintActor`的消息使用默认的调度程序进行处理 - 绿线，它代表的实际执行。

当阻塞操作在`my-blocking-dispatcher`上运行时，它使用线程(直到配置的限制)来处理这些操作。

在上运行阻止操作时my-blocking-dispatcher，它将使用线程（达到配置的限制）来处理这些操作。在这种情况下，休眠被很好地隔离到这个调度器中，默认的那个不会受到影响，允许应用程序的其余部分继续运行，就好像没有什么不好的事情发生一样。经过一段时间的空闲后，这个调度程序启动的线程将被关闭。

在这种情况下，其他actor的吞吐量没有受到影响 - 它们仍由默认调度器提供。

这是处理反应式应用程序中任何类型的阻塞的推荐方法。

对于专门关于Akka HTTP的类似讨论，请参阅处理 @extref[处理Akka HTTP中的阻塞操作](akka.http:handling-blocking-operations-in-akka-http-routes.html)。

<a id="available-solutions-to-blocking-operations"></a>
### 阻塞操作的可用解决方案

对于“阻塞问题”的适当解决方案的非详尽清单包括以下建议：

 * 在一个`Future`中进行阻塞调用，确保在任何时间点上此类调用数量的上限(提交无限数量的此类任务将耗尽您的内存或线程限制)。
 * 在一个`Future`中进行阻塞调用，提供一个有线程数上限的线程池，该上限适用于运行应用程序的硬件，如本节中所详细说明。
 * 指定一个线程来管理一组阻塞资源(例如，驱动多个通道的NIO选择器)，并在事件作为actor消息发生时调度它们。
 * 在 @ref:[路由器](../routing.md)管理的一个actor(或一组actor)内执行阻塞调用，请确保配置一个线程池，该线程池或专用于此，或足够大。

最后一种方法特别适合于单线程的资源，像数据库句柄，传统上一次只能执行一个未完成的查询，并使用内部同步来确保这一点。一个常见的模式是为N个actor创建一个路由器，每个actor包装一个DB连接并处理发送到路由器的查询。然后必须调优数字N以获得最大的吞吐量，这取决于DBMS部署在什么硬件上。

@@@ note

配置线程池是一个最好委托给Akka的任务，在`application.conf`中配置它，并通过一个 @ref:[`ActorSystem`](#dispatcher-lookup)实例化

@@@

<a id="more-dispatcher-configuration-examples"></a>
## 更多调度器配置示例

<a id="fixed-pool-size"></a>
### 固定池大小

配置一个具有固定线程池大小的调度器，例如，对于执行阻塞IO的actor：

@@snip [DispatcherDocSpec.scala](/akka-docs/src/test/scala/docs/dispatcher/DispatcherDocSpec.scala) { #fixed-pool-size-dispatcher-config }

### 内核数

另一个基于内核数使用线程池的示例(例如，用于CPU绑定任务)

<!--same config text for Scala & Java-->
@@snip [DispatcherDocSpec.scala](/akka-docs/src/test/scala/docs/dispatcher/DispatcherDocSpec.scala) {#my-thread-pool-dispatcher-config }

### 固定(Pinned)

为每个配置为使用固定调度器的actor分配一个单独的线程。

配置一个`PinnedDispatcher`:

<!--same config text for Scala & Java-->
@@snip [DispatcherDocSpec.scala](/akka-docs/src/test/scala/docs/dispatcher/DispatcherDocSpec.scala) {#my-pinned-dispatcher-config }

请注意，上面的`my-thread-pool-dispatcher`示例中的`thread-pool-executor`配置是不适用的。这是因为每个actor在使用`PinnedDispatcher`时都有自己的线程池，而这个线程池只有一个线程。

请注意，不能保证在一段时间内使用*相同的*线程，因为`PinnedDispatcher`使用了核心池超时来降低空闲角色的资源使用。要一直使用同一个线程，需要添加`thread-pool-executor.allow-core-timeout=off`到`PinnedDispatcher`的配置。

<a id="thread-shutdown-timeout"></a>
### 线程关闭超时

`fork-join-executor`和`thread-pool-executor`都可能在关闭线程的时候不会用到。如果希望延长线程的生存时间，可以调整一些超时设置。

<!--same config text for Scala & Java-->
@@snip [DispatcherDocSpec.scala](/akka-docs/src/test/scala/docs/dispatcher/DispatcherDocSpec.scala) {#my-dispatcher-with-timeouts-config }

当将调度器用作一个`ExecutionContext`而不给它分配actor时，`shutdown-timeout`通常应该增加，因为默认值1秒可能会导致整个线程池的关闭频率过高。
