package com.linux.vshow;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;

public class Constant {

	public static String saveDIR = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator + "savedir.ini";

	public static String sdcardDir = "/mnt/extsd";

	/**
	 * 4-22 V5.0.1.5更新了触摸系统退出 5-6 V5.0.1.16更新了流媒体不能删除的Bug 5-21 V5.0.1.18 取消开机同步
	 * 5-27 V5.0.1.20 单独开门狗 单独开关机
	 * 6.12按7清理apk
	 * V.5.0.1.25,8修改了开关机时间的问题
	 * V29 调台
	 * V30定时通知
	 */
	public static String version = "V5.0.1.30.0108";

	public static String mc = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator + "key.ini"; // 终端唯一标示保存文件地址
	public static String tempDir = Constant.sdcardDir + "/temp/"; // 临时文件目录
	public static String fileDir = Constant.sdcardDir + "/files/"; // 播放文件目录
	public static String TQDIR = Constant.sdcardDir + "/tq"; // 播放文件目录
	public static String HLDIR = Constant.sdcardDir + "/hl";
	public static String updateDir = Constant.sdcardDir + "/update/"; // 更新文件目录
	public static String offDir = Constant.sdcardDir + "/off/"; // 断网播放文件目录
	public static String novideoDir = Constant.sdcardDir + "/"; // 无文件播放目录
	public static String tDir = Constant.sdcardDir + "/temp"; // 临时目录
	public static String fDir = Constant.sdcardDir + "/files"; // 文件目录
	public static String uDir = Constant.sdcardDir + "/update"; // 更新目录
	public static String cDir = Constant.sdcardDir + "/camera"; // 更新目录
	public static String sdcardTemp = Constant.sdcardDir + "/temp/"; // 临时文件位置
	public static String config = Constant.sdcardDir + "/config.ini"; // 播放设置等配置文件
	public static String config2 = Constant.sdcardDir + "/config2.ini"; // 服务器IP等配置文件
	static String config3 = Constant.sdcardDir + "/config3.ini"; // 断电续播配置文件
	static String config4 = Constant.offDir + "config.ini"; // 断网播放配置文件
	
	public static String advance = Constant.sdcardDir + "/advance.ini";//高级配置保存目录

	
	public static String USBPAN = "/mnt/usbhost1/vsfile/"; // 第二个U盘vsfile目录位置
	public static String USBPAN2 = "/mnt/usbhost0/vsfile/"; // 第一个U盘vsfile目录位置
	public static String USBUPDATE = "/mnt/usbhost1/vsupdate/"; // 第二个U盘update目录位置
	public static String USBUPDATE2 = "/mnt/usbhost0/vsupdate/"; // 第一个U盘update目录位置

	public static String LogDir = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator + "Log";

	public static boolean firstrun = true;
	public static boolean downfinish = false;
	public static String downstr = "";

	public static HttpDown hd = null;
	public static RandomAccessFile rss = null;
	public static Timer timer = null;
	public static Handler handler = null;
	public static TimerTask task = null;
	public static PowerManager.WakeLock WAKELOCK = null;
	public static SocketClient sc = new SocketClient();

	public static String mfile = ""; // 当前正在播放的节目文件名
	public static String SRVIP = "192.168.1.101"; // 服务器IP
	public static int upup = 0; // 待机过标识,曾经待机过之后不进行更新
	public static boolean guanji = false; // 是否处于待机状态

	public static boolean G3G = false; // 是否开启3G U盘接口网络不通时定时重启
	public static boolean WEB = false; // 是否开启网络连通时加载程序
	public static boolean WEBGO = false; // 网络连通时标志
	public static int VIDEOAGAIN = 0; // 是否开启断电续播 大于0代表开启
	public static int G3GTOTAL = 0; // 3G U盘口重启 增加位

	public static String mac = ""; // 唯一标识码
	public static String msg = ""; // 消息字符串
	public static String downmsg = ""; // 下载时消息字符串
	public static String tempmsg = ""; // 其他消息字符串
	public static int change = 0; // 播放状态改变标识
	public static int width = 1280; // 系统分辨率宽
	public static int height = 720; // 系统分辨率高
	public static long downtotal = 0; // 已下载数据
	public static long alltotal = 0; // 总下载数据
	public static ArrayList avl = new ArrayList(); // 视频区域集合
	public static ArrayList imglist = new ArrayList(); // 图片区域集合
	static int total = 0; // 定时器累加器
	static int vx = 0; // 视频X
	static int vy = 0; // 视频Y
	static int vwidth = 0; // 视频宽
	static int vheight = 0; // 视频高
	static String modelcontent = ""; // 播放节目文件字符串
	static long totalfilelen = 0; // 播放节目总长度
	static String curmodel = ""; // 当前播放节目文件字符串
	static String fontcontent = ""; // 滚动字幕
	static String sendkey = ""; // 任务唯一标示
	static int up = 0; // 滚动字幕上移位置
	static int backcolor = Color.WHITE; // 滚动字幕背景颜色
	static int fonttype = 0; // 滚动字幕字体类型
	static int fontcolor = Color.BLACK; // 滚动字幕字体颜色
	static float fontspeed = 2.0f; // 滚动字幕滚动速度
	static float fontsize = 35f; // 滚动字幕文字大小
	public static int xiansu = 0; // 下载限速
	public static int lian = 10; // 心跳连接时间
	static int urltime = 0; // 触摸间隔时间
	static boolean chu = false; // 是否属于触摸状态
	static int chutime = 0; // 当前间隔逝去时间
	static boolean deldo = false; // 是否正在删除文件
	static int curplay = 0; // 当前播放的节目位置
	static ArrayList<PlayItem> playList = new ArrayList<PlayItem>(); // 节目队列
	static int curday = -1; // 当日星期几
	static boolean offshow = false; // 是否是播放断网节目
	static String nexttime = ""; // 下次重新加载配置时间
	static boolean afvideo = false; // 断电续播状态
	static boolean itemgo = false; // 点播状态
	static int avlcur = 0; // 当前断电续播位置
	static boolean avlbian = false; // 是否更新节目
	static boolean downing = false; // 是否正在下载

