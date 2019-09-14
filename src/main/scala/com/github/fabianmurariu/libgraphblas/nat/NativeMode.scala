package com.github.fabianmurariu.libgraphblas.nat

import cats.Applicative
import cats.effect.{IO, Resource}
import graphblas.GraphBLASLibrary

sealed trait NativeMode {
  val g: GraphBLASLibrary
}

class NonBlocking(val g: GraphBLASLibrary) extends NativeMode

class Blocking(val g: GraphBLASLibrary) extends NativeMode

object NativeMode {
  val blocking: Resource[IO, Blocking] = makeLibrary[IO](GraphBLASLibrary.GrB_Mode.GrB_BLOCKING).map(_.asInstanceOf[Blocking])
  val nonBlocking: Resource[IO, NonBlocking] = makeLibrary[IO](GraphBLASLibrary.GrB_Mode.GrB_NONBLOCKING).map(_.asInstanceOf[NonBlocking])

  private def makeLibrary[F[_]](mode: Int)(implicit F: Applicative[F]): Resource[F, NativeMode] = Resource.make(F.pure {
    val g = GraphBLASLibrary.INSTANCE
    mode match {
      case 1 =>
        g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING)
        new Blocking(g)
      case _ =>
        g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_NONBLOCKING)
        new NonBlocking(g)
    }
  })(mode => F.pure {
    GrBCode.fromInt(mode.g.GrB_wait())(mode)
    GrBCode.fromInt(mode.g.GrB_finalize())(mode)
  })
}
