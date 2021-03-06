# Akka流背后的设计原理

我们花了很长时间才对API和实现的架构的面貌感到满意，并且在直觉的指导下，设计阶段是非常探索性的研究。本节详细介绍了这些发现，并将它们编入在此过程中出现的一套原则。

@@@ note

如简介中所述，请记住，Akka流 API与响应流接口完全解耦，它是关于如何在各个运算符之间传递流数据的实现细节。

@@@

## Akka流的用户应该期待什么?

Akka建立在一个有意的决策上，即提供最小且一致的api - 而不是简单或直观的api。我们的信条是，与魔术相比，我们更喜欢明确性，如果我们提供一个功能，那么它必须始终工作，没有例外。另一种说法是，我们尽量减少用户必须学习的规则数量，而不是尝试使规则接近我们认为用户可能期望的规则。

由此可见，Akka流实现的原则是：

 * API中的所有功能都是明确的，没有魔术
 * 最高的组合性：组合件保留了每个部件的功能
 * 分布式有界流处理领域的详尽模型

这意味着我们提供了所有必要的工具来表达任何流处理拓扑，我们对这个域的所有基本方面建模(背压、缓冲、转换、故障恢复等)，并且无论用户构建什么都可以在更大的上下文中重用。

### Akka流不会将掉线的流元素发送到死信办公室

只提供可以信赖的特性的一个重要后果是，Akka流不能确保通过处理拓扑发送的所有对象都得到处理。元素可能被删除的原因有很多:

 * 普通的用户代码可以消费 *map(...)* 运算符中的一个元素，并产生一个完全不同的元素作为结果
 * 常见的流运算符有意地删除元素，例如take/drop/filter/conflate/buffer/…
 * 流故障将在没有等待处理完成的情况下中断流，所有处于迁徙状态的元素都将被丢弃
 * 流取消将向上传播(例如，来自 *take* 操作符)，导致上游处理步骤终止，而没有处理它们的所有输入

这意味着，将JVM对象发送到需要清除的流中将需要用户确保此操作在Akka流设施之外发生(例如，在超时后或在流输出中观察到它们的结果时清理它们，或使用终结器等其他方法)。

### 最终实现的注意事项

组合性要求部分流拓扑具有可重用性，这导致我们采用了一种将数据流描述为(部分)图的提升方法，这些图可以充当复合的源，flows(即管道)和数据接收器。这些构建块将要可以自由共享，并能够自由地组合它们，形成更大的图形。因此，这些片段的表述必须是一个不可变的蓝图，并在显式步骤中物化，以便启动流处理。作为结果的流处理引擎也是不可变的，因为它具有由蓝图规定的固定拓扑结构。动态网络需要通过显式地使用反应式流接口将不同的引擎连接在一起来建模。

物化过程通常会创建特定的对象，这些对象在处理引擎运行时非常有用，例如用于关闭它或提取度量。这意味着物化函数产生的结果称为 *图的物化值* 。

## 与其他响应流实现的交互

Akka流完全实现了响应流规范，并可以与所有其他一致的实现进行交互。我们选择将响应流接口与用户级API彻底地解耦，因为我们认为它们是不面向最终用户的SPI。为了从Akka Stream拓扑获得一个`Publisher`或`Subscriber`，必须使用一个对应的`Sink.asPublisher`或`Source.asSubscriber`元素。

所有由Akka流的默认物化产生的流处理器都被限制为只有一个订阅者，额外的订阅者将被拒绝。这样做的原因是，使用DSL描述的流拓扑永远不需要元素的发布端的扇出行为，所有扇出都使用诸如`Broadcast[T]`的显式元素来完成。

这意味着`Sink.asPublisher(true)`(用于启用扇出支持)必须在需要广播行为与其他响应流实现交互的地方使用。

### Sink/Source/Flow没有直接扩展响应流接口的原理和好处 

