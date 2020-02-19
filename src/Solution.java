import BacktrackingSearch.*;
import LocalSearch.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.*;

public class Solution {

    public static void main(String[] args) throws FileNotFoundException {
        Set<String> colors = new HashSet<String>();
        //Backtracking search data
        Set<BacktrackingNode> backsearchVariables = new HashSet<BacktrackingNode>();
        Map<BacktrackingNode, List<BacktrackingNode>> backsearchEdges = new HashMap<BacktrackingNode, List<BacktrackingNode>>();
        Map<String, BacktrackingNode> string_to_node_backtracking = new HashMap<String, BacktrackingNode>();

        //Local search data
        Set<LocalNode> localVariables = new HashSet<LocalNode>();
        Map<LocalNode, List<LocalNode>> localEdges = new HashMap<LocalNode, List<LocalNode>>();
        Map<String, LocalNode> string_to_node_local = new HashMap<String, LocalNode>();

        //constraints
        Set<Arc> arcs = new HashSet<Arc>();

        File source = new File(args[0]);
        Scanner sc = new Scanner(source);


        while(sc.hasNext()) {
            //reading in colors
            String line = sc.nextLine();
            if(line.isEmpty()) break;
            colors.add(line);
        }

        while(sc.hasNext()) {
            //reading in variables
            String line = sc.nextLine();
            if(line.isEmpty()) break;
            LocalNode newLocalNode = new LocalNode(line, colors);
            BacktrackingNode newBacktrackingNode = new BacktrackingNode(line, colors);
            backsearchVariables.add(newBacktrackingNode);
            localVariables.add(newLocalNode);

            string_to_node_backtracking.put(line, newBacktrackingNode);
            string_to_node_local.put(line, newLocalNode);

            //initializing list for var's edges
            backsearchEdges.put(newBacktrackingNode, new ArrayList<BacktrackingNode>());
            localEdges.put(newLocalNode, new ArrayList<LocalNode>());
        }

        //reading in edges
        sc.reset();
        while(sc.hasNext()) {
            String s1 = sc.next();
            String s2 = sc.next();

            //backtracking
            BacktrackingNode node1 = string_to_node_backtracking.get(s1);
            BacktrackingNode node2 = string_to_node_backtracking.get(s2);
            Arc arc = new Arc(node1, node2);
            arcs.add(arc);
            node1.arcs.add(arc);
            node2.arcs.add(arc);
            backsearchEdges.get(node1).add(node2);
            backsearchEdges.get(node2).add(node1);

            //local
            LocalNode node21 = string_to_node_local.get(s1);
            LocalNode node22 = string_to_node_local.get(s2);
            localEdges.get(node21).add(node22);
            localEdges.get(node22).add(node21);

        }
        sc.close();
        //finished parsing input file

        //backtracking search
        BacktrackingSearch backsearch = new BacktrackingSearch(backsearchVariables, backsearchEdges, colors, arcs);
        LocalSearch localsearch = new LocalSearch(localVariables, localEdges, colors);
        System.out.println("Solution provided by Backtracking Search:");
        if (backsearch.backtrack()) {
            for(BacktrackingNode node: backsearchVariables) {
                System.out.println("node: "+node.name + ", color: " + node.domain.iterator().next());
            }
            System.out.println("steps: "+ backsearch.num_steps);
        }else {
            System.out.print("coloring determined by backtracking search is not possible with " + colors.size()+"colors");
        }

        System.out.println();
        System.out.println("Solution provided by Local Search:");
        Set<LocalNode> sol = localsearch.getSolution();
        if(sol.size() == localVariables.size()){
            for(LocalNode node: sol) {
                System.out.println("node: "+node.name + ", color: " + node.myColor);
            }
            System.out.println("This many iterations until a solution was found: " + localsearch.iteration);
            System.out.println("How many times we restarted: " + localsearch.loops);
        } else {
            System.out.print("coloring determined by local search is not possible with " + colors.size()+"colors");
        }
    }
}
