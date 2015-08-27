/* For struct bitio
 * data buffer -> 4096
 * read and write pointer -> unsigned
 * open mode -> 1 bit to say if we are in write or read mode
 * file descriptor -> int or FILE *
 */
// funziona 08/04/2015 17:54:01
// a= buf[i] & ~(((1LL<<k)-1)<<pos)
#include<errno.h>
#ifdef __APPLE__
   // #include "Endian.h"
 #include <libkern/OSByteOrder.h>
 #else
#include<endian.h> //letohost64
#endif
#include<stdlib.h>
#include<stdio.h>
#include<stdint.h>
#include<fcntl.h>
#include<unistd.h>
#define BITIO_BUF_WORDS 512
struct bitio
{
	int bitio_fd;
	int bitio_mode;
	unsigned bitio_rp, bitio_wp;
	uint64_t buf[ BITIO_BUF_WORDS ];
};
// uint64_t btole(uint64_t)

struct bitio *
bit_open(const char *name, char mode)
{
	struct bitio *ret=NULL;
	if (!name || (mode!='r' && mode!='w')) {
		errno=EINVAL;
		goto fail;
	}
	ret=calloc(1, sizeof(struct bitio)); //memory s cleared
	if (!ret) //rare case
		goto fail;
	ret->bitio_fd=open(name, mode=='r' ? O_RDONLY : O_WRONLY | O_TRUNC | O_CREAT, 0666); // read & write for everyone
	if (ret->bitio_fd<0)
		goto fail;
	ret->bitio_mode=mode;
	ret->bitio_rp=ret->bitio_wp;
	return ret;

fail: // undo partial action
	if (ret) {
		free(ret);
	}
	if (ret!=NULL)
	{
		ret->bitio_fd=0; // non necessario in un sistema corretto
		free(ret);
		ret=NULL;
	}
	return NULL; // errno will contain the error code
}

// return 0 if ok
// return how many bits was written
int
bit_flush(struct bitio *b)
{
	int len_bytes, x;
	char *start;
	int left;
	if (!b || (b->bitio_mode != 'w') || (b->bitio_fd < 0))
	{
		errno=EINVAL;
		return -1;
	}
	len_bytes=b->bitio_wp/8;
	if (len_bytes == 0) // se non possiamo scrivere almeno un Byte finiamo
		return 0;
	start=(char*)b->buf;
	left=len_bytes;
	for(;;)
	{
		x=write(b->bitio_fd, start, left);
		if (x<0)
			goto fail;
		start+=x;
		left-=x;
		if (left==0)
			break;
	}
	if (b->bitio_wp%=8)
	{
		char *dst=(char *)b->buf;
		dst[0]=start[0];
	}
	return len_bytes*8;

fail: ;
	char *dst=(char *)b->buf;
	int i;
	for (i=0; i<left; i++)
		dst[i]=start[i];
	if (b->bitio_wp%8)
		dst[i]=start[i];
	b->bitio_wp-=(start-dst)*8;
	return (start-dst)*8;
}

char
remove_padding(char *last_byte) {
	//int last_bit=(*last_byte) & (((char)1)<<7);
	int8_t count;
	uint8_t num_bit_padding;
	for (count=6; count>=0; count--)
		if ( !(((*last_byte) & (((uint8_t)1)<<count))) != !(((*last_byte) & (((uint8_t)1)<<(count+1)))) )
			break ;
	num_bit_padding=8-count-1;
	
//	printf("last bit: %d\t#bit padding: %d\n", last_bit, num_bit_padding);
	return num_bit_padding;
}

