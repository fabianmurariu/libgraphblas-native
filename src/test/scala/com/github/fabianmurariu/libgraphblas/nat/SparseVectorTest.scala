package com.github.fabianmurariu.libgraphblas.nat

import org.scalatest.{FlatSpec, FunSuite, Matchers}

class SparseVectorTest extends FlatSpec with Matchers {

  val vectorBool = NativeMode.blocking.flatMap { implicit nb =>
    BooleanSparseVector(12, Array(0, 1, 5, 7, 11))
  }

  "NativeSparseVector boolean" should "be usable and return size and number of vals" in {
    vectorBool.use { v =>
      for {
        size <- v.size
        nvals <- v.nvals
      } yield (size, nvals)
    }.unsafeRunSync() shouldBe(12, 5)
  }

}
