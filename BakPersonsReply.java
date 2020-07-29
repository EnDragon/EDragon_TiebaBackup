import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BakPersonsReply extends BakPersonal{
	
	volatile Map<Integer, ArrayList<String>> readMap = new HashMap<>();
	volatile Map<Integer, ArrayList<String>> mapTitle = new HashMap<>();
	Map<Integer, String> map = new HashMap<>();
	volatile Map<String, AAA> map2 = new HashMap<>();
	volatile int nowI = 1;

	public BakPersonsReply(String path, String to, int totel, String cookie) {
		super(path, to, totel, cookie);
		// TODO 自动生成的构造函数存根
	}
	
	private void getAllReply(){
		
		getAll("http://tieba.baidu.com/i/i/my_reply", "下一页&gt;", this, new TT() {
			
			@Override
			public void run(Object... objects) throws Throwable {
				String str = (String)objects[0];
				int nowI = (int)objects[2];
				
				ArrayList<String> arrayList = new ArrayList<>();
				ArrayList<String> titles = new ArrayList<>();
				
				Matcher matcher = Pattern.compile("(?<=(href=\"))[^\"]*?(?=(\" target=\"_blank\">回复</a><))").matcher(str);
				Matcher matcher2 = Pattern.compile("(?<=(class=\"thread_title\" target=\"_blank\">))[\\s\\S]*?(?=<a)").matcher(str);
				while(matcher.find()){
					arrayList.add(matcher.group());
					matcher2.find();
					titles.add(matcher2.group());
				}
				synchronized (readMap) {
					System.out.println(nowI);
					readMap.put(nowI, arrayList);
				}
				synchronized (mapTitle) {
					mapTitle.put(nowI, titles);
				}
			}
		});
	}
	
	private void getReply(String outMode){
		for(int i = 0; i < totel; i++){
			int I = i;
			executorService.execute(new Runnable() {
				public void run() {
					getReply2(urls.get(I), I, outMode);
				}
			});
		}
	}
	
	private void getReply2(String s2, int idd, String outMode){
		try{
			//int nowIndex = Bak.nowIndex;
		String url = "https://tieba.baidu.com" + s2.replaceAll("&amp;", "&");
		int index = url.indexOf("pid=");
		int index2 = url.indexOf("&", index);
		String i = url.substring(index + 4, index2);
		String id = "post_content_" + url.substring(index + 4, index2);
		index = url.indexOf("/p/");
		index2 = url.indexOf("?", index);
		String s = url.substring(index + 3, index2);
		String string = get(url);
		String[] strs = split(string, "<div class=\"l_post l_post_bright j_l_post clearfix", "div");
		System.out.println(strs.length);
		String string2 = "";
		for(int i2 = 0; i2 < strs.length; i2++){
			if(strs[i2].indexOf(id) != -1){
				string2 = strs[i2];
				break;
			}
		}
		System.out.println("(?<=" + id + ")[\\s\\S]*?</div></div>");
		String String = get("https://tieba.baidu.com/p/totalComment?t=0&tid=" + s + "&pid=" + i + "&see_lz=0");
		Object o = JSONObject.fromObject(String).getJSONObject("data").get("comment_list");
		JSONObject json = null;
		if(!(o instanceof JSONArray)){
		json = JSONObject.fromObject(String).getJSONObject("data").getJSONObject("comment_list");
		}
		JSONObject jso = json;
		Floor tie = new Floor();
		tie.title = "";
		StringWriter writer2 = new StringWriter();
		if(map2.get(s) == null)
			map2.put(s, new AAA());
		a(string2, tie, path, s, map2.get(s), json, " " + i, 1, JSONObject.fromObject(String), outMode);
		writer2.write(tie.write(outMode));
		synchronized (map) {
			map.put(idd, writer2.string);
		}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void getAllPersionReply(){
    	try {
    		FileWriter writer = new FileWriter(path + "/my_reply.html");
    		ArrayList<String> titles = new ArrayList<>();
    		writer.write("<html><head><meta charset=\"UTF-8\"></head>");
    		writer.write("<body>");
    		getAllReply();
    		//System.out.println(readMa);
    		System.out.println(readMap);
    		int n = readMap.size();
			for(int i = 1; i < n + 1; i++){
				if(readMap.get(i) == null)
					System.err.println(i);
				urls.addAll(readMap.get(i));
				titles.addAll(mapTitle.get(i));
			}
			nowIndex = 0;
			totel = urls.size();
			for(int i = 0; i < totel; i++) {
				System.out.print(urls.get(i));
			}
			getReply(Bak.outMode);
			sleep();
			for(int i = 0; i < urls.size(); i++){
				if(map.get(i) == null)
				System.out.println(urls.get(i));
				writer.write("<p>你在 &nbsp" + titles.get(i) + "&nbsp中发言：</p><div style=\"position:relative;left:30px\">" + map.get(i).replaceAll("<img src=\"../", "<img src=\"") + "</div>");
			}
			writer.write("</body></html>");
			writer.close();
		} catch (Throwable e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}
