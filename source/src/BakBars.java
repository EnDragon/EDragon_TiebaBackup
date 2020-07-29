import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BakBars extends BakPersonal{

	volatile Map<Integer, ArrayList<String>> readMap = new HashMap<>();
	volatile Map<Integer, ArrayList<String[]>> bars = new HashMap<>();
	
	public BakBars(String path, String to, int totel, String cookie) {
		super(path, to, totel, cookie);
		// TODO 自动生成的构造函数存根
	}
	
	public void getBars(){
		getAll("http://tieba.baidu.com/f/like/mylike", "下一页&gt;", this, new TT() {
			
			@Override
			public void run(Object... objects) throws Throwable {
				String str = (String)objects[0];
				int nowI = (int)objects[2];
				ArrayList<String[]> t = new ArrayList<>();
				String[] strs = split(str, "<tr", "tr");
				for(int i2 = 1; i2 < strs.length; i2++){
					String string = strs[i2];
					String[] bar = new String[3];
					Matcher matcher = Pattern.compile("(?<=(title=\"))[^<]*?(?=\")").matcher(string);
					matcher.find();
					bar[0] = matcher.group();
					Matcher matcher2 = Pattern.compile("(?<=(<a class=\"cur_exp\")).*?(?=<)").matcher(string);
					matcher2.find();
					bar[1] = matcher2.group().replaceAll(".*?>", "");
					matcher.find();
					bar[2] = matcher.group();
					t.add(bar);
					//System.out.print(Arrays.toString(bar).getBytes("Unicode"));
				}
				synchronized (bars) {
					bars.put(nowI, t);
				}
			}
		}, "GBK");
	}
	
	public void run() throws Exception{
    	try{
		FileWriter writer = new FileWriter(path + "/my_bars.html");
		writer.write("<html><head><meta charset=\"UTF-8\"></head>");
		writer.write("<body>");
		ArrayList<String[]> bars = new ArrayList<>();
		getBars();
		System.out.println(this.bars.size() + " " + this.bars);
		int n = this.bars.size();
		for(int i = 1; i < n + 1; i++){
			if(this.bars.get(i) == null)
				System.err.println(i);
			bars.addAll(this.bars.get(i));
		}
		nowIndex = 0;
		totel = urls.size();
		writer.write("<font size=20px><strong><p>你的贴吧</p></strong></font>");
		for(int i = 0; i < bars.size(); i++){
			writer.write("<div><font size:10px><p>" + bars.get(i)[0] + "&nbsp;" +  bars.get(i)[1] + "&nbsp;" + bars.get(i)[2] + "</p></font></div>");
		}
		writer.write("</body></html>");
		writer.close();
    	}
    	catch(Throwable throwable){
    		throwable.printStackTrace();
    	}
	}

}
