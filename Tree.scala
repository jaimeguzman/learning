/**
 * D Holbrook
 *
 * Code Club: PO1
 *
 * (*) Define a binary tree data structure and related fundamental operations.
 *
 * Use whichever language features are the best fit (this will depend on the language you have selected).  The following operations should be supported:
 *
 *    Constructors
 *        (bitree data left right) - Should return a binary tree containing data and the left and right children.
 *    Accessors
 *        (bitree-data t) - Should return the data contained by the tree.
 *        (bitree-left t) - Should return the left child of the tree.
 *        (bitree-right t) - Should return the right child of the tree.
 *    Predicates
 *        (bitree-leaf? t) - Should return true if the tree is a leaf (has null left and right children), false otherwise
 */
trait Tree[+A] {

  import scala.annotation.tailrec

  def value: Option[A] = this match {
    case n: Node[A] => Some(n.v)
    case l: Leaf[A] => Some(l.v)
    case Empty      => None
  }

  def left: Option[Tree[A]] = this match {
    case n: Node[A] => Some(n.l)
    case l: Leaf[A] => None
    case Empty      => None
  }

  def right: Option[Tree[A]] = this match {
    case n: Node[A] => Some(n.r)
    case l: Leaf[A] => None
    case Empty      => None
  }

  /**
   * Represents a deferred evaluation of a node value
   */
  private case class Eval[A](v: A) extends Tree[A]

  /**
   * represents common functionality of all traversal order folds
   */
  @tailrec
  private def foldLoop[A, B](a: List[Tree[A]], z: B)(f: (B, A) => B)(o: (Node[A], List[Tree[A]]) => List[Tree[A]]): B = a match {
    case (n: Node[A]) :: tl => foldLoop(o(n, tl), z)(f)(o) // never directly evaluate nodes, function o will create new accumulator
    case (l: Leaf[A]) :: tl => foldLoop(tl, f(z, l.v))(f)(o) // always evaluate Leaf 
    case (e: Eval[A]) :: tl => foldLoop(tl, f(z, e.v))(f)(o) // always evaluate Eval 
    case Empty :: tl        => foldLoop(tl, z)(f)(o) // ignore Empty
    case _                  => z // will be Nil (empty list)
  }

  /**
   * fold with preorder traversal (root, left, right)
   * Tail Recursive Optimized
   *
   *        F
   *      /   \
   *    B       G
   *   / \       \
   *  A   D       I
   *     / \     /
   *    C   E   H
   *
   * head evaluate accumulator
   * ---- -------- -----------
   *              | (F)
   * F   | ()     | F::B::G::()
   * F   | (F)    | (B,G)
   * B   | ()     | B::A::D::(G)
   * B   | (B)    | (A,D,G)
   * A   | (A)    | (D,G)
   * D   | ()     | D::C::E::(G)
   * D   | (D)    | (C,E,G)
   * C   | (C)    | (E,G)
   * E   | (E)    | (G)
   * G   | ()     | G::I::()
   * G   | (G)    | (I)
   * I   | ()     | I::H::()
   * I   | (I)    | (H)
   * H   | (H)    | ()
   *
   * result
   * F, B, A, D, C, E, G, I, H
   */
  def foldPreorder[B](z: B)(f: (B, A) => B): B = {
    foldLoop(List(this), z)(f) { (n, tl) => Eval(n.v) :: n.l :: n.r :: tl }
  }

  /**
   * fold with inorder traversal (left, root, right)
   * tail recursive optimized
   *
   *        F
   *      /   \
   *    B       G
   *   / \       \
   *  A   D       I
   *     / \     /
   *    C   E   H
   *
   * head evaluate accumulator
   * ---- -------- -----------
   *              | (F)
   * F   | ()     | B::F::G::()
   * B   | ()     | A::B::D::(F,G)
   * A   | (A)    | (B,D,F,G)
   * B   | (B)    | (D,F,G)
   * D   | ()     | C::D::E::(F,G)
   * C   | (C)    | (D,E,F,G)
   * D   | (D)    | (E,F,G)
   * E   | (E)    | (F,G)
   * F   | (F)    | (G)
   * G   | ()     | G::I::()
   * G   | (G)    | (I)
   * I   | ()     | H::I::()
   * H   | (H)    | H
   * I   | (I)    | ()
   *
   * result
   * A,B,C,D,E,F,G,H,I
   */
  def foldInorder[B](z: B)(f: (B, A) => B): B = {
    foldLoop(List(this), z)(f) { (n, tl) => n.l :: Eval(n.v) :: n.r :: tl }
  }

