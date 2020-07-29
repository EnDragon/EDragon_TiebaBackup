import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BakAt extends BakPersonal{

	volatile Map<Integer, ArrayList<String>> readMap = new HashMap<>();
	volatile Map<Integer, ArrayList<String>> mapTitle = new HashMap<>();
	volatile Map<Integer, ArrayList<String>> at = new HashMap<>();
	
	public BakAt(String path, String to, int totel, String cookie) {
		super(path, to, totel, cookie);
		// TODO 自动生成的构造函数存根
	}
	
	public void getAt(){
		getAll("http://tieba.baidu.com/i/i/atme", "下一页&gt;", this, new TT() {
			
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
					Matcher matcher = Pattern.compile("(?<=(target=\"_blank\">))[^<]*?(?=</a></div>)").matcher(string);
					Matcher matcher2 = Pattern.compile("(?<=(<a class=\"itb_thread\" href=\"/p/)).*?(?=\")").matcher(string);
					Matcher matcher3 = Pattern.compile("(?<=(title=\"回复：))[\\s\\S]*?(?=\")").matcher(string);
					while(matcher2.find()){
						matcher.find();
						matcher3.find();
						t.add(matcher.group());
						arrayList.add(matcher2.group().substring(0, matcher2.group().indexOf("?")));
						titles.add(matcher3.group());
					}
				}
				synchronized (at) {
					at.put(nowI, t);
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
	
	public void run() throws Exception{
    	try{
		FileWriter writer = new FileWriter(path + "/at_me.html");
		writer.write("<html><head><meta charset=\"UTF-8\"></head>");
		writer.write("<body>");
		ArrayList<String> titles = new ArrayList<>();
		getAt();
		System.out.println(readMap.size() + " " + readMap);
		ArrayList<String> at = new ArrayList<>();
		int n = readMap.size();
		for(int i = 1; i < n + 1; i++){
			if(readMap.get(i) == null)
				System.err.println(i);
			urls.addAll(readMap.get(i));
			titles.addAll(mapTitle.get(i));
			at.addAll(this.at.get(i));
		}
		nowIndex = 0;
		totel = urls.size();
		bak2 = true;
		bakThreads(Bak.outMode);
		sleep();
		bak2 = false;
		writer.write("<font size=20px><strong><p>你的At</p></strong></font>");
		for(int i = 0; i < urls.size(); i++){
			writer.write("<div><font size:10px><p>" + at.get(i) + "&nbsp@你&nbsp" + "<a style=\"color:#808080\" href=\"tiezi/" + urls.get(i) + ".html\">" + titles.get(i) + "</p></font></div>");
		}
		writer.write("</body></html>");
		writer.close();
    	}
    	catch(Throwable throwable){
    		throwable.printStackTrace();
    	}
	}

}
