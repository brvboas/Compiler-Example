-- T4 - teste que verifica se aceita texto após fim de código (por exemplo, depois do } da função main)

--principal
procedimento principal() 
inteiro i ,j;
boleano d,e,f;
inicio
leia(i);
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

--exemplo de funcao
funcao inteiro abc(inteiro n;boleano abcc)
inteiro a;
inicio
retorna a+n;
fim

--exemplo de procedimento
procedimento joao(inteiro abc)
inteiro i;
inicio
--exemplos de atribuicao
i<- 40;
abc <- abc + i;
escreva(abc);
fim 