package separator;

import formula.Formula;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class State {

    private Formula phi;
    private int depth;

    public State(Formula f, int n) {
        phi = f;
        depth = n;
    }

    public boolean goalReached() {return phi.isSeparated(); }

    public Formula getFormula() { return phi; }

    public int getDepth() { return depth; }

    public Collection<State> expandFrontier() {
        List<State> frontier = new LinkedList<>();

        return frontier;
    }



}
