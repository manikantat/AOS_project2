import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 */

/**
 * @author Manikanta
 *
 */
public class DataStore {

	private static DataStore instance = null;
	
	
	
	protected volatile HashMap<String, Integer> replyCounter = new HashMap<String, Integer>();
	protected volatile HashMap<String, ArrayList<Owner>> fileOwners  = new HashMap<String, ArrayList<Owner>>();
	protected volatile HashMap<String, String> msgStateInfo = new HashMap<String, String>();
	protected volatile HashMap<String, Socket> neighborSockets = new HashMap<String, Socket>();
	
	protected volatile HashMap<String, Boolean> msgTimeouts = new HashMap<String, Boolean>();
	
	protected volatile ArrayList<String> neighbors = new ArrayList<String>();
	
	protected String myId;
	
	protected int searchCounter = 1;
	
	protected String resFileName = "resourceFile.txt";
	
	final int portNumber = 9090;
	
	protected DataStore()
	{
		 // Exists only to defeat instantiation.
	}
	
	public static DataStore getInstance() {
	      if(instance == null) {
	         instance = new DataStore();
	      }
	      return instance;
	   }

	protected ArrayList<String> getNeighbors() {
		// TODO Auto-generated method stub
		return neighbors;
	}
	protected void printNeighbors()
	{
		System.out.println("These are my neighbors");
		for(String n: neighbors)
			System.out.print(n + "\t");
		System.out.println();
			
	}
	
}
