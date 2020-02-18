package BacktrackingSearch;

import java.util.Comparator;

public class BacktrackingComparator implements Comparator<BacktrackingNode>{//to be used with a priority queue that orders nodes from most to least constrained

     public int compare(BacktrackingNode backtrackingNode1, BacktrackingNode backtrackingNode2) {

             //Most constrained variable
            if(backtrackingNode1.domain.size() > backtrackingNode2.domain.size()) return 1;
            if(backtrackingNode1.domain.size() < backtrackingNode2.domain.size()) return -1;
            else{
                    //for a tie, then do Most constraining variable
                if(backtrackingNode1.arcs.size() < backtrackingNode2.arcs.size()) return 1;
                if(backtrackingNode1.arcs.size() > backtrackingNode2.arcs.size()) return -1;
            }
            return 0;
        }

}
