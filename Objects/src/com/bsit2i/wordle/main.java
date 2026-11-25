package com.bsit2i.wordle;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.bsit2i.wordle", "com.bsit2i.wordle.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.bsit2i.wordle", "com.bsit2i.wordle.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.bsit2i.wordle.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static String[] _item = null;
public static anywheresoftware.b4a.objects.MediaPlayerWrapper _bg1 = null;
public static anywheresoftware.b4a.objects.MediaPlayerWrapper _bg2 = null;
public static anywheresoftware.b4a.objects.MediaPlayerWrapper _cl2 = null;
public static anywheresoftware.b4a.objects.MediaPlayerWrapper _cl1 = null;
public static float _vl1 = 0f;
public static float _vl2 = 0f;
public static String _mods = "";
public static int _tut = 0;
public static anywheresoftware.b4a.objects.collections.List _datlist = null;
public static int _gp = 0;
public static int _cw = 0;
public static int _ts = 0;
public static String _tim = "";
public static int _atm = 0;
public static String _usr = "";
public static int _sc = 0;
public static String _exp = "";
public static String _resh = "";
public static boolean _tutor = false;
public anywheresoftware.b4a.objects.ButtonWrapper _b1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pntut = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _lbs = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _hist = null;
public anywheresoftware.b4a.objects.EditTextWrapper _username = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _profn = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb6 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _conf = null;
public anywheresoftware.b4a.objects.PanelWrapper _pro = null;
public anywheresoftware.b4a.objects.LabelWrapper _usern = null;
public anywheresoftware.b4a.objects.ButtonWrapper _prob = null;
public com.bsit2i.wordle.game _game = null;
public com.bsit2i.wordle.history _history = null;
public com.bsit2i.wordle.settings _settings = null;
public com.bsit2i.wordle.credits _credits = null;
public com.bsit2i.wordle.loading _loading = null;
public com.bsit2i.wordle.gtut _gtut = null;
public com.bsit2i.wordle.musicservice _musicservice = null;
public com.bsit2i.wordle.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (game.mostCurrent != null);
vis = vis | (history.mostCurrent != null);
vis = vis | (settings.mostCurrent != null);
vis = vis | (credits.mostCurrent != null);
vis = vis | (loading.mostCurrent != null);
vis = vis | (gtut.mostCurrent != null);
vis = vis | (musicservice.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
String _saveddata = "";
String[] _parts = null;
float _vol1 = 0f;
float _vol2 = 0f;
String _s = "";
boolean _isfinished = false;
String _users = "";
 //BA.debugLineNum = 65;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 67;BA.debugLine="Activity.LoadLayout(\"mainmenu\")";
mostCurrent._activity.LoadLayout("mainmenu",mostCurrent.activityBA);
 //BA.debugLineNum = 69;BA.debugLine="datlist.Initialize";
_datlist.Initialize();
 //BA.debugLineNum = 70;BA.debugLine="If usern.Text = \"\" Then";
if ((mostCurrent._usern.getText()).equals("")) { 
 //BA.debugLineNum = 71;BA.debugLine="usern.Text = \"Player1\"";
mostCurrent._usern.setText(BA.ObjectToCharSequence("Player1"));
 };
 //BA.debugLineNum = 73;BA.debugLine="pro.SendToBack";
mostCurrent._pro.SendToBack();
 //BA.debugLineNum = 74;BA.debugLine="pro.Visible = False";
mostCurrent._pro.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 76;BA.debugLine="lbs.Color = Colors.ARGB(0, 0, 0, 0)";
mostCurrent._lbs.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 77;BA.debugLine="conf.Color = Colors.ARGB(0, 0, 0, 0)";
mostCurrent._conf.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 78;BA.debugLine="prob.Color = Colors.ARGB(0, 0, 0, 0)";
mostCurrent._prob.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 79;BA.debugLine="vl1 = 0.4";
_vl1 = (float) (0.4);
 //BA.debugLineNum = 80;BA.debugLine="vl2 = 0.6";
_vl2 = (float) (0.6);
 //BA.debugLineNum = 82;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 83;BA.debugLine="cl1.initialize";
_cl1.Initialize();
 //BA.debugLineNum = 84;BA.debugLine="cl1.Load(File.DirAssets, \"click1.mp3\")";
_cl1.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"click1.mp3");
 //BA.debugLineNum = 87;BA.debugLine="cl2.Initialize";
