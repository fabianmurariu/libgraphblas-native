package com.github.fabianmurariu.libgraphblas.graph

import cats.effect.IO
import com.github.fabianmurariu.libgraphblas.nat.NativeMode
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop._
import NativeMode.nonblocking.native

object GraphStorageSpec extends Properties("GraphStorage") {

  // 1000 nodes and 50 edges
  implicit val edge: Gen[Edge] = for {
    v1 <- Gen.choose(0,999)
    v2 <- Gen.choose(0,999).filter(_ != v1)
  } yield Edge(v1, v2)

  implicit val edges: Arbitrary[List[Edge]] = Arbitrary(Gen.nonEmptyListOf(edge))

  property("GraphStorage can be created") = forAll { edges: List[Edge] =>
    val gs = GraphStorage[IO, Int](edges.map { case Edge(v1, v2) => v1 -> v2 }: _*)

    val n = gs.use { g =>
      g.neighbours(edges.head.v1)
    }.unsafeRunSync()

    n.nonEmpty
  }

}

case class Edge(v1: Int, v2: Int)

