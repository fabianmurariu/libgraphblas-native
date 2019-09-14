package com.github.fabianmurariu.libgraphblas.nat

import graphblas.GraphBLASLibrary.{GrB_BinaryOp, GrB_Monoid_ByReference}

trait Monoid[F[_], A] {
  private[nat] def m: GrB_Monoid_ByReference
  private[nat] implicit def N: NativeMode
}
