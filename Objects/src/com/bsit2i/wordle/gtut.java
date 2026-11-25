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

public class gtut extends Activity implements B4AActivity{
	public static gtut mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.bsit2i.wordle", "com.bsit2i.wordle.gtut");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (gtut).");
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
		activityBA = new BA(this, layout, processBA, "com.bsit2i.wordle", "com.bsit2i.wordle.gtut");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.bsit2i.wordle.gtut", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (gtut) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (gtut) Resume **");
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
		return gtut.class;
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
            BA.LogInfo("** Activity (gtut) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (gtut) Pause event (activity is not paused). **");
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
            gtut mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (gtut) Resume **");
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
public anywheresoftware.b4a.objects.PanelWrapper _pn11 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn12 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn13 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn14 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn15 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn16 = null;
public anywheresoftware.b4a.objects.LabelWrapper _l2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l2t1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l2t2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l2t3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l2t4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l2t5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l2t6 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l3t1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l3t2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l3t3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l3t4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l3t5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l3t6 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t6 = null;
public anywheresoftware.b4a.objects.PanelWrapper _line7 = null;
public anywheresoftware.b4a.objects.PanelWrapper _line1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _line2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _line3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _hint1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _hint2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _hint3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _hint4 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _menu = null;
public anywheresoftware.b4a.objects.ButtonWrapper _pu1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _pu2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _pu3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _submit = null;
public anywheresoftware.b4a.objects.ButtonWrapper _surr = null;
public anywheresoftware.b4a.objects.ButtonWrapper _nex = null;
public static int _tut = 0;
public anywheresoftware.b4a.objects.PanelWrapper _pntut = null;
public anywheresoftware.b4a.objects.ButtonWrapper _bac = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt10 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt11 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt12 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt13 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt14 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt15 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt16 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt17 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt18 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt19 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt20 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt21 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt22 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt23 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt24 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt5 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt6 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt7 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt8 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbt9 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn21 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn22 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn23 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn24 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn25 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn26 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn31 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn32 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn33 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn34 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn35 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn36 = null;
public anywheresoftware.b4a.objects.Timer _time = null;
public static int _seconds = 0;
public com.bsit2i.wordle.main _main = null;
public com.bsit2i.wordle.game _game = null;
public com.bsit2i.wordle.history _history = null;
public com.bsit2i.wordle.settings _settings = null;
public com.bsit2i.wordle.credits _credits = null;
public com.bsit2i.wordle.loading _loading = null;
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
 //BA.debugLineNum = 114;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 115;BA.debugLine="Size";
_size();
 //BA.debugLineNum = 117;BA.debugLine="Activity.LoadLayout(\"mg_main1\")";
mostCurrent._activity.LoadLayout("mg_main1",mostCurrent.activityBA);
 //BA.debugLineNum = 118;BA.debugLine="bac.Color = Colors.ARGB(0, 0, 0, 0)";
mostCurrent._bac.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 119;BA.debugLine="nex.Color = Colors.ARGB(0, 0, 0, 0)";
mostCurrent._nex.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 121;BA.debugLine="pn11.Color = Colors.Green";
mostCurrent._pn11.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 122;BA.debugLine="pn12.Color = Colors.Red";
mostCurrent._pn12.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 123;BA.debugLine="pn13.Color = Colors.Red";
mostCurrent._pn13.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 124;BA.debugLine="pn14.Color = Colors.Green";
mostCurrent._pn14.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 125;BA.debugLine="pn15.Color = Colors.Green";
mostCurrent._pn15.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 126;BA.debugLine="pn16.Color = Colors.Blue";
mostCurrent._pn16.setColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 127;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 128;BA.debugLine="ShowTutorialHighlight";
_showtutorialhighlight();
 //BA.debugLineNum = 129;BA.debugLine="nex.Visible = True";
mostCurrent._nex.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 130;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 131;BA.debugLine="lbt1.BringToFront";
mostCurrent._lbt1.BringToFront();
 //BA.debugLineNum = 132;BA.debugLine="submit.Enabled = False";
mostCurrent._submit.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 133;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 134;BA.debugLine="pu2.Enabled = False";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 135;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 136;BA.debugLine="surr.Enabled = False";
mostCurrent._surr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 137;BA.debugLine="menu.Enabled = False";
mostCurrent._menu.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 141;BA.debugLine="time.Initialize(\"time\", 1000) ' 1000 ms = 1 secon";
mostCurrent._time.Initialize(processBA,"time",(long) (1000));
 //BA.debugLineNum = 142;BA.debugLine="time.Enabled = True";
