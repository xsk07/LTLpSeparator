# LTLpfSeparator

LTLpfSeparator is a tool for the separation of LTLpf formulas into a triple of pure past, pure present and pure future ones.
It is based on the Linear Temporal Logic and the Gabbay Separation Theorem.
Formulae could be passed as plain text files or directly typed on the command line.
The results can be save as a simple text file, as a tree representation image of the formula or as triple of pure past, pure present and pure future automatas.

# Prerequisites

This tool uses Graphviz for the generation of the formulas trees, hence you should first install Graphviz on your system following the instructions 
<a href="https://graphviz.org/" title="Graphviz">here</a>, and LTLf2DFA with all its dependencies for the generation of the DFAs.
The instructions are available at <a href="https://github.com/whitemech/LTLf2DFA" title="LTLf2DFA">LTLf2DFA github page</a>.
# Features

- Syntax and parsing support for the Linear Temporal Logic with Past operators over Finite traces (LTLpf);
- Conversion of LTLpf formulas containing unary temporal operators into an equivalent form of the US temporal logic;
- Visualization of the formulae as trees;
- Separation of LTLpf formulas into a triple of pure past, pure present and pure future ones, 
built up using only basic boolean and the two binary temporal operators: Since and Until, it is also possible to view 
the elimination rules of the Separation Theorem applied by the algorithm, and to generate the respective triple of automatas;
- Visualization of pure formulae as DFAs;
