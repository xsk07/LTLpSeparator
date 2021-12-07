package test;

import formula.AtomicFormula;
import formula.Formula;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static formula.Operator.*;
import static org.junit.jupiter.api.Assertions.*;
import static separator.SubformulasPatterns.*;

class SubformulasPatternsTest {

    // example: qS(a&(BUA)) == qS(a&(bUc))
    private final Formula[] subformulas = new Formula[] {
            new AtomicFormula("a"), // a
            new AtomicFormula("c"), // A
            new AtomicFormula("b"), // B
            new AtomicFormula("q")  // q
    };

    @Test
    void subformulaPattern1Test() {
        assertEquals("qSa", subformulaPattern1(subformulas, SINCE).toString());
        assertEquals("qUa", subformulaPattern1(subformulas, UNTIL).toString());
    }

    @Test
    void subformulaPattern2Test() {
        System.out.println(Arrays.stream(subformulas).toList().toString());
        assertEquals("bSa", subformulaPattern2(subformulas, SINCE).toString());
        assertEquals("bUa", subformulaPattern2(subformulas, UNTIL).toString());
    }

    @Test
    void subformulaPattern3() {
    }

    @Test
    void subformulaPattern4() {
    }

    @Test
    void subformulaPattern5() {
    }

    @Test
    void subformulaPattern6() {
    }

    @Test
    void subformulaPattern7() {
    }

    @Test
    void subformulaPattern8() {
    }

    @Test
    void subformulaPattern9() {
    }

    @Test
    void subformulaPattern10() {
    }

    @Test
    void subformulaPattern11() {
    }

    @Test
    void subformulaPattern12() {
    }

    @Test
    void subformulaPattern13() {
    }

    @Test
    void subformulaPattern14() {
    }

    @Test
    void subformulaPattern15() {
    }

    @Test
    void subformulaPattern16() {
    }

    @Test
    void subformulaPattern17() {
    }

    @Test
    void subformulaPattern18() {
    }

    @Test
    void subformulaPattern19() {
    }

}