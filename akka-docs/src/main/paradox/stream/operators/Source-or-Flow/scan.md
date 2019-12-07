# scan

发出它的当前值，它从`zero`开始，然后将当前值和下一个值应用于给定的函数，发出下一个当前值。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #scan }

@@@

## 描述

发出它的当前值，它从`zero`开始，然后将当前值和下一个值应用于给定的函数，发出下一个当前值。这意味着`scan`会首先向下游发出一个元素，并且上游元素将不会被请求，直到下游需要第二个元素。

注意`zero`值必须是不可变的。

## 响应流语义

@@@div { .callout }

**emits** 当函数扫描元素时返回一个新元素

**backpressures** 当下游背压

**completes** 当上游完成

@@@

## 示例

Scala
:  @@snip [SourceOrFlow.scala](/akka-docs/src/test/scala/docs/stream/operators/sourceorflow/Scan.scala) { #scan }

Java
:  @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #scan }
