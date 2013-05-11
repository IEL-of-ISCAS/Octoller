package cn.ac.iscas.iel.vr.octoller.constants;

public class Messages {
	public final static int NONE				=  0;
	public final static int NEWCONNECT			=  1;     //新连接
	public final static int DISCONNECT			=  2;     //断开连接
	public final static int GIVEUPCONTROL 		=  3;     //放弃控制
	public final static int REQUESTCONTROL		=  4;     //请求控制
	public final static int LOCKNAV				=  5;     //锁定导航
	public final static int UNLOCKNAV			=  6;     //打开导航
	public final static int PUSH				=  7;     //单击(单指)
	public final static int RELEASE				=  8;     //放开手指(单指)
	public final static int DRAG				=  9;     //手指拖拽(单指)
	public final static int MOVE				= 10;     //phone的自由运动，类似鼠标移动
	public final static int VOLUMEUP			= 11;     //音量按钮加大
	public final static int VOLUMEDOWN			= 12;	  //音量按钮减小
	public final static int RAYCASTMANIPULATOR	= 13;     //缺省指点
	public final static int TRACKBALLMANIPULATOR= 14;	  //各种操纵器
	public final static int FLIGHTMANIPULATOR	= 15;	  //飞行模式
	public final static int DRIVEMANIPULATOR	= 16;	  //开车模式
	public final static int UFOMANIPULATOR		= 17;
	public final static int NAVMANIPULATOR0		= 18;
	public final static int NAVMANIPULATOR1		= 19;
	public final static int ENDMANIPULATOR		= 20;
	public final static int PICK				= 21;	  //拾取操作
	public final static int PHONEMOVE			= 22;	  //连接后的缺省状态，主要是用来控制鼠标
	public final static int ROTATE              = 24;     //锁定后旋转手机
	public final static int PINCH               = 25;     //两指缩放
}
