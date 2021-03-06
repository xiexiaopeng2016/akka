# FileIO.toPath

创建一个接收器，它将把传入的`ByteString`写入给定的文件路径。

@ref[File IO Sinks and Sources](../index.md#file-io-sinks-and-sources)

@@@div { .group-scala }

## 签名

@@signature [FileIO.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/FileIO.scala) { #toPath }

@@@

## 描述

Creates a Sink which writes incoming `ByteString` elements to the given file path. Overwrites existing files by truncating their contents as default. 
Materializes a @scala[`Future`] @java[`CompletionStage`] of `IOResult` that will be completed with the size of the file (in bytes) at the streams completion, and a possible exception if IO operation was not completed successfully.

## 示例

Scala
:  @@snip [StreamFileDocSpec.scala](/akka-docs/src/test/scala/docs/stream/io/StreamFileDocSpec.scala) { #file-sink }

Java
:  @@snip [StreamFileDocTest.java](/akka-docs/src/test/java/jdocs/stream/io/StreamFileDocTest.java) { #file-sink }
