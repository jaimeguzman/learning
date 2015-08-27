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
