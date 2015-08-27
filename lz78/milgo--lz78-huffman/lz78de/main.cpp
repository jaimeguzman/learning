#include <cstdlib>
#include <iostream>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#define MAX_INDEKS 65536
#define MAX_FRAZA 1024

using namespace std;

class tfraza{
	public:
		char *fraza;
		int dl;
		
		tfraza(char *f, int d);
		tfraza(char f);
		~tfraza();
};

void test(void *p)
{
	if(!p)
	{
		cout << "Brak pamiêci!" << endl;
		exit(EXIT_FAILURE);
	}	
}

tfraza::tfraza(char *f, int d){
	test(fraza = new char[d]);
	memcpy(fraza, f, d);
	dl = d;
}

tfraza::tfraza(char f){
	test(fraza = new char);
	*fraza = f;
	dl = 1;
}

tfraza::~tfraza(){
	delete fraza;
}

tfraza *slownik[MAX_INDEKS];
long indeks;

void tworz_slownik()
{
	test(slownik[0] = new tfraza((char)0, 0));
	for(int i = 0; i < 256; i++){
		test(slownik[i + 1] = new tfraza(i));
	}
	indeks = 257;
}

void kasuj_slownik()
{
	for(long i = 0; i < indeks; i++){
		delete slownik[i];
	}
}

int main(int argc, char *argv[])
{
    FILE *we, *wy;
    
    we = fopen("wyjscie.lz78", "rb");
    if(!we){
    	cout << "Nie mogê otworzyæ pliku!" << endl;
    	exit(EXIT_FAILURE);
	}
	
	wy = fopen("wyjscie.txt", "wb");
	
	unsigned int i;
	int znak, dl;
	char fraza[MAX_FRAZA];
	
	tworz_slownik();
	
	for(;;){
		fread(&i, sizeof(int), 1, we);
		znak = fgetc(we);
		dl = slownik[i]->dl;
		
		memcpy(fraza, slownik[i]->fraza, dl);
		if(znak == EOF){
			fwrite(fraza, dl, 1, wy);
			break;
		}
		else fraza[dl++] = znak;
		
		fwrite(fraza, dl, 1, wy);
		if(indeks < MAX_INDEKS){
			test(slownik[indeks++] = new tfraza(fraza, dl));
		}
		else{
			kasuj_slownik();
			tworz_slownik();
		}
	}
	
	fclose(we);
	fclose(wy);
	kasuj_slownik();
    system("PAUSE");
    return EXIT_SUCCESS;
}
