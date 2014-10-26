package klt;

/**
 * Created by Tobi on 26.10.2014.
 */
public class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return first;
    }

    public U second() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }

    @Override
    public int hashCode() {
        return 41 * (41 + first.hashCode()) + second.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Pair<?,?>)) return false;
        Pair<?,?> p = (Pair<?,?>)o;
        return ((first == null ? p.first == null : first.equals(p.first)) &&
                (second == null ? p.second == null : second.equals(p.second)));
    }
}