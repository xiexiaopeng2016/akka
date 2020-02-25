---
project.description: Hierarchical supervision, lifecycle monitoring and error or failure handling in Akka.
---
<a id="supervision-and-monitoring"></a>
# 监督与监视

本章概述了监督背后的概念，所提供的原语及其语义。有关如何将其转换为真实代码的详细信息，请参阅 @ref:[监督](../typed/fault-tolerance.md)。

自经典以来，监督已发生变化，有关经典监督的详细信息，请参阅 @ref:[经典监督](../supervision-classic.md)。

<a id="supervision-directives"></a>
## 监督的意义

在一个actor中可能发生两类异常:

 1. 输入验证错误，预期的异常，可以使用常规try-catch或其他语言和标准库工具进行处理。
 2. 意外**故障**，例如网络资源不可用，磁盘写入失败或应用程序逻辑中的错误。

监视处理故障，应该与业务逻辑分离，同时验证数据和处理预期的异常是业务逻辑的重要组成部分。因此，监督被添加到一个actor作为装饰，而不是与actor的消息处理逻辑混杂在一起。

根据被监督工作的性质和故障的性质，监督提供了以下三种策略：

 1. 恢复actor，保留其累积的内部状态
 2. 重启actor，清除其累积的内部状态，并可能再次延迟
 3. 永久停止actor

由于actor是层次结构的一部分，所以将永久性故障向上传播通常是有意义的，如果actor的所有子actor都意外停止，则actor本身重新启动或停止以返回到一个功能状态可能是有意义的。这可以通过一个监督结合和监视子actor来得到通知，当他们终止时。这方面的一个例子可以在 @ref:[冒泡失败贯穿整个层级](../typed/fault-tolerance.md#bubble)找到。

<a id="the-top-level-actors"></a>
## 顶级的演员

一个actor系统在其创建期间将至少启动两个actor。

### `/user`: 用户监护者actor

这是顶级用户提供的actor，意味着通过生成子系统作为子actor来引导应用程序。当用户监督者停止时，整个actor系统都将关闭。

### `/system`: 系统监督者

引入这个特殊的监督者是为了实现有序的关闭顺序，在那里，虽然日志本身是使用actor实现的，但在所有正常actor终止时，日志仍然是活动的。这是通过让系统监督者监视用户监督者，并在看到用户监督者停止后启动自己的关闭来实现的。

<a id="supervision-restart"></a>
## 重启意味着什么

当处理某条消息时遇到一个actor失败时，引起失败的原因可以分为三类：

 * 接收到的特定消息的系统(即编程)错误
 * 处理消息期间使用的某些外部资源(暂时)失败
 * actor的内部状态已损坏

除非故障是特别可识别的，否则无法排除第三个原因，从而得出内部状态需要被清除的结论。如果监督者断定它的其它子actor或自身不受损坏的影响 — 例如，由于错误内核模式的有意识的应用 — 因此，最好重新启动actor。这是通过创建一个以`Behavior`类为基础的新实例，并在子actor的`ActorRef`内部将失败的实例替换为新的那个实例来实现的；能够这样做是在特殊引用中封装actor的一个原因。

然后，新的actor恢复对其邮箱的处理，这意味着重启在actor本身之外是不可见的，值得注意的例外是，发生故障的消息未得到重新处理。

<a id="what-lifecycle-monitoring-means"></a>
## 生命周期监控的含义

@@@ note

在Akka中，生命周期监控通常称为`DeathWatch`

@@@

与上面描述的父actor和子actor之间的特殊关系不同，每个actor可以监视任何其他actor。

由于参与者从创建中完全活跃地出现，并且在受影响的监控器之外无法看到重新启动。由于actor从创造中完全活过来，并且重新启动在受影响的监督者之外是不可见的，因此可用于监视的唯一状态改变是从活动状到死亡的转换。因此，监控是用来把一个actor与另一个actor联系起来，以便对其它actor的终止作出反应，而不是对故障作出反应的监督。

生命周期监视是使用监视actor接收的一个`Terminated`消息来实现的，默认的行为是抛出一个特殊的`DeathPactException`，如果没有另外处理。为了开始监听`Terminated`消息，调用`ActorContext.watch(targetActorRef)`。要停止监听，请调用`ActorContext.unwatch(targetActorRef)`。一个重要的特性是，无论监视请求和目标的终止发生的顺序如何，消息都将被传递，也就是说，即使在注册时目标已经死亡，您仍然会收到消息。

<a id="actors-and-exceptions"></a>
## Actors与异常

可能发生的是，当一个actor正在处理一条消息时，会抛出某种异常，例如数据库异常。

<a id="what-happens-to-the-message"></a>
### 消息发生了什么

如果在处理一条消息时抛出一个异常(即从其邮箱中取出并移交给当前行为)，则该消息将丢失。重要的是要了解它不会放回邮箱。因此，如果您想重试消息处理，则需要自己捕获异常并重试处理流程。确保你设置了重试次数的上限，因为你不希望系统死机(livelock)(所以消耗了很多cpu周期却没有任何进展)。

<a id="what-happens-to-the-mailbox"></a>
### 邮箱发生了什么

如果在处理一条消息时引发一个异常，则邮箱不会做出任何反应。如果actor重新启动，则相同的邮箱将在那里。因此，该邮箱上的所有消息也将在那里。

<a id="what-happens-to-the-actor"></a>
### actor发生了什么

如果一个actor中的代码引发一个异常，则该actor将被挂起并启动监督过程。根据监督者的决定，actor可以恢复(好像什么也没发生)，重新启动(清除其内部状态并从出错处开始)或终止。
