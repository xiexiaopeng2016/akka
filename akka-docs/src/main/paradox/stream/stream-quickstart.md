<a id="stream-quickstart"></a>
# Streams快速入门指南

## 依赖

要使用Akka流，请将模块添加到您的项目中：

@@dependency[sbt,Maven,Gradle] {
  group="com.typesafe.akka"
  artifact="akka-stream_$scala.binary_version$"
  version="$akka.version$"
}

@@@ note

Akka流的Java和Scala DSL都捆绑在同一JAR中。为了获得流畅的开发体验，在使用诸如Eclipse或IntelliJ IDE时，可以在使用Scala时禁止自动导入器建议的`javadsl`导入，反之亦然。请参阅 @ref:[IDE技巧](../additional/ide.md)。

@@@

## 第一步

流通常从一个源开始，因此这也是我们启动Akka流的方式。在创建一个流之前，我们先导入完整的流工具：

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #stream-imports }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #stream-imports }

如果您想在阅读快速入门指南的同时执行代码示例，则还需要进行以下导入：

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #other-imports }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #other-imports }

还有 @scala[一个对象]@java[一个类]来启动Akka `ActorSystem`并保存您的代码。设置`ActorSystem`为隐式使其可用于流，而无需在运行它们时手动传递它们：

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #main-app }

