package com.github.fabianmurariu.libgraphblas.nat

import java.io.PrintStream
import java.nio.LongBuffer

import cats.effect.{IO, Resource, Sync}
import com.sun.jna.ptr.ByteByReference
import graphblas.GraphBLASLibrary
import graphblas.GraphBLASLibrary.GrB_Matrix_ByReference

import cats.implicits._

trait SparseMatrix[F[_], +T] {
  private[nat] def m: GrB_Matrix_ByReference
  private[nat] implicit def N: NativeMode

  def nrows(implicit F: Sync[F]): F[Long] = F.delay {
    val rowsByRef = LongBuffer.allocate(8)
    GrBCode.fromInt(N.g.GrB_Matrix_nrows(rowsByRef, m.getValue))
    rowsByRef.get
  }

  def ncols(implicit F: Sync[F]): F[Long] = F.delay {
    val colsByRef = LongBuffer.allocate(8)
    GrBCode.fromInt(N.g.GrB_Matrix_ncols(colsByRef, m.getValue))
    colsByRef.get
  }

  def nvals(implicit F: Sync[F]): F[Long] = F.delay {
    val nvalsByRef = LongBuffer.allocate(8)
    GrBCode.fromInt(N.g.GrB_Matrix_nvals(nvalsByRef, m.getValue))
    nvalsByRef.get
  }

  def shape(implicit F: Sync[F]): F[(Long, Long, Long)] = for {
    rows <- nrows
    cols <- ncols
    vals <- nvals
  } yield (rows, cols, vals)

  def show(out: PrintStream = Console.out)(implicit F: Sync[F]): F[Unit] =
    shape.map { case (rows: Long, cols: Long, nvals: Long) =>
      val maxLengthRows = rows.toString.length.toLong

      def printCorners(c1: Char, c2: Char): Unit = {
        val offset = 3
        for (i <- -maxLengthRows to rows + offset) {
          i match {
            case i if i == maxLengthRows => out.print(c1)
            case i if i == rows + offset => out.println(c2)
            case _ => out.print(' ')
          }
        }
      }

      out.println(s"#SparseMatrix[bool, shape: ${rows}x${cols}, nvals: ${nvals}]")
      printCorners('┌', '┐')
      for (i <- 0L until rows) {
        out.print(String.format(s" %${maxLengthRows}d  ", Long.box(i)))
        for (j <- 0L until cols) {
          val b = new ByteByReference()
          GrBCode.fromInt(N.g.GrB_Matrix_extractElement_BOOL(b, m.getValue, i, j)) match {
            case Success =>
              out.print(b.getValue)
            case _ =>
              out.print('.')
          }
        }
        out.print('\n')
      }

      printCorners('└', '┘')
      out.flush()
    }

}


private class BooleanSparseMatrix[F[_]](val m: GrB_Matrix_ByReference, val N: NativeMode) extends SparseMatrix[F, Boolean]

object BooleanSparseMatrix {

  def apply[F[_]](rows: Long, cols: Long, i: Array[Long], j: Array[Long], xs: Array[Boolean])
                 (implicit N: NativeMode, F: Sync[F]): Resource[F, SparseMatrix[F, Boolean]] = Resource.make(F.delay {
    require(i.length == j.length && j.length == xs.length, "all arrays need to be the same size")
    val grBM = new GraphBLASLibrary.GrB_Matrix_ByReference()
    GrBCode.fromInt(N.g.GrB_Matrix_new(grBM, N.g.GrB_BOOLm, rows, cols))
    GrBCode.fromInt(N.g.GrB_Matrix_build_BOOL(grBM.getValue, i, j, xs, i.length, N.g.GrBSecondBool()))
    new BooleanSparseMatrix[F](grBM, N).asInstanceOf[SparseMatrix[F, Boolean]]
  })(mat => F.delay(GrBCode.fromInt(N.g.GrB_Matrix_free(mat.m))))

}
