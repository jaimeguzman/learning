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
	  val words = List( "A","B","R","A","C","A","D","A","B","R","A" );	

	  def construct(A: List[String]): Node = {
	    def insert(tree: Node, value: String): Node = {
	      tree match {
	        case null => LeafNode(value)

	        case LeafNode(data) => if (value > data) {
	          		LeftNode(data, LeafNode(value))
	        	}else{
	          		RightNode(data, LeafNode(value))
	        	}
	        case LeftNode(data, left) => if (value > data) {
	          		LeftNode(value, LeftNode(data, left))
	        	}else{
	          		FullNode(data, left, LeafNode(value))
	        	}

	        case RightNode(data, right) => if (value > data) {
	          		FullNode(data, LeafNode(value), right)
	        	}else{
	          		RightNode(value, RightNode(data, right))
	        	}

	        case FullNode(data, left, right) => if (value > data) {
	          		FullNode(data, insert(left, value), right)
	        	}else {
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


}


 