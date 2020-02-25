<a id="default-configuration"></a>
# 默认配置

每个Akka模块都有一个`reference.conf`文件，带有默认值。

在`application.conf`中进行编辑/覆盖。如果您不确定其含义，不要覆盖默认值。[Akka配置检查器](https://doc.akka.io/docs/akka-enhancements/current/config-checker.html)
是查找潜在配置问题的有用工具。

`reference.conf`文件的用途是让库，比如Akka，定义默认值，如果一个应用程序未定义更具体的值，则使用默认值。它也是记录配置属性的存在和意义的好地方。一个库不能试图在自己的`reference.conf`中重写原本定义在另一个库的`reference.conf`中的属性，因为在加载配置时，有效值是不确定的。

<a id="config-akka-actor"></a>
### akka-actor

@@snip [reference.conf](/akka-actor/src/main/resources/reference.conf)

<a id="config-akka-actor-typed"></a>
### akka-actor-typed

@@snip [reference.conf](/akka-actor-typed/src/main/resources/reference.conf)

<a id="config-akka-cluster-typed"></a>
### akka-cluster-typed

@@snip [reference.conf](/akka-cluster-typed/src/main/resources/reference.conf)

<a id="config-akka-cluster"></a>
### akka-cluster

@@snip [reference.conf](/akka-cluster/src/main/resources/reference.conf)

<a id="config-akka-discovery"></a>
### akka-discovery

@@snip [reference.conf](/akka-discovery/src/main/resources/reference.conf)

<a id="config-akka-coordination"></a>
### akka-coordination

@@snip [reference.conf](/akka-coordination/src/main/resources/reference.conf)

<a id="config-akka-multi-node-testkit"></a>
### akka-multi-node-testkit

@@snip [reference.conf](/akka-multi-node-testkit/src/main/resources/reference.conf)

<a id="config-akka-persistence-typed"></a>
### akka-persistence-typed

@@snip [reference.conf](/akka-persistence-typed/src/main/resources/reference.conf)

<a id="config-akka-persistence"></a>
### akka-persistence

@@snip [reference.conf](/akka-persistence/src/main/resources/reference.conf)

<a id="config-akka-persistence-query"></a>
### akka-persistence-query

@@snip [reference.conf](/akka-persistence-query/src/main/resources/reference.conf)

<a id="config-akka-remote-artery"></a>
### akka-remote artery

@@snip [reference.conf](/akka-remote/src/main/resources/reference.conf) { #shared #artery type=none }

<a id="config-akka-remote"></a>
### akka-remote classic (deprecated)

@@snip [reference.conf](/akka-remote/src/main/resources/reference.conf) { #shared #classic type=none }

<a id="config-akka-testkit"></a>
### akka-testkit

@@snip [reference.conf](/akka-testkit/src/main/resources/reference.conf)

<a id="config-cluster-metrics"></a>
### akka-cluster-metrics

@@snip [reference.conf](/akka-cluster-metrics/src/main/resources/reference.conf)

<a id="config-cluster-tools"></a>
### akka-cluster-tools

@@snip [reference.conf](/akka-cluster-tools/src/main/resources/reference.conf)

<a id="config-cluster-sharding-typed"></a>
### akka-cluster-sharding-typed

@@snip [reference.conf](/akka-cluster-sharding-typed/src/main/resources/reference.conf)

<a id="config-cluster-sharding"></a>
### akka-cluster-sharding

@@snip [reference.conf](/akka-cluster-sharding/src/main/resources/reference.conf)

<a id="config-distributed-data"></a>
### akka-distributed-data

@@snip [reference.conf](/akka-distributed-data/src/main/resources/reference.conf)

<a id="config-akka-stream"></a>
### akka-stream

@@snip [reference.conf](/akka-stream/src/main/resources/reference.conf)

<a id="config-akka-stream-testkit"></a>
### akka-stream-testkit

@@snip [reference.conf](/akka-stream-testkit/src/main/resources/reference.conf)

