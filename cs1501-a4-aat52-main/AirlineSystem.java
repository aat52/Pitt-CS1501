/*************************************************************************
*  An Airline management system that uses a directed graph implemented using
*  adjacency lists.
*
*************************************************************************/

import java.util.*;
import java.io.*;

public class AirlineSystem {
private String [] cityNames = null;
private Bigraph G = null;
private static Scanner scan = null;
private static final int INFINITY = Integer.MAX_VALUE;


/**
* Test client.
*/
public static void main(String[] args) throws IOException {
  AirlineSystem airline = new AirlineSystem();
  scan = new Scanner(System.in);
  while(true){
    switch(airline.menu()){
      case 1:
        airline.readGraph();
        break;
      case 2:
        airline.printGraph();
        break;
      case 3:
        airline.edgeRoutes();
        break;
      case 4:
        airline.twoStops();
        break;
      case 5:
        airline.threeStops();
        break;
      case 6:
        airline.budget();
        break;
      case 7: //add a new route
        airline.addRoute();
        break;
      case 8: //remove a route
        airline.removeRoute();
        break;
      case 0:
        scan.close();
        System.exit(0);
        break;
      default:
        System.out.println("Incorrect option.");
    }
  }
}


private int menu(){
  System.out.println("*********************************");
  System.out.println("Welcome to Panther Airlines.");
  System.out.println("1. Read data from a file.");
  System.out.println("2. Display all direct routes.");
  System.out.println("3. Display routes with distances and prices.");
  System.out.println("4. Calculate 2-stop routes.");
  System.out.println("5. Calculate 3-stop routes.");
  System.out.println("6. Find all trips in your budget.");
  System.out.println("7. Add a new route.");
  System.out.println("8. Remove a route.");
  System.out.println("0. Exit.");
  System.out.println("*********************************");
  System.out.print("Please choose a menu option (1-9): ");
  int choice = Integer.parseInt(scan.nextLine());
  return choice;
}

private int calculationTypeTwo(){
  System.out.println("1. Calculate based on number of hops.");
  System.out.println("2. Calculate based on distance.");
  System.out.println("3. Calculate based on price.");
  System.out.print("Please choose a menu option (1-3): ");
  int choice = Integer.parseInt(scan.nextLine());
  return choice;
}

private int calculationTypeThree(){
  System.out.println("1. Calculate based on distance.");
  System.out.println("2. Calculate based on price.");
  System.out.print("Please choose a menu option (1-2): ");
  int choice = Integer.parseInt(scan.nextLine());
  return choice;
}

private void twoStops(){
  switch(calculationTypeTwo()){
    case 1:
      shortestHops();
      break;
    case 2:
      minDist();
      break;
    case 3:
      minPrice();
      break;
    default:
      System.out.print("Incorrect option.");
  }
}

private void threeStops(){
  switch(calculationTypeThree()){
    case 1:
      minDistThree();
      break;
    case 2:
      minPriceThree();
      break;
    default:
      System.out.print("Incorrect option.");
  }
}


private void readGraph() throws IOException {
  //read in graph data
  System.out.println("Please enter graph filename:");
  String fileName = scan.nextLine();
  Scanner fileScan = new Scanner(new FileInputStream(fileName));
  int v = Integer.parseInt(fileScan.nextLine());
  G = new Bigraph(v);

  cityNames = new String[v];
  for(int i=0; i<v; i++){
    cityNames[i] = fileScan.nextLine();
  }

  while(fileScan.hasNext()){
    int from = fileScan.nextInt();
    int to = fileScan.nextInt();
    int dist = fileScan.nextInt();
    double price = fileScan.nextFloat();
    G.addEdge(new DirectedEdge(from-1, to-1, price, dist));
    G.addEdge(new DirectedEdge(to-1, from-1, price, dist));
  }
  fileScan.close();
  System.out.println("Data imported successfully.");
  System.out.print("Please press ENTER to continue ...");
  scan.nextLine();
}


private void printGraph() {
  //print out the basic points
  if(G == null){
    System.out.println("Please import a graph first (option 1).");
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  } else {
    for (int i = 0; i < G.v; i++) {
      System.out.print(cityNames[i] + ": ");
      for (DirectedEdge e : G.iterateAdj(i)) {
        System.out.print(cityNames[e.to()] + "  ");
      }
      System.out.println();
    }
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();

  }
}


public void addRoute() {
  //add a new route
  for(int i=0; i<cityNames.length; i++){
    System.out.println(i+1 + ": " + cityNames[i]);
  }
  System.out.print("Please enter source city (1-" + cityNames.length + "): ");
  int source = Integer.parseInt(scan.nextLine());
  System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
  int destination = Integer.parseInt(scan.nextLine());
  System.out.print("Please enter price for the route: ");
  double price = Float.parseFloat(scan.nextLine());
  System.out.print("Please enter dist for the route: ");
  int dist = Integer.parseInt(scan.nextLine());
  source--;
  destination--;
  G.add(source, destination, price, dist);
  G.add(destination, source, price, dist);
  System.out.print("Please press ENTER to continue ...");
  scan.nextLine();
}


public void removeRoute() {
  //removes a route from the map
  for(int i=0; i<cityNames.length; i++){
    System.out.println(i+1 + ": " + cityNames[i]);
  }
  System.out.print("Please enter source city of route you would like to remove (1-" + cityNames.length + "): ");
  int source = Integer.parseInt(scan.nextLine());
  System.out.print("Please enter destination city of route you would like to remove (1-" + cityNames.length + "): ");
  int destination = Integer.parseInt(scan.nextLine());
  source--;
  destination--;
  G.remove(source,destination);
  System.out.print("Please press ENTER to continue ...");
  scan.nextLine();
}


public void edgeRoutes() {
  //prints out a more detailed run through of all the basic routes, their prices and distances
  for (int i = 0; i < G.v; i++) {
    for (DirectedEdge e : G.iterateAdj(i)) {
      System.out.println("A trip from " + cityNames[e.v] + " to " + cityNames[e.w] + " is " + e.weight("dist") + " miles and costs " + e.weight("price") + ".");
    }
  }
}

private void budget() {
  //finds all routes below the desired budget
  System.out.print("Please enter your budget: ");
  float budget = Float.parseFloat(scan.nextLine());
  for (int i = 0; i < G.v; i++){
    for (List<Integer> route : G.flightsUnder(budget, i)) {
      for (int city : route) {
        System.out.print(cityNames[city] + ", ");
      }
      System.out.println("is within your budget.");
    }
  }
}


private void minDist() {
  //find the minimum distance route between two points
  if(G == null){
    System.out.println("Please import a graph first (option 1).");
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  } else {
    for(int i=0; i<cityNames.length; i++){
      System.out.println(i+1 + ": " + cityNames[i]);
    }
    System.out.print("Please enter source city (1-" + cityNames.length + "): ");
    int source = Integer.parseInt(scan.nextLine());
    System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
    int destination = Integer.parseInt(scan.nextLine());
    source--;
    destination--;
    G.dijkstras(source,destination,"dist");
    if(!G.marked[destination]){
      System.out.println("There is no route from " + cityNames[source] + " to " + cityNames[destination]);
    } else {
      //  Use a stack to construct the shortest path from the edgeTo array
      // then print the number of hops (from the distTo array) and the path
      Stack<Integer> path = new Stack<>();
      for(int x = destination; x!= source; x = G.edgeTo[x]) {
        path.push(x);
      }
      path.push(source);
      System.out.println("The shortest route from " +cityNames[source] + " to " +cityNames[destination] + " is:");
      int last = path.pop();
      int current;
      while (!path.empty()){
        current = path.pop();
        System.out.println(cityNames[last] + " to " + cityNames[current] + " (" + G.getWeight(current,last,"dist") + " miles)");
        last = current;
      }
      System.out.println("This route is " + G.distTo[destination] + " miles.");
    }
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  }
}

private void minDistThree() {
  // find the minimum distance route between two points with a third between
  if(G == null){
    System.out.println("Please import a graph first (option 1).");
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  } else {
    for(int i=0; i<cityNames.length; i++){
      System.out.println(i+1 + ": " + cityNames[i]);
    }
    System.out.print("Please enter source city (1-" + cityNames.length + "): ");
    int source = Integer.parseInt(scan.nextLine());
    System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
    int destination = Integer.parseInt(scan.nextLine());
    System.out.print("Please enter middle city (1-" + cityNames.length + "): ");
    int middle = Integer.parseInt(scan.nextLine());
    source--;
    destination--;
    middle--;
    G.dijkstras(source,middle,"dist");
    if(!G.marked[middle]){
      System.out.println("There is no route from " + cityNames[source] + " to " + cityNames[destination]);
    } else {
      G.dijkstras(middle,destination,"dist");
      if (!G.marked[destination]){
      System.out.println("There is no route from " + cityNames[source] + " to " + cityNames[destination]);
      } else {


      //  Use a stack to construct the shortest path from the edgeTo array
      // then print the number of hops (from the distTo array) and the path
      int totalDist = 0;
      G.dijkstras(source,middle,"dist");
      Stack<Integer> path = new Stack<>();
      for(int x = middle; x!= source; x = G.edgeTo[x]) {
        path.push(x);
      }
      path.push(source);
      System.out.println("The shortest route from " +cityNames[source] + " to " +cityNames[destination] + " containing " + cityNames[middle] + " is:");
      int last = path.pop();
      int current;
      while (!path.empty()){
        current = path.pop();
        System.out.println(cityNames[last] + " to " + cityNames[current] + " (" + G.getWeight(current,last,"dist") + " miles)");
        last = current;
      }
      totalDist += G.distTo[middle];
      G.dijkstras(middle,destination,"dist");
      path = new Stack<>();
      for(int x = destination; x!= middle; x = G.edgeTo[x]) {
        path.push(x);
      }
      while (!path.empty()){
        current = path.pop();
        System.out.println(cityNames[last] + " to " + cityNames[current] + " (" + G.getWeight(current,last,"dist") + " miles)");
        last = current;
      }
      totalDist += G.distTo[destination];
      System.out.println("This trip would be " + totalDist + " miles total.");
    }
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
    }
  }
}

private void minPrice() {
  //find the minimum price route between two points
  if(G == null){
    System.out.println("Please import a graph first (option 1).");
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  } else {
    for(int i=0; i<cityNames.length; i++){
      System.out.println(i+1 + ": " + cityNames[i]);
    }
    System.out.print("Please enter source city (1-" + cityNames.length + "): ");
    int source = Integer.parseInt(scan.nextLine());
    System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
    int destination = Integer.parseInt(scan.nextLine());
    source--;
    destination--;
    G.dijkstras(source,destination,"price");
    if(!G.marked[destination]){
      System.out.println("There is no route from " + cityNames[source] + " to " + cityNames[destination]);
    } else {
      // Use a stack to construct the shortest path from the edgeTo array
      // then print the number of hops (from the distTo array) and the path
      Stack<Integer> path = new Stack<>();
      for(int x = destination; x!= source; x = G.edgeTo[x]) {
        path.push(x);
      }
      path.push(source);
      System.out.println("The cheapest route from " +cityNames[source] + " to " +cityNames[destination] + " is:");
      int last = path.pop();
      int current;
      while (!path.empty()){
        current = path.pop();
        System.out.println(cityNames[last] + " to " + cityNames[current] + " ($" + G.getWeight(current,last,"price") + ")");
        last = current;
      }
      System.out.println("and costs $" + G.distTo[destination] + ".");
    }
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  }
}

private void minPriceThree() {
  //find the minimum price route between two points with a third in between
  if(G == null){
    System.out.println("Please import a graph first (option 1).");
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  } else {
    for(int i=0; i<cityNames.length; i++){
      System.out.println(i+1 + ": " + cityNames[i]);
    }
    System.out.print("Please enter source city (1-" + cityNames.length + "): ");
    int source = Integer.parseInt(scan.nextLine());
    System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
    int destination = Integer.parseInt(scan.nextLine());
    System.out.print("Please enter middle city (1-" + cityNames.length + "): ");
    int middle = Integer.parseInt(scan.nextLine());
    source--;
    destination--;
    middle--;
    G.dijkstras(source,middle,"price");
    if(!G.marked[middle]){
      System.out.println("There is no route from " + cityNames[source] + " to " + cityNames[destination]);
    } else {
      G.dijkstras(middle,destination,"price");
      if (!G.marked[destination]){
      System.out.println("There is no route from " + cityNames[source] + " to " + cityNames[destination]);
      } else {


      //  Use a stack to construct the shortest path from the edgeTo array
      // then print the number of hops (from the distTo array) and the path
      int totalDist = 0;
      G.dijkstras(source,middle,"price");
      Stack<Integer> path = new Stack<>();
      for(int x = middle; x!= source; x = G.edgeTo[x]) {
        path.push(x);
      }
      path.push(source);
      System.out.println("The cheapest route from " +cityNames[source] + " to " +cityNames[destination] + " containing " + cityNames[middle] + " is:");
      int last = path.pop();
      int current;
      while (!path.empty()){
        current = path.pop();
        System.out.println(cityNames[last] + " to " + cityNames[current] + " ($" + G.getWeight(current,last,"price") + ")");
        last = current;
      }
      totalDist += G.distTo[middle];
      G.dijkstras(middle,destination,"price");
      path = new Stack<>();
      for(int x = destination; x!= middle; x = G.edgeTo[x]) {
        path.push(x);
      }
      while (!path.empty()){
        current = path.pop();
        System.out.println(cityNames[last] + " to " + cityNames[current] + " ($" + G.getWeight(current,last,"price") + ")");
        last = current;
      }
      totalDist += G.distTo[destination];
      System.out.println("This route is $" + totalDist + " total.");
    }
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  }
}
}

private void shortestHops() {
  //find the minimum route between two points in terms of the number of stops
  if(G == null){
    System.out.println("Please import a graph first (option 1).");
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  } else {
    for(int i=0; i<cityNames.length; i++){
      System.out.println(i+1 + ": " + cityNames[i]);
    }
    System.out.print("Please enter source city (1-" + cityNames.length + "): ");
    int source = Integer.parseInt(scan.nextLine());
    System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
    int destination = Integer.parseInt(scan.nextLine());
    source--;
    destination--;
    G.bfs(source);
    if(!G.marked[destination]){
      System.out.println("There is no route from " + cityNames[source] + " to " + cityNames[destination]);
    } else {
      // Use a stack to construct the shortest path from the edgeTo array
      // then print the number of hops (from the distTo array) and the path
      Stack<Integer> path = new Stack<>();
      for(int x = destination; x!= source; x = G.edgeTo[x]) {
        path.push(x);
      }
      path.push(source);
      System.out.println("the shortest route from " +cityNames[source] + " to " +cityNames[destination] + " is:");
      int last = path.pop();
      int current;
      while (!path.empty()){
        current = path.pop();
        System.out.println(cityNames[last] + " to " + cityNames[current]);
        last = current;
      }
      System.out.println("This route has " + G.distTo[destination] + " hop(s)");
    }
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  }

}


/**
*  The <tt>Digraph</tt> class represents an directed graph of vertices
*  named 0 through v-1. It supports the following operations: add an edge to
*  the graph, iterate over all of edges leaving a vertex.Self-loops are
*  permitted.
*/
private class Bigraph {
  private final int v;
  private int e;
  private LinkedList<DirectedEdge>[] adj;
  private boolean[] marked;  // marked[v] = is there an s-v path
  private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
  private int[] distTo;      // distTo[v] = number of edges shortest s-v path