关于[响应流](https://github.com/reactive-streams/reactive-streams-jvm/)的一个有时被忽略的关键信息是它们是一个 [服务提供者接口](https://en.m.wikipedia.org/wiki/Service_provider_interface)，正如在有关该规范的 [早期讨论](https://github.com/reactive-streams/reactive-streams-jvm/pull/25)之一中所深入解释的那样。Akka流是在响应流的开发过程中设计的，因此它们彼此之间有很大的影响。

了解到即使在响应式规范中，类型最初也试图向API的用户隐藏`Publisher`、`Subscriber`和其他SPI类型，这可能会有所启发。尽管在某些情况下，这些内部SPI类型最终会出现在标准的最终用户面前，所以决定 [删除API类型，只有保持SPI类型](https://github.com/reactive-streams/reactive-streams-jvm/pull/25)它们是`Publisher`，`Subscriber`等等。

有了标准目标的历史知识和上下文 – 作为交互库的内部细节 – 我们可以肯定地说，它不能真的被说成与这些类型的一种直接的_继承_关系视为某种形式的优势或库之间有意义的区别。相反，可以看出向最终用户公开那些SPI类型的API意外泄漏了内部实现细节。

`Source`，`Sink`和`Flow`类型是Akka流的一部分，它们的目的是提供流畅的DSL，并充当运行这些流的"工厂"。它们在响应流中的直接对应项分别是`Publisher`，`Subscriber`和`Processor`。换句话说，Akka流对计算图的提升表示进行操作，然后根据响应流规则将其物化并执行。这还允许Akka流在物化步骤中执行诸如融合和调度程序配置之类的优化。

源于隐藏响应流接口的另一个不明显的收获来自于这样一个事实，即`org.reactivestreams.Subscriber`(等等)现在已包含在Java 9+中，并因此成为Java本身的一部分，因此库应迁移到使用`java.util.concurrent.Flow.Subscriber`代替`org.reactivestreams.Subscriber`。选择公开和直接扩展响应流类型的库现在将更难适应JDK9 +类型 -- 他们所有扩展Subscriber和Friends的类都将需要复制或更改以扩展完全相同的接口，但是从不同的包中进行的。在Akka中，我们仅在需要时暴露新类型 -- 已经支持JDK9类型，从JDK9发布之日起。

隐藏响应流接口的另一个可能是更重要的原因，可以追溯到该解释的第一点：响应流是SPI的事实，因此在专门实现中很难"正确"。因此，Akka流不鼓励使用底层基础设施中难以实现的部分，并为用户提供了更简单，更类型安全但功能更强大的抽象供用户使用：GraphStages和运算符。当然，仍然可以(或轻松地)接受或获取流操作符的响应流(或JDK+ Flow)表示形式，通过使用类似`asPublisher`或`fromSubscriber`的方法。

## streaming库用户应该期待什么？

我们期望库将基于Akka流构建，实际上Akka HTTP就是这样一个示例，它存在于Akka项目本身中。为了让用户从上述Akka流的原则中获益，我们制定了以下规则:

 * 库应向用户提供可重复使用的部件，即公开返回操作符的工厂，允许完全的组合性
 * 库能够可选并额外地提供消耗和物化操作符的设施

第一条规则背后的理由是，如果不同的库只接受运算符并期望将其物化，则组合性将被破坏：同时使用这两种方法是不可能的，因为物化只能发生一次。因此，库的功能必须表示为用户可以在库的控制之外完成物化。

第二个规则允许库为常见情况额外提供良好的支持，例如Akka HTTP API，它提供了一个方便物化的`handleWith`方法。

@@@ note

这样做的一个重要后果是，可重用的flow描述不能绑定到"实时"资源，任何连接或分配此类资源必须延迟，直到物化时间。"实时"资源的例子是已经存在的TCP连接，多点广播发布者等。一个TickSource不会属于此类别，如果它的计时器仅在物化时创建(例如我们的实现)。

对此的例外情况需要进行充分的论证并仔细记录。

@@@

### 最终实现的约束

Akka流必须使库能够根据不变的蓝图表达任何流处理实用程序。最常见的构建基块是

 * Source: 只有一个输出流的东西
 * Sink: 只有一个输入流的东西
 * Flow: 恰好只有一个输入和一个输出流的东西
 * BidiFlow: 恰好有两个输入流和两个输出流的东西，它们在概念上表现为两个相反方向的流
 * Graph: 一种封装的流处理拓扑，它公开一组输入和输出端口，表示为一个`Shape`类型的对象。

@@@ note

一个发出流中的流(a stream of streams)的源仍然是普通源，所生成的元素种类在要表达的静态流拓扑中不起作用。

@@@

## 错误和失败之间的区别

本次讨论的起点是[反应式宣言给出的定义](http://www.reactivemanifesto.org/glossary#Failure)。转换为流意味着在流中错误可以作为普通数据元素来访问，而故障意味着流本身已经失败并且正在崩溃。具体而言，在响应流接口级数据元素(包括错误)是通过`onNext`发出信号，而故障则抛出`onError`信号。

@@@ note

不幸的是，由于历史原因，向订阅者发送*失败*信号的方法名为`onError`。始终牢记，响应流接口(Publisher/Subscription/Subscriber)正在为在执行单元之间传递流的低级基础结构建模，而此级别的错误恰恰是我们在更高级别上谈论的失败，其由Akka流建模。

@@@

与用于数据元素转换的运算符相比，对Akka流中`onError`的处理仅提供了有限的支持，这是根据前面段落的精神而有意为之的。由于`onError`发出流崩溃的信号，它的排序语义与流完成不一样：任何类型的转换操作符都将跟随流崩溃，可能仍将元素保留在隐式或显式缓冲区中。这意味着，在失败之前发出的数据元素仍然可能丢失，如果`onError`赶上它们。

故障的传播能力要比数据元素快，这对于拆除背压的流至关重要，尤其是因为背压可能是故障模式(例如，通过开启上游缓冲区，然后由于它们无法执行其他操作而中止；或者发生死锁)。

### 流恢复的语义

恢复元素(也就是说，任何吸收`onError`信号并将其转换为可能更多的数据元素的转换都遵循正常的流完成)充当将流崩溃限制到流拓扑的给定区域的隔板。在崩溃区域内，缓冲的元素可能会丢失，但外部不受故障影响。

这与`try`–`catch`表达式的工作方式相同：它标记一个捕获异常的区域，但是在发生故障的情况下在该区域中跳过的确切代码量可能无法精确知道-语句的位置很重要。
