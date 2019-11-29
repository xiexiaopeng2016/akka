# conflate

只要存在背压，就可以通过将传入元素和一个摘要传递到一个聚合函数中，来允许一个较慢的下行速度。

@ref[Backpressure aware operators](../index.md#backpressure-aware-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #conflate }

@@@

## 描述

只要存在背压，就可以通过将传入元素和一个摘要传递到一个聚合函数中，来允许一个较慢的下行速度。汇总值必须与传入元素的类型相同，例如输入数字的总和或平均值，如果聚合应该导致一个不同的类型`conflateWithSeed`可以使用:

## 示例

Scala
:   @@snip [SourceOrFlow.scala](/akka-docs/src/test/scala/docs/stream/operators/sourceorflow/Conflate.scala) { #conflate }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #conflate }

If downstream is slower the elements is conflated by summing them. This means that upstream can continue producing elements while downstream is applying backpressure. For example: downstream is backpressuring while 1, 10 and 100 arrives from upstream, then backpressure stops and the conflated 111 is emitted downstream.

## 响应流语义 

@@@div { .callout }

**emits** when downstream stops backpressuring and there is a conflated element available

**backpressures** when the aggregate function cannot keep up with incoming elements

**completes** when upstream completes

@@@