_cl2.Initialize();
 //BA.debugLineNum = 88;BA.debugLine="cl2.Load(File.DirAssets, \"click2.mp3\")";
_cl2.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"click2.mp3");
 //BA.debugLineNum = 91;BA.debugLine="If bg1.IsInitialized = False Then";
if (_bg1.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 92;BA.debugLine="bg1.Initialize()";
_bg1.Initialize();
 //BA.debugLineNum = 93;BA.debugLine="bg1.Load(File.DirAssets, \"nbgm1.mp3\")";
_bg1.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"nbgm1.mp3");
 //BA.debugLineNum = 94;BA.debugLine="bg1.SetVolume(vl1, vl1)";
_bg1.SetVolume(_vl1,_vl1);
 //BA.debugLineNum = 95;BA.debugLine="bg1.Looping = True";
_bg1.setLooping(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 98;BA.debugLine="bg2.Initialize()";
_bg2.Initialize();
 //BA.debugLineNum = 99;BA.debugLine="bg2.Load(File.DirAssets, \"nbgm2.mp3\")";
_bg2.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"nbgm2.mp3");
 //BA.debugLineNum = 100;BA.debugLine="bg1.SetVolume(vl1, vl1)";
_bg1.SetVolume(_vl1,_vl1);
 //BA.debugLineNum = 101;BA.debugLine="bg2.SetVolume(vl1, vl1)";
_bg2.SetVolume(_vl1,_vl1);
 //BA.debugLineNum = 102;BA.debugLine="bg2.Looping = True";
_bg2.setLooping(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 105;BA.debugLine="cl1.Initialize()";
_cl1.Initialize();
 //BA.debugLineNum = 106;BA.debugLine="cl1.Load(File.DirAssets, \"click1.mp3\")";
_cl1.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"click1.mp3");
 //BA.debugLineNum = 107;BA.debugLine="cl1.SetVolume(vl2, vl2)";
_cl1.SetVolume(_vl2,_vl2);
 //BA.debugLineNum = 108;BA.debugLine="cl2.Initialize()";
_cl2.Initialize();
 //BA.debugLineNum = 109;BA.debugLine="cl2.Load(File.DirAssets, \"click1.mp3\")";
_cl2.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"click1.mp3");
 //BA.debugLineNum = 110;BA.debugLine="cl2.SetVolume(vl2, vl2)";
_cl2.SetVolume(_vl2,_vl2);
 //BA.debugLineNum = 113;BA.debugLine="bg1.play";
_bg1.Play();
 };
 //BA.debugLineNum = 116;BA.debugLine="If File.Exists(File.DirInternal, \"history.txt\") T";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"history.txt")) { 
 //BA.debugLineNum = 117;BA.debugLine="datlist = File.ReadList(File.DirInternal, \"histo";
_datlist = anywheresoftware.b4a.keywords.Common.File.ReadList(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"history.txt");
 };
 //BA.debugLineNum = 124;BA.debugLine="If File.Exists(File.DirInternal, \"volume.txt\") Th";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"volume.txt")) { 
 //BA.debugLineNum = 125;BA.debugLine="Dim savedData As String = File.ReadString(File.D";
_saveddata = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"volume.txt");
 //BA.debugLineNum = 126;BA.debugLine="Dim parts() As String = Regex.Split(\",\", savedDa";
