# conflateWithSeed

只要存在背压，就可以通过将传入元素和一个摘要传递到一个聚合函数中，来允许一个较慢的下行速度。

@ref[Backpressure aware operators](../index.md#backpressure-aware-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #conflateWithSeed }

@@@

## 描述

只要存在背压，就可以通过将传入元素和一个摘要传递到一个聚合函数中，来允许一个较慢的下行速度。当背压启动或没有背压元素被传递到`seed`函数中以将其转换为摘要类型。

## 示例

Scala
:   @@snip [SourceOrFlow.scala](/akka-docs/src/test/scala/docs/stream/operators/sourceorflow/Conflate.scala) { #conflateWithSeed }

Java
:   @@snip [SourceOrFlow.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceOrFlow.java) { #conflateWithSeed-type #conflateWithSeed }


If downstream is slower, the "seed" function is called which is able to change the type of the to be conflated
elements if needed (it can also be an identity function, in which case this `conflateWithSeed` is equivalent to 
a plain `conflate`). Next, the conflating function is applied while there is back-pressure from the downstream,
such that the upstream can produce elements at an rate independent of the downstream.

You may want to use this operation for example to apply an average operation on the upstream elements,
while the downstream backpressures. This allows us to keep processing upstream elements, and give an average
number to the downstream once it is ready to process the next one.

## 响应流语义 

@@@div { .callout }

**emits** when downstream stops backpressuring and there is a conflated element available

**backpressures** when the aggregate or seed functions cannot keep up with incoming elements

**completes** when upstream completes

@@@


