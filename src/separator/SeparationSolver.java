package separator;

import formula.Formula;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class SeparationSolver {

    // state queue
    private Queue<State> states = new LinkedList<>();

    public Formula solve(Formula phi) {
        states.add(new State(phi, 0)); // initial state
        Iterator<State> iterator = states.iterator();
        while(iterator.hasNext()) {
            State s = iterator.next();
            if(s.goalReached()) return s.getFormula();

        }
        return null; // throw exception
    }

}
