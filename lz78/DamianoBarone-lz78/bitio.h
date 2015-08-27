#ifndef _BITIO_H_12345931_
#define _BITIO_H_12345931_
#include<stdint.h>
/*
 * placement of bits in the buffer
 */
struct bitio;

struct bitio * bit_open(const char *name, char mode);
int bit_write(struct bitio *b, uint64_t x, unsigned int nb);
uint64_t bit_read(struct bitio *b, unsigned int nb, int *stat);
int bit_close(struct bitio *b);
int bit_flush(struct bitio *b);
char remove_padding(char *last_byte);

#endif /* ! _BITIO_H_12345931_ */
