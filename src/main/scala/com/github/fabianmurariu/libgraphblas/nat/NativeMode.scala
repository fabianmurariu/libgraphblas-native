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

  object blocking {
    implicit val native: NativeMode = {
      val g = GraphBLASLibrary.INSTANCE
      g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_BLOCKING)

      Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
        override def run(): Unit = {
          g.GrB_wait()
          g.GrB_finalize()
        }
      }))


      new Blocking(g)
    }

  }

  object nonblocking {
    implicit val native: NativeMode = {
      val g = GraphBLASLibrary.INSTANCE
      g.GrB_init(GraphBLASLibrary.GrB_Mode.GrB_NONBLOCKING)

      Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
        override def run(): Unit = {
          g.GrB_wait()
          g.GrB_finalize()
        }
      }))

      new NonBlocking(g)
    }
  }

}
