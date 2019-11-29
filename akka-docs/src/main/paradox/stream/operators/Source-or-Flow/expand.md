# expand

类似于`extrapolate`，但没有`initial`参数，而且也使用`Iterator`代替原来的元素，允许它被重写和/或过滤。

@ref[Backpressure aware operators](../index.md#backpressure-aware-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #expand }

@@@

## 描述

类似于`extrapolate`，但没有`initial`参数，而且也使用`Iterator`代替原来的元素，允许它被重写和/或过滤。

See @ref:[Understanding extrapolate and expand](../../stream-rate.md#understanding-extrapolate-and-expand) for more information
and examples.

## 响应流语义

@@@div { .callout }

**emits** when downstream stops backpressuring

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@