  /**
  * Create an empty bigraph with v vertices.
  */
  public Bigraph(int v) {
    if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
    this.v = v;
    this.e = 0;
    @SuppressWarnings("unchecked")
    LinkedList<DirectedEdge>[] temp =
    (LinkedList<DirectedEdge>[]) new LinkedList[v];
    adj = temp;
    for (int i = 0; i < v; i++)
      adj[i] = new LinkedList<DirectedEdge>();
  }

  /**
  * Add the edge e to this bigraph.
  */
  public void addEdge(DirectedEdge edge) {
    int from = edge.from();
    adj[from].add(edge);
    e++;
  }

  public void add(int destination, int source, double price, int dist) {
    // add an edge, with both directions
    addEdge(new DirectedEdge(destination, source, price, dist));
    addEdge(new DirectedEdge(source, destination, price, dist));
  }

  public void remove(int destination, int source) {
    // remove an edge from a given start and end
    boolean found = false;
    DirectedEdge badEdgeOne = new DirectedEdge(-1,-1,-1,-1);
    DirectedEdge badEdgeTwo = new DirectedEdge(-1,-1,-1,-1);
    for (DirectedEdge z : adj[destination]) {
      if (z.to() == source) {
        found = true;
        badEdgeOne = z;
      }
    }
    for (DirectedEdge z : adj[source]) {
      if (z.to() == destination) {
        found = true;
        badEdgeTwo = z;
      }
    }
    if (found) {
      adj[destination].remove(badEdgeOne);
      adj[source].remove(badEdgeTwo);
      System.out.println("Successfully removed route!");
    } else {
      System.out.println("Selected route does not exist, and so it was not removed.");
    }
  }
  /**
  * Return the edges leaving vertex v as an Iterable.
  * To iterate over the edges leaving vertex v, use foreach notation:
  * <tt>for (DirectedEdge e : graph.adj(v))</tt>.
  */

