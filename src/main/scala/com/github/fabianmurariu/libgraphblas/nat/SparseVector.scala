package com.github.fabianmurariu.libgraphblas.nat

import java.io.PrintStream
import java.nio.LongBuffer

import cats.effect.{Resource, Sync}
import graphblas.GraphBLASLibrary
import graphblas.GraphBLASLibrary.GrB_Vector_ByReference
import cats.implicits._
import com.sun.jna.ptr.ByteByReference

trait SparseVector[F[_], T] {
  self =>
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

  def show(out: PrintStream = Console.out)(implicit F: Sync[F]): F[Unit] = for {
    n <- nvals
    s <- size
    _ <- F.delay {
      out.println(s"#SparseVector[<TODO type>, size: $s, nvals: $n]")
      out.print("[")
      for (i <- 0L until s) {
        val b = new ByteByReference()
        GrBCode.fromInt(N.g.GrB_Vector_extractElement_BOOL(b, v.getValue, i)) match { //FIXME this works for bool but it's at a generic level
          case Success => out.print(b.getValue)
          case _ => out.print(".")
        }
      }
      out.println("]")
    }
  } yield ()

  def foldLeft(start: Boolean, m: Monoid[F, T], desc: Descriptor[F])(implicit F: Sync[F]): F[Unit] = F.delay {
    val b = new ByteByReference()
    if (start) b.setValue(1) else b.setValue(0)
    N.g.GrB_Vector_reduce_BOOL(b, null, m.m.getValue, self.v.getValue, desc.d.getValue)
  }

  def vxm[A](mask: SparseVector[F, A], s: Semiring[F, T], A: SparseMatrix[F, T], desc: Descriptor[F])(implicit F: Sync[F]): F[GrBCode] = F.delay {
    GrBCode.fromInt(N.g.GrB_vxm(self.v.getValue, mask.v.getValue, null, s.s.getValue, self.v.getValue, A.m.getValue, desc.d.getValue))
  }

}

private class Int32SparseVector[F[_]](val v: GrB_Vector_ByReference, val N: NativeMode) extends SparseVector[F, Int] {
  def assign(mask: SparseVector[F, Boolean], x: Int, n: Long)(implicit F: Sync[F]): F[GrBCode] = F.delay {
    GrBCode.fromInt(N.g.GrB_Vector_assign_INT32(v.getValue, mask.v.getValue, null, x, N.g.GrBALL(), n, null))(N)
  }
}

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
