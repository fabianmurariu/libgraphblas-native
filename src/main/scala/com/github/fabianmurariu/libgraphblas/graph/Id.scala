package com.github.fabianmurariu.libgraphblas.graph

trait Id[N] {

  def code(n: N): Long

}

object Id {
  implicit def IdForNum[T](implicit N: Numeric[T]): Id[T] = (n: T) => N.toLong(n)
}
