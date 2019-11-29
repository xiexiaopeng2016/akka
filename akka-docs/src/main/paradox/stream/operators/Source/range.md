# Source.range

发出一个范围内的每个整数，具有一个可选项，用于采用大于1的步长。

@ref[Source operators](../index.md#source-operators)

## 依赖

@@dependency[sbt,Maven,Gradle] {
  group="com.typesafe.akka"
  artifact="akka-stream_$scala.binary_version$"
  version="$akka.version$"
}


## 描述

Emit each integer in a range, with an option to take bigger steps than 1. @scala[In Scala, use the `apply` method to generate a sequence of integers.]

## 响应流语义

@@@div { .callout }

**emits** when there is demand, the next value

**completes** when the end of the range has been reached

@@@

## 示例

Define the range of integers.

Java
:   @@snip [SourceDocExamples.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceDocExamples.java) { #range-imports #range }

Print out the stream of integers.

Java
:   @@snip [SourceDocExamples.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceDocExamples.java) { #run-range}
