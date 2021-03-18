package friends;

import java.util.ArrayList;
import java.util.HashMap;

//import java.util.Iterator;
//import java.util.Map;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		ArrayList<String> shortestPath = new ArrayList<String>();
		boolean[] visited = new boolean[g.members.length];
		HashMap<Person,Person> parents = new HashMap<Person,Person>(g.members.length * 2);  //format of Person(friend)key and Person(parent)value
		Queue<Person> queue = new Queue<Person>();
		
		//put first parent in
		parents.put( g.members[g.map.get(p1)] , null);
		
		//enqueue starting point and marked visited /also add starting pt to return
		queue.enqueue(g.members[g.map.get(p1)]);
		visited[g.map.get(p1)] = true;
		
		//bfs code
		while( !queue.isEmpty() ) {
			Person currentPerson = queue.dequeue();
			visited[g.map.get(currentPerson.name)] = true;
			
			Friend friend = g.members[g.map.get(currentPerson.name)].first;
			
			while( friend != null ) {
				
				if( visited[friend.fnum] == false ) {
					
					// enqueue friend from dequeued current person
					queue.enqueue( g.members[friend.fnum] );
					
					// allocate parent( current friend parent is current person )
					// only if the respective parent value is empty
					if ( parents.get(g.members[friend.fnum]) == null ) {
						 parents.put( g.members[friend.fnum] , currentPerson);
					}
					
				}
				
				friend = friend.next;
			}
		}
		//parent hashmap complete
		//call do bfs again for unvisited vertices
		for (int i = 0; i < visited.length; i ++) {
			if (visited[i] == false) {
				String notVisitedYet =  g.members[i].name;
				scBFS(g, notVisitedYet, visited, queue, parents);
			}
		}
		// create temp backwards array list
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(g.members[g.map.get(p2)].name);
		
		Person ptr = parents.get(g.members[g.map.get(p2)]);
		
		while (ptr != null) {
			temp.add(ptr.name); //temp.add(g.members[g.map.get(ptr.name)].name);
			ptr = parents.get(g.members[g.map.get(ptr.name)]);
		}
		
		
		// reverse temp for shortestPath
		if(temp.get(temp.size()-1).contentEquals(p1) ) {
			for (int i = temp.size()-1; i >= 0; i-=1) {
				shortestPath.add(temp.get(i));
			}
		}else {
			return null;
		}
		
		//XXXXXXXXXXXXXX// prints parent hashmap ignore its just so i can see the hashmap/////////////////////////////////////////////////////////////XXXXXXXXXXXXX
