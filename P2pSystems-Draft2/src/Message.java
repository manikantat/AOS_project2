import java.io.Serializable;

public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String messageString;
	private String Sender;
	private int type;
	private String Uid;
	private String Originator;
	private int hopcount;
	private Owner fileOwner;
	
	public Message(String msg, String sender, int ty, String uid, String originator)
	{
		this.messageString = msg;
		this.Sender = sender;
		this.type = ty;
		this.Uid = uid;
		this.Originator = originator;
	}
	public Message(String msg, String sender, int ty, String uid, String originator, int hopcount)
	{
		this.messageString = msg;
		this.Sender = sender;
		this.type = ty;
		this.Uid = uid;
		this.Originator = originator;
		this.hopcount = hopcount;
	}
	public Message(String msg, String sender, int ty, String uid, String originator, int hopcount, Owner owner)
	{
		this.messageString = msg;
		this.Sender = sender;
		this.type = ty;
		this.Uid = uid;
		this.Originator = originator;
		this.hopcount = hopcount;
		this.fileOwner = owner;
	}
	public Message(String msg, String sender, int ty, String uid) {
		// TODO Auto-generated constructor stub
		this.messageString = msg;
		this.Sender = sender;
		this.type = ty;
		this.Uid = uid;
	}
	public Message(String msgString, String myId2, int ty) {
		// TODO Auto-generated constructor stub
		this.messageString = msgString;
		this.Sender = myId2;
		this.type = ty;
	}
	public String getMessage()
	{
		return messageString;
	}
	public String getSender()
	{
		return Sender;
	}
	public int getType()
	{
		return type;
	}
	public String getOriginator()
	{
		return Originator;
	}
	public String getUid()
	{
		return Uid;
	}
	public int getHopCount()
	{
		return hopcount;
	}
	public Owner getFileOwner()
	{
		return fileOwner;
	}
	public void setHopCount(int x)
	{
		this.hopcount = x;
	}
	public void setSender(String send)
	{
		this.Sender = send;
	}
}