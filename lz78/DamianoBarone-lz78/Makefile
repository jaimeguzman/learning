all: lz78
clean:
	rm *o lz78

bitio.o: bitio.h bitio.c
	cc -c -O2 -Wall -Werror bitio.c 
lz78_compressor.o: bitio.h lz78.h lz78_compressor.c
	cc -c -O2 -Wall -Werror lz78_compressor.c
lz78_decompressor.o: bitio.h lz78.h lz78_decompressor.c
	cc -c -O2 -Wall -Werror lz78_decompressor.c

lz78: bitio.o lz78_compressor.o lz78_decompressor.o
	cc -o lz78  lz78.c bitio.o lz78_compressor.o lz78_decompressor.o