uint64_t
bit_read(struct bitio *b, unsigned int nb, int *stat)
{
	*stat=0;
	if (nb==0)
		return 0;
	if (nb>64)
		nb=64;
	if (!b || b->bitio_mode!='r')
		goto fail;
	int x;
	unsigned int pos, ofs;
	uint64_t r1, r2=0, bit_da_scrivere_old=0, d;
	do {
		if (b->bitio_rp==b->bitio_wp) {
			// read from file
			b->bitio_rp=b->bitio_wp=0;
			for (;;)
			{
				x=read(b->bitio_fd, (void *)b->buf, sizeof(b->buf));
//				printf("byte letti: %d\n", x);
				if (x<0) {
					*stat= (*stat)*(-1);
					return r2;
				}
				if (x==0) { // EOF reached
					*stat= (*stat)*(-2);
					return r2; // continue
				}
				if (x>0)
				{
					b->bitio_wp=x*8;
					//printf("wp prima: %d\n", b->bitio_wp);
					if (x!=sizeof(b->buf) /*|| feof()*/) //{ //delete padding
					//	printf("last byte: %d\n", *(((char*)b->buf)+x-1));
						b->bitio_wp-=remove_padding( ((char*)b->buf)+x-1 );
						//printf("wp dopo: %d\n", b->bitio_wp);
					//}
				
					break;
				}
			}
		}
		pos=b->bitio_rp/(sizeof(b->buf[0])*8);
		ofs=b->bitio_rp%(sizeof(b->buf[0])*8);
		#ifdef __APPLE__
    		d=OSReadLittleInt64(&b->buf[pos],0);
 		#else
 		d=le64toh(b->buf[pos]);
		#endif
		
		unsigned int bit_da_scrivere= (b->bitio_wp - b->bitio_rp >= 64) ? ( (64-ofs)<nb ? (64-ofs) : nb ) : ( (b->bitio_wp - b->bitio_rp)<nb ? (b->bitio_wp - b->bitio_rp) : nb ) ;
		uint64_t a=(bit_da_scrivere==64) ? (0xFFFFFFFFFFFFFFFF) : ( (((uint64_t)1)<<bit_da_scrivere)-1 );
		a=a<<ofs;
		r1=(d&a)>>ofs;
//		d=d>>ofs;
//		r1=d&a;
		r2|=r1<<bit_da_scrivere_old;
//		r|=( (d&((((uint64_t)1)<<(((64-ofs)<nb ? (64-ofs) : nb)-1))-1))<<ofs )>>ofs;
	
		/*if (x)
			d|=((uint64_t)1)<<ofs;
		else
			d&=~(((uint64_t)1)<<ofs);
		*/
		*stat=(*stat)+bit_da_scrivere;
		bit_da_scrivere_old=bit_da_scrivere;
		b->bitio_rp+=bit_da_scrivere;
		//b->buf[pos]=htole64(d);
	} while (nb!=(*stat)); //(nb-=((64-ofs)<nb ? (64-ofs) : nb));
	return r2;
	/*int pos=b->bitio_rp/(sizeof(b->buf[0])*8);
	int ofs=b->bitio_rp%(sizeof(b->buf[0])*8);
	uint64_t d=le64toh(b->buf[pos]);
	d&=(((uint64_t)1)<<ofs);
	b->bitio_rp++;
	return d?1:0;*/
fail:
	//do ...
	return -1;
}

int
bit_write(struct bitio *b, uint64_t x, unsigned int nb)
{
	if (nb==0)
		return 0;
	if (nb>64*8) // sizeof(uint64_t)*8
		nb=64*8;
	if (!b || b->bitio_mode!='w')
		goto fail;

	unsigned int pos, ofs, nb2=nb;
	uint64_t d;
	do {
		if (b->bitio_wp==sizeof(b->buf)*8)
			bit_flush(b);

		pos=b->bitio_wp/(sizeof(b->buf[0])*8);
		ofs=b->bitio_wp%(sizeof(b->buf[0])*8);
		#ifdef __APPLE__
    		d=OSReadLittleInt64(&b->buf[pos],0);
 		#else
 			d=le64toh(b->buf[pos]);
		#endif

		unsigned int bit_da_scrivere=(64-ofs)<nb ? (64-ofs) : nb;
		uint64_t a= (bit_da_scrivere==64) ? (0xFFFFFFFFFFFFFFFF) : ( (((uint64_t)1)<<bit_da_scrivere)-1 );
		uint64_t x2=x&a; // pulisco x
		x2=x2<<ofs; // allineo x2 con d
		d&=~(a<<ofs); // pulisco d
		d|=x2;
//		d|=(x&((((uint64_t)1)<<(((64-ofs)<nb ? (64-ofs) : nb)+1))-1))<<ofs;
	
		/*if (x)
			d|=((uint64_t)1)<<ofs;
		else
			d&=~(((uint64_t)1)<<ofs);
		*/
		x=x>>bit_da_scrivere;
		b->bitio_wp+=(64-ofs)<nb ? (64-ofs) : nb; //bit_da_scrivere;
		#ifdef __APPLE__
			OSWriteLittleInt64(&b->buf[pos],0,d);
		#else
			b->buf[pos]=htole64(d);
		#endif
	} while (nb-=((64-ofs)<nb ? (64-ofs) : nb));
	return nb2;

fail:
	return -1;
}

int
bit_close(struct bitio *b)
{
	// check errors
	if (b->bitio_mode=='w' && b->bitio_wp!=0)
	{
		char *des=(char *)b->buf;
		char fill, d;
		if (b->bitio_wp%8) {
			fill=des[b->bitio_wp/8]; //
			d=fill & (((char)1)<<((b->bitio_wp-1)%8));
		}
		else {
			fill=des[(b->bitio_wp/8)-1]; //
			d=fill & 0x80;
		}
		int i; //, c=b->bitio_wp%8;
/*		if (d) // -------------------------------------
		printf("d=1\n"); // -------------------------------------
		else // -------------------------------------
		printf("d=0\n"); // -------------------------------------
*/		for (i=b->bitio_wp%8; i<8; i++) // i=c
			if (d) // last valid bit=1
				fill &= ~(((char)1)<<i);
			else
				fill |= (((char)1)<<i);
//		printf("fill=%02x\t%u\n", fill, (unsigned int)sizeof(fill)); // ---------
		des[b->bitio_wp/8]=fill;
		b->bitio_wp+=8-(b->bitio_wp%8);
		
		//aggiustare padding
		//if (bit_flush(b)<0 || b->bitio_wp>0) // if (flush(b)!=8) goto fail;
		
		bit_flush(b);
		if (!b->bitio_wp)
			goto fail;
	}
	close(b->bitio_fd);
	b->bitio_fd=0xFFFFFFFF;
	free(b);
	return 0;

fail:
	close(b->bitio_fd);
	return 1;
}
