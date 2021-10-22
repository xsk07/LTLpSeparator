package formula;

import static formula.Operator.*;

public enum TimeConstant {

    MIXED,
    PAST,
    PRESENT,
    FUTURE;

    public static TimeConstant determineTime(Operator op, TimeConstant t1, TimeConstant t2) {

        if(op.getArity() != 2) {
            throw new IllegalArgumentException(
                    String.format(
                            "The operator should be binary but has arity %d",
                            op.getArity()
                    )
            );
        }

        return switch (op.getTime()){
            case PAST -> determineTimePastCase(t1, t2);
            case PRESENT ->  determineTimeBooleanCase(t1, t2);
            case FUTURE -> determineTimeFutureCase(t1, t2);
            default -> MIXED;
        };
    }

    public static TimeConstant determineTime(Operator op, TimeConstant t) {

        if(op.getArity() != 1) {
            throw new IllegalArgumentException(
                    String.format(
                            "The operator should be unary but has arity %d",
                            op.getArity()
                    )
            );
        }

        switch (op.getTime()) {
            case PRESENT : {
                if(op.equals(NOT)) return t;
                return determineTimeBooleanCase(PRESENT, t);
            }
            case PAST : return determineTimePastCase(PAST, t);
            case FUTURE : return determineTimeFutureCase(FUTURE, t);
            default : return MIXED;
        }
    }

    private static TimeConstant determineTimeBooleanCase(TimeConstant t1, TimeConstant t2){
        switch (t1) {
            case PAST : {
                return switch (t2) {
                    case PAST, PRESENT -> PAST;
                    default -> MIXED;
                };
            }
            case PRESENT : return t2;
            case FUTURE : {
                return switch (t2) {
                    case PRESENT, FUTURE -> FUTURE;
                    default -> MIXED;
                };
            }
            default: return MIXED;
        }
    }

    private static TimeConstant determineTimePastCase(TimeConstant t1, TimeConstant t2) {
        switch (t1){
            case PAST, PRESENT: {
                return switch (t2) {
                    case PAST, PRESENT -> PAST;
                    default -> MIXED;
                };
            }
            default: return MIXED;
        }
    }

    private static TimeConstant determineTimeFutureCase(TimeConstant t1, TimeConstant t2){
        switch(t1) {
            case PRESENT, FUTURE: {
                return switch (t2) {
                    case PRESENT, FUTURE -> FUTURE;
                    default -> MIXED;
                };
            }
            default: return MIXED;
        }
    }


}
