package BacktrackingSearch;
import java.util.*;

public class BacktrackingNode {
    public String name;
    public Set<String> domain = new HashSet<String>();
    public Set<Arc> arcs = new HashSet<Arc>();

    List<Set<String>> snapshots = new ArrayList<Set<String>>();

    public BacktrackingNode(String state, Set<String> colors){
        this.name = state;
        this.domain.addAll(colors);

    }

    public void reduceDomain(String myColor) {
        //removes every color besides the color assigned from state's domain in order to perform arc consistency
        domain.clear();
        domain.add(myColor);
    }


    public void takeSnapshot() {
        Set<String> domain_snapshot = new HashSet<String>();
        //deep copying domain
        for(String color : domain) {
            domain_snapshot.add(color);
        }
        snapshots.add(domain_snapshot);
    }

    public void restoreState() {
        this.domain = snapshots.get(snapshots.size()-1);
        snapshots.remove(snapshots.size()-1);
    }
}
