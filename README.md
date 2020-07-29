# E龙贴吧备份

本程序是一个备份贴吧用的爬虫，他可以从网上下载整个吧的帖子，也可以备份个人的帖子，个人的帖子有你的主题帖，你的回复，你的收藏，你的贴吧，你的被@的，你的粉丝，你的关注，你的举报，你的投诉<br>
<br>
也可以备份多个很多的散开的贴号的帖子<br>
本程序禁止用于非法用途<br>
入口函数在Bak.java里<br><br>

## 使用教程
https://shimo.im/docs/4b141d04a1b84545/<br>

贴吧最近好像又加类安全验证了，防止高频访问流量，如果出现这样的情况，那么可以先断开路由器再连接路由器，这样IP地址会变<br><br>
但是，备份时请将你想要备份的东西先想好了，因为不止可能会出现安全认证，而且这种高频访问流量会给服务器带来压力<br><br>
所以，请合理使用这个爬虫<br>
<br><br>

### V0.8.7更新内容
```
增加乱帖子工具，可以进行帖子抽取，帖子格式转换等等，但是并没有完成
	  增加乱多种格式的输出(TXT格式还没有测试过)
	  	HTML格式  ---网页格式
	  	JSON格式  ---JSON格式，你可以编写代码获取已经备份下来的json中的几楼几楼的内容
	  	TXT格式   ---纯文本格式，请注意，转换成TXT格式不可逆
```
				
## 代码相关

因为本人编写这个代码的时候是一点一点加上去的，且代码编写跨度长达一年，所以代码很乱<br>

### 文件解释：
	 		Bak.java --入口文件以及命令解释<br>
	 		BakBar.java --备份贴吧用的类<br>
	 		BakPersonThreads.java --备份个人主题帖用的类<br>
	 		BakPersonReply.java --备份个人回复用的类<br>
	 		BakLike.java --备份个人收藏所用的类<br>
	 		BakAt.java --备份个人被@所用的类<br>
	 		BakBars.java --备份你的贴吧所用的类<br>
	 		BakFans.java --备份个人粉丝所用的类<br>
	 		BakJuBao.java --备份个人举报所用的类<br>
	 		BakCompain.java --备份个人投诉所用的类<br>
	 		Floor.java --楼层类<br>
	 		Gui.java --GUI界面<br>
	 		EDTBTool --帖子工具(未完成)<br>
### 其他说明：
	 这个备份爬虫从一开始的只为暂时备份一个贴吧(那时候还是纯文本)，发展到类有比较漂亮的备份文件(HTML)格式，然后又发展到类GUI界面，最后又发展处类多种格式(HTML, JSON, TXT，后面还打算加PDF)以及格式转换(未完成)和工具(未完成)<br>
	 因为一开始我没有计划发展这么多，所以架构并不怎么太好<br>
	 并且一开始代码堆在一起，知道最近才把他们拆成类这么多类文件，而且我又把以前分开的文件整合在类一起，代码显得很混乱<br>
	 一年前我的代码并不怎么样，现在任然有很多那时代码的残余<br><br>
	 本人是个学生党
	 
