# Unzip

Takes a stream of two element tuples and unzips the two elements ino two different downstreams.

@ref[Fan-out operators](index.md#fan-out-operators)

## 签名

## 描述

Takes a stream of two element tuples and unzips the two elements ino two different downstreams.

## 响应流语义

@@@div { .callout }

**emits** when all of the outputs stops backpressuring and there is an input element available

**backpressures** when any of the outputs backpressures

**completes** when upstream completes

@@@

