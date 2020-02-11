package LocalSearch;
import java.util.*;

//hill climbing search! (with restarts if it's been searching too long!!)
//generates a random starting position, and then slowly improves it until it
//*hopefully* finds a solution!

//According to what we read:
//Random-restart hill climbing is a surprisingly effective algorithm in many cases.
//It turns out that it is often better to spend CPU time exploring the space,
//than carefully optimizing from an initial condition.

//Hill climbing search:
//expands the initial state s and chooses the best of its successors s! (if
//there is more than one best successor, choose one at random)
//s! replaces s as the current state and is expanded in turn
//if none of the successors of are better (lower cost or higher quality)
//than the current state, the algorithm halts
//follows the steepest gradient up (or down) hill from the initial state
//until no improvement is possible
//only remembers the current state and its value

public class LocalSearch {

        //set of the states, map of edges, set of nodes with colors assigned
        Set<LocalNode> states;
        Map<LocalNode, List<LocalNode>> edges;
        String[] colors;
        Set<String> tempColors;
        LocalNode[] orderedLocalNodes;
        int numColors;
        int num;
        LocalNode currLocalNode;
        Set<LocalNode> completedStates;


        public LocalSearch(Set<LocalNode> setOfStates, Map<LocalNode, List<LocalNode>> edges2, Set<String> colors2) {
            this.edges = edges2;
            this.states = setOfStates;
            this.tempColors = colors2;
            colors = new String[colors2.size()];
            colors2.toArray(colors);
            this.numColors = colors.length;
            this.num = 0;
            this.orderedLocalNodes = new LocalNode[setOfStates.size()];
            setOfStates.toArray(orderedLocalNodes);
            this.currLocalNode = orderedLocalNodes[0];
        }


        //this works
        public Set<LocalNode> generateRandomStart() {
            for (LocalNode localNode : states) {
                Random myRandom = new Random();
                //generate a random int from 0 to size of set - 1
                int randColorIndex = myRandom.nextInt(colors.length);
                localNode.myColor = colors[randColorIndex];
            }
            return states;
        }



        //main function in class :-)
        public Set<LocalNode> getSolution() {
            int iteration = 0;
            states = this.generateRandomStart();
            if (isSolution(states)) {
                return states;
            }
            else {
                LocalNode currentLocalNode = null;
                while(true) {
                    if (iteration == 0) {
                        currentLocalNode = whichNeighborNext();
                        currentLocalNode.wrongColorCount = 0;
                    }
                    else {
                        if (currentLocalNode.name == whichNeighborNext().name) {
                            currentLocalNode = chooseAlternateNode(currentLocalNode);
                            currentLocalNode.wrongColorCount = 0;
                        }
                        else {
                            currentLocalNode = whichNeighborNext();
                            currentLocalNode.wrongColorCount = 0;
                        }
                    }
                    for (LocalNode n : edges.get(currentLocalNode)) {
                        if (n.myColor == currentLocalNode.myColor) {
                            currentLocalNode.wrongColorCount++;
                        }
                    }


                    boolean worked = chooseNextColor(currentLocalNode);

                    num = 0;
                    for (LocalNode n : states) {
                        num++;
                    }
                    if (isSolution(states)) {
                        return states;
                    }
                    if (iteration > 500) {
                        getSolution();
                    }
                    iteration++;
                }
            }
        }



        //sees if the current solution is a valid solution. true if valid, false if not valid
        public boolean isSolution(Set<LocalNode> currState) {
            for (LocalNode localNode : currState) {
                for (LocalNode localNode2 : edges.get(localNode)) {
                    if (localNode.myColor == localNode2.myColor) {
                        return false;
                    }
                }
            }
            return true;
        }




        //tries to randomly find a neighbor that it should fix next if it has a neighbor w the same color
        //if we have two possible nodes to choose, choose the one with the least number of edges:
        public LocalNode whichNeighborNext() {

            LocalNode finalLocalNode = null;
            for (LocalNode n : states) {
                n.wrongColorCount = 0;   //need to reset it every time so it doesn't get crazy
                for (LocalNode countNodes : edges.get(n)) {
                    if (countNodes.myColor == n.myColor) {
                        n.wrongColorCount++;
                    }
                }
            }

            //we want to select the node with the highest number of matching colors (aka "wrong" colors):
            Set<LocalNode> possibleLocalNodes = new HashSet<LocalNode>();
            int numWrong = 0;
            for (LocalNode n : states) {
                if (edges.get(n).size() > 0) {
                    //System.out.println("Node: " + n.name + ", count matching: " + n.wrongColorCount);
                    if (n.wrongColorCount >= numWrong) {
                        //finalNode = n;
                        numWrong = n.wrongColorCount;
                    }
                }
            }

            //finding the best node -> the one with highest number wrong and **lowest number of edges**:
            for (LocalNode n : states) {
                if (n.wrongColorCount == numWrong) {
                    possibleLocalNodes.add(n);
                }
            }
            int lowEdge = 10000;
            for (LocalNode possible : possibleLocalNodes) {
                if (possibleLocalNodes.size() == 1) {
                    finalLocalNode = possible;
                    break;
                }
                else {
                    if (edges.get(possible).size() < lowEdge) {
                        lowEdge = edges.get(possible).size();
                        finalLocalNode = possible;
                    }
                }
            }
            return finalLocalNode;
        }


