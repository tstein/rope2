# rope2
relativistic-oriented physics engine, reborn

## setup
First, you need this stuff:
* [IntelliJ IDEA](https://www.jetbrains.com/idea/download)
* scala 2.11
  * **Linux**: Ideally, you would get 2.11 from system package management. If your distro is still shipping an older version (like Fedora 23 is), download an `rpm`, `deb`, or `tgz` from [here](http://www.scala-lang.org/download/2.11.7.html).
  * **OS X**: Install [Homebrew](http://brew.sh/) if you don't have it, then `brew install scala`.
  * **Windows**: Get it from [scala-lang.org](http://www.scala-lang.org/download/2.11.7.html).
* java 1.7
  * **Linux**: Get it from system package management. If it gives you 1.8, no problem.
  * **OS X**, **Windows**: Get it directly from the [Great Satan](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).

Check out this repo and run IntelliJ, but **don't** tell IntelliJ to open the project yet. The [library definition](https://github.com/tstein/rope2/blob/master/.idea/libraries/scala_sdk_2_11_7.xml) for Scala contains a variable that IntelliJ will prompt you to define, but (as of version 15.1) it has a bug that will cause it to change that file and mangle the path before you get a chance to fix it. To set the variable:
* click the gear labeled `Configure` in the bottom-right and choose `Preferences`
* find `Path Variables` under `Appearance & Behavior`
* click the plus sign on the bottom of the list

The name of the variable is `SCALA_HOME`. IntelliJ is expecting to find `lib/scala-compiler.jar` in `$SCALA_HOME`. The values I used for the installation methods above are:
* **Linux**: `/usr/share/scala`
* **OS X**: `/usr/local/Cellar/scala/2.11.7/idea`
* **Windows**: `C:\Program Files (x86)\scala`

IntelliJ may prompt you to fix your JDK and will walk you through this. If you have Java 1.8, this will result in a change to `rope.iml` - please don't commit this. I'll try to get everyone on 1.8 at some point.

With this done, go back to the IntelliJ startup dialog, choose `Open`, and choose `rope.iml` in this repo. Once it finishes indexing the project, choose `Run` from the `Run` menu and pick `rope` or `rope (OS X)`. This will be your default in the future.

## problems and solutions
* `Exception in thread "main" java.lang.IllegalStateException: Please run the JVM with -XstartOnFirstThread`: You ran `rope` instead of `rope (OS X)`, but you are on OS X.
* `Unrecognized option: -XstartOnFirstThread`: You ran `rope (OS X)`, but you are not on OS X.
* `something something no compiler jar in something`: You opened the project before setting the path variable. The easiest way to fix this is to do a `git reset --hard` or, equivalently, delete rope2 and check it out anew. Path variables are an IDE setting, not a project setting, so `SCALA_HOME` will persist across projects.