mostCurrent._time.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 143;BA.debugLine="Main.bg2.Play";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 144;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 162;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 164;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 158;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 160;BA.debugLine="End Sub";
return "";
}
public static String  _bac_click() throws Exception{
 //BA.debugLineNum = 516;BA.debugLine="Private Sub bac_Click";
 //BA.debugLineNum = 517;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 518;BA.debugLine="tut = tut - 1";
_tut = (int) (_tut-1);
 //BA.debugLineNum = 519;BA.debugLine="If tut <= 0 Then";
if (_tut<=0) { 
 //BA.debugLineNum = 520;BA.debugLine="bac.Visible = False";
mostCurrent._bac.setVisible(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 522;BA.debugLine="bac.Visible = True";
mostCurrent._bac.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 524;BA.debugLine="ShowTutorialHighlight";
_showtutorialhighlight();
 //BA.debugLineNum = 525;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Private pn11 As Panel";
mostCurrent._pn11 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private pn12 As Panel";
mostCurrent._pn12 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private pn13 As Panel";
mostCurrent._pn13 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private pn14 As Panel";
mostCurrent._pn14 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private pn15 As Panel";
mostCurrent._pn15 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private pn16 As Panel";
mostCurrent._pn16 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private l2 As Label";
mostCurrent._l2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private l2t1 As EditText";
mostCurrent._l2t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private l2t2 As EditText";
mostCurrent._l2t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private l2t3 As EditText";
mostCurrent._l2t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private l2t4 As EditText";
mostCurrent._l2t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private l2t5 As EditText";
mostCurrent._l2t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private l2t6 As EditText";
mostCurrent._l2t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private l3t1 As EditText";
mostCurrent._l3t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private l3t2 As EditText";
mostCurrent._l3t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private l3t3 As EditText";
mostCurrent._l3t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private l3t4 As EditText";
mostCurrent._l3t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private l3t5 As EditText";
mostCurrent._l3t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private l3t6 As EditText";
mostCurrent._l3t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private l7t1 As EditText";
mostCurrent._l7t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private l7t2 As EditText";
mostCurrent._l7t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private l7t3 As EditText";
mostCurrent._l7t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private l7t4 As EditText";
mostCurrent._l7t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private l7t5 As EditText";
mostCurrent._l7t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private l7t6 As EditText";
mostCurrent._l7t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private line7 As Panel";
mostCurrent._line7 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private line1 As Panel";
mostCurrent._line1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private line2 As Panel";
mostCurrent._line2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private line3 As Panel";
mostCurrent._line3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private hint1 As Panel";
mostCurrent._hint1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private hint2 As Panel";
mostCurrent._hint2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private hint3 As Panel";
mostCurrent._hint3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private hint4 As Panel";
mostCurrent._hint4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private menu As Button";
mostCurrent._menu = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private pu1 As Button";
mostCurrent._pu1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 51;BA.debugLine="Private pu2 As Button";
mostCurrent._pu2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private pu3 As Button";
mostCurrent._pu3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Private submit As Button";
mostCurrent._submit = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 54;BA.debugLine="Private surr As Button";
mostCurrent._surr = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 55;BA.debugLine="Private nex As Button";
mostCurrent._nex = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Dim tut As Int = 0";
_tut = (int) (0);
 //BA.debugLineNum = 57;BA.debugLine="Private pntut As Panel";
mostCurrent._pntut = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Private bac As Button";
mostCurrent._bac = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 59;BA.debugLine="Private lbt1 As Label";
mostCurrent._lbt1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Private lbt10 As Label";
mostCurrent._lbt10 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Private lbt11 As Label";
mostCurrent._lbt11 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Private lbt12 As Label";
mostCurrent._lbt12 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Private lbt13 As Label";
mostCurrent._lbt13 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Private lbt14 As Label";
mostCurrent._lbt14 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Private lbt15 As Label";
mostCurrent._lbt15 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 66;BA.debugLine="Private lbt16 As Label";
mostCurrent._lbt16 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Private lbt17 As Label";
mostCurrent._lbt17 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 68;BA.debugLine="Private lbt18 As Label";
mostCurrent._lbt18 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Private lbt19 As Label";
mostCurrent._lbt19 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 70;BA.debugLine="Private lbt2 As Label";
mostCurrent._lbt2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 71;BA.debugLine="Private lbt20 As Label";
mostCurrent._lbt20 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 72;BA.debugLine="Private lbt21 As Label";
mostCurrent._lbt21 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Private lbt22 As Label";
mostCurrent._lbt22 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Private lbt23 As Label";
mostCurrent._lbt23 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Private lbt24 As Label";
mostCurrent._lbt24 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 76;BA.debugLine="Private lbt3 As Label";
mostCurrent._lbt3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 77;BA.debugLine="Private lbt4 As Label";
mostCurrent._lbt4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 78;BA.debugLine="Private lbt5 As Label";
mostCurrent._lbt5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 79;BA.debugLine="Private lbt6 As Label";
mostCurrent._lbt6 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 80;BA.debugLine="Private lbt7 As Label";
mostCurrent._lbt7 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 81;BA.debugLine="Private lbt8 As Label";
mostCurrent._lbt8 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 82;BA.debugLine="Private lbt9 As Label";
mostCurrent._lbt9 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 83;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 84;BA.debugLine="Private pn21 As Panel";
mostCurrent._pn21 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 85;BA.debugLine="Private pn22 As Panel";
mostCurrent._pn22 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 86;BA.debugLine="Private pn23 As Panel";
mostCurrent._pn23 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 87;BA.debugLine="Private pn24 As Panel";
mostCurrent._pn24 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Private pn25 As Panel";
mostCurrent._pn25 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 89;BA.debugLine="Private pn26 As Panel";
mostCurrent._pn26 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 90;BA.debugLine="Private pn31 As Panel";
mostCurrent._pn31 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 91;BA.debugLine="Private pn32 As Panel";
mostCurrent._pn32 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 92;BA.debugLine="Private pn33 As Panel";
mostCurrent._pn33 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 93;BA.debugLine="Private pn34 As Panel";
mostCurrent._pn34 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 94;BA.debugLine="Private pn35 As Panel";
mostCurrent._pn35 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 95;BA.debugLine="Private pn36 As Panel";
mostCurrent._pn36 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 96;BA.debugLine="Dim time As Timer";
mostCurrent._time = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 97;BA.debugLine="Dim seconds As Int = 0";
_seconds = (int) (0);
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _nex_click() throws Exception{
 //BA.debugLineNum = 505;BA.debugLine="Private Sub nex_Click";
 //BA.debugLineNum = 506;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 507;BA.debugLine="tut = tut + 1";
_tut = (int) (_tut+1);
 //BA.debugLineNum = 508;BA.debugLine="If tut > 0 Then";
if (_tut>0) { 
 //BA.debugLineNum = 509;BA.debugLine="bac.Visible = True";
mostCurrent._bac.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 511;BA.debugLine="bac.Visible = False";
mostCurrent._bac.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 513;BA.debugLine="ShowTutorialHighlight";
_showtutorialhighlight();
 //BA.debugLineNum = 514;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _showtutorialhighlight() throws Exception{
anywheresoftware.b4a.objects.ConcreteViewWrapper _target = null;
anywheresoftware.b4a.objects.drawable.ColorDrawable _darkbg = null;
 //BA.debugLineNum = 167;BA.debugLine="Sub ShowTutorialHighlight()";
 //BA.debugLineNum = 168;BA.debugLine="Dim Target As View";
_target = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
 //BA.debugLineNum = 170;BA.debugLine="pntut.Visible = True";
mostCurrent._pntut.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 171;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 174;BA.debugLine="Dim darkBG As ColorDrawable";
_darkbg = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 175;BA.debugLine="darkBG.Initialize(Colors.ARGB(180, 0, 0, 0), 0)";
_darkbg.Initialize(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (180),(int) (0),(int) (0),(int) (0)),(int) (0));
 //BA.debugLineNum = 176;BA.debugLine="pntut.Background = darkBG";
mostCurrent._pntut.setBackground((android.graphics.drawable.Drawable)(_darkbg.getObject()));
 //BA.debugLineNum = 181;BA.debugLine="Select tut";
switch (_tut) {
case 0: {
 //BA.debugLineNum = 183;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 184;BA.debugLine="lbt1.Visible = True";
mostCurrent._lbt1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 185;BA.debugLine="lbt2.Visible = False";
mostCurrent._lbt2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 186;BA.debugLine="lbt1.BringToFront";
mostCurrent._lbt1.BringToFront();
 break; }
case 1: {
 //BA.debugLineNum = 188;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 189;BA.debugLine="lbt1.Visible = False";
mostCurrent._lbt1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 190;BA.debugLine="lbt2.Visible = True";
mostCurrent._lbt2.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 191;BA.debugLine="lbt3.Visible = False";
mostCurrent._lbt3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 192;BA.debugLine="line1.BringToFront";
mostCurrent._line1.BringToFront();
 //BA.debugLineNum = 193;BA.debugLine="lbt2.BringToFront";
mostCurrent._lbt2.BringToFront();
 //BA.debugLineNum = 194;BA.debugLine="pn11.BringToFront";
mostCurrent._pn11.BringToFront();
 //BA.debugLineNum = 195;BA.debugLine="pn12.BringToFront";
mostCurrent._pn12.BringToFront();
 //BA.debugLineNum = 196;BA.debugLine="pn13.BringToFront";
mostCurrent._pn13.BringToFront();
 //BA.debugLineNum = 197;BA.debugLine="pn14.BringToFront";
mostCurrent._pn14.BringToFront();
 //BA.debugLineNum = 198;BA.debugLine="pn15.BringToFront";
mostCurrent._pn15.BringToFront();
 //BA.debugLineNum = 199;BA.debugLine="pn16.BringToFront";
mostCurrent._pn16.BringToFront();
 break; }
case 2: {
 //BA.debugLineNum = 203;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 204;BA.debugLine="lbt2.Visible = False";
mostCurrent._lbt2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 205;BA.debugLine="lbt3.Visible = True";
mostCurrent._lbt3.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 206;BA.debugLine="lbt4.Visible = False";
mostCurrent._lbt4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 207;BA.debugLine="lbt3.BringToFront";
mostCurrent._lbt3.BringToFront();
 //BA.debugLineNum = 209;BA.debugLine="pn11.BringToFront";
mostCurrent._pn11.BringToFront();
 //BA.debugLineNum = 210;BA.debugLine="pn14.BringToFront";
mostCurrent._pn14.BringToFront();
 //BA.debugLineNum = 211;BA.debugLine="pn15.BringToFront";
mostCurrent._pn15.BringToFront();
 break; }
case 3: {
 //BA.debugLineNum = 215;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 216;BA.debugLine="lbt3.Visible = False";
mostCurrent._lbt3.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 217;BA.debugLine="lbt4.Visible = True";
mostCurrent._lbt4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 218;BA.debugLine="lbt5.Visible = False";
mostCurrent._lbt5.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 219;BA.debugLine="lbt4.BringToFront";
mostCurrent._lbt4.BringToFront();
 //BA.debugLineNum = 221;BA.debugLine="pn16.BringToFront";
mostCurrent._pn16.BringToFront();
 break; }
case 4: {
 //BA.debugLineNum = 224;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 225;BA.debugLine="lbt4.Visible = False";
mostCurrent._lbt4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 226;BA.debugLine="lbt5.Visible = True";
mostCurrent._lbt5.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 227;BA.debugLine="lbt6.Visible = False";
mostCurrent._lbt6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 228;BA.debugLine="lbt5.BringToFront";
mostCurrent._lbt5.BringToFront();
 //BA.debugLineNum = 230;BA.debugLine="pn12.BringToFront";
mostCurrent._pn12.BringToFront();
 //BA.debugLineNum = 231;BA.debugLine="pn13.BringToFront";
mostCurrent._pn13.BringToFront();
 break; }
case 5: {
 //BA.debugLineNum = 234;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 235;BA.debugLine="lbt5.Visible = False";
mostCurrent._lbt5.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 236;BA.debugLine="lbt6.Visible = True";
mostCurrent._lbt6.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 237;BA.debugLine="lbt7.Visible = False";
mostCurrent._lbt7.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 238;BA.debugLine="lbt6.BringToFront";
mostCurrent._lbt6.BringToFront();
 //BA.debugLineNum = 240;BA.debugLine="hint1.BringToFront";
mostCurrent._hint1.BringToFront();
 //BA.debugLineNum = 241;BA.debugLine="hint2.BringToFront";
mostCurrent._hint2.BringToFront();
 //BA.debugLineNum = 242;BA.debugLine="hint3.BringToFront";
mostCurrent._hint3.BringToFront();
 //BA.debugLineNum = 243;BA.debugLine="hint4.BringToFront";
mostCurrent._hint4.BringToFront();
 break; }
case 6: {
 //BA.debugLineNum = 246;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 247;BA.debugLine="lbt6.Visible = False";
mostCurrent._lbt6.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 248;BA.debugLine="lbt7.Visible = True";
mostCurrent._lbt7.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 249;BA.debugLine="lbt8.Visible = False";
mostCurrent._lbt8.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 250;BA.debugLine="lbt7.BringToFront";
mostCurrent._lbt7.BringToFront();
 //BA.debugLineNum = 252;BA.debugLine="hint1.BringToFront";
mostCurrent._hint1.BringToFront();
 //BA.debugLineNum = 253;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 254;BA.debugLine="pu2.Enabled = False";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 255;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 7: {
 //BA.debugLineNum = 258;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 259;BA.debugLine="lbt7.Visible = False";
mostCurrent._lbt7.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 260;BA.debugLine="lbt8.Visible = True";
mostCurrent._lbt8.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 261;BA.debugLine="lbt9.Visible = False";
mostCurrent._lbt9.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 262;BA.debugLine="lbt8.BringToFront";
mostCurrent._lbt8.BringToFront();
 //BA.debugLineNum = 264;BA.debugLine="pu1.Enabled =True";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 265;BA.debugLine="pu2.Enabled = True";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 266;BA.debugLine="pu3.Enabled = True";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 8: {
 //BA.debugLineNum = 269;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 270;BA.debugLine="lbt8.Visible = False";
mostCurrent._lbt8.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 271;BA.debugLine="lbt9.Visible = True";
mostCurrent._lbt9.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 272;BA.debugLine="lbt10.Visible = False";
mostCurrent._lbt10.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 273;BA.debugLine="lbt9.BringToFront";
mostCurrent._lbt9.BringToFront();
 //BA.debugLineNum = 274;BA.debugLine="pu1.Enabled = True";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 275;BA.debugLine="pu2.Enabled = False";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 276;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 9: {
 //BA.debugLineNum = 279;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 280;BA.debugLine="lbt9.Visible = False";
mostCurrent._lbt9.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 281;BA.debugLine="lbt10.Visible = True";
mostCurrent._lbt10.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 282;BA.debugLine="lbt11.Visible = False";
mostCurrent._lbt11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 283;BA.debugLine="lbt10.BringToFront";
mostCurrent._lbt10.BringToFront();
 //BA.debugLineNum = 284;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 285;BA.debugLine="pu2.Enabled = True";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 286;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 10: {
 //BA.debugLineNum = 289;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 290;BA.debugLine="lbt10.Visible = False";
mostCurrent._lbt10.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 291;BA.debugLine="lbt11.Visible = True";
mostCurrent._lbt11.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 292;BA.debugLine="lbt12.Visible = False";
mostCurrent._lbt12.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 293;BA.debugLine="lbt11.BringToFront";
mostCurrent._lbt11.BringToFront();
 //BA.debugLineNum = 294;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 295;BA.debugLine="pu2.Enabled = False";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 296;BA.debugLine="pu3.Enabled = True";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 11: {
 //BA.debugLineNum = 299;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 300;BA.debugLine="lbt11.Visible = False";
mostCurrent._lbt11.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 301;BA.debugLine="lbt12.Visible = True";
mostCurrent._lbt12.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 302;BA.debugLine="lbt13.Visible = False";
mostCurrent._lbt13.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 303;BA.debugLine="lbt12.BringToFront";
mostCurrent._lbt12.BringToFront();
 //BA.debugLineNum = 304;BA.debugLine="pu1.Enabled = True";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 305;BA.debugLine="pu2.Enabled = True";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 306;BA.debugLine="pu3.Enabled = True";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 12: {
 //BA.debugLineNum = 309;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 310;BA.debugLine="lbt12.Visible = False";
mostCurrent._lbt12.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 311;BA.debugLine="lbt13.Visible = True";
mostCurrent._lbt13.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 312;BA.debugLine="lbt14.Visible = False";
mostCurrent._lbt14.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 313;BA.debugLine="lbt13.BringToFront";
mostCurrent._lbt13.BringToFront();
 //BA.debugLineNum = 314;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 315;BA.debugLine="pu2.Enabled = True";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 316;BA.debugLine="pu3.Enabled = True";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 13: {
 //BA.debugLineNum = 319;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 320;BA.debugLine="lbt13.Visible = False";
mostCurrent._lbt13.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 321;BA.debugLine="lbt14.Visible = True";
mostCurrent._lbt14.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 322;BA.debugLine="lbt15.Visible = False";
mostCurrent._lbt15.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 323;BA.debugLine="lbt14.BringToFront";
mostCurrent._lbt14.BringToFront();
 //BA.debugLineNum = 324;BA.debugLine="pu1.Enabled = True";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 325;BA.debugLine="pu2.Enabled = True";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 326;BA.debugLine="pu3.Enabled = True";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 327;BA.debugLine="l2t3.Text = \"\"";
mostCurrent._l2t3.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 328;BA.debugLine="pn23.Color = 0xFFD5D5D5";
mostCurrent._pn23.setColor(((int)0xffd5d5d5));
 break; }
case 14: {
 //BA.debugLineNum = 330;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 331;BA.debugLine="lbt14.Visible = False";
mostCurrent._lbt14.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 332;BA.debugLine="lbt15.Visible = True";
mostCurrent._lbt15.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 333;BA.debugLine="lbt16.Visible = False";
mostCurrent._lbt16.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 334;BA.debugLine="lbt15.BringToFront";
mostCurrent._lbt15.BringToFront();
 //BA.debugLineNum = 336;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 337;BA.debugLine="pu2.Enabled = True";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 338;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 340;BA.debugLine="line2.BringToFront";
mostCurrent._line2.BringToFront();
 //BA.debugLineNum = 341;BA.debugLine="pn22.BringToFront";
mostCurrent._pn22.BringToFront();
 //BA.debugLineNum = 342;BA.debugLine="pn23.BringToFront";
mostCurrent._pn23.BringToFront();
 //BA.debugLineNum = 343;BA.debugLine="l2t3.Text = \"S\"";
mostCurrent._l2t3.setText(BA.ObjectToCharSequence("S"));
 //BA.debugLineNum = 344;BA.debugLine="pn23.color = Colors.Blue";
mostCurrent._pn23.setColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 345;BA.debugLine="l2t2.text = \"\"";
mostCurrent._l2t2.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 346;BA.debugLine="pn22.Color = 0xFFD5D5D5";
mostCurrent._pn22.setColor(((int)0xffd5d5d5));
 break; }
case 15: {
 //BA.debugLineNum = 349;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 350;BA.debugLine="lbt15.Visible = False";
mostCurrent._lbt15.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 351;BA.debugLine="lbt16.Visible = True";
mostCurrent._lbt16.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 352;BA.debugLine="lbt17.Visible = False";
mostCurrent._lbt17.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 353;BA.debugLine="lbt16.BringToFront";
mostCurrent._lbt16.BringToFront();
 //BA.debugLineNum = 355;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 356;BA.debugLine="pu2.Enabled = False";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 357;BA.debugLine="pu3.Enabled = True";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 359;BA.debugLine="line2.BringToFront";
mostCurrent._line2.BringToFront();
 //BA.debugLineNum = 360;BA.debugLine="pn22.BringToFront";
mostCurrent._pn22.BringToFront();
 //BA.debugLineNum = 361;BA.debugLine="pn23.BringToFront";
mostCurrent._pn23.BringToFront();
 //BA.debugLineNum = 362;BA.debugLine="l2t2.Text = \"A\"";
mostCurrent._l2t2.setText(BA.ObjectToCharSequence("A"));
 //BA.debugLineNum = 363;BA.debugLine="pn22.Color = Colors.Green";
mostCurrent._pn22.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 break; }
case 16: {
 //BA.debugLineNum = 366;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 367;BA.debugLine="lbt16.Visible = False";
mostCurrent._lbt16.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 368;BA.debugLine="lbt17.Visible = True";
mostCurrent._lbt17.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 369;BA.debugLine="lbt18.Visible = False";
mostCurrent._lbt18.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 370;BA.debugLine="lbt17.BringToFront";
mostCurrent._lbt17.BringToFront();
 //BA.debugLineNum = 372;BA.debugLine="pn22.BringToFront";
mostCurrent._pn22.BringToFront();
 //BA.debugLineNum = 373;BA.debugLine="pn23.BringToFront";
mostCurrent._pn23.BringToFront();
 //BA.debugLineNum = 374;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 375;BA.debugLine="pu2.Enabled = True";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 376;BA.debugLine="pu3.Enabled = True";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 379;BA.debugLine="l2t1.Text = \"\"";
mostCurrent._l2t1.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 380;BA.debugLine="l2t4.Text = \"\"";
mostCurrent._l2t4.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 381;BA.debugLine="l2t5.Text = \"\"";
mostCurrent._l2t5.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 382;BA.debugLine="l2t6.Text = \"\"";
mostCurrent._l2t6.setText(BA.ObjectToCharSequence(""));
 break; }
case 17: {
 //BA.debugLineNum = 385;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 386;BA.debugLine="lbt17.Visible = False";
mostCurrent._lbt17.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 387;BA.debugLine="lbt18.Visible = True";
mostCurrent._lbt18.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 388;BA.debugLine="lbt19.Visible = False";
mostCurrent._lbt19.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 389;BA.debugLine="lbt18.BringToFront";
mostCurrent._lbt18.BringToFront();
 //BA.debugLineNum = 391;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 392;BA.debugLine="pu2.Enabled = False";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 393;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 395;BA.debugLine="line2.BringToFront";
mostCurrent._line2.BringToFront();
 //BA.debugLineNum = 396;BA.debugLine="pn22.BringToFront";
mostCurrent._pn22.BringToFront();
 //BA.debugLineNum = 397;BA.debugLine="pn23.BringToFront";
mostCurrent._pn23.BringToFront();
 //BA.debugLineNum = 399;BA.debugLine="l2t1.Text = \"G\"";
mostCurrent._l2t1.setText(BA.ObjectToCharSequence("G"));
 //BA.debugLineNum = 400;BA.debugLine="l2t4.Text = \"E\"";
mostCurrent._l2t4.setText(BA.ObjectToCharSequence("E"));
 //BA.debugLineNum = 401;BA.debugLine="l2t5.Text = \"R\"";
mostCurrent._l2t5.setText(BA.ObjectToCharSequence("R"));
 //BA.debugLineNum = 402;BA.debugLine="l2t6.Text = \"M\"";
mostCurrent._l2t6.setText(BA.ObjectToCharSequence("M"));
 //BA.debugLineNum = 404;BA.debugLine="submit.Enabled = False";
mostCurrent._submit.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 18: {
 //BA.debugLineNum = 407;BA.debugLine="line7.BringToFront";
mostCurrent._line7.BringToFront();
 //BA.debugLineNum = 408;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 409;BA.debugLine="lbt18.Visible = False";
mostCurrent._lbt18.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 410;BA.debugLine="lbt19.Visible = True";
mostCurrent._lbt19.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 411;BA.debugLine="lbt20.Visible = False";
mostCurrent._lbt20.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 412;BA.debugLine="lbt19.BringToFront";
mostCurrent._lbt19.BringToFront();
 //BA.debugLineNum = 414;BA.debugLine="submit.Enabled = True";
mostCurrent._submit.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 416;BA.debugLine="pn21.Color = 0xFFD5D5D5";
mostCurrent._pn21.setColor(((int)0xffd5d5d5));
 //BA.debugLineNum = 417;BA.debugLine="pn24.Color = 0xFFD5D5D5";
mostCurrent._pn24.setColor(((int)0xffd5d5d5));
 //BA.debugLineNum = 418;BA.debugLine="pn25.Color =0xFFD5D5D5";
mostCurrent._pn25.setColor(((int)0xffd5d5d5));
 //BA.debugLineNum = 419;BA.debugLine="pn26.Color = 0xFFD5D5D5";
mostCurrent._pn26.setColor(((int)0xffd5d5d5));
 break; }
case 19: {
 //BA.debugLineNum = 422;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 423;BA.debugLine="lbt19.Visible = False";
mostCurrent._lbt19.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 424;BA.debugLine="lbt20.Visible = True";
mostCurrent._lbt20.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 425;BA.debugLine="lbt21.Visible = False";
mostCurrent._lbt21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 426;BA.debugLine="lbt20.BringToFront";
mostCurrent._lbt20.BringToFront();
 //BA.debugLineNum = 428;BA.debugLine="Panel1.BringToFront";
mostCurrent._panel1.BringToFront();
 //BA.debugLineNum = 429;BA.debugLine="line1.BringToFront";
mostCurrent._line1.BringToFront();
 //BA.debugLineNum = 430;BA.debugLine="line3.BringToFront";
mostCurrent._line3.BringToFront();
 //BA.debugLineNum = 431;BA.debugLine="line2.BringToFront";
mostCurrent._line2.BringToFront();
 //BA.debugLineNum = 432;BA.debugLine="pn11.BringToFront";
mostCurrent._pn11.BringToFront();
 //BA.debugLineNum = 433;BA.debugLine="pn12.BringToFront";
mostCurrent._pn12.BringToFront();
 //BA.debugLineNum = 434;BA.debugLine="pn13.BringToFront";
mostCurrent._pn13.BringToFront();
 //BA.debugLineNum = 435;BA.debugLine="pn14.BringToFront";
mostCurrent._pn14.BringToFront();
 //BA.debugLineNum = 436;BA.debugLine="pn15.BringToFront";
mostCurrent._pn15.BringToFront();
 //BA.debugLineNum = 437;BA.debugLine="pn16.BringToFront";
mostCurrent._pn16.BringToFront();
 //BA.debugLineNum = 438;BA.debugLine="pn22.BringToFront";
mostCurrent._pn22.BringToFront();
 //BA.debugLineNum = 439;BA.debugLine="pn23.BringToFront";
mostCurrent._pn23.BringToFront();
 //BA.debugLineNum = 441;BA.debugLine="pn21.Color = Colors.Green";
mostCurrent._pn21.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 442;BA.debugLine="pn24.Color = Colors.Green";
mostCurrent._pn24.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 443;BA.debugLine="pn25.Color = Colors.Green";
mostCurrent._pn25.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 444;BA.debugLine="pn26.Color = Colors.Blue";
mostCurrent._pn26.setColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 447;BA.debugLine="submit.Enabled = False";
mostCurrent._submit.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 448;BA.debugLine="surr.Enabled = False";
mostCurrent._surr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 20: {
 //BA.debugLineNum = 452;BA.debugLine="line7.BringToFront";
mostCurrent._line7.BringToFront();
 //BA.debugLineNum = 453;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 454;BA.debugLine="lbt20.Visible = False";
mostCurrent._lbt20.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 455;BA.debugLine="lbt21.Visible = True";
mostCurrent._lbt21.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 456;BA.debugLine="lbt22.Visible = False";
mostCurrent._lbt22.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 457;BA.debugLine="lbt21.BringToFront";
mostCurrent._lbt21.BringToFront();
 //BA.debugLineNum = 458;BA.debugLine="surr.Enabled = True";
mostCurrent._surr.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 21: {
 //BA.debugLineNum = 462;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 463;BA.debugLine="lbt21.Visible = False";
mostCurrent._lbt21.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 464;BA.debugLine="lbt22.Visible = True";
mostCurrent._lbt22.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 465;BA.debugLine="lbt23.Visible = False";
mostCurrent._lbt23.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 466;BA.debugLine="lbt22.BringToFront";
mostCurrent._lbt22.BringToFront();
 //BA.debugLineNum = 468;BA.debugLine="surr.Enabled = True";
mostCurrent._surr.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 469;BA.debugLine="line7.BringToFront";
mostCurrent._line7.BringToFront();
 //BA.debugLineNum = 470;BA.debugLine="l7t1.Text = \"G\"";
mostCurrent._l7t1.setText(BA.ObjectToCharSequence("G"));
 //BA.debugLineNum = 471;BA.debugLine="l7t2.Text = \"A\"";
mostCurrent._l7t2.setText(BA.ObjectToCharSequence("A"));
 //BA.debugLineNum = 472;BA.debugLine="l7t3.Text = \"M\"";
mostCurrent._l7t3.setText(BA.ObjectToCharSequence("M"));
 //BA.debugLineNum = 473;BA.debugLine="l7t4.Text = \"E\"";
mostCurrent._l7t4.setText(BA.ObjectToCharSequence("E"));
 //BA.debugLineNum = 474;BA.debugLine="l7t5.Text = \"R\"";
mostCurrent._l7t5.setText(BA.ObjectToCharSequence("R"));
 //BA.debugLineNum = 475;BA.debugLine="l7t6.Text = \"S\"";
mostCurrent._l7t6.setText(BA.ObjectToCharSequence("S"));
 //BA.debugLineNum = 476;BA.debugLine="menu.Enabled = False";
mostCurrent._menu.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 22: {
 //BA.debugLineNum = 480;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 481;BA.debugLine="lbt22.Visible = False";
mostCurrent._lbt22.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 482;BA.debugLine="lbt23.Visible = True";
mostCurrent._lbt23.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 483;BA.debugLine="lbt24.Visible = False";
mostCurrent._lbt24.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 484;BA.debugLine="lbt23.BringToFront";
mostCurrent._lbt23.BringToFront();
 //BA.debugLineNum = 485;BA.debugLine="nex.Visible = True";
mostCurrent._nex.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 486;BA.debugLine="menu.Visible = True";
mostCurrent._menu.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 487;BA.debugLine="menu.Enabled = True";
mostCurrent._menu.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 488;BA.debugLine="surr.Enabled = False";
mostCurrent._surr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 23: {
 //BA.debugLineNum = 491;BA.debugLine="pntut.BringToFront";
mostCurrent._pntut.BringToFront();
 //BA.debugLineNum = 492;BA.debugLine="lbt23.Visible = False";
mostCurrent._lbt23.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 493;BA.debugLine="lbt24.Visible = True";
mostCurrent._lbt24.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 494;BA.debugLine="lbt24.BringToFront";
mostCurrent._lbt24.BringToFront();
 //BA.debugLineNum = 495;BA.debugLine="menu.Enabled = False";
mostCurrent._menu.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 break; }
case 24: {
 //BA.debugLineNum = 497;BA.debugLine="File.WriteString(File.DirInternal, \"tutorial.tx";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"tutorial.txt","true");
 //BA.debugLineNum = 498;BA.debugLine="Main.bg2.Stop";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Stop();
 //BA.debugLineNum = 499;BA.debugLine="StartActivity(\"loading\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("loading"));
 //BA.debugLineNum = 500;BA.debugLine="Main.mods = \"Main\"";
mostCurrent._main._mods /*String*/  = "Main";
 //BA.debugLineNum = 501;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 break; }
}
;
 //BA.debugLineNum = 503;BA.debugLine="End Sub";
return "";
}
public static String  _size() throws Exception{
double _xx = 0;
double _yy = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper _view = null;
 //BA.debugLineNum = 146;BA.debugLine="Sub Size";
 //BA.debugLineNum = 147;BA.debugLine="Dim xx,yy As Double";
_xx = 0;
_yy = 0;
 //BA.debugLineNum = 148;BA.debugLine="xx=((100%x)/450)/Density";
_xx = ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA))/(double)450)/(double)anywheresoftware.b4a.keywords.Common.Density;
 //BA.debugLineNum = 149;BA.debugLine="yy=((100%y)/ 800)/Density";
_yy = ((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA))/(double)800)/(double)anywheresoftware.b4a.keywords.Common.Density;
 //BA.debugLineNum = 150;BA.debugLine="For Each View As View In Activity.GetAllViewsRecu";
_view = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group4 = mostCurrent._activity.GetAllViewsRecursive();
final int groupLen4 = group4.getSize()
;int index4 = 0;
;
for (; index4 < groupLen4;index4++){
_view = (anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(group4.Get(index4)));
 //BA.debugLineNum = 151;BA.debugLine="View.Width=(View.Width) * xx";
_view.setWidth((int) ((_view.getWidth())*_xx));
 //BA.debugLineNum = 152;BA.debugLine="View.Left=(View.Left) * xx";
_view.setLeft((int) ((_view.getLeft())*_xx));
 //BA.debugLineNum = 153;BA.debugLine="View.Height=(View.Height)* yy";
_view.setHeight((int) ((_view.getHeight())*_yy));
 //BA.debugLineNum = 154;BA.debugLine="View.Top=(View.Top)*yy";
_view.setTop((int) ((_view.getTop())*_yy));
 }
};
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
return "";
}
public static String  _time_tick() throws Exception{
 //BA.debugLineNum = 100;BA.debugLine="Sub time_Tick";
 //BA.debugLineNum = 101;BA.debugLine="seconds = seconds + 1";
_seconds = (int) (_seconds+1);
 //BA.debugLineNum = 102;BA.debugLine="Log(\"Timer tick: \" & seconds) ' Check if this pri";
anywheresoftware.b4a.keywords.Common.LogImpl("67405570","Timer tick: "+BA.NumberToString(_seconds),0);
 //BA.debugLineNum = 103;BA.debugLine="updatelabel";
_updatelabel();
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _updatelabel() throws Exception{
int _mins = 0;
int _sec = 0;
 //BA.debugLineNum = 106;BA.debugLine="Sub updatelabel";
 //BA.debugLineNum = 107;BA.debugLine="Dim mins As Int = Floor(seconds/60)";
_mins = (int) (anywheresoftware.b4a.keywords.Common.Floor(_seconds/(double)60));
 //BA.debugLineNum = 108;BA.debugLine="Dim sec As Int = seconds Mod 60";
_sec = (int) (_seconds%60);
 //BA.debugLineNum = 109;BA.debugLine="l2.Text = NumberFormat(mins, 2, 0) & \":\" & Number";
mostCurrent._l2.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat(_mins,(int) (2),(int) (0))+":"+anywheresoftware.b4a.keywords.Common.NumberFormat(_sec,(int) (2),(int) (0))));
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
}
