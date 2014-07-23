import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class MessageSender {
	
	
	
	
	final int JOIN =1;
	final  int SEARCH =2;
	final  int REPLY =3;
	final  int TERMINATE = 4;
	final  int TERMINATEREPLY = 5;
	final  int DONE = 6;
	final int DOWNLOAD = 7;
    private String myId;

    protected void searchFile(String searchQuery) throws UnknownHostException, IOException
	   {
		   int multiFactor = 1;
		   int hopCount = 1;
		   myId = DataStore.getInstance().myId;
		  // DataStore.getInstance().searchCounter = 1;
		   // CHange the UID format to netXX + S + searchCounter + H + hopcount
		   String Uid = myId.substring(0,5).concat("S"+DataStore.getInstance().searchCounter).concat("H"+hopCount);
		    
		   DataStore.getInstance().searchCounter++;
									// Search query, sender, "SEARCH", uid, Originator, hopcount.
			Message msgToSend = new Message(searchQuery,myId,SEARCH,Uid,myId,hopCount);
			ArrayList<String> neighbors = DataStore.getInstance().getNeighbors();
			
			DataStore.getInstance().replyCounter.put(Uid, neighbors.size());
			
			while(true)
			{
				if(neighbors.size() == 0)
				{
					System.out.println("You dont have any neighbors... ");
					break;
				}
				// Sending messages to all the neighbors..
				for(String n:neighbors)
				{
					System.out.println("Sending search message to n= "+ n );
					sendMessage(msgToSend, n);
					DataStore.getInstance().msgTimeouts.put(Uid, false);
				}
				long startTime = System.currentTimeMillis();
	
				while (System.currentTimeMillis() < (startTime + hopCount*1000*multiFactor)) {
	
					// Check if you got all messages.. if so break the while loop.
				    if(DataStore.getInstance().replyCounter.get(Uid) == 0)
				    	break;
				    
				}
				if(DataStore.getInstance().replyCounter.get(Uid) != 0)
					DataStore.getInstance().msgTimeouts.put(Uid, true);
				
				boolean fileFound = false;
				// find a way to get the number of replies that you get.
				System.out.println("no of replies pending:"+ DataStore.getInstance().replyCounter.get(Uid));
				if(DataStore.getInstance().replyCounter.get(Uid) == 0)
				{
					// set file found to false or true based on the replies..
					if(DataStore.getInstance().fileOwners.containsKey(Uid))
					{
						System.out.println("File found!. ");
						fileFound = true;
					}
					else
						fileFound = false;
				}
				if (DataStore.getInstance().replyCounter.get(Uid) != 0 || !fileFound)
				{
					hopCount *= 2;
					System.out.println("Timed out or file not found.. Doubling the hopcount... "+hopCount);
					  // CHange the UID format to netXX + S + searchCounter + H + hopcount					
					Uid = myId.substring(0,5).concat("S"+DataStore.getInstance().searchCounter).concat("H"+hopCount);
					//DataStore.getInstance().searchCounter++;
					msgToSend = new Message(searchQuery,myId,SEARCH,Uid,myId,hopCount);
					DataStore.getInstance().replyCounter.put(Uid, neighbors.size());
				}
				if(fileFound)
				{
					//  Change.. 
					// establish a tcp connection with the node that posseses the file and download the file.

					System.out.println("File is found for KeyWord"+searchQuery+".. Press Download to view the results and Download the file.");
					break;
				}
				if(hopCount > 16)
				{
					System.out.println("hop count exceeded 16.. \n 404: File not found for KeyWord:" +searchQuery);
					break;
				}
			}
			
	   }
	protected void sendMessage(Message m, String toHost) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		//Socket echoSocket = new Socket(toHost, DataStore.getInstance().portNumber);
		ObjectOutputStream out = new ObjectOutputStream(DataStore.getInstance().neighborSockets.get(toHost).getOutputStream());
	//	ObjectOutputStream out = new ObjectOutputStream(echoSocket.getOutputStream());
		System.out.println("sending message to host:" +toHost);
		out.writeObject(m);
		System.out.println("Senttt..");
		//echoSocket.close();
	}

	void sendJoin(String myId2, String host) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		try {
			Socket echoSocket = new Socket(host, DataStore.getInstance().portNumber);
			DataStore.getInstance().neighborSockets.put(host, echoSocket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldnot create a socket for...." +host);
		}
		Message m = new Message("JOIN",myId2,JOIN);
		sendMessage(m, host);
	}
		
}