  public double getWeight(int source,int destination,String type){
    //return the weight between two points for a given type
    int dist;
    for (DirectedEdge edge : iterateAdj(source)) {
      if (edge.to() == destination){
        return edge.weight(type);
      }
    }
    return -1;
  }

  public Iterable<DirectedEdge> iterateAdj(int v) {
    return adj[v];
  }

  /*

  private int[] addX(int arr[], int x)
  {
      int n = arr.length()
      int newarr[] = new int[n + 1];
      // insert the elements from
      // the old array into the new array
      // insert all elements till n
      // then insert x at n+1
      for (int i = 0; i < n; i++)
        newarr[i] = arr[i];
      newarr[n] = x;
      return newarr;
  }

  private int[][] addX(int[][] arr, int[] x)
  {
      int n = arr.length()
      int newarr[] = new int[n + 1];
      // insert the elements from
      // the old array into the new array
      // insert all elements till n
      // then insert x at n+1
      for (int i = 0; i < n; i++)
        newarr[i] = arr[i];
      newarr[n] = x;
      return newarr;
  }
  */
  public List<List<Integer>> flightsUnder(double price, int source){
    //return a list of list of integers representing a route
    List<List<Integer>> routes = new ArrayList<>();
    List<Integer> basePoint = new ArrayList<>();
    basePoint.add(source);
    for (List<Integer> arr : flightsUnder(price,source,0,basePoint)) {
      routes.add(arr);
    }
    return routes;
  }

