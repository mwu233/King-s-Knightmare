///////////////////////////////////////////////////////////////////////////////
// Title:           HW1 - Problem3
// File:            KingsKnightmare.java
// Semester:        Spring 2018
//
// Author:          Meiliu Wu
// Email:			mwu233@wisc.edu
// Lab Section:     CS540 - 002
///////////////////////////////////////////////////////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author Meiliu Wu 
 * HW1 for CS540 section 2
 */
/**
 * Data structure to store each node.
 */
class Location {
	private int x;
	private int y;
	private Location parent;

	public Location(int x, int y, Location parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Location getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Location) {
			Location loc = (Location) obj;
			return loc.x == x && loc.y == y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * (hash + x);
		hash = 31 * (hash + y);
		return hash;
	}
}

public class KingsKnightmare {
	//represents the map/board
	private static boolean[][] board;
	//represents the goal node
	private static Location king;
	//represents the start node
	private static Location knight;
	//y dimension of board
	private static int n;
	//x dimension of the board
	private static int m;
	//enum defining different algo types
	enum SearchAlgo{
		BFS, DFS, ASTAR;
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			//loads the input file and populates the data variables
			SearchAlgo algo = loadFile(args[0]);
			if (algo != null) {
				switch (algo) {
					case DFS :
						executeDFS();
						break;
					case BFS :
						executeBFS();
						break;
					case ASTAR :
						executeAStar();
						break;
					default :
						break;
				}
			}
		}
	}

	/**
	 * Implementation of Astar algorithm for the problem
	 */
	private static void executeAStar() {
		//TODO: Implement A* algorithm in this method
		
		// AStar uses PriorityQueue to implement Frontier
		PriorityQ<Location> frontier = new PriorityQ<Location>();
		boolean[][] explored = new boolean[n][m];
		
		// f = g + h
		// g = min cost from start to node
		// h = |x – x_goal| + |y – y_goal|
		frontier.add(knight, Math.abs(knight.getX() - king.getX()) + Math.abs(knight.getY() - king.getY()));
		
		boolean found = false;
		Location goalNode = new Location(-1,-1,null);
		
		while (found != true) {
			if (frontier.isEmpty()) {
				break;
			} 

			Location curr = frontier.peek().getKey();
			
			// goal test
			if (curr.equals(king)) {
				found = true;
				goalNode = curr;
				break;
			}

			int h_curr = Math.abs(curr.getX() - king.getX()) + Math.abs(curr.getY() - king.getY());
			int g_suc = frontier.getPriorityScore(curr) - h_curr + 3;
			
			// current node's 8 possible next steps (successors)
			Location suc1 = new Location(curr.getX() + 2, curr.getY() + 1, curr);
			Location suc2 = new Location(curr.getX() + 1, curr.getY() + 2, curr);
			Location suc3 = new Location(curr.getX() - 1, curr.getY() + 2, curr);
			Location suc4 = new Location(curr.getX() - 2, curr.getY() + 1, curr);
			Location suc5 = new Location(curr.getX() - 2, curr.getY() - 1, curr);
			Location suc6 = new Location(curr.getX() - 1, curr.getY() - 2, curr);
			Location suc7 = new Location(curr.getX() + 1, curr.getY() - 2, curr);
			Location suc8 = new Location(curr.getX() + 2, curr.getY() - 1, curr);
			
			// make sure the newNode:
			// 1) is within the range of board;
			// 2) is not an obstacle;
			// 3) is not explored or frontier.

			// 1st successor (+2,+1)
			if (suc1.getX() < m && suc1.getY() < n) {
				int h = Math.abs(suc1.getX() - king.getX()) + Math.abs(suc1.getY() - king.getY());
				if (board[suc1.getY()][suc1.getX()] != true && explored[suc1.getY()][suc1.getX()] != true && !frontier.exists(suc1)) {
					frontier.add(suc1,h+g_suc);
				}
				else if (!board[suc1.getY()][suc1.getX()] && frontier.exists(suc1) && frontier.getPriorityScore(suc1) > h + g_suc) {
					// if the old one in frontier with higher PATH-COST, then replace it with this new one
						frontier.modifyEntry(suc1, h + g_suc);
				}
				
			}
			// 2nd successor (+1,+2)
			if (suc2.getX() < m && suc2.getY() < n) {
				int h = Math.abs(suc2.getX() - king.getX()) + Math.abs(suc2.getY() - king.getY());
				if (board[suc2.getY()][suc2.getX()] != true && explored[suc2.getY()][suc2.getX()] != true && !frontier.exists(suc2)) {
					frontier.add(suc2,h+g_suc);
				}
				else if (!board[suc2.getY()][suc2.getX()] && frontier.exists(suc2) && frontier.getPriorityScore(suc2) > h + g_suc) {
					// if the old one in frontier with higher PATH-COST, then replace it with this new one
					if (frontier.getPriorityScore(suc2) > h + g_suc) {
						frontier.modifyEntry(suc2, h + g_suc);
					}
				}
			}
			// 3rd successor (-1,+2)
			if (suc3.getX() >= 0 && suc3.getY() < n) {
				int h = Math.abs(suc3.getX() - king.getX()) + Math.abs(suc3.getY() - king.getY());
				if (board[suc3.getY()][suc3.getX()] != true && explored[suc3.getY()][suc3.getX()] != true && !frontier.exists(suc3)) {
					
					frontier.add(suc3,h+g_suc);
				}
				else if (!board[suc3.getY()][suc3.getX()] && frontier.exists(suc3) && frontier.getPriorityScore(suc3) > h + g_suc) {
					// if the old one in frontier with higher PATH-COST, then replace it with this new one

						frontier.modifyEntry(suc3, h + g_suc);
					
				}
			}
			// 4th successor (-2,+1)
			if (suc4.getX() >= 0 && suc4.getY() < n) {
				int h = Math.abs(suc4.getX() - king.getX()) + Math.abs(suc4.getY() - king.getY());
				if (board[suc4.getY()][suc4.getX()] != true && explored[suc4.getY()][suc4.getX()] != true && !frontier.exists(suc4)) {
					
					frontier.add(suc4,h+g_suc);
				}
				else if (!board[suc4.getY()][suc4.getX()] && frontier.exists(suc4) && frontier.getPriorityScore(suc4) > h + g_suc) {
					// if the old one in frontier with higher PATH-COST, then replace it with this new one
					
						frontier.modifyEntry(suc4, h + g_suc);
					
				}
			}
			// 5th successor (-2,-1)
			if (suc5.getX() >= 0 && suc5.getY() >= 0) {
				int h = Math.abs(suc5.getX() - king.getX()) + Math.abs(suc5.getY() - king.getY());
				if (board[suc5.getY()][suc5.getX()] != true && explored[suc5.getY()][suc5.getX()] != true && !frontier.exists(suc5)) {
					
					frontier.add(suc5,h+g_suc);
				}
				else if (!board[suc5.getY()][suc5.getX()] && frontier.exists(suc5) && frontier.getPriorityScore(suc5) > h + g_suc) {
					// if the old one in frontier with higher PATH-COST, then replace it with this new one
					
			
						frontier.modifyEntry(suc5, h + g_suc);
					
				}
			}
			// 6th successor (-1,-2)
			if (suc6.getX() >= 0 && suc6.getY() >= 0) {
				int h = Math.abs(suc6.getX() - king.getX()) + Math.abs(suc6.getY() - king.getY());
				if (board[suc6.getY()][suc6.getX()] != true && explored[suc6.getY()][suc6.getX()] != true && !frontier.exists(suc6)) {
					
					frontier.add(suc6,h+g_suc);
				}
				else if (!board[suc6.getY()][suc6.getX()] && frontier.exists(suc6) && frontier.getPriorityScore(suc6) > h + g_suc) {
					// if the old one in frontier with higher PATH-COST, then replace it with this new one
					
				
						frontier.modifyEntry(suc6, h + g_suc);
		
				}
			}
			// 7th successor (+1,-2)
			if (suc7.getX() < m && suc7.getY() >= 0) {
				int h = Math.abs(suc7.getX() - king.getX()) + Math.abs(suc7.getY() - king.getY());
				if (board[suc7.getY()][suc7.getX()] != true && explored[suc7.getY()][suc7.getX()] != true && !frontier.exists(suc7)) {
					
					frontier.add(suc7,h+g_suc);
				}
				else if (!board[suc7.getY()][suc7.getX()] && frontier.exists(suc7) && frontier.getPriorityScore(suc7) > h + g_suc) {
					// if the old one in frontier with higher PATH-COST, then replace it with this new one
					
					
						frontier.modifyEntry(suc7, h + g_suc);
					
				}
			}
			// 8th successor (+2,-1)
			if (suc8.getX() < m && suc8.getY() >= 0) {
				int h = Math.abs(suc8.getX() - king.getX()) + Math.abs(suc8.getY() - king.getY());
				if (board[suc8.getY()][suc8.getX()] != true && explored[suc8.getY()][suc8.getX()] != true && !frontier.exists(suc8)) {
					
					frontier.add(suc8,h+g_suc);
				}
				else if (!board[suc8.getY()][suc8.getX()] && frontier.exists(suc8) && frontier.getPriorityScore(suc8) > h + g_suc) {
					// if the old one in frontier with higher PATH-COST, then replace it with this new one
					
					
						frontier.modifyEntry(suc8, h + g_suc);
					
				}
			}
			
			frontier.remove(curr);
			explored[curr.getY()][curr.getX()] = true;

		}

		// write the output file
		try {
			File file = new File("output_astar.txt");
			FileWriter fw = new FileWriter(file);

			if (found == true) {
				Location pathNode = goalNode;
				Stack<Location> tmpS = new Stack<Location>();
				tmpS.push(pathNode);

				while (pathNode.getParent() != null) {
					tmpS.push(pathNode.getParent());
					pathNode = pathNode.getParent();
				}

				while (!tmpS.isEmpty()) {
					Location tmp = tmpS.pop();
					fw.write(tmp.toString() + "\n");
				}

			} 
			else {
				fw.write("NOT REACHABLE\n");
			}

			int expandedNodes = 0;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					if (explored[i][j]) {
						expandedNodes++;
					}
				}
			}
			fw.write("Expanded Nodes: " + expandedNodes);
			fw.close();
			
