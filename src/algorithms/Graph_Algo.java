package algorithms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
 * @author Eden Reuveni
 * @autor Gal Bar
 *
 */
public class Graph_Algo implements graph_algorithms
{
	private graph graph_algo;

	public Graph_Algo() {}
	public Graph_Algo(graph g) {
		this.graph_algo=g;
	}
	/**
	 * Init this set of algorithms on the parameter - graph.
	 * @param g - graph
	 */
	@Override
	public void init(graph g) 
	{
		this.graph_algo=g;
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
			ObjectInputStream input = new ObjectInputStream(file); 
			graph_algo = (graph)input.readObject(); 
			input.close(); 
			file.close(); 
		} 
		catch(IOException e) 
		{ 
			System.out.println("IOException is caught"); 
		} 
		catch(ClassNotFoundException e) 
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
			out.writeObject(graph_algo); 
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
		int firstNode=0;//if its the first iteration
		int src=0,dest;
		for(Iterator<node_data> allNodes=graph_algo.getV().iterator();allNodes.hasNext();)
		{
			if(firstNode==0) //check if the first vertex is reachable to the other
			{
				firstNode++;
				src=allNodes.next().getKey();
				int count=countReachableNodes(src);
				if(count!=graph_algo.nodeSize()-1)
					return false;	
				resetTag();
			}
			else //check if every vertex has a path to src
			{
				dest=allNodes.next().getKey();
				if(!isAValidPath(dest,src))
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
		if(graph_algo.getNode(src)==null||graph_algo.getNode(dest)==null)
		{
			throw new RuntimeException("The node is not in the graph");
		}
		if(src==dest)
			return 0;
		this.setWeight();//set all the weight vertices to infinity
		this.resetInfo();// set all "father" vertices to null
		this.resetTag();// set all "color" vertices to white
		graph_algo.getNode(src).setWeight(0);
		graph_algo.getNode(src).setTag(1);
		try 
		{
			for(Iterator<edge_data> iteratorForEdges=graph_algo.getE(src).iterator();iteratorForEdges.hasNext();)
			{ //for the first vertex calculate the cost to arrive for each neighbor
				edge_data e=iteratorForEdges.next();
				if(graph_algo.getNode(e.getDest()).getWeight()>graph_algo.getNode(src).getWeight()+e.getWeight())
				{
					graph_algo.getNode(e.getDest()).setWeight(graph_algo.getNode(src).getWeight()+e.getWeight());
					graph_algo.getNode(e.getDest()).setInfo(""+src);
				}
			}
		}
		catch(NullPointerException e)//if there is not a path
		{
			return Double.POSITIVE_INFINITY;
		}		
		ArrayList<node_data> iteratorForNodes=new ArrayList<node_data>();
		for(Iterator<node_data> itertorForNodes=graph_algo.getV().iterator();itertorForNodes.hasNext();)
		{ //insert all the vertices to array list
			node_data n=itertorForNodes.next();
			if(n.getKey()!=src)
			{
				iteratorForNodes.add(n);
			}
		}
		while(iteratorForNodes.size()!=0)
		{
			int minimalNode=findMinNode(iteratorForNodes);//find the vertex with the minimum weight and delete it from the array
			graph_algo.getNode(minimalNode).setTag(1);
			try 
			{
				for(Iterator<edge_data> iteratorForEdges=graph_algo.getE(minimalNode).iterator();iteratorForEdges.hasNext();)
				{
					edge_data e=iteratorForEdges.next();
					if(graph_algo.getNode(e.getDest()).getTag()==0&&graph_algo.getNode(e.getDest()).getWeight()>graph_algo.getNode(minimalNode).getWeight()+e.getWeight())
					{
						graph_algo.getNode(e.getDest()).setWeight(graph_algo.getNode(minimalNode).getWeight()+e.getWeight());
						graph_algo.getNode(e.getDest()).setInfo(""+minimalNode);
					}
				}	
			}
			catch(NullPointerException e)
			{}
		}
		this.resetTag();
		return graph_algo.getNode(dest).getWeight();
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
		if(graph_algo.getNode(src)==null||graph_algo.getNode(dest)==null)
		{
			throw new RuntimeException("The vertex is not in the graph");
		}
		List <node_data> list_to_go_through=new ArrayList <node_data>();
		if(src==dest)
		{
			list_to_go_through.add(graph_algo.getNode(src));
			return list_to_go_through;
		}
		double distance=this.shortestPathDist(src,dest);
		if(distance<Double.POSITIVE_INFINITY)//if there is a path
		{
			node_data v=graph_algo.getNode(dest);
			list_to_go_through.add(v);
			while(!v.getInfo().isEmpty())//insert the path from dest to src to the array
			{
				int father=Integer.parseInt(v.getInfo());
				list_to_go_through.add(graph_algo.getNode(father));
				v=graph_algo.getNode(father);
			}
			List <node_data> optimalPath=new ArrayList <node_data>();

			for(int i=list_to_go_through.size()-1;i>=0;i--)//reverse the array
			{
				optimalPath.add(list_to_go_through.get(i));
			}
			return optimalPath;
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
		int indexOfSrc=(int)(Math.random()*targets.size());//start from random vertex in the list
		int src=targets.get(indexOfSrc);
		targets.remove(indexOfSrc);
		int dest=0,indexOfDest=0;
		TSP.add(graph_algo.getNode(src));
		while(targets.size()>0) 
		{
			double minimalWay=Double.POSITIVE_INFINITY;
			for(int j=0;j<targets.size();j++) //find the vertex that is the closest to src
			{
				if(this.shortestPathDist(src, targets.get(j))<minimalWay) 
				{
					minimalWay=this.shortestPathDist(src, targets.get(j));
					dest=targets.get(j);
					indexOfDest=j;
				}
			}
			List<node_data> tempTSP=this.shortestPath(src, dest);
			for(int j=1;j<tempTSP.size();j++) //add the vertices from i to i+1 to the list
			{
				TSP.add(tempTSP.get(j));
			}
			src=dest;
			targets.remove(indexOfDest);
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
		return copy.graph_algo;
	}
	/**
	 * change the color of all the vertices to white (0)
	 */
	private void resetTag()
	{
		for(Iterator<node_data> iteratorForNodes=graph_algo.getV().iterator();iteratorForNodes.hasNext();)
		{
			iteratorForNodes.next().setTag(0);
		}
	}
	/**
	 * change the color of all the edges to white (0)
	 */
	public void resetTagEdge()
	{
		for(Iterator<node_data> iteratorForNodes=graph_algo.getV().iterator();iteratorForNodes.hasNext();)
		{
			int src=iteratorForNodes.next().getKey();
			try 
			{
				for(Iterator<edge_data> edgeIter=graph_algo.getE(src).iterator();edgeIter.hasNext();)
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
	private void setWeight()
	{
		for(Iterator<node_data> iteratorForNodes=graph_algo.getV().iterator();iteratorForNodes.hasNext();)
		{
			iteratorForNodes.next().setWeight(Double.POSITIVE_INFINITY);
		}
	}
	/**
	 * change the weight of all the vertices to 0
	 */
	private void resetWeight()
	{
		for(Iterator<node_data> verIter=graph_algo.getV().iterator();verIter.hasNext();)
		{
			verIter.next().setWeight(0);
		}
	}
	/**
	 * change the info(father) of all the vertices to null
	 */
	private void resetInfo()
	{
		for(Iterator<node_data> verIter=graph_algo.getV().iterator();verIter.hasNext();)
		{
			verIter.next().setInfo("");
		}
	}
	/**
	 * The function check the amount of reachable vertices from some vertex,if there is not return 0
	 * @param key - the id of the vertex
	 * @return  the amount of vertices that vertex key can reach them
	 */
	private int countReachableNodes(int key)
	{
		graph_algo.getNode(key).setTag(1);//change the color of vertex key to gray
		int count=0;
		try 
		{
			for(Iterator<edge_data> iteratorForEdges=graph_algo.getE(key).iterator();iteratorForEdges.hasNext();)
			{
				int dest =iteratorForEdges.next().getDest();
				if(graph_algo.getNode(dest).getTag()==0)
				{
					count++;
					count=count+countReachableNodes(dest);
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
	private boolean isAValidPath(int src,int dest)
	{
		boolean path=false;
		graph_algo.getNode(src).setTag(1);//change the color of vertex key to gray
		try 
		{
			for(Iterator<edge_data> iteratorForEdges=graph_algo.getE(src).iterator();iteratorForEdges.hasNext();)
			{
				int verPath =iteratorForEdges.next().getDest();
				if(graph_algo.getNode(verPath).getTag()==0)//check if the color is white
				{
					if(verPath==dest)//if verPath is the vertex that we search
					{
						return true;
					}
					else
					{
						if(!path&&isAValidPath(verPath,dest))//check if from this vertex there is a edge to dest
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
	private int findMinNode(ArrayList <node_data> ver)
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


}