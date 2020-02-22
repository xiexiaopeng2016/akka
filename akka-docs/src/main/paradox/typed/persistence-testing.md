<a id="testing"></a>
# 测试

<a id="dependency"></a>
## 依赖

要使用Akka持久化和Actor TestKit，请将模块添加到您的项目中：

@@dependency[sbt,Maven,Gradle] {
  group1=com.typesafe.akka
  artifact1=akka-persistence-typed_$scala.binary_version$
  version1=$akka.version$
  group2=com.typesafe.akka
  artifact2=akka-actor-testkit-typed_$scala.binary_version$
  version2=$akka.version$
  scope2=test
}

<a id="unit-testing"></a>
## 单元测试

`EventSourcedBehavior`的单元测试可以使用 @ref:[ActorTestKit](testing-async.md)来进行，测试方式与其他行为相同。

针对`EventSourcedBehavior`的 @ref:[同步行为测试](testing-sync.md)尚不支持，但在 @github[问题#26338](#23712)跟踪。

您需要配置一个日志，并且内存日志对于单元测试来说已经足够了。要启用内存日志，您需要将以下配置传递给`ScalaTestWithActorTestKit`。

Scala
:  @@snip [AccountExampleDocSpec.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleDocSpec.scala) { #inmem-config }

Java
:  @@snip [AccountExampleDocTest.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleDocTest.java) { #inmem-config } 

配置`InmemJournal`的`test-serialization = on`将验证持久化的事件可以序列化和反序列化。

可选的，您还可以配置快照存储。要启用基于文件的快照存储，您需要将以下配置传递给`ScalaTestWithActorTestKit`。

Scala
:  @@snip [AccountExampleDocSpec.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleDocSpec.scala) { #snapshot-store-config }

Java
:  @@snip [AccountExampleDocTest.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleDocTest.java) { #snapshot-store-config }

然后你就可以`spawn`(生成)`EventSourcedBehavior`，并验证使用以下命令向参与者发送命令的结果，工具是 @ref:[ActorTestKit](testing-async.md)。

一个`AccountEntity`的完整测试，在@ref:[持久化风格指南](persistence-style.md)中显示的可能如下所示：

Scala
:  @@snip [AccountExampleDocSpec.scala](/akka-cluster-sharding-typed/src/test/scala/docs/akka/cluster/sharding/typed/AccountExampleDocSpec.scala) { #test }

Java
:  @@snip [AccountExampleDocTest.java](/akka-cluster-sharding-typed/src/test/java/jdocs/akka/cluster/sharding/typed/AccountExampleDocTest.java) { #test }  

请注意，每个测试用例都使用不同的`PersistenceId`，以免彼此干扰。

<a id="integration-testing"></a>
## 集成测试

基于内存的日志和基于文件的快照存储也可以用于单个`ActorSystem`实例的集成样式测试，例如，在使用单个集群节点的集群分片时。

对于涉及多个集群节点的测试，必须使用另一个日志和快照存储。尽管可以使用 @ref:[持久化插件代理](../persistence-plugins.md#persistence-plugin-proxy)，但使用真实数据库通常更好，更现实。

参见[akka-samples issue #128](https://github.com/akka/akka-samples/issues/128).    
