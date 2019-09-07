package com.github.fabianmurariu.libgraphblas.nat

sealed trait GrBCode {
  self =>

  def isSuccess: Boolean = self == Success

  def isError: Boolean = false
}

object GrBCode {

  @inline
  def fromInt(i: Int): GrBCode = i match {
    case 0 => Success
    case 1 => NoValue
    case 2 => throw UnInitializedObject
    case 3 => throw InvalidObject
    case 4 => throw NullPointer
    case 5 => throw InvalidValue
    case 6 => throw InvalidIndex
    case 7 => throw DomainMismatch
    case 8 => throw DimensionMismatch
    case 9 => throw OutputNotEmpty
    case 10 => throw OutOfMemory
    case 11 => throw InsufficientSpace
    case 12 => throw IndexOutOfBounds
    case 13 => throw Panic
  }
}

sealed trait GrBError extends RuntimeException with GrBCode {
  override def isError: Boolean = true
}

case object Success extends GrBCode

case object NoValue extends GrBCode // specific when looking for A(i, j) and it's not found

case object UnInitializedObject extends GrBError

case object InvalidObject extends GrBError

case object NullPointer extends GrBError

case object InvalidValue extends GrBError

case object InvalidIndex extends GrBError

case object DomainMismatch extends GrBError

case object DimensionMismatch extends GrBError

case object OutputNotEmpty extends GrBError

case object OutOfMemory extends GrBError

case object InsufficientSpace extends GrBError

case object IndexOutOfBounds extends GrBError

case object Panic extends GrBError
