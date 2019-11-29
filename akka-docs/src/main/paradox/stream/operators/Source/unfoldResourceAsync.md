# Source.unfoldResourceAsync

将任何可以打开、查询下一个元素(以阻塞方式)和使用三个不同的函数关闭的资源包装到一个源中。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #unfoldResourceAsync }

@@@

## 描述

Wrap any resource that can be opened, queried for next element (in a blocking way) and closed using three distinct functions into a source.
Functions return @scala[`Future`] @java[`CompletionStage`] to achieve asynchronous processing

## 响应流语义

@@@div { .callout }

**emits** when there is demand and @scala[`Future`] @java[`CompletionStage`] from read function returns value

**completes** when @scala[`Future`] @java[`CompletionStage`] from read function returns `None`

@@@

