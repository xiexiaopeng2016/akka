# lazyCompletionStageSource

将一个future源的创建推迟到有需求的时候。

@ref[Source operators](../index.md#source-operators)

## 描述

Invokes the user supplied factory when the first downstream demand arrives. When the returned `CompletionStage` completes 
successfully the source switches over to the new source and emits downstream just like if it had been created up front. If the future or the factory fails the 
stream is failed.

Note that asynchronous boundaries (and other operators) in the stream may do pre-fetching which counter acts
the laziness and will trigger the factory immediately.

See also @ref:[lazySource](lazySource.md).

## 响应流语义

@@@div { .callout }

**emits** depends on the wrapped `Source`

**completes** depends on the wrapped `Source`

@@@

