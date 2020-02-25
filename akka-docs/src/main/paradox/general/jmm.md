---
project.description: Akka, Actors, Futures and the Java Memory Model.
---
<a id="akka-and-the-java-memory-model"></a>
# Akka和Java内存模型

使用Lightbend平台(包括Scala和Akka)的主要好处是，它简化了编写并发软件的过程。本文讨论了Lightbend平台(尤其是Akka)如何在并发应用程序中处理共享内存。

<a id="the-java-memory-model"></a>
## Java内存模型

在Java 5之前，Java内存模型(JMM)定义不正确。当多个线程访问共享内存时，可能会得到各种奇怪的结果，例如：

 * 一个线程看不到其他线程写入的值：可见性问题
 * 线程观察到其他线程的'不可能'行为，这是由于未按预期顺序执行指令导致的：指令重新排序问题。

随着Java 5中JSR 133的实现，许多这些问题已得到解决。JMM是基于"发生-之前"关系的一组规则，该规则约束一个内存访问必须在另一个内存访问之前发生，反之，它们被允许不按顺序发生。这些规则的两个示例是：

 * **监控器锁定规则:** 释放锁发生在随后的每次获取同一锁定之前。
 * **volatile变量规则:** volatile变量的写操作发生在同一volatile变量的每次读取之前

尽管JMM看起来很复杂，该规范试图在易用性与编写高性能和可伸缩并发数据结构的能力之间找到平衡。

<a id="actors-and-the-java-memory-model"></a>
## Actor和Java内存模型

通过Akka中的Actors实现，多个线程可以通过两种方式对共享内存执行操作：

 * 如果一个消息被发送给一个actor(例如，由另一个actor发送)。在大多数情况下，消息是不可变的，但如果该消息不是正确构造的不可变对象，如果没有“先发生”规则，则接收方可能会看到部分初始化的数据结构，甚至可能凭空看到值(longs/doubles)。
 * 如果actor在处理一条消息时对其内部状态进行了更改，并在稍后处理另一条消息时访问了该状态。重要的是要认识到，使用actor模型并不能保证相同的线程会为不同的消息执行相同的actor，。

为了防止actor上的可见性和重新排序问题，Akka保证以下两个"发生之前"规则：

 * **actor的发送规则:** 向actor发送消息是在同一actor接收该消息之前进行的。
 * **actor的后续处理规则:** 一个消息的处理发生在同一actor处理下一个消息之前。

@@@ note

用外行术语来说，这意味着当下一条消息由actor处理时，actor的内部字段的更改是可见的。因此，actor中的字段不必是易变的或等价的。

@@@

这两个规则仅适用于相同的actor实例，如果使用了不同的actor，则无效。

<a id="futures-and-the-java-memory-model"></a>
## Future和Java内存模型

一个Future的完成，发生在执行注册到Future的回调调用之前。

我们建议不要覆盖非final字段(Java中为final，而Scala中为val)，如果确实选择覆盖非final字段，则必须将其标记为*volatile*，以便字段的当前值对回调可见。

如果覆盖一个引用，您还必须确保引用的实例是线程安全的。我们强烈建议远离使用锁的对象，因为它会带来性能问题，在最坏的情况下，还会导致死锁。这就是同步的危害。

<a id="jmm-shared-state"></a>
## Actor和共享的可变状态

由于Akka在JVM上运行，因此仍然需要遵循一些规则。

最重要的是，您一定不能覆盖Actor内部状态并将其暴露给其他线程：

Scala
: @@snip [SharedMutableStateDocSpec.scala](/akka-docs/src/test/scala/docs/actor/typed/SharedMutableStateDocSpec.scala) { #mutable-state }

Java
: @@snip [DistributedDataDocTest.java](/akka-docs/src/test/java/jdocs/actor/typed/SharedMutableStateDocTest.java) { #mutable-state }


 * 消息**应该**是不可变的，这是为了避免共享的可变状态陷阱。
