/**
 * A simple class that can act as a spellchecker
 * - could be extended to autocomplete
 *
 * The basic principle here is that words forming the dictionary are stored in a tree structure,
 * with unique values [a-z] at each node. Children of each node are also unique in the set [a-z].
 * In this way, words are stored by traversing common keys down the branches until the final leaf
 * is found (or not as the case may be).
 *
 * Finding the word once the tree is constructed is essentially a breadth-first search through each layer
 * of the tree
 *
 * I believe this is called a trie
 * It should look something like this:
 *           s
 *          / \
 *         a   o
 *        /\    \
 *       v  f    f
 *      /    \    \
 *     e      e    a
 *
 *  TODO: Make all structures immutable. Specifically, change HashMap if possible.
 */

import collection.mutable.HashMap
// import replutils._

object Spellchecker {
  val dictionary = new Node()

  def buildDictionary(words: Array[String]) { words.foreach( fill(dictionary, _) ) }

  def fill(node: Node, letters: String) {
    val (letter, tail) = (letters.head, letters.tail)
    val child          = node.children.getOrElseUpdate(letter, new Node())

    System.out.println("letters:  "+letters);

    if (!tail.isEmpty) {
      fill(child, tail)
    }
  }
}



class Spellchecker {

  def check(word: String): Boolean = exists(Spellchecker.dictionary, word.trim)

  private def exists(node: Node, letters: String): Boolean = {
    val (letter, tail) = (letters.head, letters.tail)
    System.out.println("letras  "+letters);
    System.out.println("letra_  "+node.children.get(letter));


    node.children.get(letter) match {
      case Some(child) if !tail.isEmpty => exists(child, tail)
      case None => false
      case _ => true // final matching case.. tail is empty (last letter)
    }
  }

  def printShit(): Unit ={
    System.out.println("IMPRIME MIERDA")
    println(Spellchecker.dictionary.toString)

  }

}

// It seems hard to define a self-referencing HashMap without some sort of wrapper
//
class Node(val children: HashMap[Char, Node] = HashMap.empty[Char, Node])




object Tree extends App {

  System.out.println("Hola mundo");

  val palabras = Array("A","B","R","A","C","A","D","A","B","R","A"  );

  Spellchecker.buildDictionary(palabras)

  val sp = new Spellchecker()

  sp.printShit(  )


}

