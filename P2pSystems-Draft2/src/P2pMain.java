import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * @author Manikanta Talanki
 * @category AOS Lamport Mutex Proj
 * @version 3.2
 *
 */
public class P2pMain {
	
	static String myId;
	//static ArrayList<String> neighbors = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ServerThread serverT;	
		serverT = new ServerThread();
		myId = java.net.InetAddress.getLocalHost().getHostName();
		DataStore.getInstance().myId = myId;
		boolean running = true;
		boolean joined = false;
		int userSelection=0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		serverT.start();
		while(running)
		{
			System.out.println("************** MENU ***************");
			System.out.println("Select the option: \n Ex: Enter 1 to Join");
			if(!joined)
			{
				System.out.println("1. Join");
				joined = true;
			}
			System.out.println("2. Search");
			System.out.println("3. Terminate");
			System.out.println("4. Print neighbors.");
			System.out.println("5. Download.");
			String line= br.readLine();
			
			while(line ==null )
				line = br.readLine();
			
			userSelection = Integer.parseInt(line); 
			
			switch(userSelection)
			{
			case 1: System.out.println("Joining the network....");
					// Joining the network
					// check not join here also.
					joinNetwork(); // Finish the code in thefunction.
					
					break;
			case 2: System.out.println("Enter a keywork or a  filename to search for...");
			 		String searchQuery = br.readLine();
				
					System.out.println("Searching for " + searchQuery  + " \n Please wait..");
					// Search for a file.
					searchFile(searchQuery);
					break;
			case 3: System.out.println("Terminating the connection.");
					// Initiate Termination.
					serverT.terminate();
					br.close();
					running = false;
					break;
			case 4: DataStore.getInstance().printNeighbors();
					break;
			case 5: downloadFiles();
					break;
			default: System.out.println("invalid input.. please try again..");
					break;
			}
			
		}
		
	}

	private static void downloadFiles() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		
		if(!DataStore.getInstance().fileOwners.isEmpty())
		{
			Object[] keys = DataStore.getInstance().fileOwners.keySet().toArray();
			int i = 1;
			System.out.println("Select the file to download.");
			for(Object k: keys)
			{
				for(int l =0; l< DataStore.getInstance().fileOwners.get(k).size(); l++ )
				{
					Owner o = DataStore.getInstance().fileOwners.get(k).get(l);
					System.out.println(i + ". " + k +" " + o.getFileName() + " " + o.getOwnerID());
					i++;
				}
			}
			/*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int userSelection = Integer.parseInt(br.readLine());*/
			
			//System.out.println("you want to download the file.." + DataStore.getInstance().fileOwners.get(keys[userSelection-1])));
			System.out.println("Please wait while we download your file.. ");			
		}
		else
		{
			System.out.println("You didnot search for any file or your search didnot yield a resukt.. \nselect search first.");
		}
	}

	private static void searchFile(String searchQuery) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		MessageSender ms = new MessageSender();
		ms.searchFile(searchQuery);
		
		
	}
	
	private static void joinNetwork() throws IOException {
		// TODO Auto-generated method stub
		File f = new File("peers.txt");
		if(!f.exists() ) { 
			f.createNewFile();
		}
			BufferedReader reader = new BufferedReader(new FileReader("peers.txt"));
			ArrayList<String> nodesOnTheNetwork = new ArrayList<String>();
			
			String line = null;
			line = reader.readLine();
			if(line == null)
				System.out.println("I am the first node to join the network");
			else {
				while (line != null) {
					nodesOnTheNetwork.add(line);
					line = reader.readLine();
				}
				System.out.println("no of nodes on the network:" + nodesOnTheNetwork.size());
				Random r = new Random();
				int neighbor = r.nextInt(nodesOnTheNetwork.size());
				DataStore.getInstance().neighbors.add(nodesOnTheNetwork.get(neighbor));
				
				
				// send neighbor req to nodesOnTheNetwork(neighbor). 
				// Populate a neighbor request message and send to the selected node. 
				
				MessageSender ms = new MessageSender();
				ms.sendJoin(myId, nodesOnTheNetwork.get(neighbor).trim());				
				
				
				System.out.println("Joining the network as " + nodesOnTheNetwork.get(neighbor) +"'s Neighbour.");
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("peers.txt", true)));
			out.println(myId);
			out.close();
			reader.close();	
	}
}
