# FileIO.fromFile

发出一个文件的内容。

@ref[File IO Sinks and Sources](../index.md#file-io-sinks-and-sources)

@@@ warning

The `fromFile` operator has been deprecated, use @ref:[fromPath](./fromPath.md) instead. 

@@@

@@@div { .group-scala }

## 签名

@@signature [FileIO.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/FileIO.scala) { #fromFile }

@@@

## 描述

Emits the contents of a file, as `ByteString`s, materializes into a @scala[`Future`] @java[`CompletionStage`] which will be completed with
a `IOResult` upon reaching the end of the file or if there is a failure.

