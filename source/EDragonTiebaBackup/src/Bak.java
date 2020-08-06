
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.sun.corba.se.spi.orb.StringPair;
import com.sun.java_cup.internal.runtime.virtual_parse_stack;
import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.swing.internal.plaf.basic.resources.basic;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Bak {

	/**
	 * 本程序是一个备份贴吧用的爬虫，他可以从网上下载整个吧的帖子，也可以备份个人的帖子，个人的帖子有你的主题帖，你的回复，你的收藏，你的贴吧，你的被@的，你的粉丝，你的关注，你的举报，你的投诉
	 * 也可以备份多个很多的散开的贴号的帖子
	 * 
	 * 本程序禁止用于非法用途
	 * 
	 * 入口函数在Bak.java里
	 * 
	 * 爬虫使用方法：
	 * 		https://shimo.im/docs/4b141d04a1b84545/
	 * 
	 * 贴吧最近好像又加类安全验证了，防止高频访问流量，如果出现这样的情况，那么可以先断开路由器再连接路由器，这样IP地址会变
	 * 但是，备份时请将你想要备份的东西先想好了，因为不止可能会出现安全认证，而且这种高频访问流量会给服务器带来压力
	 * 所以，请合理使用这个爬虫
	 * 
	 * 因为本人编写这个代码的时候是一点一点加上去的，且代码编写跨度长达一年，所以代码很乱
	 * 
	 * V0.8.7更新内容
	 * 		增加乱帖子工具，可以进行帖子抽取，帖子格式转换等等，但是并没有完成
	 * 		增加乱多种格式的输出(TXT格式还没有测试过)
	 * 			HTML格式  ---网页格式
	 * 			JSON格式  ---JSON格式，你可以编写代码获取已经备份下来的json中的几楼几楼的内容
	 * 			TXT格式   ---纯文本格式，请注意，转换成TXT格式不可逆
	 * 使用：点击"打开我运行Gui.bat"打开这个程序的GUI
	 * 
	 * 文件解释：
	 *		Bak.java --入口文件以及命令解释
	 *		BakBar.java --备份贴吧用的类
	 *		BakPersonThreads.java --备份个人主题帖用的类
	 *		BakPersonReply.java --备份个人回复用的类
	 *		BakLike.java --备份个人收藏所用的类
	 *		BakAt.java --备份个人被@所用的类
	 *		BakBars.java --备份你的贴吧所用的类
	 *		BakFans.java --备份个人粉丝所用的类
	 *		BakJuBao.java --备份个人举报所用的类
	 *		BakCompain.java --备份个人投诉所用的类
	 *		Floor.java --楼层类
	 *		Gui.java --GUI界面
	 *		EDTBTool --帖子工具(未完成)
	 *
	 *这个备份爬虫从一开始的只为暂时备份一个贴吧(那时候还是纯文本)，发展到类有比较漂亮的备份文件(HTML)格式，然后又发展到类GUI界面，最后又发展处类多种格式(HTML, JSON, TXT，后面还打算加PDF)以及格式转换(未完成)和工具(未完成)
	 *因为一开始我没有计划发展这么多，所以架构并不怎么太好
	 *并且一开始代码堆在一起，知道最近才把他们拆成类这么多类文件，而且我又把以前分开的文件整合在类一起，代码显得很混乱
	 *一年前我的代码并不怎么样，现在任然有很多那时代码的残余
	 *
	 *本人是个学生党
	 * 
	 * */

	static String path = null;
	/**这是备份贴吧的网址，&pn=必须要有*/
	static String to = null;
	static String cookie = "";
	static int totelThreadNum = 40;
	static Map<String, String> valueMap = new HashMap<>();
	public static String outMode = "HTML";
	
	public static final String version = "0.8.8";
	
	public static void update(String[] args){
    	if(valueMap.get("-noCheck") == null)
    	try {
    		System.out.println(Bak.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    		String path = URLDecoder.decode(Bak.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1), "UTF-8");
			FileInputStream stream = new FileInputStream(path);
			byte[] bytes = new byte[1024];
			int c = 0;
			StringBuffer buffer = new StringBuffer("");
			while((c = stream.read(bytes)) > 0) {
				buffer.append(new String(bytes, 0, c));
			}
			stream.close();
			System.out.println(new File(Bak.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ")).canRead());
			byte[] md55 = MessageDigest.getInstance("MD5").digest(buffer.toString().getBytes());
			String logos = "logos/logo.log";
			if(new File(logos).exists()){
				FileReader inputStream = new FileReader(logos);
				int chars;
				String buffer2 = "";
				while((chars = inputStream.read()) != -1){
					buffer2 += (char)chars;
				}
				System.out.println(buffer2);
				String string = buffer2.toString();
				JSONObject jsonObject = JSONObject.fromObject(string);
				inputStream.close();
				Date time = new Date();
				if(time.getTime() < jsonObject.getLong("time") + 60 * 60 * 1000){
					return;
				}
			}
			System.out.println("正在检查更新");
			FileOutputStream outputStream = new FileOutputStream(logos);
			JSONObject jsona = new JSONObject();
			jsona.put("time", new Date().getTime());
			outputStream.write(jsona.toString().getBytes());
			outputStream.close();
			String str = get("https://api.github.com/repos/EnDragon/TieBaBak/contents/update");
			//System.out.println(str);
			JSONObject jsonObject2 = JSONObject.fromObject(str);
			String content = jsonObject2.getString("content");
			String str2 = "";
			String[] strs = content.split("\\n");
			for(int i = 0; i < strs.length; i++){
				str2 += new String(Base64.getDecoder().decode(strs[i]));
			}
			//System.out.println(str2);
			JSONObject json = JSONObject.fromObject(str2);
    		if(valueMap.get("-noCheck") == null){
    		JSONObject a = json.getJSONObject("message");
    		Object[] set = a.keySet().toArray();
    		System.out.println("\r\n[公告]:");
    		for(int i = 0; i < set.length; i++){
    			if(("V" + version).matches((String)set[i])){
    				System.out.println("对于" + set[i] + "[V" + version + "]的公告：\r\n" + a.get(set[i]));
    			}
    		}
			String MD5 = json.getJSONObject("MD5").getString("V" + version);
			String md5 = "";
			for(int i = 0; i < 16; i++) {
				String sss = Integer.toString(Byte.toUnsignedInt(md55[i]), 16);
				md5 += sss.length() == 1 ? "0" + sss : sss;
			}
			System.out.println("\r\n");
			System.out.println(md5);
			if(!new String(md5).equals(MD5)) {
				/*System.out.println("警告：文件不完整，可能是因为别人篡改或者文件发生损坏，完整文件请至：\r\nhttps://github.com/EnDragon/EDragon_TiebaBackup");
				System.exit(0);*/
			}
			//System.out.println("验证完成");
    		}
			String ver = json.getString("lastest");
			String version = ver.substring(1);
			String nowVersion = version;
			String[] versions = version.split("\\.");
			String[] nowVersions = nowVersion.split("\\.");
			boolean b = false;
			int len = nowVersions.length > versions.length ? nowVersions.length : versions.length;
			for(int i = 0; i < len; i++) {
				int nv = i >= nowVersions.length ? 0 : Integer.parseInt(nowVersions[i]);
				int v = i >= versions.length ? 0 : Integer.parseInt(versions[i]);
				if(nv < v) {
					b = true;
					break;
				}
			}
			if(b) {
				JSONObject jsonObject = json.getJSONObject("update").getJSONObject("V" + version);
				JSONArray update = jsonObject.getJSONArray("mode");
				System.out.println("发现新版本，最新版本" + ver + "更新内容：" + jsonObject.getString("updateContent"));
				if(update.indexOf("ask") != -1){
					System.out.println("你确定要更新？（y表示是，n表示否）");
					a : while(true){
					char chars = (char)System.in.read();
					switch(chars){
					case '\r':
					case '\n':
						break;
					case 'n':
						System.out.println("已跳过更新");
						return;
					case 'y':
						break a;
					default:
						System.out.println("请输入y或n");
					}
					}
				}
				if(update.indexOf("mark") != -1){
					return;
				}
				if(update.indexOf("all") != -1){
					
				}
				if(update.indexOf("download") != -1){
			    	String string = "";
			    	for(int i = 0; i < args.length; i++){
			    		string += args[i].replaceAll("\\|", "||") + (i == args.length - 1 ? "" : "|");
			    	}
					Process process = Runtime.getRuntime().exec("cmd /c start java -jar update.jar \"" + jsonObject.getString("url") + "\" \"" + string + "\"");
					//System.out.write(process.get);
					System.exit(0);
				}
				if(update.indexOf("open") != -1){
					Runtime.getRuntime().exec("cmd /c start " + jsonObject.getString("url"));
				}
			}
		} catch (Throwable e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
    	else
    		System.out.println("已跳过检查");
	}
	
	public static boolean DEBUG = true;
	
	public static void debug(){
		//EDTBTool.path = "F:\\Western Dragon Bak 20200729 3\\tiezi";
		//System.out.println(Tool.get("https://tieba.baidu.com/p/6830257920?pn=2", new HashMap<>()));
		BakFactory bakFactory = new BakFactory("F:\\testT", "", 1);
		//bakFactory.bak("4001044315", "F:\\testT", "HTML");
		bakFactory.sleep();
		bakFactory.shutdown();
		//EDTBTool.main(new String[]{"search", "floor", "time=2020-07-29.*", "F:\\seatchResult"});
		//EDTBTool.main(new String[]{"draw", "-outFile", "F:\\seatchResult", "F:\\bakaaa"});
		//EDTBTool.main(new String[]{"translate", "HTML", EDTBTool.path});
		/*try {
			Gui.main(new String[]{});
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
	}
	
    public static void main(String[] args){
    	
    	//args = new String[]{"bak",  "F:\\bak_20200722" ,"4001044315"/*, "6220722353", "6837050684"*/, "-totelThreadNum", "40"};
    	/*if(DEBUG){
    		debug();
    		return;
    	}*/
    	
    	if(args[0].equals("Gui")){
    		try {
    			String[] strings = new String[args.length - 1];
    			for(int i = 0; i < strings.length; i++){
    				strings[i] = args[i + 1];
    			}
				Gui.main(strings);
				return;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
    	}
    	EDTBTool.main(args);
    	/*path = "F:\\testtest";
    	to = "https://tieba.baidu.com/p/5988646587";
    	BakFactory bakFactory2 = new BakFactory(path, "", 1);
    	bakFactory2.bak("5988646587", path);*/
    	//分割指令
    	for(int i2 = 0; i2 < args.length; ){
    		if(args[i2].indexOf("-") == 0){
    			String str = i2 + 1 < args.length ? args[i2 + 1] : "";
    			if(str.indexOf("-") == 0) {
    				valueMap.put(args[i2], "");
    				i2++;
    				continue;
    			}
    			valueMap.put(args[i2], str);
    		}
    		i2++;
    	}
    	
    	//检查更新
    	update(args);
    	
    	//修改最大允许的显成熟
    	if(valueMap.get("-totelThreadNum") != null)
    		totelThreadNum = Integer.parseInt(valueMap.get("-totelThreadNum"));
    	
    	//输出的作者信息
    	System.out.println("/////////////////////////\r\n/E龙贴吧备份器V" + version + "    /\r\n/      By EnDragon\r\n/\r\n/////////////////////////源码地址：https://github.com/EnDragon/EDragon_TiebaBackup");
    	System.out.println(args[1]);
    	
    	//path这个变量是历史保留下来的，我没有修改
    	path = args[2];
    	if(args[0].equals("bak"))
    		path = args[1];
    	File file = new File(path + "\\tiezi");
    	file.mkdirs();
    	File f = new File(path + "\\pic");
    	f.mkdirs();
    	new File(path + "\\err").mkdirs();
    	if(valueMap.get("-noFans") == null || valueMap.get("-noConcerns") == null){
    		new File(path + "\\users").mkdirs();
    	}
    	//threads.start();
    	try {
    		//重定向错误输出流
			System.setErr(new PrintStream(new FileOutputStream(path + "/out.txt")));
		} catch (FileNotFoundException e2) {
			// TODO 自动生成的 catch 块
			e2.printStackTrace();
			return;
		}
		if(args[0].equals("bak"))path = args[1];
    	
    	System.out.println(Arrays.toString(args));
    	
    	if(valueMap.get("-outputMode") != null){
    		outMode = valueMap.get("-outputMode");
    	}
    	//运行指令
    	if(args[0].equals("bakBar")){
    		to = args[1] + "&pn=";
    		BakBar bakBarB = new BakBar(path, to, Integer.parseInt(args[3]));
    		bakBarB.getAllThread();
    		bakBarB.sleep();
    		bakBarB.writeFeilds();
    		try {
				bakBarB.run();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
    		bakBarB.sleep();
    		bakBarB.shutdown();
    	}
    	else if(args[0].equals("bakPerson")){
    		cookie = args[1];
    		try {//try{...}catch(){...}是历史保留下来的，我一点一点加也没有考虑整体架构
    			if(valueMap.get("-noThread") == null) {
    				System.out.println("正咋备份：所有主题帖");
    				BakPersonsThreads bakPerson = new BakPersonsThreads(path, "", 1, cookie);
    				bakPerson.getMainPageAndThreads();
    				bakPerson.sleep();
    				bakPerson.bakThreads(outMode);
    				bakPerson.sleep();
    				bakPerson.shutdown();;
    			}
    			try{
    			if(valueMap.get("-noReply") == null) {
    				System.out.println("正咋备份：所有回复");
    				BakPersonsReply bakPerssionReply = new BakPersonsReply(path, "", 1, cookie);
    				bakPerssionReply.getAllPersionReply();
    				bakPerssionReply.sleep();
    				bakPerssionReply.shutdown();
    			}
    			}
    			catch (Exception e) {
					System.err.println("-noReply error");
					e.printStackTrace();
				}
    			try{
    			if(valueMap.get("-noLike") == null){
    				System.out.println("正咋备份：所有收藏");
    				BakLike bakLike = new BakLike(path, "", 1, cookie);
    				bakLike.getAllLike();
    				bakLike.sleep();
    				bakLike.shutdown();
    			}
    			}
    			catch (Exception e) {
					System.err.println("-noLike error");
					e.printStackTrace();
				}
    			try{
    			if(valueMap.get("-noAt") == null){
    				System.out.println("正咋备份：所有@");
    				BakAt bakAt = new BakAt(path, "", 1, cookie);
    				bakAt.run();
    				bakAt.sleep();
    				bakAt.shutdown();
    			}
    			}
    			catch (Exception e) {
					System.err.println("-noAt error");
					e.printStackTrace();
				}
    			try{
    			if(valueMap.get("-noBars") == null){
    				System.out.println("正咋备份：所有我的贴吧");
    				BakBars bakBars = new BakBars(path, "", 1, cookie);
    				bakBars.run();
    				bakBars.sleep();
    				bakBars.shutdown();
    			}
    			}
    			catch (Exception e) {
					System.err.println("-noBars error");
					e.printStackTrace();
				}
    			try{
    			if(valueMap.get("-noFans") == null){
    				System.out.println("正咋备份：所有粉丝");
    				BakFans bakFans = new BakFans(path, "", 1, "/my_fans.html", "http://tieba.baidu.com/i/i/fans", "你的粉丝", "fan", cookie);
    		    	bakFans.run();
    		    	bakFans.sleep();
    		    	bakFans.shutdown();
    			}
    			}
    			catch (Exception e) {
					System.err.println("-noFans error");
					e.printStackTrace();
				}
    			try{
    			if(valueMap.get("-noConcerns") == null){
    				System.out.println("正咋备份：所有关注");
    				BakFans bakFans = new BakFans(path, "", 1, "/my_concern.html", "http://tieba.baidu.com/i/i/concern", "你的关注", "concern", cookie);
    		    	bakFans.run();
    		    	bakFans.sleep();
    		    	bakFans.shutdown();
    			}
    			}
    			catch (Exception e) {
					System.err.println("-noConcerns error");
					e.printStackTrace();
				}
    			if(valueMap.get("-noReport") == null){
    				BakJuBao bakJuBao = new BakJuBao(path, "", 1, cookie);
    				bakJuBao.run();
    				bakJuBao.sleep();
    				bakJuBao.shutdown();
    			}
    			if(valueMap.get("-noCompain") == null){
    				BakCompain bakCompain = new BakCompain(path, "", 1, cookie);
    				bakCompain.run();
    				bakCompain.sleep();
    				bakCompain.shutdown();
    			}
			} catch (Throwable e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
    	}
    	else if(args[0].equals("bak")){
    		path = args[1];
    		BakFactory bakFactory = new BakFactory(path, "", args.length - 4);
    		bakFactory.totel = args.length - 4;
    		for(int i = 2; i < args.length - 2; i++){
    			bakFactory.urls.add(args[i]);
    		}
			bakFactory.bakThreads(outMode);
			bakFactory.sleep();
			bakFactory.shutdown();
    	}
    	
    	System.out.println("程序退出");
    	
    }
    
    //获取网页代码
    public static String get(String s, String...add) throws Exception    {
    	System.out.println(s);
    	StringBuffer buffer = new StringBuffer("");
    	int i2 = 0;
    	while(i2 < 7)//最多获取7次
    	try{
			URL url = new URL(s);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			for(int i = 0; i < add.length; i+=2){
				con.setRequestProperty(add[i], add[i + 1]);
			}
			InputStreamReader stream = new InputStreamReader(con.getInputStream());
			int c = 0;
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){//解决重定向
			str = get(con.getHeaderField("Location"), "Cookie", cookie);
		}
    	
    	i2 = 7;
    	return str;
    	}
    	catch(Exception throwable){
    		//throwable.printStackTrace();
    		i2++;
    		System.out.println(s + " " + i2);
    		if(i2 == 7){
    			System.err.println("未成功获取：" + s + " ");
    			throwable.printStackTrace();
    		}
    	}
    	
    	return null;
    	
    	
    }
    
  //用到的提交数据
    public static String post(String s, String[] add, String file){
    	System.out.println(s);
    	StringBuffer buffer = new StringBuffer("");
    	int i2 = 0;
    	while(i2 < 7)
    	try{
			URL url = new URL(s);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			for(int i = 0; i < add.length; i+=2){
				con.setRequestProperty(add[i], add[i + 1]);
			}
			con.setDoOutput(true);
			OutputStream outputStream = con.getOutputStream();
			outputStream.write(file.getBytes());
			outputStream.close();
			InputStreamReader stream = new InputStreamReader(con.getInputStream());
			int c = 0;
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){
			str = get(con.getHeaderField("Location"), "Cookie", cookie);
		}
    	
    	i2 = 7;
    	return str;
    	}
    	catch(Throwable throwable){
    		i2++;
    		System.out.println(s + " " + i2);
    		if(i2 == 7){
    			System.err.println("未成功获取：" + s + " ");
    			throwable.printStackTrace();
    		}
    	}
    	
    	return null;
    }
    
    public static String getMore(String s, String...add) throws Exception    {
    	System.out.println(s);
    	StringBuffer buffer = new StringBuffer("");
    	int i2 = 0;
    	while(i2 < 7)
    	try{
			URL url = new URL(s);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			for(int i = 0; i < add.length; i+=2){
				con.setRequestProperty(add[i], add[i + 1]);
			}
			InputStreamReader stream = new InputStreamReader(con.getInputStream(), "GBK");
			int c = 0;
			while((c = stream.read()) > -1){
				buffer.append((char)c);
			}
			stream.close();
    	
    	System.out.println("AAA");//云里雾里的输出报告
    	System.out.println(buffer.length());//不是什么太重要的，把这个划去
    	
    	char[] chars = new char[buffer.length()];
    	buffer.getChars(0, buffer.length(), chars, 0);
    	String str = new String(chars);
    	
		if(con.getResponseCode() == 301){
			str = get(con.getHeaderField("Location"), "Cookie", cookie);
		}
    	
    	i2 = 7;
    	return str;
    	}
    	catch(Exception throwable){
    		i2++;
    		System.out.println(s + " " + i2);
    		if(i2 == 7){
    			System.err.println("未成功获取：" + s + " ");
    			throwable.printStackTrace();
    		}
    	}
    	
    	return null;
    	
    	
    }
    
    public static void download(String s, String path, String... add) throws Exception{
        	FileOutputStream os = new FileOutputStream(path);
        	int i2 = 0;
        	while(i2 < 3){
        	try{
			URL url = new URL((s.indexOf("http") == -1 ? "https:" : "") + s);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			for(int i = 0; i < add.length; i+=2){
				con.setRequestProperty(add[i], add[i+1]);
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
        	}catch(Exception throwable){
        		i2++;
        		if(i2 == 3){
        			System.err.println("未下载：" + s + " 路径会下载到：" + path);
        		}
        	}
        	}
			os.close();
    }
}

