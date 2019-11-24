# 使用图(Graph)

## 依赖

要使用Akka Streams，请将模块添加到您的项目中：

@@dependency[sbt,Maven,Gradle] {
  group="com.typesafe.akka"
  artifact="akka-stream_$scala.binary_version$"
  version="$akka.version$"
}

## 介绍

在Akka流中，计算图不是像线性计算那样使用流畅的DSL来表达的，相反，它们是用一种更类似于图形的DSL编写的，目的是使翻译图形图纸(例如，来自设计讨论的注释或协议规范中的插图)到(从)代码更简单。在本节中，我们将深入探讨构建和重用图的多种方法，并说明常见的陷阱和如何避开它们。

当您希望执行任何类型的扇入("多个输入")或扇出("多个输出")操作时，都需要使用图。考虑到线性Flow就像道路，我们可以把图操作描绘成交叉点：多个flow在单个点处连接。一些很常见且适合于线性形式的Flows的运算符，例如`concat`(它将两个流连接起来，以便在第一个流完成后消耗第二个流)，可能`Flow`或`Source`它们本身已定义了速记方法，但是您应保留请记住，它们也被实现为图交叉点。

<a id="constructing-graphs"></a>
## 构建图

图是由简单的Flow构建的，这些简单的流充当图中的线性连接，和交叉点充当Flow的扇入和扇出点一样。得益于交叉点具有基于其行为的有意义的类型，并使它们成为显式元素，所以这些元素的使用非常容易。

Akka流当前提供这些交叉点(有关详细列表，请参见@ref[运算符索引](operators/index.md))：

 * **扇出**

    * @scala[`Broadcast[T]`]@java[`Broadcast<T>`] – *(1个输入，N个输出)* 给定一个输入元素，发射到每个输出端口
    * @scala[`Balance[T]`]@java[`Balance<T>`] – *(1个输入，N个输出)* 给定一个输入元素发射到其一个输出端口
    * @scala[`UnzipWith[In,A,B,...]`]@java[`UnzipWith<In,A,B,...>`] – *(1个输入，N个输出)*在1个输入获得一个函数，该函数给与每个输入一个值，发射N个输出元素(其中N <= 20)
    * @scala[`UnZip[A,B]`]@java[`UnZip<A,B>`] – *(1个输入，2个输出)* 将一个元组流 @scala[`(A,B)`]@java[`Pair<A,B>`]拆分成两个流，一个`A`类型，另一个`B`类型

 * **扇入**

    * @scala[`Merge[In]`]@java[`Merge<In>`] – *(N个输入，1个输出)* 从输入中随机选择，将它们逐个推到它的输出
    * @scala[`MergePreferred[In]`]@java[`MergePreferred<In>`] – 类似`Merge`，但如果`preferred`端口上有可用元素，则从中选择，否则从`others`中随机选择
    * @scala[`MergePrioritized[In]`]@java[`MergePrioritized<In>`] – 类似`Merge`，但如果元素在所有输入端口上可用，则会根据其`priority`从它们中随机选择
    * @scala[`MergeLatest[In]`]@java[`MergeLatest<In>`] – *(N个输入，1个输出)* 发射`List[In]`，当第i个输入流发射元素时，则已发射列表中的第i个元素是更新过的
    * @scala[`ZipWith[A,B,...,Out]`]@java[`ZipWith<A,B,...,Out>`] – *(N个输入，1个输出)* which takes a function of N inputs that given a value for each input emits 1 output element
    * @scala[`Zip[A,B]`]@java[`Zip<A,B>`] – *(2个输入，1个输出)* 是一个`ZipWith`专门用于将`A`和`B`的输入流压缩到一个元组流 @scala[`(A,B)`]@java[`Pair(A,B)`]
    * @scala[`Concat[A]`]@java[`Concat<A>`] – *(2个输入，1个输出)* 连接两个流(首先消耗一个，然后消耗第二个)

GraphDSL DSL的目标之一是使其看起来就像在白板上画图形一样，从而可以轻松地将设计从白板转换为代码，并能将两者联系起来。让我们通过将下面的手绘图转换为Akka流来说明这一点：

![simple-graph-example.png](../images/simple-graph-example.png)

这种图很容易转换为图DSL，因为每个线性元素都对应到一个`Flow`，并且每个圆都要么对应到一个`Junction`，要么对应到一个`Source`或`Sink`，假如它是在一个`Flow`的开头或结尾。@scala[交叉点必须始终使用定义的类型参数创建，否则将推断成`Nothing`类型。]

