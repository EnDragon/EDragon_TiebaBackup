import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BakFactoryNew {
	Map<String, String> requestHeader = new HashMap<String, String>();
	volatile ThreadPoolExecutor executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(Bak.totelThreadNum);
	
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
}
