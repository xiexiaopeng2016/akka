<a id="stash"></a>
# 存储

有关此功能的Akka经典文档，请参阅 @ref:[经典Actor](../actors.md#stash)。

## 依赖

要使用类型化Akka Actor，必须在项目中添加以下依赖项：

@@dependency[sbt,Maven,Gradle] {
  group=com.typesafe.akka
  artifact=akka-actor-typed_$scala.binary_version$
  version=$akka.version$
}

<a id="introduction"></a>
## 介绍

存储使一个actor能够临时缓冲所有或某些消息，它们无法使用该actor的当前行为处理。

在此情况下，一个典型的例子是，如果actor必须加载某个初始状态或初始化某些资源才能接受第一个真正的消息。另一个例子是，当actor在处理下一条消息之前等待某件事情完成。

让我们用一个例子来说明这两种情况。下面的`DataAccess`actor的使用就像存储在一个数据库中的一个值的单个访问点。启动时，它将从数据库中加载当前状态，并且在等待该初始值时，将存储所有传入消息。

当一个新状态保存在数据库中时，它也存储传入的消息，一边按顺序进行处理，一个接一个，而没有多个挂起的写操作。

Scala
:  @@snip [StashDocSpec.scala](/akka-actor-typed-tests/src/test/scala/docs/akka/typed/StashDocSpec.scala) { #stashing }

Java
:  @@snip [StashDocTest.java](/akka-actor-typed-tests/src/test/java/jdocs/akka/typed/StashDocSample.java) {
  #import
  #db
  #stashing
}

需要注意的重要一点是，`StashBuffer`是一个缓冲区，并且已存储的消息将一直保留在内存中，直到它们被取消存储(或actor被停止并垃圾回收)为止。建议避免存储太多消息，以避免过多的内存使用，如果有许多actor正在存储许多消息，甚至要冒`OutOfMemoryError`风险。因此，`StashBuffer`是有界的，必须在创建时指定它的`capacity`，可以容纳多少消息。

如果试图存储比`capacity`更多的消息，一个`StashOverflowException`将被抛出。您可以在存储消息之前使用`StashBuffer.isFull`以避免发生这种情况，并采取其他措施，例如删除消息。

当通过调用`unstashAll`取消缓冲消息的存储时，这些消息将按它们被添加的顺序依次处理，除非抛出异常，否则所有消息都将被处理。在`unstashAll`完成之前，actor对其他新消息没有响应。这是保持储存消息数量较低的另一个原因。占用消息处理线程时间过长的Actor可能导致其他Actor饥饿。

这可以通过使用`StashBuffer.unstash`与`numberOfMessages`参数来缓解。然后发送一个消息到`context.self`，在继续释放更多之前。这意味着其他新消息可能会在它们之间到达，并且必须将它们存储起来以保持消息的原始顺序。它变得更加复杂，所以最好保持存储消息的数量较低。
