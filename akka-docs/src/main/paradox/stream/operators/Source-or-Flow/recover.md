# recover

Allow sending of one last element downstream when a failure has happened upstream.

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #recover }

@@@

## 描述

Allow sending of one last element downstream when a failure has happened upstream.

Throwing an exception inside `recover` _will_ be logged on ERROR level automatically.

## 响应流语义

@@@div { .callout }

**emits** when the element is available from the upstream or upstream is failed and pf returns an element

**backpressures** when downstream backpressures, not when failure happened

**completes** when upstream completes or upstream failed with exception pf can handle

@@@

