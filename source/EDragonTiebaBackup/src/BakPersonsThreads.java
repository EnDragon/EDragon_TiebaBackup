import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BakPersonsThreads extends BakPersonal{

	public BakPersonsThreads(String path, String to, int totel, String cookie) {
		super(path, to, totel, cookie);
		// TODO 自动生成的构造函数存根
	}
	
	public void getMainPageAndThreads() throws Throwable{
		if(cookie == null){
			throw new NullPointerException("cookie == null");
		}
		if(path == null){
			throw new NullPointerException("path == null");
		}
		String strs = "";
    	ArrayList<String> arrayList = new ArrayList<>();
    	int b = 1;
    	while(b > 0){
    		System.out.println(b);
    	String str = get("http://tieba.baidu.com/i/i/my_tie?pn=" + b, "Cookie", cookie);
    	//System.out.println(str);
    	Matcher matcher5 = Pattern.compile("(?<=(<img id=\"img_aside_head\" isrc=\"http://tb.himg.baidu.com/sys/portrait/item/)).*?(?=\")").matcher(str);
    	matcher5.find();
    	System.out.println(matcher5.group());
    	download("https://gss0.baidu.com/7Ls0a8Sm2Q5IlBGlnYG/sys/portraith/item/" + matcher5.group(), path + "/head.png", "Cookie", cookie);
    	Matcher matcher = Pattern.compile("<td class=\"nowrap\">在.*?回复").matcher(str);
    	Matcher matcher4 = Pattern.compile("(?<=(<td class=\"wrap\"><a href=\")).*?(?=\\?)").matcher(str);
    	FileOutputStream outputStream = new FileOutputStream(path + "/I.html");
    	outputStream.write("<html><head><meta charset=\"UTF-8\"></head><body>".getBytes());
    	Map<String, String> map = getUsers(get("http://tieba.baidu.com/home/main?id=" + matcher5.group()));
    	outputStream.write(("<div align=\"center\"><img src=\"head.png\" width=300px height=300px style=\"float:center\"></img><br><p><div style=\"float:center;text-align:left;width:300px;height:100px\">用户名：" + map.get("username") + "<br>昵称：" + map.get("nickname") + "<br>吧龄：" + map.get("age") + "<br>发帖数：" + map.get("tienum") + "</div></p><hr>").getBytes());
    	outputStream.write("<div style=\"width:700px;text-align:left\"><p>你的发帖目录：</p><div width=500px style=\"float:center;text-align:left\">".getBytes());
    	Matcher Matcher = Pattern.compile("(?<=(\"thread_title\"  target=\"_blank\">))[^<]*(?=<)").matcher(str);
    	while(matcher.find()){
    		matcher4.find();
    		String s = matcher.group();
    		Matcher matcher2 = Pattern.compile("(?<=在)[^>]*>").matcher(s);
    		matcher2.find();
    		Matcher matcher3 = Pattern.compile("(?<=(href=\"/f\\?kw=))[^\"]*(?=\")").matcher(matcher2.group());
    		matcher3.find();
    		try {
				//System.out.println(java.net.URLDecoder.decode(matcher3.group(), "UTF-8"));
    			//String string = get("https://tieba.baidu.com" + matcher4.group());
    			//Matcher Matcher = Pattern.compile("(?<=(title=\"))[^\"]*?(?=(\" style))").matcher(string);
    			Matcher.find();
    			arrayList.add(matcher4.group().substring(matcher4.group().lastIndexOf("/") + 1));
    			if(matcher4.group().substring(matcher4.group().lastIndexOf("/") + 1).equals("6193383384")){
    				System.err.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + b);
    			}
    			//System.out.println((string + " " + matcher4.group()));
    			String string2 = Matcher.group();
				strs += "<b><font size=5px>你在" + java.net.URLDecoder.decode(matcher3.group(), "UTF-8") + "吧发表:<a style=\"color:#808080\" href=\"tiezi/" + matcher4.group().substring(matcher4.group().lastIndexOf("/") + 1) + ".html\">" + string2 + "</a></font></b><br>";
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
    	}
    	outputStream.write(strs.getBytes());
    	outputStream.write("</div></div></div>".getBytes());
    	outputStream.write("</body></html>".getBytes());
		System.out.println(strs);
		
		if(str.indexOf("下一页&gt") == -1){
			b = -5;
		}
		b++;
    	}
    	
    	urls = new ArrayList<>();
    	for(int i = 0; i < arrayList.size(); i++){
    		urls.add(arrayList.get(i));
    	}
    	
    	//cookie = "BAIDUID=389EBD934FC09D2B69A79B114ECFB17B:FG=1; BIDUPSID=389EBD934FC09D2B69A79B114ECFB17B; PSTM=1592734441; TIEBA_USERTYPE=b285b4caaafe9471a2f25e8a; rpln_guide=1; cflag=13%3A3; Hm_lvt_98b9d8c2fd6608d564bf2ac2ae642948=1595135485,1595139511,1595209879,1595401056; st_key_id=17; baidu_broswer_setup_E___Dragon=0; TIEBAUID=ebcfd95d39213f6b72d8d903; BDUSS=mNUanVBQk1rWURmdDkyOHBhTGZLS3JZVWJYdXVSVHhCek9qSTh1UzZpQTVkVDlmRUFBQUFBJCQAAAAAAQAAAAEAAADjzqMeRW5EcmFnb25fTGFqdXMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADnoF1856BdfV; BDUSS_BFESS=mNUanVBQk1rWURmdDkyOHBhTGZLS3JZVWJYdXVSVHhCek9qSTh1UzZpQTVkVDlmRUFBQUFBJCQAAAAAAQAAAAEAAADjzqMeRW5EcmFnb25fTGFqdXMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADnoF1856BdfV; STOKEN=6ea9a39a95f8b31c8630d521ae67e18a9d1c0f373cf6947d84cf70aa11245760; wise_device=0; Hm_lpvt_98b9d8c2fd6608d564bf2ac2ae642948=1595403474; st_data=d000ab0f1575b09b206e328eb0739612a43cfa6737b9b6c7024aed204151676796ca3ce01b1ec38899477603759cbfef6a3ad97d1a733cfa356f11d58b96ae711af37cab9d13ed7b0a24ce0dca44069fcb77d1307d15afb451adf423f4a70e9779457b05c5c4028306656e0610aaa7b7215341dc55e40a7f7d17fecaa39aaf9e; st_sign=b6eb06fc";
    	for(String str : urls)
    	System.out.print(str + ", ");
    	totel = arrayList.size();
	}
	
	public void run() throws Exception{
		
		for(int i = 0; i < totel; i++){
			int I = i;
			executorService.execute(new Runnable() {
				public void run() {
					bak(urls.get(I), path, Bak.outMode);
				}
			});
		}
		
		
	}

}
