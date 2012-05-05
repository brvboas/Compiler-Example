Error at line 5: 
procedimento principal(inteiro abc) 
principal must be a parameterless procedure
#include <stdio.h>

enum boolean {
    true = 1, false = 0
};
typedef  enum boolean  bool;

void principal(int abc) {
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


