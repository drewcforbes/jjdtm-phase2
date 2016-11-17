package config;

import java.util.ArrayList;
import java.util.List;

public class RangeUtil {

    public static List<Integer> makeRangeList(int lowerBound, int upperBound) {

        //Switch bounds if they're backwards
        if (lowerBound > upperBound) {
            int temp = upperBound;
            upperBound = lowerBound;
            lowerBound = temp;
        }

        List<Integer> range = new ArrayList<>();
        for (int i = lowerBound; i <= upperBound; i++) {
            range.add(i);
        }

        return range;
    }
}
