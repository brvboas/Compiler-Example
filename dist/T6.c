#include <stdio.h>

enum boolean {
    true = 1, false = 0
};
typedef  enum boolean  bool;

void principal() {
   int i;
   bool d;
   bool e;
   char f;
   char g;
   f = 'a';
   g = 'b';
   d = 1;
   e = 0;
   while (i < 10){
      printf("%c",'!'      );
   }
   while (f == g){
      printf("%c",'<'      );
   }
   while (d != e){
      printf("%c",'>'      );
   }
}


