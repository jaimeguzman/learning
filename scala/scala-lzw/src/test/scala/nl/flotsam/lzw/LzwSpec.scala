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

import org.specs2.mutable.Specification

class LzwSpec extends Specification {

  "Lzw" should {

    "be able to encode a sequence of bytes appropriately" in {
      Lzw.encode("sir sid eastman easily teases sea sick seals".getBytes("utf-8")).toList must be equalTo(
        List(
        (115, 8), (105, 9), (114, 9), (32, 9), (256, 9), (100, 9), (32, 9), (101, 9), (97, 9), (115, 9),
        (116, 9), (109, 9), (97, 9), (110, 9), (262, 9), (264, 9), (105, 9), (108, 9), (121, 9), (32, 9),
        (116, 9), (263, 9), (115, 9), (101, 9), (115, 9), (259, 9), (263, 9), (259, 9), (105, 9), (99, 9),
        (107, 9), (281, 9), (97, 9), (108, 9), (115, 9)
      ))
    }

    "be able to decode a sequence of Ints" in {
      val in = "sir sid eastman easily teases sea sick seals".getBytes("utf-8")
      val encoded = Lzw.encode(in)
      val decoded = Lzw.decode({
        _: (() => Int) => encoded.map(_._1)
      })
      in.toList must be equalTo decoded.toList
    }

  }

}