  /**
   * fold with postorder traversal (left, right, root)
   * tail recursive optimized
   *
   *        F
   *      /   \
   *    B       G
   *   / \       \
   *  A   D       I
   *     / \     /
   *    C   E   H
   *
   * head evaluate accumulator
   * ---- -------- -----------
   *              | (F)
   * F   | ()     | B::G::F::()
   * B   | ()     | A::D::(B,G,F)
   * A   | (A)    | (D,B,G,F)
   * D   | ()     | C::E::D::(B,G,F)
   * C   | (C)    | (E,D,B,G,F)
   * E   | (E)    | (D,B,G,F)
   * D   | (D)    | (B,G,F)
   * B   | (B)    | (G,F)
   * G   | ()     | I::G::(F)
   * I   | ()     | H::I::(G,F)
   * H   | (H)    | (I,G,F)
   * I   | (I)    | (G,F)
   * G   | (G)    | (F)
   * F   | (F)    | ()
   *
   * result
   * A,C,E,D,B,H,I,G,F
   */
  def foldPostorder[B](z: B)(f: (B, A) => B): B = {
    foldLoop(List(this), z)(f) { (n, tl) => n.l :: n.r :: Eval(n.v) :: tl }
  }

  /**
   * fold with levelorder traversal
   * tail recursive optimized
   *
   *        F
   *      /   \
   *    B       G
   *   / \       \
   *  A   D       I
   *     / \     /
   *    C   E   H
   *
   * head evaluate accumulator
   * ---- -------- -----------
   *              | (F)
   * F   | ()     | (F::()) ::: (B,G)
   * F   | (F)    | (B,G)
   * B   | ()     | (B::(G)) ::: (A,D)
   * B   | (B)    | (G,A,D)
   * G   | ()     | (G::(A,D)) ::: (I)
   * G   | (G)    | (A,D,I)
   * A   | (A)    | (D,I)
   * D   | ()     | (D::(I)) ::: (C,E)
   * D   | (D)    | (I,C,E)
   * I   | ()     | (I::(C,E)) ::: (H)
   * I   | (I)    | (C,E,H)
   * C   | (C)    | (E,H)
   * E   | (E)    | (H)
   * H   | (H)    | ()
   *
   * result
   * F, B, G, A, D, I, C, E, H
   */
  def foldLevelorder[B](z: B)(f: (B, A) => B): B = {
    foldLoop(List(this), z)(f) { (n, tl) => (Eval(n.v) :: tl) ::: List(n.l, n.r) }
  }

  /**
   * calls foldInorder
   */
  def fold[B](z: B)(f: (B, A) => B): B = foldInorder(z)(f)

  /**
   * P02
   * (*) Count the number of nodes in a binary tree.
   */
  def size: Int = fold(0) { (sum, v) => sum + 1 }

  /**
   * P03
   * (*) Determine the height of a binary tree.
   * Definition:  The height of a tree is the length of the path from the root to the deepest node in the tree. A (rooted) tree with only one node (the root) has a height of zero.
   */
  def height: Int = {
    def loop(t: Tree[A]): Int = t match {
      case l: Leaf[A] => 1
      case n: Node[A] => Seq(loop(n.left.get), loop(n.right.get)).max + 1
      case _          => 0
    }
    loop(this) - 1
  }

  /**
   * P04
   * (*) Count the number of leaves in a binary tree.
   */
  def leafCount: Int = {
    @tailrec
    def loop(t: List[Tree[A]], z: Int): Int = t match {
      case (l: Leaf[A]) :: tl => loop(tl, z + 1)
      case (n: Node[A]) :: tl => loop(n.left.get :: n.right.get :: tl, z)
      case _ :: tl            => loop(tl, z)
      case _                  => z
    }
    loop(List(this), 0)
  }

  def toSeq: Seq[A] = fold(List[A]()) { (l, v) => v :: l } reverse

  def toSeqPreorder: Seq[A] = foldPreorder(List[A]()) { (l, v) => v :: l } reverse
  def toSeqInorder: Seq[A] = foldInorder(List[A]()) { (l, v) => v :: l } reverse
  def toSeqPostorder: Seq[A] = foldPostorder(List[A]()) { (l, v) => v :: l } reverse
  def toSeqLevelorder: Seq[A] = foldLevelorder(List[A]()) { (l, v) => v :: l } reverse

