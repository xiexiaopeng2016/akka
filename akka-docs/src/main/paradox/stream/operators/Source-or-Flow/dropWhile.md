# dropWhile

删除元素，只要一个谓词函数对元素返回true

@ref[Simple operators](../index.md#simple-operators)

@@@div { .group-scala }

## 签名

@@signature [Flow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Flow.scala) { #dropWhile }

@@@

## 描述

删除元素，只要一个谓词函数对元素返回true

## 响应流语义

@@@div { .callout }

**emits** when the predicate returned false and for all following stream elements

**backpressures** predicate returned false and downstream backpressures

**completes** when upstream completes

@@@

