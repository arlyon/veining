package arlyon.veining.util;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The value queue is a fifo (excluding when the predicate is changed)
 * data structure backed by a map of values. Items added to the queue with
 * a that pass the predicate will be added into the set but not in
 * the queue. Only when the predicate changes or the item is re-added may
 * it appear in the queue. Items that have already been popped may be added again,
 * but will be ignored.
 * <p>
 * Uses include:
 * - calculating Levenshtein distance of words and retrieving words with x or less.
 * - getting items only x units away from a source
 *
 * @param <T> Any object that implements equals and hashCode (for Hashtable)
 */
public class ValueUniqueQueue<T> {

    private final Map<T, Integer> map;
    private Queue<T> queue;

    private Predicate<Integer> predicate;
    private Comparator<Integer> comparator;

    /**
     * Creates a new instance of the ValueUniqueQueue
     *
     * @param predicate  The predicate to test for entering the queue.
     * @param comparator The comparator to compare two values in the queue.
     */
    public ValueUniqueQueue(Predicate<Integer> predicate, @Nullable Comparator<Integer> comparator) {
        this.predicate = predicate;
        this.comparator = comparator;

        this.map = new Hashtable<>();
        this.queue = new ArrayDeque<>();
    }

    /**
     * Adds the element to the queue if the value is lower
     * than the max and has not been added to the queue.
     *
     * @param t        The element to add to the queue.
     * @param newValue The value to insert.
     */
    public void add(T t, int newValue) {
        try {
            int oldValue = map.get(t); // get the old value for the item
            if (!predicate.test(oldValue) && predicate.test(newValue)) {
                queue.add(t); // new value qualifies for queue: add it
                map.put(t, newValue);
            } else if (comparator != null && comparator.compare(oldValue, newValue) > 0) {
                map.put(t, newValue); // new value is smaller: update it
            }
        } catch (NullPointerException e) {
            map.put(t, newValue); // add to the map if not exists
            if (predicate.test(newValue)) queue.add(t); // add to the queue if qualified
        }
    }

    /**
     * Polls the queue for an element and
     * if the queue has any elements left to give,
     * sets that element to -1 (signifying completion)
     * and then returns it.
     *
     * @return The element or null if no more exist.
     */
    public T remove() {
        T element = queue.poll();
        if (element != null) map.put(element, -1);
        return element;
    }

    /**
     * Peeks the first element.
     *
     * @return The element or null.
     */
    public T peek() {
        return queue.peek();
    }

    public int getValue(T value) {
        return map.get(value);
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean contains(T o) {
        return queue.contains(o);
    }

    public Iterator<T> iterator() {
        return queue.iterator();
    }

    public void forEach(Consumer<? super T> action) {
        queue.forEach(action);
    }

    public Object[] toArray() {
        return queue.toArray();
    }

    /**
     * Clears the queue but retains the set
     * meaning that items that have been
     * popped may not be re-added.
     */
    public void clear() {
        queue.clear();
        map.clear();
    }

    /**
     * Clears the queue and the set,
     * allowing for elements that have been
     * previously popped to be re-added
     * and appear in the queue again.
     */
    public void reset() {
        queue.clear();
        map.clear();
    }

    public Stream<T> stream() {
        return queue.stream();
    }

    /**
     * Updates the max value and recalculates
     * the queue to match. Expensive!
     *
     * @param predicate The new predicate.
     */
    public void setPredicate(Predicate<Integer> predicate) {
        // filter newly unqualified items in queue
        this.queue = this.queue.stream()
            .filter(element -> !predicate.test(this.map.get(element)))
            .collect(Collectors.toCollection(ArrayDeque::new));

        // add newly qualified from set
        this.map.keySet().forEach(element -> {
            int val = this.map.get(element);
            if (this.predicate.negate().and(predicate).test(val)) {
                this.queue.add(element);
            }
        });

        this.predicate = predicate;
    }
}
