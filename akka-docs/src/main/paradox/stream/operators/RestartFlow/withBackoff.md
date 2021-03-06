# RestartFlow.withBackoff

Wrap the given @apidoc[Flow] with a @apidoc[Flow] that will restart it when it fails or complete using an exponential backoff.

@ref[Error handling](../index.md#error-handling)

@@@div { .group-scala }

## 签名

@@signature [RestartFlow.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/RestartFlow.scala) { #withBackoff }

@@@

## 描述

The resulting @apidoc[Flow] will not cancel, complete or emit a failure, until the opposite end of it has been cancelled or
completed. Any termination by the @apidoc[Flow] before that time will be handled by restarting it. Any termination
signals sent to this @apidoc[Flow] however will terminate the wrapped @apidoc[Flow], if it's running, and then the @apidoc[Flow]
will be allowed to terminate without being restarted.

The restart process is inherently lossy, since there is no coordination between cancelling and the sending of
messages. A termination signal from either end of the wrapped @apidoc[Flow] will cause the other end to be terminated,
and any in transit messages will be lost. During backoff, this @apidoc[Flow] will backpressure.

This uses the same exponential backoff algorithm as @apidoc[Backoff].

## 响应流语义

@@@div { .callout }

**emits** when the wrapped flow emits

**backpressures** during backoff and when the wrapped flow backpressures

**completes** when the wrapped flow completes

@@@
