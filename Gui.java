import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Gui {
	
	static int x = 10;
	static int y = 10;
	
	static String jvm = "";
	static JTextArea jvmFeilds = new JTextArea();
	static JTextField totelThreadNum = new JTextField();
	
	public static void main(String[] args) throws IOException {
		
		
		/*try {
			System.out.println(get("https://api.github.com/repos/EnDragon/TieBaBak/contents/update", "Authorization", "12dc897e7550c81112899fdf0342e71889596baa"));
		} catch (Throwable e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}*/
		/*boolean b = true;
		if(args.length > 0)
		if(!args[0].equals("-noCheck"))
			b = false;
		if(b)
		update(null);*/
		
		JFrame frame = new JFrame();
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setTitle("E-Dragon TieBa Backup");
		int width = dimension.width;
		int height = dimension.height;
		int w = width / 3 * 2;
		int h = height / 3 * 2;
		frame.setBounds(width / 2 - w / 2, height / 2 - h / 2, w, h);
		frame.setLayout(null);
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setSize(w, h);
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(frame.getSize());
		JTextField textField = new JTextField("");
		JTextField out = new JTextField("");
		JTextField num = new JTextField("");
		JComboBox<String> jComboBox = new JComboBox<String>();
		jComboBox.addItem("HTML");
		jComboBox.addItem("JSON");
		jComboBox.addItem("TXT");
		set(panel, "备份的贴吧：", textField, 100);
		set(panel, "备份输出路径：", out, 100);
		set(panel, "备份贴数(前):", num, 50);
		set(panel, "输出格式:", jComboBox, 100);
		JButton button = new JButton("开始");
		button.setBounds(w - 150, h - 130, 90, 25);
		button.setVisible(true);
		panel.add(button);
		
		button.addActionListener((a)->{
			try {
				//Bak.main(new String[]{"bakBar", "https://tieba.baidu.com/f?kw=" + URLEncoder.encode(textField.getText(), "UTF-8") + "&ie=utf-8", out.getText()});
				write("bakBar", "\"https://tieba.baidu.com/f?kw=" + URLEncoder.encode(textField.getText(), "UTF-8").replaceAll("%", "%%") + "&ie=utf-8\" \"" + out.getText() + "\" " + num.getText() + " -outputMode " + jComboBox.getSelectedItem());
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		});
		
		tabbedPane.add("备份贴吧", panel);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(null);
		y = 10;
		JTextField textField2 = new JTextField("");
		JTextField out2 = new JTextField("");
		set(panel2, "你的Cookie：", textField2, 1000);
		set(panel2, "备份输出路径：", out2, 100);
		String[] titles = new String[]{"备份主题帖", "备份回复", "备份收藏", "备份被@", "备份我的贴吧", "备份粉丝", "备份关注", "备份举报", "备份投诉"};
		String[] cmd = new String[]{"-noThread", "-noReply", "-noLike", "-noAt", "-noBars", "-noFans", "-noConcerns", "-noReport", "-noCompain"};
		JCheckBox[] checkBoxes = new JCheckBox[titles.length];
		for(int i = 0; i < titles.length; i++){
			JCheckBox box = new JCheckBox(titles[i]);
			checkBoxes[i] = box;
			set(panel2, "", box, 500);
			box.setSelected(true);
		}
		panel2.add(textField2);
		panel2.add(out2);
		JButton button2 = new JButton("开始");
		button2.setBounds(w - 150, h - 130, 90, 25);
		button2.setVisible(true);
		panel2.add(button2);
		button2.addActionListener((a)->{
			try {
				String str = "";
				for(int i = 0; i < titles.length; i++){
					if(!checkBoxes[i].isSelected())
						str += cmd[i] + " ";
				}
				write("bakPerson", "\"" + textField2.getText() + "\" \"" + out2.getText() + "\" " + str);
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		});
		
		JPanel panel3 = new JPanel();
		JTextField out3 = new JTextField("");
		panel3.setLayout(null);
		y = 10;
		JTextArea textField3 = new JTextArea("");
		textField3.setLineWrap(true);
		JScrollPane bar = new JScrollPane(textField3);
		//bar.add(textField3);
		bar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//textField3.setBorder(new EmptyBorder(0, 0, 1000, 500));
		textField3.setBounds(0, 0, 1000, 500);
		System.out.println(textField3.getBounds());
		bar.setVisible(true);
		
		set(panel3, "备份的帖子(空格为分割符)：", bar, 1000, 500);
		set(panel3, "备份输出路径：", out3, 70);
		panel3.add(bar);
		
		JButton button3 = new JButton("开始");
		button3.setBounds(w - 150, h - 130, 90, 25);
		button3.setVisible(true);
		panel3.add(button3);
		button3.addActionListener((a)->{
			try {
				write("bak", " \"" + out3.getText() + "\" " + textField3.getText());
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		});
		
		JPanel panel4 = new JPanel();
		panel4.setLayout(null);
		JLabel label = new JLabel("JVM参数：");
		label.setBounds(10, 10, 1000, 25);
		panel4.add(label);
		jvmFeilds.setBounds(10, 30, w - 50, 400);
		panel4.add(jvmFeilds);
		y = 440;
		totelThreadNum.setText("40");
		set(panel4, "总线程数：", totelThreadNum, 100);
		
		/*JPanel panel5 = new JPanel();
		panel5.setLayout(null);
		JLabel label2 = new JLabel("<html>E龙贴吧备份<br><br>E-Dragon TieBa Backup<br><br>作者：拉瑞<br><br>联系方式：<br>QQ:3477232849<br>E-mail:EnDragon@foxmail.com</html>");
		label2.setFont(new Font("宋体", 1, 30));
		label2.setHorizontalTextPosition(JLabel.LEFT);
		label2.setVerticalTextPosition(JLabel.TOP);
		label2.setBounds(w / 4, 0, 1000, 350);
		panel5.add(label2);*/
		
		y = 10;
		JPanel panel6 = new JPanel();
		panel6.setLayout(null);
		JTextField textField4 = new JTextField("");
		set(panel6, "请输入匹配表达式", textField4, 500);
		String[] cmds = new String[]{"title", "floor", "floor2"};
		String[] titles2 = new String[]{"查找主标题", "查找楼层", "查找楼中楼"};
		JCheckBox[] jCheckBoxs = new JCheckBox[titles2.length];
		for(int i = 0; i < titles2.length; i++){
			jCheckBoxs[i] = new JCheckBox(titles2[i]);
			jCheckBoxs[i].setSelected(true);
			set(panel6, "", jCheckBoxs[i], 100);
		}
		JLabel label3 = new JLabel("<html><font color=gray>匹配表达式是匹配帖子的某项属性用的(支持正则表达式)<br>[属性值]=[值]<br>如果有多个属性值需要匹配那么如果是或，那么请用||，如果是和，那么请用&&连接<br>[属性值]有：<br>username --用户名<br>content --贴的内容<br>floor --楼层<br>time --时间(为yyyy-MM-dd HH:mm格式)<br><br><br>正在制作<br><br>还会有更多命令，如帖子抽取等等</font></html>");
		//label3.setFont(new Font("宋体", 1, 30));
		label3.setBounds(x + 10, y += 10 - 40, 350, 300);
		y += 200;
		/*JScrollPane scrollPane = new JScrollPane();
		J
		scrollPane.add(arg0)*/
		panel6.add(label3);
		
		tabbedPane.add("备份贴吧", panel);
		
		tabbedPane.add("备份个人", panel2);
		
		tabbedPane.add("备份帖子", panel3);
		
		tabbedPane.add("其他参数", panel4);
		
		//tabbedPane.add("关于", panel5);
		
		tabbedPane.add("帖子工具", panel6);
		frame.add(tabbedPane);
	}
	
	/*public static JPanel createBakBar(int w, int h){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(w, h);
		JTextField textField = new JTextField("");
		JTextField out = new JTextField("");
		JTextField num = new JTextField("");
		set(panel, "备份的贴吧：", textField, 100);
		//set(panel, "备份输出路径：", out, 100);
		set(panel, "备份贴数(前):", num, 50);
		JButton button = new JButton("开始");
		button.setBounds(w - 150, h - 130, 90, 25);
		button.setVisible(true);
		panel.add(button);
		button.addActionListener((actionListener)->{
			
		});
	}*/
	
	public static void update(String[] args){
    	try {
    		boolean bbb = true;
    		if(bbb)
    			return;
    		System.out.println(Gui.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    		String path = URLDecoder.decode(Gui.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1), "UTF-8");
			FileInputStream stream = new FileInputStream(path);
			byte[] bytes = new byte[1024];
			int c = 0;
			StringBuffer buffer = new StringBuffer("");
			while((c = stream.read(bytes)) > 0) {
				buffer.append(new String(bytes, 0, c));
			}
			stream.close();
			System.out.println(new File(Gui.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ")).canRead());
			byte[] md55 = MessageDigest.getInstance("MD5").digest(buffer.toString().getBytes());
			String str = get("https://api.github.com/repos/EnDragon/TieBaBak/contents/update", "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400", "", "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			//System.out.println(str);
			JSONObject jsonObject = JSONObject.fromObject(str);
			String content = jsonObject.getString("content");
			String str2 = "";
			String[] strs = content.split("\\n");
			for(int i = 0; i < strs.length; i++){
				str2 += new String(Base64.getDecoder().decode(strs[i]));
			}
			//System.out.println(str2);
			JSONObject json = JSONObject.fromObject(str2);
			String MD5 = json.getJSONObject("Gui").getJSONObject("MD5").getString("0.7.3");
			String md5 = "";
			for(int i = 0; i < 16; i++) {
				String sss = Integer.toString(Byte.toUnsignedInt(md55[i]), 16);
				md5 += sss.length() == 1 ? "0" + sss : sss;
			}
			System.out.println(md5);
			if(!new String(md5).equals(MD5)) {
				System.out.println("警告：文件不完整，可能是因为别人篡改或者文件发生损坏，完整文件请至：\r\nhttps://github.com/EnDragon/EDragon_TiebaBackup");
				Thread.sleep(5000);
				System.exit(0);
			}
		} catch (Throwable e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
	}
	
    public static String get(String s, String...add) throws Throwable    {
    	System.out.println(s);
    	StringBuffer buffer = new StringBuffer("");
    	int i2 = 0;
    	while(i2 < 7)
    	try{
			URL url = new URL(s);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			//con.setRequestProperty("Content-Encoding", "gzip");
			//con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			//con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			//con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400");
			for(int i = 0; i < add.length; i+=2){
				con.setRequestProperty(add[i], add[i + 1]);
			}
			/*con.setRequestProperty("Cookie", "TIEBA_USERTYPE=35e5640d10b2278caa4e8c9a; bdshare_firstime=1513953911714; PSTM=1521725322; BIDUPSID=79564A52C9572C301550935483204A3D; rpln_guide=1; IS_NEW_USER=7bc17a41aab0f3bb650d89a6; BAIDUID=62FEB50F1AB68F750C2EF0E0EC08BF02:FG=1; Hm_lvt_287705c8d9e2073d13275b18dbd746dc=1565965390,1566289340,1566480461,1566633150; pgv_pvi=7140703232; __cfduid=dc3505addfe74ea01ac6a3be09d2352111567316748; SEENKW=%E6%88%98%E5%9C%B05%23%E9%BE%99%23%E6%B5%B7%E8%B4%BC%E7%8E%8B%23%E7%88%B6%E6%AF%8D%23%E5%AE%89%E5%8D%93%E6%B8%B8%E6%88%8F; BAIDU_WISE_UID=wapp_1576849917403_558; CLIENTWIDTH=946; CLIENTHEIGHT=1920; SET_PB_IMAGE_WIDTH=848; TIEBAUID=72034c465bb3e9cd3be11771; BDUSS=zZzfi1sLW1PMFZuSU5EU0VYM2tpLWh2QXJlbHJ1M355SnJnWXZla1ppTHh-U1plRUFBQUFBJCQAAAAAAAAAAAEAAAD5accVc21qNTgzMDIwOTg0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPFw~13xcP9dS; STOKEN=4076d6abfe81ba2fe7f0f751197ec67651dae47b7ae6e8039f41e6cc84f4a080; Hm_lvt_98b9d8c2fd6608d564bf2ac2ae642948=1577020961,1577021648,1577021712,1577491051; ZD_ENTRY=sogou; wise_device=0; Hm_lpvt_98b9d8c2fd6608d564bf2ac2ae642948=1577491443");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400");*/
			InputStreamReader stream = new InputStreamReader(con.getInputStream());
			System.out.println(con.getHeaderField("X-Ratelimit-Remaining"));
			int c = 0;
			//byte[] bytes = new byte[1024];
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	System.out.println("AAA");//云里雾里的输出报告
    	System.out.println(buffer.length());//不是什么太重要的，把这个划去
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){
			str = get(con.getHeaderField("Location"));
		}
    	
    	i2 = 7;
    	return str;
    	}
    	catch(Throwable throwable){
    		//throwable.printStackTrace();
    		i2++;
    		System.out.println(s + " " + i2);
    		if(i2 == 7){
    			System.err.println("未成功获取：" + s + " ");
    			throwable.printStackTrace();
    		}
    	}
    	
    	return "NULL";
    	
    	
    }
	
	public static void write(String cmd, String feilds){
			try {
				FileOutputStream o = new FileOutputStream("Run.bat");
				o.write(("echo off\r\nset a=%1\r\nif \"%a%\"==\"\" (\r\necho This is a file that created by the TieBa Backup. if you click it, it will not run the code in it. if you want it to run again, please run it with \"abc\" feild.  !!!!!!!BUT IF YOU RUN IT, IT WILL OVERWRITE YOUR BACKUP FILES, PLEASE DO NOT RUN IT!!!!!!!\r\npause\r\nexit)\r\necho on\r\nchcp 936\r\njava -Dfile.encoding=utf-8 " + jvmFeilds.getText() + " -jar BackUp.jar " + cmd + " " + feilds + " -totelThreadNum " + totelThreadNum.getText() + "\r\npause").getBytes());
				o.close();
				Runtime.getRuntime().exec("cmd /c start Run.bat abc");
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
	}
	
	public static void set(JPanel panel, String key, Component textField, int w, int... h){
		JLabel label = new JLabel(key);
		FontMetrics font = label.getFontMetrics(label.getFont());
		int width = 0;
		for(int i = 0; i < key.length(); i++){
			width += font.charWidth(key.charAt(i));
		}
		label.setBounds(x, y, width, 25);
		label.setVisible(true);
		panel.add(label);
		textField.setBounds(x + 10 + width, y, w, h.length == 0 ? 25 : h[0]);
		//textField.resize(200, 50);
		panel.add(textField);
		y += font.getHeight() + 10;
	}
}