_parts = anywheresoftware.b4a.keywords.Common.Regex.Split(",",_saveddata);
 //BA.debugLineNum = 127;BA.debugLine="Dim vol1 As Float = parts(0)";
_vol1 = (float)(Double.parseDouble(_parts[(int) (0)]));
 //BA.debugLineNum = 128;BA.debugLine="Dim vol2 As Float = parts(1)";
_vol2 = (float)(Double.parseDouble(_parts[(int) (1)]));
 //BA.debugLineNum = 129;BA.debugLine="vl1 = vol1";
_vl1 = _vol1;
 //BA.debugLineNum = 130;BA.debugLine="vl2 = vol2";
_vl2 = _vol2;
 };
 //BA.debugLineNum = 140;BA.debugLine="lbs.Text = \"Next\"";
mostCurrent._lbs.setText(BA.ObjectToCharSequence("Next"));
 //BA.debugLineNum = 143;BA.debugLine="If File.Exists(File.DirInternal, \"tutorial.txt\")";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"tutorial.txt")) { 
 //BA.debugLineNum = 144;BA.debugLine="Dim s As String = File.ReadString(File.DirIntern";
_s = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"tutorial.txt");
 //BA.debugLineNum = 145;BA.debugLine="Dim isFinished As Boolean = (s.ToLowerCase = \"tr";
_isfinished = ((_s.toLowerCase()).equals("true"));
 //BA.debugLineNum = 146;BA.debugLine="tutor = isFinished";
_tutor = _isfinished;
 }else if((_s).equals("true")) { 
 //BA.debugLineNum = 148;BA.debugLine="tutor = True";
_tutor = anywheresoftware.b4a.keywords.Common.True;
 }else if(_tutor==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 150;BA.debugLine="ShowTutorialHighlight";
_showtutorialhighlight();
 //BA.debugLineNum = 151;BA.debugLine="tutor = True";
_tutor = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 154;BA.debugLine="If File.Exists(File.DirInternal, \"username.txt\") T";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"username.txt")) { 
 //BA.debugLineNum = 155;BA.debugLine="Dim users As String = File.ReadString(File.DirInt";
_users = anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"username.txt");
 //BA.debugLineNum = 156;BA.debugLine="usern.Text = users";
mostCurrent._usern.setText(BA.ObjectToCharSequence(_users));
 //BA.debugLineNum = 157;BA.debugLine="username.Text = users";
mostCurrent._username.setText(BA.ObjectToCharSequence(_users));
 //BA.debugLineNum = 158;BA.debugLine="usr = users";
_usr = _users;
 //BA.debugLineNum = 160;BA.debugLine="If File.Exists(File.DirInternal, \"gp.txt\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"gp.txt")) { 
 //BA.debugLineNum = 161;BA.debugLine="gp = File.ReadString(File.DirInternal, \"gp.txt\")";
_gp = (int)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"gp.txt")));
 };
 //BA.debugLineNum = 163;BA.debugLine="If File.Exists(File.DirInternal, \"cw.txt\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"cw.txt")) { 
 //BA.debugLineNum = 164;BA.debugLine="cw = File.ReadString(File.DirInternal, \"cw.txt\"";
_cw = (int)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"cw.txt")));
 };
 //BA.debugLineNum = 166;BA.debugLine="If File.Exists(File.DirInternal, \"ts.txt\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"ts.txt")) { 
 //BA.debugLineNum = 167;BA.debugLine="ts = File.ReadString(File.DirInternal, \"ts.txt\"";
_ts = (int)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.File.ReadString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"ts.txt")));
 };
 //BA.debugLineNum = 170;BA.debugLine="File.WriteString(File.DirInternal, \"gp.txt\", gp)";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"gp.txt",BA.NumberToString(_gp));
 //BA.debugLineNum = 171;BA.debugLine="File.WriteString(File.DirInternal, \"cw.txt\", cw)";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"cw.txt",BA.NumberToString(_cw));
 //BA.debugLineNum = 172;BA.debugLine="File.WriteString(File.DirInternal, \"ts.txt\", ts)";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"ts.txt",BA.NumberToString(_ts));
 };
 //BA.debugLineNum = 175;BA.debugLine="Size";
