Práctica 2 - Matemáticas Computacionales - 14/10/29
Carlos Ernesto López Solano A01633683

---------[Analizador Sintáctico]-----------

------/Descripción:

El programa adjunto se encarga de determinar si es posible formar
una cadena con una gramática brindada.
El programa recibirá la cadena y la gramática y verificará si
fueron correctamente ingresadas y cumplen con el formato estipulado.
Después de validar las entradas, imprimirá la gramática con el
formato original y a través del algoritmo CYK formará una tabla
para determinar si la cadena puede ser formada por la gramática,
e imprimirá la tabla y la conclusión a la que llegó.


------/Parámetros:

El docker recibirá como parámetros una cadena (que se verificará
si pertenece a las gramáticas brindadas) y 1 o más gramáticas
(sobre las cuales será probada la cadena), separado por espacios ' '.
La cadena puede ser cualquier conjunto de símbolos (excepto el
espacio ' ').
Las gramáticas deben tener por lo menos una producción y seguir
el siguiente formato:

[Símbolo Productor|Producción1|Producción2|...|ProducciónN]

Por ejemplo:

aabbab S|AB|A|B A|a B|b

lo cual representa:

Cadena: aabbab
S->AB|A|B
A->a
B->b

Por último, el programa solo recibe gramáticas en la forma normal
de Chomsky, por lo que cada producción deberá ser una únicamente
un caractér o una letra minúscula o dos letras mayúsculas (dos
símbolos generadores).

NOTAS:
Es necesario ingresar los parámetros de la manera indicada,
o de lo contrario el programa no funcionará y deberá iniciar la
máquina otra vez.
Además, cabe denotar que SIEMPRE el primer parámetro recibido
deberá ser la cadena y los demás parámetros deberán ser las
gramáticas.


------/Modo de uso:

La manera más sencilla de ejecutar el archivo Docker es a través de la terminal
de Docker, método que se explica a continuación:
1. Abra Docker Terminal
2. Teclee el comando: 

docker build -t Practica2 .

para crear la imagen del Docker

3. Teclee el comando:

docker run -it Practica2 [Cadena a buscar] [Gramáticas a ingresar]

para correr el docker con los parámetros ingresados.
Utilice comillas dobles (") para delimitar los string's y espacios (' ') para
separar los parámetros.

4. Para detener la máquina, deberá presionar CTRL + C.
