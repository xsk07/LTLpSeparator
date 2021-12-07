# LTLpSeparator

LTLpSeparator is a tool for the separation of LTLp formulae into a combination of pure, past, present and future formulae.
The separation algorithm is based on the Gabbay' Separation Theorem.
Formulae could be passed as text files or directly typed on the command line.
The result can be saved as simple text file, as an image depicting the tree representation of the formula.
Furthermore, LTLpSeparator can generate the corresponding separated automata set.

# Prerequisites

This tool uses Graphviz for the generation of the images depicting the tree representation of formulae, hence you should first install Graphviz on your system following the instructions <a href="https://graphviz.org/" title="Graphviz">here</a>.
For the generation of DFAs, LTLpSeparator uses LTLf2DFA with all its dependencies, its installation instructions are available at <a href="https://github.com/whitemech/LTLf2DFA" title="LTLf2DFA">LTLf2DFA github page</a>.

# Features

- Syntax and parsing support for the Linear Temporal Logic with Past operators (LTLp);
- Conversion of LTLp formulae into an equivalent form with only the temporal operators U and S;
- Visualization of the formulae as trees;
- Visualization of pure formulae as DFAs;
- Generation of the separated automata set corresponding to the separated formula; 

# Contact

Please contact the developer, Seweryn Kaniowski, for any information, comment or bug reporting:
severinokaniowski@mail.com
 
