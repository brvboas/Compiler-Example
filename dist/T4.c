#include <stdio.h>

enum boolean {
    true = 1, false = 0
};
typedef  enum boolean  bool;

void principal() {
   int i;
   int j;
   bool d;
   bool e;
   bool f;
   scanf("%d",&i);
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
}


int abc(int n, bool abcc) {
   int a;

   return a + n;
}


void joao(int abc) {
   int i;
   i = 40;
   abc = abc + i;
   printf("%d",abc);
}


