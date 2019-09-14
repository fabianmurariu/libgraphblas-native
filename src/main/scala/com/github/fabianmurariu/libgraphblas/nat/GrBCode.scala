package com.github.fabianmurariu.libgraphblas.nat

sealed trait GrBCode {
  self =>

  def isSuccess: Boolean = self == Success

  def isError: Boolean = false
}

object GrBCode {

  @inline
  def fromInt(f : => Int)(implicit N:NativeMode): GrBCode = f match {
    case 0 => Success
    case 1 => NoValue
    case 2 => throw UnInitializedObject(N.g.GrB_error())
    case 3 => throw InvalidObject(N.g.GrB_error())
    case 4 => throw NullPointer(N.g.GrB_error())
    case 5 => throw InvalidValue(N.g.GrB_error())
    case 6 => throw InvalidIndex(N.g.GrB_error())
    case 7 => throw DomainMismatch(N.g.GrB_error())
    case 8 => throw DimensionMismatch(N.g.GrB_error())
    case 9 => throw OutputNotEmpty(N.g.GrB_error())
    case 10 => throw OutOfMemory(N.g.GrB_error())
    case 11 => throw InsufficientSpace(N.g.GrB_error())
    case 12 => throw IndexOutOfBounds(N.g.GrB_error())
    case 13 => throw Panic(N.g.GrB_error())
  }
}

sealed abstract class GrBError(msg:String) extends RuntimeException(msg) with GrBCode {
  override def isError: Boolean = true
}

case object Success extends GrBCode

case object NoValue extends GrBCode // specific when looking for A(i, j) and it's not found

case class UnInitializedObject(msg:String) extends GrBError(msg)
case class InvalidObject(msg:String) extends GrBError(msg)
case class NullPointer(msg:String) extends GrBError(msg)
case class InvalidValue(msg:String) extends GrBError(msg)
case class InvalidIndex(msg:String) extends GrBError(msg)
case class DomainMismatch(msg:String) extends GrBError(msg)
case class DimensionMismatch(msg:String) extends GrBError(msg)
case class OutputNotEmpty(msg:String) extends GrBError(msg)
case class OutOfMemory(msg:String) extends GrBError(msg)
case class InsufficientSpace(msg:String) extends GrBError(msg)
case class IndexOutOfBounds(msg:String) extends GrBError(msg)
case class Panic(msg:String) extends GrBError(msg)