  private List<List<Integer>> flightsUnder(double price,int source, double totalCost, List<Integer> points) {
    //recursively makes lists of legitimate routes by adding stops as long as they stay under the given price.
    List<List<Integer>> routes = new ArrayList<>();
    for (DirectedEdge edge : iterateAdj(source)) {
      if (totalCost + edge.weight("price") <= price && ! points.contains(edge.to())) {
        List<Integer> newPoints = new ArrayList<>();
        for (int point : points) {
          newPoints.add(point);
        }
        newPoints.add(edge.to());
        //System.out.print("" + points);
        //System.out.println(" -> " + newPoints);
        routes.add(newPoints);
        for (List<Integer> arr : flightsUnder(price,edge.to(),totalCost + edge.weight("price"),newPoints)) {
          routes.add(arr);
        }
      }
    }
    return routes;
  }

  public void bfs(int source) {
    marked = new boolean[this.v];
    distTo = new int[this.v];
    edgeTo = new int[this.v];

    Queue<Integer> q = new LinkedList<Integer>();
    for (int i = 0; i < v; i++){
      distTo[i] = INFINITY;
      marked[i] = false;
    }
    distTo[source] = 0;
    marked[source] = true;
    q.add(source);

    while (!q.isEmpty()) {
      int v = q.remove();
      for (DirectedEdge w : iterateAdj(v)) {
        if (!marked[w.to()]) {
        // BFS implementation
          edgeTo[w.to()] = v;
          distTo[w.to()] = distTo[v] + 1;

          marked[w.to()] = true;
          q.add(w.to());
        }
      }
    }
  }


