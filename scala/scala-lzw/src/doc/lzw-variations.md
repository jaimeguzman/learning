# LZW Variations

LZW implementations vary in a number of ways. 

* Difference in the maximum number of bits allocated for the codes pointing to sequences of bytes.
* Some implementations reserve a fixed number of bits to represent these codes; other implementations slowly increase the number of bits to represent these codes, based on need.
* Some implementations reserve a specific code to represent a 'reset', also known as a 'clear code'. (PDF's LZW filter relies on this.)
* Some implementations reseve another specific code to represent the end of data, or EOD. (PDF's LZW filter relies on this.) 
* Some implementations offer support for 'Early Change'.
* Some implementations offer support for LSB or MSB encoding of the output.
* Some implementations offer support for input values smaller than a byte - which is useful if you use character encoding schemes that use only some characters.