_size();
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 207;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 208;BA.debugLine="bg1.pause";
_bg1.Pause();
 //BA.debugLineNum = 209;BA.debugLine="bg2.pause";
_bg2.Pause();
 //BA.debugLineNum = 210;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 190;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 192;BA.debugLine="If bg1.IsInitialized = False Then";
if (_bg1.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 193;BA.debugLine="bg1.Initialize()";
_bg1.Initialize();
 //BA.debugLineNum = 194;BA.debugLine="bg1.Load(File.DirAssets, \"nbgm1.mp3\")";
_bg1.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"nbgm1.mp3");
 //BA.debugLineNum = 195;BA.debugLine="bg1.SetVolume(vl1, vl1)";
_bg1.SetVolume(_vl1,_vl1);
 //BA.debugLineNum = 196;BA.debugLine="bg1.Looping = True";
_bg1.setLooping(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 199;BA.debugLine="bg2.Initialize()";
_bg2.Initialize();
 //BA.debugLineNum = 200;BA.debugLine="bg2.Load(File.DirAssets, \"nbgm2.mp3\")";
_bg2.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"nbgm2.mp3");
 //BA.debugLineNum = 201;BA.debugLine="bg2.SetVolume(vl1, vl1)";
_bg2.SetVolume(_vl1,_vl1);
 //BA.debugLineNum = 202;BA.debugLine="bg2.Looping = True";
_bg2.setLooping(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 204;BA.debugLine="bg1.Play";
_bg1.Play();
 //BA.debugLineNum = 206;BA.debugLine="End Sub";
return "";
}
public static String  _b1_click() throws Exception{
 //BA.debugLineNum = 219;BA.debugLine="Private Sub b1_Click";
 //BA.debugLineNum = 220;BA.debugLine="gp = gp + 1";
_gp = (int) (_gp+1);
 //BA.debugLineNum = 221;BA.debugLine="bg1.Stop";
_bg1.Stop();
 //BA.debugLineNum = 222;BA.debugLine="cl1.play";
_cl1.Play();
 //BA.debugLineNum = 223;BA.debugLine="usr = username.Text";
_usr = mostCurrent._username.getText();
 //BA.debugLineNum = 224;BA.debugLine="File.WriteString(File.DirInternal, \"gp.txt\", gp)";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"gp.txt",BA.NumberToString(_gp));
 //BA.debugLineNum = 227;BA.debugLine="StartActivity(\"loading\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("loading"));
 //BA.debugLineNum = 228;BA.debugLine="mods = \"game\"";
_mods = "game";
 //BA.debugLineNum = 229;BA.debugLine="End Sub";
return "";
}
public static String  _b2_click() throws Exception{
 //BA.debugLineNum = 215;BA.debugLine="Private Sub b2_Click";
 //BA.debugLineNum = 216;BA.debugLine="cl1.play";
_cl1.Play();
 //BA.debugLineNum = 217;BA.debugLine="StartActivity(\"settings\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("settings"));
 //BA.debugLineNum = 218;BA.debugLine="End Sub";
return "";
}
public static String  _b3_click() throws Exception{
 //BA.debugLineNum = 211;BA.debugLine="Private Sub b3_Click";
 //BA.debugLineNum = 212;BA.debugLine="cl2.play";
_cl2.Play();
 //BA.debugLineNum = 213;BA.debugLine="StartActivity(\"credits\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("credits"));
 //BA.debugLineNum = 214;BA.debugLine="End Sub";
return "";
}
public static String  _conf_click() throws Exception{
 //BA.debugLineNum = 343;BA.debugLine="Private Sub conf_Click";
 //BA.debugLineNum = 346;BA.debugLine="cl1.play";
_cl1.Play();
 //BA.debugLineNum = 347;BA.debugLine="usr = username.text";
_usr = mostCurrent._username.getText();
 //BA.debugLineNum = 348;BA.debugLine="usern.text = username.text";
mostCurrent._usern.setText(BA.ObjectToCharSequence(mostCurrent._username.getText()));
 //BA.debugLineNum = 349;BA.debugLine="pro.SendToBack";
mostCurrent._pro.SendToBack();
 //BA.debugLineNum = 350;BA.debugLine="pro.Visible = False";
mostCurrent._pro.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 352;BA.debugLine="b1.Enabled = True";
mostCurrent._b1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 353;BA.debugLine="b2.Enabled = True";
mostCurrent._b2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 354;BA.debugLine="b3.Enabled = True";
mostCurrent._b3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 355;BA.debugLine="hist.Enabled = True";
mostCurrent._hist.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 356;BA.debugLine="prob.Enabled = True";
mostCurrent._prob.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 357;BA.debugLine="File.WriteString(File.DirInternal, \"username.txt\"";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"username.txt",_usr);
 //BA.debugLineNum = 358;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 39;BA.debugLine="Dim tutor As Boolean = False";
