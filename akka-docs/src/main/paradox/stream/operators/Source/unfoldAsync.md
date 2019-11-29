# Source.unfoldAsync

与`unfold`类似，但是fold函数返回一个 @scala[`Future`]@java[`CompletionStage`]。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #unfoldAsync }

@@@

## 描述

Just like `unfold` but the fold function returns a @scala[`Future`] @java[`CompletionStage`] which will cause the source to
complete or emit when it completes.

Can be used to implement many stateful sources without having to touch the more low level @ref[`GraphStage`](../../stream-customize.md) API.

## 响应流语义

@@@div { .callout }

**emits** when there is demand and unfold state returned future completes with some value

**completes** when the @scala[future] @java[CompletionStage] returned by the unfold function completes with an empty value

@@@

