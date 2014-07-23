import java.io.Serializable;


public class Owner implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String keyWord;
	String FileName;
	String ownerID;
	String msgUID;
	
	public Owner(String key, String fName, String oID, String mUID)
	{
		this.keyWord = key;
		this.FileName = fName;
		this.ownerID = oID;
		this.msgUID = mUID;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public String getMsgUID() {
		return msgUID;
	}

	public void setMsgUID(String msgUID) {
		this.msgUID = msgUID;
	}
}