Java
:   @@snip [Main.java](/akka-docs/src/test/java/jdocs/stream/Main.java) { #main-app }

现在，我们将从一个非常简单的源开始，发出1到100的整数：

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #create-source }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #create-source }

用两个类型对`Source`类型进行参数化：第一个是该源发出的元素的类型，第二个是"实体化值"，允许运行该源以产生一些辅助值(例如，网络源可以提供有关绑定的端口或对等方的地址的信息)。如果没有产生辅助信息，则使用`akka.NotUsed`类型。一个简单的整数范围属于此类别 - 运行我们的流会生成一个`NotUsed`。

创建此源意味着我们已经对如何发出前100个自然数进行了描述，但是该源尚未激活。为了获得这些数字，我们必须运行它：

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #run-source }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #run-source }

这行代码将使用一个消费者函数对源进行补充 - 在本示例中我们将数字输出到控制台 - 并传递这个微小的流，设置到一个运行它的Actor。通过将"run"作为方法名称的一部分来表示激活。还有其他运行Akka流的方法，它们都遵循这种模式。

在`scala.App`中运行此源时，您可能会注意到它不会终止，因为`ActorSystem`永远不会终止。幸运的是，`runForeach`返回一个`Future[Done]`，其在流结束时解决：

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #run-source-and-terminate }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #run-source-and-terminate }

关于Akka流的好事是，`Source`是对您要运行的内容的描述，并且像建筑师的蓝图一样，可以重复使用，并整合到更大的设计中。我们可以选择转换整数的源并将其写入文件：

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #transform-source }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #transform-source }

首先，我们使用`scan`操作符对整个流进行计算：从数字1( @scala[`BigInt(1)`]@java[`BigInteger.ONE`])开始，我们将每个传入的数字相乘，一个接一个。scan操作将发出初始值，然后发出每个计算结果。这产生了一系列阶乘数，我们将其作为一个 `Source` 存储起来以备以后重用 - 要注意的是，实际上还没有计算任何东西，这是对运行流后要计算的内容的描述，这一点很重要。然后，我们将得到的一系列数字转换为`ByteString`对象流，这些对象描述文本文件中的行。然后，通过附加一个文件作为数据的接收者来运行这个流。在Akka流的术语中，这称为`Sink`。
`IOResult`是一种IO操作在Akka流中返回的类型，用于告诉您消费了多少字节或元素，以及流是正常终止还是异常终止。
BigInt(1)SourceByteStringSinkIOResult IO操作在Akka流中返回的一种类型，以告诉您消耗了多少字节或元素以及流是正常终止还是异常终止。

### 浏览器嵌入式示例

FIXME：fiddle要等到Akka 2.6发布并用[#27510](https://github.com/akka/akka/issues/27510)更新fiddle后才能工作
 
<a name="here-is-another-example-that-you-can-edit-and-run-in-the-browser-"></a>
这是您可以在浏览器中编辑和运行的另一个示例：

@@fiddle [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #fiddle_code template=Akka layout=v75 minheight=400px }


## 可重用的部分(Pieces)

Akka流的一些优点 - 其他流库没有提供的一些 - 是，不仅源可以像蓝图一样重用，所有其他元素也可以。
我们可以使用文件写入`Sink`，预先完成从传入字符串获取`ByteString`元素所需的处理步骤，并将其打包为可重用的部分。由于编写这些流的语言始终从左到右流动(就像普通的英语一样)，因此我们需要一个像源一样的起点，但使用“开放”的输入。在Akka流中，这称为Flow:

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #transform-sink }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #transform-sink }

从一个字符串流开始，我们将每个字符串转换为`ByteString`，然后供应给已知的文件写入`Sink`。产生的蓝图是一个 @scala[`Sink[String, Future[IOResult]]`]@java[`Sink<String, CompletionStage<IOResult>>`]，这意味着它接受字符串作为输入，并且在具体化时会创建 @scala[`Future[IOResult]`]@java[`CompletionStage<IOResult>`] 类型的辅助信息(当对一个`Source`或`Flow`的类型进行链接操作时，辅助信息的类型 - 称为"具体化值" - 由最左边的起点给出；由于我们想保留`FileIO.toPath`接收器提供的东西，我们需要说 @scala[`Keep.right`]@java[`Keep.right()`])。

我们可以使用我们刚刚创建的新而闪亮的`Sink`，把它附加到我们的`factorials`源上 - 经过一个小小的改编，把数字转换成字符串:

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #use-transformed-sink }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #use-transformed-sink }

## 基于时间的处理

在开始看一个更复杂的示例之前，我们先探讨Akka流可以做到的流性质。从`factorials`源开始，我们通过将它与另一个流压缩在一起来转换流，代表一个`Source`，其发射数字0到100：`factorials`源发出的第一个数字是零的阶乘，第二个是一的阶乘，以此类推。我们通过形成像`"3! = 6"`这样的字符串来组合这两个。

Scala
:   @@snip [QuickStartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/QuickStartDocSpec.scala) { #add-streams }

Java
:   @@snip [QuickStartDocTest.java](/akka-docs/src/test/java/jdocs/stream/QuickStartDocTest.java) { #add-streams }

到目前为止，所有操作都与时间无关，并且可以在严格的元素集合上以相同的方式执行。下一行表明，我们实际上是在处理以一定速度流动的流：我们使用`throttle`操作符将流的速度降至每秒1个元素。

如果你运行此程序，您将看到每秒打印一行。不过，有一个方面是不能马上看到的，然而值得一提：如果您尝试将流设置为每个产生十亿个数字，那么您将注意到JVM不会因为一个OutOfMemoryError而崩溃，即使您也注意到运行流会发生在后台，异步地(这是将来将辅助信息提供为 @scala[`Future`]@java[`CompletionStage`]的原因)。进行这项工作的秘诀是Akka流隐式地实现了普遍的流控制，所有的操作符都遵守背压。这使节流操作符可以向所有上游数据源发出信号，表明它只能以一定的速率接受元素 - 当输入速率高于每秒一秒时，节流操作符将断言*back-pressure*上游。

简而言之，这就是Akka流的全部 - 反映了一个事实，即有几十个源和接收器，还有更多的Stream转换运算符可供选择，另请参阅 @ref:[运算符索引](operators/index.md)。

# 反应式Tweets

流处理的典型用例是消费实时数据流，我们要从中提取或聚合其他数据。在这个示例中，我们将考虑使用一个tweet流，并从中提取有关Akka的信息。

我们还将考虑所有非阻塞流解决方案固有的问题：*"如果订阅者太慢而无法使用实时数据流怎么办?"*。传统的解决方案通常是对元素进行缓冲，但这可能(通常也会)导致最终的缓冲区溢出和此类系统的不稳定性。相反，Akka流依靠内部背压信号来控制在这种情况下应该发生什么。

在快速入门示例中，我们将使用以下数据模型：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #model }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #model }

@@@ note

如果您想先概览所用词汇，而不是先深入实际的示例，则可以查看文档的 @ref:[核心 概念](stream-flows-and-basics.md#core-concepts) 以及 @ref:[定义和运行streams](stream-flows-and-basics.md#defining-and-running-streams)部分，然后回到此快速入门看到所有这些拼凑成一个简单的示例应用程序。

@@@

## 转换和消费简单流

我们将要看的示例应用程序是一个简单的Twitter feed流，我们将希望从中提取某些信息，例如查找发`#akka`推文的用户的所有twitter句柄。

为了准备我们的环境，创建一个`ActorSystem`，它将负责运行我们将要创建的流:

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #system-setup }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #system-setup }

假设我们有一个即时可用的tweet流。在Akka中，这表示为一个 @scala[`Source[Out, M]`]@java[`Source<Out, M>`]：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #tweet-source }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #tweet-source }

流总是从一个 @scala[`Source[Out,M1]`]@java[`Source<Out,M1>`] 开始流动，然后可以继续通过 @scala[`Flow[In,Out,M2]`]@java[`Flow<In,Out,M2>`] 元素或更高级的操作符，最终可以被一个 @scala[`Sink[In,M3]`]@java[`Sink<In,M3>`]消耗 @scala[(忽略类型参数`M1`，`M2`和`M3`，目前而言，它们与这些类产生/消耗的元素的类型无关 - 它们是"实体化类型"，我们将在 @ref:[下面](#materialized-values-quick) 讨论它)]

对于使用过Scala集合库的人来说，这些操作应该很熟悉，但是它们是在流上操作的，而不是数据集合(这是非常重要的区别，因为某些操作仅在流中有意义，反之亦然)：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #authors-filter-map }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #authors-filter-map }

最后，为了 @ref:[具体化](stream-flows-and-basics.md#stream-materialization) 和运行流计算，我们需要将Flow附加到一个 @scala[`Sink`]@java[`Sink<T, M>`]，那将使Flow运行。要做到这一点最简单的方法是在 @scala[`Source`]@java[`Source<Out, M>`]上调用 `runWith(sink)`。为了方便，预定义了一些常见的接收器，并将其聚集为`Sink`伴生对象上的方法。现在，让我们打印每个作者：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #authors-foreachsink-println }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #authors-foreachsink-println }

或使用简写版本(那些仅针对最流行的接收器例如`Sink.fold`和`Sink.foreach`定义)：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #authors-foreach-println }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #authors-foreach-println }

物化(Materializing)和运行流总是需要一个`Materializer`在隐式作用域(或明确的传递，像这样：`.run(materializer)`)。

完整的代码段如下所示：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #first-sample }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #first-sample }

## 在流中扁平序列

在上一节中，我们工作在元素的1:1关系，这是最常见的情况，但是有时我们可能想要从一个元素映射到多个元素并接收"扁平化"的流，就像`flatMap`工作在Scala集合上一样。为了从我们的tweets流中获得一个话题标签的扁平化流，我们可以使用`mapConcat`操作符:

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #hashtags-mapConcat }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #hashtags-mapConcat }

@@@ note

`flatMap`这个名字是特意地避免的，因为它接近for-推导和一元组成。这是有问题的，原因有两个：首先，在有界流处理中，由于存在死锁的风险，通常不希望使用串联进行展平(在合并时是首选策略)；其次，monad法则不适用于我们的`flatMap`的实现(由于存活性(liveness)问题)。

请注意，`mapConcat` 要求提供的函数必须返回 @scala[一个可迭代的 (`f: Out => immutable.Iterable[T]`]@java[a strict collection (`Out f -> java.util.List<T>`)]，而`flatMap`则需要在数据流由始至终的操作。

@@@

## 广播一个流

现在让我们说，我们要保存该实时流中的所有主题标签以及所有作者姓名。例如，我们想将所有作者句柄写入磁盘上的一个文件，并将所有主题标签写入另一个文件。这意味着我们必须将源流分成两个流，以处理对这些不同文件的写入。

可用于形成此类"扇出"(或"扇入")结构的元素在Akka流中称为"交叉点(junctions)"。在此示例中，我们将使用的其中一个称为`Broadcast`，它将元素从其输入端口发射到其所有输出端口。

Akka流有意将线性流结构(Flows)与非线性分支结构(Graphs)分开，以便为这两种情况提供最方便的API。图可以表达任意复杂的流设置，但代价是阅读起来不像集合转换那样熟悉。

使用`GraphDSL`构造图，如下所示：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #graph-dsl-broadcast }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #graph-dsl-broadcast }

如您所见，在`GraphDSL`内部，我们使用隐式图构建器`b`使用`~>`"edge操作符"(也读成"connect"或"via"或"to")来不定地(mutably)构建图。运算符通过导入`GraphDSL.Implicits._`隐式提供。

`GraphDSL.create`返回一个`Graph`，在此示例中，一个 @scala[`Graph[ClosedShape, NotUsed]`]@java[`Graph<ClosedShape,NotUsed>`]。这里`ClosedShape`表示它是*一个完全连接的图形*或"闭合"-没有未连接的输入或输出。由于它是闭合的，因此可以使用`RunnableGraph.fromGraph`将图形转换为一个`RunnableGraph`。`RunnableGraph`然后可被`run()`，在它之外具体化一个流。。

`Graph`和`RunnableGraph`两者都是*不可变的，线程安全的，并自由地共享的*。

一个图还可以具有其他几种形状之一，并具有一个或多个未连接的端口。具有未连接的端口表示一个图是*部分图*。有关大型结构中的组合图和嵌套图的概念，将在 @ref:[模块化，组合和层次结构](stream-composition.md)中进行详细说明。还可以将复杂的计算图包装为Flows，Sinks或Sources，这将在 @scala[@ref:[从部分图构造Sources, Sinks和Flows](stream-graphs.md#constructing-sources-sinks-flows-from-partial-graphs)]中进行详细说明。

## 背压实战

Akka流的主要优点之一是，它们*始终*将背压信息从流Sinks(订阅者)传播到其Sources(发布者)。它不是一项可选功能，并且始终处于启用状态。要了解有关Akka流和所有其他Reactive Streams兼容实现所使用的背压协议的更多信息，请阅读 @ref:[Back-pressure解释](stream-flows-and-basics.md#back-pressure-explained)。

像这样的典型问题(不使用Akka流)应用程序经常面临的问题是，它们不能足够快地处理传入数据，无论是临时的还是有意的，并且将开始缓冲传入数据，直到没有更多空间可缓冲为止，导致`OutOfMemoryError`或其他服务响应能力严重下降。使用Akka流可以而且必须明确地使用缓冲。例如，如果我们仅对"*最近的推文，具有10个元素的缓冲区*"感兴趣，这可以用`buffer`元素来表示：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #tweets-slow-consumption-dropHead }

Java
:  @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #tweets-slow-consumption-dropHead }

该`buffer`元素具有一个显式且必需的`OverflowStrategy`，它定义了缓冲区在已满的时候接收另一个元素时的反应。提供的策略包括删除最旧的元素(`dropHead`)，删除整个缓冲区，发出 @scala[错误]@java[失败]信号等。请务必选择最适合您的用例的策略。

<a id="materialized-values-quick"></a>
## 物化值

到目前为止，我们仅使用Flow处理数据，并将其消耗到某种外部Sink中 - 通过打印值或将其存储在某些外部系统中来实现它。然而，有时我们可能会对从物化处理管道中获得的一些值感兴趣。例如，我们想知道我们处理了多少条推文。尽管在无限的tweets流的情况下，这个问题不太容易回答(在一个流设置中回答此问题的一种方法是创建一个计数流，描述为"*到目前为止*，我们已经处理了N条推文")。但是一般来说，处理有限的流并得到一个不错的结果是可能的，比如元素的总数。

首先，让我们使用 @scala[`Sink.fold`]@java[`Flow.of(Class)`和`Sink.fold`]来编写这样的元素计数器，并看看类型是什么样的：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #tweets-fold-count }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #tweets-fold-count }

首先，我们准备一个可重用的对象`Flow`，它将每个传入的tweet变成值为`1`的整数。我们将用这个以便把它们和一个`Sink.fold`结合起来，它将合计流的所有`Int`元素，并使其结果作为一个`Future[Int]`可用。接下来，我们使用`via`将`tweets`流连接到`count`。最后，我们使用`toMat`将Flow连接到先前已准备的接收器。

还记得 @scala[`Source[+Out, +Mat]`, `Flow[-In, +Out, +Mat]`和`Sink[-In, +Mat]`]@java[`Source<Out, Mat>`, `Flow<In, Out, Mat>`和`Sink<In, Mat>`]上的那些神秘的`Mat`类型参数吗？它们表示这些处理部件在物化时返回的值的类型。当你将它们链接在一起时，可以显式组合其物化值。在我们的示例中，我们使用了预定义的 @scala[`Keep.right`]@java[`Keep.right()`]函数，这告诉实现只关心当前附加到右边的操作符的物化类型。`sumSink`的物化类型为 @scala[`Future[Int]`]@java[`CompletionStage<Integer>`]，由于使用 @scala[`Keep.right`]@java[`Keep.right()`]，结果`RunnableGraph`也具有一个 @scala[`Future[Int]`]@java[`CompletionStage<Integer>`]类型参数。

这个步骤还*没有*物化处理管道，它只是准备流的描述，现在连接到一个接收器，并且因此可以被`run()`，由它的类型所指示的：@scala[`RunnableGraph[Future[Int]]`]@java[`RunnableGraph<CompletionStage<Integer>>`]。接下来我们调用`run()`，它使用 @scala[隐式] `Materializer` 来物化和运行Flow。通过在 @scala[`RunnableGraph[T]`]@java[`RunnableGraph<T>`]上调用`run()`返回的值是`T`类型。在我们的情况下，此类型是@scala[`Future[Int]`]@java[`CompletionStage<Integer>`]，在完成后，它将包含`tweets`流的总长度。如果流失败，则这个future将以失败完成。

一个`RunnableGraph`可以多次重用和物化，因为它只是流的"蓝图"。这意味着，如果我们物化一个流，例如，在一分钟内消耗了一条实时推文流，那么这两个物化的物化值将有所不同，如以下示例所示：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #tweets-runnable-flow-materialized-twice }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #tweets-runnable-flow-materialized-twice }

Akka流中的许多元素提供了物化值，可用于获取计算结果或操纵这些元素，这将在 @ref:[流物化](stream-flows-and-basics.md#stream-materialization)中进行详细讨论。总结这一节，现在我们知道运行此单行代码时幕后发生了什么，这等效于上面的多行版本：

Scala
:   @@snip [TwitterStreamQuickstartDocSpec.scala](/akka-docs/src/test/scala/docs/stream/TwitterStreamQuickstartDocSpec.scala) { #tweets-fold-count-oneline }

Java
:   @@snip [TwitterStreamQuickstartDocTest.java](/akka-docs/src/test/java/jdocs/stream/TwitterStreamQuickstartDocTest.java) { #tweets-fold-count-oneline }

@@@ note

`runWith()`是一种便捷的方法，它会自动忽略除`runWith()`自身附加的运算符之外的任何其他运算符的物化值。
在上面的例子中，它转换为使用 @scala[`Keep.right`]@java[`Keep.right()`] 作为物化值的组合器。
在上面的示例中，它转换为用作实现值的组合器。

@@@
