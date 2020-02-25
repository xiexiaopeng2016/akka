---
project.description: Append only event logs, snapshots and recovery with Akka event sourced actors.
---
<a id="snapshotting"></a>
# 快照

有关此功能的Akka经典文档，请参阅 @ref:[经典Akka持久化](../persistence.md)。

<a id="snapshots"></a>
## 快照

当您使用 @ref:[事件溯源actor](persistence.md)来为领域建模时，您可能会注意到，有些actor可能倾向于积累非常长的事件日志并经历很长的恢复时间。有时，正确的做法可能是分成一组寿命较短的actor。但是，如果没有这个选项，您可以使用快照来大大减少恢复时间。

持久化actor可以每隔N个事件或在满足状态的给定谓词时保存内部状态的快照。

Scala
:  @@snip [BasicPersistentActorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #retentionCriteria }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #retentionCriteria }


Scala
:  @@snip [BasicPersistentActorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #snapshottingPredicate }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #snapshottingPredicate }

触发快照时，将存储传入的命令，直到快照被保存为止。这意味着虽然状态的序列化和存储是异步执行的，但状态可以安全地被改变。在保存快照之前，状态实例不会被新的事件更新。

在恢复期间，持久化actor正在使用最新保存的快照来初始化状态。此后，使用事件处理程序重播快照后的事件，以将持久化actor恢复到其当前(即最新)状态。

如果未指定，则默认将`SnapshotSelectionCriteria.Latest`选择为最新(最新)快照。可以像这样覆盖要用于恢复的快照选择：

Scala
:  @@snip [BasicPersistentActorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #snapshotSelection }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #snapshotSelection }

要禁用基于快照的恢复，应用程序可以使用`SnapshotSelectionCriteria.None`。如果没有保存的快照与指定的`SnapshotSelectionCriteria`匹配，则恢复将重播所有日志事件。这将很有用，如果快照序列化格式已经用不兼容的方式更改。通常在删除事件时不应该使用它。

为了使用快照，默认的快照存储(`akka.persistence.snapshot-store.plugin`)必须进行配置，
或者你可以通过定义`EventSourcedBehavior`的`withSnapshotPluginId`来为一个特定的`EventSourcedBehavior`选择一个快照存储。

因为有些用例可能不会从快照中受益或需要快照，所以不配置快照存储是完全正确的。但是，当检测到这种情况时，Akka将记录一条警告消息，然后继续操作，直到一个actor试图存储快照，此时操作将失败。

<a id="snapshot-failures"></a>
## 快照失败

保存快照可以成功也可以失败 - 这个信息将通过`SnapshotCompleted`或`SnapshotFailed`信号反馈给持久化actor。默认情况下会记录快照故障，但不会导致Actor停止或重新启动。

如果在启动actor时从日志中恢复actor的状态存在问题，则会发出`RecoveryFailed`信号(默认情况下记录错误)，并且actor将停止。
请注意，加载快照失败也被这样处理，但是您可以禁用快照的加载，如果您知道序列化格式已经以不兼容的方式改变了。

<a id="snapshot-deletion"></a>
## 快照删除

为了释放空间，事件溯源的actor可以根据给定`RetentionCriteria`，自动删除较旧的快照。

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #retentionCriteria }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #retentionCriteria #snapshottingPredicate }

快照删除是在保存新快照后触发的。

上面的示例将在每个`numberOfEvents = 100`自动保存快照。序列号小于已保存快照的序列号减`keepNSnapshots * numberOfEvents` (`100 * 2`)的快照将被自动删除。

此外，当持久化事件为`BookingCompleted`时，它还将保存一个快照。可以使用基于`numberOfEvents`的自动快照，而无需指定由`snapshotWhen`谓词触发的`snapshotWhen`快照， 不会触发旧快照的删除。

在异步删除时，将发出一个`DeleteSnapshotsCompleted`或`DeleteSnapshotsFailed`信号。
您可以通过使用`receiveSignal`处理程序对信号结果作出反应。默认情况下，系统将在`debug`日志级别记录成功完成，而在`warning`日志级别记录失败。

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #retentionCriteriaWithSignals }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #retentionCriteriaWithSignals }

<a id="event-deletion"></a>
## 事件删除

在基于事件溯源的应用程序中删除事件通常要么根本不使用，要么与快照结合使用。

通常根本不使用基于事件源的应用程序中的事件，或者与快照结合使用。通过删除事件，您将丢失系统在达到当前状态之前如何更改的历史记录，这是首先使用事件源的主要原因之一。

如果启用了基于快照的保留，则在成功存储一个快照之后，一个事件的删除(由单个事件溯源的actor记录)，直到可以发出该快照所持有的数据的序列号为止。

要选择使用这一点，启用`RetentionCriteria`的`withDeleteEventsOnSnapshot`，其默认为禁用。

Scala
:  @@snip [BasicPersistentBehaviorCompileOnly.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/BasicPersistentBehaviorCompileOnly.scala) { #snapshotAndEventDeletes }

Java
:  @@snip [BasicPersistentBehaviorTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/BasicPersistentBehaviorTest.java) { #snapshotAndEventDeletes }

事件删除在保存新快照后触发。旧事件将在旧快照被删除之前被删除。

在异步删除时，将发出一个`DeleteEventsCompleted`或`DeleteEventsFailed`信号。您可以通过使用`receiveSignal`处理程序对信号结果作出反应。默认情况下，系统将在`debug`日志级别记录成功完成，而在`warning`日志级别记录失败。

消息删除不会影响日志的最高序列号，即使所有的消息在删除发生后都被删除了。

@@@ note

事件是否从存储中实际删除取决于日志实现。

@@@
