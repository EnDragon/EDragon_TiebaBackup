import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BakLike extends BakPersonal{
	
	volatile Map<Integer, ArrayList<String>> readMap = new HashMap<>();
	volatile Map<Integer, ArrayList<String>> mapTitle = new HashMap<>();
	volatile Map<Integer, ArrayList<String>> tag = new HashMap<>();
	volatile int nowI = 1;

	public BakLike(String path, String to, int totel, String cookie) {
		super(path, to, totel, cookie);
		// TODO 自动生成的构造函数存根
	}
	
	private int getLike(){
		int i = getAll("http://tieba.baidu.com/i/i/storethread", "下一页&gt;", this, new TT() {
			@Override
			public void run(Object... objects) throws Throwable {
				String str = (String)objects[0];
				int nowI = (int)objects[2];
				ArrayList<String> arrayList = new ArrayList<>();
				ArrayList<String> titles = new ArrayList<>();
				ArrayList<String> t = new ArrayList<>(); 
				String[] strs = split(str, "<li class=\"feed_item", "li");
				for(int i2 = 0; i2 < strs.length; i2++){
					String string = strs[i2];
					Matcher matcher = Pattern.compile("(?<=(<a class=\"itb_thread\" href=\")).*?(?=\")").matcher(string);
					Matcher matcherTitle = Pattern.compile("(?<=(title=\"))[\\s\\S]*?(?=\")").matcher(string);
					Matcher matcherLike = Pattern.compile("(?<=(<div class='j_favth_recommend_tag' title = \"))[\\s\\S]*(?=\")").matcher(string);
					while(matcher.find()){
					matcherTitle.find();
					arrayList.add(matcher.group().substring(3));
					titles.add(matcherTitle.group());
					String tag = "未添加标签";
					if(matcherLike.find()){
						tag = matcherLike.group();
					}
					t.add(tag);
					}
				}
				synchronized (tag) {
					tag.put(nowI, t);
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
		return i;
	}
	
	public void getAllLike(){
    	try {
    		FileWriter writer = new FileWriter(path + "/my_like.html");
    		writer.write("<html><head><meta charset=\"UTF-8\"></head>");
    		writer.write("<body>");
    		ArrayList<String> titles = new ArrayList<>();
    		ArrayList<String> tags = new ArrayList<>();
    		int I = getLike();
    		//System.out.println(readMa);
    		System.out.println(readMap.size() + " " + readMap);
    		int n = I;
			for(int i = 1; i < n + 1; i++){
				if(readMap.get(i) == null)
					System.err.println(i);
				urls.addAll(readMap.get(i));
				titles.addAll(mapTitle.get(i));
				tags.addAll(tag.get(i));
			}
			nowIndex = 0;
			totel = urls.size();
			bak2 = true;
			bakThreads(Bak.outMode);
			sleep();
			bak2 = false;
			System.out.println(readMap);
			System.out.println(urls.size() + " " + tags.size());
			writer.write("<font size=20px><strong><p>你的收藏</p></strong></font>");
			for(int i = 0; i < urls.size(); i++){
				writer.write("<div><font size:10px><p><a style=\"color:#808080\" href=\"tiezi/" + 
			urls.get(i) + ".html\">" + 
						titles.get(i) + "</a> &nbsp &nbsp &nbsp" + 
			tags.get(i) + "</p></font></div>");
			}
			writer.write("</body></html>");
			writer.close();
		} catch (Throwable e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}
