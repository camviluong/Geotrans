package geotrans3.misc;

import java.util.Collection;
import java.util.List;

/**
 * Interface to search an underlying inventory of items and return a collection of found items. 
 * @param <E> The type of items to be found.
 * @param <V> The type of items to be searched
 */
public interface Searchable<E, V> {
    /**
     * Searches an underlying inventory of items consisting of type E
     * @param value A searchable value of type V
     * @return A Collection of items of type E.
     */
    public Collection<E> search(V value);
    
    /**
     * Returns the first N items in the collection
     */
    public List<E> getFirstNItems(final int n, final boolean numeric);
    
    /**
     * Tests whether key is in collection.
     */
    public boolean contains(final E key);
}

