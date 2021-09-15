package params;

import graphviz.GraphViz;

import java.io.File;

public class OutputManager {



    public static void graphVizOutput(GraphViz gv, String file, String encoding){
        gv.increaseDpi();   // 106 dpi
        String type = encoding;
        String repesentationType= "dot";
        File out = new File( file + type);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, repesentationType), out);
        System.out.println("Tree representation saved in " + file + type);
    }


}
