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

public class settings extends Activity implements B4AActivity{
	public static settings mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.bsit2i.wordle", "com.bsit2i.wordle.settings");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (settings).");
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
		activityBA = new BA(this, layout, processBA, "com.bsit2i.wordle", "com.bsit2i.wordle.settings");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.bsit2i.wordle.settings", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (settings) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (settings) Resume **");
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
		return settings.class;
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
            BA.LogInfo("** Activity (settings) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (settings) Pause event (activity is not paused). **");
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
            settings mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (settings) Resume **");
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
public anywheresoftware.b4a.objects.ButtonWrapper _b1 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _cb1 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _cb2 = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _sb1 = null;
public anywheresoftware.b4a.objects.SeekBarWrapper _sb2 = null;
public static float _v1 = 0f;
public static float _v2 = 0f;
public com.bsit2i.wordle.main _main = null;
public com.bsit2i.wordle.game _game = null;
public com.bsit2i.wordle.history _history = null;
public com.bsit2i.wordle.credits _credits = null;
public com.bsit2i.wordle.loading _loading = null;
public com.bsit2i.wordle.gtut _gtut = null;
public com.bsit2i.wordle.musicservice _musicservice = null;
public com.bsit2i.wordle.starter _starter = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 24;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 26;BA.debugLine="Activity.LoadLayout(\"settings\")";
mostCurrent._activity.LoadLayout("settings",mostCurrent.activityBA);
 //BA.debugLineNum = 28;BA.debugLine="b1.Color = Colors.ARGB(0, 0, 0, 0)";
mostCurrent._b1.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 29;BA.debugLine="Main.bg1.play";
mostCurrent._main._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 30;BA.debugLine="sb1.Max = 100";
mostCurrent._sb1.setMax((int) (100));
 //BA.debugLineNum = 33;BA.debugLine="sb2.Max = 100";
mostCurrent._sb2.setMax((int) (100));
 //BA.debugLineNum = 37;BA.debugLine="If MusicService.bg1.IsInitialized Then";
if (mostCurrent._musicservice._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .IsInitialized()) { 
 //BA.debugLineNum = 38;BA.debugLine="Log(\"MusicService.bg1 is initialized\")";
anywheresoftware.b4a.keywords.Common.LogImpl("65636110","MusicService.bg1 is initialized",0);
 }else {
 //BA.debugLineNum = 40;BA.debugLine="Log(\"MusicService.bg1 is NOT initialized\")";
anywheresoftware.b4a.keywords.Common.LogImpl("65636112","MusicService.bg1 is NOT initialized",0);
 };
 //BA.debugLineNum = 42;BA.debugLine="sb1.Value = Main.vl1 * 100";
mostCurrent._sb1.setValue((int) (mostCurrent._main._vl1 /*float*/ *100));
 //BA.debugLineNum = 43;BA.debugLine="sb2.Value = Main.vl2 * 100";
mostCurrent._sb2.setValue((int) (mostCurrent._main._vl2 /*float*/ *100));
 //BA.debugLineNum = 44;BA.debugLine="Size";
_size();
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 139;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 140;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 141;BA.debugLine="Main.vl1 = v1";
mostCurrent._main._vl1 /*float*/  = _v1;
 //BA.debugLineNum = 142;BA.debugLine="Main.vl2 = v2";
mostCurrent._main._vl2 /*float*/  = _v2;
 //BA.debugLineNum = 143;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 144;BA.debugLine="StartActivity(\"Main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("Main"));
 //BA.debugLineNum = 145;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 147;BA.debugLine="Return False ' Allow default behavior for other k";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 148;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 64;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 60;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _b1_click() throws Exception{
String _data = "";
 //BA.debugLineNum = 126;BA.debugLine="Private Sub b1_Click";
 //BA.debugLineNum = 129;BA.debugLine="Dim data As String = v1 & \",\" & v2";
_data = BA.NumberToString(_v1)+","+BA.NumberToString(_v2);
 //BA.debugLineNum = 130;BA.debugLine="File.WriteString(File.DirInternal, \"volume.txt\",";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"volume.txt",_data);
 //BA.debugLineNum = 132;BA.debugLine="Main.vl1 = v1";