_tutor = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 41;BA.debugLine="Private b1 As Button";
mostCurrent._b1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private b2 As Button";
mostCurrent._b2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private b3 As Button";
mostCurrent._b3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Dim cl2 As MediaPlayer";
_cl2 = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Dim cl1 As MediaPlayer";
_cl1 = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private pntut As Panel";
mostCurrent._pntut = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private lb1 As Label";
mostCurrent._lb1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private lb2 As Label";
mostCurrent._lb2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private lbs As Button";
mostCurrent._lbs = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private lb3 As Label";
mostCurrent._lb3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 51;BA.debugLine="Private lb4 As Label";
mostCurrent._lb4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private lb5 As Label";
mostCurrent._lb5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Private hist As Button";
mostCurrent._hist = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 54;BA.debugLine="Private username As EditText";
mostCurrent._username = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 55;BA.debugLine="Private Panel2 As Panel";
mostCurrent._panel2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private profn As Panel";
mostCurrent._profn = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Private lb6 As Label";
mostCurrent._lb6 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Private lb7 As Label";
mostCurrent._lb7 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 59;BA.debugLine="Private conf As Button";
mostCurrent._conf = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Private pro As Panel";
mostCurrent._pro = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Private usern As Label";
mostCurrent._usern = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Private prob As Button";
mostCurrent._prob = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _hist_click() throws Exception{
 //BA.debugLineNum = 338;BA.debugLine="Private Sub hist_Click";
 //BA.debugLineNum = 339;BA.debugLine="cl1.play";
_cl1.Play();
 //BA.debugLineNum = 340;BA.debugLine="StartActivity(\"history\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("history"));
 //BA.debugLineNum = 341;BA.debugLine="End Sub";
return "";
}
public static String  _lbs_click() throws Exception{
 //BA.debugLineNum = 332;BA.debugLine="Private Sub lbs_Click";
 //BA.debugLineNum = 333;BA.debugLine="cl1.play";
_cl1.Play();
 //BA.debugLineNum = 334;BA.debugLine="tut = tut + 1";
_tut = (int) (_tut+1);
 //BA.debugLineNum = 335;BA.debugLine="ShowTutorialHighlight";
_showtutorialhighlight();
 //BA.debugLineNum = 336;BA.debugLine="End Sub";
return "";
}
public static String  _prob_click() throws Exception{
 //BA.debugLineNum = 360;BA.debugLine="Private Sub prob_Click";
 //BA.debugLineNum = 361;BA.debugLine="cl1.play";
_cl1.Play();
 //BA.debugLineNum = 362;BA.debugLine="pro.Visible = True";
mostCurrent._pro.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 363;BA.debugLine="pro.BringToFront";
mostCurrent._pro.BringToFront();
 //BA.debugLineNum = 364;BA.debugLine="b1.Enabled = False";
mostCurrent._b1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 365;BA.debugLine="b2.Enabled = False";
mostCurrent._b2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 366;BA.debugLine="b3.Enabled = False";
mostCurrent._b3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 367;BA.debugLine="hist.Enabled = False";
mostCurrent._hist.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 368;BA.debugLine="prob.Enabled = False";
mostCurrent._prob.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 369;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
game._process_globals();
history._process_globals();
settings._process_globals();
credits._process_globals();
loading._process_globals();
gtut._process_globals();
musicservice._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 9;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 12;BA.debugLine="Dim item() As String";
_item = new String[(int) (0)];
java.util.Arrays.fill(_item,"");
 //BA.debugLineNum = 14;BA.debugLine="Dim bg1 As MediaPlayer";
_bg1 = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim bg2 As MediaPlayer";
_bg2 = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim cl2 As MediaPlayer";
_cl2 = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim cl1 As MediaPlayer";
_cl1 = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim vl1 As Float";
_vl1 = 0f;
 //BA.debugLineNum = 19;BA.debugLine="Dim vl2 As Float";
_vl2 = 0f;
 //BA.debugLineNum = 20;BA.debugLine="Dim mods As String = \"\"";
_mods = "";
 //BA.debugLineNum = 21;BA.debugLine="Dim tut As Int = 0";
_tut = (int) (0);
 //BA.debugLineNum = 23;BA.debugLine="Public datlist As List";
_datlist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 24;BA.debugLine="Dim gp As Int = 0";
_gp = (int) (0);
 //BA.debugLineNum = 25;BA.debugLine="Dim cw As Int = 0";
_cw = (int) (0);
 //BA.debugLineNum = 26;BA.debugLine="Dim ts As Int = 0";
_ts = (int) (0);
 //BA.debugLineNum = 27;BA.debugLine="Dim tim As String = \"\"";
_tim = "";
 //BA.debugLineNum = 28;BA.debugLine="Dim atm As Int = 0";
_atm = (int) (0);
 //BA.debugLineNum = 29;BA.debugLine="Dim usr As String = \"Name: \"";
_usr = "Name: ";
 //BA.debugLineNum = 30;BA.debugLine="Dim sc As Int = 0";
_sc = (int) (0);
 //BA.debugLineNum = 31;BA.debugLine="Dim exp As String = \"IGN             Attempts No.";
_exp = "IGN             Attempts No.    Time Finished     Score";
 //BA.debugLineNum = 32;BA.debugLine="Dim resh As String  '= usr & \"         \" & tim &";
_resh = "";
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static String  _showtutorialhighlight() throws Exception{
anywheresoftware.b4a.objects.ConcreteViewWrapper _target = null;
anywheresoftware.b4a.objects.drawable.ColorDrawable _darkbg = null;
 //BA.debugLineNum = 231;BA.debugLine="Sub ShowTutorialHighlight()";
 //BA.debugLineNum = 232;BA.debugLine="Dim Target As View";
_target = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
 //BA.debugLineNum = 234;BA.debugLine="pntut.Visible = True";
mostCurrent._pntut.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 235;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 238;BA.debugLine="Dim darkBG As ColorDrawable";
_darkbg = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 239;BA.debugLine="darkBG.Initialize(Colors.ARGB(180, 0, 0, 0), 0)";
_darkbg.Initialize(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (180),(int) (0),(int) (0),(int) (0)),(int) (0));
 //BA.debugLineNum = 240;BA.debugLine="pntut.Background = darkBG";
mostCurrent._pntut.setBackground((android.graphics.drawable.Drawable)(_darkbg.getObject()));
 //BA.debugLineNum = 243;BA.debugLine="pntut.RemoveAllViews";
mostCurrent._pntut.RemoveAllViews();
 //BA.debugLineNum = 245;BA.debugLine="lbs.Visible = True";
mostCurrent._lbs.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 246;BA.debugLine="b1.Visible = False";
mostCurrent._b1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 247;BA.debugLine="b2.Visible = False";
mostCurrent._b2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 248;BA.debugLine="b3.Visible = False";
mostCurrent._b3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 249;BA.debugLine="b1.Enabled= False";
mostCurrent._b1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 250;BA.debugLine="b2.Enabled = False";
mostCurrent._b2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 251;BA.debugLine="b3.Enabled = False";
mostCurrent._b3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 252;BA.debugLine="hist.Enabled = False";
mostCurrent._hist.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 253;BA.debugLine="hist.Visible = False";
mostCurrent._hist.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 254;BA.debugLine="username.Enabled = False";
mostCurrent._username.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 255;BA.debugLine="Select tut";
switch (_tut) {
case 0: {
 //BA.debugLineNum = 257;BA.debugLine="lb1.Visible = True";
mostCurrent._lb1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 258;BA.debugLine="lb1.BringToFront";
mostCurrent._lb1.BringToFront();
 break; }
case 1: {
 //BA.debugLineNum = 260;BA.debugLine="lb2.BringToFront";
mostCurrent._lb2.BringToFront();
 //BA.debugLineNum = 261;BA.debugLine="lb1.Visible = False";
mostCurrent._lb1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 262;BA.debugLine="lb2.Visible = True";
mostCurrent._lb2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 2: {
 //BA.debugLineNum = 264;BA.debugLine="lb3.BringToFront";
mostCurrent._lb3.BringToFront();
 //BA.debugLineNum = 265;BA.debugLine="lb2.Visible = False";
mostCurrent._lb2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 266;BA.debugLine="lb3.Visible = True";
mostCurrent._lb3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 267;BA.debugLine="b3.Visible = True";
mostCurrent._b3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 268;BA.debugLine="b3.BringToFront";
mostCurrent._b3.BringToFront();
 break; }
case 3: {
 //BA.debugLineNum = 270;BA.debugLine="lb6.visible = True";
mostCurrent._lb6.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 271;BA.debugLine="lb6.BringToFront";
mostCurrent._lb6.BringToFront();
 //BA.debugLineNum = 272;BA.debugLine="hist.BringToFront";
mostCurrent._hist.BringToFront();
 //BA.debugLineNum = 273;BA.debugLine="hist.Visible = True";
mostCurrent._hist.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 274;BA.debugLine="lb3.Visible = False";
mostCurrent._lb3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 275;BA.debugLine="b3.Visible = False";
mostCurrent._b3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 4: {
 //BA.debugLineNum = 277;BA.debugLine="lb6.Visible = False";
mostCurrent._lb6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 278;BA.debugLine="lb7.Visible = True";
mostCurrent._lb7.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 279;BA.debugLine="lb7.BringToFront";
mostCurrent._lb7.BringToFront();
 //BA.debugLineNum = 280;BA.debugLine="hist.Visible = False";
mostCurrent._hist.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 281;BA.debugLine="profn.BringToFront";
mostCurrent._profn.BringToFront();
 break; }
case 5: {
 //BA.debugLineNum = 283;BA.debugLine="lb4.BringToFront";
mostCurrent._lb4.BringToFront();
 //BA.debugLineNum = 284;BA.debugLine="lb7.Visible = False";
mostCurrent._lb7.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 285;BA.debugLine="lb4.Visible = True";
mostCurrent._lb4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 286;BA.debugLine="b2.Visible = True";
mostCurrent._b2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 287;BA.debugLine="b2.BringToFront";
mostCurrent._b2.BringToFront();
 //BA.debugLineNum = 288;BA.debugLine="b3.Visible = False";
mostCurrent._b3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 6: {
 //BA.debugLineNum = 290;BA.debugLine="lb5.BringToFront";
mostCurrent._lb5.BringToFront();
 //BA.debugLineNum = 291;BA.debugLine="lb4.Visible = False";
mostCurrent._lb4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 292;BA.debugLine="lb5.Visible = True";
mostCurrent._lb5.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 293;BA.debugLine="b1.Visible = True";
mostCurrent._b1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 294;BA.debugLine="b1.BringToFront";
mostCurrent._b1.BringToFront();
 //BA.debugLineNum = 295;BA.debugLine="b2.Visible = False";
mostCurrent._b2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 7: {
 //BA.debugLineNum = 297;BA.debugLine="lb4.Visible = False";
mostCurrent._lb4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 298;BA.debugLine="lb5.Visible = False";
mostCurrent._lb5.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 299;BA.debugLine="b1.Visible = True";
mostCurrent._b1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 300;BA.debugLine="b1.BringToFront";
mostCurrent._b1.BringToFront();
 //BA.debugLineNum = 301;BA.debugLine="b2.Visible = True";
mostCurrent._b2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 302;BA.debugLine="b3.Visible = True";
mostCurrent._b3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 303;BA.debugLine="b1.Enabled = True";
mostCurrent._b1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 304;BA.debugLine="b2.Enabled = True";
mostCurrent._b2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 305;BA.debugLine="b3.Enabled = True";
mostCurrent._b3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 306;BA.debugLine="lbs.Enabled = False";
mostCurrent._lbs.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 307;BA.debugLine="lbs.Visible =False";
mostCurrent._lbs.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 308;BA.debugLine="pntut.Visible = False";
mostCurrent._pntut.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 309;BA.debugLine="hist.Enabled = True";
mostCurrent._hist.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 310;BA.debugLine="hist.Visible = True";
mostCurrent._hist.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 311;BA.debugLine="username.Enabled = True";
mostCurrent._username.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 312;BA.debugLine="StartActivity(\"gtut\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("gtut"));
 //BA.debugLineNum = 313;BA.debugLine="bg2.Play";
_bg2.Play();
 break; }
}
;
 //BA.debugLineNum = 329;BA.debugLine="End Sub";
return "";
}
public static String  _size() throws Exception{
double _xx = 0;
double _yy = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper _view = null;
 //BA.debugLineNum = 178;BA.debugLine="Sub Size";
 //BA.debugLineNum = 179;BA.debugLine="Dim xx,yy As Double";
_xx = 0;
_yy = 0;
 //BA.debugLineNum = 180;BA.debugLine="xx=((100%x)/450)/Density";
_xx = ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA))/(double)450)/(double)anywheresoftware.b4a.keywords.Common.Density;
 //BA.debugLineNum = 181;BA.debugLine="yy=((100%y)/ 800)/Density";
_yy = ((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA))/(double)800)/(double)anywheresoftware.b4a.keywords.Common.Density;
 //BA.debugLineNum = 182;BA.debugLine="For Each View As View In Activity.GetAllViewsRecu";
_view = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group4 = mostCurrent._activity.GetAllViewsRecursive();
final int groupLen4 = group4.getSize()
;int index4 = 0;
;
for (; index4 < groupLen4;index4++){
_view = (anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(group4.Get(index4)));
 //BA.debugLineNum = 183;BA.debugLine="View.Width=(View.Width) * xx";
_view.setWidth((int) ((_view.getWidth())*_xx));
 //BA.debugLineNum = 184;BA.debugLine="View.Left=(View.Left) * xx";
_view.setLeft((int) ((_view.getLeft())*_xx));
 //BA.debugLineNum = 185;BA.debugLine="View.Height=(View.Height)* yy";
_view.setHeight((int) ((_view.getHeight())*_yy));
 //BA.debugLineNum = 186;BA.debugLine="View.Top=(View.Top)*yy";
_view.setTop((int) ((_view.getTop())*_yy));
 }
};
 //BA.debugLineNum = 188;BA.debugLine="End Sub";
return "";
}
}
