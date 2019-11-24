package com.github.fabianmurariu.libgraphblas.graph

import cats.effect.IO
import com.github.fabianmurariu.libgraphblas.nat.NativeMode
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop.{forAll, propBoolean}
import NativeMode.nonblocking.native

object GraphStorageSpec extends Properties("GraphStorage") {

  // 1000 nodes and 50 edges
  implicit val edge: Gen[Edge] = for {
    v1 <- Gen.choose(0,999)
    v2 <- Gen.choose(0,999).filter(_ != v1)
  } yield Edge(v1, v2)

  implicit val edges: Arbitrary[List[Edge]] = Arbitrary(Gen.nonEmptyListOf(edge))

  property("GraphStorage can be created") = forAll { edges: List[Edge] =>
    val gs = GraphStorage[IO, Int](edges.distinct.flatMap (Edge.unapply): _*)

    val (neighbours,e@Edge(v1, v2))  = gs.use { g =>
      g.neighbours(edges.head.v1).map(n => n -> edges.head)
    }.unsafeRunSync()

    (neighbours(v1) == 1 && neighbours(v2) == 2) :| s"${neighbours.toVector} did not contain $e, total: ${neighbours.length}"
  }

}

case class Edge(v1: Int, v2: Int)

