# Source.unfoldResource

将任何可以打开、查询下一个元素(以阻塞方式)和使用三个不同的函数关闭的资源包装到一个源中。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #unfoldResource }

@@@

## 描述

`Source.unfoldResource` allows us to safely extract stream elements from blocking resources by providing it with three functions: 

1. `create`: Open or create the resource
1. `read`: Fetch the next element or signal that we reached the end of the stream by returning a @java[`Optional.empty`]@scala[`None`]
1. `close`: Close the resource, invoked on end of stream or if the stream fails

The functions are by default called on Akka's dispatcher for blocking IO to avoid interfering with other stream operations. 
See @ref:[Blocking Needs Careful Management](../../../typed/dispatchers.md#blocking-needs-careful-management) for an explanation on why this is important.

Note that there are pre-built `unfoldResource`-like operators to wrap `java.io.InputStream`s in 
@ref:[Additional Sink and Source converters](../index.md#additional-sink-and-source-converters), 
`Iterator` in @ref:[fromIterator](fromIterator.md) and File IO in @ref:[File IO Sinks and Sources](../index.md#file-io-sinks-and-sources).
Additional prebuilt technology specific connectors can also be found in the [Alpakka project](https://doc.akka.io/docs/alpakka/current/).

## 示例

Imagine we have a database API which may potentially block both when we initially perform a query and 
on retrieving each result from the query. It also gives us an iterator like way to determine if we have reached
the end of the result and a close method that must be called to free resources:

Scala
:   @@snip [UnfoldResource.scala](/akka-docs/src/test/scala/docs/stream/operators/source/UnfoldResource.scala) { #unfoldResource-blocking-api }

Java
:   @@snip [UnfoldResource.java](/akka-docs/src/test/java/jdocs/stream/operators/source/UnfoldResource.java) { #unfoldResource-blocking-api }

Let's see how we use the API above safely through `unfoldResource`:

Scala
:   @@snip [UnfoldResource.scala](/akka-docs/src/test/scala/docs/stream/operators/source/UnfoldResource.scala) { #unfoldResource }

Java
:   @@snip [UnfoldResource.java](/akka-docs/src/test/java/jdocs/stream/operators/source/UnfoldResource.java) { #unfoldResource }

If the resource produces more than one element at a time, combining `unfoldResource` with 
@scala[`mapConcat(identity)`]@java[`mapConcat(elems -> elems)`] will give you a stream of individual elements.
See @ref:[mapConcat](../Source-or-Flow/mapConcat.md)) for details.

## 响应流语义

@@@div { .callout }

**emits** when there is demand and the `read` function returns a value

**completes** when the `read` function returns @scala[`None`]@java[an empty `Optional`]

@@@
