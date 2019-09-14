package com.github.fabianmurariu.libgraphblas.nat

import cats.effect.Sync

trait Ops[F[_]] {

  /**
   * Vector matrix multiply TODO: add docs from guide
   *
   * @param w
   * @param u
   * @param A
   * @param mask
   * @param accum
   * @param semiring
   * @param desc
   * @tparam T
   * @return
   */
  def vxm[T](w: SparseVector[F, T]) // input/output vector for results
            (u: SparseVector[F, T], A: SparseMatrix[F, T]) // input vector and matrix
            (mask: Option[SparseVector[F, Boolean]],
             accum: Option[Monoid[F, T]],
             semiring: Semiring[F, T],
             desc: Descriptor[F])(implicit N: NativeMode, F: Sync[F]): F[Unit] = F.delay{
//    N.g.GrB_vxm()
    ???
  }

}
