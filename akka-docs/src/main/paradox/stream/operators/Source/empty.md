# empty

立即完成，而不发出任何元素。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #empty }

@@@

## 描述

立即完成，而不发出任何元素。当您必须为API提供一个源，但是没有要发出的元素时，它非常有用。

<a id="reactive-streams-semantics"></a>
## 响应流语义

@@@div { .callout }

**emits** 从不

**completes** 直接

@@@

