package com.github.fabianmurariu.libgraphblas.nat

import cats.effect.{Resource, Sync}
import graphblas.GraphBLASLibrary
import graphblas.GraphBLASLibrary.{GrB_BinaryOp, GrB_Monoid_ByReference}

trait Monoid[F[_], A] {
  private[nat] def m: GrB_Monoid_ByReference

  private[nat] implicit def N: NativeMode
}

case class BooleanMonoid[F[_]](m: GrB_Monoid_ByReference, N: NativeMode) extends Monoid[F, Boolean]

object BooleanMonoid {
  def LOR[F[_]](implicit F: Sync[F], N: NativeMode): Resource[F, Monoid[F, Boolean]] = Resource.make(F.delay {
    val m = new GraphBLASLibrary.GrB_Monoid_ByReference()
    GrBCode.fromInt(N.g.GrB_Monoid_new_BOOL(m, N.g.GrBLOR(), false))
    BooleanMonoid(m, N).asInstanceOf[Monoid[F, Boolean]]
  })(monoid => F.delay(N.g.GrB_Monoid_free(monoid.m)))
}

case class Int32Monoid[F[_]](m: GrB_Monoid_ByReference, N: NativeMode) extends Monoid[F, Boolean]

