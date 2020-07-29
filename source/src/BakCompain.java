import java.io.FileWriter;
import java.io.IOException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BakCompain extends BakPersonal{

	public BakCompain(String path, String to, int totel, String cookie) {
		super(path, to, totel, cookie);
		// TODO 自动生成的构造函数存根
	}
	
	public void run() throws Exception{
    	try{
    	FileWriter writer = new FileWriter(path + "/my_2.html");
		writer.write("<html><head><meta charset=\"UTF-8\"></head>");
		writer.write("<body>");
		writer.write("<font size=20px><strong><p>你的投诉</p></strong></font><br>");
		writer.write("<font size:\"30px\"><strong>未完成的记录：</strong></font><br>");
		bakTouSu("undo", writer);
		writer.write("<br><font size:\"30px\"><strong>已完成的记录：</strong></font><br>");
		bakTouSu("done", writer);
		writer.close();
    	}
    	catch(Throwable throwable){
    		throwable.printStackTrace();
    	}
	}
	
    public void bakTouSu(String status, FileWriter writer){
    	int i2 = 1;
    	try{
    	writer.write("<table border=\"1\"><tr><td>被投诉人</td><td>被投诉人的吧</td><td>投诉类型</td><td>投诉理由</td><td>投诉时间</td><td>投诉结果</td><td>审核人</td><td>remark（我也不知道这是什么）</td><td>投诉处理时间</td></tr>");
    	while(true){
    	String str = post("http://tieba.baidu.com/pmc/tousu/listTousu", new String[]{"Cookie", cookie, "content-type", "application/x-www-form-urlencoded; charset=UTF-8"}, "status=" + status + "&page=" + i2 + "");
    	if(str.indexOf("\"list\":[]") != -1){
        	writer.write("</table>");
    		return;
    	}
    	JSONObject json = JSONObject.fromObject(str);
    	JSONArray array = json.getJSONObject("data").getJSONArray("list");
    	for(int i = 0; i < array.size(); i++){
    		JSONObject jsonObject = array.getJSONObject(i);
    		try {
				writer.write("<tr><td>" + jsonObject.getString("manager_uname") + "</td><td>" + jsonObject.getString("forum_name") + "</td><td>" + jsonObject.getString("complaint_type") + "</td><td>" + jsonObject.getString("complaint_reason").replaceAll("\n", "<br>") + "</td><td>" + jsonObject.getString("op_time") + "</td><td>" + jsonObject.getString("op_reason") + "</td><td>" + jsonObject.getString("op_uname") + "</td><td>" + jsonObject.getString("remark") + "</td><td>" + jsonObject.getString("op_time") + "</td></tr>");
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
    	}
    	i2++;
    	}
    	}
    	catch(Throwable throwable){
    		throwable.printStackTrace();
    	}
    }

}