  /**
   * P05
   * (**) Find the last element in a binary tree using pre/in/post/level order traversals.
   *  Note:  This is S-99 problem P01 converted from lists to binary trees.
   */
  def lastPreorder = toSeqPreorder.last
  def lastInorder = toSeqInorder.last
  def lastPostorder = toSeqPostorder.last
  def lastLevelorder = toSeqLevelorder.last

  /**
   * P06
   * (**) Find the last but one element in a binary tree using pre/in/post/level order traversals.
   *  Note:  This is S-99 problem P02 converted from lists to binary trees.
   */
  def penultimatePreorder = toSeqPreorder.dropRight(1).last
  def penultimateInorder = toSeqInorder.dropRight(1).last
  def penultimatePostorder = toSeqPostorder.dropRight(1).last
  def penultimateLevelorder = toSeqLevelorder.dropRight(1).last

  /**
   * P07
   * (**) Find the Nth element in a binary tree using pre/in/post/level order traversals.
   * By convention, the first element in the tree is element 0.
   *
   */
  def nthPreorder(n: Int) = toSeqPreorder(n)
  def nthInorder(n: Int) = toSeqInorder(n)
  def nthPostorder(n: Int) = toSeqPostorder(n)
  def nthLevelorder(n: Int) = toSeqLevelorder(n)

}

case class Node[A](v: A, l: Tree[A], r: Tree[A]) extends Tree[A]
case class Leaf[A](v: A) extends Tree[A]
case object Empty extends Tree[Nothing]

object Run extends App {
  val t: Tree[Symbol] = Node('F, Node('B, Leaf('A), Node('D, Leaf('C), Leaf('E))), Node('G, Empty, Node('I, Leaf('H), Empty)))
  println("tree: " + t)

  //print the value of b node navigating from root
  for {
    b <- t.left
    value <- b.value
  } println("B node: " + value)

  //print the value of e node navigating from root
  for {
    b <- t.left
    d <- b.right
    value <- d.value
  } println("D node: " + value)

  //no println() is executed for empty node chain
  for {
    b <- t.left
    d <- b.right
    e <- d.right
    x <- e.right
    value <- x.value
  } println("X node SHOUL NOT PRINT!: " + value)

  println("as seq: " + t.toSeq)

  println("count: " + t.size)
  assert(t.size == 9)

  println("height: " + t.height)
  assert(t.height == 3)

  println("leaft count: " + t.leafCount)
  assert(t.leafCount == 4)

  println("as seqPreorder: " + t.toSeqPreorder)
  println("as seqInorder: " + t.toSeqInorder)
  println("as seqPostorder: " + t.toSeqPostorder)
  println("as seqLevelorder: " + t.toSeqLevelorder)

  println("last preorder :" + t.lastPreorder)
  println("last inorder :" + t.lastInorder)
  println("last postorder :" + t.lastPostorder)
  println("last levelorder: " + t.lastLevelorder)

  println("nth preorder 5 : " + t.nthPreorder(5))
  println("nth inorder 5 : " + t.nthInorder(5))
  println("nth postorder 5 : " + t.nthPostorder(5))
  println("nth levelorder 5 : " + t.nthLevelorder(5))

  //    
  /**
   * **** output *********
   *
   * tree: Node('F,Node('B,Leaf('A),Node('D,Leaf('C),Leaf('E))),Node('G,Empty,Node('I,Leaf('H),Empty)))
   * B node: 'B
   * D node: 'D
   * as seq: List('A, 'B, 'C, 'D, 'E, 'F, 'G, 'H, 'I)
   * count: 9
   * height: 3
   * leaft count: 4
   * as seqPreorder: List('F, 'B, 'A, 'D, 'C, 'E, 'G, 'I, 'H)
   * as seqInorder: List('A, 'B, 'C, 'D, 'E, 'F, 'G, 'H, 'I)
   * as seqPostorder: List('A, 'C, 'E, 'D, 'B, 'H, 'I, 'G, 'F)
   * as seqLevelorder: List('F, 'B, 'G, 'A, 'D, 'I, 'C, 'E, 'H)
   * last preorder :'H
   * last inorder :'I
   * last postorder :'F
   * last levelorder: List('F, 'B, 'G, 'A, 'D, 'I, 'C, 'E, 'H)
   * nth preorder 5 : 'C
   * nth inorder 5 : 'E
   * nth postorder 5 : 'B
   * nth levelorder 5 : 'D
   *
   * ***********************
   */




}