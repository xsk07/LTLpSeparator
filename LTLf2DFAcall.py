import sys
import os
import json
import shutil
import pydot
from ltlf2dfa.parser.ltlf import LTLfParser

def save_dfa(phi, row, time):
    parser = LTLfParser()
    formula = parser(phi)
    dfa = formula.to_dfa()
    graphs = pydot.graph_from_dot_data(dfa)
    graph = graphs[0]
    graph.write_png('automatons/' + str(row) + "/" + time + ".png")

''' create a directory called automatons 
if such directory already exists,
 delete itself and its content and create it again '''
if(os.path.exists('automatons')):
    shutil.rmtree('automatons')
os.mkdir('automatons')

''' read the json file containing the matrix representation 
of the disjunctive normal form of the formula and for each 
row of the matrix create a directory  '''

with open('matrix.json') as f: 
    matrix = json.load(f)
    for i in range(len(matrix)):
        os.mkdir('automatons/'+ str(i+1))
        save_dfa(matrix[i][0], i+1, "past")
        save_dfa(matrix[i][1], i+1, "present")
        save_dfa(matrix[i][2], i+1, "future")