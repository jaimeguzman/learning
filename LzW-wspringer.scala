
/*
Learning from this @author:wspringer
https://github.com/wspringer/scala-lzw/blob/master/src/main/scala/nl/flotsam/lzw/Node.scala
*/



trait Node {
	
  def decode[T](i: Int, fn: (Byte) => T): Node
  def encode[T](b: Byte, fn: (Int, Int) => T): Node
  def apply[T](fn: (Byte) => T)
  def root: Node
  def bitsRequired: Int
  def terminate[T](fn: (Int, Int) => T)
  def first: Byte

}


trait NodeManager {
	
	def create(owner: Node, value: Byte, first: Byte): Option[Node]
	def get(value: Int): Option[Node]
}

class ValueNode(index: Int, owner: Node, value: Byte, val first: Byte, nodeManager: NodeManager) extends Node {

  private val children = new Array[Node](255)

  def decode[T](i: Int, fn: (Byte) => T) = {
    val node = nodeManager.get(i).get
    node.apply(fn)
    val child = children(0xff & node.first)
    if (child == null) {
      nodeManager.create(this, node.first, this.first) match {
        case Some(nxt) => children(0xff & node.first) = nxt
        case _ =>
      }
    }
    node
  }

  def encode[T](b: Byte, fn: (Int, Int) => T) = {
    val child = children(0xff & b)
    if (child == null) {
      fn(index, bitsRequired)
      nodeManager.create(this, b, first) match {
        case Some(node) => children(0xff & b) = node
        case _ =>
      }
      root.encode(b, fn)
    } else child
  }

  def apply[T](fn: (Byte) => T) {
    owner.apply(fn)
    fn(value)
  }

  def root = owner.root

  def bitsRequired = owner.bitsRequired

  def terminate[T](fn: (Int, Int) => T) {
    fn(index, bitsRequired)
  }

}



class RootNode(limit: Int = 512) extends Node with NodeManager {

  private var index = 255
  private val initial = Array.tabulate[ValueNode](256)(b => new ValueNode(b, this, b.toByte, b.toByte, this))
  private val createdNodes = new Array[ValueNode](limit - 256)

  def decode[T](i: Int, fn: (Byte) => T) = {
    val node = initial(i)
    node.apply(fn)
    node
  }

  def encode[T](b: Byte, fn: (Int, Int) => T) = initial(0xff & b)
  def apply[T](fn: (Byte) => T) { }
  def terminate[T](fn: (Int, Int) => T) {}
  def first = 0
  val root = this

  def create(owner: Node, value: Byte, first: Byte) = if (index <= limit) {
    index += 1
    val node = new ValueNode(index, owner, value, first, this)
    createdNodes(index - 256) = node
    Some(node)
  } else {
    // No reset
    None
  }

  def get(value: Int): Option[Node] =
    if (value < 256) Some(initial(value))
    else if (value > index) None
    else Some(createdNodes(value - 256))

  def bitsRequired = 32 - Integer.numberOfLeadingZeros(index)

}



object Lzw extends App {

  /**
   * LZW encodes a sequence of bytes. Returns a non-strict collection of tuples, where the first element of the tuple
   * represents a value to be send to the output, and the second element the minimal number of bits expected to be
   * used for representing the output.
   *
   * Depending on the size of the index, the number of bits used to represent pointers to elements of the index can
   * vary (and will grow while encoding). There are different policies for dealing with this while writing the output
   * values. Some may prefer to always allocate a fixed number of bits for the output values,
   * while others might prefer to limit the number of bits written to the minimum needed. By explicitly passing the
   * number of bits required *minimally* to store the output value, callers can choose to implement any policy they
   * deem appropriate.
   *
   * @param in A collection of bytes.
   * @return A non-strict collection providing the values of the LZW encoded representation of the input collection.
   */

  println( " Hello World - LZW ")

  def encode(in: Traversable[Byte], limit: Int = 256): Traversable[(Int, Int)] =
    new Traversable[(Int, Int)] {
      def foreach[U](f: ((Int, Int)) => U) {
        val root: Node = new RootNode(limit)
        val untupled = Function.untupled(f)
        in.foldLeft(root)({ (node, b) => node.encode(b, untupled) }).terminate(untupled)
      }
    }

   System.out.println( encode("ABRACADADABRA".getBytes("utf-8"),500 ) )




}
