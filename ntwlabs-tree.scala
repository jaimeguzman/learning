import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue

case class Node[T <% Ordered[T]](value: T, var left: Option[Node[T]] = None, var right: Option[Node[T]] = None) {
  override def toString() :String = {
    "T(" + value.toString + " " + left.toString + " " + right.toString + ")"
  }
}

object Tree {
  def addNode[T <% Ordered[T]](root: Option[Node[T]], node: Option[Node[T]]) : Option[Node[T]] = {
    if (node == None)
      root
    else {
      root match {
        case None => node
        case Some(curr) => {
          if (curr.left == None)
            curr.left = node
          else
            curr.right = node

          root
        }
      }
    }
  }

  def levelOrder[T <% Ordered[T]](root: Option[Node[T]]) : List[T] = {
    var list = new ListBuffer[T]

    val queue = new Queue[Option[Node[T]]]
    queue.enqueue(root)
    while (!queue.isEmpty) {
      val curr = queue.dequeue()

      list += curr.get.value
      if (curr.get.left != None)
        queue.enqueue(curr.get.left)
      if (curr.get.right != None)
        queue.enqueue(curr.get.right)
    }

    list.toList
  }
}