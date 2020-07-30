# E龙贴吧备份

本程序是一个备份贴吧用的爬虫，他可以从网上下载整个吧的帖子，也可以备份个人的帖子，个人的帖子有你的主题帖，你的回复，你的收藏，你的贴吧，你的被@的，你的粉丝，你的关注，你的举报，你的投诉<br>
<br>
也可以备份多个很多的散开的贴号的帖子<br>
本程序禁止用于非法用途<br>
入口函数在Bak.java里<br><br>

## 使用教程
https://shimo.im/docs/4b141d04a1b84545/<br>

贴吧最近好像又加类安全验证了，防止高频访问流量，如果出现这样的情况，那么可以先断开路由器再连接路由器，这样IP地址会变<br>
但是，备份时请将你想要备份的东西先想好了，因为不止可能会出现安全认证，而且这种高频访问流量会给服务器带来压力<br>
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

因为本人编写这个代码的时候是一点一点加上去的，且代码编写跨度长达一年，所以代码很乱<br><br>

把工程clone下来，source内为源码<br>
用eclipse打开那个source内的EDragonTiebaBackup<br>

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
	 这个备份爬虫从一开始的只为暂时备份一个贴吧(那时候还是纯文本)，发展到类有比较漂亮的备份文件(HTML)格式，然后又发展到类GUI界面，最后又发展处类多种格式(HTML, JSON, TXT，后面还打算加PDF)以及格式转换(未完成)和工具(未完成)一年半前我才刚刚接触到网络编程<br>
	 因为一开始我没有计划发展这么多，所以架构并不怎么太好<br>
	 并且一开始代码堆在一起，知道最近才把他们拆成类这么多类文件，而且我又把以前分开的文件整合在类一起，代码显得很混乱<br>
	 一年前我的代码并不怎么样，现在任然有很多那时代码的残余<br><br>
	 本人是个学生党
	 
### 仓库使用教程
<br>
因为这一次我把结构重写了，可能会有一些bug，如果有bug请反馈(提交Issues)

