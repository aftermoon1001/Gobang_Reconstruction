package net;

import java.io.IOException;
/**
 *客户端类 
 */
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import chess.NameDialog;
import chess.Room;
import chess.RoomList;
import msg.BaseMsg;
/**
 * 客户端类
 * @author john
 *
 */
public class MyClient {
	
	private static MyClient myclient;
	private MyClient(){}
	private  NameDialog namedialog;
	private boolean connected=false;
	private Socket client=null;
	private Room room;
	
	
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	/**
	 * 单例获取MyClient对象
	 */
	public static  MyClient getMyClient(){
		if(myclient==null){		
			myclient= new MyClient();
		}
		return myclient;
	}
	public RoomList roomlist;
     public RoomList getRoomlist() {
		return roomlist;
	}
	public void setRoomlist(RoomList roomlist) {
		this.roomlist = roomlist;
	}
	public NameDialog getNamedialog() {
		return namedialog;
	}
	public void setNamedialog(NameDialog namedialog) {
		this.namedialog = namedialog;
	}
	 public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	/**
	 * 连接8888端口上的服务器
	 * @return是否成功连接
	 */
	public boolean connect(){
		try {
			client=new Socket("localhost",8888);		
		} catch (Exception e) {
			e.printStackTrace();
			this.setConnected(false);
		}
		this.setConnected(true);
		new ReceiveServerThread(client).start();
		return true;
	}
	/**
	 *关闭客户端连接 
	 * @return 是否成功
	 */
	public boolean disConnect(){
		if(client==null){
			return true;
		}
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("关闭了");
		}
		connected=false;
		return true;
	}
	/**
	 * 供用户界面的监听器调用，向服务器发送报文类的方法
	 * @param msg
	 */
	
	public void sendMsg(BaseMsg msg){
		if(!this.isConnected()){
			return;
		}
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			oos.writeObject(msg);
			System.out.println("发送报文"+msg);
		  //  oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
