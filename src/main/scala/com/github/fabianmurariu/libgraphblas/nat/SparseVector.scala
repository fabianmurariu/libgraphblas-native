package com.github.fabianmurariu.libgraphblas.nat

import java.nio.LongBuffer

import cats.effect.{Resource, Sync}
import graphblas.GraphBLASLibrary
import graphblas.GraphBLASLibrary.GrB_Vector_ByReference

trait SparseVector[F[_], T] {
  private[nat] def v: GrB_Vector_ByReference
  private[nat] implicit def N: NativeMode

  def nvals(implicit F: Sync[F]): F[Long] = F.delay {
    val nvalsByRef = LongBuffer.allocate(8)
    GrBCode.fromInt(N.g.GrB_Vector_nvals(nvalsByRef, v.getValue))
    nvalsByRef.get()
  }

  def size(implicit F: Sync[F]): F[Long] = F.delay {
    val nvalsByRef = LongBuffer.allocate(8)
    GrBCode.fromInt(N.g.GrB_Vector_size(nvalsByRef, v.getValue))
    nvalsByRef.get()
  }
}

private class Int32SparseVector[F[_]](val v: GrB_Vector_ByReference, val N: NativeMode) extends SparseVector[F, Int]

object Int32SparseVector {
  def apply[F[_]](size: Long, i: Array[Long], xs: Array[Int])
                 (implicit N: NativeMode, F: Sync[F]): Resource[F, SparseVector[F, Int]] = {
    require(i.length == xs.length, "All arrays need to be the same size")
    Resource.make(F.delay {
      val v = new GraphBLASLibrary.GrB_Vector_ByReference()
      GrBCode.fromInt(N.g.GrB_Vector_new(v, N.g.GrB_INT32m(), size))
      GrBCode.fromInt(N.g.GrB_Vector_build_INT32(v.getValue, i, xs, i.length, N.g.GrBSecondBool()))
      new Int32SparseVector[F](v, N).asInstanceOf[SparseVector[F, Int]]
    })(vec => F.delay(GrBCode.fromInt(N.g.GrB_Vector_free(vec.v))))
  }
}

private class BooleanSparseVector[F[_]](val v: GrB_Vector_ByReference, val N: NativeMode) extends SparseVector[F, Boolean]

object BooleanSparseVector {
  def apply[F[_]](size: Long, i: Array[Long])
                 (implicit N: NativeMode, F: Sync[F]): Resource[F, SparseVector[F, Boolean]] = {
    Resource.make(F.delay {
      val v = new GraphBLASLibrary.GrB_Vector_ByReference()
      GrBCode.fromInt(N.g.GrB_Vector_new(v, N.g.GrB_BOOLm(), size))
      GrBCode.fromInt(N.g.GrB_Vector_build_BOOL(v.getValue, i, Array.fill[Boolean](i.length)(true), i.length, N.g.GrBSecondBool()))
      new BooleanSparseVector[F](v, N).asInstanceOf[SparseVector[F, Boolean]]
    })(vec => F.delay(GrBCode.fromInt(N.g.GrB_Vector_free(vec.v))))
  }
}
