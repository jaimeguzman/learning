import scala.language.postfixOps
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

object LZ77 {
  def generateDistanceLengthTable(bytes: Array[Byte]) : List[Pair[Byte, Pair[Int, Int]]] = {
    val searchTable = new HashMap[List[Byte], ListBuffer[Int]]
    val finalTable  = new ListBuffer[Pair[Byte, Pair[Int, Int]]]

    def loop(idx: Int) : Unit = {
      if (idx < bytes.length) {
        if (idx + 3 > bytes.length) {
          val bestMatch = (bytes(idx), (idx, idx)); finalTable += bestMatch
          loop(idx + 1)
        }
        else {
          val toSearch = bytes.slice(idx, idx + 3).toList

          searchTable.get(toSearch) match {
            case Some(el) => {
              var bestMatch = (0, 0)
              el foreach {
                var tmpIdx = idx + 3
                position => {
                  var i = position + 3
                  if (bestMatch._2 < 3)
                    bestMatch = (idx - position, 3)
                  while (i < bytes.length && (tmpIdx < bytes.length && (bytes(i) == bytes(tmpIdx)))) {
                    if (bestMatch._2 < i)
                      bestMatch = (idx - position, i - position + 1)
                    tmpIdx = tmpIdx + 1
                    i = i + 1
                  }
                }
              } 

              val myMatch = (0.asInstanceOf[Byte], bestMatch); finalTable += myMatch

              el.prepend(idx)
            
              for (i <- idx until (idx + bestMatch._2)) {
                val toSearch = bytes.slice(i, i + 3).toList
                searchTable.get(toSearch) match {
                  case Some(curr) => curr.prepend(i)
                  case None => val b = new ListBuffer[Int]; b+=idx; searchTable.put(toSearch, b)
                }
              }
              loop(idx + bestMatch._2)
            }
            case None => {
              val b = new ListBuffer[Int]; b += idx; searchTable.put(toSearch, b)
              val bestMatch = (bytes(idx), (idx, idx)); finalTable += bestMatch
              loop(idx + 1)
            }
          }
        }
      }
    }

    loop(0)
    finalTable.toList
  }
}