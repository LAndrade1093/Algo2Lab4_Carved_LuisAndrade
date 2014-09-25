import java.util.List;
import java.util.ArrayList;

public class TopologicalSort 
{
	
	public List<Integer> sort(Graph g)
	{
		Graph topoGraph = g;
		List<Integer> sortedNodes = new ArrayList<>();
		List<Integer> sourceNodes = new ArrayList<>();
		initializeSourceNodeList(sourceNodes, topoGraph);
		
		while(!sourceNodes.isEmpty())
		{
			int currentNode = sourceNodes.remove(0);
			sortedNodes.add(currentNode);
			for(int neighbor = topoGraph.first(currentNode); neighbor < topoGraph.vcount(); neighbor = topoGraph.next(currentNode, neighbor))
			{
				topoGraph.removeEdge(currentNode, neighbor);
				if(countIncomingEdges(neighbor, topoGraph) == 0)
				{
					sourceNodes.add(neighbor);
				}
			}
		}
		
		if(!noEdgesRemaining(topoGraph))
		{
			throw new IllegalArgumentException("Graph contains a cycle; topological sort cannot be completed");
		}
		
		return sortedNodes;
	}
	
	private void initializeSourceNodeList(List<Integer> sourceNodes, Graph g)
	{
		for(int i = 0; i < g.vcount(); i++)
		{
			if(countIncomingEdges(i, g) == 0)
			{
				sourceNodes.add(i);
			}
		}
	}
	
	private int countIncomingEdges(int vertexIndex, Graph g)
	{
		return g.degree(vertexIndex);
	}
	
	private boolean noEdgesRemaining(Graph topoGraph)
	{
		boolean valid = true;
		
		for(int i = 0; i < topoGraph.vcount() && valid; i++)
		{
			if(countIncomingEdges(i, topoGraph) > 0)
			{
				valid = false;
			}
		}
		
		return valid;
	}
}
