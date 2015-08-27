# README

A basic implementation of LZW, in Scala. The ambition is to make some of its behavior pluggable, in order to allow it be
used for PDF LZW decoding.

## How is this different from anything else out there?

I don't know really. But I could imagine that it's different in a number of ways:

* This implementation does not just output pointers to index values, but also the number of bits expected to be
required to store the index values. The reason for it is that some encoding formats (like PDF) will grow the number
of bits used for storing index references automatically, based on need. By just outputting the integer values,
you wouldn't be able to tell if the encoder reached a stage in which this is needed. With this version of an LZW
encoder, you can tell.

* This implementation returns a non-strict collection. That is, it doesn't actually start decoding the data unless
you peek into the output collection.

* This implementation consumes data from a collection in a way that allows the input to be informed of the number of
bits required. Related to what I said before. While decoding data, you want to be informed on the number of bits from
which you need to construct index values.

* Both encoding and decoding are coded as `foldLeft` calls on the input collections.

* It uses the same data structure both for encoding and decoding. (Although this might actually be something I will
change.)

## What is missing?

Convenience wrappers around the `Lzw.encode` and `Lzw.decode` operations.

## License


