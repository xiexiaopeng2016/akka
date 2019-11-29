# failed

使用用户指定的异常直接失败。

@ref[Source operators](../index.md#source-operators)

@@@div { .group-scala }

## 签名

@@signature [Source.scala](/akka-stream/src/main/scala/akka/stream/scaladsl/Source.scala) { #failed }

@@@

## 描述

使用用户指定的异常直接失败。

<a id="reactive-streams-semantics"></a>
## 响应流语义

@@@div { .callout }

**emits** 从不

**completes** 使用给定的异常直接使流失败

@@@


[info] Assembly up to date: /home/odoo/code/akka/akka-protobuf-v3/target/akka-protobuf-v3-assembly-2.6-SNAPSHOT.jar
[error] java.lang.RuntimeException: Unable to extract details from /home/odoo/code/akka/akka-docs/src/main/paradox/stream/operators/Source/empty.md
[error] 	at StreamOperatorsIndexGenerator$.getDetails(StreamOperatorsIndexGenerator.scala:264)
[error] 	at StreamOperatorsIndexGenerator$.$anonfun$generateAlphabeticalIndex$13(StreamOperatorsIndexGenerator.scala:200)
[error] 	at scala.collection.immutable.List.map(List.scala:290)
[error] 	at StreamOperatorsIndexGenerator$.$anonfun$generateAlphabeticalIndex$1(StreamOperatorsIndexGenerator.scala:198)
[error] 	at scala.Function1.$anonfun$compose$1(Function1.scala:49)
[error] 	at sbt.internal.util.$tilde$greater.$anonfun$$u2219$1(TypeFunctions.scala:62)
[error] 	at sbt.std.Transform$$anon$4.work(Transform.scala:67)
[error] 	at sbt.Execute.$anonfun$submit$2(Execute.scala:281)
[error] 	at sbt.internal.util.ErrorHandling$.wideConvert(ErrorHandling.scala:19)
[error] 	at sbt.Execute.work(Execute.scala:290)
[error] 	at sbt.Execute.$anonfun$submit$1(Execute.scala:281)
[error] 	at sbt.ConcurrentRestrictions$$anon$4.$anonfun$submitValid$1(ConcurrentRestrictions.scala:178)
[error] 	at sbt.CompletionService$$anon$2.call(CompletionService.scala:37)
[error] 	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
[error] 	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
[error] 	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
[error] 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
[error] 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
[error] 	at java.lang.Thread.run(Thread.java:748)
[error] Caused by: java.lang.IllegalArgumentException: requirement failed: category link in /home/odoo/code/akka/akka-docs/src/main/paradox/stream/operators/Source/empty.md should start with @ref, but saw ""
[error] 	at scala.Predef$.require(Predef.scala:281)
[error] 	at StreamOperatorsIndexGenerator$.getDetails(StreamOperatorsIndexGenerator.scala:256)
[error] 	at StreamOperatorsIndexGenerator$.$anonfun$generateAlphabeticalIndex$13(StreamOperatorsIndexGenerator.scala:200)
[error] 	at scala.collection.immutable.List.map(List.scala:290)
[error] 	at StreamOperatorsIndexGenerator$.$anonfun$generateAlphabeticalIndex$1(StreamOperatorsIndexGenerator.scala:198)
[error] 	at scala.Function1.$anonfun$compose$1(Function1.scala:49)
[error] 	at sbt.internal.util.$tilde$greater.$anonfun$$u2219$1(TypeFunctions.scala:62)
[error] 	at sbt.std.Transform$$anon$4.work(Transform.scala:67)
[error] 	at sbt.Execute.$anonfun$submit$2(Execute.scala:281)
[error] 	at sbt.internal.util.ErrorHandling$.wideConvert(ErrorHandling.scala:19)
[error] 	at sbt.Execute.work(Execute.scala:290)
[error] 	at sbt.Execute.$anonfun$submit$1(Execute.scala:281)
[error] 	at sbt.ConcurrentRestrictions$$anon$4.$anonfun$submitValid$1(ConcurrentRestrictions.scala:178)
[error] 	at sbt.CompletionService$$anon$2.call(CompletionService.scala:37)
[error] 	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
[error] 	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
[error] 	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
[error] 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
[error] 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
[error] 	at java.lang.Thread.run(Thread.java:748)
[error] (akka-docs / Compile / managedResources) Unable to extract details from /home/odoo/code/akka/akka-docs/src/main/paradox/stream/operators/Source/empty.md
[error] Total time: 28 s, completed Nov 28, 2019 6:08:02 PM
odoo@ubuntu:~/code/akka$
