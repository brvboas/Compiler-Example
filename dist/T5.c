Error at line 10: 
i <- d;
Type error in assignment
Error at line 11: 
j <- h;
Type error in assignment
Error at line 15: 
a<- 'a';
Type error in assignment
Error at line 19: 
f <- 33;
Type error in assignment
#include <stdio.h>

enum boolean {
    true = 1, false = 0
};
typedef  enum boolean  bool;

void principal() {
   int i;
   int j;
   int a;
   int b;
   int c;
   bool d;
   bool e;
   bool f;
   char g;
   char h;
   char k;
   i = d;
   j = h;
   b = 3;
   a = b;
   a = a + a;
   a = 'a';
   d = 1;
   e = 0;
   f = e;
   f = 33;
   g = 's';
   h = g;
   k = h;
}


