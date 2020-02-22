---
project.description: Service discovery with Akka using DNS, Kubernetes, AWS, Consul or Marathon.
---
<a id="discovery"></a>
# 发现

Akka发现API使**服务发现**可以由不同的技术提供。它允许委托端点查找，以便可以根据环境通过配置文件以外的其他方式配置服务。

Akka Discovery模块提供的实现是

* @ref:[配置](#discovery-method-configuration) (HOCON)
* @ref:[DNS](#discovery-method-dns) (SRV records)
* @ref:[聚合](#discovery-method-aggregate-multiple-discovery-methods)多种发现方法

此外， @extref:[Akka管理工具](akka-management:) 箱还包含用于以下方面的Akka Discovery实现：

* @extref:[Kubernetes API](akka-management:discovery/kubernetes.html)
* @extref:[AWS API: EC2 Tag-Based Discovery](akka-management:discovery/aws.html#discovery-method-aws-api-ec2-tag-based-discovery)
* @extref:[AWS API: ECS Discovery](akka-management:discovery/aws.html#discovery-method-aws-api-ecs-discovery)
* @extref:[Consul](akka-management:discovery/consul.html)
* @extref:[Marathon API](akka-management:discovery/marathon.html)


@@@ note

发现曾经是Akka管理的一部分，但是在Akka的`2.5.19`和Akka管理的`1.0.0`版本中变成了一个Akka模块。如果您还将Akka管理用于其他服务发现方法或引导程序，请确保至少使用了Akka管理的`1.0.0`版本。

查看 @ref:[迁移提示](#migrating-from-akka-management-discovery-before-1-0-0-)

@@@

<a id="module-info"></a>
## 模块信息

@@dependency[sbt,Gradle,Maven] {
  group="com.typesafe.akka"
  artifact="akka-discovery_$scala.binary_version$"
  version="$akka.version$"
}

@@project-info{ projectId="akka-discovery" }

<a id="how-it-works"></a>
## 它是如何运作的

加载扩展：

Scala
:  @@snip [CompileOnlySpec.scala](/akka-discovery/src/test/scala/doc/akka/discovery/CompileOnlySpec.scala) { #loading }

Java
:  @@snip [CompileOnlyTest.java](/akka-discovery/src/test/java/jdoc/akka/discovery/CompileOnlyTest.java) { #loading }

一个`Lookup`包含一个必填`serviceName`，和一个可选的`portName`和`protocol`。如何解释这些取决于发现方法，例如，如果缺少任何字段，DNS会进行一个A/AAAA记录查询，和一个SRV查询为了一个完整查找：

Scala
:  @@snip [CompileOnlySpec.scala](/akka-discovery/src/test/scala/doc/akka/discovery/CompileOnlySpec.scala) { #basic }

Java
:  @@snip [CompileOnlyTest.java](/akka-discovery/src/test/java/jdoc/akka/discovery/CompileOnlyTest.java) { #basic }

`portName`和`protocol`是可选的，其含义由方法解释。

Scala
:  @@snip [CompileOnlySpec.scala](/akka-discovery/src/test/scala/doc/akka/discovery/CompileOnlySpec.scala) { #full }

Java
:  @@snip [CompileOnlyTest.java](/akka-discovery/src/test/java/jdoc/akka/discovery/CompileOnlyTest.java) { #full }

当服务打开多个端口(例如HTTP端口和Akka远程端口)时，可以使用端口。

<a id="discovery-method-dns"></a>
## 发现方法：DNS

DNS发现将`Lookup`查询映射如下：

* `serviceName`，`portName`和`protocol`设置: SRV查询，格式为: `_port._protocol.name` Where the `_`s are added.
* 缺少任何字段的任何查询都将为`serviceName`映射到一个A/AAAA查询

Akka服务发现术语和SRV术语之间的映射：

* SRV service = port
* SRV name = serviceName
* SRV protocol = protocol

在你的`application.conf`中配置`akka-dns`作为发现实现:

@@snip[application.conf](/akka-discovery/src/test/scala/akka/discovery/dns/DnsDiscoverySpec.scala){ #configure-dns }

从那里开始，您可以使用通用API，这就隐藏了调用正在使用哪个发现方法的事实:

Scala
:   ```scala
    import akka.discovery.ServiceDiscovery
    val system = ActorSystem("Example")
    // ...
    val discovery = ServiceDiscovery(system).discovery
    val result: Future[Resolved] = discovery.lookup("service-name", resolveTimeout = 500 milliseconds)
    ```

Java
:   ```java
    import akka.discovery.ServiceDiscovery;
    ActorSystem system = ActorSystem.create("Example");
    // ...
    SimpleServiceDiscovery discovery = ServiceDiscovery.get(system).discovery();
    Future<SimpleServiceDiscovery.Resolved> result = discovery.lookup("service-name", Duration.create("500 millis"));
    ```

<a id="dns-records-used"></a>
### 使用的DNS记录

DNS发现将使用A/AAAA记录还是SRV记录，具体取决于发出的查找是一个`Simple`还是`Full`。SRV记录的优点是它们可以包含一个端口。

#### SRV记录

设置了所有字段的查找将变成SRV查询。例如：

```
dig srv _service._tcp.akka.test

; <<>> DiG 9.11.3-RedHat-9.11.3-6.fc28 <<>> srv service.tcp.akka.test
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 60023
;; flags: qr aa rd ra; QUERY: 1, ANSWER: 2, AUTHORITY: 1, ADDITIONAL: 5

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 4096
; COOKIE: 5ab8dd4622e632f6190f54de5b28bb8fb1b930a5333c3862 (good)
;; QUESTION SECTION:
;service.tcp.akka.test.         IN      SRV

;; ANSWER SECTION:
_service._tcp.akka.test.  86400   IN      SRV     10 60 5060 a-single.akka.test.
_service._tcp.akka.test.  86400   IN      SRV     10 40 5070 a-double.akka.test.

```

在这种情况下，`service.tcp.akka.test`解析为`a-single.akka.test`在端口`5060`和`a-double.akka.test`在端口`5070`。当前发现不支持权重。

In this case `service.tcp.akka.test` resolves to `a-single.akka.test` on port `5060`
and `a-double.akka.test` on port `5070`. Currently discovery does not support the weightings.

#### A/AAAA记录

缺少任何字段的查找将变成A/AAAA记录查询。例如:

```
dig a-double.akka.test

; <<>> DiG 9.11.3-RedHat-9.11.3-6.fc28 <<>> a-double.akka.test
;; global options: +cmd
;; Got answer:
;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 11983
;; flags: qr aa rd ra; QUERY: 1, ANSWER: 2, AUTHORITY: 1, ADDITIONAL: 2

;; OPT PSEUDOSECTION:
; EDNS: version: 0, flags:; udp: 4096
; COOKIE: 16e9815d9ca2514d2f3879265b28bad05ff7b4a82721edd0 (good)
;; QUESTION SECTION:
;a-double.akka.test.            IN      A

;; ANSWER SECTION:
a-double.akka.test.     86400   IN      A       192.168.1.21
a-double.akka.test.     86400   IN      A       192.168.1.22

```

在这种情况下，`service.tcp.akka.test`解析为`a-single.akka.test`在端口`5060`和`a-double.akka.test`在端口`5070`。目前发现并不支持权重。

<a id="discovery-method-configuration"></a>
## 发现方法：配置

配置当前将忽略除服务名称之外的所有字段。

对于简单的用例，可以将配置用于服务发现。在配置中使用Akka发现而不是您自己的配置值的优点是，可以将应用程序迁移到更复杂的发现方法，而不需要进行任何代码更改。

在你的`application.conf`中配置它以用作您的发现方法

```
akka {
  discovery.method = config
}
```

默认情况下，可发现服务的定义在`akka.discovery.config.services`，并有如下格式：

```
akka.discovery.config.services = {
  service1 = {
    endpoints = [
      {
        host = "cat"
        port = 1233
      },
      {
        host = "dog"
        port = 1234
      }
    ]
  },
  service2 = {
    endpoints = []
  }
}
```

上面的块定义了两个服务，`service1`和`service2`。每个服务可以具有多个端点。

<a id="discovery-method-aggregate-multiple-discovery-methods"></a>
## 发现方法：聚合多种发现方法

聚合发现允许聚合多种发现方法，例如，通过DNS尝试并解决回退到配置。

要使用聚合发现，请添加它的依赖项以及希望聚合的所有发现。

配置`aggregate`为`akka.discovery.method`，尝试哪些发现方法，按什么顺序。

```
akka {
  discovery {
    method = aggregate
    aggregate {
      discovery-methods = ["akka-dns", "config"]
    }
    config {
      services {
        service1 {
          endpoints = [
            {
              host = "host1"
              port = 1233
            },
            {
              host = "host2"
              port = 1234
            }
          ]
        }
      }
    }
  }
}

```

上述配置将导致首先检查`akka-dns`，如果失败了或未返回给定服务名的目标，则`config`将查询，其中配置了一个名为`service1`的服务，它有两个主机`host1`和`host2`。

<a id="migrating-from-akka-management-discovery-before-1-0-0-"></a>
## 从Akka Management Discovery迁移(1.0.0之前)

Akka Discovery started out as a submodule of Akka Management, before 1.0.0 of Akka Management. Akka Discovery is not compatible with those versions of Akka Management Discovery.

At least version `1.0.0` of any Akka Management module should be used if also using Akka Discovery.

Migration steps:

* Any custom discovery method should now implement `akka.discovery.ServiceDiscovery`
* `discovery-method` now has to be a configuration location under `akka.discovery` with at minimum a property `class` specifying the fully qualified name of the implementation of `akka.discovery.ServiceDiscovery`.
  Previous versions allowed this to be a class name or a fully qualified config location e.g. `akka.discovery.kubernetes-api` rather than just `kubernetes-api`
