import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class BakBar extends BakFactory{
	public Map<Long, String> titles = new TreeMap<>((o1, o2)->{return (int)(o2 - o1);});
	public Map<String, Integer> pageMap = new HashMap<>();
	int max = -1;
	
	public BakBar(String path, String to, int totel) {
		super(path, to, totel);
		max = totel / 50;
		// TODO 自动生成的构造函数存根
	}
	
	public void getAllThread(){
		System.out.println("AAAAAAAAAAAAAAAAAAA" + (max + 1));
		for(int i = 0; i < totel; i+=50){
			int nowI = i;
			executorService.execute(new Runnable(){
				
				public void run() {
					System.out.println(nowI);
					String v = null;
					try {
						v = get(to + nowI);
						System.out.println(v);
					} catch (Throwable e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					}
					int nowIndex = 0;
					int prevIndex = 0;
					//System.out.println(v);
					while((nowIndex = v.indexOf("href=\"/p/", prevIndex)) != -1){
						String id = v.substring(nowIndex + 9, v.indexOf("\"", nowIndex + 7));
						synchronized (urls) {
							urls.add(id);
						}
						prevIndex = nowIndex + 1;
						int index = v.indexOf("title=\"", nowIndex);
						int index2 = v.indexOf("\"", index + 8);
						String title = v.substring(index + 7, index2);
						synchronized (titles) {
							titles.put(Long.parseLong(id), title);
						}
						synchronized (pageMap) {
							pageMap.put(id, nowI / 50 + 1);
						}
					}
					
		    	}
			});
		}
	}
	
	public void writeFeilds(){
		System.out.println(titles.size() + " " + titles);
		try {
			FileWriter writer = new FileWriter(path + "/帖子总id.txt");
			for(int i = 0; i < totel; i++){
				writer.write(i + " " + (i < urls.size() ? urls.get(i) : "null") + " ");
			}
			writer.close();
			writer = new FileWriter(path + "/贴吧总贴目录.html");
			writer.write("<html><head><meta charset=\"UTF-8\"></head>");
			writer.write("<body>");
			writer.write("<font size=20px><strong><p>贴吧帖子目录</p></strong></font>");
			for(Entry<Long, String> entry : titles.entrySet()){
				writer.write("<div><p><a style=\"color:#808080\" href=\"tiezi/" + entry.getKey() + ".html\">" + entry.getValue() + "</a></p></div>");
			}
			for(String str : pageMap.keySet()){
				System.err.println("LIST " + str + " " + pageMap.get(str));
			}
			writer.write("</body></html");
			writer.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public void init(){
		getAllThread();
		sleep();
		writeFeilds();
	}

	@Override
	public void run() throws Exception {
		super.run();
		for(int i = 0; i < urls.size(); i++){
			int I = i;
			executorService.execute(new Runnable() {
				public void run() {
					bak(urls.get(I), path, Bak.outMode);
				}
			});
		}
	}
}
