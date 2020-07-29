import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BakFans extends BakPersonal{
	
    volatile Map<Integer, ArrayList<String>> readMap = new HashMap<>();
    volatile Map<Integer, ArrayList<String[]>> users = new HashMap<>();
    public volatile Map<Integer, String[]> userMap = new HashMap<>();

	String pathName;
	String url;
	String title;
	String add;
	
	public BakFans(String path, String to, int totel, String cookie) {
		super(path, to, totel, cookie);
		// TODO 自动生成的构造函数存根
	}
	
	public BakFans(String path, String to, int totel, String pathName, String url, String title, String add, String cookie) {
		super(path, to, totel, cookie);
		this.pathName = pathName;
		this.url = url;
		this.title = title;
		this.add = add;
		// TODO 自动生成的构造函数存根
	}
	
	public void getFans(){
		getAll(url, "下一页&gt;", this, new TT() {
			
			@Override
			public void run(Object... objects) throws Throwable {
				String str = (String)objects[0];
				int nowI = (int)objects[2];
				ArrayList<String[]> t = new ArrayList<>();
				String[] strs = split(str, "<div class=\"user\"", "div");
				for(int i2 = 0; i2 < strs.length; i2++){
					String string = strs[i2];
					String[] bar = new String[4];
					Matcher homeMatcher = Pattern.compile("(?<=<span class=\"name\"><a href=\").*?(?=\")").matcher(string);
					homeMatcher.find();
					bar[3] = homeMatcher.group();
					Matcher matcher = Pattern.compile("(?<=(target=\"_blank\">))[\\s\\S]*?(?=</a>)").matcher(string);
					matcher.find();
					matcher.find();
					bar[0] = matcher.group();
					Matcher matcher2 = Pattern.compile("(?<=(name=\")).*?(?=\")").matcher(string);
					matcher2.find();
					bar[1] = matcher2.group().replaceAll(".*?>", "");
					matcher.find();
					Matcher matcher3 = Pattern.compile("(?<=(portrait=\")).*?(?=\")").matcher(string);
					matcher3.find();
					bar[2] = matcher3.group();
					t.add(bar);
				}
				synchronized (users) {
					users.put(nowI, t);
				}
			}
		});
	}
	
    public static String Header(String username, String nickname, String tienum, String age, String head){
    	String str = "";
    	str += "<div align=\"center\"><img src=\"" + head + "\" width=300px height=300px style=\"float:center\"></img><br><p><div style=\"float:center;text-align:left;width:300px;height:100px\">用户名：" + username + "<br>昵称：" + nickname + "<br>吧龄：" + age + "<br>发帖数：" + tienum + "</div></p><hr>";
    	return str;
    }
    
    public void getFans2(String str, AAA aaa, String s, String add, int page, String home){
		try {
			String a = strs(str, s, aaa, add, 0);
			String str2 = get("https://tieba.baidu.com" + home);
			String[] strs = split(str2, "<div class=\"thread_name\"", "div");
			ArrayList<String> arrayList = new ArrayList<>();
			for(int i = 0; i < strs.length; i++){
				String string = strs[i];
				Matcher matcher = Pattern.compile("(?<=\"thread_name\"><a href=\"/p/)\\d*").matcher(string);
				Matcher matcher2 = Pattern.compile("(?<=(title=\"))[\\s\\S]*?(?=\")").matcher(string);
				matcher.find();
				if(matcher.group().equals("")){
					System.out.println("个人主页被封");
					continue;
				}
				if(!matcher2.find())
					System.err.println(home + " " + string + " " + matcher.group());
				arrayList.add(matcher.group());
				arrayList.add(matcher2.group());
			}
			Map<String, String> map = getUsers(str);
			arrayList.add(map.get("username"));
			arrayList.add(map.get("nickname"));
			arrayList.add(map.get("age"));
			arrayList.add(map.get("tienum"));
			
			String[] strings = new String[arrayList.size() + 1];
			strings[0] = a;
			for(int i = 0; i < arrayList.size(); i++){
				strings[i + 1] = arrayList.get(i);
			}
			
			synchronized (userMap) {
				userMap.put(page, strings);
			}
		} catch (Throwable e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
    }
    
    public void bakFans(AAA aaa, ArrayList<String[]> users){
		for(int i = 0; i < users.size(); i++){
			int I = i;
			executorService.execute(new Runnable() {
				public void run() {
					System.err.println(urls.size() + urls.toString());
					getFans2(urls.get(I), aaa, "user " + add, "", I, users.get(I)[3]);
				}
			});
		}
    }
	
	public void run() throws Exception{
    	try{
		FileWriter writer = new FileWriter(path + pathName);
		writer.write("<html><head><meta charset=\"UTF-8\"></head>");
		writer.write("<body>");
		ArrayList<String[]> users = new ArrayList<>();
		getFans();
		sleep();
		System.out.println(this.users.size() + " " + this.users);
		int n = this.users.size();
		for(int i = 1; i < n + 1; i++){
			if(this.users.get(i) != null && this.users.get(i).size() != 0)
			users.addAll(this.users.get(i));
		}
		totel = users.size();
		System.err.println("len" + users.size());
		writer.write("<font size=20px><strong><p>" + title +"</p></strong></font>");
		for(int i = 0; i < users.size(); i++){
			String str = "<img src=\"https://gss0.baidu.com/7Ls0a8Sm2Q5IlBGlnYG/sys/portraith/item/" + users.get(i)[2] + "\" />&nbsp;<a style=\"color:#808080\" href=\"users/user " + i + ".html\">" + users.get(i)[0] + "</a>" + "&nbsp;" + users.get(i)[1];
			System.err.println("add" + str);
			urls.add(str);
		}
		System.err.println(title + " " + urls.size());
		totel = users.size();
		AAA aaa = new AAA();
		bakFans(aaa, users);
		sleep();
		//Thread.sleep(500);
		urls = new ArrayList<>();
		bak2 = true;
		for(int i = 0; i <users.size(); i++){
			String[] strs = userMap.get(i);
			if(userMap.get(i) == null)
				continue;
			strs[0] = strs[0].replaceAll("<img src=\"../", "<img width=50 height=50 src=\"");
			writer.write("<div><font size:10px><p>" + strs[0] + "</p></font></div>");
			int len = userMap.get(i).length;
			FileOutputStream stream = new FileOutputStream(path + "/users/user " + i + ".html");
			stream.write("<html><head><meta charset=\"UTF-8\"></head><body><font size=30px>获取粉丝主页正在维护</font></body></html>".getBytes());
			/*Matcher matcher = Pattern.compile("(?<=(<img width=50 height=50 src=\")).*?(?=\")").matcher(strs[0]);
			matcher.find();
			stream.write(Header(strs[len - 4], strs[len - 3], strs[len - 1], strs[len - 2], "../" + matcher.group()).getBytes());
			stream.write("<div style=\"width:700px;align:center;text-align:left\">".getBytes());
			for(int i2 = 1; i2 < strs.length - 4; i2+=2){
				urls.add(strs[i2]);
				stream.write(((Bak.valueMap.get("-hasFansTies") != null ? ("<a style=\"color:#808080\" href=\"tiezi/" + strs[i2] + "\">") : strs[i2]) + strs[i2 + 1] + "</a><br>").getBytes());
			}
			stream.write("</div>".getBytes());*/
			stream.close();
		}
		nowIndex = 0;
		totel = urls.size();
		Bak.totelThreadNum = 40;
		/*if(valueMap.get("-hasFansTies") != null)
		for(int i = 0; i < totelThreadNum; i++){
			Bak.start(0, null);
		}*/
		writer.write("</body></html>");
		writer.close();
    	}
    	catch(Throwable throwable){
    		throwable.printStackTrace();
    	}
	}

}
