# SyntacticAnalyzer

Syntactic Analyzer made with Java.

Receives a phrase and a context free grammar in the Chomsky normal form as an argument in the form:
[phrase] [array of non terminals and their productions separated with | (pipe) and separated from each other with spaces]
Foe example:
aaabbbb S|AB|AC|DB|DC C|SB A|a D|a B|TB|b T|b

The program will try to identify if the format of the received data is correct. It will print to console the phrase and the non terminal and terminal arguments.
Then, it will follow the CYK algorithm to identify if the phrase can be generated with the given grammar and will print the result in a "staircase form".
Finally, it will indicate it the phrase could be generated or not.
