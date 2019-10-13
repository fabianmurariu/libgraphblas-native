package com.github.fabianmurariu.libgraphblas.nat

import cats.effect.{Resource, Sync}
import graphblas.GraphBLASLibrary.GrB_Semiring_ByReference

trait Semiring[F[_], +A] {
  private[nat] def s: GrB_Semiring_ByReference

  private[nat] implicit def N: NativeMode

}

case class BooleanSemiring[F[_]](s: GrB_Semiring_ByReference, N: NativeMode) extends Semiring[F, Boolean]

object Semiring {
  def LAND[F[_]](m: Monoid[F, Boolean])(implicit S: Sync[F], N: NativeMode): Resource[F, Semiring[F, Boolean]] = Resource.make(S.delay {
    val s = new GrB_Semiring_ByReference()
    N.g.GrB_Semiring_new(s, m.m.getValue, N.g.GrBLAND())
    BooleanSemiring(s, N).asInstanceOf[Semiring[F, Boolean]]
  })((s: Semiring[F, Boolean]) => S.delay(N.g.GrB_Semiring_free(s.s)))
}

