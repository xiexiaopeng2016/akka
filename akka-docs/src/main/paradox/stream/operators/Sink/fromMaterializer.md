# Sink.fromMaterializer

将一个`Sink`的创建推迟，直到物化和访问`Materializer`和`Attributes`

@ref[Sink operators](../index.md#sink-operators)

@@@ div { .group-scala }

## 签名

@@signature [Sink.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Sink.scala) { #fromMaterializer }
@@@

## 描述

通常用在构建接收器期间需要访问一个物化器来运行不同的流时。也可以用来从`Materializer`访问底层的`ActorSystem`。
