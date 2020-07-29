import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EDTBTool {
	public static String path = "";
	public static void read(String s, String outputMode){
		
	}
	
	public static class List{
		String list;
		
		public List() {
			// TODO 自动生成的构造函数存根
		}
	}
	
	public static Floor get(String list){
		System.out.println(list);
		String[] path = list.split("/");
		Floor floor = null;
		try {
			floor = Floor.fromString(file(EDTBTool.path + "/" + path[0]), "JSON");
		for(int i = 1; i < path.length; i++){
			if(floor.a == null){
				floor = floor.getFromFloor(Integer.parseInt(path[i]));
				continue;
			}
			return floor.get(i);
		}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return floor;
	}
	
	public static String file(String file) throws Exception{
		FileReader reader = new FileReader(file);
		StringBuffer buffer = new StringBuffer("");
		char[] bytes = new char[1024];
		int l = 0;
		while((l = reader.read(bytes)) > 0){
			buffer.append(new String(bytes, 0, l));
		}
		reader.close();
		return buffer.toString();
	}
	
	public static boolean matcher(String str, String pattern){
		Matcher matcher = Pattern.compile("^" + pattern + "$").matcher(str);
		return matcher.find();
	}
	
	public static boolean check(Floor floor, String pattern){
		String[] patterns = pattern.split("\\|\\|");
		boolean b = false;
		for(int i = 0; i < pattern.length(); i++){
			String string = patterns[i];
			String tag = string.split("=")[0];
			String p = string.split("=")[1];
			switch (tag) {
			case "username":
				return matcher(floor.username, p);
			case "nickname":
				return matcher(floor.user, p);
			case "floor":
				return matcher((floor.num + ""), p);
			case "content":
				return floor.content.split(p).length == 1;
			case "time":
				return matcher(floor.time, p);
			}
		}
		return false;
	}
	
	public static String[] search(String mode, String pattern){
		ArrayList<String> arrayList = new ArrayList<>();
		String[] strings = new File(path).list();
		for(int i = 0; i < strings.length; i++){
			String str = strings[i];
			Floor floor = get(str);
			if(mode.equals("title")){
				if(check(floor, pattern)){
					arrayList.add(str);
				}
			}
			if(mode.equals("floor")){
				for (Floor floor2 : floor.floors.values()) {
					if(check(floor2, pattern)){
						arrayList.add(str + "/" + floor2.num);
					}
				}
			}
			if(mode.equals("floor2")){
				for (Floor floor2 : floor.floors.values()) {
					//System.out.println("value");
					for(int ii = 0; ii < floor2.floors.size(); ii++){
						Floor floor3 = floor2.floors.get(ii + 1);
						//System.out.println(floor2.num + " " + ii + floor3.content + " " + floor2.floors.size());
						if(check(floor3, pattern)){
							arrayList.add(str + "/" + floor2.num + "/" + ii);
						}
					}
				}
			}
		}
		
		String[] result = new String[arrayList.size()];
		for(int i = 0; i < arrayList.size(); i++){
			result[i] = arrayList.get(i);
		}
		return result;
	}
	
	public static int getIndex(String string, String indexString, int i){
		int index = string.indexOf(indexString, i);
		return index == -1 ? string.length() : index;
	}
	
	public static final int CREATE_NEW_FILE = 1;
	public static final int OUTPUT_TO_A_FILE = 2;
	
	public static class Draw{
		public String run(String path){
			return path;
		}
	}
	
	public static void copyFile(String path, String toPath) throws IOException{
		System.out.println(path);
		FileInputStream inputStream = new FileInputStream(path);
		FileOutputStream outputStream = new FileOutputStream(toPath);
		int l;
		byte[] bytes = new byte[1024];
		while((l = inputStream.read(bytes)) > 0){
			outputStream.write(bytes, 0, l);
		}
		inputStream.close();
		outputStream.close();
	}
	
	public static void copy(String string, String path){
		Matcher matcher = Pattern.compile("<(img|embed).[^>]*>").matcher(string);
		while(matcher.find()){
			String stri = matcher.group();
			Matcher matcher2 = Pattern.compile("(?<=(src=\")).*(?=\")").matcher(stri);
			matcher2.find();
			try {
				copyFile(EDTBTool.path + "/" + matcher2.group(), path + "/" + matcher2.group().substring(matcher2.group().lastIndexOf("/")));
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
		}
	}
	
	public static void makeFiles(String path){
		new File(path + "/tiezi").mkdirs();
		new File(path + "/pic").mkdirs();
		new File(path + "/users").mkdirs();
	}
	
	public static void copyAll(Floor floor, String path){
		copy(floor.content, path);
		if(floor.a.a == null)
			copy(floor.user, path);
	}
	
	public static void draw(String mode, String pattern, int outMode, String path, Draw... draw){
		if(draw.length == 0){
			draw[0] = new Draw();
		}
		String[] list = search(mode, pattern);
		if(outMode == CREATE_NEW_FILE){
			
		}
	}
	
	/*public static String getPattern(String tag){
		
	}*/
	
	public static String match(String string, String tag){
		Matcher matcher = Pattern.compile("<!--" + tag + "-->.*?<!--" + "/-->").matcher(string);
		matcher.find();
		return matcher.group().replaceAll("<.*?>", "");
	}
	
	/*public static JSONObject translateToJson(String string){
		
	}*/
	
	public static void main(String[] args) {
		if(args[0].equals("draw")){
			makeFiles(args[3]);
			try {
			String string = file(args[2]);
			String[] strs = string.split("(\r|\n){1,}");
			if(args[1].equals("-copyFile")){
					for(String str : strs){
						System.out.println(str);
						int index = str.indexOf("/");
						String name = str.substring(0, index == -1 ? str.length() : index);
						copyFile(path + "/" + name, args[3] + "/tiezi/" + name);
						copyAll(get(str), args[3] + "/pic");
					}
			}
			else if(args[1].equals("-outFile")){
				JSONArray s = new JSONArray();
				for(int i = 0; i < strs.length; i++){
					String path = strs[i];
					path = path.substring(0, getIndex(path, "/", path.indexOf("/") + 1));
					System.out.println(" " + path);
					Floor floor = get(path);
					s.add(floor.write("JSON"));
					copyAll(floor, args[3] + "/pic");
				}
				
				/*for(String str : strs){
					System.out.println(str);
					int index = str.indexOf("/");
					String name = str.substring(0, index == -1 ? str.length() : index);
					copyAll(get(str), args[3] + "/pic");
				}*/
				FileOutputStream outputStream = new FileOutputStream(args[3] + "/tiezi.json");
				outputStream.write(s.toString().getBytes());
				outputStream.close();
			}
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			/*else if(args[1].equals("-outFile")){
				
			}*/
		}
		else if(args[0].equals("search")){
			System.out.println("查找指令：查找区域：" + args[1] + "，查找匹配：" + args[2] + "，输出文件：" + args[3]);
			String[] strs = args[1].split("\\|");
			String string = "";
			for(int i = 0; i < strs.length; i++){
				System.out.println("Searching... " + strs[i]);
				String[] strings = search(strs[i], args[2]);
				for(String s : strings){
					string += s + "\n";
				}
			}
			System.out.println("Search Result:" + string + "");
			try {
				FileOutputStream outputStream = new FileOutputStream(args[3]);
				outputStream.write(string.getBytes());
				outputStream.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		else if(args[0].equals("translate")){
			if(args[1].equals("HTML")){
				String[] strings = new File(path).list();
				for(int i = 0; i < strings.length; i++){
					String str = strings[i];
					Floor floor = get(str);
					String string = floor.write("HTML");
					try {
						FileOutputStream outputStream = new FileOutputStream(args[2] + "/" + str.split("\\.")[0] + ".html");
						outputStream.write(string.getBytes());
						outputStream.close();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
		}
	}
}
