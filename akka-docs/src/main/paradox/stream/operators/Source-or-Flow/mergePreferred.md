# mergePreferred

Merge multiple sources.

@ref[Fan-in operators](../index.md#fan-in-operators)

## 签名

## 描述

Merge multiple sources. Prefer one source if all sources have elements ready.

## 响应流语义

@@@div { .callout }

**emits** when one of the inputs has an element available, preferring a defined input if multiple have elements available

**backpressures** when downstream backpressures

**completes** when all upstreams complete (This behavior is changeable to completing when any upstream completes by setting `eagerComplete=true`.)

@@@

