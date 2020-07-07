package Client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener{
    private JButton send,clear,exit,login,register,logout;
    private JPanel p_login,p_chat;
    private JTextField nick,nick1,pass1,message;
    private JTextArea msg,online;

    private Socket client;
    private DataStream dataStream;
    private DataOutputStream dos;
    private DataInputStream dis;
    //Account[] p = new Account[]{ new Account("abc", "abc"), new Account("def", "def") };
    ArrayList<Account> user = new ArrayList<Account>();;
                 
        public class Account {
            String username;
            String password;
            
            public Account (String u, String p){
                this.username = u;
                this.password = p;
            }
            
            public String getUsername(){
                return username;
            }
            
            public String getPassword(){
                return password;
            }
            
            public void setUsername (String username){
                this.username = username;
            }
            
            public void setPassword (String password){
                this.password = password;
            }
        }
        
	public Client(){
		super("Chat chit : Client");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
                                System.exit(0);
			}
		});
		setSize(650, 400);
		addItem();
		setVisible(true);
	}
//-----[ Tạo giao diện ]--------//
	private void addItem() {
		setLayout(new BorderLayout());

		exit = new JButton("Thoát");
		exit.addActionListener(this);
		send = new JButton("Gởi");
		send.addActionListener(this);
		clear = new JButton("Xóa");
		clear.addActionListener(this);
		login= new JButton("Đăng nhập");
		login.addActionListener(this);
                register= new JButton("Đăng ký");
		register.addActionListener(this);
		logout= new JButton("Thoát");
		logout.addActionListener(this);

		p_chat = new JPanel();
		p_chat.setLayout(new BorderLayout());

		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		nick = new JTextField(20);
		p1.add(new JLabel("Níck chát : "));
		p1.add(nick);
		p1.add(exit);

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());

		JPanel p22 = new JPanel();
		p22.setLayout(new FlowLayout(FlowLayout.CENTER));
		p22.add(new JLabel("Danh sách online"));
		p2.add(p22,BorderLayout.NORTH);

		online = new JTextArea(10,10);
		online.setEditable(false);
		p2.add(new JScrollPane(online),BorderLayout.CENTER);
		p2.add(new JLabel("     "),BorderLayout.SOUTH);
		p2.add(new JLabel("     "),BorderLayout.EAST);
		p2.add(new JLabel("     "),BorderLayout.WEST);

		msg = new JTextArea(10,20);
		msg.setEditable(false);

		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.LEFT));
		p3.add(new JLabel("Tin nhắn"));
		message = new JTextField(30);
		p3.add(message);
		p3.add(send);
		p3.add(clear);

		p_chat.add(new JScrollPane(msg),BorderLayout.CENTER);
		p_chat.add(p1,BorderLayout.NORTH);
		p_chat.add(p2,BorderLayout.EAST);
		p_chat.add(p3,BorderLayout.SOUTH);
		p_chat.add(new JLabel("     "),BorderLayout.WEST);

		p_chat.setVisible(false);
		add(p_chat,BorderLayout.CENTER);
		//-------------------------
		p_login = new JPanel();
		p_login.setLayout(new FlowLayout(FlowLayout.CENTER));
		p_login.add(new JLabel("Nick chát : "));
		nick1=new JTextField(10);
                p_login.add(nick1);
                p_login.add(new JLabel("Pass : "));
		pass1=new JTextField(10);
		p_login.add(pass1);
		p_login.add(login);
                p_login.add(register);
		p_login.add(logout);
                logout.setVisible(false);
                
		add(p_login,BorderLayout.NORTH);
                
                user.add(new Account("a","a"));
                user.add(new Account("b","b"));
	}
//---------[ Socket ]-----------//
	private void go() {
		try {
			client = new Socket("localhost",2207);
			dos=new DataOutputStream(client.getOutputStream());
			dis=new DataInputStream(client.getInputStream());

			//client.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,"Lỗi kết nối, xem lại dây mạng đi hoặc room chưa mở.","Message Dialog",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}
        
	public static void main(String[] args) {
		new Client().go();
	}
        
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String getMSG(){
		String data=null;
		try {
			data=dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public void getMSG(String msg1, String msg2){
		int stt = Integer.parseInt(msg1);
		switch (stt) {
		// tin nhắn của những người khác
		case 3:
			this.msg.append(msg2);
			break;
		// update danh sách online
		case 4:
			this.online.setText(msg2);
			break;
		// server đóng cửa
		case 5:
			dataStream.stopThread();
			exit();
			break;
		// bổ sung sau
		default:
			break;
		}
	}
//----------------------------------------------
	private void checkSend(String msg){
		if(msg.compareTo("\n")!=0){
			this.msg.append("Tôi : "+msg);
			sendMSG("1");
			sendMSG(msg);
		}
	}
	private boolean checkLogin(String nick, String pass){        
		int l = user.size();
                for(int i = 0; i < l; i++){
                    if(nick.compareTo(user.get(i).username)==0 && pass.compareTo(user.get(i).password)==0){
                            sendMSG(nick);
                            int sst = Integer.parseInt(getMSG());
                            if(sst==0)
                                return false;
                            else 
                                return true;
                    }
                }
                return false;
	}

	private void exit(){
		try {
                        sendMSG("0");
			dos.close();
			dis.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exit){
                        dataStream.stopThread();
                        exit();
                        p_chat.setVisible(false);
                        p_login.setVisible(true);
                        nick1.setText("");
                        pass1.setText("");
                        go();
		}
		else if(e.getSource()==clear){
			message.setText("");
		}
		else if(e.getSource()==send){
			checkSend(message.getText()+"\n");
			message.setText("");
		}
                else if(e.getSource()==register){
                        user.add(new Account(nick1.getText(),pass1.getText()));
                        JOptionPane.showMessageDialog(this,"Bạn đã đăng ký thành công!","Message Dialog",JOptionPane.WARNING_MESSAGE);
		}
		else if(e.getSource()==login){
			if(checkLogin(nick1.getText(),pass1.getText())){
				p_chat.setVisible(true);
				p_login.setVisible(false);
				nick.setText(nick1.getText());
				nick.setEditable(false);
				this.setTitle(nick1.getText());
				msg.append("Đã đăng nhập thành công\n");
				dataStream = new DataStream(this, this.dis);
			}
			else{
				JOptionPane.showMessageDialog(this,"Chưa đăng ký tài khoản, bạn vui lòng đăng ký.","Message Dialog",JOptionPane.WARNING_MESSAGE);
			}
		}
		else if(e.getSource()==logout){
			exit();
		}
	}


}
