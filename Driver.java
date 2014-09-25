import java.util.Arrays;
import java.util.List;

import edu.neumont.ui.Picture;


public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String graphString = "";
		graphString += "0)      -->3)\n";
		graphString += "  \\    /  || \n";
		graphString += "   =>2)   || \n";
		graphString += "  /    \\  \\/ \n";
		graphString += "1)      -->4)\n";
		System.out.println(graphString);
		
		System.out.println("__________Topo Sort Test without Cycle (No error)__________");
		Graph g = new Graph(5);
		
		g.addEdge(0, 2, 100);
		
		g.addEdge(1, 2, 100);
		
		g.addEdge(2, 3, 100);
		g.addEdge(2, 4, 100);
		
		g.addEdge(3, 4, 100);
		
		TopologicalSort t = new TopologicalSort();
		
		List<Integer> l = t.sort(g);
		
		for(int i = 0; i < l.size(); i++)
		{
			System.out.println(l.get(i));
		}
		
		
		System.out.println("\n\n__________Topo Sort Test with Cycle (With error)__________");
		Graph g2 = new Graph(5);
		
		g2.addEdge(0, 2, 100);
		
		g2.addEdge(1, 2, 100);
		
		g2.addEdge(2, 3, 100);
		g2.addEdge(2, 4, 100);
		
		g2.addEdge(3, 4, 100);
		g2.addEdge(4, 2, 100); //Adds a cycle to the graph
		
		l = t.sort(g2);
		
		for(int i = 0; i < l.size(); i++)
		{
			System.out.println(l.get(i));
		}
		
	}

}










