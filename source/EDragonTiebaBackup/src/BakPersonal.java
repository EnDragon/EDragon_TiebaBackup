import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BakPersonal extends BakFactory{

	public BakPersonal(String path, String to, int totel, String cookie) {
		super(path, to, totel);
		this.cookie = cookie;
		// TODO 自动生成的构造函数存根
	}
	
    public static Map<String, String> getUsers(String str){
    	Matcher matcher = Pattern.compile("(?<=(homeUserName\":\"))[^\"]*(?=(\"))").matcher(str.replaceAll("\\s", ""));
    	Matcher matcher2 = Pattern.compile("(?<=(>吧龄:))[^<]*(?=<)").matcher(str);
    	Matcher matcher3 = Pattern.compile("(?<=(>发贴:))[^<]*(?=<)").matcher(str);
    	Matcher matcher4 = Pattern.compile("(?<=(<title>))[^<]*(?=的)").matcher(str);
    	Map<String, String> map = new HashMap<>();
    	if(!(matcher.find() && matcher2.find() && matcher3.find() && matcher4.find())){
        	map.put("username", "null");
        	map.put("age", "null");
        	map.put("tienum", "null");
        	map.put("nickname", "null");
        	System.err.println("ERROR: 获取用户主页失败");
    	}
    	else{
        	map.put("username", matcher.group());
        	map.put("age", matcher2.group());
        	map.put("tienum", matcher3.group());
        	map.put("nickname", matcher4.group());
    	}
    	return map;
    }

}
