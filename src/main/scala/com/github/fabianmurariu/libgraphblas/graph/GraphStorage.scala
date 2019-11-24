package com.github.fabianmurariu.libgraphblas.graph

import cats.effect.{Resource, Sync}
import com.github.fabianmurariu.libgraphblas.nat.{BooleanMonoid, BooleanSparseMatrix, BooleanSparseVector, Descriptor, GrBCode, Int32SparseVector, Mask, Monoid, NativeMode, Output, Replace, Semiring, SparseMatrix, StructComplement}
import cats.implicits._

/**
 * Mutable Native support for sparse adjacency matrix
 * @param edges
 *              the grapblblas sparse matrix
 * @param sync$F$0
 *
 * @param N
 *          Native calls
 * @tparam F
 *           Effect
 */
class GraphStorage[F[_] : Sync](edges: SparseMatrix[F, Boolean])(implicit N: NativeMode) {

  def directed[V](e: (V, V)*)(implicit I: Id[V]): F[GrBCode] =
    edges.set(e.iterator.map { case (v1, v2) =>
      (I.code(v1) -> I.code(v2))
    })

  def undirected[V, E](e: (V, V)*)(implicit I: Id[V]): F[GrBCode] = {
    edges.set(e.iterator.flatMap { case (v1, v2) =>
      Set(I.code(v1) -> I.code(v2), I.code(v2) -> I.code(v1))
    })
  }

  /**
   * Naive implementation of neighbours with no filter on edges
   * @param start
   * @param I
   * @tparam V
   * @return
   */
  def neighbours[V](start: V)(implicit I: Id[V]): F[Array[Int]] = {
    val F = implicitly[Sync[F]]
    (for {
      v <- Int32SparseVector(edges.nrows)
      q <- BooleanSparseVector(edges.nrows)
      lor <- BooleanMonoid.LOR[F]
      booleanSemiring <- Semiring.LAND(lor)
      desc <- Descriptor(Mask -> StructComplement, Output -> Replace)
    } yield (v, q, booleanSemiring, lor, desc)).use { case (v, q, booleanSemiring, lor, desc) =>
      for {
        _ <- q.set(true, I.code(start))
        n <- edges.nrows
        _ <- v.assign(q, 1, n)
        _ <- q.vxm(v, booleanSemiring, edges, desc)
        successor <- q.foldLeft(start = true, lor, desc)
        _ <- v.assign(q, 2, n)
        _ <- q.vxm(v, booleanSemiring, edges, desc)
        successor <- q.foldLeft(start = true, lor, desc)
        values <- v.dense
      } yield values
    }
  }

  // contains edge metadata as well

  // maybe index for nodes

}

object GraphStorage {
  def apply[F[_]](implicit F: Sync[F], N: NativeMode): Resource[F, GraphStorage[F]] =
    BooleanSparseMatrix(1000, 1000, Array(), Array(), Array()).map { m =>
      new GraphStorage[F](m) {}
    }

  def apply[F[_], V](e: (V, V)*)
                    (implicit F: Sync[F], N: NativeMode, I: Id[V]): Resource[F, GraphStorage[F]] =
    BooleanSparseMatrix(1000, 1000, Array(), Array(), Array()).evalMap { m =>
      for {
        g <- F.pure(new GraphStorage[F](m))
        _ <- g.undirected(e: _*)
      } yield g
    }
}
