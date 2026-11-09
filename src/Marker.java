import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int[] pi = new int[p.length()];
        int k = 0;
        for(int i = 1; i < p.length(); i++){
            while(k > 0 && p.charAt(i) != p.charAt(k)){
                k = pi[k-1];
            }
            if(p.charAt(i) == p.charAt(k)){
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        int m = p.length();
        if(m == 0 || m > text.length()){
            return new int[0];
        }
        int[] pi = prefixFunction(p);
        List<Integer> res = new ArrayList<>();
        int k = 0;
        for(int i = 0; i < text.length(); i++){
            while(k > 0 && text.charAt(i) != p.charAt(k)){
                k = pi[k - 1];
            }
            if(text.charAt(i) == p.charAt(k)){
                k++;
            }
            if(k == m){
                res.add(i - m + 1);
                k = pi[k - 1];
            }
        }
        int[] out = new int[res.size()];
        for(int i = 0; i < out.length; i++){
            out[i] = res.get(i);
        }
        return out;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 131;
    
    private static long[] windowHashes(String s, int k){
        int count = s.length() - k + 1;
        long[] h = new long[count];
        long pow = 1;
        for(int i = 0; i < k - 1; i++){
            pow = pow * BASE % MOD;
        }
        long current = 0;
        for(int i = 0; i < k; i++){
            current = (current * BASE + s.charAt(i)) % MOD;
        }
        h[0] = current;
        for(int i = 1; i < count; i++){
            current = (current - s.charAt(i - 1) * pow % MOD + MOD) % MOD;
            current = (current * BASE + s.charAt(i + k - 1)) % MOD;
            h[i] = current;
        }
        return h;
    }
    
    public static int sharedWords(String a, String b, int k) {
        if(k <= 0 || k > a.length() || k > b.length()){
            return 0;
        }
        long[] ha = windowHashes(a, k);
        long[] hb = windowHashes(b, k);
        Map<Long, List<Integer>> map = new HashMap<>();
        for(int j = 0; j < hb.length; j++){
            map.computeIfAbsent(hb[j], x -> new ArrayList<>()).add(j);
        }
        int count = 0;
        for(int i = 0; i < ha.length; i++){
            List<Integer> cand = map.get(ha[i]);
            if(cand == null){
                continue;
            }
            for(int j : cand){
                if(a.regionMatches(i, b, j, k)){
                    count++;
                    break;
                }
            }
        }
        return count;
    }
}
