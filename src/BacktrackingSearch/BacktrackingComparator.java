package BacktrackingSearch;

import java.util.Comparator;

public class BacktrackingComparator implements Comparator<BacktrackingNode>{//to be used with a priority queue that orders nodes from most to least constrained

        public int compare(BacktrackingNode backtrackingNode1, BacktrackingNode backtrackingNode2) {

            if(backtrackingNode1.domain.size() > backtrackingNode2.domain.size()) return 1;
            if(backtrackingNode1.domain.size() < backtrackingNode2.domain.size()) return -1;
            return 0;
        }

}