//		 System.out.println();
//		Iterator<Person> iterator = parents.keySet().iterator();
//		while (iterator.hasNext()) {
//		   Person key = iterator.next();
//		   Person value = parents.get(key);
//		   if (value != null) {
//			  System.out.println(key.name + " " + value.name); 
//		   }else {
//			   System.out.println(key.name + "none"); 
//		   }
//		}
		//XXXXXXXXXXXXXXX////////////////////////////////////////////////////////////////////////////////////XXXXXXXXXXXXXXXX
		
		return shortestPath;
		
	}
	
	
	
	private static void scBFS(Graph g, String notVisitedYet, boolean[] visited, Queue<Person> queue, HashMap<Person,Person> parents) {
	//same shit as before
		parents.put( g.members[g.map.get(notVisitedYet)] , null);
		//enqueue starting point and marked visited /also add starting pt to return
		queue.enqueue(g.members[g.map.get(notVisitedYet)]);
		visited[g.map.get(notVisitedYet)] = true;
		while( !queue.isEmpty() ) {
			Person currentPerson = queue.dequeue();
			visited[g.map.get(currentPerson.name)] = true;
			Friend friend = g.members[g.map.get(currentPerson.name)].first;
			while( friend != null ) {
				if( visited[friend.fnum] == false ) {
					// enqueue friend from dequeued current person
					queue.enqueue( g.members[friend.fnum] );
					// allocate parent( current friend parent is current person )
					// only if the respective parent space is empty
					if ( parents.get(g.members[friend.fnum]) == null ) {
						 parents.put( g.members[friend.fnum] , currentPerson);
				} }
				friend = friend.next;
	} } }
	
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		ArrayList<ArrayList<String>> cliques = new ArrayList<ArrayList<String>>();
		boolean[] visited = new boolean[g.members.length];
		boolean[] added = new boolean[g.members.length];
		//HashMap<Person,Person> parents = new HashMap<Person,Person>(g.members.length * 2);  //format of Person(friend)key and Person(parent)value
		Queue<Person> queue = new Queue<Person>();
		
		for (int i = 0; i< visited.length; i ++) {
			if (visited[i] == false && g.members[i].student == true && g.members[i].school.equals(school) ) {
				cliques.add(cBFS(g,i,visited, added, queue, school));
			}
		}
		
		
		return cliques;
		
	}
	
	private static ArrayList<String> cBFS(Graph g, int i, boolean[] visited, boolean[] added, Queue<Person> queue, String school) {
		ArrayList<String> clique = new ArrayList<String>();
		
		//enqueue starting point and marks visited of starting pt (i) 
		queue.enqueue( g.members[i] );
		visited[i] = true;
		
		//bfs code
		while( !queue.isEmpty() ) {
			Person currentPerson = queue.dequeue();
			visited[g.map.get(currentPerson.name)] = true;
			if ( g.members[g.map.get(currentPerson.name)].student  == true &&  g.members[g.map.get(currentPerson.name)].school.equals(school) && added[g.map.get(currentPerson.name)] == false) {	
					clique.add(g.members[g.map.get(currentPerson.name)].name);
					added[g.map.get(currentPerson.name)] = true;
			}
			
			Friend friend = g.members[g.map.get(currentPerson.name)].first;
			while( friend != null ) {
				if( visited[friend.fnum] == false ) {
					visited[friend.fnum] = true;
					// enqueue friend from dequeued current person
					if ( g.members[friend.fnum].student  == true &&  g.members[friend.fnum].school.equals(school) && added[friend.fnum] == false) {	
						clique.add(g.members[friend.fnum].name);
						added[friend.fnum] = true;
						queue.enqueue( g.members[friend.fnum] );
					}
				}
				friend = friend.next;
				
			} 
		} 
		return clique;
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		ArrayList<String> connectors = new ArrayList<>();
		boolean[] visited = new boolean[g.members.length];
		int[] dfsN = new int[g.members.length];
		int[] x = new int[g.members.length];
		boolean[] xx = new boolean[g.members.length];

		for( Person p : g.members ) {
			int pInt = g.map.get( p.name );
			if( visited[pInt] )
				continue;

			cDFS( g, visited, connectors, p, dfsN, x, xx, pInt, pInt );
		}

		return connectors;
		
	}
	

	private static void cDFS( Graph g, boolean[] visited, ArrayList<String> connectors, Person person, int[] dfsN, int[] x, boolean[] xx, int before, int first ) {
		
		int personNum = g.map.get(person.name);
		
		if( visited[personNum] ) {
			return;
		}
			
		visited[personNum] = true;
		dfsN[personNum] = dfsN[before] + 1;
		x[personNum] = dfsN[personNum];

		for( Friend currentfriend = person.first; currentfriend  != null; currentfriend  = currentfriend .next ) {
			if( visited[currentfriend .fnum] ) {
				x[personNum] = Math.min( x[personNum], dfsN[currentfriend .fnum] );
			}else {
				cDFS(g, visited, connectors, g.members[currentfriend .fnum], dfsN,x, xx, personNum, first );

				if( !connectors.contains(person.name) && dfsN[personNum] <= x[currentfriend .fnum] ) {
					if( personNum != first || xx[personNum] )
						connectors.add( person.name );
				}

				if( dfsN[personNum] > x[currentfriend .fnum] ) {
					
					x[personNum] = Math.min( x[personNum], x[currentfriend .fnum] );
///////////
				}
				xx[personNum] = true;
			}
		}
	}

}

