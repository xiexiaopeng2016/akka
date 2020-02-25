<a id="configuration"></a>
# 配置

由于提供了合理的默认值，因此无需定义任何配置即可开始使用Akka。稍后，您可能需要修改设置以更改默认行为或适应特定的运行时环境。您可能会修改的典型设置示例：

 * @ref:[日志级别和记录器后端](../typed/logging.md)
 * @ref:[启用集群](../typed/cluster.md)
 * @ref:[消息序列化器](../serialization.md)
 * @ref:[调度器调优](../typed/dispatchers.md)

Akka使用[Typesafe配置库](https://github.com/lightbend/config)，这对于配置您自己的应用程序或库(使用或没有使用Akka构建)，是一个不错的选择。该库是用Java实现的，没有任何外部依赖性。这只是最重要部分的摘要，有关更多详细信息，请参见[配置库文档](https://github.com/lightbend/config/blob/master/README.md)。

<a id="where-configuration-is-read-from"></a>
## 从哪里读取配置

Akka的所有配置都保存在`ActorSystem`的实例中，或者换句话说，从外面看，`ActorSystem`是配置信息的唯一消费者。在构建一个actor系统时，你可以传入一个`Config`对象，也可以不传入，第二种情况相当于传递`ConfigFactory.load()`(使用正确的类加载器)。这大致意味着默认是解析所有在类路径的根目录下找到的`application.conf`、`application.json`和`application.properties` —详细信息请参考前面提到的文档。actor系统然后合并所有在类路径的根目录下找到的`reference.conf`资源，以形成回滚配置，即它在内部使用

```scala
appConfig.withFallback(ConfigFactory.defaultReference(classLoader))
```

理念是代码从不包含默认值，而是依赖于它们在`reference.conf`中的存在，与该库一起提供。

优先级最高的是作为系统属性的覆盖，请参见[HOCON规范](https://github.com/typesafehub/config/blob/master/HOCON.md)(在底部附近)。同样值得注意的是，application可以使用config.resource属性覆盖应用程序配置（默认为）（还有更多内容，请参阅Config docs）

同样值得注意的是，应用程序配置 — 默认为`application` — 可以使用`config.resource`属性重写(还有更多内容，请参阅[配置文档](https://github.com/typesafehub/config/blob/master/README.md)).

@@@ note

如果你正在编写一个Akka应用程序，将配置保留在类路径的根目录下的`application.conf`中。如果你正在编写基于Akka的库，请将其配置保留在JAR文件的根目录下的`reference.conf`中。不支持在一个库的`reference.conf`中覆盖属于另一个库的配置属性。

@@@

<a id="when-using-jarjar-onejar-assembly-or-any-jar-bundler"></a>
## 当使用JarJar, OneJar, Assembly或任何jar-bundler时

@@@ warning

Akka的配置方法严重依赖于每个模块/jar都有自己的`reference.conf`文件的概念。所有这些都将由配置发现并加载。不幸的是，这也意味着如果您将多个jar放入/合并到同一个jar中，你需要合并所有的`reference.conf`文件，否则所有的默认值都将丢失。

@@@

有关如何在打包时合并`reference.conf`资源的信息，请参阅 @ref[部署文档](../additional/deploy.md)。

<a id="custom-application-conf"></a>
## 自定义application.conf

一个自定义`application.conf`可能如下所示：

```
# In this file you can override any option defined in the reference files.
# Copy in parts of the reference files and modify as you please.

akka {

  # Logger config for Akka internals and classic actors, the new API relies
  # directly on SLF4J and your config for the logger backend.

  # Loggers to register at boot time (akka.event.Logging$DefaultLogger logs
  # to STDOUT)
  loggers = ["akka.event.slf4j.Slf4jLogger"]

  # Log level used by the configured loggers (see "loggers") as soon
  # as they have been started; before that, see "stdout-loglevel"
  # Options: OFF, ERROR, WARNING, INFO, DEBUG
  loglevel = "DEBUG"

  # Log level for the very basic logger activated during ActorSystem startup.
  # This logger prints the log messages to stdout (System.out).
  # Options: OFF, ERROR, WARNING, INFO, DEBUG
  stdout-loglevel = "DEBUG"

  # Filter of log events that is used by the LoggingAdapter before
  # publishing log events to the eventStream.
  logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"

  actor {
    provider = "cluster"

    default-dispatcher {
      # Throughput for default Dispatcher, set to 1 for as fair as possible
      throughput = 10
    }
  }

  remote.artery {
    # The port clients should connect to.
    canonical.port = 4711
  }
}
```

<a id="including-files"></a>
## 包括的文件

有时，包括另一个配置文件很有用，例如，如果您拥有一个`application.conf`具有所有与环境无关的设置，然后覆盖特定环境的一些设置。

使用`-Dconfig.resource=/dev.conf`指定系统属性，将加载`dev.conf`文件，其中包括`application.conf`

### dev.conf

```
include "application"

akka {
  loglevel = "DEBUG"
}
```

[HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md)规范中解释了更高级的包含和替换机制。

<a id="dakka-log-config-on-start"></a>
## Logging of Configuration

如果系统或配置属性`akka.log-config-on-start`设置为`on`，则在启动actor系统时，将在INFO日志级别输出完整的配置。当您不确定使用了什么配置时，这很有用。

@@@div { .group-scala }

如果有疑问，你可以检查你的配置对象之前或之后，使用他们来建设一个actor系统：

```
Welcome to Scala 2.12 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import com.typesafe.config._
import com.typesafe.config._

scala> ConfigFactory.parseString("a.b=12")
res0: com.typesafe.config.Config = Config(SimpleConfigObject({"a" : {"b" : 12}}))

scala> res0.root.render
res1: java.lang.String =
{
    # String: 1
    "a" : {
        # String: 1
        "b" : 12
    }
}
```

@@@

每个项目前面的注释提供有关设置来源的详细信息(文件和行号)，加上可能存在的注释，例如在reference配置中。与reference合并，并由actor系统解析的设置可以显示如下：

Scala
: @@snip [ConfigDocSpec.scala](/akka-docs/src/test/scala/docs/config/ConfigDocSpec.scala) { #dump-config }

Java
: @@snip [ConfigDocTest.java](/akka-docs/src/test/java/jdocs/config/ConfigDocTest.java) { #dump-config }

<a id="a-word-about-classloaders"></a>
## 关于类加载器的一句话

在配置文件的几个地方，可以指定要由Akka实例化的对象的完全限定类名。

在配置文件的多个位置，可以指定要由Akka实例化的东西的完全限定的类名。这是使用Java反射完成的，而Java反射又使用了一个`ClassLoader`。在诸如应用程序容器或OSGi包之类的具有挑战性的环境中找到合适的对象并不总是一件容易的事，Akka的当前方法是每个`ActorSystem`实现都存储当前线程的上下文类加载器(如果可用的话，否则仅自己的加载器，比如在`this.getClass.getClassLoader`)，并将其用于所有反射式访问。这意味着将Akka放在引导类路径上会从奇怪的地方产生`NullPointerException`：这是不支持的。

<a id="application-specific-settings"></a>
## 特定于应用程序的设置

配置还可用于特定于应用程序的设置。一个好的做法是将这些设置放在一个 @ref:[扩展](../extending-akka.md#extending-akka-settings)中。

<a id="configuring-multiple-actorsystem"></a>
## 配置多个ActorSystem

如果您拥有多个`ActorSystem`库(或者您正在编写一个库，并且有一个`ActorSystem`，它可能与应用程序的分开)，您可能希望将每个系统的配置分开。

考虑到`ConfigFactory.load()`从整个类路径中将所有具有匹配名称的资源合并在一起，最简单的方法是利用该功能并在配置层次结构中区分actor系统：

```
myapp1 {
  akka.loglevel = "WARNING"
  my.own.setting = 43
}
myapp2 {
  akka.loglevel = "ERROR"
  app2.setting = "appname"
}
my.own.setting = 42
my.other.setting = "hello"
```

Scala
: @@snip [ConfigDocSpec.scala](/akka-docs/src/test/scala/docs/config/ConfigDocSpec.scala) { #separate-apps }

Java
: @@snip [ConfigDocTest.java](/akka-docs/src/test/java/jdocs/config/ConfigDocTest.java) { #separate-apps }

这两个示例演示了“lift-a-subtree”技巧的不同变体：在第一种情况下，可从actor系统内部访问的配置，像这样

```ruby
akka.loglevel = "WARNING"
my.own.setting = 43
my.other.setting = "hello"
// plus myapp1 and myapp2 subtrees
```

而在第二个，只有“akka”子树被提升，结果如下

```ruby
akka.loglevel = "ERROR"
my.own.setting = 42
my.other.setting = "hello"
// plus myapp1 and myapp2 subtrees
```

@@@ note

配置库非常强大，说明所有功能都超出了此处可承受的范围。特别是没有讨论如何在其他文件中包含其他配置文件(请参见[包含文件](#including-files)中的一个小例子)并通过路径替换的方式复制配置树的部分内容。

@@@

实例化`ActorSystem`时，您还可以通过其他方式以编程方式指定和解析配置。


Scala
: @@snip [ConfigDocSpec.scala](/akka-docs/src/test/scala/docs/config/ConfigDocSpec.scala) { #imports #custom-config }

Java
: @@snip [ConfigDocTest.java](/akka-docs/src/test/java/jdocs/config/ConfigDocTest.java) { #imports #custom-config }

<a id="reading-configuration-from-a-custom-location"></a>
## 从自定义位置读取配置

您可以用代码或使用系统属性来替换或补充`application.conf`。

如果您正在使用`ConfigFactory.load()`(那是Akka默认的)，您可以替换`application.conf`，通过定义`-Dconfig.resource=whatever`，`-Dconfig.file=whatever`或`-Dconfig.url=whatever`。

从您的替换文件中指定`-Dconfig.resource`和朋友，你可以`include "application"`，如果你仍然想使用`application.{conf,json,properties}`。在`include "application"`之前指定的设置将被包含的文件覆盖，而之后的设置将覆盖包含的文件。

在代码中，有许多自定义选项。

有几个`ConfigFactory.load()`重载；这允许您指定要夹在系统属性之间的内容(覆盖)和默认值(来自`reference.conf`)之间的内容，从而替换通常的内容`application.{conf,json,properties}`并替换`-Dconfig.file`和朋友。

`ConfigFactory.load()`的最简单变体采用一个资源基本名称(而不是`application`)；`myname.conf`，`myname.json`，和`myname.properties`会被用来代替`application.{conf,json,properties}`。

最灵活的变体采用一个`Config`对象，您可以使用`ConfigFactory`中的任何方法加载该对象。例如，您可以使用`ConfigFactory.parseString()`在代码中放入配置字符串，或者可以制作一个映射和`ConfigFactory.parseMap()`，或者可以加载一个文件。

您还可以将自定义配置与通常的配置结合使用，如下所示：

Scala
: @@snip [ConfigDocSpec.scala](/akka-docs/src/test/scala/docs/config/ConfigDocSpec.scala) { #custom-config-2 }

Java
: @@snip [ConfigDocTest.java](/akka-docs/src/test/java/jdocs/config/ConfigDocTest.java) { #custom-config-2 }

当使用`Config`对象时，请记住，蛋糕中包含三个“层”：

 * `ConfigFactory.defaultOverrides()` (系统属性)
 * 应用程序的设置
 * `ConfigFactory.defaultReference()` (reference.conf)

通常的目标是自定义中间层，而不涉及其他两层。。

 * `ConfigFactory.load()` 加载整个堆栈
 * `ConfigFactory.load()`的重载让您指定不同的中间层
 * `ConfigFactory.parse()`变体加载单个文件或资源

要堆叠两层，请使用`override.withFallback(fallback)`；尽量保留系统属性(`defaultOverrides()`)在顶部和`reference.conf` (`defaultReference()`)在底部。

请记住，您通常可以在`application.conf`中添加另一个`include`语句，而不是编写代码。`application.conf`顶部的包含将被其余部分覆盖，而底部的包含将覆盖之前的内容。

<a id="listing-of-the-reference-configuration"></a>
## 参考配置清单

每个Akka模块都有一个带有默认值的参考配置文件。这些`reference.conf`文件列在 @ref[默认配置](configuration-reference.md)中。
