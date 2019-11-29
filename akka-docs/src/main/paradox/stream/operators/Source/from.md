# @scala[apply]@java[from]

流动 @scala[`immutable.Seq`]@java[`Iterable`] 的值。

@ref[Source operators](../index.md#source-operators)


@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #apply }

@@@

## 描述

Stream the values of an @scala[`immutable.Seq`]@java[`Iterable`]. @java[Make sure the `Iterable` is immutable or at least not modified after being used
as a source. Otherwise the stream may fail with `ConcurrentModificationException` or other more subtle errors may occur.]

## 响应流语义

@@@div { .callout }

**emits** the next value of the seq

**completes** when the last element of the seq has been emitted

@@@


## 示例

Java
:  @@snip [from.java](/akka-docs/src/test/java/jdocs/stream/operators/SourceDocExamples.java) { #imports #source-from-example }
