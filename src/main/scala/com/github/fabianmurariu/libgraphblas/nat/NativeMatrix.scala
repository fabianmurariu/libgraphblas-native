package com.github.fabianmurariu.libgraphblas.nat

import java.nio.LongBuffer

import cats.Applicative
import cats.effect.{IO, Resource, Sync}
import com.sun.jna.ptr.LongByReference
import graphblas.GraphBLASLibrary
import graphblas.GraphBLASLibrary.GrB_Matrix_ByReference

trait NativeMatrix[F[_]] {

  import cats.implicits._

  trait NativeSparseMatrix[+T] {
    private[nat] val m: GrB_Matrix_ByReference
    private[nat] val N: NativeMode

    def nrows(implicit F: Sync[F]) = F.delay {
      val rowsByRef = LongBuffer.allocate(8)
      GrBCode.fromInt(N.g.GrB_Matrix_nrows(rowsByRef, m.getValue))
      rowsByRef.get
    }

    def ncols(implicit F: Sync[F]) = F.delay {
      val colsByRef = LongBuffer.allocate(8)
      GrBCode.fromInt(N.g.GrB_Matrix_ncols(colsByRef, m.getValue))
      colsByRef.get
    }

    def nvals(implicit F: Sync[F]) = F.delay {
      val nvalsByRef = LongBuffer.allocate(8)
      GrBCode.fromInt(N.g.GrB_Matrix_nvals(nvalsByRef, m.getValue))
      nvalsByRef.get
    }

    def shape(implicit F:Sync[F]) = for {
      rows <- nrows
      cols <- ncols
    } yield (rows, cols)
  }


  private class BooleanNativeSparseMatrix(val m: GrB_Matrix_ByReference, val N: NativeMode) extends NativeSparseMatrix[Boolean]

  object BooleanNativeSparseMatrix {

    def apply(rows: Long, cols: Long, i: Array[Long], j: Array[Long], xs: Array[Boolean])
             (implicit N: NativeMode, F: Sync[F]): Resource[F, NativeSparseMatrix[Boolean]] = Resource.make(F.delay {
      require(i.length == j.length && j.length == xs.length, "all arrays need to be the same size")
      val grBM = new GraphBLASLibrary.GrB_Matrix_ByReference()
      GrBCode.fromInt(N.g.GrB_Matrix_new(grBM, N.g.GrB_BOOLm, rows, cols))
      GrBCode.fromInt(N.g.GrB_Matrix_build_BOOL(grBM.getValue, i, j, xs, i.length, N.g.GrBSecondBool()))
      new BooleanNativeSparseMatrix(grBM, N).asInstanceOf[NativeSparseMatrix[Boolean]]
    })(mat => F.delay {
      GrBCode.fromInt(N.g.GrB_Matrix_free(mat.m))
    })

  }

}

object IONativeMatrix extends NativeMatrix[IO]
