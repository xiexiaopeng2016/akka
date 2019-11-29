# cycle

Stream iterator in cycled manner.

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #cycle }

@@@

## 描述

Stream iterator in cycled manner. Internally a new iterator is being created to cycle the one provided via argument meaning
when the original iterator runs out of elements to process it will start all over again from the beginning of the iterator
provided by the evaluation of provided parameter. If the method argument provides an empty iterator the stream will be 
terminated with an exception.

## 响应流语义

@@@div { .callout }

**emits** the next value returned from cycled iterator

**completes** never

@@@


## 示例

Scala
:  @@snip [cycle.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/SourceSpec.scala) { #cycle }

Java
:  @@snip [cycle.java](/akka-stream-tests/src/test/java/akka/stream/javadsl/SourceTest.java) { #cycle }


When iterator is empty the stream will be terminated with _IllegalArgumentException_

Scala
:  @@snip [cycleError.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/SourceSpec.scala) { #cycle-error }

Java
:  @@snip [cycle.java](/akka-stream-tests/src/test/java/akka/stream/javadsl/SourceTest.java) { #cycle-error }
