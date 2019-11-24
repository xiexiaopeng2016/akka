---
project.description: What is an Actor and sending messages between independent units of computation in Akka.
---
# 什么是Actor?

关于 @ref:[Actor Systems](actor-systems.md) 的前一节说明了actor如何形成层次结构，并且是构建应用程序时最小的单元。本节单独讨论这样一个actor，解释您在实现它时遇到的概念。想要更深入地了解所有细节，请参考 @ref:[Actor介绍](../typed/actors.md)。

[Actor Model](http://en.wikipedia.org/wiki/Actor_model) 是Hewitt，Bishop和Steiger在1973年定义的，是一个计算模型，它准确地表达了分布式计算的含义。处理单元—Actor—只能通过交换消息进行通信，而在接收到消息之后，Actor可以执行以下三个基本操作：

  1. 向它知道的Actor发送有限数量的消息
  2. 创建有限数量的新Actor
  3. 指定要应用于下一条消息的行为

actor是 @ref:[状态](#state)，@ref:[行为](#behavior)，@ref:[邮箱](#mailbox)，@ref:[子Actor](#child-actors)和 @ref:[监督策略](#supervisor-strategy) 的容器。所有这些都封装在一个 @ref:[Actor引用](#actor-reference) 背后。一个值得注意的方面是actor具有明确的生命周期，当不再引用它们时，它们不会自动销毁。创建完一个后，您有责任确保它最终也将终止，这也使您可以控制 @ref:[当一个Actor终止时](#when-an-actor-terminates)时如何释放资源。

<a id="actor-reference"></a>
## Actor引用

如下所述，为了从actor模型中受益，需要从外部屏蔽actor对象。因此，使用actor引用将actor呈现给外部，actor引用是可以自由传递且不受限制的对象。这种分为内部和外部对象的功能使所有所需操作的透明性得以实现：重新启动actor，无需在其他地方更新引用；将实际的actor对象放置在远程主机上；向运行在完全不同的位置的actor发送消息。但最重要的方面是，不可能从查看actor的内部并从外部获取其状态，除非参与者不明智地发布了自己的信息。

Actor引用是参数化的，只有特定类型的消息才能发送给它们。

<a id="state"></a>
## 状态

Actor对象通常将包含一些变量，这些变量反映Actor可能处于的状态。这可以是显式状态机，或者它也可以是计数器，侦听器集合，未决的请求等。这些数据是使一个演员有价值的东西，必须保护它们免受其他actor的腐蚀。好消息是，Akka actor从概念上讲都有各自的轻量级线程，该线程与系统的其余部分完全隔离。这意味着不必使用锁来同步访问，您可以编写自己的actor代码，而不必担心并发性。

在后台，Akka将在一组实际线程上运行一组actor，其中通常有许多actor共享一个线程，而对一个actor的后续调用最终可能会在不同的线程上进行处理。Akka确保此实现细节不会影响处理actor状态的单线程。

因为内部状态对actor的操作至关重要，所以不一致的状态是致命的。因此，当actor失败并由其上级重新启动时，状态将从零开始创建，就像第一次创建actor时一样。这是为了使系统能够自我修复。

另外，可以通过持久化接收到的消息，将参与者的状态自动恢复到重启之前的状态，且重启后重播(查看 @ref:[Event Sourcing](../typed/persistence.md))。

## 行为

Every time a message is processed, it is matched against the current behavior
of the actor. Behavior means a function which defines the actions to be taken
in reaction to the message at that point in time, say forward a request if the
client is authorized, deny it otherwise. This behavior may change over time,
e.g. because different clients obtain authorization over time, or because the
actor may go into an “out-of-service” mode and later come back. These changes
are achieved by either encoding them in state variables which are read from the
behavior logic, or the function itself may be swapped out at runtime, by returning
a different behavior to be used for next message. However, the initial behavior defined
during construction of the actor object is special in the sense that a restart
of the actor will reset its behavior to this initial one.

Messages can be sent to an @ref:[actor Reference](#actor-reference) and behind
this façade there is a behavior that receives the message and acts upon it. The
binding between Actor reference and behavior can change over time, but that is not
visible on the outside.

Actor references are parameterized and only messages that are of the specified type
can be sent to them. The association between an actor reference and its type
parameter must be made when the actor reference (and its Actor) is created.
For this purpose each behavior is also parameterized with the type of messages
it is able to process. Since the behavior can change behind the actor reference
façade, designating the next behavior is a constrained operation: the successor
must handle the same type of messages as its predecessor. This is necessary in
order to not invalidate the actor references that refer to this Actor.

What this enables is that whenever a message is sent to an Actor we can
statically ensure that the type of the message is one that the Actor declares
to handle—we can avoid the mistake of sending completely pointless messages.
What we cannot statically ensure, though, is that the behavior behind the
actor reference will be in a given state when our message is received. The
fundamental reason is that the association between actor reference and behavior
is a dynamic runtime property, the compiler cannot know it while it translates
the source code.

This is the same as for normal Java objects with internal variables: when
compiling the program we cannot know what their value will be, and if the
result of a method call depends on those variables then the outcome is
uncertain to a degree—we can only be certain that the returned value is of a
given type.

The reply message type of an Actor command is described by the type of the
actor reference for the reply-to that is contained within the message. This
allows a conversation to be described in terms of its types: the reply will
be of type A, but it might also contain an address of type B, which then allows
the other Actor to continue the conversation by sending a message of type B to
this new actor reference. While we cannot statically express the “current” state
of an Actor, we can express the current state of a protocol between two Actors,
since that is just given by the last message type that was received or sent.

## Mailbox

An actor’s purpose is the processing of messages, and these messages were sent
to the actor from other actors (or from outside the actor system). The piece
which connects sender and receiver is the actor’s mailbox: each actor has
exactly one mailbox to which all senders enqueue their messages. Enqueuing
happens in the time-order of send operations, which means that messages sent
from different actors may not have a defined order at runtime due to the
apparent randomness of distributing actors across threads. Sending multiple
messages to the same target from the same actor, on the other hand, will
enqueue them in the same order.

There are different mailbox implementations to choose from, the default being a
FIFO: the order of the messages processed by the actor matches the order in
which they were enqueued. This is usually a good default, but applications may
need to prioritize some messages over others. In this case, a priority mailbox
will enqueue not always at the end but at a position as given by the message
priority, which might even be at the front. While using such a queue, the order
of messages processed will naturally be defined by the queue’s algorithm and in
general not be FIFO.

An important feature in which Akka differs from some other actor model
implementations is that the current behavior must always handle the next
dequeued message, there is no scanning the mailbox for the next matching one.
Failure to handle a message will typically be treated as a failure, unless this
behavior is overridden.

## Child Actors

Each actor is potentially a parent: if it creates children for delegating
sub-tasks, it will automatically supervise them. The list of children is
maintained within the actor’s context and the actor has access to it.
Modifications to the list are done by spawning or stopping children and
these actions are reflected immediately. The actual creation and termination
actions happen behind the scenes in an asynchronous way, so they do not “block”
their parent.

## Supervisor Strategy

The final piece of an actor is its a strategy for handling unexpected exceptions - failures. 
Fault handling is then done transparently by Akka, applying one of the strategies described 
in @ref:[Fault Tolerance](../typed/fault-tolerance.md) for each failure.

## When an Actor Terminates

Once an actor terminates, i.e. fails in a way which is not handled by a
restart, stops itself or is stopped by its supervisor, it will free up its
resources, draining all remaining messages from its mailbox into the system’s
“dead letter mailbox” which will forward them to the EventStream as DeadLetters.
The mailbox is then replaced within the actor reference with a system mailbox,
redirecting all new messages to the EventStream as DeadLetters. This
is done on a best effort basis, though, so do not rely on it in order to
construct “guaranteed delivery”.
