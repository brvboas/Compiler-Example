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
   j = 7;
   if(i > 10){
      printf("%d",i);
   }
   else{
      printf("%c",'<'      );
   }
}


