import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Floor {
	String title = "";
	public String user;
	public String username;
	public String content;
	public String time = "";
	public int num;
	public long id;
	public String from;
	public Map<Integer, Floor> floors = new HashMap<>();
	Floor a = null;
	int now = 0;
	
	public Floor(){
	}
	
	public Floor(String title){
		this.title = title;
	}
	
	public Floor(String user, String content, String time, int num, String from, String username){
		this.user = user;
		this.content = content;
		this.time = time;
		this.num = num;
		this.from = from;
		this.username = username;
	}
	
	public Floor(String user, String content, String time, String username){
		this.user = user;
		this.content = content;
		this.time = time;
	}
	
	public Floor get(int pn){
		if(floors.get(pn) == null)
			floors.put(pn, new Floor("", "", "", 0, "", ""));
		return floors.get(pn);
	}
	
	public void add(Floor floor){
		/*if(Math.random() < 0.0001)
			update();*/
		floor.a= this;
		floors.put(floors.size() + 1, floor);
	}
	
	public static String get(String s, String tag){
		return "<!--" + tag + "-->" + s + "<!--" + tag + "/-->";
	}
	
	public Floor getFromFloor(int num) {
		if(a == null){
			for(int i = 0; i < floors.size(); i++){
				if(floors.get(i + 1).num == num){
					return floors.get(i + 1);
				}
			}
		}
		else{
			
		}
		return null;
	}
	
	public String write(String mode){
		if(mode.equals("HTML")){
			if(a== null){
				StringBuffer buffer = new StringBuffer("");
				buffer.append("<html><head><meta charset=\"UTF-8\"></head><body>");
				buffer.append("<!--title--><p><font size=50><strong>" + title + "</p></strong></font><!--title/-->\n");
				for(int i = 1; i < floors.size() + 1; i++){
					buffer.append("<!--floor 1-->" + floors.get(i).write(mode) + "<!--floor 1/-->");
				}
				buffer.append("</body></html>");
				return buffer.toString();
			}
			if(a.a == null){
				String str = "";
				for(int i = 1; i < floors.size() + 1; i++){
					str += "<!--floor 2-->" + floors.get(i).write(mode) + "<!--floor 2/-->";
				}
				return "<p text-indent:2em>" + get(content, "content") + "</p><p text-indent:2em>" + get(user, "user") + get("<!--" + username + "-->", "username") + " " + get(num + "", "num") + "楼 " + get(time, "time") + "</p>" + "<div style=\"background-color:#808080\">" + str + "</div><hr>";
			}
			if(a.a.a == null){
				return get(content, "content") + " " + "---" + get(user, "user") + " " + get(time, "time") + "<br><hr>";
			}
		}
		else if(mode.equals("JSON")){
			return toJson().toString();
		}
		else if(mode.equals("TXT")){
			
		}
		return null;
	}
	
	public static Floor fromString(String str, String outputMode){
		return fromJson(JSONObject.fromObject(str), outputMode);
	}
	
	public static Floor fromJson(JSONObject jsonObject, String outputMode){
		if(outputMode.equals("JSON")){
			Floor floor = new Floor();
			String model = jsonObject.getString("model");
			switch (model) {
			case "top":
				floor.title = jsonObject.getString("title");
				JSONArray array = jsonObject.getJSONArray("floors");
				for(int i = 0; i < array.size(); i++){
					JSONObject floor22 = (JSONObject)array.get(i);
					Floor floor2 = fromJson(floor22, outputMode);
					floor.add(floor2);
				}
				break;
			case "floor":
				floor.content = jsonObject.getString("content");
				floor.user = jsonObject.getString("nickname");
				floor.username = jsonObject.getString("username");
				floor.num = jsonObject.getInt("floor");
				floor.time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(jsonObject.getLong("time")));
				JSONArray array2 = jsonObject.getJSONArray("floor2");
				for(int ii = 0; ii < array2.size(); ii++){
					JSONObject jsonObject2 = (JSONObject)array2.get(ii);
					Floor floor3 = fromJson(jsonObject2, outputMode);
					//floor3.time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(jsonObject2.getLong("time")));
					floor.add(floor3);
				}
				break;
			case "floor2":
				floor.content = jsonObject.getString("content");
				floor.username = jsonObject.getString("username");
				floor.user = jsonObject.getString("username");
			default:
				break;
			}
			return floor;
		}
		else if(outputMode.equals("default")){
			
		}
		return null;
	}
	
	public JSONObject toJson(){
		JSONObject jsonObject = new JSONObject();
		if(a == null){
			jsonObject.put("title", title);
			jsonObject.put("model", "top");
			JSONArray array = new JSONArray();
			for(int i = 1; i < floors.size() + 1; i++){
				array.add(floors.get(i).toJson());
			}
			jsonObject.put("floors", array);
			return jsonObject;
		}
		if(a.a == null){
			JSONArray array = new JSONArray();
			for(int i = 1; i < floors.size() + 1; i++){
				array.add(floors.get(i).toJson());
			}
			jsonObject.put("model", "floor");
			jsonObject.put("content", content);
			jsonObject.put("nickname", user);
			jsonObject.put("username", username);
			jsonObject.put("floor", num);
			try {
				jsonObject.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time).getTime());
			} catch (ParseException e) {
				// TODO 自动生成的 catch 块
				System.err.println("time error" + a.id);
				e.printStackTrace();
			}
			jsonObject.put("floor2", array);
			return jsonObject;
		}
		if(a.a.a == null){
			jsonObject.put("model", "floor2");
			jsonObject.put("content", content);
			jsonObject.put("username", username);
			return jsonObject;
		}
		return null;
	}
	
	public String toTxt(){
		String string = "";
		if(a == null){
			string += title + "\r\n\r\n";
			for(int i = 1; i < floors.size() + 1; i++){
				string += floors.get(i).toTxt() + "=======================\r\n\r\n";
			}
			return toTxt();
		}
		if(a.a == null){
			string += content.replaceAll("<br>", "\r\n");
			string += username;
			string += "  " + num + "楼" + "  ";
			try {
				string += new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time).getTime() + "\r\n";
			} catch (ParseException e) {
				// TODO 自动生成的 catch 块
				System.err.println("time error" + a.id);
				e.printStackTrace();
			}
			
			for(int i = 1; i < floors.size() + 1; i++){
				string += floors.get(i).toTxt();
			}
			
			return string;
		}
		if(a.a.a == null){
			string += "   ---" + content.replaceAll("<br>", "\r\n");
			string += "        " + username;
			return string;
		}
		return null;
	}
	
	/*public static void update(){
    	try {
    		boolean b = true;
    		if(b)
    			return;
    		String path = URLDecoder.decode(Bak.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1), "UTF-8");
			FileInputStream stream = new FileInputStream(path);
			byte[] bytes = new byte[1024];
			int c = 0;
			StringBuffer buffer = new StringBuffer("");
			while((c = stream.read(bytes)) > 0) {
				buffer.append(new String(bytes, 0, c));
			}
			stream.close();
			System.out.println(new File(Bak.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ")).canRead());
			byte[] md55 = MessageDigest.getInstance("MD5").digest(buffer.toString().getBytes());
			String str = Bak.get("https://api.github.com/repos/EnDragon/TieBaBak/contents/update", "Authorization", "token 12dc897e7550c81112899fdf0342e71889596baa");
			//System.out.println(str);
			JSONObject jsonObject = JSONObject.fromObject(str);
			String content = jsonObject.getString("content");
			String str2 = "";
			String[] strs = content.split("\\n");
			for(int i = 0; i < strs.length; i++){
				str2 += new String(Base64.getDecoder().decode(strs[i]));
			}
			System.out.println(str2);
			JSONObject json = JSONObject.fromObject(str2);
			String MD5 = json.getJSONObject("MD5").getString("V0.8.4");
			String md5 = "";
			for(int i = 0; i < 16; i++) {
				String sss = Integer.toString(Byte.toUnsignedInt(md55[i]), 16);
				md5 += sss.length() == 1 ? "0" + sss : sss;
			}
			if(!new String(md5).equals(MD5)) {
				JFrame frame = new JFrame();
				JLabel label = new JLabel("<html><font size=50px>你使用的软件是盗版软件<br>本软件名称：E龙贴吧备份<br>作者：QQ:3477232849<br>如果发现盗版请与我联系<br>正版软件下载地址：https://github.com/EnDragon/EDragon_TiebaBackup<br>本软件为免费软件，请不要用于非法用途</font></html>");
				label.setFont(new Font(label.getFont().getName(), 1, 50));
				frame.add(label);
				frame.setBounds(100, 100, 1000, 700);
				frame.setVisible(true);
			}
		} catch (Throwable e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
	}*/
}
