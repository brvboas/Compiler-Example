-- T12 - função sem valor de retorno deve gerar erro

--exemplo de funcao
funcao inteiro abc(inteiro n;boleano abcc)
inteiro a;
inicio
a <- n + 5;
fim

--exemplo de funcao
funcao inteiro xyz(inteiro n;boleano abcc)
inteiro a;
inicio
a <- n + 5;  retorna 1;
fim


--principal
procedimento principal() 
inteiro i ,j;
boleano d,e,f;
inicio
leia(i);
j <- abc(j;d);
se(i > 10)
entao
escreva(i);

senao
escreva('<');
fimse;

enquanto (i < 10) faca
escreva('<');
i <- i+1;
fimenquanto;

  fim