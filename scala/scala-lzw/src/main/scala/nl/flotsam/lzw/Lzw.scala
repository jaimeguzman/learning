/*
 * Scala LZW
 * Copyright (C) 2012, Wilfred Springer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.flotsam.lzw

object Lzw {

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
  def encode(in: Traversable[Byte], limit: Int = 256): Traversable[(Int, Int)] =
    new Traversable[(Int, Int)] {
      def foreach[U](f: ((Int, Int)) => U) {
        val root: Node = new RootNode(limit)
        val untupled = Function.untupled(f)
        in.foldLeft(root)({ (node, b) => node.encode(b, untupled) }).terminate(untupled)
      }
    }

  /**
   * LZW decodes a sequence of integer numbers. The important thing to remember is that LZW defines a mapping from a
   * sequence of bytes to pointers to an index - with the index being built on the fly. There is a *direct* relation
   * between the number of elements in the index, and the number of bits required to represent pointers into that
   * index. If the number of elements in the index is 512, every pointer requires at least 9 bits.
   *
   * However, that does not mean that every pointer is always encoded the minimal number of bits required to make LZW
   * work. There are different policies that could work. You could for instance implement a scheme that always uses
   * two bytes. You could gain compression, but loose ease of implementation, and potentially also (some) speed.
   *
   * Because of all of this, this method does not assume a particular way in which LZW output values are encoded into
   * bytes. It leaves that to the callers of the decode operation. The factory accepted creates the Traversable from
   * which the integer values are read. The factory itself accepts a function that allows the factory to pass a
   * reference to a function that informs it of the number of bits required at any given moment. Implementations can
   * choose to ignore this function, and always construct the integer values from the same number of bits. Or they
   * can use it and leverage the information provided by the decoder.
   *
   * @param factory The factory constructing a Traversable of integer values, used as input for the decode operation.
   * @return A Traversable providing the decoded version of the values passed in.
   */
  def decode(factory: (() => Int) => Traversable[Int], limit: Int = 256): Traversable[Byte] = {
    new Traversable[Byte] {
      def foreach[U](f: (Byte) => U) {
        val root: Node = new RootNode(limit)
        val in = factory({ () => root.bitsRequired })
        in.foldLeft(root)({ (node, i) => node.decode(i, f)})
      }
    }
  }

}
