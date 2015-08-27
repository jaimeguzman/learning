#include <iostream>
#include <stdio.h>
#include <string.h>
/* run this program using the console pauser or add your own getch, system("pause") or input loop */

using namespace std;

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
char bajt = 0;
int il_drzew = 0, poz = -1;
long bits;

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

char wczyt_bit(){
	if(poz == -1){
		bajt = fgetc(we);
		poz = 7;
	}
	--bits;
	return bajt & (1 << poz--);
}

void wczytaj_wagi(){
	int i, z;
	long waga;
	
	fread(&il_drzew, sizeof(int), 1, we);
	for(i=0; i < il_drzew; i++){
		z = fgetc(we);
		fread(&waga, sizeof(long), 1, we);
		drzewo[z] = new tdrzewo(z, waga, NULL, NULL);
	}
}

void dekoduj(tdrzewo *d){
	tdrzewo *temp;
	
	while(bits > 0){
		temp = d;
		while(temp->znak == -1){
			if(wczyt_bit())
				temp = temp->lw;
			else
				temp = temp->pr;
		}
		fputc(temp->znak, wy);
	}
}


int main(int argc, char** argv) {
	tdrzewo *huff;
	long ofs;
	
	we = fopen("wyjscie.huff", "rb");
	if(!we){
		cout << "Brak pliku wejsciowego" << endl;
		return 1;
	}
	
	wy = fopen("Data2.txt", "wb");
	wczytaj_wagi();
	huff = buduj();
	fread(&bits, sizeof(long), 1, we);
	dekoduj(huff);
	delete huff;
	
	fclose(we);
	fclose(wy);

	return 0;
}
