# keepAlive

Injects additional (configured) elements if upstream does not emit for a configured amount of time.

@ref[Time aware operators](../index.md#time-aware-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #keepAlive }

@@@

## 描述

Injects additional (configured) elements if upstream does not emit for a configured amount of time.

## 响应流语义

@@@div { .callout }

**emits** when upstream emits an element or if the upstream was idle for the configured period

**backpressures** when downstream backpressures

**completes** when upstream completes

**cancels** when downstream cancels

@@@

