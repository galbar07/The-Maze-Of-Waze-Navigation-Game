package algorithms;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import javax.xml.crypto.NodeSetData;

import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;

/**
 * This empty class represents the set of graph-theory algorithms
 * which should be implemented as part of Ex2 - Do edit this class.
 * @author Eden Reuveni
 * @author Gal bar
 *
 */
public class Graph_Algo implements graph_algorithms,java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8972207312025958107L;
	private graph graph_algo;

	public Graph_Algo() {
		graph_algo = new DGraph();
	}
	
	public Graph_Algo(graph g) {
		graph_algo = g;
	}
	
	/**
	 * Init this set of algorithms on the parameter - graph.
	 * @param g
	 */
	@Override
	public void init(graph g) {
		graph_algo =  g;
	}
	/**
	 * Init a graph from file
	 * @param file_name
	 */
	@Override
	public void init(String file_name) {
		deserialize(file_name);
	}
	/** Saves the graph to a file.
	 * 
	 * @param file_name
	 */
	@Override
	public void save(String file_name) {
		serialize(file_name); 
	}
	/**
	 * Returns true if and only if (iff) there is a valid path from EVREY node to each
	 * other node. NOTE: assume directional graph - a valid path (a-->b) does NOT imply a valid path (b-->a).
	 * @return the boolean value of the question
	 */
	@Override
	public boolean isConnected() {
		Collection<node_data> forSearch=this.graph_algo.getV();
		for (node_data v : forSearch) {
			//for each node in the edges hash map:
			//check if the amount of all neighbors equals to number of nodes in graph 
			int num=myNeighbors(v);
			if (num<forSearch.size())
				//if it is smaller, it can't be reached to all neighbors from this node
				return false;
		}
		return true;
	}
	
	/**
	 * Determines which of the nodes in the graph are 
	 * connected by an edge, meaning- this node's neighbors
	 * and counts them
	 * @param v the given node
	 * @return the amount of neighbors
	 */
	private int myNeighbors(node_data v) {
		int ans=0;
		colorWhite(this.graph_algo.getV());
		Stack<node_data> s=new Stack<node_data>();
		s.push(v);
		while (!s.isEmpty()) {
			node_data nd=s.pop();
			if (nd.getTag()==0) {
				//as long as this node wasn't visited
				ans++;
				ArrayList<node_data> T = allWhiteNeighbors(nd);
				//put into T the neighbors that wasn't visited 
				nd.setTag(1);
				for (int i = 0; i < T.size(); i++) {
					s.push(T.get(i));
				}
			}
		}
		return ans;
	}
	/**
	 *  Determines which of this node's neighbors is white,
	 *  meaning- not been visited, with tag=0
	 * @param n the given node
	 * @return a list of all this node neighbors
	 */
	private ArrayList<node_data> allWhiteNeighbors(node_data n) {
		Collection<edge_data> edges = this.graph_algo.getE(n.getKey());
		ArrayList<node_data>List= new ArrayList<node_data>();
		if(edges!=null) {
			for (edge_data ed : edges) {
				//for each node in the edgesMap:
				if (ed.getTag()==0) {
					//find all the neighbors with tag=0,
					//meaning wasn't visited
					List.add(this.graph_algo.getNode(ed.getDest()));
				}
			}
		}
		return List;
	}
	/**
	 *Colors all this node's neighbors white, sets tag to 0
	 * @param v the given node
	 */
	private void colorWhite(Collection<node_data> v) {
		for (node_data node : v) {
			//for each node in this nodesMap:
			//color this node white
			node.setTag(0);
		}
	}
	/**
	 * Returns the length of the shortest path between src to dest
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return the distance of the shortest path between src and dest
	 */
	@Override
	public double shortestPathDist(int src, int dest) {
		List <node_data> listFromShortest=shortestPath(src, dest);
		if (listFromShortest==null)
			return Double.MIN_VALUE;
		return this.graph_algo.getNode(dest).getWeight();
		//dest node will hold the 'cost' of the path
	}
	/**
	 * Returns the the shortest path between src to dest - as an ordered List of nodes:
	 * src--> n1-->n2-->...dest
	 * see: https://en.wikipedia.org/wiki/Shortest_path_problem
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return
	 */
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		boolean stop = false;
		if (this.graph_algo.getNode(src)==null || (this.graph_algo.getNode(dest)==null))
			//if either of the src and dest nodes isn't in the graph	
			return null;
		ArrayList<node_data>List = new ArrayList<node_data>();
		Collection<node_data> dij = this.graph_algo.getV();
		PriorityQueue<node_data> queue = new PriorityQueue<node_data>();
		for (node_data v : dij) {
			//for each node in the nodesMap:
			//set the weight to infinity and info to null
			v.setWeight(Double.MAX_VALUE);
			v.setInfo("null");			
		}
		this.graph_algo.getNode(src).setWeight(0);
		//set the src node weight to be 0, making it to minimal
		queue.addAll(dij);
		this.graph_algo.getNode(src).setInfo("null");
		while (!queue.isEmpty()) {
			node_data nd = queue.poll();

			Collection<edge_data> edges = this.graph_algo.getE(nd.getKey());
			ArrayList<node_data>ListOfNeighbors= new ArrayList<node_data>();
			if(edges!=null) {
				for (edge_data ed : edges) {
					//for each edge in edgesMapL
					//add to list this edge dest
					ListOfNeighbors.add(this.graph_algo.getNode(ed.getDest()));

				}
				for (int i = 0; i < ListOfNeighbors.size(); i++) {
					if (ListOfNeighbors.get(i).getWeight()> nd.getWeight() + this.graph_algo.getEdge(nd.getKey(), ListOfNeighbors.get(i).getKey()).getWeight()) {
						//if the next node your looking at is 'lighter' than this
						ListOfNeighbors.get(i).setWeight(nd.getWeight() + this.graph_algo.getEdge(nd.getKey(), ListOfNeighbors.get(i).getKey()).getWeight());
						//add them and set the info to this key
						//meaning, this is the node it 'came from'
						ListOfNeighbors.get(i).setInfo(""+nd.getKey());
					}
					if(ListOfNeighbors.get(i).getKey()==dest) {
						//if this node is the dest, stop the algorithm
						//you got to the destination
						stop = true;
						break;
					}
				}
				if(stop) {
					break;
				}
			}
		}
		String str = "";
		int search = dest;
		node_data node = this.graph_algo.getNode(dest);
		if(node.getWeight()== Double.MAX_VALUE)
			//if dest weight stayed infinity- there is no path from src to it
			return null;		
		while(!node.getInfo().equals("null")) {
			//add all the nodes you went through, according to their info
			str = node.getInfo();
			List.add(this.graph_algo.getNode((search)));
			search = Integer.parseInt(str);	
			node = this.graph_algo.getNode(search);				
		}
		List.add(this.graph_algo.getNode(src));
		Collections.reverse(List);
		if (!List.contains(this.graph_algo.getNode(dest)))	
			//if the list doesn't contain dest- there is no path between src to it
			return null;
		return List;
	}
	/**
	 * Computes a relatively short path which visit each node in the targets List.
	 * Note: this is NOT the classical traveling salesman problem, 
	 * as you can visit a node more than once, and there is no need to return to source node - 
	 * just a simple path going over all nodes in the list. 
	 * @param targets the given list of the nodes wanted to go through
	 * @return a list of the nodes visited in this path
	 */
	@Override
	public List<node_data> TSP(List<Integer> targets) {
		if (targets.isEmpty())
			//there are no nodes in the list
			return null;
		Graph_Algo gr=new Graph_Algo();
		gr.init(this.graph_algo);
		Collections.sort(targets);
		List <node_data> nodesFromTSP = new ArrayList<node_data>();
		int firstNode = targets.get(0);
		for (int i = 1; i < targets.size(); i++) {
			int nextNode=targets.get(i);
			if (gr.shortestPath(firstNode, nextNode)==null)
				//find the shortest path from first node to the one in i
				return null;
			nodesFromTSP.addAll(gr.shortestPath(firstNode, nextNode));
			nodesFromTSP.remove(graph_algo.getNode(nextNode));
			//remove from list the node you visited at
			firstNode=nextNode;
			//set first node to the one in i 
		}
		nodesFromTSP.addAll(gr.shortestPath(firstNode, firstNode));
		return nodesFromTSP;
	}
	/** 
	 * Compute a deep copy of this graph.
	 * @return the copied graph
	 */
	@Override
	public graph copy() {
		graph copy = new DGraph();
		Collection<node_data>Node_copy = this.graph_algo.getV();
		for (node_data v : Node_copy) {
			//for each node in nodesMap:
			//add it to the copy graph
			copy.addNode(v);
		}
		for (node_data v : Node_copy) {
			Collection<edge_data>edge_copy = this.graph_algo.getE(v.getKey());
			if(edge_copy==null) 
				break;
			for (edge_data E : edge_copy) {
				//for each edge in edgesMap:
				//connect nodes in copy graph, the same why they are in this
				copy.connect(v.getKey(), E.getDest(), E.getWeight());
			}	
		}
		return copy;
	}
/**
 * //serialize the object to a .txt file
 * @param file_name
 */
	
	private void serialize(String file_name) {
		try
		{    
			FileOutputStream file = new FileOutputStream(file_name); 
			ObjectOutputStream out = new ObjectOutputStream(file); 

			out.writeObject(this.graph_algo); 

			out.close(); 
			file.close(); 
		}

		catch(IOException  ex) 
		{ 
			System.out.println("IOException is caught"); 
		}               
	}
/**
 * deserialize the object from a .txt file
 * @param file_name
 */
	private void deserialize(String file_name) {
		graph_algo = new DGraph();
		try
		{    
			FileInputStream file = new FileInputStream(file_name); 
			ObjectInputStream in = new ObjectInputStream(file); 

			try {
				graph_algo = (DGraph)in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
			in.close(); 
			file.close(); 
		}
		catch(IOException ex) 
		{ 
			System.out.println("IOException is caught"); 
		} 


	}
}