mostCurrent._main._vl1 /*float*/  = _v1;
 //BA.debugLineNum = 133;BA.debugLine="Main.vl2 = v2";
mostCurrent._main._vl2 /*float*/  = _v2;
 //BA.debugLineNum = 134;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 135;BA.debugLine="StartActivity(\"Main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("Main"));
 //BA.debugLineNum = 136;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 137;BA.debugLine="End Sub";
return "";
}
public static String  _cb1_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Private Sub cb1_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 117;BA.debugLine="If cb1.Checked = True Then";
if (mostCurrent._cb1.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 118;BA.debugLine="Main.cl2.play";
mostCurrent._main._cl2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 119;BA.debugLine="sb1.Value = 0";
mostCurrent._sb1.setValue((int) (0));
 }else {
 //BA.debugLineNum = 121;BA.debugLine="Main.cl2.play";
mostCurrent._main._cl2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 122;BA.debugLine="sb1.Value = Main.vl1 * 100";
mostCurrent._sb1.setValue((int) (mostCurrent._main._vl1 /*float*/ *100));
 };
 //BA.debugLineNum = 124;BA.debugLine="End Sub";
return "";
}
public static String  _cb2_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 107;BA.debugLine="Private Sub cb2_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 108;BA.debugLine="If cb2.Checked = True Then";
if (mostCurrent._cb2.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 109;BA.debugLine="sb2.Value = 0";
mostCurrent._sb2.setValue((int) (0));
 }else {
 //BA.debugLineNum = 111;BA.debugLine="Main.cl2.play";
mostCurrent._main._cl2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 112;BA.debugLine="sb2.Value = Main.vl2 * 100";
mostCurrent._sb2.setValue((int) (mostCurrent._main._vl2 /*float*/ *100));
 };
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Private b1 As Button";
mostCurrent._b1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Private cb1 As CheckBox";
mostCurrent._cb1 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private cb2 As CheckBox";
mostCurrent._cb2 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private sb1 As SeekBar";
mostCurrent._sb1 = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private sb2 As SeekBar";
mostCurrent._sb2 = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim v1 As Float = Main.vl1";
_v1 = mostCurrent._main._vl1 /*float*/ ;
 //BA.debugLineNum = 21;BA.debugLine="Dim v2 As Float = Main. vl2";
