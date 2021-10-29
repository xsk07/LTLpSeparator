# LTLfSeparator

LTLfSeparator is a tool for the separation of LTLpf formulae into triples of pure past, pure present and pure future automata.
It is based on Linear Temporal Logic and the Gabbay Separation Theorem.
Formulae could be passed as text files or directly typed on the command line.
The result can be save as a simple text file, as a tree representation image of the formula or as triples of pure past, pure present and pure future automata.

# Prerequisites

This tool uses Graphviz for the generation of the formulae trees, hence you should first install Graphviz on your system following the instructions 
<a href="https://graphviz.org/" title="Graphviz">here</a>, and LTLf2DFA with all its dependencies for the generation of the DFAs.
The instructions are available at <a href="https://github.com/whitemech/LTLf2DFA" title="LTLf2DFA">LTLf2DFA github page</a>.
# Features

- Syntax and parsing support for the Linear Temporal Logic with Past operators over Finite traces (LTLpf);
- Conversion of LTLpf formulae containing unary temporal operators into an equivalent form of the US temporal logic;
- Visualization of the formulae as trees;
- Separation of LTLpf formulae into triples of pure past, pure present and pure future ones, written using booleans and the connectives US only, 
it is also possible to view the elimination rules of the Separation Theorem used by the algorithm, and to generate the respective triple of automata;
- Visualization of pure formulae as DFA;
