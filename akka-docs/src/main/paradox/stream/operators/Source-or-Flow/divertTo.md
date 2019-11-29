# divertTo

根据应用于元素的谓词函数，每个上游元素要么被转移到给定的接收器，要么被转移到下游消费者。

@ref[Fan-out operators](../index.md#fan-out-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #divertTo }

@@@

## 描述

根据应用于元素的谓词函数，每个上游元素要么被转移到给定的接收器，要么被转移到下游消费者。

## 响应流语义

@@@div { .callout }

**emits** when the chosen output stops backpressuring and there is an input element available

**backpressures** when the chosen output backpressures

**completes** when upstream completes and no output is pending

**cancels** when any of the downstreams cancel

@@@

