package com.github.fabianmurariu.libgraphblas.nat

import java.io.{ByteArrayOutputStream, PrintStream}

import cats.effect.IO
import org.scalatest.{FlatSpec, FunSuite, Matchers}
import NativeMode.nonblocking.native

class SparseVectorTest extends FlatSpec with Matchers {

  val vectorBool = BooleanSparseVector[IO](12, Array[Long](0, 1, 5, 7, 11))

  "NativeSparseVector boolean" should "be usable and return size and number of vals" in {
    vectorBool.use { v =>
      for {
        size <- v.size
        nvals <- v.nvals
      } yield (size, nvals)
    }.unsafeRunSync() shouldBe(12, 5)
  }

  it should "print the vector" in {
    vectorBool.use(
      sv =>
        for {
          bs <- IO.pure(new ByteArrayOutputStream())
          out <- IO.pure(new PrintStream(bs))
          _ <- sv.show(out)
          _ <- IO.delay {
            out.close()
            bs.flush()
            bs.close()
          }
        } yield new String(bs.toByteArray, "UTF-8").trim
    ).unsafeRunSync() shouldBe
      """#SparseVector[<TODO type>, size: 12, nvals: 5]
        |[11...1.1...1]""".stripMargin
  }

  it should "extract the dense vector" in {
    vectorBool.use(_.dense).unsafeRunSync().toVector shouldBe Vector(true, true, false, false, false, true, false, true, false, false, false, true)
  }

}
