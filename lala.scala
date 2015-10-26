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

case class LeafNode(data: String, counter: Int) extends Node;
case class FullNode(data: String, left: Node, right: Node) extends Node
case class LeftNode(data: String, left: Node) extends Node
case class RightNode(data: String, right: Node) extends Node



object trieLZ78 extends App {
		
/*	val alphabet:Seq[String]
	val firstSymbol:String
	val rootHeadName:String // Name of root level's head*/


	System.out.println("This is the lz78")
	// val words = List( "B","E","O","R","N","O","T","T","O","B","E" );	
	// val words = List( "A","B","R","A","C","A","D","A","B","R","A" );	
	val alphabet = List( "A","B","C","R");	
	val words = List( "A","B","R");	

	def construct(A: List[String]): Node = {

	    def insert(tree: Node, value: String): Node = {
	      tree match {
	        case null => LeafNode(value,0)
	        case LeafNode(data,1) => if (value > data) {

	        		System.out.println("1) Nodo Hoja \t (value > data) "+value+" >  "+data )
	          		LeftNode(data, LeafNode(value,1))
	        	}else{

		        	System.out.println("2) Nodo Hoja\t (value < = data) "+value+" >  "+data )	
	          		RightNode(data, LeafNode(value,1))
	        	}
	        case LeftNode(data, left) => if (value > data) {
		        	System.out.println("3) Nodo Izq \t (value > data) "+value+" >  "+data )
	          		LeftNode(value, LeftNode(data, left))
	        	}else{
		        	System.out.println("4) Nodo Izq\t (value <= data) "+value+" >  "+data )
	          		FullNode(data, left, LeafNode(value,1))
	        	}
/*
	        case RightNode(data, right) => if (value > data) {

		        	System.out.println("5) Nodo Dcha\t (value > data) "+value+" >  "+data )
	          		FullNode(data, LeafNode(value,1), right)
	        	}else{
		        	System.out.println("6) Nodo Dcha\t (value <= data) "+value+" >  "+data )
	          		RightNode(value, RightNode(data, right))
	        	}

	        case FullNode(data, left, right) => if (value > data) {
		        	System.out.println("7) FullNode\t (value > data) "+value+" >  "+data )
	          		FullNode(data, insert(left, value), right)
	        	}else {
		        	System.out.println("8) FullNode\t (value <= data) "+value+" >  "+data )
	          		FullNode(data, left, insert(right, value))
	        	}*/
	      }
	    }
	 	
	 	//Comienzo el Tree vacio
	    var tree: Node = null;
	    tree = insert(tree, "")


	    return tree
	  };// termina el construct
	 



	  //=> System.out.println(" Valor List "+A);
	  val f = (A: String) => System.out.println(A)
	  words.map(f);

	  
	  var x = construct(words);


	  def recurseNode(A: Node, depth: Int) {
	    def display(data: String, depth: Int) {
	      for (i <- 1 to depth * 2) { System.out.print("*") }
	      // System.out.println(data);
	    }
	    A match {
	      case null => {
	        display("[]", depth)
	      }
	      case LeafNode(data,0) => {
	        display(data, depth)
	        recurseNode(null, depth + 1)
	        recurseNode(null, depth + 1)
	      }
/*	      case FullNode(data, left, right) => {
	        display(data, depth)
	        recurseNode(left, depth + 1)
	        recurseNode(right, depth + 1)
	      }
	      case RightNode(data, right) => {
	        display(data, depth)
	        recurseNode(null, depth + 1)
	        recurseNode(right, depth + 1)
	      }*/
	      case LeftNode(data, left) => {
	      	// System.out.println("izq")
	        display(data, depth)
	        recurseNode(left, depth + 1)
	        recurseNode(null, depth + 1)
	      }
	    }
	  } //recurseNode
 
  	def output(A: Node, recurse: (Node, Int) => Unit) = { recurse(A, 0)	}
  	def renderTree(A: Node) = {	output(x, recurseNode);	}


	renderTree(x);




 

}


 