package BacktrackingSearch;

public class Arc {
    BacktrackingNode vertex1;
    BacktrackingNode vertex2;

    public Arc(BacktrackingNode v1, BacktrackingNode v2) {
        this.vertex1 = v1;
        this.vertex2 = v2;
    }
}