        //this is the same as whichNeighborNext(), except it prevents picking the same node 2x's in a row!
        public LocalNode chooseAlternateNode(LocalNode localNode) {
            LocalNode returnLocalNode = null;
            int oldWrongCount = 0;
            for (LocalNode nn : edges.get(localNode)) {
                if (nn.myColor == localNode.myColor) {
                    oldWrongCount++;
                }
            }
            for (LocalNode n : states) {
                if (n.wrongColorCount == oldWrongCount && n.name != localNode.name & edges.get(n).size() <= edges.get(localNode).size()) {
                    returnLocalNode = n;
                }
            }
            //if the node i'm trying to make it pick is still null, we want to make it not null:
            if (returnLocalNode == null) {
                //then we'll just choose the most problematic node that has the least edges/connected nodes
                for (LocalNode myLocalNode : states) {
                    myLocalNode.wrongColorCount = 0;   //need to reset it every time so it doesn't get crazy
                    for (LocalNode countNodes : edges.get(myLocalNode)) {
                        if (countNodes.myColor == myLocalNode.myColor) {
                            myLocalNode.wrongColorCount++;
                        }
                    }
                }
                Set<LocalNode> contenders = new HashSet<LocalNode>();
                for (LocalNode n2 : states) {
                    if (edges.get(n2).size() >= 0) {
                        if (n2.wrongColorCount >= oldWrongCount) {
                            contenders.add(n2);
                        }
                    }
                }
                int numEdges = 100;
                for (LocalNode m : contenders) {
                    if (edges.get(m).size() < numEdges) {
                        numEdges = edges.get(m).size();
                        returnLocalNode = m;
                    }
                }
            }
            return returnLocalNode;
        }



        //choose the next color for the node in question
        //need to make this smarter! need to examine what happens for each different color we could choose!
        //basically, see what happens in all other possible states
        //if changing to different colors causes "equal damage," choose the color of the neighboring node with
        //the least number of edges.
        public boolean chooseNextColor(LocalNode localNode) {

            //where we'll end up keeping possible colors:
            Set<String> contendingColors = new HashSet<String>();


            String oldColor = localNode.myColor;
            int lowestWrongCount = localNode.wrongColorCount;   //measure of comparison

            for (String color : localNode.possibleColors) {
                if (localNode.myColor != color) {
                    localNode.myColor = color;
                    //node.possibleColors.remove(color);
                }
                localNode.wrongColorCount = 0;
                for (LocalNode n : edges.get(localNode)) {
                    if (n.myColor == localNode.myColor) {
                        localNode.wrongColorCount++;
                    }
                }
                if (localNode.wrongColorCount <= lowestWrongCount) {
                    lowestWrongCount = localNode.wrongColorCount;
                }
            }
            //we've theoretically found the lowestWrongCount!

            //for each color in possible colors, take turns setting alternative colors to my node.
            //if the new color has a value equal to lowestWrongCount, add it to the contendingColors set.
            for (String color : localNode.possibleColors) {
                if (localNode.myColor != color) {
                    localNode.myColor = color;
                }
                localNode.wrongColorCount = 0;
                for (LocalNode n2 : edges.get(localNode)) {
                    if (n2.myColor == localNode.myColor) {
                        localNode.wrongColorCount++;
                    }
                }
                if (localNode.wrongColorCount == lowestWrongCount & localNode.myColor != oldColor) {
                    contendingColors.add(color);
                }
            }

            int lowestCount = 10000;
            for (String c2 : contendingColors) {
                int nonMatching = 0;
                for (LocalNode n : edges.get(localNode)) {  //if it's not a color on an edge, GREAT
                    if (n.myColor != c2) {
                        nonMatching++;
                    }
                    if (nonMatching == edges.get(localNode).size()) {
                        localNode.myColor = c2;
                        return true;
                    }
                }
            }
            //if they are colors on an edge... try to choose color matching node w least number of edges
            for (String c3 : contendingColors) {
                for (LocalNode w : edges.get(localNode)) {
                    if (edges.get(w).size() < lowestCount & w.myColor == c3) {
                        lowestCount = edges.get(w).size();
                        localNode.myColor = c3;
                    }
                }
            }

            return true;
        }
}