	public static SeePic sPic = null;
	public static SocketCamera scamera = null;
	public static RecordVideo rcamera = null;

	public static String tqkey = "";
	public static int tqagain = 0;
	public static String tqcolor = "";
	public static String tqsize = "";
	public static String tqimgsize = "";
	public static int tqx;
	public static int tqy;
	public static int tqwidth;
	public static int tqheight;
	public static boolean tqdo = false;
	public static int tqurl = 0;

	public static String playname = "";
	public static int playtime = 0;

	public static boolean leftdown = false;
	public static int lefttotal = 0;

	public static int imgtime = 0;

	public static int camera = 0;

	public static String tuichu = "";

	public static int popTime = 30;
	public static final int NONE = 0;
	public static final int ZOOM = 1;
	public static boolean chick = true;
	public static boolean isPop = false;
	public static int mode = NONE;
	public static float oldDist = 0;

	public static int effects[] = { R.anim.fade, R.anim.my_scale_action,
			R.anim.scale_rotate, R.anim.zoom_enter, R.anim.scale_translate };
	public static int effectsId = 6;

	public static boolean isChange = false;
	public static String hour = "00";
	public static String minute = "00";
	public static String initTime = "00:00";
	public static String shut = "";
	public static String getOpenTime1 = "00";
	public static String getOpenTime1_2 = "00";
	public static String getColseTime1 = "00";
	public static String getColseTime1_2 = "00";
	public static String getOpenTime2 = "00";
	public static String getOpenTime2_2 = "00";
	public static String getColseTime2 = "00";
	public static String getColseTime2_2 = "00";
	public static String getOpenTime3 = "00";
	public static String getOpenTime3_2 = "00";
	public static String getColseTime3 = "00";
	public static String getColseTime3_2 = "00";

	public static String setOpenTime1 = "00:00";
	public static String setColseTime1 = "00:00";
	public static String setOpenTime2 = "00:00";
	public static String setColseTime2 = "00:00";
	public static String setOpenTime3 = "00:00";
	public static String setColseTime3 = "00:00";

	public static int firstDay = 0;
	public static int secondDay = 0;
	public static int thirdDay = 0;

	public static int day = 0;

	public static boolean oneTime = false;
	public static boolean twoTime = false;
	public static boolean threeTime = false;

	public static int scale = 0;

	public static int vpX = 0;
	public static int vpY = 0;

	public static int light = 0;
	public static int lighttime = 60;
	public static boolean dodod = false;
	public static int currs = 0;

	public static String CloseTime = "";
	public static int CloseTimes = 0;
	public static int dayStr = 0;

	public static float downX = 0;
	public static float moveX = 0;

	public static boolean interval = true;
	public static int cameraType = 0;

	public static boolean outsideCount1 = false;
	public static boolean outsideCount2 = false;
	public static boolean outsideCount3 = false;
	public static boolean outsideCount4 = false;
	public static int ix = 0;
	public static int iy = 0;
	public static int iw = 1;
	public static int ih = 1;

	public static String hlkey = "";
	public static int hltime = 0;
	public static int hlx;
	public static int hly;
	public static int hlwidth;
	public static int hlheight;
	public static boolean hldo = false;

	public static String tkey = "";
	public static int tx;
	public static int ty;
	public static int twidth;
	public static int theight;
	public static boolean tdo = false;

	public static String worktime = "";
	public static String starttime = "";
	public static String endtime = "";

	public static int sync = 0;
	// public static boolean firstsync = true;

	public static boolean openurl = false;
	public static int syncTime = 0;
	public static int allTime = 0;
	public static int nowTime = 0;
	public static int nowPlays = 0;
	public static boolean changePlays = false;
	public static boolean changePlaysVideo = false;
	public static boolean changePlaysImage = false;

	public static boolean isInstall = true;
	public static int installNum = 0;

	public static boolean isZoom = false;
	public static double scaleZoomW = 0;
	public static double scaleZoomH = 0;

	public static LogInfo li = new LogInfo();
	public static boolean showInfo = false;

	public static String zipFileStr = "";
	public static String zipMsgStr = "";

	public static String cblbStr = "普通";

	public static boolean isHuDong = false;

	public static boolean upCameraFile = false;

	public static boolean dpStart = false;

	public static int dpPlayCount = 0;
	public static int dpCount = 0;

	public static boolean dpTimeCount = false;

	public static int dpRunTime = 0;
	public static int dpTime = 0;

	public static boolean dpIsStart = false;

	public static int VpGoONCur = 0;
	public static int VpGoONTime = 0;

	public static boolean dpIsStop = false;
	
	public static String textApk = "com.vshow.textfont";
	public static String fontApk = "com.vshow.scrollfont";
	public static String serviceapk = "com.example.vsplayerservice";
	public static boolean onkeys = false;
	
	public static boolean cmdopen = false;
	public static String scrollfont = "";
	public static String nextfontTime = "";
	public static boolean fontStart = false;
	
	public static int allVideoTime = 0;
	public static int dsTime = 0;
	public static boolean firstVideo = false;
	public static int videocur = 0;
	public static int videototo = 0;
	public static String nowTimes = "";
	public static String tbtime = "";
	public static int dsItem = 0;
	
	public static boolean startup = true;
	
	public static boolean updateapk = false;
	

	
}
