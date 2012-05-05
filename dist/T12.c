Error at line 11: 
função: int abc(...)
A função necessita de retorno
#include <stdio.h>

enum boolean {
    true = 1, false = 0
};
typedef  enum boolean  bool;

int abc(int n, bool abcc) {
   int a;

   a = n + 5;
}


int xyz(int n, bool abcc) {
   int a;

   a = n + 5;
   return 1;
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
}


