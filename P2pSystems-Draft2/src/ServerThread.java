import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class ServerThread  extends Thread {
    final int JOIN =1;
	final int SEARCH =2;
	final int REPLY =3;
	final int TERMINATE = 4;
	final int TERMINATEREPLY = 5;
	final int DONE = 6;
	
	ServerSocket serverSocket;
    
    boolean serverRunning = true;
 
    Random randomGenerator = new Random();

    public ServerThread() throws IOException
    {
    	serverSocket = new ServerSocket(DataStore.getInstance().portNumber);
    }
	public void run()
	{
		System.out.println("Server thread started.");
		while(serverRunning)
        {
        	Socket clientSocket;
        	try {
					clientSocket = serverSocket.accept();
					ObjectInputStream ObjectIn = null;
					ObjectIn = new ObjectInputStream(clientSocket.getInputStream());
                    Message msgObjectMessage;
                    msgObjectMessage = (Message) ObjectIn.readObject();
                    System.out.println("message received:" + msgObjectMessage.getMessage());
                    if( msgObjectMessage != null)
                    	{
                			int type = msgObjectMessage.getType();
                 			switch(type)
                			{
                			case JOIN:  handleJoin(msgObjectMessage);
                						break;
    								
                			case SEARCH: handleSearch(msgObjectMessage);
                						break;
    								
                			case REPLY: handleReply(msgObjectMessage);
                						break;
    								
                			case TERMINATE:
                						break;
                								
                			case TERMINATEREPLY: 
                						break;
                			case DONE:
                						break;
                			}
                    	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
        }
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void handleJoin(Message msgObjectMessage) {
		// TODO Auto-generated method stub
		// Adding the sender as a neighbor. 
		System.out.println("JOIN message received...");
		DataStore.getInstance().neighbors.add(msgObjectMessage.getSender());
		try {
			Socket echoSocket = new Socket(msgObjectMessage.getSender(), DataStore.getInstance().portNumber);
			DataStore.getInstance().neighborSockets.put(msgObjectMessage.getSender(), echoSocket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldnot create a socket for....");
		}
		
	}
	private void handleSearch(Message msgObjectMessage) {
		// TODO Auto-generated method stub
		MessageSender ms = new MessageSender();
		if(!DataStore.getInstance().msgStateInfo.containsKey(msgObjectMessage.getUid()))
		{
			System.out.println("New search message received. " + msgObjectMessage.getUid());
			System.out.println("Sender:" + msgObjectMessage.getSender() + "\t message:"+ msgObjectMessage.getMessage());
			DataStore.getInstance().msgStateInfo.put(msgObjectMessage.getUid(), msgObjectMessage.getSender());
	
			// Search your resource file and send reply or fwd the message accordingly..	
			String fileNameFound = null;
			try { 
				fileNameFound = searchKeyworkinResourceFile(msgObjectMessage.getMessage());
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				System.out.println("Resource file not found. ");
			}
			
			if(fileNameFound != null)
			{
				System.out.println("File Found in my repository.");
				// send FOUND reply message..
											
				Owner own = new Owner(msgObjectMessage.getMessage(), fileNameFound, DataStore.getInstance().myId, msgObjectMessage.getUid());
				//								String msg, String sender, int ty, String uid, String originator, int hopcount, Owner owner
				Message replyMessage = new Message("FOUND",DataStore.getInstance().myId,REPLY, msgObjectMessage.getUid(),msgObjectMessage.getOriginator(),msgObjectMessage.getHopCount(),own);
				try {
					ms.sendMessage(replyMessage, DataStore.getInstance().msgStateInfo.get(msgObjectMessage.getUid()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("File found... but failed to send REPLY Message to the parent.. ");
					e.printStackTrace();
				}
				
			}
			else if(msgObjectMessage.getHopCount()-1 > 0)
			{
				System.out.println("keyword or filename not found in my resourceFile.txt and hopcount is not zero");
				// send search message to your neighbors..
				// Search query, sender, "SEARCH", uid, Originator, hopcount
				Message msgToFwd = new Message(msgObjectMessage.getMessage(),DataStore.getInstance().myId, msgObjectMessage.getType(),msgObjectMessage.getUid(),msgObjectMessage.getOriginator(),msgObjectMessage.getHopCount()-1);
				ArrayList<String> neigh = DataStore.getInstance().getNeighbors();
				DataStore.getInstance().replyCounter.put(msgToFwd.getUid(), neigh.size()-1);
				for(String n:neigh)
				{
					if(!msgObjectMessage.getSender().equals(n))
					{
						try {
							ms.sendMessage(msgToFwd, n);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							System.out.println("Error fwding search message - unknown host..");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("Error fwding search message - IOE..");
						}
					}
				}
				if( neigh.size() == 1 && neigh.get(0).equals(msgObjectMessage.getSender()))
				{
					System.out.println("sending NOT FOUND if your parent/sender is your only neighbor...");
					Message replyMessage = new Message("NOT FOUND",DataStore.getInstance().myId,REPLY, msgObjectMessage.getUid(),msgObjectMessage.getOriginator(),msgObjectMessage.getHopCount(),null);
					try {
						ms.sendMessage(replyMessage, DataStore.getInstance().msgStateInfo.get(msgObjectMessage.getUid()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("File NOT found... but failed to send REPLY Message to the parent.. ");
						e.printStackTrace();
					}					
				}
				
			}
			else
			{
				System.out.println("File or keyword not found in my resourceFile.txt and hopcount is 0.. sneding notfound reply to the parent");
				Message replyMessage = new Message("NOT FOUND",DataStore.getInstance().myId,REPLY, msgObjectMessage.getUid(),msgObjectMessage.getOriginator(),msgObjectMessage.getHopCount(),null);
				try {
					ms.sendMessage(replyMessage, DataStore.getInstance().msgStateInfo.get(msgObjectMessage.getUid()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("File NOT found... but failed to send REPLY Message to the parent.. ");
					e.printStackTrace();
				}
			}
		}
		else
		{
			// Send a reply with a message already received.. 
			
			Message replyMessage = new Message("Already processed",DataStore.getInstance().myId,REPLY, msgObjectMessage.getUid(),msgObjectMessage.getOriginator(),msgObjectMessage.getHopCount(),null);
			try {
				ms.sendMessage(replyMessage, DataStore.getInstance().msgStateInfo.get(msgObjectMessage.getUid()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Search already received.. but failed to send REPLY Message to the parent.. ");
				e.printStackTrace();
			}
		}
	}
	private String searchKeyworkinResourceFile(String message) throws FileNotFoundException {
		// TODO Auto-generated method stub
		String path = DataStore.getInstance().myId.substring(0,5).concat("/").concat(DataStore.getInstance().resFileName);
		System.out.println("Path to the resource file is.. " + path);
		Scanner s = new Scanner(new File(path));
        while(s.hasNextLine())
        {
        	String nxtLine = s.nextLine().toLowerCase();
        	
        	if(nxtLine.contains(message.toLowerCase()))
        	{
        		s.close();
        		return nxtLine.subSequence(0, nxtLine.indexOf(" ")).toString();
        	}
        }
        s.close();
		return null;
	}
	private void handleReply(Message msgObjectMessage)  {
		// TODO Auto-generated method stub
		DataStore.getInstance().replyCounter.put(msgObjectMessage.getUid(), DataStore.getInstance().replyCounter.get(msgObjectMessage.getUid())-1);
		
		System.out.println("Reply message received for: " + msgObjectMessage.getUid());
		System.out.println("Sender:" + msgObjectMessage.getSender() + "\t message:"+ msgObjectMessage.getMessage());
		
		
		// Check if current node is the Originator of the message
		if (msgObjectMessage.getOriginator().equals(DataStore.getInstance().myId))
		{
			System.out.println(msgObjectMessage.getMessage());
			if(msgObjectMessage.getMessage().equals("FOUND"))
			{
				// set the file owner to the node that possesses the file.
				if (DataStore.getInstance().fileOwners.containsKey(msgObjectMessage.getUid()))
				{
					DataStore.getInstance().fileOwners.get(msgObjectMessage.getUid()).add(msgObjectMessage.getFileOwner());
				}
				else
				{
					System.out.println("Adding msg UID to the fileOwners data object..");
					ArrayList<Owner> owner = new ArrayList<Owner>();
					owner.add(msgObjectMessage.getFileOwner());
					DataStore.getInstance().fileOwners.put(msgObjectMessage.getUid(), owner);
					System.out.println("Adding done.");
				}
			}
		}
		else 
		{

			// Send reply to the host (previous sender for the corresponding REQUEST msg) which is found in hashmap msgStateInfo
			
			// Call sendReply function from MessageSender.java which should be like
			//String msg, String sender, int ty, String uid, String originator, int hopcount, Owner owner
			Message msgToSend = new Message(msgObjectMessage.getMessage(),DataStore.getInstance().myId,REPLY,msgObjectMessage.getUid(),msgObjectMessage.getOriginator(),msgObjectMessage.getHopCount(), msgObjectMessage.getFileOwner());
			MessageSender ms = new MessageSender();
			try {
				ms.sendMessage(msgToSend, DataStore.getInstance().msgStateInfo.get(msgToSend.getUid()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Fwding the reply.. but failed.. ");
			}
			
		}
	}
    public void terminate()
    {
    	this.serverRunning = false;
    	/*Set<String> keySet = DataStore.getInstance().neighborSockets.keySet();
    	
    	for(String key: keySet)
    	{
    		try {
				DataStore.getInstance().neighborSockets.get(key).close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Couldn't close the socket for node: " + key);
			}
    	}*/
    	System.exit(0);
    }
}
