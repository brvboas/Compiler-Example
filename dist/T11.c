Error at line 27: 
j <- abc(j;d);
Wrong number of parameters
Error at line 41: 
joao(d);
Type error in parameter passing
#include <stdio.h>

enum boolean {
    true = 1, false = 0
};
typedef  enum boolean  bool;

int abc(int n, bool abcc, char teste) {
   int a;

   return a + n;
}


void joao(int abc) {
   int i;
   i = 40;
   abc = abc + i;
   printf("%d",abc);
}


void principal() {
   int i;
   int j;
   bool d;
   bool e;
   bool f;
   scanf("%d",&i);
   j = abc(j, d);
   if(i > 10){
      printf("%d",i);
   }
   else{
      printf("%c",'<'      );
   }
   while (i < 10){
      printf("%c",'<'      );
      i = i + 1;
   }
   joao(d);
}