_v2 = mostCurrent._main._vl2 /*float*/ ;
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _savevolume(int _v) throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Sub SaveVolume(v As Int)";
 //BA.debugLineNum = 75;BA.debugLine="File.WriteString(File.DirInternal, \"volume.txt\",";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"volume.txt",BA.NumberToString(_v1)+","+BA.NumberToString(_v2));
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static void  _sb1_valuechanged(int _value,boolean _userchanged) throws Exception{
ResumableSub_sb1_ValueChanged rsub = new ResumableSub_sb1_ValueChanged(null,_value,_userchanged);
rsub.resume(processBA, null);
}
public static class ResumableSub_sb1_ValueChanged extends BA.ResumableSub {
public ResumableSub_sb1_ValueChanged(com.bsit2i.wordle.settings parent,int _value,boolean _userchanged) {
this.parent = parent;
this._value = _value;
this._userchanged = _userchanged;
}
com.bsit2i.wordle.settings parent;
int _value;
boolean _userchanged;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 94;BA.debugLine="v1 = Value / 100";
parent._v1 = (float) (_value/(double)100);
 //BA.debugLineNum = 95;BA.debugLine="Main.bg1.SetVolume(v1, v1)";
parent.mostCurrent._main._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .SetVolume(parent._v1,parent._v1);
 //BA.debugLineNum = 96;BA.debugLine="Main.bg2.SetVolume(v1, v1)";
parent.mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .SetVolume(parent._v1,parent._v1);
 //BA.debugLineNum = 97;BA.debugLine="File.WriteString(File.DirInternal, \"volume.txt\",";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"volume.txt",BA.NumberToString(parent._v1)+","+BA.NumberToString(parent._v2));
 //BA.debugLineNum = 98;BA.debugLine="Sleep(5)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (5));
this.state = 7;
return;
case 7:
//C
this.state = 1;
;
 //BA.debugLineNum = 99;BA.debugLine="If v1 <=0 Then";
if (true) break;

case 1:
//if
this.state = 6;
if (parent._v1<=0) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
 //BA.debugLineNum = 100;BA.debugLine="cb1.Checked = True";
parent.mostCurrent._cb1.setChecked(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 102;BA.debugLine="cb1.Checked = False";
parent.mostCurrent._cb1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 105;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _sb2_valuechanged(int _value,boolean _userchanged) throws Exception{
ResumableSub_sb2_ValueChanged rsub = new ResumableSub_sb2_ValueChanged(null,_value,_userchanged);
rsub.resume(processBA, null);
}
public static class ResumableSub_sb2_ValueChanged extends BA.ResumableSub {
public ResumableSub_sb2_ValueChanged(com.bsit2i.wordle.settings parent,int _value,boolean _userchanged) {
this.parent = parent;
this._value = _value;
this._userchanged = _userchanged;
}
com.bsit2i.wordle.settings parent;
int _value;
boolean _userchanged;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 80;BA.debugLine="v2 = Value / 100";
parent._v2 = (float) (_value/(double)100);
 //BA.debugLineNum = 81;BA.debugLine="Main.cl1.SetVolume(v2, v2)";
parent.mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .SetVolume(parent._v2,parent._v2);
 //BA.debugLineNum = 82;BA.debugLine="Main.cl2.SetVolume(v2, v2)";
parent.mostCurrent._main._cl2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .SetVolume(parent._v2,parent._v2);
 //BA.debugLineNum = 83;BA.debugLine="File.WriteString(File.DirInternal, \"volume.txt\",";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"volume.txt",BA.NumberToString(parent._v1)+","+BA.NumberToString(parent._v2));
 //BA.debugLineNum = 85;BA.debugLine="Sleep(5)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (5));
this.state = 7;
return;
case 7:
//C
this.state = 1;
;
 //BA.debugLineNum = 86;BA.debugLine="If v2 <=0 Then";
if (true) break;

case 1:
//if
this.state = 6;
if (parent._v2<=0) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
 //BA.debugLineNum = 87;BA.debugLine="cb2.Checked = True";
parent.mostCurrent._cb2.setChecked(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 89;BA.debugLine="cb2.Checked = False";
parent.mostCurrent._cb2.setChecked(anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _size() throws Exception{
double _xx = 0;
double _yy = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper _view = null;
 //BA.debugLineNum = 48;BA.debugLine="Sub Size";
 //BA.debugLineNum = 49;BA.debugLine="Dim xx,yy As Double";
_xx = 0;
_yy = 0;
 //BA.debugLineNum = 50;BA.debugLine="xx=((100%x)/450)/Density";
_xx = ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA))/(double)450)/(double)anywheresoftware.b4a.keywords.Common.Density;
 //BA.debugLineNum = 51;BA.debugLine="yy=((100%y)/ 800)/Density";
_yy = ((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA))/(double)800)/(double)anywheresoftware.b4a.keywords.Common.Density;
 //BA.debugLineNum = 52;BA.debugLine="For Each View As View In Activity.GetAllViewsRecu";
_view = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group4 = mostCurrent._activity.GetAllViewsRecursive();
final int groupLen4 = group4.getSize()
;int index4 = 0;
;
for (; index4 < groupLen4;index4++){
_view = (anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(group4.Get(index4)));
 //BA.debugLineNum = 53;BA.debugLine="View.Width=(View.Width) * xx";
_view.setWidth((int) ((_view.getWidth())*_xx));
 //BA.debugLineNum = 54;BA.debugLine="View.Left=(View.Left) * xx";
_view.setLeft((int) ((_view.getLeft())*_xx));
 //BA.debugLineNum = 55;BA.debugLine="View.Height=(View.Height)* yy";
_view.setHeight((int) ((_view.getHeight())*_yy));
 //BA.debugLineNum = 56;BA.debugLine="View.Top=(View.Top)*yy";
_view.setTop((int) ((_view.getTop())*_yy));
 }
};
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
}
