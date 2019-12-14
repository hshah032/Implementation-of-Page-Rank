/** To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *
 *
 */
/*
Author: Hussam Shah
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import javafx.util.Pair;

/**
 *
 * @author Administrator
 */
public class CSI2110 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String edgesFilename = "email-dnc.edges";
        Graph graph = readGraph(edgesFilename);
        List<Integer>               nodes = graph.getGraphNodes();
        Map<Integer, List<Integer>> edges = graph.getGraphEdges();
        Map<Integer, Double> map = new HashMap<>();

        
        System.out.println("Number of nodes in the Graph: " + nodes.size());
        
        for(Integer node : nodes) {
            map.put(node, 1.0);
            System.out.println("Node number: " + node);
            System.out.print("Adjacent Nodes: ");
            if (edges.containsKey(node)) {
                for(Integer edge : edges.get(node)) {
                    System.out.print(edge + " ");
                }
            }
            System.out.println();
            System.out.println("------------------------------------");
        }
        Map<Integer,Double> result = pageRank(edges, nodes, map, 0.85,0.0000000001);
        result = nLargest(result,10);
        for(int k: result.keySet()) {
            System.out.println(k);
        }
    }
    /*
    inspired by: https://stackoverflow.com/questions/23805861/finding-the-n-largest-values-in-a-hashma
     */
    //sorts the map with n entries
    static Map<Integer, Double> nLargest(Map<Integer, Double> map, int n) { //map and n largest values to search for

        Double value;
        ArrayList<Integer> keys = new ArrayList<>(n); //to store keys of the n largest values
        ArrayList<Double> values = new ArrayList<>(n); //to store n largest values (same index as keys)
        int index;
        for (Integer key : map.keySet()) { //iterate on all the keys (i.e. on all the values)
            value = map.get(key); //get the corresponding value
            index = keys.size() - 1; //initialize to search the right place to insert (in a sorted order) current value within the n largest values
            while (index >= 0 && value > values.get(index)) { //we traverse the array of largest values from smallest to biggest
                index--; //until we found the right place to insert the current value
            }
            index = index + 1; //adapt the index (come back by one)
            values.add(index, value); //insert the current value in the right place
            keys.add(index, key); //and also the corresponding key
            if (values.size() > n) { //if we have already found enough number of largest values
                values.remove(n); //we remove the last largest value (i.e. the smallest within the largest)
                keys.remove(n); //actually we store at most n+1 largest values and therefore we can discard just the last one (smallest)
            }
        }
        Map<Integer, Double> result = new HashMap<>(values.size());
        for (int i = 0; i < values.size(); i++) { //copy keys and value into an HashMap
            result.put(keys.get(i), values.get(i));
        }
        return result;
    }
    /*
    main algo:
    see report for arguments specifications
     */
    public static Map<Integer,Double>  pageRank (Map<Integer, List<Integer>> edges ,
                                                    List<Integer>               nodes,
                                                    Map<Integer, Double> map,
                                                    double damp, double tol) {
        //how many nodes?
        while (true) {

            Map<Integer, Double> newMap = new HashMap<>();
            // UNO
            for (Integer node : nodes) {
                newMap.put(node, 0.0);
            }
            // DEUS
            for (Integer node : nodes) { // iterator
                for (Integer neighneigh : edges.get(node)) {
                    double temp = newMap.get(neighneigh);
                    temp += damp * map.get(node) / edges.get(node).size();
                    newMap.put(neighneigh, temp);
                }
            }
//            // TROIS
            for (int node : nodes) { // iterator
                newMap.put(node, newMap.get(node) + (1 - damp));
            }

            // FINAL STEP
            double diff = 0;
            for (int i : nodes) { // iterator
                if (Math.abs(map.get(i)- newMap.get(i)) > diff) {
                    diff = Math.abs(map.get(i)- newMap.get(i));
                }
            }
            map = newMap;

            if (diff <= tol) break;
            System.out.println(diff);
        }

        return map; // this will return when the vales have converged
    }

    
    private static Graph readGraph(String edgesFilename) throws FileNotFoundException, IOException {
        System.getProperty("user.dir");
        URL edgesPath = CSI2110.class.getResource(edgesFilename);
        BufferedReader csvReader = new BufferedReader(new FileReader(edgesPath.getFile()));
        String row;
        List<Integer>               nodes = new ArrayList<Integer>();
        Map<Integer, List<Integer>> edges = new HashMap<Integer, List<Integer>>(); 
        
        boolean first = false;
        while ((row = csvReader.readLine()) != null) {
            if (!first) {
                first = true;
                continue;
            }
            
            String[] data = row.split(",");
            
            Integer u = Integer.parseInt(data[0]);
            Integer v = Integer.parseInt(data[1]);
            
            if (!nodes.contains(u)) {
                nodes.add(u);
            }
            if (!nodes.contains(v)) {
                nodes.add(v);
            }
            
            if (!edges.containsKey(u)) {
                // Create a new list of adjacent nodes for the new node u
                List<Integer> l = new ArrayList<Integer>();
                l.add(v);
                edges.put(u, l);
            } else {
                edges.get(u).add(v);
            }
        }
        
        for (Integer node : nodes) {
            if (!edges.containsKey(node)) {
                edges.put(node, new ArrayList<Integer>());
            }
        }
        
        csvReader.close();
        return new Graph(nodes, edges);
    }
    
}
