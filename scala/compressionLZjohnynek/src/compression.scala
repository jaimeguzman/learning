/**
scala> compress(List.fill(1000)(1))
res17: List[Either[Int,Int]] = List(Left(1), Right(0), Left(1), Right(1), Left(1), Right(2), Left(1), Right(3), Left(1), Right(4), Left(1), Right(5), Left(1), Right(6), Left(1), Right(7), Left(1), Right(8), Left(1), Right(9), Left(1), Right(10), Left(1), Right(11), Left(1), Right(12), Left(1), Right(13), Left(1), Right(14), Left(1), Right(15), Left(1), Right(16), Left(1), Right(17), Left(1), Right(18), Left(1), Right(19), Left(1), Right(20), Left(1), Right(21), Left(1), Right(22), Left(1), Right(23), Left(1), Right(24), Left(1), Right(25), Left(1), Right(26), Left(1), Right(27), Left(1), Right(28), Left(1), Right(29), Left(1), Right(30), Left(1), Right(31), Left(1), Right(32), Left(1), Right(33), Left(1), Right(34), Left(1), Right(35), Left(1), Right(36), Left(1), Right(37), Left(1), Ri...

scala> compress(List.fill(1000)(1)).size
res18: Int = 88

scala> decompress(res17)
res19: List[Int] = List(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,...
scala> decompress(res17).size
res20: Int = 1000

// Random strings are uncompressable:
scala> List.fill(1000)(0).map { _ => (scala.math.random * 100).toInt }
res25: List[Int] = List(2, 22, 37, 51, 19, 31, 1, 96, 40, 50, 83, 49, 16, 99, 44, 24, 17, 16, 2, 11, 98, 77, 35, 5, 22, 9, 46, 67, 31, 36, 51, 31, 12, 51, 84, 67, 90, 56, 78, 75, 2, 42, 57, 78, 7, 19, 59, 9, 31, 0, 18, 69, 10, 95, 64, 53, 25, 11, 16, 85, 56, 3, 17, 14, 24, 55, 19, 55, 63, 71, 9, 28, 33, 92, 68, 94, 84, 39, 80, 14, 7, 91, 9, 82, 88, 64, 62, 8, 1, 63, 50, 31, 36, 26, 3, 27, 29, 24, 38, 84, 67, 58, 17, 85, 10, 93, 50, 99, 36, 29, 30, 70, 11, 7, 48, 3, 63, 56, 24, 32, 1, 3, 38, 40, 86, 38, 93, 68, 84, 12, 31, 32, 33, 30, 59, 82, 70, 41, 10, 31, 51, 92, 14, 68, 0, 60, 28, 35, 58, 90, 99, 41, 47, 17, 8, 88, 4, 37, 16, 11, 43, 31, 20, 91, 58, 91, 48, 84, 13, 57, 46, 4, 64, 57, 28, 19, 13, 15, 63, 99, 21, 51, 77, 89, 3, 21, 11, 95, 84, 35, 15, 27, 9, 64, 15, 11, 5, 17, 6, 41, 4...
scala> compress(res25)
res26: List[Either[Int,Int]] = List(Left(2), Left(22), Left(37), Left(51), Left(19), Left(31), Left(1), Left(96), Left(40), Left(50), Left(83), Left(49), Left(16), Left(99), Left(44), Left(24), Left(17), Right(12), Left(2), Left(11), Left(98), Left(77), Left(35), Left(5), Right(1), Left(9), Left(46), Left(67), Right(5), Left(36), Right(3), Left(31), Left(12), Right(3), Left(84), Right(25), Left(90), Left(56), Left(78), Left(75), Right(0), Left(42), Left(57), Right(32), Left(7), Right(4), Left(59), Left(9), Right(5), Left(0), Left(18), Left(69), Left(10), Left(95), Left(64), Left(53), Left(25), Right(18), Left(16), Left(85), Right(31), Left(3), Right(16), Left(14), Right(15), Left(55), Right(4), Left(55), Left(63), Left(71), Right(38), Left(28), Left(33), Left(92), Left(68), Left(94), Le...
scala> compress(res25).size
res27: Int = 992

scala> decompress(res26)
res28: List[Int] = List(2, 22, 37, 51, 19, 31, 1, 96, 40, 50, 83, 49, 16, 99, 44, 24, 17, 16, 2, 11, 98, 77, 35, 5, 22, 9, 46, 67, 31, 36, 51, 31, 12, 51, 84, 67, 90, 56, 78, 75, 2, 42, 57, 78, 7, 19, 59, 9, 31, 0, 18, 69, 10, 95, 64, 53, 25, 11, 16, 85, 56, 3, 17, 14, 24, 55, 19, 55, 63, 71, 9, 28, 33, 92, 68, 94, 84, 39, 80, 14, 7, 91, 9, 82, 88, 64, 62, 8, 1, 63, 50, 31, 36, 26, 3, 27, 29, 24, 38, 84, 67, 58, 17, 85, 10, 93, 50, 99, 36, 29, 30, 70, 11, 7, 48, 3, 63, 56, 24, 32, 1, 3, 38, 40, 86, 38, 93, 68, 84, 12, 31, 32, 33, 30, 59, 82, 70, 41, 10, 31, 51, 92, 14, 68, 0, 60, 28, 35, 58, 90, 99, 41, 47, 17, 8, 88, 4, 37, 16, 11, 43, 31, 20, 91, 58, 91, 48, 84, 13, 57, 46, 4, 64, 57, 28, 19, 13, 15, 63, 99, 21, 51, 77, 89, 3, 21, 11, 95, 84, 35, 15, 27, 9, 64, 15, 11, 5, 17, 6, 41, 4...
scala> res28 == res25
res29: Boolean = true
  */

/**
 * Emit a list of items that are either literals, or they are indices into a Vector of a repeated history
 */
def compress[T](i: List[T]): List[Either[T,Int]] = {
  @annotation.tailrec
  def loop(todo: List[T],
           matched: List[T],
           acc: List[Either[T, Int]],
           table: Map[List[T], Int],
           idx: Int): List[Either[T, Int]] = todo match {
    case Nil => if (matched.isEmpty) acc else Right(table(matched)) :: acc
    case h :: tail =>
      val nmatch = h :: matched
      if(table.contains(nmatch)) {
        loop(tail, nmatch, acc, table, idx)
      }
      else {
        val nacc = if (matched.isEmpty)
          Left(h) :: acc
        else
          Left(h) :: Right(table(matched)) :: acc
        val ntab = table + (nmatch -> idx)
        val nidx = idx + 1
        loop(tail, Nil, nacc, ntab, nidx)
      }
  }
  loop(i, Nil, Nil, Map.empty, 0).reverse
}

def decompress[T](comp: List[Either[T, Int]]): List[T] = {
  @annotation.tailrec
  def loop(todo: List[Either[T, Int]],
           prefix: List[T],
           acc: List[T],
           table: Vector[List[T]],
           idx: Int): List[T] = todo match {
    case Nil => acc.reverse
    case Left(v) :: tail =>
      // miss in the table, update the entry
      val nidx = idx + 1
      val ntab = table :+ (v :: prefix)
      val nprefix = Nil
      loop(tail, nprefix, v :: acc, ntab, nidx)
    case Right(idx) :: tail =>
      //hit in the table:
      val nprefix = table(idx)
      loop(tail, nprefix, nprefix ::: acc, table, idx)
  }
  loop(comp, Nil, Nil,  Vector(), 0)
}