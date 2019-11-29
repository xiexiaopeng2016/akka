# UnzipWith

Splits each element of input into multiple downstreams using a function

@ref[Fan-out operators](index.md#fan-out-operators)

## 签名

## 描述

Splits each element of input into multiple downstreams using a function

## 响应流语义

@@@div { .callout }

**emits** when all of the outputs stops backpressuring and there is an input element available

**backpressures** when any of the outputs backpressures

**completes** when upstream completes

@@@


