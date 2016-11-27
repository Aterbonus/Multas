package cl.aterbonus.multas.utilidades;

import android.util.Pair;

/**
 * Created by root on 12-10-16.
 */
public class Item<K, V> extends Pair<K, V> {

    public Item(K first, V second) {
        super(first, second);
    }

    public K hiddenValue() {
        return first;
    }

    public V displayValue() {
        return second;
    }

    public String toString() {
        return second.toString();
    }
}
