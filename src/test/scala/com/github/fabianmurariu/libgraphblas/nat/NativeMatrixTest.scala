package com.github.fabianmurariu.libgraphblas.nat

import cats.Applicative
import cats.effect.{IO, Resource, Sync}
import org.scalatest.{FlatSpec, FunSuite, Matchers}

class NativeMatrixTest extends FlatSpec with Matchers {

  import IONativeMatrix._

  "NativeMatrix boolean" should "have a valid constructor that releases references after usage" in {

    val matrix = NativeMode.nonBlocking.flatMap(implicit nb => BooleanNativeSparseMatrix(5, 5, Array(2, 3), Array(1, 4), Array(true, true)))

    matrix.use { m =>
      (for {
        rows <- m.nrows
        cols <- m.ncols
        nvals <- m.nvals
      } yield (rows, cols, nvals)).map(println)
    }.unsafeRunSync()
  }

}
