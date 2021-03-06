package net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;

import dao.IUserDao;
import dao.IUserDaoImp;
import entity.RoomPojo;
import entity.User;
import net.MyServer;
import msg.BaseMsg;

/**
 * 服务器类
 * @author zzb  
 *
 */
public class MyServer {
	private static MyServer myserver;
	public IUserDao userDao=new IUserDaoImp();
	private MyServer(){}
	/**
	 * 单例获取服务器对象
	 * @return
	 */
	public static  MyServer getMyServer(){
		if(myserver==null){
		    myserver=new MyServer();
			myserver.resetRooms();
		}
		return myserver;
	}
	private List<RoomPojo> rooms=new ArrayList<RoomPojo>();
	public static List<ClientChatThread> pool=new ArrayList();//socket池
	ServerSocket server=null;
	private static boolean started=false;
	public static void setStarted(boolean started) {
		MyServer.started = started;
	}
	public static boolean isStarted() {
		return started;
	}
	
	public List<RoomPojo> getRooms() {
		return rooms;
	}

	public void setRooms(List<RoomPojo> rooms) {
		this.rooms = rooms;
	}
	/**
	 * 服务器启动时重置房间列表信息
	 */
	private void resetRooms(){
		rooms.clear();
		for(int i=0;i<12;i++){
			RoomPojo r = new RoomPojo(i,null,null,RoomPojo.IDLE);
			rooms.add(r);
		}
	}
	/**
	 * 获得当前在线用户的集合数组
	 * @return 
	 */
	public List<User> getUserList(){
		List<User> list = new ArrayList<User>();
		for(ClientChatThread ct : pool){
			if(ct.getUser()!=null)
			list.add(ct.getUser());
		}
		return list;
	}
	/**
	 * 启动服务器，启动线程监听客户端连接
	 * @return 返回服务器是否启动
	 * 
	 */
	public boolean startListen(){
		try {
			server=new ServerSocket(8888);
			started=true;
		    System.out.println("服务器启动成功");	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		new WaitForClientThread(this).start();
		return true;
	}
	/**
	 * 关闭服务器套接字
	 * @return是否成功关闭
	 */
	public boolean stopListen(){
		if(server==null){
			return true;
		}
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setStarted(false);
		return true;
	}
	
	/**
	 * 执行从客户端中收的的报文的Biz(),在Biz中调用此方法，给相应客户端发送相应的报文
	 * @param msg 应该发送的报文
	 * @param client  相应的客户端套接字
	 */
	public void sendMsgToClient(BaseMsg msg,Socket client){
		for(ClientChatThread c:pool){
			if(c.getClient()==client){
				c.sendMsg(msg,client);
				return;
			}
		}
	}
	/**
	 * 将用户对象和与之匹配的线程进行绑定
	 * @param user
	 * @param client
	 */
	public void bindUsername(User user,Socket client){
		for(ClientChatThread c:pool){
			if(c.getClient()==client){
				c.setUser(user);
				return;
			}
		}
	}

	/**
	 * 功能: 退出时删除姓名
	 * @param client
   */
	public void deleteUserCilent(Socket client){
		for(ClientChatThread c:pool){
			if(c.getClient()==client){
				c.setUser(null);
				return;
			}
		}
	}
public boolean loged(User user){
  for(ClientChatThread c:pool){
    if(user.equals(c.getUser())){
      System.out.println(user.getName()+"   判断相等   "+c.getUser().getName());
      return false;
    }
  }
  return true;
}
	/**
	 * 功能: 客户端断开连接
	 * @param client
   */
	public void deleteClientSocket(Socket client){
		for(ClientChatThread c:pool){
			if(c.getClient()==client){
				pool.remove(c);
				return;
			}
		}
	}
	/**
	 * 服务器向所有在线客户端发送报文类对象方法
	 * @param msg
	 */
	public void sendMsgToAll(BaseMsg msg){
		for(ClientChatThread c:pool){
			c.sendMsg(msg,c.getClient());
		}
	}
	public static void main(String[] args) {
		MyServer.getMyServer().startListen();
	}
	/**
	 * 发送报文给指定User的客户端
	 */
	public void  sendMsgToClient(BaseMsg msg,User user){
		for(ClientChatThread c:pool){
			if(c.getUser().getName().equals(user.getName())){
				c.sendMsg(msg, c.getClient());
				return;
			}
		}
	}
  public User findUser(String name){
    return userDao.findUser(name);
  }
  public void updateUserImag(String filename,String user){
    userDao.updateUserImag(filename,user);
  }
  public void insertUser(User user){
    userDao.insertUser(user);
  }
	public void updateWinNum(User user){
		userDao.update(user.getWinNum()+1,user.getName());
	}
	public void updateLoseNum(User user){
		userDao.updateLoseNum(user.getLoseNum()+1,user.getName());
	}
}
