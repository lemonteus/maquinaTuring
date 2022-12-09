# maquinaTuring

### Dupla:
Mateus Lemos de Freitas Barbosa

Rafael Augusto de Souza

## Compilar

Windows (cmd): ``javac -cp ".;json-java.jar" MaquinaTuring.java``

Linux (bash): ``javac -cp ".:json-java.jar" MaquinaTuring.java``

## Executar

Windows (cmd): ``java -cp ".;json-java.jar" MaquinaTuring [arquivo JSON da MT] [palavra]``

Linux (bash): ``java -cp ".:json-java.jar" MaquinaTuring [arquivo JSON da MT] [palavra]``

## ATENÇÃO 

No caso de executar o programa utilizando uma string vazia no Windows, Tanto o CMD quanto o Powershell possuem comportamentos problemáticos. Cada um possui uma forma diferente de processar os caracteres "", gerando erro na execução, mas isso é uma inconsistência dos terminais. No entanto, a execução no bash do Linux acontece normalmente. 

Por causa disso, fizemos uma única exceção no programa para considerar a sequencia "" como uma palavra vazia. Essa é a única exceção, pois para o restante das palavras, os terminais avaliam o parâmetro como uma expressão, eliminando as aspas duplas da string processada pelo programa. Para executar o programa utilizando uma string vazia no CMD, pode-se usar:

``java -cp ".:json-java.jar" MaquinaTuring [arquivo JSON da MT] \"\"``

No Powershell fazer isso é praticamente impossível, vide: https://stackoverflow.com/questions/6714165/powershell-stripping-double-quotes-from-command-line-arguments

