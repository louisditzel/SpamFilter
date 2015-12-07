import java.util.Comparator;
import java.util.Map;

/**
 * Created by louis on 07/12/2015.
 */
class ValueComparator implements Comparator {
    Map base;

    public ValueComparator(Map base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    @Override
    public int compare(Object a, Object b) {
        if ((Double) base.get(a) >= (Double) base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}