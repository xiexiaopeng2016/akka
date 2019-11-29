# Source.repeat

重复流动一个对象

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #repeat }

@@@

## 描述

重复流动一个对象

## 响应流语义

@@@div { .callout }

**emits** the same value repeatedly when there is demand

**completes** never

@@@

