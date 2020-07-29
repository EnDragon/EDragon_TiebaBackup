import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BakJuBao extends BakPersonal{

	public BakJuBao(String path, String to, int totel, String cookie) {
		super(path, to, totel, cookie);
		// TODO 自动生成的构造函数存根
	}
	
	public void run() throws Exception{
    	try{
    	FileWriter writer = new FileWriter(path + "/my_1.html");
		writer.write("<html><head><meta charset=\"UTF-8\"></head>");
		writer.write("<body>");
		writer.write("<font size=20px><strong><p>你的举报</p></strong></font><br>");
		writer.write("<font size:\"30px\"><strong>未完成的记录：</strong></font><br>");
		bakJuBao(2, writer);
		writer.write("<br><font size:\"30px\"><strong>已完成的记录：</strong></font><br>");
		bakJuBao(1, writer);
    	writer.close();
    	}
    	catch(Throwable throwable){
    		throwable.printStackTrace();
    	}
	}
	
    public void bakJuBao(int s, FileWriter writer){
    	int i = 1;
    	/*ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();
    	threadLocal.set(true);*/
    	ExecutorService executorService = Executors.newFixedThreadPool(Bak.totelThreadNum);
    	Map<String, Boolean> threadLocal = new HashMap<>();
    	threadLocal.put("threadLocal", true);
    	try{
		Map<Integer, String> map = new HashMap<>();
		boolean bool = true;
    	while(threadLocal.get("threadLocal")){
    		int i2 = i;
    		try{
    			executorService.execute(new Runnable() {
					
					@Override
					public void run() {
						try{
						String str = get("http://tieba.baidu.com/pmc/listjubao?status=" + s + "&currpage=" + i2, "Cookie", cookie);
						JSONObject json = JSONObject.fromObject(str);
						JSONArray array = json.getJSONObject("data").getJSONArray("reportList");
						System.out.println(str);
						synchronized (map) {
							String strs = "";
							for(int i2 = 0; i2 < array.size(); i2++){
								JSONObject jsonObject = array.getJSONObject(i2);
								String reportNum = jsonObject.getString("reportNum");
								String reportTypeNum = jsonObject.getString("reportTypeNum");
								Map<String, Boolean> booleans = new HashMap<>();
								Map<String, String> strings = new HashMap<>();
								booleans.put("local", false);
								new Thread(new Runnable() {
									@Override
									public void run() {
										try{
										String str = getMore("http://tieba.baidu.com/pmc/detailjubao?type=" +reportTypeNum + "&reportid=" + reportNum, "Cookie", cookie);
										booleans.put("local", true);
										strings.put("str", str);
										}
										catch(Throwable throwable){
											throwable.printStackTrace();
										}
									}
								}).start();
								while(!booleans.get("local"));
								Matcher matcher = Pattern.compile("(?<=<div class=\"acc_de_box1_tips\">)[\\s\\S]*?(?=</div>)").matcher(strings.get("str"));
								Matcher matcherUser = Pattern.compile("(?<=<div class=\"acc_user_name\" title=\")[\\s\\S]*?(?=\")").matcher(strings.get("str"));
								matcher.find();
								matcherUser.find();
								String user = matcherUser.group();
								String acc = matcher.group();
								String string = user + "&nbsp;" + jsonObject.getString("reportReason") + "&nbsp;" + jsonObject.getString("reportType") + "&nbsp;" + jsonObject.getString("complaintTime") + "&nbsp;" + acc;
								strs += string + "<br>";
							}
							map.put(i2, strs);
						}
						//System.out.println(!(str.indexOf("\"reportList\":[]") != -1));
						threadLocal.put("threadLocal", threadLocal.get("threadLocal") & !(array.size() == 0));
						}
						catch(Throwable throwable){
							throwable.printStackTrace();
						}
					}
				});
    		}
    		catch(Throwable throwable){
    			throwable.printStackTrace();
    		}
    		while(((ThreadPoolExecutor)executorService).getActiveCount() == Bak.totelThreadNum);
    		i++;
    	}
    	while(((ThreadPoolExecutor)executorService).getActiveCount() != 0){
    		System.out.println(((ThreadPoolExecutor)executorService).getActiveCount());
    		Thread.sleep(5000);
    	}
    	executorService.shutdown();
    	for(int i2 = 1; i2 < map.size() + 1; i2++){
    		writer.write(map.get(i2));
    	}
    	}
    	catch(Throwable throwable){
    		throwable.printStackTrace();
    	}
    }

}
