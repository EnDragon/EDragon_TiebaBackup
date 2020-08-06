import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Tool {
	
	public static Map<String, String> translate(String... add){
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < add.length; i+=2){
			/*if(add[i].equals("Cookie") && add[i + 1].equals(""))
				continue;*/
			map.put(add[i], add[i + 1]);
		}
		return map;
	}
	
    public static String get(String s, Map<String, String> add){
    	System.out.println(s);
    	StringBuffer buffer = new StringBuffer("");
    	int i2 = 0;
    	while(i2 < 7)
    	try{
			URL url = new URL(s);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			//con.setRequestProperty("Content-Encoding", "gzip");
			//con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			//con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			//con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400");
			//System.out.println(add[0] + add[1]);
			Set<String> keySet = add.keySet();
			for(Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); iterator.next()){
				con.setRequestProperty(iterator.toString(), add.get(iterator));
			}
			/*con.setRequestProperty("Cookie", "TIEBA_USERTYPE=35e5640d10b2278caa4e8c9a; bdshare_firstime=1513953911714; PSTM=1521725322; BIDUPSID=79564A52C9572C301550935483204A3D; rpln_guide=1; IS_NEW_USER=7bc17a41aab0f3bb650d89a6; BAIDUID=62FEB50F1AB68F750C2EF0E0EC08BF02:FG=1; Hm_lvt_287705c8d9e2073d13275b18dbd746dc=1565965390,1566289340,1566480461,1566633150; pgv_pvi=7140703232; __cfduid=dc3505addfe74ea01ac6a3be09d2352111567316748; SEENKW=%E6%88%98%E5%9C%B05%23%E9%BE%99%23%E6%B5%B7%E8%B4%BC%E7%8E%8B%23%E7%88%B6%E6%AF%8D%23%E5%AE%89%E5%8D%93%E6%B8%B8%E6%88%8F; BAIDU_WISE_UID=wapp_1576849917403_558; CLIENTWIDTH=946; CLIENTHEIGHT=1920; SET_PB_IMAGE_WIDTH=848; TIEBAUID=72034c465bb3e9cd3be11771; BDUSS=zZzfi1sLW1PMFZuSU5EU0VYM2tpLWh2QXJlbHJ1M355SnJnWXZla1ppTHh-U1plRUFBQUFBJCQAAAAAAAAAAAEAAAD5accVc21qNTgzMDIwOTg0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPFw~13xcP9dS; STOKEN=4076d6abfe81ba2fe7f0f751197ec67651dae47b7ae6e8039f41e6cc84f4a080; Hm_lvt_98b9d8c2fd6608d564bf2ac2ae642948=1577020961,1577021648,1577021712,1577491051; ZD_ENTRY=sogou; wise_device=0; Hm_lpvt_98b9d8c2fd6608d564bf2ac2ae642948=1577491443");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400");*/
			InputStreamReader stream = new InputStreamReader(con.getInputStream());
			int c = 0;
			//byte[] bytes = new byte[1024];
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	/*System.out.println("AAA");//云里雾里的输出报告
    	System.out.println(buffer.length());*///不是什么太重要的，把这个划去
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){
			str = get(con.getHeaderField("Location"), add);
		}
    	
    	i2 = 7;
    	return str;
    	}
    	catch(Throwable throwable){
    		//throwable.printStackTrace();
    		i2++;
    		System.out.println(s + " " + i2);
    		if(i2 == 7){
    			System.err.println("未成功获取：" + s + " ");
    			throwable.printStackTrace();
    		}
    	}
    	
    	return "NULL";
    }
    
    public static String getMore(String s, Map<String, String> add) throws Exception    {
    	System.out.println(s);
    	StringBuffer buffer = new StringBuffer("");
    	int i2 = 0;
    	while(i2 < 7)
    	try{
			URL url = new URL(s);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			//con.setRequestProperty("Content-Encoding", "gzip");
			//con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			//con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			//con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400");
			Set<String> keySet = add.keySet();
			for(Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); iterator.next()){
				con.setRequestProperty(iterator.toString(), add.get(iterator));
			}
			/*con.setRequestProperty("Cookie", "TIEBA_USERTYPE=35e5640d10b2278caa4e8c9a; bdshare_firstime=1513953911714; PSTM=1521725322; BIDUPSID=79564A52C9572C301550935483204A3D; rpln_guide=1; IS_NEW_USER=7bc17a41aab0f3bb650d89a6; BAIDUID=62FEB50F1AB68F750C2EF0E0EC08BF02:FG=1; Hm_lvt_287705c8d9e2073d13275b18dbd746dc=1565965390,1566289340,1566480461,1566633150; pgv_pvi=7140703232; __cfduid=dc3505addfe74ea01ac6a3be09d2352111567316748; SEENKW=%E6%88%98%E5%9C%B05%23%E9%BE%99%23%E6%B5%B7%E8%B4%BC%E7%8E%8B%23%E7%88%B6%E6%AF%8D%23%E5%AE%89%E5%8D%93%E6%B8%B8%E6%88%8F; BAIDU_WISE_UID=wapp_1576849917403_558; CLIENTWIDTH=946; CLIENTHEIGHT=1920; SET_PB_IMAGE_WIDTH=848; TIEBAUID=72034c465bb3e9cd3be11771; BDUSS=zZzfi1sLW1PMFZuSU5EU0VYM2tpLWh2QXJlbHJ1M355SnJnWXZla1ppTHh-U1plRUFBQUFBJCQAAAAAAAAAAAEAAAD5accVc21qNTgzMDIwOTg0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPFw~13xcP9dS; STOKEN=4076d6abfe81ba2fe7f0f751197ec67651dae47b7ae6e8039f41e6cc84f4a080; Hm_lvt_98b9d8c2fd6608d564bf2ac2ae642948=1577020961,1577021648,1577021712,1577491051; ZD_ENTRY=sogou; wise_device=0; Hm_lpvt_98b9d8c2fd6608d564bf2ac2ae642948=1577491443");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3741.400 QQBrowser/10.5.3863.400");*/
			InputStreamReader stream = new InputStreamReader(con.getInputStream(), "GBK");
			int c = 0;
			//byte[] bytes = new byte[1024];
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	/*System.out.println("AAA");//云里雾里的输出报告
    	System.out.println(buffer.length());*///不是什么太重要的，把这个划去
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){
			str = get(con.getHeaderField("Location"), add);
		}
    	
    	i2 = 7;
    	return str;
    	}
    	catch(Throwable throwable){
    		//throwable.printStackTrace();
    		i2++;
    		System.out.println(s + " " + i2);
    		if(i2 == 7){
    			System.err.println("未成功获取：" + s + " ");
    			throwable.printStackTrace();
    		}
    	}
    	
    	return "NULL";
    	
    	
    }
    
    public static void download(String s, String path, Map<String, String> add) throws Throwable{
    	FileOutputStream os = new FileOutputStream(path);
    	int i2 = 0;
    	while(i2 < 3){
    	try{
		URL url = new URL((s.indexOf("http") == -1 ? "https:" : "") + s);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		Set<String> keySet = add.keySet();
		for(Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); iterator.next()){
			con.setRequestProperty(iterator.toString(), add.get(iterator));
		}
		InputStream stream = con.getInputStream();
		int c = 0;
		byte[] bytes = new byte[1024 * 1024];
		//byte[] bytes = new byte[1024];
		while((c = stream.read(bytes)) > 0){
			os.write(bytes, 0, c);
		}
		stream.close();
		i2 = 5;
    	}catch(Throwable throwable){
    		i2++;
    		if(i2 == 3){
    			System.err.println("未下载：" + s + " 路径会下载到：" + path);
    		}
    	}
    	}
		os.close();
}
}
