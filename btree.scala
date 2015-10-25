

sealed trait Tree[+A]
case object EmptyTree extends Tree[Nothing]
case class Node[A](value: A , left: Tree[A], right: Tree[A]) extends Tree[A]


object Tree{
  def empty[A]: Tree[A] = EmptyTree
  def node[A](value: A, left: Tree[A] = empty, right: Tree[A] = empty): Tree[A] = Node(value, left, right)
}



def fold[A, B](t:Tree[A] , z:B)(f:(B,A,B) => B): B = t match {
  case EmptyTree => z
  case Node(x,l,r) => f ( fold( l , z )(f) , x , fold( r , z )(f) )
}