EDragonTiebaBackup<br>
E龙贴吧备份器<br>
大家可以使用<br>
<br>
这是一个贴吧备份软件，可以备份一整个吧的帖子，可以备份你自己的主题帖，回复，收藏，<br>
@，你的贴吧，你的粉丝，你的关注，你的举报等等<br>
<br>
V0.8 版本，加入 GUI 界面，可以不用去敲命令行了，放低了门槛，全程傻瓜化操作，<br>
修复了些 bug<br>
V0.8.2 重写了架构<br>
<br>
## 使用教程：
首先下载这个软件，如目录所示，尽量选高版本<br>
这个软件需要安装环境为java，可以去：https://www.java.com<br>
下载完成后<br>
可以参照"说明.pdf"打开看怎么操作<br>
<br>
### 用户界面操作：
首先打开"运行我打开GUI.bat"<br>
可以看到打开了一个GUI<br>
不解释，都能看懂<br>
![<图片加载失败>](describeFiles/tieba1.png)<br>
然后点开始就行了<br>
<br>
备份个人时Cookie只需要输入BDUSS=……..和STOKEN=……..，分号别丢<br>
![<图片加载失败>](describeFiles/tieba2.png)<br>
![<图片加载失败>](describeFiles/tieba3.png)<br>
备份散开的帖子每个贴号之间用空格隔开<br>
<br>
其他参数里JVM参数可以上网上查，为防止你被听的云里雾里，这里只说两个简单的参数：<br>
		`-Xmx<内存大小>表示最大内存大小<br>`<br>
		`-Xms<内存大小>表示初始内存大小<br>`<br>
		`例如：-Xmx2048m表示最大运行内存为2048mb`<br>
		` 如果你要备份很多的贴，特长的贴等等，请将最大内存调大点，不然会超出内存，导致失败`<br>
![<图片加载失败>](describeFiles/tieba4.png)<br>
最后点开始就行了<br>
<br>
### 命令行使用方法：
在这里调出命令窗口<br>
在运行之前请先输入chcp 936调整编码，不然输出到CMD上的都是乱码<br>
输入java -Dfile.encoding=utf-8 -jar BackUp.jar<br>
后面是参数
```
bakBar是备份贴吧
后面的参数是：
	[贴吧网址] [备份到本地的路径] [要备份的贴数（主题帖数量）]
		[贴吧网址]就是贴吧的网址，比如：https://tieba.baidu.com/f?kw=ndragon&ie=utf-8
			后面不要加上&pn=，它会自己加上
		[备份到本地的路径]是备份到本地的路径，比如：F:\TIEBA_BAK
			后面不要加上\，这个加不加会不会产生影响没测试过


bakPerssion是备份个人
后面参数是：
	[你的个人Cookie(我不会代码登陆贴吧(T_T)，只能用这样)][备份到本地的路径]
		[你的个人Cookie]可以调出检查点击头像找（我是网络编程小白(T_T)）



bak
	[贴号] [备份到本地的路径]
		[贴号]可以有多个用空格隔开



后面还有一个补充参数：
		-totelThreadNum 后面加数字，表示在读取网页时的多线程数量，默认是40
		-noThread 表示不备份主题帖，后面不用加参数
		-noReply 表示不备份回复，后面不用加参数
		-noLike 表示不备份收藏
		-noAt 表示不备份我被@到的
		-noBars 表示不备份我的贴吧
		-noFans 表示不备份我的粉丝
		-noConcerns 表示不备份我的关注
		-noReport 表示不备份我的举报
		-noCompain 表示不备份我的投诉
		-hasFansTies 表示备份粉丝的主页
		-noCheck表示不对文件进行检查，因为本软件获取验证的网址github很不稳定，所以有可能导致获取了N次也没有获取到，导致检查不了，如果一直获取不到请加上-noCheck
```

#### 例子：
```
   备份我个人的贴：
       java -Dfile.encoding=utf-8 -jar BackUp.jar bakPerssion "[我的Cookie]" "F:\I"
   备份ndragon吧前100贴：
       java -Dfile.encoding=utf-8 -jar BackUp.jar bakBar "https://tieba.baidu.com/f?kw=ndragon&ie=utf-8" "F:\backup" 100
   备份我个人的除了主题帖，回复外的内容：
       java -Dfile.encoding=utf-8 -jar BackUp.jar bakPerssion "[我的Cookie]" "F:\I" –noThread -noReply
```

**一般情况下直接用GUI就行了，命令提示符不用在意**<br><br>
**这个软件运行起来是耗流量的，因为他本身就是爬取网上的帖子**<br>
**这是基本操作信息，更多信息请至"说明.pdf"**

