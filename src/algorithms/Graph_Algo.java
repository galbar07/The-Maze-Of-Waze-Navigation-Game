package algorithms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.NodeData;
import dataStructure.node_data;
/**
 * This  class represents the set of graph-theory algorithms
 * which implemented graph_algorithms, the graph supports the following algorithms:
 * is connect, the length of shortest path and the "vertices path", TSP.
 * the class can also init and save the graph .
 * @author itay simhayev and lilach mor
 *
 */
public class Graph_Algo implements graph_algorithms
{
	private graph g;

	public Graph_Algo() {}
	public Graph_Algo(graph g) {
		this.g=g;
	}
	/**
	 * Init this set of algorithms on the parameter - graph.
	 * @param g - graph
	 */
	@Override
	public void init(graph g) 
	{
		this.g=g;
	}

	/**
	 * Init a graph from file
	 * @param file_name
	 */
	@Override
	public void init(String file_name) 
	{
		try
		{    
			FileInputStream file = new FileInputStream(file_name); 
			ObjectInputStream in = new ObjectInputStream(file); 
			g = (graph)in.readObject(); 
			in.close(); 
			file.close(); 
		} 
		catch(IOException ex) 
		{ 
			System.out.println("IOException is caught"); 
		} 
		catch(ClassNotFoundException ex) 
		{ 
			System.out.println("ClassNotFoundException is caught"); 
		} 
	}
	/** Saves the graph to a file.
	 * @param file_name
	 */
	@Override
	public void save(String file_name) 
	{
		try
		{    
			FileOutputStream file = new FileOutputStream(file_name); 
			ObjectOutputStream out = new ObjectOutputStream(file); 
			out.writeObject(g); 
			out.close(); 
			file.close(); 
		}   
		catch(IOException ex) 
		{ 
			System.out.println("IOException is caught"); 
		} 
	}
	/**
	 * Returns true if and only if (iff) there is a valid path from EVREY node to each
	 * other node. NOTE: assume directional graph - a valid path (a-->b) does NOT imply a valid path (b-->a).
	 * @return true if connected, else false
	 */
	@Override
	public boolean isConnected()
	{
		int first=0;//if its the first iteration
		int src=0,dest;
		for(Iterator<node_data> verIter=g.getV().iterator();verIter.hasNext();)
		{
			if(first==0) //check if the first vertex is reachable to the other
			{
				first++;
				src=verIter.next().getKey();
				int count=countReachableVer(src);
				if(count!=g.nodeSize()-1)
					return false;	
				resetTag();
			}
			else //check if every vertex has a path to src
			{
				dest=verIter.next().getKey();
				if(!isPath(dest,src))
					return false;
				resetTag();
			}
		}
		return true;
	}
	/**
	 * returns the length of the shortest path between src to dest
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return the length of the path
	 */
	@Override
	public double shortestPathDist(int src, int dest) 
	{
		if(g.getNode(src)==null||g.getNode(dest)==null)
		{
			throw new RuntimeException("The vertex is not in the graph");
		}
		if(src==dest)
			return 0;
		this.weightInfi();//set all the weight vertices to infinity
		this.resetInfo();// set all "father" vertices to null
		this.resetTag();// set all "color" vertices to white
		g.getNode(src).setWeight(0);
		g.getNode(src).setTag(1);
		try 
		{
			for(Iterator<edge_data> edgeIter=g.getE(src).iterator();edgeIter.hasNext();)
			{ //for the first vertex calculate the cost to arrive for each neighbor
				edge_data e=edgeIter.next();
				if(g.getNode(e.getDest()).getWeight()>g.getNode(src).getWeight()+e.getWeight())
				{
					g.getNode(e.getDest()).setWeight(g.getNode(src).getWeight()+e.getWeight());
					g.getNode(e.getDest()).setInfo(""+src);
				}
			}
		}
		catch(NullPointerException e)//if there is not a path
		{
			return Double.POSITIVE_INFINITY;
		}		
		ArrayList<node_data> ver=new ArrayList<node_data>();
		for(Iterator<node_data> verIter=g.getV().iterator();verIter.hasNext();)
		{ //insert all the vertices to array list
			node_data v=verIter.next();
			if(v.getKey()!=src)
			{
				ver.add(v);
			}
		}
		while(ver.size()!=0)
		{
			int minVer=findMinVer(ver);//find the vertex with the minimum weight and delete it from the array
			g.getNode(minVer).setTag(1);
			try 
			{
				for(Iterator<edge_data> edgeIter=g.getE(minVer).iterator();edgeIter.hasNext();)
				{
					edge_data e=edgeIter.next();
					if(g.getNode(e.getDest()).getTag()==0&&g.getNode(e.getDest()).getWeight()>g.getNode(minVer).getWeight()+e.getWeight())
					{
						g.getNode(e.getDest()).setWeight(g.getNode(minVer).getWeight()+e.getWeight());
						g.getNode(e.getDest()).setInfo(""+minVer);
					}
				}	
			}
			catch(NullPointerException e)
			{}
		}
		this.resetTag();
		return g.getNode(dest).getWeight();
	}
	/**
	 * returns the the shortest path between src to dest - as an ordered List of nodes:
	 * src--> n1-->n2-->...dest
	 * see: https://en.wikipedia.org/wiki/Shortest_path_problem
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return List of vertices
	 */
	@Override
	public List<node_data> shortestPath(int src, int dest) 
	{
		if(g.getNode(src)==null||g.getNode(dest)==null)
		{
			throw new RuntimeException("The vertex is not in the graph");
		}
		List <node_data> path=new ArrayList <node_data>();
		if(src==dest)
		{
			path.add(g.getNode(src));
			return path;
		}
		double dis=this.shortestPathDist(src,dest);
		if(dis<Double.POSITIVE_INFINITY)//if there is a path
		{
			node_data v=g.getNode(dest);
			path.add(v);
			while(!v.getInfo().isEmpty())//insert the path from dest to src to the array
			{
				int father=Integer.parseInt(v.getInfo());
				path.add(g.getNode(father));
				v=g.getNode(father);
			}
			List <node_data> OPath=new ArrayList <node_data>();

			for(int i=path.size()-1;i>=0;i--)//reverse the array
			{
				OPath.add(path.get(i));
			}
			return OPath;
		}
		else 
			return null;
	}
	/**
	 * computes a relatively short path which visit each node in the targets List.
	 * Note: this is NOT the classical traveling salesman problem, 
	 * as you can visit a node more than once, and there is no need to return to source node - 
	 * just a simple path going over all nodes in the list. 
	 * @param targets
	 * @return
	 */
	@Override
	public List<node_data> TSP(List<Integer> targets) 
	{
		if(!this.isConnected())
			return null;
		List<node_data> TSP=new ArrayList<node_data>();
		int indexSrc=(int)(Math.random()*targets.size());//start from random vertex in the list
		int src=targets.get(indexSrc);
		targets.remove(indexSrc);
		int dest=0,indexDest=0;
		TSP.add(g.getNode(src));
		while(targets.size()>0) 
		{
			double minWay=Double.POSITIVE_INFINITY;
			for(int j=0;j<targets.size();j++) //find the vertex that is the closest to src
			{
				if(this.shortestPathDist(src, targets.get(j))<minWay) 
				{
					minWay=this.shortestPathDist(src, targets.get(j));
					dest=targets.get(j);
					indexDest=j;
				}
			}
			List<node_data> TSPtemp=this.shortestPath(src, dest);
			for(int j=1;j<TSPtemp.size();j++) //add the vertices from i to i+1 to the list
			{
				TSP.add(TSPtemp.get(j));
			}
			src=dest;
			targets.remove(indexDest);
		}
		return TSP;
	}
	/** 
	 * Compute a deep copy of this graph.
	 * @return graph
	 */
	@Override
	public graph copy() 
	{		
		this.save("copy.txt");
		Graph_Algo copy=new Graph_Algo();
		copy.init("copy.txt");
		return copy.g;
	}
	/**
	 * change the color of all the vertices to white (0)
	 */
	private void resetTag()
	{
		for(Iterator<node_data> verIter=g.getV().iterator();verIter.hasNext();)
		{
			verIter.next().setTag(0);
		}
	}
	/**
	 * change the color of all the edges to white (0)
	 */
	public void resetTagEdge()
	{
		for(Iterator<node_data> verIter=g.getV().iterator();verIter.hasNext();)
		{
			int src=verIter.next().getKey();
			try 
			{
				for(Iterator<edge_data> edgeIter=g.getE(src).iterator();edgeIter.hasNext();)
				{
					edgeIter.next().setTag(0);
				}	
			}
			catch(NullPointerException e)
			{}
		}
	}
	/**
	 * change the weight of all the vertices to infinity
	 */
	private void weightInfi()
	{
		for(Iterator<node_data> verIter=g.getV().iterator();verIter.hasNext();)
		{
			verIter.next().setWeight(Double.POSITIVE_INFINITY);
		}
	}
	/**
	 * change the weight of all the vertices to 0
	 */
	private void resetWeight()
	{
		for(Iterator<node_data> verIter=g.getV().iterator();verIter.hasNext();)
		{
			verIter.next().setWeight(0);
		}
	}
	/**
	 * change the info(father) of all the vertices to null
	 */
	private void resetInfo()
	{
		for(Iterator<node_data> verIter=g.getV().iterator();verIter.hasNext();)
		{
			verIter.next().setInfo("");
		}
	}
	/**
	 * The function check the amount of reachable vertices from some vertex,if there is not return 0
	 * @param key - the id of the vertex
	 * @return  the amount of vertices that vertex key can reach them
	 */
	private int countReachableVer(int key)
	{
		g.getNode(key).setTag(1);//change the color of vertex key to gray
		int count=0;
		try 
		{
			for(Iterator<edge_data> edgeIter=g.getE(key).iterator();edgeIter.hasNext();)
			{
				int dest =edgeIter.next().getDest();
				if(g.getNode(dest).getTag()==0)
				{
					count++;
					count=count+countReachableVer(dest);
				}
			}
			return count;
		}
		catch(NullPointerException e)//if there is not white neighbor
		{
			return 0;
		}
	}
	/**
	 * The function check if there is a path between the vertices
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return true if there is a path from vertex src to  vertex dest, else false
	 */
	private boolean isPath(int src,int dest)
	{
		boolean path=false;
		g.getNode(src).setTag(1);//change the color of vertex key to gray
		try 
		{
			for(Iterator<edge_data> edgeIter=g.getE(src).iterator();edgeIter.hasNext();)
			{
				int verPath =edgeIter.next().getDest();
				if(g.getNode(verPath).getTag()==0)//check if the color is white
				{
					if(verPath==dest)//if verPath is the vertex that we search
					{
						return true;
					}
					else
					{
						if(!path&&isPath(verPath,dest))//check if from this vertex there is a edge to dest
						{
							path=true;
						}
					}
				}
			}
			this.resetTag();
			return path;
		}
		catch(NullPointerException e)//if there isn't a path
		{
			this.resetTag();
			return false;
		}
	}
	/**
	 * The function find the vertex with the minimum weight in the collection
	 * @param ver - collection of the vertices
	 * @return the id of the vertex with the minimum weight
	 */
	private int findMinVer(ArrayList <node_data> ver)
	{
		double weight=Double.POSITIVE_INFINITY;
		int minVer=0;
		int index=0;
		for(int i=0;i<ver.size();i++)
		{
			if(ver.get(i).getWeight()<weight)//check if the weight of vertex i is smaller than the curtain minimum weight
			{
				weight=ver.get(i).getWeight();
				minVer=ver.get(i).getKey();
				index=i;
			}
		}
		ver.remove(index);
		return minVer;
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
		//colorWhite(g.getV());
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
	
	public ArrayList<node_data> myNeighborsList(node_data v){
		//int ans=0;
		//colorWhite(this.g.getV());
		Stack<node_data> s=new Stack<node_data>();
		s.push(v);
		ArrayList<node_data> T=new ArrayList<>();
		while (!s.isEmpty()) {
			node_data nd=s.pop();
			if (nd.getTag()==0) {
				 T = allWhiteNeighbors(nd);
				//put into T the neighbors that wasn't visited 
				nd.setTag(1);
				for (int i = 0; i < T.size(); i++) {
					s.push(T.get(i));
				}
			}
		}
			
		return T;
	}
	/**
	 *  Determines which of this node's neighbors is white,
	 *  meaning- not been visited, with tag=0
	 * @param n the given node
	 * @return a list of all this node neighbors
	 */
	public ArrayList<node_data> allWhiteNeighbors(node_data n) {
		Collection<edge_data> edges = this.g.getE(n.getKey());
		ArrayList<node_data>List= new ArrayList<node_data>();
		if(edges!=null) {
			for (edge_data ed : edges) {
				//for each node in the edgesMap:
				//if (ed.getTag()==0) {
					//find all the neighbors with tag=0,
					//meaning wasn't visited
					List.add(this.g.getNode(ed.getDest()));
				}
			//}
		}
		return List;
	}
	public ArrayList<node_data> allNeighbors(node_data n) {
		Collection<edge_data> edges = this.g.getE(n.getKey());
		ArrayList<node_data>List= new ArrayList<node_data>();
		if(edges!=null) {
			for (edge_data ed : edges) {
				//for each node in the edgesMap:
				List.add(this.g.getNode(ed.getDest()));
			//	System.out.println(ed.getDest());
			}
		}
		return List;
	}
	
	/**
	 *Colors all this node's neighbors white, sets tag to 0
	 * @param v the given node
	 */
/*	private void colorWhite(Collection<node_data> v) {
		for (node_data node : v) {
			//for each node in this nodesMap:
			//color this node white
			node.setTag(0);
		}
	}*/


}