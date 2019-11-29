# FileIO.fromPath

从给定路径发出一个文件的内容。

@ref[File IO Sinks and Sources](../index.md#file-io-sinks-and-sources)

@@@div { .group-scala }

## 签名

@@signature [FileIO.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/FileIO.scala) { #fromPath }

@@@

## 描述

Emits the contents of a file from the given path, as `ByteString`s, materializes into a @scala[`Future`] @java[`CompletionStage`] which will be completed with
a `IOResult` upon reaching the end of the file or if there is a failure.

## 示例

Scala
:  @@snip [StreamFileDocSpec.scala](/akka-docs/src/test/scala/docs/stream/io/StreamFileDocSpec.scala) { #file-source }

Java
:  @@snip [StreamFileDocTest.java](/akka-docs/src/test/java/jdocs/stream/io/StreamFileDocTest.java) { #file-source }

