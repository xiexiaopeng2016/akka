# Source.single

流动一个单个对象

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #single }

@@@

## 描述

流动一个单个对象

## 响应流语义

@@@div { .callout }

**emits** the value once

**completes** when the single value has been emitted

@@@

## 示例

Scala
:  @@snip [source.scala](/akka-stream-tests/src/test/scala/akka/stream/scaladsl/SourceSpec.scala) { #imports #source-single }

Java
:   @@snip [source.java](/akka-stream-tests/src/test/java/akka/stream/javadsl/SourceTest.java) { #imports #source-single }


