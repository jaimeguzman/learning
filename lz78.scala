import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/*

*/

abstract class Node

case class LeafNode(data: String) extends Node;
case class FullNode(data: String, left: Node, right: Node) extends Node
case class LeftNode(data: String, left: Node) extends Node
case class RightNode(data: String, right: Node) extends Node



object Lz78Trie extends App {
		
/*	val alphabet:Seq[String]
	val firstSymbol:String
	val rootHeadName:String // Name of root level's head*/


System.out.println("This is the lz78")
val words = List( "B","E","O","R","N","O","T","T","O","B","E" );	

	  def construct(A: List[String]): Node = {

	    def insert(tree: Node, value: String): Node = {


	      tree match {
	        case null => LeafNode(value)


	        case LeafNode(data) => if (value > data) {

	        		System.out.println("1) LeafNode\t value : "+value +" y data es: "+data )
	          		LeftNode(data, LeafNode(value))
	        	}else{

		        	System.out.println("2) LeafNode\t value : "+value +" y data es: "+data )	
	          		RightNode(data, LeafNode(value))
	        	}
	        case LeftNode(data, left) => if (value > data) {
		        	System.out.println("3) LeafNode\t value : "+value +" y data es: "+data )
	          		LeftNode(value, LeftNode(data, left))
	        	}else{
		        	System.out.println("4) LeafNode\t value : "+value +" y data es: "+data )
	          		FullNode(data, left, LeafNode(value))
	        	}

	        case RightNode(data, right) => if (value > data) {

		        	System.out.println("5)\tRightNode\t value : "+value +" y data es: "+data )
	          		FullNode(data, LeafNode(value), right)
	        	}else{
		        	System.out.println("6) \tRightNode\t value : "+value +" y data es: "+data )
	          		RightNode(value, RightNode(data, right))
	        	}

	        case FullNode(data, left, right) => if (value > data) {
		        	System.out.println("7) FullNode\t value : "+value +" y data es: "+data )
	          		FullNode(data, insert(left, value), right)
	        	}else {
		        	System.out.println("8) FullNode\t value : "+value +" y data es: "+data )
	          		FullNode(data, left, insert(right, value))
	        	}
	      }
	    }
	 	
	 	//Comienzo el Tree vacio
	    var tree: Node = null;
	    for (item <- A) {	  
	    	System.out.print("insert item \t"+item +"\n")
	      	tree = insert(tree, item)
	    }
	 
	    return tree
	  };// termina el construct
	 

	  val f = (A: String) => System.out.println(" Valor List "+A);
	 
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


}


 