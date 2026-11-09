
/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];
        if(n == 0) return tested;
        int[][] dp = new int[n][2];
        calc(0, -1, nb, cost, dp);
        restore(0, -1, dp[0][1] <= dp[0][0], nb, dp, tested);
        return tested;
    }

    public static void calc(int v, int p, int[][] nb, int[] cost, int[][] dp){
        dp[v][0] = 0;
        dp[v][1] = cost[v];
        for(int c : nb[v]){
            if(c == p) continue;
            calc(c, v, nb, cost, dp);
            dp[v][0] += dp[c][1];
            dp[v][1] += Math.min(dp[c][0], dp[c][1]);
        }
    }

    public static void restore(int v, int  p, boolean take, int[][] nb, int[][] dp, boolean[] tested){
        tested[v] = take;
        for(int c : nb[v]){
            if(c == p) continue;
            if(take){
                restore(c, v, dp[c][1] <= dp[c][0], nb, dp, tested);
            } else{
                restore(c, v, true, nb, dp, tested);
            }
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if(n == 0) return 0;
        int[] best = new int[1];
        height(0, -1, nb, best);
        return best[0];
    }

    public static int height(int v, int p, int[][] nb, int[] best){
        int first = 0, second = 0;
        for(int c : nb[v]){
            if(c==p) continue;
            int h = height(c, v, nb, best) + 1;
            if(h > first){
                second = first;
                first = h;
            }else if(h > second){
                second = h;
            }
        }
            best[0] = Math.max(best[0], second+first);
            return first;
    }
}
