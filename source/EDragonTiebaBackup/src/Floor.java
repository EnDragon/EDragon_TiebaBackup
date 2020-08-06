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

import org.apache.commons.lang.ObjectUtils.Null;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Floor extends BakFactoryNew{
	String title = "";
	public String user;
	public String username;
	public String content;
	public long pid;
	public String time = "";
	public int num;
	public long id;
	public String from;
	public String bar;
	public int floorpe = THREAD;
	
	public static final int THREAD = 1;
	public static final int PIC = 2;
	
	public Map<Integer, Floor> floors = new HashMap<>();
	Floor a = null;
	int now = 0;
	
	public Floor(){
	}
	
	public Floor(String title){
		this.title = title;
	}
	
	public Floor(long id){
		this.id = id;
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
	
    public void check(String v, String s, Floor tie) throws Throwable{
    	String t = "";
		if(v.indexOf("id=\"ag_container") != -1){
			String name = bar;
			floorpe = PIC;
			for(int I = 1; ; I++){
				String str = Tool.getMore("http://tieba.baidu.com/photo/g/bw/picture/list?kw=" + name + "&tid=" + s + "&pn=" + I + "&info=1", requestHeader);
				JSONObject jsonObject = JSONObject.fromObject(str);
				if(jsonObject.getString("error").equals("failed!")){
					System.err.println("ERROR 读取图册失败 " + s + " " + I);
				}
				if(str.indexOf("\"pic_list\":[]") != -1){
					t += jsonObject.getJSONObject("data").getString("descr") + "<br>";
					t += jsonObject.getJSONObject("data").getString("user_name") + "<br>";
					break;
				}
				if(I == 1){
					tie.title = "图册：" + jsonObject.getJSONObject("data").getString("title");
				}
				JSONArray js = jsonObject.getJSONObject("data").getJSONArray("pic_list");
				for(int i3 = 0; i3 < js.size(); i3++){
					JSONObject object = js.getJSONObject(i3);
					String url = object.getString("purl");
					t += "<img src=\"" + url + "\"></img>";
				}
			}
			tie.get(1).content += t;
		}
		else{
		
		Matcher Matcher = Pattern.compile("span class=\"text\"[\\s\\S]*?收藏</(a|p)>").matcher(v);
		if(!Matcher.find()){
			/*try {
				FileWriter writer = new FileWriter("F:\\I_LIKE\\AAA");
				writer.write(v);
				writer.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}*/
			System.err.println(v + " " + s);
		}
		String sssss = Matcher.group();
		Matcher = Pattern.compile("(?<=(title=\"))[\\s\\S]*?(?=\")").matcher(sssss);
		if(!Matcher.find()){
			System.err.println("NO FOUND TITLE " + s);
		}
		int i1 = v.indexOf("title: \"");
		int i2 = v.indexOf("\"", i1 + 9);
		System.out.println(s);
		String title = Matcher.group();
		System.out.println(title);
		
		tie.title = title;
		}
    }
    
    public int getPageCount(){
    	String v = Tool.get("https://tieba.baidu.com/p/" + id, requestHeader);
		Pattern nString = Pattern.compile("(?<=回复贴，共).*(?=页)");
		Matcher matcher = nString.matcher(v);
		if(!matcher.find()){
			System.err.println("AAAAAAAAAAAAAA" + v + " " + id);
		}
		int l = Integer.parseInt(matcher.group().replaceAll("<[^>]*>", ""));
		return l;
    }
	
	public Floor getFloors(int page){
		Floor floor = new Floor();
		String V = Tool.get("https://tieba.baidu.com/p/" + id + "?pn=" + page, new HashMap<String, String>());
		if(page == 1){
			try {
				check(V, id + "", floor);
			} catch (Throwable e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		
		String string = Tool.get("https://tieba.baidu.com/p/totalComment?t=0&tid=" + id + "&pn=" + page + "&see_lz=0", requestHeader);
		Object o = JSONObject.fromObject(string).getJSONObject("data").get("comment_list");
		JSONObject json = null;
		if(!(o instanceof JSONArray)){
		json = JSONObject.fromObject(string).getJSONObject("data").getJSONObject("comment_list");
		}
		JSONObject jso = json;
		try {
			to(V, "<div class=\"l_post.*?l_post_bright", new TT(){
				public void run(Object... objects)  throws Throwable{
					int nowIndex = (int)objects[0];
					int prevIndex = (int)objects[1];
					int now = 0;
					int pi = nowIndex + 2;
					int i2 = 0;
					int nowIndexN = nowIndex + 1;
					while(now >= 0){
						int left = V.indexOf("<div", pi);
						i2 = Math.min(left == -1 ? Integer.MAX_VALUE : left, V.indexOf("</div", pi));
						now += V.charAt(i2 + 1) == '/' ? -1 : 1;
						pi = i2 + 1;
					}
					String VV = V.substring(nowIndex, i2);
					//System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA:" + V);
					a(VV, floor, id + "", jso, page, JSONObject.fromObject(string));
				}
			});
		} catch (Throwable e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		System.out.println(page);//云里雾里的输出报告
		return floor;
	}
	
    private void a(String V, Floor tie, String s, JSONObject bak, int ints, JSONObject ojson) throws Throwable{
		String find = "post_content_\\d{1,}[\\s\\S]*?style=\"display:.*?;\">";
		to(V, find, new TT(){
			public void run(Object... objects) throws Throwable{
				int nowIndex = (int)objects[0];
				int prevIndex = (int)objects[1];
				Matcher matcher5 = (Matcher)objects[2];
				int now = 0;
				int i2 = 0;
				int pi = nowIndex;
				int nowIndexN = nowIndex;
				while(now >= 0){
					i2 = Math.min(V.indexOf("<div", pi), V.indexOf("</div", pi));
					now += V.charAt(i2 + 1) == '/' ? -1 : 1;
					pi = i2 + 4;
				}
					//System.out.println(nowIndexN + " " + i2);
				if(nowIndex + matcher5.group().length() > i2){
					System.err.println("OUT " + s + "\r\n" + V);
				}
				System.out.println(matcher5.group() + " " + i2);
					String string = V.substring(nowIndex + matcher5.group().length(), i2).replaceAll("\\s", "");
					System.out.println(string);
					if(string.indexOf(">来自：") != -1){
						Matcher matcher = Pattern.compile("(?<=r-url\">)tieba.*?(?=<)").matcher(string);
						Matcher matcher2 = Pattern.compile("(?<=(=\"j-no-opener-url\">))[\\s\\S]*?(?=<)").matcher(string);
						Matcher matcher3 = Pattern.compile("[\\s\\S]*?(?=(<p))").matcher(string);
						matcher.find();
						if(!matcher2.find()){
							System.err.println(string);
						}
						matcher3.find();
						String id = matcher.group();
						id = id.replaceAll("\\?.*", "");
						System.err.println("SHARE:" + id + " " + s);
						//bak(id.substring(id.lastIndexOf("/") + 1));
						string = matcher3.group() + "分享帖子：<!--share--><a href=\"" + id + matcher2.group() + "</a><!--share/-->";
					}
					//System.out.println(string);
					Floor floor2 = new Floor();
					floor2.content = string;
					
					tie.add(floor2);
					//oStream.write(string);
					/*String stri = string.replaceAll("\\s", "").replaceAll("<br>", "\r\n").replaceAll("<[^>]*>", "");
					System.out.println(stri);
					oStream.write("<lou>" + stri + "\n");
					Pattern pattern = Pattern.compile("(?<=(<img))[^>]*(?=>)");
					Matcher matcher = pattern.matcher(string);
					while(matcher.find()){
						//System.out.println(matcher.group());
						Pattern pat = Pattern.compile("(?<=(src=\"))[^\"]*(?=\")");
						Matcher matcher2 = pat.matcher(matcher.group());
						matcher2.find();
						if(map.get(matcher2.group()) == null){
							String p = matcher2.group();
							String name = p.substring(p.lastIndexOf("/"));
							String aString = name.substring(name.lastIndexOf("."));
							aString = aString.split("[#\\?&]")[0];
							String nameee = s + " " + aaa.noww + aString;
							download(matcher2.group(), path + "/pic/" + nameee);
							map.put(matcher2.group(), nameee);
							aaa.noww++;
						}
						oStream.write("<pic>" + map.get(matcher2.group()) + "\r\n");
					}*/
					
					a2(V, floor2, s, ints);
					
					if(bak != null){
						Pattern patte = Pattern.compile("(?<=(post_content_))\\d{1,}");
						Matcher matche = patte.matcher(V);
						matche.find();
						
						JSONObject jso = bak.getJSONObject(matche.group());
						if(jso.toString().equals("null"))
							return;
						JSONArray array = jso.getJSONArray("comment_info");
						for(int j = 0; j < array.size(); j++){
							JSONObject aJsonObject = array.getJSONObject(j);
							
							//JSONObject jsonObject = ojson.getJSONObject("data").getJSONObject("user_list").getJSONObject(aJsonObject.getLong("user_id") + "");
							//System.out.print(jsonObject);
							//String[] strs = 
							//user.println(jsonObject.getString("user_name") + " " + jsonObject.getString("user_nickname") + "\r\n");
							Floor floor = new Floor(aJsonObject.getString("username"), aJsonObject.getString("content"), "", -1, "", aJsonObject.getString("username"));
							//String[] sss = {aJsonObject.getString("content"), "---" + aJsonObject.getString("username")/* + " " + getTime(aJsonObject.getString("now_time"))*/ + "<br><hr>"};
							floor2.add(floor);
							//strings[i] += "\r\n  " + aJsonObject.get("content");
						}
					}
			}
		});
    }
    
    /*public ArrayList<Floor> getFloors(){
    	if(getDepth() == 1){
    		
    	}
    }*/
    
    private void a2(String V, Floor tie, String s, int pn) throws Throwable{
    	//System.out.println("AAAAA:::::::" + V);
    	Pattern pattern = Pattern.compile("(alog-group=\"p_author\").*(</a>)");
    	Matcher matcher = pattern.matcher(V);
    	
    	matcher.find();
    	matcher.group();
    	//System.out.println(matcher.group());
    	Pattern p = Pattern.compile("(?<=(target=\"_blank\">)).*(?=</a>)");
    	Matcher m = p.matcher(matcher.group(0));
    	m.find();
    	Matcher username = Pattern.compile("/home/main\\?un=[^&]*").matcher(V);
    	username.find();
    	Matcher username2 = Pattern.compile("(?<=(un=)).*").matcher(username.group());
    	username2.find();
    	tie.user = m.group();
    	tie.username = URLDecoder.decode(username2.group(), "UTF-8");
    	Pattern pa = Pattern.compile("(?<=(<span class=\"tail-info\">))[^>]*(?=</span>)");
    	Matcher ma = pa.matcher(V);
    	if(ma.find()){
    		tie.num = Integer.parseInt(ma.group().substring(0, ma.group().indexOf("楼")));
    	}
    	if(ma.find()){
    		tie.time = ma.group();
    	}
    	if(ma.find()){
    		tie.from = ma.group();
    	}
    	
    }
    
    public void add(int page, Floor floor){
    	floors.put(page, floor);
    }
    
    public void delete(int page){
    	floors.remove(page);
    }
    
    public void getAllPages(){
    	int l = getPageCount();
		for(int i = 1; i < l + 1; i++){
			int pn = i;
			Floor floor = getFloors(i);
			add(floor);
		}
    }
    
    public int getDepth(){
    	return a == null ? 1 : a.a == null ? 2 : a.a.a == null ? 3 : -1;
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
				return get(content, "content") + " " + "---" + get(username, "username") + " " + get(user, "user") + " " + get(time, "time") + "<br><hr>";
			}
		}
		else if(mode.equals("JSON")){
			return toJson().toString();
		}
		else if(mode.equals("TXT")){
			return toTxt();
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
			jsonObject.put("nickname", user);
			jsonObject.put("username", username);
			try {
				jsonObject.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time).getTime());
			} catch (ParseException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
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
			return string;
		}
		if(a.a == null){
			string += content.replaceAll("<br>", "\r\n");
			string += "\r\n   " + username;
			string += "  " + num + "楼" + "  ";
			string += time + "\r\n";
			
			for(int i = 1; i < floors.size() + 1; i++){
				string += floors.get(i).toTxt() + "\r\n";
			}
			
			return string;
		}
		if(a.a.a == null){
			string += "      ---" + content.replaceAll("<br>", "\r\n") + "\r\n";
			string += "           " + username;
			return string;
		}
		return null;
	}
}
