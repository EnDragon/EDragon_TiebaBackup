import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BakFactory {
	volatile Map<String, String> map = new HashMap<String, String>();
	volatile ThreadPoolExecutor executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(Bak.totelThreadNum);
	String path;
	String to;
	volatile int nowIndex = 0;
	int totel = -1;
	volatile ArrayList<String> urls = new ArrayList<>();
	boolean bak2 = false;
	String cookie = "";

	Writer writer;
	
	public BakFactory(String path, String to, int totel){
		this.path = path;
		this.to = to;
		this.totel = totel;
	}
	
	public void addUrl(String url){
		urls.add(url);
	}
	
	public void run() throws Exception{
		if(path == null){
			throw new NullPointerException("path == null");
		}
		if(to == null){
			throw new NullPointerException("to == null");
		}
		if(totel == -1){
			throw new NullPointerException("totel == -1");
		}
	}
	
	public void sleep(){
		try {
			Thread.sleep(500);
		while(executorService.getActiveCount() > 0){
			Thread.sleep(200);
		}
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public void bakThreads(String outMode){
		for(int i = 0; i < totel; i++){
			int I = i;
			System.out.println("开始备份id = " + urls.get(I) + " " + I + "/" + urls.size());
			executorService.execute(new Runnable() {
				public void run() {
					bak(urls.get(I), path, outMode);
				}
			});
		}
	}
	
	public synchronized void next(){
		nowIndex++;
	}
	
	//备份一个帖子的函数
    public void bak(String s, String path, String outMode){
    	//System.out.println(s);
    	System.out.println(":Backing up Thread[id=" + s + "]");
    	
    	if(bak2){
		File file = new File(path + "/tiezi/" + s + ".html");
		if(file.exists())
		try {
			if(file.length() > 7){
			FileInputStream stream = new FileInputStream(file);
			stream.skip(file.length() - 7);
			byte[] bytes = new byte[7];
			stream.read(bytes);
			stream.close();
			if(new String(bytes).equals("</html>")){
				System.out.println("已检测到以备份的贴：自动跳过：" + s);
				return;
			}
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
    	}
		
		try{
    	if(s.equals("null")){
    		return;
    	}
    	
    	AAA aaa = new AAA();
    	
			String v = get("https://tieba.baidu.com/p/" + s, "Cookie", cookie);
			//System.out.println(v);
			String id = s;
			
			Writer oStream;
			
			oStream = new AFileWriter(path + "/tiezi/" + s + "." + (outMode.equals("HTML") ? "html" : outMode.equals("JSON") ? "json" : "txt"));
			
			Floor tie = new Floor();
			
			tie.id = Long.parseLong(s);
			check(v, s, tie);
			
			//System.out.println(v);

			Pattern nString = Pattern.compile("(?<=回复贴，共).*(?=页)");
			Matcher matcher = nString.matcher(v);
			if(!matcher.find()){
				System.err.println("AAAAAAAAAAAAAA" + v + " " + s);
			}
			int l = Integer.parseInt(matcher.group().replaceAll("<[^>]*>", ""));
			for(int i = 1; i < l + 1; i++){
				System.out.println("::正在备份：第" + i + "页");
				int pn = i;
				bak(s, pn, tie, aaa, l, oStream, outMode, path);
				//System.out.println("完成备份：" + i + "页");
			}
			
			oStream.write(tie.write(outMode));
			oStream.close();
			
			System.err.println("FINISH " + s);
			
			}catch (Throwable e) {
				e.printStackTrace();
				System.err.println("FAIL " + s);
				System.out.println("帖子未完成备份: " + s);
				/*String string = e.getMessage();
				try {
					FileWriter writer = new FileWriter(path + "/err/" + s + ".2");
					writer.write(string);
					writer.close();
				} catch (IOException e1) {
					// TODO 自动生成的 catch 块
					e1.printStackTrace();
				}*/
			}
		
		//System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + s);
			
    }
    
    public void check(String v, String s, Floor tie) throws Throwable{
    	String t = "";
		if(v.indexOf("id=\"ag_container") != -1){
			Matcher matcher = Pattern.compile("(?<=(kw=))[%a-zA-Z0-9]*").matcher(to);
			matcher.find();
			String name = matcher.group();
			for(int I = 1; ; I++){
				String str = getMore("http://tieba.baidu.com/photo/g/bw/picture/list?kw=" + name + "&tid=" + s + "&pn=" + I + "&info=1");
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
					String string = url.substring(url.lastIndexOf("/") + 1);
					System.out.println(Thread.currentThread().getName() + " DOWNLOAD 来自" + s + " " + string);
					t += "<img src=\"../pic/" + string + "\"></img>";
					download(url, path + "/pic/" + string);
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
		//System.out.println("title=" + s);
		String title = Matcher.group();
		System.out.println("title = \"" + title + "\"");
		
		tie.title = title;
		}
    }
    
    public void bak(String s, int pn, Floor tie, AAA aaa, int l, Writer oStream, String outMode, String path){
		try{
			synchronized (tie) {
				tie.now++;
			}
		String V = get("https://tieba.baidu.com/p/" + s + "?pn=" + pn, "Cookie", cookie);
		//System.out.println(V);
		Pattern ppp = Pattern.compile("(?<=(\"forum_id\":))\\d");
		Matcher ma = ppp.matcher(V);
		ma.find();
		System.out.println(":::get totalComment");
		String string = get("https://tieba.baidu.com/p/totalComment?t=0&tid=" + s + "&pn=" + pn + "&see_lz=0");
		Object o = JSONObject.fromObject(string).getJSONObject("data").get("comment_list");
		JSONObject json = null;
		if(!(o instanceof JSONArray)){
		json = JSONObject.fromObject(string).getJSONObject("data").getJSONObject("comment_list");
		}
		JSONObject jso = json;
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
				a(VV, tie, path, s, aaa, jso, "", pn, JSONObject.fromObject(string), outMode);
			}
		});
		//System.out.println(pn);//云里雾里的输出报告
		}
		catch (Throwable e) {
			System.err.println("FAIL  " + s + " " + pn);
			e.printStackTrace(System.err);
		}
    }
    
    public void to(String v, String s, TT tt) throws Throwable{
		int nowIndex = 0;
		int prevIndex = 0;
		Matcher matcher = Pattern.compile(s).matcher(v);
		while(matcher.find()){
			prevIndex = nowIndex;
			tt.run(nowIndex = v.indexOf(matcher.group(), prevIndex + 1), prevIndex, matcher);
		}
    }
    
    public static abstract class TT{
    	public abstract void run(Object... objects) throws Throwable;
    }
    
    public void a(String V, Floor tie, String path, String s, AAA aaa, JSONObject bak, String add, int ints, JSONObject ojson, String outMode) throws Throwable{
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
				//System.out.println(matcher5.group() + " " + i2);
					String string = V.substring(nowIndex + matcher5.group().length(), i2).replaceAll("\\s", "");
					//System.out.println(string);
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
						bak(id.substring(id.lastIndexOf("/") + 1), path, outMode);
						string = matcher3.group() + "分享帖子：<a href=\"" + id.substring(id.lastIndexOf("/") + 1) + ".html\">" + matcher2.group() + "</a>";
					}
					//System.out.println(string);
					string = strs(string, s, aaa, add, ints);
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
					
					a2(V, floor2, aaa, s, add, ints);
					
					if(bak != null){
						Pattern patte = Pattern.compile("(?<=(post_content_))\\d{1,}");
						
						Matcher matche = patte.matcher(V);
						matche.find();
						
						JSONObject jso = bak.getJSONObject(matche.group());
						if(jso.toString().equals("null"))
							return;
						int commentNum = jso.getInt("comment_num");
						int commentListNum = jso.getInt("comment_list_num");
						for(int i = 0; i < commentNum; i+= commentListNum){
							System.out.println("::::获取id=" + s + " pid=" + matche.group() + " 回复页数：" + (i + 1));
							//System.out.println("AAAAAAAAAAAA");
							if(i == 0){
								JSONArray array = jso.getJSONArray("comment_info");
								for(int j = 0; j < array.size(); j++){
									JSONObject aJsonObject = array.getJSONObject(j);
									
									//JSONObject jsonObject = ojson.getJSONObject("data").getJSONObject("user_list").getJSONObject(aJsonObject.getLong("user_id") + "");
									//System.out.print(jsonObject);
									//String[] strs = 
									//user.println(jsonObject.getString("user_name") + " " + jsonObject.getString("user_nickname") + "\r\n");
									Floor floor = new Floor(aJsonObject.getString("username"), strs(aJsonObject.getString("content"), s, aaa, "", 1), "", -1, "", aJsonObject.getString("username"));
									floor.time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(aJsonObject.getLong("now_time") * 1000));
									//String[] sss = {aJsonObject.getString("content"), "---" + aJsonObject.getString("username")/* + " " + getTime(aJsonObject.getString("now_time"))*/ + "<br><hr>"};
									floor2.add(floor);
									//strings[i] += "\r\n  " + aJsonObject.get("content");
								}
								continue;
							}
							ArrayList<Floor> floors = getFloor2(Long.parseLong(s), Long.parseLong(matche.group()), i + 1, aaa);
							for(int j = 0; j < floors.size(); j++){
								//System.out.println("AAAAAAAAAAAA" + j);
								floor2.add(floors.get(j));
							}
						}
					}
			}
		});
    }
    
    private ArrayList<Floor> getFloor2(long id, long pid, int pn, AAA aaa, long... time){
    	if(time.length == 0){
    		time = new long[]{System.currentTimeMillis()};
    	}
    	ArrayList<Floor> floors = new ArrayList<>();
    	try{
    	String str = get("https://tieba.baidu.com/p/comment?tid=" + id + "&pid=" + pid + "&pn=" + pn + "&t=" + time[0], "Cookie", cookie);
    	String[] strs = split("  " + str, "<li class=\"lzl_single_post", "li");
    	for(int i = 0; i < strs.length; i++){
    		//System.out.println(strs[i]);
    		//System.out.println("AAAAAAAAAAAAAAAAAAAAAAA " + strs.length + " " + i);
    		Floor floor = new Floor();
        	Matcher username = Pattern.compile("/home/main(/)?\\?un=[^&]*").matcher(strs[i]);
        	username.find();
        	Matcher username2 = Pattern.compile("(?<=(un=)).*").matcher(username.group());
        	username2.find();
        	try {
				floor.username = URLDecoder.decode(username2.group(), "UTF-8");
				//System.err.println("      AAA      " + floor.username);
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
        	//System.out.println("AAAAAAAAAAAAAAAAAAAAAAA " + strs.length + " " + i);
        	Pattern p = Pattern.compile("(?<=(target=\"_blank\" username=\"" + floor.username + "\">)).*?(?=</a>)");
        	Matcher m = p.matcher(strs[i]);
        	m.find();
        	floor.user = strs(m.group(), id + "", aaa, "", 1);
        	Pattern pa = Pattern.compile("(?<=(<span class=\"lzl_time\">))[^>]*(?=</span>)");
        	Matcher ma = pa.matcher(strs[i]);
        	ma.find();
        	floor.time = ma.group();
        	//System.out.println("AAAAAAAAAAAAAAAAAAAAAAA " + strs.length + " " + i);
        	Matcher matcher = Pattern.compile("<span class=\"lzl_content_main\"[\\s\\S]*?</span>").matcher(strs[i]);
        	matcher.find();
        	floor.content = strs(matcher.group(), id + "", aaa, "", 1).replaceAll("(^<span.*?>)|(</span>$)", "");
        	floors.add(floor);
    	}
    	}
    	catch(Throwable exception){
    		exception.printStackTrace();
    	}
    	return floors;
    	
    }
    
    public void a2(String V, Floor tie, AAA aaa, String s, String add, int pn) throws Throwable{
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
    	tie.user = strs(m.group(), s, aaa, add, pn);
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
    	
    	System.out.println(":::获取id=" + s + " floor=" + tie.num);
    	
    }
    
    public String strs(String str, String s, AAA aaa, String add, int i){
    	str = strs(str, "img", "src", aaa, add, s, "", i);
    	str = strs(str, "embed", "data-video", aaa, add, s, " width=1000 height=600", i);
		return str;
    }
    
    public String strs(String str, String s, String a, AAA aaa, String add, String string, String aa, int i){
    	try{
    	Matcher matcher = Pattern.compile("<" + s + "[\\s\\S]*?>").matcher(str);
		while(matcher.find()){
			Matcher matcher2 = Pattern.compile("(?<=(" + a + "=\")).*?(?=\")").matcher(matcher.group());
			boolean b = false;
			if(s.equals("embed") & (b = !matcher2.find())){
				matcher2 = Pattern.compile("(?<=(v(h)?src=\")).*?(?=\")").matcher(matcher.group());
				if(matcher2.find())
				str = str.replace(matcher.group(), "视频：" + matcher2.group());
				else
					System.err.println("NO MUTCH FOUND strs:\r\n" + matcher.group());
				continue;
			}
			if(b){
				System.err.println("未找到：" + string + " " + i + " " + s + " " + a + " " + matcher.group());
				continue;
			}
			String name = matcher2.group();
			int noww = add(aaa);
			System.out.println("----DOWNLOAD 来自" + string + " " + name + " " + noww);
			String names = name.substring(name.lastIndexOf("/") + 1);
			//System.out.println("AAAAA" + matcher.group());
			if(map.get(name) == null){
				String stri = names.split("[\\?]")[0];
				String nameee = string + add + " " + noww + stri.substring(stri.lastIndexOf("."));
				//System.err.println(Thread.currentThread().getName() + " DOWNLOAD 来自" + string + " " + name + " " + noww + " " + nameee);
				download(name, path + "/pic/" + nameee);
				map.put(name, nameee);
			}
			str = str.replace(matcher.group(), /*"<pic>" + */"<" + s + aa + " src=\"../pic/" + map.get(name) + "\"></img>"/* + "</pic>"*/);
		}
    	}
    	catch (Throwable e) {
			System.err.println(str + " " + string + " " + i);
			e.printStackTrace();
		}
		return str;
    }
    
    public synchronized int add(AAA aaa){
    	aaa.noww++;
    	return aaa.noww - 1;
    }
    
    public int getAll(String url, String checkStr, BakFactory bakFactory, TT tt, String... encode){
		AAA nowI = new AAA();
		AAA now = new AAA();
		AAA nowP = new AAA();
		int i = 0;
		now.noww = 1;
		nowI.noww = 0;
		nowP.noww = Integer.MAX_VALUE;
		while(now.noww > 0){
		for(int ii = 0; ii < 10; ii++){
			int I = ii;
			try {
				executorService.execute(new Runnable() {
					
					@Override
					public void run() {
						String str;
						try {
							int i = nowI.noww * 10 + I + 1;
							str = encode.length == 0 ? get(url + "?pn=" + i, "Cookie", cookie) : getMore(url + "?pn=" + i, "Cookie", cookie);
							tt.run(str, bakFactory, i);
							if(str.indexOf(checkStr) == -1){
								synchronized (now) {
									now.noww = -1;
								}
								synchronized (nowP) {
									if(i < nowP.noww){
										nowP.noww = i;
									}
								}
							}
							else{
								
							}
						} catch (Throwable e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
				});
			} catch (Throwable e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		sleep();
		if(now.noww == 2){
			i++;
			now.noww = 1;
		}
		if(i == 2){
			now.noww = -1;
		}
		nowI.noww++;
		}
		
		return nowP.noww;
    }
    
    public void shutdown(){
    	executorService.shutdown();
    }
    
    public static String[] split(String str, String string, String tag){
    	int index1 = 0;
    	int index2 = 0;
    	ArrayList<String> arrayList = new ArrayList<>();
    	while((index2 = str.indexOf(string, index1 + 1)) != -1){
			int now = 0;
			int pi = index2 + 2;
			int i2 = 0;
			int nowIndexN = index2 + 1;
			while(now >= 0){
				int left = str.indexOf("<" + tag, pi);
				i2 = Math.min(left == -1 ? Integer.MAX_VALUE : left, str.indexOf("</" + tag, pi));
				now += str.charAt(i2 + 1) == '/' ? -1 : 1;
				pi = i2 + 1;
				//System.out.println(i2);
			}
			String VV = str.substring(index2, i2);
			//System.out.println(VV);
			//System.err.println(VV);
    		arrayList.add(VV);
    		index1 = index2;
    	}
    	String[] strs = new String[arrayList.size()];
    	for(int i = 0; i < strs.length; i++){
    		strs[i] = arrayList.get(i);
    	}
    	return strs;
    }
    
    public String get(String s, String...add) throws Throwable    {
    	System.out.println("----GET " + s);
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
			//System.out.println(add[0] + add[1]);
			for(int i = 0; i < add.length; i+=2){
				con.setRequestProperty(add[i], add[i + 1]);
			}
			/*con.setRequestProperty("Cookie", "TIEBA_USERTYPE=35e5640d10b2278caa4e8c9a; bdshare_firstime=1513953911714; PSTM=1521725322; BIDUPSID=79564A52C9572C301550935483204A3D; rpln_guide=1; IS_NEW_USER=7bc17a41aab0f3bb650d89a6; BAIDUID=62FEB50F1AB68F750C2EF0E0EC08BF02:FG=1; Hm_lvt_287705c8d9e2073d13275b18dbd746dc=1565965390,1566289340,1566480461,1566633150; pgv_pvi=7140703232; __cfduid=dc3505addfe74ea01ac6a3be09d2352111567316748; SEENKW=%E6%88%98%E5%9C%B05%23%E9%BE%99%23%E6%B5%B7%E8%B4%BC%E7%8E%8B%23%E7%88%B6%E6%AF%8D%23%E5%AE%89%E5%8D%93%E6%B8%B8%E6%88%8F; BAIDU_WISE_UID=wapp_1576849917403_558; CLIENTWIDTH=946; CLIENTHEIGHT=1920; SET_PB_IMAGE_WIDTH=848; TIEBAUID=72034c465bb3e9cd3be11771; BDUSS=zZzfi1sLW1PMFZuSU5EU0VYM2tpLWh2QXJlbHJ1M355SnJnWXZla1ppTHh-U1plRUFBQUFBJCQAAAAAAAAAAAEAAAD5accVc21qNTgzMDIwOTg0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPFw~13xcP9dS; STOKEN=4076d6abfe81ba2fe7f0f751197ec67651dae47b7ae6e8039f41e6cc84f4a080; Hm_lvt_98b9d8c2fd6608d564bf2ac2ae642948=1577020961,1577021648,1577021712,1577491051; ZD_ENTRY=sogou; wise_device=0; Hm_lpvt_98b9d8c2fd6608d564bf2ac2ae642948=1577491443");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400");*/
			InputStreamReader stream = new InputStreamReader(con.getInputStream());
			int c = 0;
			//byte[] bytes = new byte[1024];
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	/*System.out.println("AAA");//云里雾里的输出报告
    	System.out.println(buffer.length());*///不是什么太重要的，把这个划去
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){
			System.out.println("----Redirect " + con.getHeaderField("Location"));
			str = get(con.getHeaderField("Location"), "Cookie", cookie);
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
    	
    	return null;
    	
    	
    }
    
    public String getMore(String s, String...add) throws Throwable    {
    	System.out.println("----GET " + s);
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
			InputStreamReader stream = new InputStreamReader(con.getInputStream(), "GBK");
			int c = 0;
			//byte[] bytes = new byte[1024];
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	/*System.out.println("AAA");//云里雾里的输出报告
    	System.out.println(buffer.length());*///不是什么太重要的，把这个划去
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){
			System.out.println("----Redirect " + con.getHeaderField("Location"));
			str = get(con.getHeaderField("Location"), "Cookie", cookie);
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
    	
    	return null;
    	
    	
    }
    
    public String post(String s, String[] add, String file){
    	System.out.println("----POST " + s);
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
			con.setRequestMethod("POST");
			for(int i = 0; i < add.length; i+=2){
				con.setRequestProperty(add[i], add[i + 1]);
			}
			con.setDoOutput(true);
			OutputStream outputStream = con.getOutputStream();
			outputStream.write(file.getBytes());
			outputStream.close();
			/*con.setRequestProperty("Cookie", "TIEBA_USERTYPE=35e5640d10b2278caa4e8c9a; bdshare_firstime=1513953911714; PSTM=1521725322; BIDUPSID=79564A52C9572C301550935483204A3D; rpln_guide=1; IS_NEW_USER=7bc17a41aab0f3bb650d89a6; BAIDUID=62FEB50F1AB68F750C2EF0E0EC08BF02:FG=1; Hm_lvt_287705c8d9e2073d13275b18dbd746dc=1565965390,1566289340,1566480461,1566633150; pgv_pvi=7140703232; __cfduid=dc3505addfe74ea01ac6a3be09d2352111567316748; SEENKW=%E6%88%98%E5%9C%B05%23%E9%BE%99%23%E6%B5%B7%E8%B4%BC%E7%8E%8B%23%E7%88%B6%E6%AF%8D%23%E5%AE%89%E5%8D%93%E6%B8%B8%E6%88%8F; BAIDU_WISE_UID=wapp_1576849917403_558; CLIENTWIDTH=946; CLIENTHEIGHT=1920; SET_PB_IMAGE_WIDTH=848; TIEBAUID=72034c465bb3e9cd3be11771; BDUSS=zZzfi1sLW1PMFZuSU5EU0VYM2tpLWh2QXJlbHJ1M355SnJnWXZla1ppTHh-U1plRUFBQUFBJCQAAAAAAAAAAAEAAAD5accVc21qNTgzMDIwOTg0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPFw~13xcP9dS; STOKEN=4076d6abfe81ba2fe7f0f751197ec67651dae47b7ae6e8039f41e6cc84f4a080; Hm_lvt_98b9d8c2fd6608d564bf2ac2ae642948=1577020961,1577021648,1577021712,1577491051; ZD_ENTRY=sogou; wise_device=0; Hm_lpvt_98b9d8c2fd6608d564bf2ac2ae642948=1577491443");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400");*/
			InputStreamReader stream = new InputStreamReader(con.getInputStream());
			int c = 0;
			//byte[] bytes = new byte[1024];
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	/*System.out.println("AAA");//云里雾里的输出报告
    	System.out.println(buffer.length());*///不是什么太重要的，把这个划去
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){
			System.out.println("----Redirect " + con.getHeaderField("Location"));
			str = get(con.getHeaderField("Location"), "Cookie", cookie);
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
    	
    	return null;
    }
    
    public void download(String s, String path, String... add) throws Throwable{
    	FileOutputStream os = new FileOutputStream(path);
    	int i2 = 0;
    	while(i2 < 3){
    	try{
		URL url = new URL((s.indexOf("http") == -1 ? "https:" : "") + s);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		for(int i = 0; i < add.length; i+=2){
			con.setRequestProperty(add[i], add[i+1]);
		}
		InputStream stream = con.getInputStream();
		int c = 0;
		byte[] bytes = new byte[1024 * 1024];
		//byte[] bytes = new byte[1024];
		while((c = stream.read(bytes)) > 0){
			os.write(bytes, 0, c);
		}
		stream.close();
		i2 = 5;
    	}catch(Throwable throwable){
    		i2++;
    		if(i2 == 3){
    			System.err.println("未下载：" + s + " 路径会下载到：" + path);
    		}
    	}
    	}
		os.close();
}
    
    static class AAA{
    	public int noww = 0;
    }
    
    static class Writer{
    	
    	public Writer() throws Throwable{
			
		}
    	
    	public void write(String str) throws Throwable{
    		
    	}
    	
    	public void close() throws Throwable{
    		
    	}
    }
    
    static class AFileWriter extends Writer{

    	FileWriter writer;
		public AFileWriter(String s) throws Throwable {
			super();
			writer = new FileWriter(s);
			// TODO 自动生成的构造函数存根
		}
		
    	public void write(String str) throws Throwable{
    		writer.write(str);
    	}
    	
    	public void close() throws Throwable{
    		writer.close();
    	}
    	
    }
    
    static class StringWriter extends Writer{
    	public StringWriter() throws Throwable {
			super();
			// TODO 自动生成的构造函数存根
		}

		public String string = "";
    	public void write(String str) throws Throwable{
    		string += str;
    	}
    	
    	public void close() throws Throwable{
    		
    	}
    }
	
}