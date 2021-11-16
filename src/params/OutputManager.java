package params;

import formula.Formula;
import graphviz.GraphViz;
import separator.PureFormulaeMatrix;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputManager {

    public static void graphVizOutput(GraphViz gv, String file, String encoding) {
        gv.increaseDpi();   // 106 dpi
        String representationType= "dot";
        File out = new File( file + encoding);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), encoding, representationType), out);
        System.out.println("Tree representation saved in " + file + encoding);
    }

    public static void textOutput(String file, Formula f) {
        String str = f.toString();
        File out = new File( file + ".txt");
    }

    public static void dfaOutput(String matrix) throws IOException {

        // gets the user current working directory
        final String dir = System.getProperty("user.dir");
        final String script = "/LTLf2DFAcall.py";

        String[] cmd = {
                "python3",
                dir + script,
                String.format("\" %s \"", matrix)
        };

        try{
            Process p = Runtime.getRuntime().exec(cmd);
            int exitVal = p.waitFor();
            System.out.println("Process exitValue: " + exitVal);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void matrixToJsonFile(PureFormulaeMatrix m) {
        //Write JSON file
        try (FileWriter file = new FileWriter("matrix.json")) {
            file.write(m.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
