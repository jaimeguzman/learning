#include <cstdlib>
#include <iostream>
#include <fstream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sstream> 

#define MAX_INDEKS 65536

using namespace std;

//How can I set the comma to be a decimal point?
//http://stackoverflow.com/questions/15220861/how-can-i-set-the-comma-to-be-a-decimal-point

class tznak
{
	public:
		unsigned int n;
		tznak *nast[256];
		tznak();
};

tznak *p[MAX_INDEKS];

long indeks;
tznak *start;

tznak::tznak()
{
	p[indeks] = this;
	n = indeks++;
	for (int i = 0; i < 256; i++)
	{
		nast[i] = NULL;
	}
}

void test(void *p)
{
	if(!p)
	{
		cout << "Brak pamiêci!" << endl;
		exit(EXIT_FAILURE);
	}	
}

void tworz_slownik()
{
	indeks = 0;
	test(start = new tznak());
	for(int i = 0; i < 256; i++)
	{
		test(start->nast[i] = new tznak());
	}
}

void kasuj_slownik()
{
	for(long i = 0; i < indeks; i++)
	{
		delete p[i];
	}
}

int main(int argc, char *argv[])
{
	/*string line;
  	ifstream inputfile ("data2.txt");
  	ofstream tempfile;
  	tempfile.open("temp.dat", ios::binary);
  	
  	stringstream ss(line);
  	stringstream sconv;
  	string token1, token2;
  	double d1, d2;
  	
 	if (inputfile.is_open())
  	{
    	while ( getline (inputfile, line) )
    	{
    		ss.str(std::string());
    		ss.clear();
			ss << line;
      		getline(ss, token1, ',');
      		getline(ss, token2, ',');
      		
      		sconv.clear();
      		sconv << token1;
      		sconv >> d1;
      		
      		sconv.clear();
      		sconv << token2;
      		sconv >> d2;
      		
      		tempfile.write( reinterpret_cast<char*>( &d1 ), sizeof d1 );
			tempfile.write( reinterpret_cast<char*>( &d2 ), sizeof d2 );
			
      		cout << fixed << d1 ;
			cout << ", " << fixed << d2 << endl;

    	}
    	inputfile.close();
    	tempfile.close();
	}
	else cout << "Unable to open file"; */


    FILE *we, *wy;
    
	we = fopen("temp.dat", "rb");
    if(!we){
    	cout << "Nie mogê otworzyæ pliku!" << endl;
    	exit(EXIT_FAILURE);
	}
	
	wy = fopen("wyjscie.lz78", "wb");
	
	int znak;
	tznak *temp;
	
	tworz_slownik();
	temp = start;
	
	for(;;){
		znak = fgetc(we);
		if(znak == EOF){
			fwrite(&temp->n, sizeof(int), 1, wy);
			break;
		}
		if(temp->nast[znak]){
			temp = temp->nast[znak];
		}
		else{
			fwrite(&temp->n, sizeof(int), 1, wy);
			fputc(znak, wy);
			if(indeks < MAX_INDEKS){
				test(temp->nast[znak] = new tznak());
			}
			else{
				kasuj_slownik();
				tworz_slownik();
			}
			temp = start;
		}
	}
	
	fclose(we);
	fclose(wy);
	kasuj_slownik();
    system("PAUSE");
    return EXIT_SUCCESS;
}
