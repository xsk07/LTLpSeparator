package test;

import formula.Operator;
import formula.TimeConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static formula.Operator.*;
import static formula.TimeConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class DetermineTimeTest {

    // Test of UNTIL operator

    @Test
    @DisplayName("UntilOfPastPast")
    void untilOfPastPast() {
        timeTest(UNTIL, PAST, PAST, MIXED);
    }

    @Test
    @DisplayName("UntilOfPastPresent")
    void untilOfPastPresent() {
        timeTest(UNTIL, PAST, PRESENT, MIXED);
    }

    @Test
    @DisplayName("UntilOfPastFuture")
    void untilOfPastFuture() {
        timeTest(UNTIL, PAST, FUTURE, MIXED);
    }

    @Test
    @DisplayName("UntilOfPresentPast")
    void untilOfPresentPast() {
        timeTest(UNTIL, PRESENT, PAST, MIXED);
    }

    @Test
    @DisplayName("UntilOfPresentPresent")
    void untilOfPresentPresent() {
        timeTest(UNTIL, PRESENT, PRESENT, FUTURE);
    }

    @Test
    @DisplayName("UntilOfPresentFuture")
    void untilOfPresentFuture() {
        timeTest(UNTIL, PRESENT, FUTURE, FUTURE);
    }

    @Test
    @DisplayName("UntilOfFuturePast")
    void untilOfFuturePast() {
        timeTest(UNTIL, FUTURE, PAST, MIXED);
    }

    @Test
    @DisplayName("UntilOfFuturePresent")
    void untilOfFuturePresent() {
        timeTest(UNTIL, FUTURE, PRESENT, FUTURE);
    }

    @Test
    @DisplayName("UntilOfFutureFuture")
    void untilOfFutureFuture() {
        timeTest(UNTIL, FUTURE, FUTURE, FUTURE);
    }

    // Test of SINCE operator

    @Test
    @DisplayName("SinceOfPastPast")
    void sinceOfPastPast() {
        timeTest(SINCE, PAST, PAST, PAST);
    }

    @Test
    @DisplayName("SinceOfPastPresent")
    void sinceOfPastPresent() {
        timeTest(SINCE, PAST, PRESENT, PAST);
    }

    @Test
    @DisplayName("SinceOfPastFuture")
    void sinceOfPastFuture() {
        timeTest(SINCE, PAST, FUTURE, MIXED);
    }

    @Test
    @DisplayName("SinceOfPresentPast")
    void sinceOfPresentPast() {
        timeTest(SINCE, PRESENT, PAST, PAST);
    }

    @Test
    @DisplayName("SinceOfPresentPresent")
    void sinceOfPresentPresent() {
        timeTest(SINCE, PRESENT, PRESENT, PAST);
    }

    @Test
    @DisplayName("SinceOfPresentFuture")
    void sinceOfPresentFuture() {
        timeTest(SINCE, PRESENT, FUTURE, MIXED);
    }

    @Test
    @DisplayName("SinceOfFuturePast")
    void sinceOfFuturePast() {
        timeTest(SINCE, FUTURE, PAST, MIXED);
    }

    @Test
    @DisplayName("SinceOfFuturePresent")
    void sinceOfFuturePresent() {
        timeTest(SINCE, FUTURE, PRESENT, MIXED);
    }

    @Test
    @DisplayName("SinceOfFutureFuture")
    void sinceOfFutureFuture() {
        timeTest(SINCE, FUTURE, FUTURE, MIXED);
    }

    // Test of NOT operator

    @Test
    @DisplayName("NotOfPast")
    void notOfPast() { timeTest(NOT, PAST, PAST); }

    @Test
    @DisplayName("NotOfPresent")
    void notOfPresent() { timeTest(NOT, PRESENT, PRESENT); }

    @Test
    @DisplayName("NotOfFuture")
    void notOfFuture() { timeTest(NOT, FUTURE, FUTURE); }


    private void timeTest(Operator op, TimeConstant t1, TimeConstant t2, TimeConstant r){
        assertEquals(r, determineTime(op, t1, t2));
    }

    private void timeTest(Operator op, TimeConstant t, TimeConstant r){
        assertEquals(r, determineTime(op, t));
    }

}