package BacktrackingSearch;
import java.util.*;

public class BacktrackingSearch {
    Queue<BacktrackingNode> states;
    Map<BacktrackingNode, List<BacktrackingNode>> adj_list;
    Set<BacktrackingNode> assigned;
    Set<Arc> arcs;
    int numStates;
    public int num_steps = 0;



    public BacktrackingSearch(Set<BacktrackingNode> variables, Map<BacktrackingNode, List<BacktrackingNode>> adj_list,
                              Set<String> colors, Set<Arc> arcs) {
        BacktrackingComparator comparator = new BacktrackingComparator();
        this.states = new PriorityQueue<BacktrackingNode>(comparator);
        for(BacktrackingNode backtrackingNode : variables) {
            states.add(backtrackingNode);
        }
        this.adj_list = adj_list;
        this.assigned = new HashSet<BacktrackingNode>();
        this.numStates = variables.size();
        this.arcs = arcs;

    }

    public boolean backtrack() {
        //Most Constrained Variable + arc consistency

        //if assignment is complete return assignment

        if(assigned.size() == numStates) return true;

        BacktrackingNode state= states.poll();
        //selecting uncolored state
        num_steps++;

        Set<String> colors = new HashSet<String>();
        for(String color: state.domain) {
            colors.add(color);
        }
        //for each color in state's domain
        for(String color: colors) {
            if(isSafe(state, color)) {
                assigned.add(state);
                state.takeSnapshot();
                state.reduceDomain(color);
                Set<BacktrackingNode> inferences = new HashSet<BacktrackingNode>();
                inferences.add(state);
                if (arcConsistency(inferences)) {
                    if (backtrack()) return true;
                    //remove assignments if backtrack unsuccessful
                    assigned.remove(state);
                    //undo inferences made by arc consistency if backtrack is unsuccessful
                    undoInferences(inferences);
                }else {
                    //undo inferences made by arc consistency if it is unsuccessful
                    assigned.remove(state);
                    undoInferences(inferences);
                }
            }
        }
        //adding state back to queue if there are no safe colorings
        states.add(state);
        return false;
    }

    public void undoInferences(Set<BacktrackingNode> inferences) {
        for(BacktrackingNode state : inferences) {
            state.restoreState();
        }
        inferences.clear();
    }


    public boolean isSafe(BacktrackingNode state, String color) {
        List<BacktrackingNode> neighbors = adj_list.get(state);
        for(BacktrackingNode neighbor : neighbors) {
            if(neighbor.domain.size()==1 && neighbor.domain.iterator().next().equals(color))return false;
        }
        return true;
    }

    public boolean arcConsistency(Set<BacktrackingNode> inferences) {
        //returns false if an inconsistency is found, true otherwise
        Queue<Arc> qarc = new LinkedList<Arc>();
        qarc.addAll(arcs);

        while(!qarc.isEmpty()) {
            Arc an_arc = qarc.remove();
            if (revise(an_arc.vertex1, an_arc.vertex2, inferences)){
                if (an_arc.vertex1.domain.size()==0) {return false;}
                for (Arc neighbor_arc : an_arc.vertex1.arcs) {
                    //need to add the arc back into queue
                    if(neighbor_arc==an_arc)continue; //don't add previous arc
                    qarc.add(neighbor_arc);
                }
            }
        }
        return true;

    }

    public boolean revise(BacktrackingNode X, BacktrackingNode Y, Set<BacktrackingNode> inferences) { //returns true if we revise the domain of X
        boolean revised = false;
        Set<String> toRemove = new HashSet<String>();
        X.takeSnapshot();

        for(String color : X.domain) {
            boolean no_value_Y_domain = true;
            for(String color_Y : Y.domain) {
                if(!color.equals(color_Y)){
                    no_value_Y_domain = false;
                }
            }
            if(no_value_Y_domain) { //if no color satisfies the constraint b/n X and Y
                toRemove.add(color);
                revised = true;
            }
        }

        //removing colors from domain that are not arc consistent
        X.domain.removeAll(toRemove);
        if(revised) inferences.add(X);
        if(!revised) X.restoreState();
        return revised;
    }
}
