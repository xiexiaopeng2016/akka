# lazyCompletionStage

将一个单个元素的源的future的创建推迟到有需求的时候。

@ref[Source operators](../index.md#source-operators)

## 描述

Invokes the user supplied factory when the first downstream demand arrives. When the returned future completes 
successfully the value is emitted downstream as a single stream element. If the future or the factory fails the 
stream is failed.

Note that asynchronous boundaries (and other operators) in the stream may do pre-fetching which counter acts
the laziness and will trigger the factory immediately.

## 响应流语义

@@@div { .callout }

**emits** when there is downstream demand and the element factory returned future has completed

**completes** after emitting the single element

@@@

