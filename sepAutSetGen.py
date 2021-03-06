import sys
import os
import json
import shutil
import pydot
from ltlf2dfa.parser.ltlf import LTLfParser
from ltlf2dfa.parser.pltlf import PLTLfParser

def main():
    ''' create a directory called automata
    if such directory already exists,
    delete it and its content and create it again '''
    if(os.path.exists('automata')):
        shutil.rmtree('automata')
    os.mkdir('automata')

    ''' read the json file containing the matrix representation
    of the SNF of the formula and for each row of the matrix create a directory  '''

    with open('matrix.json') as f:
        matrix = json.load(f)

    for i in range(len(matrix)):
        os.mkdir('automata/{!s}'.format(i+1))
        save_dfa(matrix[i][0], i+1, 'past')
        save_dfa(matrix[i][1], i+1, 'present')
        save_dfa(matrix[i][2], i+1, 'future')


def save_dfa(phi, row, time):
    if(time == 'past'): parser = PLTLfParser()
    else: parser = LTLfParser()
    formula = parser(phi)
    dfa = formula.to_dfa()
    graphs = pydot.graph_from_dot_data(dfa)
    graph = graphs[0]
    graph.write_png('automata/{!s}/{}.png'.format(row, time))


if __name__ == "__main__":
    main()



