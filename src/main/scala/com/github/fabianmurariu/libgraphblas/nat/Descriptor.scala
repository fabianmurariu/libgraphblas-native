package com.github.fabianmurariu.libgraphblas.nat

import cats.effect.{Resource, Sync}
import graphblas.GraphBLASLibrary.GrB_Descriptor_ByReference

case class Descriptor[F[_]](d: GrB_Descriptor_ByReference, N: NativeMode)

object Descriptor {
  def apply[F[_]](ds: (Field, Value)*)(implicit S: Sync[F], N: NativeMode):Resource[F, Descriptor[F]] = Resource.make(S.delay {
    val d = new GrB_Descriptor_ByReference
    N.g.GrB_Descriptor_new(d)
    ds.foldLeft(d){
      case (d0, (f, v)) =>
        GrBCode.fromInt(N.g.GrB_Descriptor_set(d0.getValue, f.code, v.code))
        d0
    }
    Descriptor[F](d, N)
  })(d => S.delay(GrBCode.fromInt(N.g.GrB_Descriptor_free(d.d))))

}

sealed abstract class Field(val code: Int)
case object Output extends Field(0)
case object Mask extends Field(1)
case object Input1 extends Field(2)
case object Input2 extends Field(3)

sealed abstract class Value(val code: Int)
case object Defaults extends Value(0)
case object Replace extends Value(1)
case object StructComplement extends Value(2)
case object Transpose extends Value(2)
