<a id="eventsourced-behaviors-as-finite-state-machines"></a>
# 事件溯源行为作为有限状态机

一个 @apidoc[EventSourcedBehavior]可以被用于表示一个持久化FSM。如果要将现有的经典持久化FSM 迁移到`EventSourcedBehavior`，请参阅 @ref[迁移指南](../persistence-fsm.md#migration-to-eventsourcedbehavior)。

为了说明这一点，请思考购物应用程序的示例。一个客户可以处于以下状态：

* 东张西望
* 购物(篮子里有东西)
* 不活跃
* 已支付

Scala
:  @@snip [PersistentFsmToTypedMigrationSpec.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/PersistentFsmToTypedMigrationSpec.scala) { #state }

Java
:  @@snip [PersistentFsmToTypedMigrationCompileOnlyTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/PersistentFsmToTypedMigrationCompileOnlyTest.java) { #state }

以及可能导致状态改变的命令:

* 新增项目
* 购买
* 离开 
* 超时(内部命令抛弃放弃的购买)

以及以下只读命令：

* 获取当前购物车

Scala
:  @@snip [PersistentFsmToTypedMigrationSpec.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/PersistentFsmToTypedMigrationSpec.scala) { #commands }

Java
:  @@snip [PersistentFsmToTypedMigrationCompileOnlyTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/PersistentFsmToTypedMigrationCompileOnlyTest.java) { #commands }

`EventSourcedBehavior`的命令处理程序用于转换命令，它将FSM的状态更改为事件，并回复命令。

命令处理程序：

Scala
:  @@snip [PersistentFsmToTypedMigrationSpec.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/PersistentFsmToTypedMigrationSpec.scala) { #command-handler }

Java
:  @@snip [PersistentFsmToTypedMigrationCompileOnlyTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/PersistentFsmToTypedMigrationCompileOnlyTest.java) { #command-handler }

一旦事件被持久化，事件处理程序将用于更改状态。当`EventSourcedBehavior`重新启动时，将重播事件以回到正确的状态。

Scala
:  @@snip [PersistentFsmToTypedMigrationSpec.scala](/akka-persistence-typed/src/test/scala/docs/akka/persistence/typed/PersistentFsmToTypedMigrationSpec.scala) { #event-handler }

Java
:  @@snip [PersistentFsmToTypedMigrationCompileOnlyTest.java](/akka-persistence-typed/src/test/java/jdocs/akka/persistence/typed/PersistentFsmToTypedMigrationCompileOnlyTest.java) { #event-handler }