Scala
:   @@snip [GraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphDSLDocSpec.scala) { #simple-graph-dsl }

Java
:   @@snip [GraphDSLTest.java](/akka-stream-tests/src/test/java/akka/stream/javadsl/GraphDslTest.java) { #simple-graph-dsl }

@@@ note

交叉点*引用相等性*定义*图节点相等性*(例如，在一个GraphDSL中使用的同一个merge *实例*指的是结果图中相同的位置)。

@@@

@scala[请注意`import GraphDSL.Implicits._`，它将`~>`运算符(读作"edge"，"via"或"to")及其反向的对应项`<~`(for noting down flows in the opposite direction where appropriate)纳入作用域内。]

通过查看上面的代码片段，很明显 @scala[`GraphDSL.Builder`]@java[`builder`]对象是*可变的*。@scala[它由`~>`运算符(隐式)使用，也使它成为一个可变的操作。]选择这种设计的原因是为了能够更简单地创建复杂的图，这些图甚至可能包含循环。一旦GraphDSL构建完成，@scala[`GraphDSL`]@java[`RunnableGraph`]实例将*是不可变的，线程安全的和可自由共享的*。所有操作符 —sources，sinks和flows— 在构建之后都是如此。这意味着您可以在处理图中的多个位置安全地重用一个给定的Flow或交叉点。

我们已经在上面看到了这种重用的例子：合并和广播交叉点是使用`builder.add(...)`导入到图中的，一个运算将传递给它的蓝图复制一份，并返回结果副本的入口和出口，因此他们可以连接起来。另一种选择是传递现有的图 - 任何形状的 - 到生成新图形的工厂方法中。这两种方法之间的区别在于，使用`builder.add(...)`导入会忽略已导入图的物化值，而通过工厂方法导入则允许包含它; 有关更多详细信息，请参见 @ref[流物化](stream-flows-and-basics.md#stream-materialization)。

在下面的示例中，我们准备了一个由两个并行流组成的图，在它里面我们重用了相同`Flow`实例，然而，它将被适当地物化为相应的Source和Sink之间的两个连接：

Scala
:   @@snip [GraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphDSLDocSpec.scala) { #graph-dsl-reusing-a-flow }

Java
:   @@snip [GraphDSLTest.java](/akka-stream-tests/src/test/java/akka/stream/javadsl/GraphDslTest.java) { #graph-dsl-reusing-a-flow }

In some cases we may have a list of graph elements, for example if they are dynamically created. 
If these graphs have similar signatures, we can construct a graph collecting all their materialized values as a collection:

Scala
:   @@snip [GraphOpsIntegrationSpec.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/GraphOpsIntegrationSpec.scala) { #graph-from-list }

Java
:   @@snip [GraphDSLTest.java](/akka-stream-tests/src/test/java/akka/stream/javadsl/GraphDslTest.java) { #graph-from-list }


<a id="partial-graph-dsl"></a>
## 构造和组合部分图

有时不可能(或需要)在一个地方构造整个计算图，而是在不同地方构造它的所有不同阶段，最后将它们全部连接成一个完整的图并运行它。

这可以通过以下方式实现，@scala[从给`GraphDSL.create`的函数中返回一个不同于`ClosedShape`的`Shape`，例如`FlowShape(in, out)`。有关此类预定义形状的列表，请参见 @ref:[预定义形状](#predefined-shapes)。将一个`Graph`变成`RunnableGraph`]@java[using the returned `Graph` from `GraphDSL.create()` rather than passing it to `RunnableGraph.fromGraph()` to wrap it in a `RunnableGraph`.The reason of representing it as a different type is that a `RunnableGraph`]需要连接所有端口，如果没有连接，则会在构建时抛出异常，这有助于避免在使用图时出现简单的连接错误。然而，局部图允许您从执行内部连接的代码块返回尚未连接的端口集。

让我们想象一下，我们想为用户提供一个特殊的元素，给定3个输入，为每个压缩后的三元组选择最大的int值。我们将要公开3个输入端口(未连接的源)和1个输出端口(未连接的接收器)。

Scala
:   @@snip [StreamPartialGraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/StreamPartialGraphDSLDocSpec.scala) { #simple-partial-graph-dsl }  

Java
:   @@snip [StreamPartialGraphDSLDocTest.java](/akka-docs/src/test/java/jdocs/stream/StreamPartialGraphDSLDocTest.java) { #simple-partial-graph-dsl }

@@@ note

虽然上面的例子展示了如何组合两个2个输入的`ZipWith`，但实际上ZipWith已经提供了大量的重载，包括一个3个(以及更多)参数版本。因此，这可以使用一个使用3参数版本的ZipWith来实现，像这样：@scala[`ZipWith((a, b, c) => out)`]@java[`ZipWith.create((a, b, c) -> out)`]。(带有N输入的ZipWith具有N + 1类型参数；最后一个类型参数是输出类型。)

@@@

正如您所看到的，首先我们构造一个局部图，它 @scala[包含流元素的所有压缩和比较。这个局部图将有三个输入和一个输出，因此我们使用`UniformFanInShape`]。然后，我们将它(它的所有节点和连接)显式地导入第二步构建的封闭图，在那里所有未定义的元素都被重新连接到实际的源和接收器。然后可以运行该图并产生预期的结果。

@@@ warning

请注意，`GraphDSL`无法提供编译时类型 - 关于是否所有元件都已正确连接的安全性 - 此验证在图的实例化期间作为运行时的检查执行的。

一个局部图还可以验证所有端口是否已连接或已返回的`Shape`的一部分。

@@@

<a id="constructing-sources-sinks-flows-from-partial-graphs"></a>
## 从局部图构造源，汇和Flow

与其将 @scala[局部图]@java[`Graph`]视为可能尚未全部连接的flow和交叉点的集合，不如将这样一个复杂的图公开为一个更简单的结构，如一个`Source`，`Sink`或`Flow`，这样做有时很有用。

实际上，这些概念可以表示为局部连接图的特殊情况：

Source是仅具有一个输出的部分图形，即返回a SourceShape。
Sink是仅具有一个输入的部分图形，即返回一个SinkShape。
Flow是具有正好一个输入和正好一个输出的部分图形，即返回a FlowShape。

 * `Source` 是一个*仅有一个*输出的局部图，它会返回一个`SourceShape`。
 * `Sink` 是一个*仅有一个*输入的局部图，它会返回一个`SinkShape`。
 * `Flow` 是一个*仅有一个*输入和*仅有一个*输出的局部图，它会返回一个`FlowShape`。

能够在诸如Sink/Source/Flow之类的简单元素中隐藏复杂图，使您能够创建一个复杂元素，在此基础上，将其视为用于线性计算的简单复合运算符。

为了从图创建Source要使用`Source.fromGraph`方法，要使用它，我们必须有一个`Graph[SourceShape, T]`。

这是使用`GraphDSL.create`构造的，并从传入的函数返回一个`SourceShape`。必须为`SourceShape.of`方法提供一个出口，并将成为"在这个源运行之前必须附加的接收器"。

参考下面的例子，在这个例子中，我们创建了一个将两个数字压缩在一起的源，以在实战中查看这个图的结构:

Scala
:   @@snip [StreamPartialGraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/StreamPartialGraphDSLDocSpec.scala) { #source-from-partial-graph-dsl }    

Java
:   @@snip [StreamPartialGraphDSLDocTest.java](/akka-docs/src/test/java/jdocs/stream/StreamPartialGraphDSLDocTest.java) { #source-from-partial-graph-dsl }

类似地，使用`SinkShape.of`可以对一个`Sink[T]`执行相同的操作。在这种情况下，所提供的值必须是一个`Inlet[T]`。为了定义一个`Flow[T]`，我们需要同时暴露一个入口和一个出口：

Scala
:   @@snip [StreamPartialGraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/StreamPartialGraphDSLDocSpec.scala) { #flow-from-partial-graph-dsl }    

Java
:   @@snip [StreamPartialGraphDSLDocTest.java](/akka-docs/src/test/java/jdocs/stream/StreamPartialGraphDSLDocTest.java) { #flow-from-partial-graph-dsl }


## 将源和接收器与简化的API组合

有一个简化的API，可以用来将源和汇与接口交叉点组合起来，比如：`Broadcast[T]`，`Balance[T]`，`Merge[In]`和`Concat[A]`，不需要使用图DSL。`combine`方法负责在幕后构造必要的图。在以下示例中，我们将两个来源组合成一个(扇入)：

Scala
:   @@snip [StreamPartialGraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/StreamPartialGraphDSLDocSpec.scala) { #source-combine }   

Java
:   @@snip [StreamPartialGraphDSLDocTest.java](/akka-docs/src/test/java/jdocs/stream/StreamPartialGraphDSLDocTest.java) { #source-combine }

可以对一个`Sink[T]`进行相同的操作，但是在这种情况下，它会是扇出：

Scala
:   @@snip [StreamPartialGraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/StreamPartialGraphDSLDocSpec.scala) { #sink-combine }  

Java
:   @@snip [StreamPartialGraphDSLDocTest.java](/akka-docs/src/test/java/jdocs/stream/StreamPartialGraphDSLDocTest.java) { #sink-combine }


## 构建可重用的Graph组件

可以使用图DSL构建可重用的、任意输入和输出端口的封装组件。

作为一个例子，我们会建立一个图交叉点代表一群工人，在那里一个`Flow[I,O,_]`表示一个工人，一个简单的转换的作业，即从类型`I`到结果类型`O`(如您所见，这个flow内部实际上可以包含一个内复杂的图)。我们可重用的工人池交叉点将不会保留传入作业的顺序(假定它们具有合适的ID字段)，它将使用一个`Balance`交叉点将作业调度给可用的工人。除此以外，我们的交叉点将扮演一个"快速通道"，一个专用端口，在那里可以发送更高优先级的作业。

总而言之，我们的交叉点将具有两个`I`类型的输入端口(分别用于普通和优先作业)和一个`O`类型的输出端口。要表示此接口，我们需要定义一个自定义`Shape`。以下几行显示了如何执行此操作

Scala
:   @@snip [GraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphDSLDocSpec.scala) { #graph-dsl-components-shape }  

<a id="predefined-shapes"></a>
## 预定义形状(Shape)

通常，一个自定义`Shape`需要能够提供它的所有输入和输出端口，能够复制自身，并且还能够从给定的端口创建新实例。这里提供了一些预定义的形状，以避免不必要的样板文件：

 * `SourceShape`，`SinkShape`，`FlowShape` 很简单的形状，
 * `UniformFanInShape`和`UniformFanOutShape` 用于具有多个相同类型的输入(或输出)端口的交叉点，
 * `FanInShape1`，`FanInShape2`， ...，`FanOutShape1`，`FanOutShape2`，... 用于具有多个不同类型的输入(或输出)端口的交叉点。

由于我们的形状有两个输入端口和一个输出端口，我们可以使用`FanInShape`DSL来定义我们的自定义形状：

Scala
:  @@snip [GraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphDSLDocSpec.scala) { #graph-dsl-components-shape2 }  

现在我们有了一个`Shape`，我们可以连接一个代表我们的工人池的图。首先，我们将使用`MergePreferred`合并传入的普通和优先作业，然后我们将作业发送到一个`Balance`交叉点，该交叉点将扇出到一个可配置数量的工人(flows)，最后我们将所有结果合并在一起，然后通过我们唯一的输出端口发送出去。这由以下代码表示：

Scala
:   @@snip [GraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphDSLDocSpec.scala) { #graph-dsl-components-create }  

我们现在要做的就是在图中使用我们的自定义交叉点。以下代码使用纯字符串模拟一些简单的工人和作业，并打印出结果。实际上，我们使用了*2*个工人池交叉点的实例，使用`add()`两次。

Scala
:   @@snip [GraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphDSLDocSpec.scala) { #graph-dsl-components-use }

<a id="bidi-flow"></a>
## 双向Flows

通常一个有用的图拓扑是向相反方向的两个flows。以编解码器运算符为例，它序列化传出消息，并反序列化传入的八字节流。另一个这样的运算符可以添加一个帧协议，它将长度头附加到传出数据，并将传入的帧解析回原始的八字节流块。这两个运算符要被组合在一起，作为协议栈的一部分，一个运算符应用于另一个运算符之上。为此，存在一种特殊的类型`BidiFlow`，它是一种图形，它恰好拥有两个开放的入口和两个开放的出口。相应的形状被称为`BidiShape`，并定义如下：

@@snip [Shape.scala](/akka-stream/src/main/scala/akka/stream/Shape.scala) { #bidi-shape }   

一个双向flow的定义就像单向`Flow`一样，上述编解码器的演示：

Scala
:   @@snip [BidiFlowDocSpec.scala](/akka-docs/src/test/scala/docs/stream/BidiFlowDocSpec.scala) { #codec }

Java
:   @@snip [BidiFlowDocTest.java](/akka-docs/src/test/java/jdocs/stream/BidiFlowDocTest.java) { #codec }

第一个版本类似于局部图构造器，而对于一个1:1转换功能的简单情况，有一种简洁方便的方法，如最后一行所示。这两个函数的实现也不难：

Scala
:   @@snip [BidiFlowDocSpec.scala](/akka-docs/src/test/scala/docs/stream/BidiFlowDocSpec.scala) { #codec-impl }  

Java
:   @@snip [BidiFlowDocTest.java](/akka-docs/src/test/java/jdocs/stream/BidiFlowDocTest.java) { #codec-impl }

通过这种方式，您可以集成任何其他序列化库，它将一个对象转换为一个字节序列。

我们讨论的另一个运算符要稍微复杂一些，因为反转成帧协议意味着任何接收到的字节块都可能对应到零或多个消息。最好使用 @ref[`GraphStage`](stream-customize.md)来实现(请参见 @ref[使用GraphStage进行自定义处理](stream-customize.md#graphstage))。

Scala
:   @@snip [BidiFlowDocSpec.scala](/akka-docs/src/test/scala/docs/stream/BidiFlowDocSpec.scala) { #framing }  

Java
:   @@snip [BidiFlowDocTest.java](/akka-docs/src/test/java/jdocs/stream/BidiFlowDocTest.java) { #framing }

通过这些实现，我们可以构建协议栈并对其进行测试：

Scala
:   @@snip [BidiFlowDocSpec.scala](/akka-docs/src/test/scala/docs/stream/BidiFlowDocSpec.scala) { #compose }  

Java
:   @@snip [BidiFlowDocTest.java](/akka-docs/src/test/java/jdocs/stream/BidiFlowDocTest.java) { #compose }

此示例演示了多少`BidiFlow`子图可以被连接在一起，而且还要使用`.reversed`方法将其翻转。该测试模拟了网络通信协议的双方，而无需实际打开网络连接 - flows可被直接连接。

<a id="graph-matvalue"></a>
## 访问图内部的物化值

在某些情况下，可能需要反馈图的物化值(局部的，闭合或返回一个Source，Sink，Flow或BidiFlow)。这可以通过使用`builder.materializedValue`做到，它提供一个`Outlet`，可以在图形中用作普通源或出口，并最终发出物化值。如果在一个以上的地方需要物化价值，则可以调用`materializedValue`任意次以获得必要数量的出口。

Scala
:   @@snip [GraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphDSLDocSpec.scala) { #graph-dsl-matvalue }

Java
:   @@snip [GraphDSLTest.java](/akka-stream-tests/src/test/java/akka/stream/javadsl/GraphDslTest.java) { #graph-dsl-matvalue }

注意不要引入一个循环，在循环中，物化值实际上会贡献给物化值。以下示例演示了一个折叠(fold)的物化的`Future`被反馈到折痕折叠的案例。

Scala
:  @@snip [GraphDSLDocSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphDSLDocSpec.scala) { #graph-dsl-matvalue-cycle }

Java
:  @@snip [GraphDSLTest.java](/akka-stream-tests/src/test/java/akka/stream/javadsl/GraphDslTest.java) { #graph-dsl-matvalue-cycle }

<a id="graph-cycles"></a>
## 图循环，存活性和死锁

有界流拓扑中的循环需要特殊的考虑，以避免潜在的死锁和其他存活性问题。本节展示了几个示例，关于在流处理图中存在反馈弧时可能出现的问题。

在以下示例中，可运行图已经创建好了，但是不能运行，因为每个图都有一些问题而且启动后会死锁。`Source`变量没有定义，因为元素的性质和数量与所描述的问题无关。

第一个示例演示了一个包含简单循环的图。该图从源中获取元素，打印它们，然后将这些元素广播给消费者(目前我们只是使用`Sink.ignore`)和给一个反馈弧，它通过`Merge`交叉点合并回到主流中。

@@@ note

图DSL允许反转连接箭头，这在编写循环时特别方便 - 我们将看到，在一些情况下，这是非常有用的。

@@@

Scala
:   @@snip [GraphCyclesSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphCyclesSpec.scala) { #deadlocked }  

Java
:   @@snip [GraphCyclesDocTest.java](/akka-docs/src/test/java/jdocs/stream/GraphCyclesDocTest.java) { #deadlocked }

运行这段代码，我们观察到，在打印了一些数字之后，不再有任何元素输出日志到控制台 - 一段时间后所有处理停止。经过一些调查，我们发现：

 * 通过从`source`合并，我们增加了循环中流动的元素数量
 * 由广播回到循环，我们没有减少循环中的元素数量

由于Akka流(和通常的响应流)保证有界处理(请参阅"缓冲"部分以了解更多详细信息)，这意味着在任何时间范围内只有有限数量的元素被缓冲。由于我们的循环获得越来越多的元素，最终它的所有内部缓冲区都变满了，一直背压`source`。为了能够处理来自`source`的更多元素，元素需要以某种方式离开循环。

如果我们修改我们的反馈循环，将`Merge`交叉点替换为一个`MergePreferred`，就可以避免死锁。`MergePreferred`是不公平的，由于它总是试图先从一个优先的输入端口消费，如果那里有元素可用，然后再尝试其他较低优先级的输入端口。由于我们通过优先的端口进行反馈，因此始终保证循环中的元素可以流动。

Scala
:   @@snip [GraphCyclesSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphCyclesSpec.scala) { #unfair }

Java
:   @@snip [GraphCyclesDocTest.java](/akka-docs/src/test/java/jdocs/stream/GraphCyclesDocTest.java) { #unfair }

如果我们运行这个例子，我们会看到相同的数字序列被一次又一次地打印出来，但是这个处理不会停止。从此，我们避免了死锁，但`source`仍然一直背压，因为缓冲区空间从未恢复：我们看到的唯一动作是来自`source`的几个初始元素的循环。

@@@ note

我们在这里看到的是，在某些情况下，我们需要在有界与活跃度之间做出选择。如果循环中有无穷大的缓冲区，我们的第一个示例将不会死锁，反之亦然，如果循环中的元素是平衡的(移除的元素数量与注入的元素数量相同)，也不会出现死锁。

@@@

为了使我们的循环既活跃(不死锁)又公平，我们可以在反馈弧上引入一个丢弃(dropping)元素。在这种情况下，我们选择`buffer()`运算，给它一个丢弃策略`OverflowStrategy.dropHead`。

Scala
:   @@snip [GraphCyclesSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphCyclesSpec.scala) { #dropping }

Java
:   @@snip [GraphCyclesDocTest.java](/akka-docs/src/test/java/jdocs/stream/GraphCyclesDocTest.java) { #dropping }

如果我们运行这个示例，我们将会看到

 * 元素的流动不会停止，总是在打印元素
 * 我们看到一些数字随着时间的推移被打印了几次(由于反馈循环)，但平均起来，这个数字还在增长，从长期来看

这个例子强调了一个解决方案，它可以避免当潜在的不平衡循环(循环的元素的数量是无限的)存在时出现的死锁，这个方案是丢弃元素。一种替代方法是使用`OverflowStrategy.fail`定义一个较大的缓冲区，它将使流失败，而不是在消耗完所有缓冲区空间后将其死锁。

正如我们在前面的例子中发现的那样，核心问题是反馈循环的不平衡性质。我们通过添加一个丢弃元素来避开这个问题，但现在我们想建立一个从一开始就平衡的循环。为了实现这一点，我们通过用一个`ZipWith`替换`Merge`交叉点来修改我们的第一个图。由于`ZipWith`从`source`取走一个元素，*并*从反馈弧注入一个元素到循环中，我们保持了元素的平衡。

Scala
:   @@snip [GraphCyclesSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphCyclesSpec.scala) { #zipping-dead }    

Java
:   @@snip [GraphCyclesDocTest.java](/akka-docs/src/test/java/jdocs/stream/GraphCyclesDocTest.java) { #zipping-dead }

尽管如此，当我们尝试运行这个示例时，却发现根本没有打印任何元素! 经过一些调查，我们意识到:

 * 为了使第一个元素从`source`进入循环，我们需要一个循环中已经存在的元素
 * 为了获得循环中的初始元素，我们需要一个来自`source`的元素

这两个条件是一个典型的"鸡与蛋"的问题。解决方案是将一个初始元素注入到独立于`source`的循环中。为此，我们在向逆向(backwards)弧上使用一个`Concat`交叉点，它使用`Source.single`注入单个元素。

Scala
:   @@snip [GraphCyclesSpec.scala](/akka-docs/src/test/scala/docs/stream/GraphCyclesSpec.scala) { #zipping-live }    

Java
:   @@snip [GraphCyclesDocTest.java](/akka-docs/src/test/java/jdocs/stream/GraphCyclesDocTest.java) { #zipping-live }

当我们运行上面的例子时，我们看到处理开始并且不会停止。从这个例子中得出的要点是，平衡的循环通常需要一个初始的"开球"元素注入到循环中。
