#include <iostream>
#include <stdio.h>
#include <string.h>
/* run this program using the console pauser or add your own getch, system("pause") or input loop */

using namespace std;

class tznak
{
	public:
		int dl;
		char *kod;
		tznak(int d, char *k);
		~tznak();
};

tznak::tznak(int d, char *k)
{
	dl = d;
	kod = new char[d];
	memcpy(kod, k, d);
}

tznak::~tznak(){
	delete kod;
}

class tdrzewo{
	public:
		long waga;
		int znak;
		tdrzewo *lw, *pr;
		tdrzewo(int z, long w, tdrzewo *l, tdrzewo *p);
		~tdrzewo();
};

tdrzewo::tdrzewo(int z, long w, tdrzewo *l, tdrzewo *p){
	znak = z;
	waga = w;
	lw = l;
	pr = p;
}

tdrzewo::~tdrzewo(){
	if(lw) delete lw;
	if(pr) delete pr;
}

FILE *we, *wy;
tdrzewo *drzewo[256];
tznak *znak[256];
char kod[256], bajt = 0;
int il_drzew = 0, poz = 7;
long bits = 0;

void skanuj_we(){
	int z;
	
	il_drzew = 0;
	for(;;){
		z = fgetc(we);
		if(z == EOF){
			break;
		}
		if(!drzewo[z]){
			drzewo[z] = new tdrzewo(z, 1, NULL, NULL);
			++il_drzew;
		}
		else{
			++drzewo[z]->waga;
		}
	}
}

void zap_wagi(){
	fwrite(&il_drzew, sizeof(int), 1, wy);
	for(int i = 0; i < 256; i++){
		if(drzewo[i]){
			fputc(i, wy);
			fwrite(&drzewo[i]->waga, sizeof(long), 1, wy);
		}
	}
}

tdrzewo *szukaj(){
	int i, j;
	tdrzewo *d;
	long min = 2147483647;
	
	for(i = 0; i < 256; i++){
		if(drzewo[i] && drzewo[i]->waga < min){
			min = drzewo[i]->waga;
			d = drzewo[i];
			j = i;
		}
	}
	
	drzewo[j] = NULL;
	return d;
}

void dodaj(tdrzewo *d){
	int i;
	for(i=0; drzewo[i]; i++);
	drzewo[i] = d;
}

tdrzewo *buduj(){
	tdrzewo *d1, *d2, *d;
	while(il_drzew-- > 1){
		d1 = szukaj();
		d2 = szukaj();
		dodaj(new tdrzewo(-1, d1->waga + d2->waga, d1, d2));
	}
	return drzewo[0];
}

void generuj_kody(tdrzewo *d, int dl){
	int z;
	
	if((z = d->znak) != -1){
		znak[z] = new tznak(dl, kod);
		return;
	}
	
	kod[dl] = 1;
	generuj_kody(d->lw, dl+1);
	kod[dl] = 0;
	generuj_kody(d->pr, dl+1);
}

void zap_bit(char bit){
	bajt |= bit << poz--;
	if(poz == -1){
		fputc(bajt, wy);
		bajt = 0;
		poz = 7;
	}
	++bits;
}

void koduj(){
	int z, i;
	for(;;){
		z = fgetc(we);
		if(z == EOF)break;
		for(i = 0; i < znak[z]->dl; i++)
			zap_bit(znak[z]->kod[i]);
	}
	if(poz < 7)
		fputc(bajt, wy);
}

void kasuj_kody(){
	for(int i=0; i<256; i++){
		if(znak[i])delete znak[i];
	}
}

int main(int argc, char** argv) {
	tdrzewo *huff;
	long ofs;
	
	we = fopen("temp.dat", "rb");
	if(!we){
		cout << "Brak pliku wejsciowego" << endl;
		return 1;
	}
	
	wy = fopen("wyjscie.huff", "wb");
	skanuj_we();
	fseek(we, 0, SEEK_SET);
	zap_wagi();
	huff = buduj();
	generuj_kody(huff, 0);
	ofs = ftell(wy);
	fseek(wy, sizeof(long), SEEK_CUR);
	koduj();
	fseek(wy, ofs, SEEK_SET);
	fwrite(&bits, sizeof(long), 1, wy);
	fclose(we);
	fclose(wy);
	kasuj_kody();
	
	return 0;
}
