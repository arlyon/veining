/*
 * veining (c) by Alexander Lyon
 *
 * veining is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nc-sa/4.0/>
 */

/*
 * veining (c) by arlyon
 *
 * veining is licensed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by-nc-sa/4.0/>
 */

package arlyon.veining.util;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The weighted unique queue is a fifo data structure backed by a map of weights.
 * When an item is added to the queue it is saved along with its weight.
 * Only items that pass the predicate appear in the queue. Items that have
 * already been added to the queue will update the weight of that item (if
 * the comparator passes).
 * <p>
 * Uses include:
 * - calculating Levenshtein distance of words and retrieving words with x or less.
 * - getting items only x units away from a source
 *
 * @param <T> Any object that implements equals and hashCode (for Hashtable)
 */
public class WeightedUniqueQueue<T> {

    private final Map<T, Integer> map;
    private Queue<T> queue;
    private Predicate<Integer> predicate;
    private Comparator<Integer> comparator;
    /**
     * Creates a new instance of the WeightedUniqueQueue
     *
     * @param predicate  The predicate to test for entering the queue.
     * @param comparator The comparator to compare two values in the queue.
     */
    public WeightedUniqueQueue(Predicate<Integer> predicate, @Nullable Comparator<Integer> comparator) {
        this.predicate = predicate;
        this.comparator = comparator;

        this.map = new Hashtable<>();
        this.queue = new ArrayDeque<>();
    }

    /**
     * Adds the element to the queue if it passes the predicate and
     * has not already been added to the queue.
     *
     * @param t      The element to add to the queue.
     * @param weight The weight of the element.
     */
    public void add(T t, int weight) {
        try {
            int oldWeight = map.get(t); // get the old value for the item
            if (comparator != null && comparator.compare(oldWeight, weight) > 0)
                map.put(t, weight);
            if (!predicate.test(oldWeight) && predicate.test(weight))
                queue.add(t); // new value qualifies for queue: add it
        } catch (NullPointerException e) {
            map.put(t, weight); // add to the map if not exists
            if (predicate.test(weight)) queue.add(t); // add to the queue if qualified
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
    public WeightedPair remove() {
        T element = queue.poll();
        WeightedPair pair = new WeightedPair(element, map.get(element));
        if (element != null) map.put(element, -1);
        return pair;
    }

    /**
     * Peeks the first element.
     *
     * @return The element or null.
     */
    public T peek() {
        return queue.peek();
    }

    public int getWeight(T t) {
        return map.get(t);
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Checks if the given element exists in the map.
     *
     * @param t The element to check.
     * @return True if in the map.
     */
    public boolean contains(T t) {
        return map.get(t) != null;
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
     * Clears the queue but retains the map
     * meaning that items that have been
     * popped may not be re-added.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Resets the queue and the set,
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

        // remove newly invalid items from queue
        this.queue = this.queue.stream()
            .filter(element -> !predicate.test(this.map.get(element)))
            .collect(Collectors.toCollection(ArrayDeque::new));

        // add newly valid items to queue
        this.map.keySet().forEach(element -> {
            int val = this.map.get(element);
            if (this.predicate.negate().and(predicate).test(val)) {
                this.queue.add(element);
            }
        });

        this.predicate = predicate;
    }

    public class WeightedPair {

        public final T element;
        public final Integer weight;

        WeightedPair(T element, Integer weight) {
            this.element = element;
            this.weight = weight;
        }
    }
}
