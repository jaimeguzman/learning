// https://www.garysieling.com/blog/scala-tree-sort-example


abstract class Node
case class LeafNode(data: String) extends Node;
case class FullNode(data: String, left: Node, right: Node) extends Node
case class LeftNode(data: String, left: Node) extends Node
case class RightNode(data: String, right: Node) extends Node
 
object test extends App {
  // val words = List("revertively", "dispelled", "overmoral",
  //   "sylphid", "nonhabitability", "noiselessness",
  //   "undisconnected", "shoveling", "visalia", "ilo");
 
  val words = List( "A","B","R","A","C","A","D","A","B","R","A" );


  def construct(A: List[String]): Node = {
    def insert(tree: Node, value: String): Node = {
      tree match {
        case null => LeafNode(value)
        case LeafNode(data) => if (value > data) {
          LeftNode(data, LeafNode(value))
        } else {
          RightNode(data, LeafNode(value))
        }
        case LeftNode(data, left) => if (value > data) {
          LeftNode(value, LeftNode(data, left))
        } else {
          FullNode(data, left, LeafNode(value))
        }
        case RightNode(data, right) => if (value > data) {
          FullNode(data, LeafNode(value), right)
        } else {
          RightNode(value, RightNode(data, right))
        }
        case FullNode(data, left, right) => if (value > data) {
          FullNode(data, insert(left, value), right)
        } else {
          FullNode(data, left, insert(right, value))
        }
      }
    }
 
    var tree: Node = null;
 
    for (item <- A) {
      tree = insert(tree, item)
    }
 
    return tree
  };
 
  val f = (A: String) =>
    System.out.println(A);
 
  words.map(f);
 
  var x = construct(words);
 
  def recurseNode(A: Node, depth: Int) {
    def display(data: String, depth: Int) {
      for (i <- 1 to depth * 2) { System.out.print("-") }
      System.out.println(data);
    }
    A match {
      case null => {
        display("[]", depth)
      }
      case LeafNode(data) => {
        display(data, depth)
        recurseNode(null, depth + 1)
        recurseNode(null, depth + 1)
      }
      case FullNode(data, left, right) => {
        display(data, depth)
        recurseNode(left, depth + 1)
        recurseNode(right, depth + 1)
      }
      case RightNode(data, right) => {
        display(data, depth)
        recurseNode(null, depth + 1)
        recurseNode(right, depth + 1)
      }
      case LeftNode(data, left) => {
        display(data, depth)
        recurseNode(left, depth + 1)
        recurseNode(null, depth + 1)
      }
    }
  }
 
  def output(A: Node, recurse: (Node, Int) => Unit) = {
    recurse(A, 0)
  }
 
  def renderTree(A: Node) = {
    output(x, recurseNode);
  }
 
  renderTree(x);
 
  def sortedRender(A: Node, depth: Int) {
    def display(data: String, depth: Int) {
      System.out.println(data);
    }
    A match {
      case null => {
      }
      case LeafNode(data) => {
        display(data, depth)
      }
      case FullNode(data, left, right) => {
        sortedRender(left, depth + 1)
        display(data, depth)
        sortedRender(right, depth + 1)
      }
      case RightNode(data, right) => {
        sortedRender(null, depth + 1)
        display(data, depth)
        sortedRender(right, depth + 1)
      }
      case LeftNode(data, left) => {
        sortedRender(left, depth + 1)
        display(data, depth)
      }
    }
  }
 
  def renderTreeSorted(A: Node) = {
    output(x, sortedRender);
  }
 
  System.out.println("")
  //renderTreeSorted(x);
}