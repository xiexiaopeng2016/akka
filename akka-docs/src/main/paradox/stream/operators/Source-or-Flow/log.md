# log

日志记录通过流的元素以及完成和错误。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #log }

@@@

## 描述

日志记录通过流的元素以及完成和错误。默认情况下，元素和完成信号记录在调试级别，错误记录在错误级别。这可以通过在给定流上调用 @scala[`Attributes.logLevels(...)`] @java[`Attributes.createLogLevels(...)`] 来修改。

## 示例

Scala
:   @@snip [SourceOrFlow.scala](/akka-docs/src/test/scala/docs/stream/operators/sourceorflow/Log.scala) { #log }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #log }

## 响应流语义 

@@@div { .callout }

**emits** when upstream emits

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@
