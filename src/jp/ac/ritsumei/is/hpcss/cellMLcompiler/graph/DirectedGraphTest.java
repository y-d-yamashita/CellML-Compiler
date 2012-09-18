package jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph;

import org.junit.Test;

import jp.ac.ritsumei.is.hpcss.cellMLcompiler.graph.exception.GraphException;



public class DirectedGraphTest {

	public class MyNode extends Vertex{
	      String label;
	       public MyNode(String label){
	             this.label =label;
	      }
	       @Override
	       public String toString(){
	             return label ;
	      }
	}



	public class MyEdge extends Edge{
	      String label;
	       public MyEdge(String label){
	             this.label =label;
	      }
	       @Override
	       public String toString(){
	             return label ;
	      }
	}

	@Test
	public void testMain(){
		
		  Graph<MyNode, MyEdge> g = new DirectedGraph<MyNode,MyEdge>();
          
		  MyNode n1 = new MyNode("n1" );
		   MyNode n3 = new MyNode("n3" );

		  MyNode n2 = new MyNode("n2" );
       
          try {
        	//  g.addVertex(n1);
	          g.addVertex(n1);
	          g.addVertex(n2);
	          g.addVertex(n3);

		} catch (GraphException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
          
          MyEdge e1 =  new MyEdge("e1" );
          MyEdge e2 =  new MyEdge("e2" );
          MyEdge e3 =  new MyEdge("e3" );
          
          try {
        	  g.addEdge(e1, n1,n2);
	          g.addEdge(e2, n2,n3);
	          g.addEdge(e3, n1,n3);
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
              
          System. out.println(g.toString());
          
          System.out.print("Nodes:");
          for(MyNode n:g.getVertexes()){
        	  System.out.print(n.toString()+",");
          }
          System.out.println();

          System.out.print("Edges:");
          for(MyEdge e:g.getEdges()){
        	  System.out.print(e.toString()+",");
          }
          System.out.println();
}

}
