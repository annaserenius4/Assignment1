package LocalSearch;
import java.util.*;

public class LocalNode {
    //nodes have a name (the state name, number of colors it could possibly be, a set of possible colors,
//and an assigned color).

        public String name;
        int numColors;
        Set<String> possibleColors;
        public String myColor;
        boolean hasChanged;
        boolean hasBeenThrough;
        int wrongColorCount;

        //which state of the US the node refers to, the colors that the node could potentially be?
        public LocalNode(String state, Set<String> colors){
            this.name = state;
            numColors = colors.size();
            //initializing domain for state (all colors in set)
            this.possibleColors = colors;
            this.hasChanged = false;
            this.hasBeenThrough = false;
            this.wrongColorCount = 0;

        }
}
