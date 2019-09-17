package bangbit.in.coinanalysis;

import java.util.Comparator;

import bangbit.in.coinanalysis.pojo.LambdaHistory;

/**
 * Created by vm on 04-06-2019.
 */

public class LambdaHistoryComparator implements Comparator<LambdaHistory> {
    @Override
    public int compare(LambdaHistory o1, LambdaHistory o2) {
        if (o1.getTime() == null) {
            return (o2.getTime() == null) ? 0 : -1;
        }
        if (o2.getTime() == null) {
            return 1;
        }
        Long ob1 = Long.parseLong(o1.getTime());
        Long ob2 = Long.parseLong(o2.getTime());
        return ob1.compareTo(ob2);
    }
}