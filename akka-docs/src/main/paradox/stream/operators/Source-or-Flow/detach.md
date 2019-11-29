# detach

从下游需求分离上游需求，而不分离流比率。

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #detach }

@@@

## 描述

从下游需求分离上游需求，而不分离流比率。

## 响应流语义

@@@div { .callout }

**emits** when the upstream operators has emitted and there is demand

**backpressures** when downstream backpressures

**completes** when upstream completes

@@@