  public void dijkstras(int source, int destination, String type) {
    marked = new boolean[this.v];
    distTo = new int[this.v];
    edgeTo = new int[this.v];


    for (int i = 0; i < v; i++){
      distTo[i] = INFINITY;
      marked[i] = false;
    }
    distTo[source] = 0;
    marked[source] = true;
    int nMarked = 1;

    int current = source;
    //System.out.println("Start: " + source);
    //System.out.println("Finish: " + destination);
    while (nMarked < this.v) {
      for (DirectedEdge w : iterateAdj(current)) {
        //System.out.println(w.to());
        if (distTo[current]+w.weight(type) < distTo[w.to()]) {
          //update edgeTo and distTo
          distTo[w.to()] = distTo[current] + (int) w.weight(type);
          edgeTo[w.to()] = current;
        }
      }
      /*
      for (int a : edgeTo){
        System.out.print(a + ",");
      }
      System.out.println();
      for (int a : distTo){
        System.out.print(a + ",");
      }
      System.out.println();
      */
      //Find the vertex with minimim path distance
      //This can be done more effiently using a priority queue!
      int min = INFINITY;
      current = -1;

      for(int i=0; i<distTo.length; i++){
        if(marked[i]) {
          continue;
        }
        if(distTo[i] < min){
          min = distTo[i];
          current = i;
        }
      }
      //Update marked[] and nMarked. Check for disconnected graph.
      if (current == -1) {
        break;
      } else {
        marked[current] = true;
        nMarked++;
      }
    }
      /*
      for (int a : edgeTo){
        System.out.print(a + ",");
      }
      System.out.println();

      for (int a : distTo){
        System.out.print(a + ",");
      }
      System.out.println();
      */
  }
}

/**
*  The <tt>DirectedEdge</tt> class represents an edge in an directed graph.
*/

private class DirectedEdge {
  private final int v;
  private final int w;
  private final double price;
  private final int dist;
  /**
  * Create a directed edge from v to w with given weight.
  */
  public DirectedEdge(int v, int w, double price, int dist) {
    this.v = v;
    this.w = w;
    this.price = price;
    this.dist = dist;
  }

  public int from(){
    return v;
  }

  public int to(){
    return w;
  }

  public double weight(String which){
    //by making a single weight returning algorithm that can modally change between price and dist, we can more easily implement dijkstra's by making it return either one based on an argument
    if (which == "price"){
      return price;
    } else if (which == "dist") {
      return dist;
    } else {
      return -1;
    }
  }
}
}
