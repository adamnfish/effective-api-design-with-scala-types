Effective API design with Scala types
=====================================

This repository contains resources that support my talk on API design.

## The talk

You can see
[the full talk](https://skillsmatter.com/skillscasts/5834-effective-api-design-with-scala-types)
on the Skills Matter website. You'll need to register to view the
video, but it is completely free. This talk was given at the Scala
eXchange in London, on 8th December, 2014.

There are
[more details about the talk](http://www.adamnfish.com/post/scalax-typed-api-design)
on my website.

## This repository

This repo contains more complete versions of the source code examples
that I used in the talk. Note that while this repo does compile, these
are only examples. Hopefully they'll be helpful if you are trying to
take a similar approach in your own work but do be aware that your
requirements will be different. Think about your own types rather than
simply copying these ones.

The source code is a simple [Play](https://www.playframework.com/)
application. The source code is in the `app` subdirectory and is split
into 3 main parts.

### Simple

This package contains `SimpleController.scala`, which gives the
example of how an API might traditionally be written.

### Sync

This package contains the version that has been converted to use
`Either` as the fundamental type for the API. The `Either` type's
behaviour takes care of our error handling so you'll see that the
controller (`SyncController.scala`) is enormously simplified.

### Async

The `async` package contains the fully asynchronous implementation
that I show at the end of the talk. This includes a custom
`ApiResponse` type that wraps both `Either` and `Future` into a single
type that can be used in for-comprehensions. The controller
(`AsyncController.scala`) is very nearly identical to the synchronous
version but we've managed to make the implementation non-blocking.

### Models, etc

The `models` package contains a few type that are shared among the
other packages.

The rest of the application is just a standard Play project.
