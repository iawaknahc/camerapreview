package com.github.iawaknahc.camerapreview;

import java.util.LinkedHashSet;

public class Sorting {

    public static int compareInt(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs > rhs ? 1 : 0);
    }

    public static int reverseCompareOrder(int compareResult) {
        return compareResult < 0 ? 1 : (compareResult > 0 ? -1 : 0);
    }

    public static <T> int compareWithPriorities(LinkedHashSet<? extends T> priorities, T lhs, T rhs) {
        for (T priority : priorities) {
            final boolean lhsEqualsPriority = lhs.equals(priority);
            final boolean rhsEqualsPriority = rhs.equals(priority);
            if (lhsEqualsPriority && !rhsEqualsPriority) {
                return -1;
            }
            if (!lhsEqualsPriority && rhsEqualsPriority) {
                return 1;
            }
        }
        return 0;
    }

}