//			int frontier_size = frontier.size();
//			while((!frontier.isEmpty())) {
//				Location tmp = frontier.poll().getKey();
//				System.out.println(tmp.toString());
//			}
//			System.out.println(frontier_size + "");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Implementation of BFS algorithm
	 */
	private static void executeBFS() {
		// Implement bfs algorithm in this method
		
		// BFS uses Queue to implement Frontier
		Queue<Location> frontier = new LinkedList<Location>();
		boolean[][] explored = new boolean[n][m];
		
		frontier.add(knight);
		
		boolean found = false;
		Location goalNode = new Location(-1,-1,null);
		
		while (found != true) {
			
			if (frontier.isEmpty()) {
				break;
			}
			Location curr = frontier.remove(); //LIFO
			
			// n is number of rows, m is number of columns
			explored[curr.getY()][curr.getX()] = true;
			
			// current node's 8 possible next steps (successors)
			Location suc1 = new Location(curr.getX() + 2, curr.getY() + 1, curr);
			Location suc2 = new Location(curr.getX() + 1, curr.getY() + 2, curr);
			Location suc3 = new Location(curr.getX() - 1, curr.getY() + 2, curr);
			Location suc4 = new Location(curr.getX() - 2, curr.getY() + 1, curr);
			Location suc5 = new Location(curr.getX() - 2, curr.getY() - 1, curr);
			Location suc6 = new Location(curr.getX() - 1, curr.getY() - 2, curr);
			Location suc7 = new Location(curr.getX() + 1, curr.getY() - 2, curr);
			Location suc8 = new Location(curr.getX() + 2, curr.getY() - 1, curr);
			
			// make sure the newNode:
			// 1) is within the range of board;
			// 2) is not an obstacle;
			// 3) is not explored or frontier
			
			// 1st successor (+2,+1)
			if (suc1.getX() < m && suc1.getY() < n) {
				if (board[suc1.getY()][suc1.getX()] != true && explored[suc1.getY()][suc1.getX()] != true && !frontier.contains(suc1)) {
					if (suc1.equals(king)) {
						found = true;
						goalNode = suc1;
						break;
					}
					frontier.add(suc1);
				}
			}
			// 2nd successor (+1,+2)
			if (suc2.getX() < m && suc2.getY() < n) {
				if (board[suc2.getY()][suc2.getX()] != true && explored[suc2.getY()][suc2.getX()] != true && !frontier.contains(suc2)) {
					if (suc2.equals(king)) {
						found = true;
						goalNode = suc2;
						break;
					}
					frontier.add(suc2);
				}
			}
			// 3rd successor (-1,+2)
			if (suc3.getX() >= 0 && suc3.getY() < n) {
				if (board[suc3.getY()][suc3.getX()] != true && explored[suc3.getY()][suc3.getX()] != true && !frontier.contains(suc3)) {
					if (suc3.equals(king)) {
						found = true;
						goalNode = suc3;
						break;
					}
					frontier.add(suc3);
				}
			}
			// 4th successor (-2,+1)
			if (suc4.getX() >= 0 && suc4.getY() < n) {
				if (board[suc4.getY()][suc4.getX()] != true && explored[suc4.getY()][suc4.getX()] != true && !frontier.contains(suc4)) {
					if (suc4.equals(king)) {
						found = true;
						goalNode = suc4;
						break;
					}
					frontier.add(suc4);
				}
			}
			// 5th successor (-2,-1)
			if (suc5.getX() >= 0 && suc5.getY() >= 0) {
				if (board[suc5.getY()][suc5.getX()] != true && explored[suc5.getY()][suc5.getX()] != true && !frontier.contains(suc5)) {
					if (suc5.equals(king)) {
						found = true;
						goalNode = suc5;
						break;
					}
					frontier.add(suc5);
				}
			}
			// 6th successor (-1,-2)
			if (suc6.getX() >= 0 && suc6.getY() >= 0) {
				if (board[suc6.getY()][suc6.getX()] != true && explored[suc6.getY()][suc6.getX()] != true && !frontier.contains(suc6)) {
					if (suc6.equals(king)) {
						found = true;
						goalNode = suc6;
						break;
					}
					frontier.add(suc6);
				}
			}
			// 7th successor (+1,-2)
			if (suc7.getX() < m && suc7.getY() >= 0) {
				if (board[suc7.getY()][suc7.getX()] != true && explored[suc7.getY()][suc7.getX()] != true && !frontier.contains(suc7)) {
					if (suc7.equals(king)) {
						found = true;
						goalNode = suc7;
						break;
					}
					frontier.add(suc7);
				}
			}
			// 8th successor (+2,-1)
			if (suc8.getX() < m && suc8.getY() >= 0) {
				if (board[suc8.getY()][suc8.getX()] != true && explored[suc8.getY()][suc8.getX()] != true && !frontier.contains(suc8)) {
					if (suc8.equals(king)) {
						found = true;
						goalNode = suc8;
						break;
					}
					frontier.add(suc8);
				}
			}

		}

		// write the output file
		try {
			File file = new File("output_bfs.txt");
			FileWriter fw = new FileWriter(file);
			
			
			if (found == true) {
				Location pathNode = goalNode;
				Stack<Location> tmpS = new Stack<Location>();
				tmpS.push(pathNode);
				
				while (pathNode.getParent() != null) {
					tmpS.push(pathNode.getParent());
					pathNode = pathNode.getParent();
				}
				
				while (!tmpS.isEmpty()) {
					Location tmp = tmpS.pop();
					fw.write(tmp.toString() + "\n");
				}

			}
			else {
				fw.write("NOT REACHABLE\n");
			}
			
			int expandedNodes = 0;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					if (explored[i][j]) {
						expandedNodes++;
					}
				}
			}
			fw.write("Expanded Nodes: " + expandedNodes);
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Implementation of DFS algorithm
	 */
	private static void executeDFS() {
		// Implement dfs algorithm in this method
		
		// DFS uses Stack to implement Frontier
		Stack<Location> frontier = new Stack<Location>();
		boolean[][] explored = new boolean[n][m];
		
		frontier.add(knight);
		
		boolean found = false;
		Location goalNode = new Location(-1,-1,null);
		
		while (found != true) {
			
			if (frontier.isEmpty()) {
				break;
			}
			Location curr = frontier.pop(); //LIFO
			
			// n is number of rows, m is number of columns
			explored[curr.getY()][curr.getX()] = true;
			
			// current node's 8 possible next steps (successors)
			Location suc1 = new Location(curr.getX() + 2, curr.getY() + 1, curr);
			Location suc2 = new Location(curr.getX() + 1, curr.getY() + 2, curr);
			Location suc3 = new Location(curr.getX() - 1, curr.getY() + 2, curr);
			Location suc4 = new Location(curr.getX() - 2, curr.getY() + 1, curr);
			Location suc5 = new Location(curr.getX() - 2, curr.getY() - 1, curr);
			Location suc6 = new Location(curr.getX() - 1, curr.getY() - 2, curr);
			Location suc7 = new Location(curr.getX() + 1, curr.getY() - 2, curr);
			Location suc8 = new Location(curr.getX() + 2, curr.getY() - 1, curr);
			
			// make sure the newNode:
			// 1) is within the range of board;
			// 2) is not an obstacle;
			// 3) is not explored or frontier
			
			// 1st successor (+2,+1)
			if (suc1.getX() < m && suc1.getY() < n) {
				if (!board[suc1.getY()][suc1.getX()] && !explored[suc1.getY()][suc1.getX()] && !frontier.contains(suc1)) {
					if (suc1.equals(king)) {
						found = true;
						goalNode = suc1;
						break;
					}
					frontier.add(suc1);
				}
			}
			// 2nd successor (+1,+2)
			if (suc2.getX() < m && suc2.getY() < n) {
				if (!board[suc2.getY()][suc2.getX()] && !explored[suc2.getY()][suc2.getX()] && !frontier.contains(suc2)) {
					if (suc2.equals(king)) {
						found = true;
						goalNode = suc2;
						break;
					}
					frontier.add(suc2);
				}
			}
			// 3rd successor (-1,+2)
			if (suc3.getX() >= 0 && suc3.getY() < n) {
				if (!board[suc3.getY()][suc3.getX()] && !explored[suc3.getY()][suc3.getX()] && !frontier.contains(suc3)) {
					if (suc3.equals(king)) {
						found = true;
						goalNode = suc3;
						break;
					}
					frontier.add(suc3);
				}
			}
			// 4th successor (-2,+1)
			if (suc4.getX() >= 0 && suc4.getY() < n) {
				if (!board[suc4.getY()][suc4.getX()] && !explored[suc4.getY()][suc4.getX()] && !frontier.contains(suc4)) {
					if (suc4.equals(king)) {
						found = true;
						goalNode = suc4;
						break;
					}
					frontier.add(suc4);
				}
			}
			// 5th successor (-2,-1)
			if (suc5.getX() >= 0 && suc5.getY() >= 0) {
				if (!board[suc5.getY()][suc5.getX()] && !explored[suc5.getY()][suc5.getX()] && !frontier.contains(suc5)) {
					if (suc5.equals(king)) {
						found = true;
						goalNode = suc5;
						break;
					}
					frontier.add(suc5);
				}
			}
			// 6th successor (-1,-2)
			if (suc6.getX() >= 0 && suc6.getY() >= 0) {
				if (!board[suc6.getY()][suc6.getX()] && !explored[suc6.getY()][suc6.getX()] && !frontier.contains(suc6)) {
					if (suc6.equals(king)) {
						found = true;
						goalNode = suc6;
						break;
					}
					frontier.add(suc6);
				}
			}
			// 7th successor (+1,-2)
			if (suc7.getX() < m && suc7.getY() >= 0) {
				if (!board[suc7.getY()][suc7.getX()] && !explored[suc7.getY()][suc7.getX()] && !frontier.contains(suc7)) {
					if (suc7.equals(king)) {
						found = true;
						goalNode = suc7;
						break;
					}
					frontier.add(suc7);
				}
			}
			// 8th successor (+2,-1)
			if (suc8.getX() < m && suc8.getY() >= 0) {
				if (!board[suc8.getY()][suc8.getX()] && !explored[suc8.getY()][suc8.getX()] && !frontier.contains(suc8)) {
					if (suc8.equals(king)) {
						found = true;
						goalNode = suc8;
						break;
					}
					frontier.add(suc8);
				}
			}

		}

		// write the output file
		try {
			File file = new File("output_dfs.txt");
			FileWriter fw = new FileWriter(file);
			
			if (found == true) {
				Location pathNode = goalNode;
				Stack<Location> tmpS = new Stack<Location>();
				tmpS.push(pathNode);
				
				while (pathNode.getParent() != null) {
					tmpS.push(pathNode.getParent());
					pathNode = pathNode.getParent();
				}
				
				while (!tmpS.isEmpty()) {
					Location tmp = tmpS.pop();
					fw.write(tmp.toString() + "\n");
				}

			}
			else {
				fw.write("NOT REACHABLE\n");
			}
			
			int expandedNodes = 0;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					if (explored[i][j]) {
						expandedNodes++;
					}
				}
			}
			fw.write("Expanded Nodes: " + expandedNodes);
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param filename
	 * @return Algo type
	 * This method reads the input file and populates all the 
	 * data variables for further processing
	 */
	private static SearchAlgo loadFile(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			SearchAlgo algo = SearchAlgo.valueOf(sc.nextLine().trim().toUpperCase());
			n = sc.nextInt();
			m = sc.nextInt();
			sc.nextLine();
			board = new boolean[n][m];
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					if (line.charAt(j) == '1') {
						board[i][j] = true;
					} else if (line.charAt(j) == 'S') {
						knight = new Location(j, i, null);
					} else if (line.charAt(j) == 'G') {
						king = new Location(j, i, null);
					}
				}
			}
			sc.close();
			return algo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
