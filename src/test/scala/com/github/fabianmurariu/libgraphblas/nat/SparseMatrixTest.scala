package com.github.fabianmurariu.libgraphblas.nat

import java.io.{ByteArrayOutputStream, PrintStream}

import cats.effect.IO
import org.scalatest.{FlatSpec, Matchers}

class SparseMatrixTest extends FlatSpec with Matchers {

  val matrix = NativeMode.nonBlocking.flatMap { implicit nb =>
    BooleanSparseMatrix[IO](11, 11, Array(2, 3, 5, 9, 10), Array(1, 4, 7, 0, 10), Array(true, true, true, true, true))
  }

  "NativeMatrix boolean" should "have a valid constructor that releases references after usage" in {

    matrix.use { m =>
      (for {
        rows <- m.nrows
        cols <- m.ncols
        nvals <- m.nvals
      } yield (rows, cols, nvals))
    }.unsafeRunSync() shouldBe(11, 11, 5)

  }

  it should "correctly print a sparse bool matrix" in {

    matrix.use {
      m =>
        for {
          bs <- IO.pure(new ByteArrayOutputStream())
          out <- IO.pure(new PrintStream(bs))
          _ <- m.show(out)
          _ <- IO.delay {
            out.close()
            bs.flush()
            bs.close()
          }
        } yield new String(bs.toByteArray, "UTF-8").trim
    }.unsafeRunSync() shouldBe
      """#SparseMatrix[bool, shape: 11x11, nvals: 5]
        |    ┌           ┐
        |  0  ...........
        |  1  ...........
        |  2  .1.........
        |  3  ....1......
        |  4  ...........
        |  5  .......1...
        |  6  ...........
        |  7  ...........
        |  8  ...........
        |  9  1..........
        | 10  ..........1
        |    └           ┘""".stripMargin

  }

}
