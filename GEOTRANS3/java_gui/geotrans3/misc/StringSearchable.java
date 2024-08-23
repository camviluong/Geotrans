package geotrans3.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;

/**
 * Implementation of the Searchable interface that searches a List of String objects. 
 * This implementation searches only the beginning of the words, and is not be optimized
 * for very large Lists. 
 *
 */
public class StringSearchable implements Searchable<String, String> {
    private List<String> terms = new ArrayList<>();
    private List<String> numericallySortedTerms = new ArrayList<>();

    /**
     * Constructs a new object based upon the parameter terms. 
     * @param terms The inventory of terms to search.
     */
    public StringSearchable(final List<String> terms){
        this.terms.addAll(terms);
        this.numericallySortedTerms.addAll(terms);
        this.numericallySortedTerms.sort((String a, String b) -> new Integer(a).compareTo(new Integer(b)));
    }
    
    @Override
    public List<String> getFirstNItems(final int n, final boolean numeric) {
        if (numeric) {
            return this.numericallySortedTerms.stream().limit(n).collect(Collectors.toList());
        } else {
            return this.terms.stream().limit(n).collect(Collectors.toList());
        }
    }

    @Override
    public Collection<String> search(String value) {
        List<String> founds = new ArrayList<String>();

        for (final String s : terms) {
            if (s.contains(value)) {
                founds.add(s);
            }
        }

        return founds;
    }
    
    @Override
    public boolean contains(final String key) {
        return terms.contains(key);
    }
}
