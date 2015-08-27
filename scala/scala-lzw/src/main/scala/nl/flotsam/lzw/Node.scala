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

trait Node {

  def decode[T](i: Int, fn: (Byte) => T): Node

  def encode[T](b: Byte, fn: (Int, Int) => T): Node

  def apply[T](fn: (Byte) => T)

  def root: Node

  def bitsRequired: Int

  def terminate[T](fn: (Int, Int) => T)

  def first: Byte

}
