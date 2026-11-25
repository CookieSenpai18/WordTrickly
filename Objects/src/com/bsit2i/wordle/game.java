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

public class game extends Activity implements B4AActivity{
	public static game mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.bsit2i.wordle", "com.bsit2i.wordle.game");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (game).");
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
		activityBA = new BA(this, layout, processBA, "com.bsit2i.wordle", "com.bsit2i.wordle.game");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.bsit2i.wordle.game", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (game) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (game) Resume **");
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
		return game.class;
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
            BA.LogInfo("** Activity (game) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (game) Pause event (activity is not paused). **");
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
            game mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (game) Resume **");
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
public anywheresoftware.b4a.objects.B4XViewWrapper _root = null;
public anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b1 = null;
public static int _pow2 = 0;
public static boolean _gameisrunning = false;
public anywheresoftware.b4a.objects.collections.List _usedwords = null;
public static int _levelcounter = 0;
public static String _targetword = "";
public static int _hintcount = 0;
public static int _maxhints = 0;
public static int _currentrow = 0;
public anywheresoftware.b4a.objects.collections.Map[] _pcall = null;
public static String _selectedword = "";
public static int _currentline = 0;
public static int _pu2_uses = 0;
public static int _pu1_uses = 0;
public static boolean _powerupused2 = false;
public static boolean _powerupused3 = false;
public static int[][] _panelcolors = null;
public anywheresoftware.b4a.objects.EditTextWrapper[][] _edittexts = null;
public anywheresoftware.b4a.objects.PanelWrapper[][] _panels = null;
public static int _elapsedtime = 0;
public static boolean _powerup1used = false;
public anywheresoftware.b4a.objects.collections.List _usedhints = null;
public anywheresoftware.b4a.objects.collections.Map _wordhints = null;
public static String _correctword = "";
public static boolean _isprogrammaticchange = false;
public static int _hintindex = 0;
public static String _currentword = "";
public static boolean _pu3used = false;
public anywheresoftware.b4a.objects.collections.List _pu2positions = null;
public static boolean _actionbarhomeclicked = false;
public anywheresoftware.b4a.objects.Timer _time = null;
public static int _seconds = 0;
public anywheresoftware.b4a.objects.EditTextWrapper _l1t1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l1t2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l1t3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l1t4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l1t5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l1t6 = null;
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
public anywheresoftware.b4a.objects.EditTextWrapper _l4t1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l4t2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l4t3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l4t4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l4t5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l4t6 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l5t1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l5t2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l5t3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l5t4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l5t5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l5t6 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l6t1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l6t2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l6t3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l6t4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l6t5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l6t6 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t2 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t4 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t5 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _l7t6 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn11 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn12 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn13 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn14 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn15 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn16 = null;
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
public anywheresoftware.b4a.objects.PanelWrapper _pn41 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn42 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn43 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn44 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn45 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn46 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn51 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn52 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn53 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn54 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn55 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn56 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn61 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn62 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn63 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn64 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn65 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn66 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn71 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn72 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn73 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn74 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn75 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pn76 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _submit = null;
public anywheresoftware.b4a.objects.ButtonWrapper _deletebtn = null;
public anywheresoftware.b4a.objects.ButtonWrapper _pu1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _pu2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _pu3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _cl = null;
public anywheresoftware.b4a.objects.LabelWrapper _h1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _h2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _h3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _h4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _l1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _l2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _l3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _l4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _l5 = null;
public anywheresoftware.b4a.objects.collections.List _hintlist = null;
public anywheresoftware.b4a.objects.collections.Map _pc = null;
public static int _currentattempt = 0;
public static int _hc = 0;
public static int _pu2uses = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _surr = null;
public anywheresoftware.b4a.objects.collections.List _wordlist = null;
public Object[] _etall = null;
public Object[] _panelsall = null;
public anywheresoftware.b4a.objects.collections.List _solvedwords = null;
public anywheresoftware.b4a.objects.ButtonWrapper _nexl = null;
public anywheresoftware.b4a.objects.LabelWrapper _ress = null;
public anywheresoftware.b4a.objects.PanelWrapper _gover = null;
public anywheresoftware.b4a.objects.PanelWrapper _srd = null;
public anywheresoftware.b4a.objects.PanelWrapper _gsum = null;
public anywheresoftware.b4a.objects.PanelWrapper _gov = null;
public static int _atmp = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _to_main = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cancel = null;
public anywheresoftware.b4a.objects.PanelWrapper _menu_panel = null;
public anywheresoftware.b4a.objects.PanelWrapper _cover = null;
public com.bsit2i.wordle.main _main = null;
public com.bsit2i.wordle.history _history = null;
public com.bsit2i.wordle.settings _settings = null;
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
 //BA.debugLineNum = 337;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 338;BA.debugLine="Activity.LoadLayout(\"mg_main\")";
mostCurrent._activity.LoadLayout("mg_main",mostCurrent.activityBA);
 //BA.debugLineNum = 339;BA.debugLine="Main.bg2.Stop";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Stop();
 //BA.debugLineNum = 340;BA.debugLine="Main.bg2.IsInitialized";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .IsInitialized();
 //BA.debugLineNum = 341;BA.debugLine="Main.bg2.initialize";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Initialize();
 //BA.debugLineNum = 342;BA.debugLine="Main.bg2.Load(File.DirAssets, \"nbgm2.mp3\")";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"nbgm2.mp3");
 //BA.debugLineNum = 343;BA.debugLine="Main.bg2.SetVolume(Main.vl1, Main.vl1)";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .SetVolume(mostCurrent._main._vl1 /*float*/ ,mostCurrent._main._vl1 /*float*/ );
 //BA.debugLineNum = 344;BA.debugLine="Main.bg2.Looping = True";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .setLooping(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 345;BA.debugLine="cover.Color =  Colors.ARGB(0, 0, 0, 0)";
mostCurrent._cover.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (0),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 346;BA.debugLine="cover.Visible = False";
mostCurrent._cover.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 348;BA.debugLine="time.Initialize(\"time\", 1000)";
mostCurrent._time.Initialize(processBA,"time",(long) (1000));
 //BA.debugLineNum = 349;BA.debugLine="time.Enabled = True ' Start the timer";
mostCurrent._time.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 350;BA.debugLine="seconds = 0";
_seconds = (int) (0);
 //BA.debugLineNum = 351;BA.debugLine="updatelabel";
_updatelabel();
 //BA.debugLineNum = 353;BA.debugLine="pu2_uses = 0";
_pu2_uses = (int) (0);
 //BA.debugLineNum = 354;BA.debugLine="pu1_uses = 0";
_pu1_uses = (int) (0);
 //BA.debugLineNum = 355;BA.debugLine="pow2 = 0";
_pow2 = (int) (0);
 //BA.debugLineNum = 356;BA.debugLine="pu3Used = False";
_pu3used = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 358;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 359;BA.debugLine="InitializeGameArrays";
_initializegamearrays();
 };
 //BA.debugLineNum = 363;BA.debugLine="wordList.Initialize";
mostCurrent._wordlist.Initialize();
 //BA.debugLineNum = 364;BA.debugLine="wordList.AddAll(Array As String( _ \"ACCESS\", \"ANA";
mostCurrent._wordlist.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"ACCESS","ANALOG","AVATAR","BACKUP","BANNER","BINARY","BITMAP","BITNET","BOOTUP","BRANCH","BRIDGE","BROWSE","CLIENT","CURSOR","DECODE","DEFRAG","DELETE","DIALUP","DOMAIN","DONGLE","DRIVER","DUPLEX","EDITOR","ESCAPE","EXPORT","FILTER","FLOPPY","FOLDER","FORMAT","GOOGLE","GOPHER","HACKER","HEADER","IMPORT","INKJET","ITUNES","JOVIAL","KEYPAD","LAPTOP","LOGOFF","LOGOUT","MEMORY","NEWBIE","NIBBLE","OFFICE","ONLINE","OUTPUT","PACKET","PARITY","PARSER","PASCAL","PLUGIN","PORTAL","PROLOG","PROMPT","RASTER","RECORD","REMOTE","ROUTER","SCROLL","SEARCH","SECTOR","SERVER","SMILEY","SNOBOL","SPIDER","SPRITE","STATUS","SYSTEM","TABLET","TELNET","TROJAN","UPDATE","UPLOAD","UPTIME","USENET","VECTOR","WEBLOG","WINDOW","WIZARD"}));
 //BA.debugLineNum = 373;BA.debugLine="currentWord = wordList.Get(Rnd(0, wordList.Size))";
mostCurrent._currentword = BA.ObjectToString(mostCurrent._wordlist.Get(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),mostCurrent._wordlist.getSize())));
 //BA.debugLineNum = 374;BA.debugLine="currentWord = currentWord.ToUpperCase";
mostCurrent._currentword = mostCurrent._currentword.toUpperCase();
 //BA.debugLineNum = 376;BA.debugLine="Main.bg1.Stop";
mostCurrent._main._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Stop();
 //BA.debugLineNum = 378;BA.debugLine="Main.bg2.play";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 380;BA.debugLine="Size";
_size();
 //BA.debugLineNum = 382;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 1102;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 1103;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 1104;BA.debugLine="menu_Click";
_menu_click();
 };
 //BA.debugLineNum = 1106;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 117;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 118;BA.debugLine="Main.bg2.pause";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Pause();
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 111;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 112;BA.debugLine="Main.bg2.play";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 113;BA.debugLine="If gameIsRunning = False Then";
if (_gameisrunning==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 114;BA.debugLine="ResetGame";
_resetgame();
 };
 //BA.debugLineNum = 116;BA.debugLine="End Sub";
return "";
}
public static String  _animategovtomiddle() throws Exception{
int _middley = 0;
 //BA.debugLineNum = 1217;BA.debugLine="Sub AnimateGovToMiddle";
 //BA.debugLineNum = 1219;BA.debugLine="gov.Top = -gov.Height";
mostCurrent._gov.setTop((int) (-mostCurrent._gov.getHeight()));
 //BA.debugLineNum = 1222;BA.debugLine="gov.Visible = True";
mostCurrent._gov.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1225;BA.debugLine="Dim middleY As Int = (80%y - gov.Height) / 2";
_middley = (int) ((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (80),mostCurrent.activityBA)-mostCurrent._gov.getHeight())/(double)2);
 //BA.debugLineNum = 1226;BA.debugLine="gov.SetLayoutAnimated(1400, gov.Left, middleY, go";
mostCurrent._gov.SetLayoutAnimated((int) (1400),mostCurrent._gov.getLeft(),_middley,mostCurrent._gov.getWidth(),mostCurrent._gov.getHeight());
 //BA.debugLineNum = 1227;BA.debugLine="End Sub";
return "";
}
public static String  _cancel_click() throws Exception{
 //BA.debugLineNum = 1230;BA.debugLine="Private Sub cancel_Click";
 //BA.debugLineNum = 1231;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 1232;BA.debugLine="menu_Click";
_menu_click();
 //BA.debugLineNum = 1233;BA.debugLine="End Sub";
return "";
}
public static void  _gameover(boolean _success) throws Exception{
ResumableSub_GameOver rsub = new ResumableSub_GameOver(null,_success);
rsub.resume(processBA, null);
}
public static class ResumableSub_GameOver extends BA.ResumableSub {
public ResumableSub_GameOver(com.bsit2i.wordle.game parent,boolean _success) {
this.parent = parent;
this._success = _success;
}
com.bsit2i.wordle.game parent;
boolean _success;
int _mins = 0;
int _sec = 0;
int _i = 0;
anywheresoftware.b4a.objects.EditTextWrapper[] _etrow = null;
anywheresoftware.b4a.objects.EditTextWrapper _et = null;
int _score = 0;
int _atmps = 0;
int _timepenalty = 0;
String _result = "";
String _pu3text = "";
String _entry = "";
int step17;
int limit17;
Object[] group20;
int index20;
int groupLen20;
anywheresoftware.b4a.objects.EditTextWrapper[] group21;
int index21;
int groupLen21;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 495;BA.debugLine="time.Enabled = False";
parent.mostCurrent._time.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 496;BA.debugLine="Dim mins As Int = Floor(seconds/60)";
_mins = (int) (anywheresoftware.b4a.keywords.Common.Floor(parent._seconds/(double)60));
 //BA.debugLineNum = 497;BA.debugLine="Dim sec As Int = seconds Mod 60";
_sec = (int) (parent._seconds%60);
 //BA.debugLineNum = 498;BA.debugLine="Main.tim = NumberFormat(mins, 2, 0) & \":\" & Numbe";
parent.mostCurrent._main._tim /*String*/  = anywheresoftware.b4a.keywords.Common.NumberFormat(_mins,(int) (2),(int) (0))+":"+anywheresoftware.b4a.keywords.Common.NumberFormat(_sec,(int) (2),(int) (0));
 //BA.debugLineNum = 500;BA.debugLine="AnimateGovToMiddle	' Game Over Panel";
_animategovtomiddle();
 //BA.debugLineNum = 502;BA.debugLine="Sleep(3000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (3000));
this.state = 54;
return;
case 54:
//C
this.state = 1;
;
 //BA.debugLineNum = 503;BA.debugLine="gov.Visible = False";
parent.mostCurrent._gov.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 504;BA.debugLine="nexl.Enabled = True";
parent.mostCurrent._nexl.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 505;BA.debugLine="nexl.Visible = True";
parent.mostCurrent._nexl.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 506;BA.debugLine="submit.visible = False";
parent.mostCurrent._submit.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 507;BA.debugLine="submit.Enabled = False";
parent.mostCurrent._submit.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 508;BA.debugLine="pu1.Enabled = False";
parent.mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 509;BA.debugLine="pu2.Enabled = False";
parent.mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 510;BA.debugLine="pu3.Enabled = False";
parent.mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 511;BA.debugLine="surr.Enabled = False";
parent.mostCurrent._surr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 513;BA.debugLine="Dim CorrectWord As String = currentWord.ToUpperCa";
parent.mostCurrent._correctword = parent.mostCurrent._currentword.toUpperCase();
 //BA.debugLineNum = 516;BA.debugLine="For i = 0 To 5";
if (true) break;

case 1:
//for
this.state = 4;
step17 = 1;
limit17 = (int) (5);
_i = (int) (0) ;
this.state = 55;
if (true) break;

case 55:
//C
this.state = 4;
if ((step17 > 0 && _i <= limit17) || (step17 < 0 && _i >= limit17)) this.state = 3;
if (true) break;

case 56:
//C
this.state = 55;
_i = ((int)(0 + _i + step17)) ;
if (true) break;

case 3:
//C
this.state = 56;
 //BA.debugLineNum = 517;BA.debugLine="EditTexts(6, i).Text = currentWord.SubString2(i,";
parent.mostCurrent._edittexts[(int) (6)][_i].setText(BA.ObjectToCharSequence(parent.mostCurrent._currentword.substring(_i,(int) (_i+1))));
 if (true) break;
if (true) break;
;
 //BA.debugLineNum = 520;BA.debugLine="For Each etRow() As EditText In etAll";

case 4:
//for
this.state = 11;
group20 = parent.mostCurrent._etall;
index20 = 0;
groupLen20 = group20.length;
this.state = 57;
if (true) break;

case 57:
//C
this.state = 11;
if (index20 < groupLen20) {
this.state = 6;
_etrow = (anywheresoftware.b4a.objects.EditTextWrapper[])(group20[index20]);}
if (true) break;

case 58:
//C
this.state = 57;
index20++;
if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 521;BA.debugLine="For Each et As EditText In etRow";
if (true) break;

case 7:
//for
this.state = 10;
group21 = _etrow;
index21 = 0;
groupLen21 = group21.length;
this.state = 59;
if (true) break;

case 59:
//C
this.state = 10;
if (index21 < groupLen21) {
this.state = 9;
_et = group21[index21];}
if (true) break;

case 60:
//C
this.state = 59;
index21++;
if (true) break;

case 9:
//C
this.state = 60;
 //BA.debugLineNum = 522;BA.debugLine="et.Enabled = False";
_et.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 if (true) break;
if (true) break;

case 10:
//C
this.state = 58;
;
 if (true) break;
if (true) break;

case 11:
//C
this.state = 12;
;
 //BA.debugLineNum = 527;BA.debugLine="time.Enabled = False";
parent.mostCurrent._time.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 530;BA.debugLine="Dim score As Int = 100";
_score = (int) (100);
 //BA.debugLineNum = 531;BA.debugLine="Dim atmps As Int = 0";
_atmps = (int) (0);
 //BA.debugLineNum = 533;BA.debugLine="Select atmp";
if (true) break;

case 12:
//select
this.state = 25;
switch (parent._atmp) {
case 1: {
this.state = 14;
if (true) break;
}
case 2: {
this.state = 16;
if (true) break;
}
case 3: {
this.state = 18;
if (true) break;
}
case 4: {
this.state = 20;
if (true) break;
}
case 5: {
this.state = 22;
if (true) break;
}
case 6: {
this.state = 24;
if (true) break;
}
}
if (true) break;

case 14:
//C
this.state = 25;
 //BA.debugLineNum = 535;BA.debugLine="atmps = atmps + 15";
_atmps = (int) (_atmps+15);
 if (true) break;

case 16:
//C
this.state = 25;
 //BA.debugLineNum = 537;BA.debugLine="atmps = atmps + 10";
_atmps = (int) (_atmps+10);
 if (true) break;

case 18:
//C
this.state = 25;
 //BA.debugLineNum = 539;BA.debugLine="atmps = atmps + 7";
_atmps = (int) (_atmps+7);
 if (true) break;

case 20:
//C
this.state = 25;
 //BA.debugLineNum = 541;BA.debugLine="atmps = atmps + 5";
_atmps = (int) (_atmps+5);
 if (true) break;

case 22:
//C
this.state = 25;
 //BA.debugLineNum = 543;BA.debugLine="atmps = atmps + 3";
_atmps = (int) (_atmps+3);
 if (true) break;

case 24:
//C
this.state = 25;
 //BA.debugLineNum = 545;BA.debugLine="atmps = atmps + 1";
_atmps = (int) (_atmps+1);
 if (true) break;

case 25:
//C
this.state = 26;
;
 //BA.debugLineNum = 549;BA.debugLine="Dim timePenalty As Int = (elapsedTime / 20) * 5";
_timepenalty = (int) ((parent._elapsedtime/(double)20)*5);
 //BA.debugLineNum = 550;BA.debugLine="score = score - timePenalty";
_score = (int) (_score-_timepenalty);
 //BA.debugLineNum = 553;BA.debugLine="score = score - (pu1_uses * 2)";
_score = (int) (_score-(parent._pu1_uses*2));
 //BA.debugLineNum = 554;BA.debugLine="score = score - (pow2 * 5)";
_score = (int) (_score-(parent._pow2*5));
 //BA.debugLineNum = 555;BA.debugLine="score = score + atmps";
_score = (int) (_score+_atmps);
 //BA.debugLineNum = 556;BA.debugLine="If pu3Used Then";
if (true) break;

case 26:
//if
this.state = 29;
if (parent._pu3used) { 
this.state = 28;
}if (true) break;

case 28:
//C
this.state = 29;
 //BA.debugLineNum = 557;BA.debugLine="score = score - 15";
_score = (int) (_score-15);
 if (true) break;
;
 //BA.debugLineNum = 559;BA.debugLine="If score > 100 Then";

case 29:
//if
this.state = 32;
if (_score>100) { 
this.state = 31;
}if (true) break;

case 31:
//C
this.state = 32;
 //BA.debugLineNum = 560;BA.debugLine="score = 100";
_score = (int) (100);
 if (true) break;
;
 //BA.debugLineNum = 563;BA.debugLine="If score < 0 Then score = 0";

case 32:
//if
this.state = 37;
if (_score<0) { 
this.state = 34;
;}if (true) break;

case 34:
//C
this.state = 37;
_score = (int) (0);
if (true) break;

case 37:
//C
this.state = 38;
;
 //BA.debugLineNum = 566;BA.debugLine="Dim result As String";
_result = "";
 //BA.debugLineNum = 567;BA.debugLine="If success = True Then";
if (true) break;

case 38:
//if
this.state = 43;
if (_success==anywheresoftware.b4a.keywords.Common.True) { 
this.state = 40;
}else {
this.state = 42;
}if (true) break;

case 40:
//C
this.state = 43;
 //BA.debugLineNum = 568;BA.debugLine="Main.cw = Main.cw + 1";
parent.mostCurrent._main._cw /*int*/  = (int) (parent.mostCurrent._main._cw /*int*/ +1);
 //BA.debugLineNum = 569;BA.debugLine="gsum.BringToFront";
parent.mostCurrent._gsum.BringToFront();
 //BA.debugLineNum = 570;BA.debugLine="gsum.Visible = True";
parent.mostCurrent._gsum.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 571;BA.debugLine="result = \"Congratulations! You guessed the word!";
_result = "Congratulations! You guessed the word!"+anywheresoftware.b4a.keywords.Common.CRLF;
 if (true) break;

case 42:
//C
this.state = 43;
 //BA.debugLineNum = 573;BA.debugLine="srd.BringToFront";
parent.mostCurrent._srd.BringToFront();
 //BA.debugLineNum = 574;BA.debugLine="srd.Visible = True";
parent.mostCurrent._srd.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 575;BA.debugLine="score = 0";
_score = (int) (0);
 //BA.debugLineNum = 576;BA.debugLine="result = \"Game Over. Better luck next time!\" & C";
_result = "Game Over. Better luck next time!"+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF;
 if (true) break;

case 43:
//C
this.state = 44;
;
 //BA.debugLineNum = 581;BA.debugLine="result = result & \"Number of Attempts: \" & atmp &";
_result = _result+"Number of Attempts: "+BA.NumberToString(parent._atmp)+" Attempt(s)"+"   +"+BA.NumberToString(_atmps)+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 582;BA.debugLine="result = result & \"Time taken: \" & elapsedTime &";
_result = _result+"Time taken: "+BA.NumberToString(parent._elapsedtime)+" seconds"+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 583;BA.debugLine="result = result & \"Hints Used: \" & pu1_uses & \" t";
_result = _result+"Hints Used: "+BA.NumberToString(parent._pu1_uses)+" times   -"+BA.NumberToString((parent._pu1_uses*2))+""+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 584;BA.debugLine="result = result & \"Power Up 2 used: \" & pu2_uses";
_result = _result+"Power Up 2 used: "+BA.NumberToString(parent._pu2_uses)+" times   -"+BA.NumberToString((parent._pu2_uses*5))+""+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 586;BA.debugLine="Dim pu3Text As String";
_pu3text = "";
 //BA.debugLineNum = 587;BA.debugLine="If pu3Used Then";
if (true) break;

case 44:
//if
this.state = 49;
if (parent._pu3used) { 
this.state = 46;
}else {
this.state = 48;
}if (true) break;

case 46:
//C
this.state = 49;
 //BA.debugLineNum = 588;BA.debugLine="pu3Text = \"Yes (-15)\"";
_pu3text = "Yes (-15)";
 if (true) break;

case 48:
//C
this.state = 49;
 //BA.debugLineNum = 590;BA.debugLine="pu3Text = \"No\"";
_pu3text = "No";
 if (true) break;

case 49:
//C
this.state = 50;
;
 //BA.debugLineNum = 592;BA.debugLine="result = result & \"Power Up 3 used:     \" & pu3Te";
_result = _result+"Power Up 3 used:     "+_pu3text+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 594;BA.debugLine="result = result & \"Final Score:   \" & score";
_result = _result+"Final Score:   "+BA.NumberToString(_score);
 //BA.debugLineNum = 595;BA.debugLine="Main.sc = score";
parent.mostCurrent._main._sc /*int*/  = _score;
 //BA.debugLineNum = 596;BA.debugLine="Main.atm = atmp";
parent.mostCurrent._main._atm /*int*/  = parent._atmp;
 //BA.debugLineNum = 597;BA.debugLine="gover.BringToFront";
parent.mostCurrent._gover.BringToFront();
 //BA.debugLineNum = 598;BA.debugLine="gover.SetVisibleAnimated(1800, True)";
parent.mostCurrent._gover.SetVisibleAnimated((int) (1800),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 599;BA.debugLine="gover.Visible = True";
parent.mostCurrent._gover.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 600;BA.debugLine="ress.Text = result";
parent.mostCurrent._ress.setText(BA.ObjectToCharSequence(_result));
 //BA.debugLineNum = 605;BA.debugLine="If success Then";
if (true) break;

case 50:
//if
this.state = 53;
if (_success) { 
this.state = 52;
}if (true) break;

case 52:
//C
this.state = 53;
 //BA.debugLineNum = 606;BA.debugLine="Dim entry As String = Main.usr & \",\" & Main.atm";
_entry = parent.mostCurrent._main._usr /*String*/ +","+BA.NumberToString(parent.mostCurrent._main._atm /*int*/ )+","+parent.mostCurrent._main._tim /*String*/ +","+BA.NumberToString(parent.mostCurrent._main._sc /*int*/ );
 //BA.debugLineNum = 607;BA.debugLine="Main.datlist.Add(entry)";
parent.mostCurrent._main._datlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_entry));
 //BA.debugLineNum = 608;BA.debugLine="File.WriteList(File.DirInternal, \"history.txt\",";
anywheresoftware.b4a.keywords.Common.File.WriteList(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"history.txt",parent.mostCurrent._main._datlist /*anywheresoftware.b4a.objects.collections.List*/ );
 //BA.debugLineNum = 609;BA.debugLine="File.WriteString(File.DirInternal, \"cw.txt\", Mai";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"cw.txt",BA.NumberToString(parent.mostCurrent._main._cw /*int*/ ));
 if (true) break;

case 53:
//C
this.state = -1;
;
 //BA.debugLineNum = 612;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static anywheresoftware.b4a.objects.EditTextWrapper[]  _getcurrentlineedittexts(int _linenumber) throws Exception{
anywheresoftware.b4a.objects.EditTextWrapper[] _ets = null;
 //BA.debugLineNum = 981;BA.debugLine="Sub GetCurrentLineEditTexts(lineNumber As Int) As";
 //BA.debugLineNum = 982;BA.debugLine="Dim ets(6) As EditText ' for 6 letters, index 0 t";
_ets = new anywheresoftware.b4a.objects.EditTextWrapper[(int) (6)];
{
int d0 = _ets.length;
for (int i0 = 0;i0 < d0;i0++) {
_ets[i0] = new anywheresoftware.b4a.objects.EditTextWrapper();
}
}
;
 //BA.debugLineNum = 983;BA.debugLine="Select lineNumber";
switch (_linenumber) {
case 1: {
 //BA.debugLineNum = 985;BA.debugLine="ets(0) = l1t1";
_ets[(int) (0)] = mostCurrent._l1t1;
 //BA.debugLineNum = 986;BA.debugLine="ets(1) = l1t2";
_ets[(int) (1)] = mostCurrent._l1t2;
 //BA.debugLineNum = 987;BA.debugLine="ets(2) = l1t3";
_ets[(int) (2)] = mostCurrent._l1t3;
 //BA.debugLineNum = 988;BA.debugLine="ets(3) = l1t4";
_ets[(int) (3)] = mostCurrent._l1t4;
 //BA.debugLineNum = 989;BA.debugLine="ets(4) = l1t5";
_ets[(int) (4)] = mostCurrent._l1t5;
 //BA.debugLineNum = 990;BA.debugLine="ets(5) = l1t6";
_ets[(int) (5)] = mostCurrent._l1t6;
 break; }
case 2: {
 //BA.debugLineNum = 992;BA.debugLine="ets(0) = l2t1";
_ets[(int) (0)] = mostCurrent._l2t1;
 //BA.debugLineNum = 993;BA.debugLine="ets(1) = l2t2";
_ets[(int) (1)] = mostCurrent._l2t2;
 //BA.debugLineNum = 994;BA.debugLine="ets(2) = l2t3";
_ets[(int) (2)] = mostCurrent._l2t3;
 //BA.debugLineNum = 995;BA.debugLine="ets(3) = l2t4";
_ets[(int) (3)] = mostCurrent._l2t4;
 //BA.debugLineNum = 996;BA.debugLine="ets(4) = l2t5";
_ets[(int) (4)] = mostCurrent._l2t5;
 //BA.debugLineNum = 997;BA.debugLine="ets(5) = l2t6";
_ets[(int) (5)] = mostCurrent._l2t6;
 break; }
}
;
 //BA.debugLineNum = 1000;BA.debugLine="Return ets";
if (true) return _ets;
 //BA.debugLineNum = 1001;BA.debugLine="End Sub";
return null;
}
public static anywheresoftware.b4a.objects.EditTextWrapper[]  _getedittextarray(Object _obj) throws Exception{
 //BA.debugLineNum = 978;BA.debugLine="Sub GetEditTextArray(obj As Object) As EditText()";
 //BA.debugLineNum = 979;BA.debugLine="Return obj";
if (true) return (anywheresoftware.b4a.objects.EditTextWrapper[])(_obj);
 //BA.debugLineNum = 980;BA.debugLine="End Sub";
return null;
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 12;BA.debugLine="Private Root As B4XView";
mostCurrent._root = new anywheresoftware.b4a.objects.B4XViewWrapper();
 //BA.debugLineNum = 13;BA.debugLine="Private xui As XUI";
mostCurrent._xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 14;BA.debugLine="Private b1 As Button";
mostCurrent._b1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim pow2 As Int = 0";
_pow2 = (int) (0);
 //BA.debugLineNum = 17;BA.debugLine="Dim gameIsRunning As Boolean";
_gameisrunning = false;
 //BA.debugLineNum = 19;BA.debugLine="Dim usedWords As List";
mostCurrent._usedwords = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 20;BA.debugLine="Dim levelCounter As Int = 0";
_levelcounter = (int) (0);
 //BA.debugLineNum = 21;BA.debugLine="Dim TargetWord As String";
mostCurrent._targetword = "";
 //BA.debugLineNum = 22;BA.debugLine="Dim hintCount As Int = 0";
_hintcount = (int) (0);
 //BA.debugLineNum = 23;BA.debugLine="Dim maxHints As Int = 3";
_maxhints = (int) (3);
 //BA.debugLineNum = 24;BA.debugLine="Dim currentRow As Int = 1";
_currentrow = (int) (1);
 //BA.debugLineNum = 25;BA.debugLine="Dim pcAll(6) As Map";
mostCurrent._pcall = new anywheresoftware.b4a.objects.collections.Map[(int) (6)];
{
int d0 = mostCurrent._pcall.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._pcall[i0] = new anywheresoftware.b4a.objects.collections.Map();
}
}
;
 //BA.debugLineNum = 26;BA.debugLine="Dim selectedWord As String";
mostCurrent._selectedword = "";
 //BA.debugLineNum = 27;BA.debugLine="Dim currentLine As Int = 1 ' Start from line 1";
_currentline = (int) (1);
 //BA.debugLineNum = 28;BA.debugLine="Dim pu2_uses As Int = 0";
_pu2_uses = (int) (0);
 //BA.debugLineNum = 29;BA.debugLine="Dim pu1_uses As Int = 0";
_pu1_uses = (int) (0);
 //BA.debugLineNum = 30;BA.debugLine="Dim PowerUpUsed2 As Boolean";
_powerupused2 = false;
 //BA.debugLineNum = 31;BA.debugLine="Dim PowerUpUsed3 As Boolean = False";
_powerupused3 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 32;BA.debugLine="Dim panelColors(6, 6) As Int ' To keep track of e";
_panelcolors = new int[(int) (6)][];
{
int d0 = _panelcolors.length;
int d1 = (int) (6);
for (int i0 = 0;i0 < d0;i0++) {
_panelcolors[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 33;BA.debugLine="Dim EditTexts(7, 6) As EditText";
mostCurrent._edittexts = new anywheresoftware.b4a.objects.EditTextWrapper[(int) (7)][];
{
int d0 = mostCurrent._edittexts.length;
int d1 = (int) (6);
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._edittexts[i0] = new anywheresoftware.b4a.objects.EditTextWrapper[d1];
for (int i1 = 0;i1 < d1;i1++) {
mostCurrent._edittexts[i0][i1] = new anywheresoftware.b4a.objects.EditTextWrapper();
}
}
}
;
 //BA.debugLineNum = 34;BA.debugLine="Dim Panels(7, 6) As Panel";
mostCurrent._panels = new anywheresoftware.b4a.objects.PanelWrapper[(int) (7)][];
{
int d0 = mostCurrent._panels.length;
int d1 = (int) (6);
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._panels[i0] = new anywheresoftware.b4a.objects.PanelWrapper[d1];
for (int i1 = 0;i1 < d1;i1++) {
mostCurrent._panels[i0][i1] = new anywheresoftware.b4a.objects.PanelWrapper();
}
}
}
;
 //BA.debugLineNum = 35;BA.debugLine="Dim elapsedTime As Int = 0";
_elapsedtime = (int) (0);
 //BA.debugLineNum = 37;BA.debugLine="Dim PowerUp1Used As Boolean = False";
_powerup1used = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 38;BA.debugLine="Dim UsedHints As List";
mostCurrent._usedhints = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 39;BA.debugLine="Dim WordHints As Map";
mostCurrent._wordhints = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 40;BA.debugLine="Dim CorrectWord As String";
mostCurrent._correctword = "";
 //BA.debugLineNum = 42;BA.debugLine="Dim isProgrammaticChange As Boolean = False";
_isprogrammaticchange = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 43;BA.debugLine="Dim hintIndex As Int = 0";
_hintindex = (int) (0);
 //BA.debugLineNum = 44;BA.debugLine="Dim currentWord As String = \"\"";
mostCurrent._currentword = "";
 //BA.debugLineNum = 45;BA.debugLine="Dim pu3Used As Boolean = False";
_pu3used = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 46;BA.debugLine="Dim pu2Positions As List";
mostCurrent._pu2positions = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 48;BA.debugLine="Public ActionBarHomeClicked As Boolean";
_actionbarhomeclicked = false;
 //BA.debugLineNum = 49;BA.debugLine="Private time As Timer";
mostCurrent._time = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 50;BA.debugLine="Private seconds As Int";
_seconds = 0;
 //BA.debugLineNum = 53;BA.debugLine="Private l1t1, l1t2, l1t3, l1t4, l1t5, l1t6 As Edi";
mostCurrent._l1t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l1t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l1t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l1t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l1t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l1t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 54;BA.debugLine="Private l2t1, l2t2, l2t3, l2t4, l2t5, l2t6 As Edi";
mostCurrent._l2t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l2t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l2t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l2t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l2t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l2t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 55;BA.debugLine="Private l3t1, l3t2, l3t3, l3t4, l3t5, l3t6 As Edi";
mostCurrent._l3t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l3t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l3t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l3t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l3t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l3t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private l4t1, l4t2, l4t3, l4t4, l4t5, l4t6 As Edi";
mostCurrent._l4t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l4t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l4t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l4t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l4t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l4t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Private l5t1, l5t2, l5t3, l5t4, l5t5, l5t6 As Edi";
mostCurrent._l5t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l5t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l5t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l5t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l5t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l5t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Private l6t1, l6t2, l6t3, l6t4, l6t5, l6t6 As Edi";
mostCurrent._l6t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l6t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l6t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l6t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l6t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l6t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 59;BA.debugLine="Private l7t1, l7t2, l7t3, l7t4, l7t5, l7t6 As Edi";
mostCurrent._l7t1 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l7t2 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l7t3 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l7t4 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l7t5 = new anywheresoftware.b4a.objects.EditTextWrapper();
mostCurrent._l7t6 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Private pn11, pn12, pn13, pn14, pn15, pn16 As Pan";
mostCurrent._pn11 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn12 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn13 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn14 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn15 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn16 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Private pn21, pn22, pn23, pn24, pn25, pn26 As Pan";
mostCurrent._pn21 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn22 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn23 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn24 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn25 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn26 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Private pn31, pn32, pn33, pn34, pn35, pn36 As Pan";
mostCurrent._pn31 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn32 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn33 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn34 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn35 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn36 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Private pn41, pn42, pn43, pn44, pn45, pn46 As Pan";
mostCurrent._pn41 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn42 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn43 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn44 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn45 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn46 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 66;BA.debugLine="Private pn51, pn52, pn53, pn54, pn55, pn56 As Pan";
mostCurrent._pn51 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn52 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn53 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn54 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn55 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn56 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Private pn61, pn62, pn63, pn64, pn65, pn66 As Pan";
mostCurrent._pn61 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn62 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn63 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn64 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn65 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn66 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 68;BA.debugLine="Private pn71, pn72, pn73, pn74, pn75, pn76 As Pan";
mostCurrent._pn71 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn72 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn73 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn74 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn75 = new anywheresoftware.b4a.objects.PanelWrapper();
mostCurrent._pn76 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 70;BA.debugLine="Private submit As Button";
mostCurrent._submit = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 71;BA.debugLine="Private deleteBtn As Button";
mostCurrent._deletebtn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 72;BA.debugLine="Private pu1, pu2, pu3 As Button";
mostCurrent._pu1 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._pu2 = new anywheresoftware.b4a.objects.ButtonWrapper();
mostCurrent._pu3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Private cl, h1, h2, h3, h4, l1, l2, l3, l4, l5 As";
mostCurrent._cl = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._h1 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._h2 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._h3 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._h4 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._l1 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._l2 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._l3 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._l4 = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._l5 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Private hintList As List";
mostCurrent._hintlist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 76;BA.debugLine="Private pc As Map";
mostCurrent._pc = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 77;BA.debugLine="Private currentAttempt As Int = 0";
_currentattempt = (int) (0);
 //BA.debugLineNum = 78;BA.debugLine="Private hc As Int = 3";
_hc = (int) (3);
 //BA.debugLineNum = 79;BA.debugLine="Private pu2Uses As Int = 2";
_pu2uses = (int) (2);
 //BA.debugLineNum = 80;BA.debugLine="Private surr As Button";
mostCurrent._surr = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 82;BA.debugLine="Dim wordList As List";
mostCurrent._wordlist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 83;BA.debugLine="Dim etAll() As Object";
mostCurrent._etall = new Object[(int) (0)];
{
int d0 = mostCurrent._etall.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._etall[i0] = new Object();
}
}
;
 //BA.debugLineNum = 84;BA.debugLine="Dim panelsAll() As Object";
mostCurrent._panelsall = new Object[(int) (0)];
{
int d0 = mostCurrent._panelsall.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._panelsall[i0] = new Object();
}
}
;
 //BA.debugLineNum = 85;BA.debugLine="Dim solvedWords As List";
mostCurrent._solvedwords = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 86;BA.debugLine="Private nexl As Button";
mostCurrent._nexl = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 87;BA.debugLine="Private ress As Label";
mostCurrent._ress = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Private gover As Panel";
mostCurrent._gover = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 89;BA.debugLine="Private srd As Panel";
mostCurrent._srd = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 90;BA.debugLine="Private gsum As Panel";
mostCurrent._gsum = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 91;BA.debugLine="Private gov As Panel";
mostCurrent._gov = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 92;BA.debugLine="Dim atmp As Int = 1";
_atmp = (int) (1);
 //BA.debugLineNum = 93;BA.debugLine="Private to_main As Button";
mostCurrent._to_main = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 94;BA.debugLine="Private cancel As Button";
mostCurrent._cancel = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 95;BA.debugLine="Private menu_panel As Panel";
mostCurrent._menu_panel = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 96;BA.debugLine="Private cover As Panel";
mostCurrent._cover = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 97;BA.debugLine="End Sub";
return "";
}
public static String  _handletextchanged(anywheresoftware.b4a.objects.EditTextWrapper _edtsender,String _old,String _new) throws Exception{
anywheresoftware.b4a.objects.EditTextWrapper _nextet = null;
anywheresoftware.b4a.objects.EditTextWrapper _prevet = null;
 //BA.debugLineNum = 721;BA.debugLine="Sub HandleTextChanged(edtSender As EditText, Old A";
 //BA.debugLineNum = 722;BA.debugLine="If isProgrammaticChange Then Return";
if (_isprogrammaticchange) { 
if (true) return "";};
 //BA.debugLineNum = 724;BA.debugLine="Dim nextEt As EditText = Null";
_nextet = new anywheresoftware.b4a.objects.EditTextWrapper();
_nextet = (anywheresoftware.b4a.objects.EditTextWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.EditTextWrapper(), (android.widget.EditText)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 725;BA.debugLine="Dim prevEt As EditText = Null";
_prevet = new anywheresoftware.b4a.objects.EditTextWrapper();
_prevet = (anywheresoftware.b4a.objects.EditTextWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.EditTextWrapper(), (android.widget.EditText)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 728;BA.debugLine="isProgrammaticChange = True";
_isprogrammaticchange = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 729;BA.debugLine="If New.Length > 1 Then";
if (_new.length()>1) { 
 //BA.debugLineNum = 730;BA.debugLine="edtSender.Text = New.SubString2(0, 1).ToUpperCas";
_edtsender.setText(BA.ObjectToCharSequence(_new.substring((int) (0),(int) (1)).toUpperCase()));
 }else {
 //BA.debugLineNum = 732;BA.debugLine="edtSender.Text = New.ToUpperCase";
_edtsender.setText(BA.ObjectToCharSequence(_new.toUpperCase()));
 };
 //BA.debugLineNum = 734;BA.debugLine="isProgrammaticChange = False";
_isprogrammaticchange = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 737;BA.debugLine="If edtSender.Text.Length > 0 Then";
if (_edtsender.getText().length()>0) { 
 //BA.debugLineNum = 738;BA.debugLine="edtSender.SelectionStart = edtSender.Text.Length";
_edtsender.setSelectionStart(_edtsender.getText().length());
 };
 //BA.debugLineNum = 742;BA.debugLine="Select edtSender";
switch (BA.switchObjectToInt(_edtsender,mostCurrent._l1t1,mostCurrent._l1t2,mostCurrent._l1t3,mostCurrent._l1t4,mostCurrent._l1t5,mostCurrent._l1t6,mostCurrent._l2t1,mostCurrent._l2t2,mostCurrent._l2t3,mostCurrent._l2t4,mostCurrent._l2t5,mostCurrent._l2t6,mostCurrent._l3t1,mostCurrent._l3t2,mostCurrent._l3t3,mostCurrent._l3t4,mostCurrent._l3t5,mostCurrent._l3t6,mostCurrent._l4t1,mostCurrent._l4t2,mostCurrent._l4t3,mostCurrent._l4t4,mostCurrent._l4t5,mostCurrent._l4t6,mostCurrent._l5t1,mostCurrent._l5t2,mostCurrent._l5t3,mostCurrent._l5t4,mostCurrent._l5t5,mostCurrent._l5t6,mostCurrent._l6t1,mostCurrent._l6t2,mostCurrent._l6t3,mostCurrent._l6t4,mostCurrent._l6t5,mostCurrent._l6t6)) {
case 0: {
 //BA.debugLineNum = 743;BA.debugLine="Case l1t1: nextEt = l1t2";
_nextet = mostCurrent._l1t2;
 break; }
case 1: {
 //BA.debugLineNum = 744;BA.debugLine="Case l1t2: nextEt = l1t3";
_nextet = mostCurrent._l1t3;
 break; }
case 2: {
 //BA.debugLineNum = 745;BA.debugLine="Case l1t3: nextEt = l1t4";
_nextet = mostCurrent._l1t4;
 break; }
case 3: {
 //BA.debugLineNum = 746;BA.debugLine="Case l1t4: nextEt = l1t5";
_nextet = mostCurrent._l1t5;
 break; }
case 4: {
 //BA.debugLineNum = 747;BA.debugLine="Case l1t5: nextEt = l1t6";
_nextet = mostCurrent._l1t6;
 break; }
case 5: {
 //BA.debugLineNum = 748;BA.debugLine="Case l1t6: nextEt = l1t6 ' Last in row";
_nextet = mostCurrent._l1t6;
 break; }
case 6: {
 //BA.debugLineNum = 750;BA.debugLine="Case l2t1: nextEt = l2t2";
_nextet = mostCurrent._l2t2;
 break; }
case 7: {
 //BA.debugLineNum = 751;BA.debugLine="Case l2t2: nextEt = l2t3";
_nextet = mostCurrent._l2t3;
 break; }
case 8: {
 //BA.debugLineNum = 752;BA.debugLine="Case l2t3: nextEt = l2t4";
_nextet = mostCurrent._l2t4;
 break; }
case 9: {
 //BA.debugLineNum = 753;BA.debugLine="Case l2t4: nextEt = l2t5";
_nextet = mostCurrent._l2t5;
 break; }
case 10: {
 //BA.debugLineNum = 754;BA.debugLine="Case l2t5: nextEt = l2t6";
_nextet = mostCurrent._l2t6;
 break; }
case 11: {
 //BA.debugLineNum = 755;BA.debugLine="Case l2t6: nextEt = l2t6";
_nextet = mostCurrent._l2t6;
 break; }
case 12: {
 //BA.debugLineNum = 757;BA.debugLine="Case l3t1: nextEt = l3t2";
_nextet = mostCurrent._l3t2;
 break; }
case 13: {
 //BA.debugLineNum = 758;BA.debugLine="Case l3t2: nextEt = l3t3";
_nextet = mostCurrent._l3t3;
 break; }
case 14: {
 //BA.debugLineNum = 759;BA.debugLine="Case l3t3: nextEt = l3t4";
_nextet = mostCurrent._l3t4;
 break; }
case 15: {
 //BA.debugLineNum = 760;BA.debugLine="Case l3t4: nextEt = l3t5";
_nextet = mostCurrent._l3t5;
 break; }
case 16: {
 //BA.debugLineNum = 761;BA.debugLine="Case l3t5: nextEt = l3t6";
_nextet = mostCurrent._l3t6;
 break; }
case 17: {
 //BA.debugLineNum = 762;BA.debugLine="Case l3t6: nextEt = l3t6";
_nextet = mostCurrent._l3t6;
 break; }
case 18: {
 //BA.debugLineNum = 764;BA.debugLine="Case l4t1: nextEt = l4t2";
_nextet = mostCurrent._l4t2;
 break; }
case 19: {
 //BA.debugLineNum = 765;BA.debugLine="Case l4t2: nextEt = l4t3";
_nextet = mostCurrent._l4t3;
 break; }
case 20: {
 //BA.debugLineNum = 766;BA.debugLine="Case l4t3: nextEt = l4t4";
_nextet = mostCurrent._l4t4;
 break; }
case 21: {
 //BA.debugLineNum = 767;BA.debugLine="Case l4t4: nextEt = l4t5";
_nextet = mostCurrent._l4t5;
 break; }
case 22: {
 //BA.debugLineNum = 768;BA.debugLine="Case l4t5: nextEt = l4t6";
_nextet = mostCurrent._l4t6;
 break; }
case 23: {
 //BA.debugLineNum = 769;BA.debugLine="Case l4t6: nextEt = l4t6";
_nextet = mostCurrent._l4t6;
 break; }
case 24: {
 //BA.debugLineNum = 771;BA.debugLine="Case l5t1: nextEt = l5t2";
_nextet = mostCurrent._l5t2;
 break; }
case 25: {
 //BA.debugLineNum = 772;BA.debugLine="Case l5t2: nextEt = l5t3";
_nextet = mostCurrent._l5t3;
 break; }
case 26: {
 //BA.debugLineNum = 773;BA.debugLine="Case l5t3: nextEt = l5t4";
_nextet = mostCurrent._l5t4;
 break; }
case 27: {
 //BA.debugLineNum = 774;BA.debugLine="Case l5t4: nextEt = l5t5";
_nextet = mostCurrent._l5t5;
 break; }
case 28: {
 //BA.debugLineNum = 775;BA.debugLine="Case l5t5: nextEt = l5t6";
_nextet = mostCurrent._l5t6;
 break; }
case 29: {
 //BA.debugLineNum = 776;BA.debugLine="Case l5t6: nextEt = l5t6";
_nextet = mostCurrent._l5t6;
 break; }
case 30: {
 //BA.debugLineNum = 778;BA.debugLine="Case l6t1: nextEt = l6t2";
_nextet = mostCurrent._l6t2;
 break; }
case 31: {
 //BA.debugLineNum = 779;BA.debugLine="Case l6t2: nextEt = l6t3";
_nextet = mostCurrent._l6t3;
 break; }
case 32: {
 //BA.debugLineNum = 780;BA.debugLine="Case l6t3: nextEt = l6t4";
_nextet = mostCurrent._l6t4;
 break; }
case 33: {
 //BA.debugLineNum = 781;BA.debugLine="Case l6t4: nextEt = l6t5";
_nextet = mostCurrent._l6t5;
 break; }
case 34: {
 //BA.debugLineNum = 782;BA.debugLine="Case l6t5: nextEt = l6t6";
_nextet = mostCurrent._l6t6;
 break; }
case 35: {
 //BA.debugLineNum = 783;BA.debugLine="Case l6t6: nextEt = l6t6";
_nextet = mostCurrent._l6t6;
 break; }
}
;
 //BA.debugLineNum = 787;BA.debugLine="If New.Length > Old.Length And nextEt <> Null And";
if (_new.length()>_old.length() && _nextet!= null && (_nextet).equals(_edtsender) == false) { 
 //BA.debugLineNum = 788;BA.debugLine="nextEt.RequestFocus";
_nextet.RequestFocus();
 };
 //BA.debugLineNum = 792;BA.debugLine="If New.Length < Old.Length Then";
if (_new.length()<_old.length()) { 
 //BA.debugLineNum = 793;BA.debugLine="Select edtSender";
switch (BA.switchObjectToInt(_edtsender,mostCurrent._l1t1,mostCurrent._l1t2,mostCurrent._l1t3,mostCurrent._l1t4,mostCurrent._l1t5,mostCurrent._l1t6,mostCurrent._l2t1,mostCurrent._l2t2,mostCurrent._l2t3,mostCurrent._l2t4,mostCurrent._l2t5,mostCurrent._l2t6,mostCurrent._l3t1,mostCurrent._l3t2,mostCurrent._l3t3,mostCurrent._l3t4,mostCurrent._l3t5,mostCurrent._l3t6,mostCurrent._l4t1,mostCurrent._l4t2,mostCurrent._l4t3,mostCurrent._l4t4,mostCurrent._l4t5,mostCurrent._l4t6,mostCurrent._l5t1,mostCurrent._l5t2,mostCurrent._l5t3,mostCurrent._l5t4,mostCurrent._l5t5,mostCurrent._l5t6,mostCurrent._l6t1,mostCurrent._l6t2,mostCurrent._l6t3,mostCurrent._l6t4,mostCurrent._l6t5,mostCurrent._l6t6)) {
case 0: {
 //BA.debugLineNum = 794;BA.debugLine="Case l1t1: prevEt = l1t1";
_prevet = mostCurrent._l1t1;
 break; }
case 1: {
 //BA.debugLineNum = 795;BA.debugLine="Case l1t2: prevEt = l1t1";
_prevet = mostCurrent._l1t1;
 break; }
case 2: {
 //BA.debugLineNum = 796;BA.debugLine="Case l1t3: prevEt = l1t2";
_prevet = mostCurrent._l1t2;
 break; }
case 3: {
 //BA.debugLineNum = 797;BA.debugLine="Case l1t4: prevEt = l1t3";
_prevet = mostCurrent._l1t3;
 break; }
case 4: {
 //BA.debugLineNum = 798;BA.debugLine="Case l1t5: prevEt = l1t4";
_prevet = mostCurrent._l1t4;
 break; }
case 5: {
 //BA.debugLineNum = 799;BA.debugLine="Case l1t6: prevEt = l1t5";
_prevet = mostCurrent._l1t5;
 break; }
case 6: {
 //BA.debugLineNum = 801;BA.debugLine="Case l2t1: prevEt = l2t1";
_prevet = mostCurrent._l2t1;
 break; }
case 7: {
 //BA.debugLineNum = 802;BA.debugLine="Case l2t2: prevEt = l2t1";
_prevet = mostCurrent._l2t1;
 break; }
case 8: {
 //BA.debugLineNum = 803;BA.debugLine="Case l2t3: prevEt = l2t2";
_prevet = mostCurrent._l2t2;
 break; }
case 9: {
 //BA.debugLineNum = 804;BA.debugLine="Case l2t4: prevEt = l2t3";
_prevet = mostCurrent._l2t3;
 break; }
case 10: {
 //BA.debugLineNum = 805;BA.debugLine="Case l2t5: prevEt = l2t4";
_prevet = mostCurrent._l2t4;
 break; }
case 11: {
 //BA.debugLineNum = 806;BA.debugLine="Case l2t6: prevEt = l2t5";
_prevet = mostCurrent._l2t5;
 break; }
case 12: {
 //BA.debugLineNum = 808;BA.debugLine="Case l3t1: prevEt = l3t1";
_prevet = mostCurrent._l3t1;
 break; }
case 13: {
 //BA.debugLineNum = 809;BA.debugLine="Case l3t2: prevEt = l3t1";
_prevet = mostCurrent._l3t1;
 break; }
case 14: {
 //BA.debugLineNum = 810;BA.debugLine="Case l3t3: prevEt = l3t2";
_prevet = mostCurrent._l3t2;
 break; }
case 15: {
 //BA.debugLineNum = 811;BA.debugLine="Case l3t4: prevEt = l3t3";
_prevet = mostCurrent._l3t3;
 break; }
case 16: {
 //BA.debugLineNum = 812;BA.debugLine="Case l3t5: prevEt = l3t4";
_prevet = mostCurrent._l3t4;
 break; }
case 17: {
 //BA.debugLineNum = 813;BA.debugLine="Case l3t6: prevEt = l3t5";
_prevet = mostCurrent._l3t5;
 break; }
case 18: {
 //BA.debugLineNum = 815;BA.debugLine="Case l4t1: prevEt = l4t1";
_prevet = mostCurrent._l4t1;
 break; }
case 19: {
 //BA.debugLineNum = 816;BA.debugLine="Case l4t2: prevEt = l4t1";
_prevet = mostCurrent._l4t1;
 break; }
case 20: {
 //BA.debugLineNum = 817;BA.debugLine="Case l4t3: prevEt = l4t2";
_prevet = mostCurrent._l4t2;
 break; }
case 21: {
 //BA.debugLineNum = 818;BA.debugLine="Case l4t4: prevEt = l4t3";
_prevet = mostCurrent._l4t3;
 break; }
case 22: {
 //BA.debugLineNum = 819;BA.debugLine="Case l4t5: prevEt = l4t4";
_prevet = mostCurrent._l4t4;
 break; }
case 23: {
 //BA.debugLineNum = 820;BA.debugLine="Case l4t6: prevEt = l4t5";
_prevet = mostCurrent._l4t5;
 break; }
case 24: {
 //BA.debugLineNum = 822;BA.debugLine="Case l5t1: prevEt = l5t1";
_prevet = mostCurrent._l5t1;
 break; }
case 25: {
 //BA.debugLineNum = 823;BA.debugLine="Case l5t2: prevEt = l5t1";
_prevet = mostCurrent._l5t1;
 break; }
case 26: {
 //BA.debugLineNum = 824;BA.debugLine="Case l5t3: prevEt = l5t2";
_prevet = mostCurrent._l5t2;
 break; }
case 27: {
 //BA.debugLineNum = 825;BA.debugLine="Case l5t4: prevEt = l5t3";
_prevet = mostCurrent._l5t3;
 break; }
case 28: {
 //BA.debugLineNum = 826;BA.debugLine="Case l5t5: prevEt = l5t4";
_prevet = mostCurrent._l5t4;
 break; }
case 29: {
 //BA.debugLineNum = 827;BA.debugLine="Case l5t6: prevEt = l5t5";
_prevet = mostCurrent._l5t5;
 break; }
case 30: {
 //BA.debugLineNum = 829;BA.debugLine="Case l6t1: prevEt = l6t1";
_prevet = mostCurrent._l6t1;
 break; }
case 31: {
 //BA.debugLineNum = 830;BA.debugLine="Case l6t2: prevEt = l6t1";
_prevet = mostCurrent._l6t1;
 break; }
case 32: {
 //BA.debugLineNum = 831;BA.debugLine="Case l6t3: prevEt = l6t2";
_prevet = mostCurrent._l6t2;
 break; }
case 33: {
 //BA.debugLineNum = 832;BA.debugLine="Case l6t4: prevEt = l6t3";
_prevet = mostCurrent._l6t3;
 break; }
case 34: {
 //BA.debugLineNum = 833;BA.debugLine="Case l6t5: prevEt = l6t4";
_prevet = mostCurrent._l6t4;
 break; }
case 35: {
 //BA.debugLineNum = 834;BA.debugLine="Case l6t6: prevEt = l6t5";
_prevet = mostCurrent._l6t5;
 break; }
}
;
 //BA.debugLineNum = 837;BA.debugLine="If prevEt <> Null Then";
if (_prevet!= null) { 
 //BA.debugLineNum = 838;BA.debugLine="prevEt.RequestFocus";
_prevet.RequestFocus();
 //BA.debugLineNum = 839;BA.debugLine="prevEt.SelectionStart = prevEt.Text.Length";
_prevet.setSelectionStart(_prevet.getText().length());
 };
 };
 //BA.debugLineNum = 844;BA.debugLine="If edtSender = l1t6 Or edtSender = l2t6 Or edtSen";
if ((_edtsender).equals(mostCurrent._l1t6) || (_edtsender).equals(mostCurrent._l2t6) || (_edtsender).equals(mostCurrent._l3t6) || (_edtsender).equals(mostCurrent._l4t6) || (_edtsender).equals(mostCurrent._l5t6) || (_edtsender).equals(mostCurrent._l6t6)) { 
 //BA.debugLineNum = 845;BA.debugLine="edtSender.SelectionStart = edtSender.Text.Length";
_edtsender.setSelectionStart(_edtsender.getText().length());
 };
 //BA.debugLineNum = 847;BA.debugLine="End Sub";
return "";
}
public static String  _inithints() throws Exception{
 //BA.debugLineNum = 1107;BA.debugLine="Sub InitHints";
 //BA.debugLineNum = 1108;BA.debugLine="WordHints.Initialize";
mostCurrent._wordhints.Initialize();
 //BA.debugLineNum = 1109;BA.debugLine="WordHints.Put(\"ACCESS\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("ACCESS"),(Object)(new String[]{"Hint 1: Login permission","Hint 2: Gained via credentials","Hint 3: Often restricted","Hint 4: Network related"}));
 //BA.debugLineNum = 1110;BA.debugLine="WordHints.Put(\"ANALOG\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("ANALOG"),(Object)(new String[]{"Hint 1: Not digital","Hint 2: Continuous signals","Hint 3: Older tech","Hint 4: Used in radios"}));
 //BA.debugLineNum = 1111;BA.debugLine="WordHints.Put(\"AVATAR\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("AVATAR"),(Object)(new String[]{"Hint 1: Online persona","Hint 2: Used in games","Hint 3: Virtual character","Hint 4: Profile image"}));
 //BA.debugLineNum = 1112;BA.debugLine="WordHints.Put(\"BACKUP\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BACKUP"),(Object)(new String[]{"Hint 1: Data copy","Hint 2: Recovery source","Hint 3: Cloud or disk","Hint 4: Essential for safety"}));
 //BA.debugLineNum = 1113;BA.debugLine="WordHints.Put(\"BANNER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BANNER"),(Object)(new String[]{"Hint 1: Web ad","Hint 2: Found on websites","Hint 3: Horizontal layout","Hint 4: Marketing tool"}));
 //BA.debugLineNum = 1114;BA.debugLine="WordHints.Put(\"BINARY\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BINARY"),(Object)(new String[]{"Hint 1: 0s and 1s","Hint 2: Machine language","Hint 3: Two states","Hint 4: Base-2 system"}));
 //BA.debugLineNum = 1115;BA.debugLine="WordHints.Put(\"BITMAP\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BITMAP"),(Object)(new String[]{"Hint 1: Image format","Hint 2: Pixel-based","Hint 3: .BMP extension","Hint 4: Graphics file"}));
 //BA.debugLineNum = 1116;BA.debugLine="WordHints.Put(\"BITNET\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BITNET"),(Object)(new String[]{"Hint 1: Old network","Hint 2: Pre-Internet","Hint 3: Academic use","Hint 4: 1980s system"}));
 //BA.debugLineNum = 1117;BA.debugLine="WordHints.Put(\"BOOTUP\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BOOTUP"),(Object)(new String[]{"Hint 1: Start process","Hint 2: OS loading","Hint 3: Happens on power on","Hint 4: BIOS involved"}));
 //BA.debugLineNum = 1118;BA.debugLine="WordHints.Put(\"BRANCH\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BRANCH"),(Object)(new String[]{"Hint 1: Git term","Hint 2: Diverges code","Hint 3: Version control","Hint 4: Often merged"}));
 //BA.debugLineNum = 1119;BA.debugLine="WordHints.Put(\"BRIDGE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BRIDGE"),(Object)(new String[]{"Hint 1: Network device","Hint 2: Connects segments","Hint 3: Data traffic","Hint 4: Reduces collisions"}));
 //BA.debugLineNum = 1120;BA.debugLine="WordHints.Put(\"BROWSE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("BROWSE"),(Object)(new String[]{"Hint 1: Internet action","Hint 2: Using a browser","Hint 3: Web surfing","Hint 4: View content"}));
 //BA.debugLineNum = 1121;BA.debugLine="WordHints.Put(\"CLIENT\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("CLIENT"),(Object)(new String[]{"Hint 1: Connected to server","Hint 2: Requests services","Hint 3: Part of network","Hint 4: Software or hardware"}));
 //BA.debugLineNum = 1122;BA.debugLine="WordHints.Put(\"CURSOR\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("CURSOR"),(Object)(new String[]{"Hint 1: Blinking line","Hint 2: Shows position","Hint 3: Text input","Hint 4: Mouse pointer"}));
 //BA.debugLineNum = 1123;BA.debugLine="WordHints.Put(\"DECODE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("DECODE"),(Object)(new String[]{"Hint 1: Convert data","Hint 2: Reverse of encode","Hint 3: Read format","Hint 4: Decryption involved"}));
 //BA.debugLineNum = 1124;BA.debugLine="WordHints.Put(\"DEFRAG\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("DEFRAG"),(Object)(new String[]{"Hint 1: Disk utility","Hint 2: Rearranges files","Hint 3: Speeds access","Hint 4: Windows tool"}));
 //BA.debugLineNum = 1125;BA.debugLine="WordHints.Put(\"DELETE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("DELETE"),(Object)(new String[]{"Hint 1: Remove data","Hint 2: Erase file","Hint 3: Trash bin","Hint 4: Cannot undo sometimes"}));
 //BA.debugLineNum = 1126;BA.debugLine="WordHints.Put(\"DIALUP\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("DIALUP"),(Object)(new String[]{"Hint 1: Old internet","Hint 2: Phone line","Hint 3: Slow connection","Hint 4: Modem sound"}));
 //BA.debugLineNum = 1127;BA.debugLine="WordHints.Put(\"DOMAIN\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("DOMAIN"),(Object)(new String[]{"Hint 1: Website address","Hint 2: Internet identity","Hint 3: Ends in .com","Hint 4: Owned online"}));
 //BA.debugLineNum = 1128;BA.debugLine="WordHints.Put(\"DONGLE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("DONGLE"),(Object)(new String[]{"Hint 1: Small device","Hint 2: USB tool","Hint 3: Adds function","Hint 4: License key"}));
 //BA.debugLineNum = 1129;BA.debugLine="WordHints.Put(\"DRIVER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("DRIVER"),(Object)(new String[]{"Hint 1: Software","Hint 2: Hardware interface","Hint 3: Allows device operation","Hint 4: Often installed manually"}));
 //BA.debugLineNum = 1130;BA.debugLine="WordHints.Put(\"DUPLEX\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("DUPLEX"),(Object)(new String[]{"Hint 1: Communication type","Hint 2: Both directions","Hint 3: Two-way communication","Hint 4: Full-duplex network"}));
 //BA.debugLineNum = 1131;BA.debugLine="WordHints.Put(\"EDITOR\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("EDITOR"),(Object)(new String[]{"Hint 1: Text manipulation","Hint 2: Source code editor","Hint 3: Often used in programming","Hint 4: Notepad++ or Sublime Text"}));
 //BA.debugLineNum = 1132;BA.debugLine="WordHints.Put(\"ESCAPE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("ESCAPE"),(Object)(new String[]{"Hint 1: Key on keyboard","Hint 2: Exit command","Hint 3: Leave mode","Hint 4: Often used for closing"}));
 //BA.debugLineNum = 1133;BA.debugLine="WordHints.Put(\"EXPORT\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("EXPORT"),(Object)(new String[]{"Hint 1: Data transfer","Hint 2: Moving files","Hint 3: Output data","Hint 4: Often done to CSV"}));
 //BA.debugLineNum = 1134;BA.debugLine="WordHints.Put(\"FILTER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("FILTER"),(Object)(new String[]{"Hint 1: Block unwanted","Hint 2: Used in image editing","Hint 3: Network protection","Hint 4: Email spam control"}));
 //BA.debugLineNum = 1135;BA.debugLine="WordHints.Put(\"FLOPPY\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("FLOPPY"),(Object)(new String[]{"Hint 1: Storage device","Hint 2: Magnetic disk","Hint 3: 3.5 inches","Hint 4: Legacy technology"}));
 //BA.debugLineNum = 1136;BA.debugLine="WordHints.Put(\"FOLDER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("FOLDER"),(Object)(new String[]{"Hint 1: File organizer","Hint 2: Virtual directory","Hint 3: Can be opened","Hint 4: Holds documents"}));
 //BA.debugLineNum = 1137;BA.debugLine="WordHints.Put(\"FORMAT\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("FORMAT"),(Object)(new String[]{"Hint 1: Disk preparation","Hint 2: Clears storage","Hint 3: File system setup","Hint 4: Often done to USB drives"}));
 //BA.debugLineNum = 1138;BA.debugLine="WordHints.Put(\"GOOGLE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("GOOGLE"),(Object)(new String[]{"Hint 1: Search engine","Hint 2: Founded by Larry Page","Hint 3: Alphabet Inc.","Hint 4: Famous for its algorithm"}));
 //BA.debugLineNum = 1139;BA.debugLine="WordHints.Put(\"GOPHER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("GOPHER"),(Object)(new String[]{"Hint 1: Old internet protocol","Hint 2: Text-based","Hint 3: Pre-Web","Hint 4: 1990s use"}));
 //BA.debugLineNum = 1140;BA.debugLine="WordHints.Put(\"HACKER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("HACKER"),(Object)(new String[]{"Hint 1: Code breaker","Hint 2: Cybersecurity","Hint 3: Penetration tester","Hint 4: Often associated with computers"}));
 //BA.debugLineNum = 1141;BA.debugLine="WordHints.Put(\"HEADER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("HEADER"),(Object)(new String[]{"Hint 1: Section title","Hint 2: Top of page","Hint 3: Web document element","Hint 4: Displays info"}));
 //BA.debugLineNum = 1142;BA.debugLine="WordHints.Put(\"IMPORT\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("IMPORT"),(Object)(new String[]{"Hint 1: Data input","Hint 2: External source","Hint 3: Reverse of export","Hint 4: Data from another system"}));
 //BA.debugLineNum = 1143;BA.debugLine="WordHints.Put(\"INKJET\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("INKJET"),(Object)(new String[]{"Hint 1: Printer type","Hint 2: Cartridge-based","Hint 3: Uses liquid ink","Hint 4: Common office printer"}));
 //BA.debugLineNum = 1144;BA.debugLine="WordHints.Put(\"ITUNES\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("ITUNES"),(Object)(new String[]{"Hint 1: Music software","Hint 2: Apple product","Hint 3: Media management","Hint 4: Syncs devices"}));
 //BA.debugLineNum = 1145;BA.debugLine="WordHints.Put(\"JOVIAL\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("JOVIAL"),(Object)(new String[]{"Hint 1: Cheerful","Hint 2: Positive demeanor","Hint 3: Bright personality","Hint 4: Joyful"}));
 //BA.debugLineNum = 1146;BA.debugLine="WordHints.Put(\"KEYPAD\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("KEYPAD"),(Object)(new String[]{"Hint 1: Button interface","Hint 2: On devices","Hint 3: Numeric or QWERTY","Hint 4: Often found on phones"}));
 //BA.debugLineNum = 1147;BA.debugLine="WordHints.Put(\"LAPTOP\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("LAPTOP"),(Object)(new String[]{"Hint 1: Portable computer","Hint 2: Runs on battery","Hint 3: Can be folded","Hint 4: Personal device"}));
 //BA.debugLineNum = 1148;BA.debugLine="WordHints.Put(\"LOGOFF\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("LOGOFF"),(Object)(new String[]{"Hint 1: Exit session","Hint 2: Disconnect","Hint 3: End account","Hint 4: User logout"}));
 //BA.debugLineNum = 1149;BA.debugLine="WordHints.Put(\"LOGOUT\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("LOGOUT"),(Object)(new String[]{"Hint 1: Sign off","Hint 2: End session","Hint 3: Close account","Hint 4: Logout from system"}));
 //BA.debugLineNum = 1150;BA.debugLine="WordHints.Put(\"MEMORY\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("MEMORY"),(Object)(new String[]{"Hint 1: Stores data","Hint 2: RAM or ROM","Hint 3: Temporary or permanent","Hint 4: Computer storage"}));
 //BA.debugLineNum = 1151;BA.debugLine="WordHints.Put(\"NEWBIE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("NEWBIE"),(Object)(new String[]{"Hint 1: Beginner","Hint 2: Novice","Hint 3: New to a field","Hint 4: Often seen in online forums"}));
 //BA.debugLineNum = 1152;BA.debugLine="WordHints.Put(\"NIBBLE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("NIBBLE"),(Object)(new String[]{"Hint 1: Half a byte","Hint 2: Data unit","Hint 3: 4 bits","Hint 4: Smaller than byte"}));
 //BA.debugLineNum = 1153;BA.debugLine="WordHints.Put(\"OFFICE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("OFFICE"),(Object)(new String[]{"Hint 1: Work environment","Hint 2: Microsoft product","Hint 3: Desk and computer","Hint 4: Business-related"}));
 //BA.debugLineNum = 1154;BA.debugLine="WordHints.Put(\"ONLINE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("ONLINE"),(Object)(new String[]{"Hint 1: Connected to the internet","Hint 2: Web-based","Hint 3: Real-time","Hint 4: Available on the net"}));
 //BA.debugLineNum = 1155;BA.debugLine="WordHints.Put(\"OUTPUT\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("OUTPUT"),(Object)(new String[]{"Hint 1: Data produced","Hint 2: Result of process","Hint 3: Sent to external","Hint 4: Device response"}));
 //BA.debugLineNum = 1156;BA.debugLine="WordHints.Put(\"PACKET\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("PACKET"),(Object)(new String[]{"Hint 1: Data unit","Hint 2: Transmitted over networks","Hint 3: Contains information","Hint 4: Broken into smaller chunks"}));
 //BA.debugLineNum = 1157;BA.debugLine="WordHints.Put(\"PARITY\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("PARITY"),(Object)(new String[]{"Hint 1: Error checking","Hint 2: Odd or even","Hint 3: Data integrity","Hint 4: Used in transmission"}));
 //BA.debugLineNum = 1158;BA.debugLine="WordHints.Put(\"PARSER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("PARSER"),(Object)(new String[]{"Hint 1: Processes text","Hint 2: Compiles code","Hint 3: Breaks down data","Hint 4: Important in compilers"}));
 //BA.debugLineNum = 1159;BA.debugLine="WordHints.Put(\"PASCAL\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("PASCAL"),(Object)(new String[]{"Hint 1: Programming language","Hint 2: Developed by Niklaus Wirth","Hint 3: Old school language","Hint 4: Used in education"}));
 //BA.debugLineNum = 1160;BA.debugLine="WordHints.Put(\"PLUGIN\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("PLUGIN"),(Object)(new String[]{"Hint 1: Add-on feature","Hint 2: Extends software","Hint 3: Works with other programs","Hint 4: Enhances capabilities"}));
 //BA.debugLineNum = 1161;BA.debugLine="WordHints.Put(\"PORTAL\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("PORTAL"),(Object)(new String[]{"Hint 1: Web gateway","Hint 2: Website entry point","Hint 3: Provides access","Hint 4: Often used for services"}));
 //BA.debugLineNum = 1162;BA.debugLine="WordHints.Put(\"PROLOG\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("PROLOG"),(Object)(new String[]{"Hint 1: Programming language","Hint 2: Logic-based","Hint 3: AI and databases","Hint 4: Symbolic computation"}));
 //BA.debugLineNum = 1163;BA.debugLine="WordHints.Put(\"PROMPT\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("PROMPT"),(Object)(new String[]{"Hint 1: Command line input","Hint 2: User interaction","Hint 3: Ask for input","Hint 4: Displayed text"}));
 //BA.debugLineNum = 1164;BA.debugLine="WordHints.Put(\"RASTER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("RASTER"),(Object)(new String[]{"Hint 1: Image grid","Hint 2: Pixel-based","Hint 3: Bitmap format","Hint 4: Scanned image"}));
 //BA.debugLineNum = 1165;BA.debugLine="WordHints.Put(\"RECORD\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("RECORD"),(Object)(new String[]{"Hint 1: Data storage","Hint 2: Music album","Hint 3: Video tape","Hint 4: Input data"}));
 //BA.debugLineNum = 1166;BA.debugLine="WordHints.Put(\"REMOTE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("REMOTE"),(Object)(new String[]{"Hint 1: Control device","Hint 2: Wireless","Hint 3: TV or computer","Hint 4: Sends signals"}));
 //BA.debugLineNum = 1167;BA.debugLine="WordHints.Put(\"ROUTER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("ROUTER"),(Object)(new String[]{"Hint 1: Network device","Hint 2: Routes data","Hint 3: Internet gateway","Hint 4: Wi-Fi connection"}));
 //BA.debugLineNum = 1168;BA.debugLine="WordHints.Put(\"SCROLL\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SCROLL"),(Object)(new String[]{"Hint 1: Move screen","Hint 2: Down or up","Hint 3: Read content","Hint 4: Use mouse or keyboard"}));
 //BA.debugLineNum = 1169;BA.debugLine="WordHints.Put(\"SEARCH\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SEARCH"),(Object)(new String[]{"Hint 1: Find information","Hint 2: Internet query","Hint 3: Google function","Hint 4: Keywords used"}));
 //BA.debugLineNum = 1170;BA.debugLine="WordHints.Put(\"SECTOR\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SECTOR"),(Object)(new String[]{"Hint 1: Disk division","Hint 2: Storage part","Hint 3: Geographical area","Hint 4: File system structure"}));
 //BA.debugLineNum = 1171;BA.debugLine="WordHints.Put(\"SERVER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SERVER"),(Object)(new String[]{"Hint 1: Provides services","Hint 2: Host computer","Hint 3: Stores files","Hint 4: Network endpoint"}));
 //BA.debugLineNum = 1172;BA.debugLine="WordHints.Put(\"SMILEY\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SMILEY"),(Object)(new String[]{"Hint 1: Expression","Hint 2: Emoji","Hint 3: Happy face","Hint 4: Social media"}));
 //BA.debugLineNum = 1173;BA.debugLine="WordHints.Put(\"SNOBOL\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SNOBOL"),(Object)(new String[]{"Hint 1: Programming language","Hint 2: String processing","Hint 3: Old language","Hint 4: Pattern matching"}));
 //BA.debugLineNum = 1174;BA.debugLine="WordHints.Put(\"SPIDER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SPIDER"),(Object)(new String[]{"Hint 1: Web crawler","Hint 2: Search engine bot","Hint 3: Indexing tool","Hint 4: Collects web data"}));
 //BA.debugLineNum = 1175;BA.debugLine="WordHints.Put(\"SPRITE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SPRITE"),(Object)(new String[]{"Hint 1: 2D image","Hint 2: Animation","Hint 3: Used in games","Hint 4: Character representation"}));
 //BA.debugLineNum = 1176;BA.debugLine="WordHints.Put(\"STATUS\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("STATUS"),(Object)(new String[]{"Hint 1: Current state","Hint 2: User condition","Hint 3: System info","Hint 4: Display message"}));
 //BA.debugLineNum = 1177;BA.debugLine="WordHints.Put(\"SYSTEM\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SYSTEM"),(Object)(new String[]{"Hint 1: Set of components","Hint 2: Operating unit","Hint 3: Computer environment","Hint 4: Interrelated parts"}));
 //BA.debugLineNum = 1178;BA.debugLine="WordHints.Put(\"TABLET\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("TABLET"),(Object)(new String[]{"Hint 1: Touchscreen device","Hint 2: Portable","Hint 3: Personal computing","Hint 4: iPad or Android"}));
 //BA.debugLineNum = 1179;BA.debugLine="WordHints.Put(\"TELNET\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("TELNET"),(Object)(new String[]{"Hint 1: Remote login","Hint 2: Old internet protocol","Hint 3: Command line interface","Hint 4: Used in networking"}));
 //BA.debugLineNum = 1180;BA.debugLine="WordHints.Put(\"TROJAN\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("TROJAN"),(Object)(new String[]{"Hint 1: Malware","Hint 2: Virus type","Hint 3: Often disguised","Hint 4: Used in cyber attacks"}));
 //BA.debugLineNum = 1181;BA.debugLine="WordHints.Put(\"UPDATE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("UPDATE"),(Object)(new String[]{"Hint 1: Software improvement","Hint 2: New features","Hint 3: Version change","Hint 4: System upgrade"}));
 //BA.debugLineNum = 1182;BA.debugLine="WordHints.Put(\"UPLOAD\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("UPLOAD"),(Object)(new String[]{"Hint 1: Send data","Hint 2: Internet transfer","Hint 3: Files to server","Hint 4: Cloud service"}));
 //BA.debugLineNum = 1183;BA.debugLine="WordHints.Put(\"USERID\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("USERID"),(Object)(new String[]{"Hint 1: Login name","Hint 2: Unique identifier","Hint 3: Used in systems","Hint 4: Personal account"}));
 //BA.debugLineNum = 1184;BA.debugLine="WordHints.Put(\"VIRUS\", Array As String(\"Hint 1: M";
mostCurrent._wordhints.Put((Object)("VIRUS"),(Object)(new String[]{"Hint 1: Malicious code","Hint 2: Infects systems","Hint 3: Spreads rapidly","Hint 4: Can corrupt data"}));
 //BA.debugLineNum = 1185;BA.debugLine="WordHints.Put(\"VIRTUAL\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("VIRTUAL"),(Object)(new String[]{"Hint 1: Not physical","Hint 2: Simulated environment","Hint 3: Computer-generated","Hint 4: Used in reality simulations"}));
 //BA.debugLineNum = 1186;BA.debugLine="WordHints.Put(\"WIFI\", Array As String(\"Hint 1: Wi";
mostCurrent._wordhints.Put((Object)("WIFI"),(Object)(new String[]{"Hint 1: Wireless connection","Hint 2: Internet access","Hint 3: Router connection","Hint 4: Radio frequency"}));
 //BA.debugLineNum = 1187;BA.debugLine="WordHints.Put(\"WINDOWS\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("WINDOWS"),(Object)(new String[]{"Hint 1: Operating system","Hint 2: Microsoft product","Hint 3: GUI-based","Hint 4: Runs on PCs"}));
 //BA.debugLineNum = 1188;BA.debugLine="WordHints.Put(\"XENON\", Array As String(\"Hint 1: R";
mostCurrent._wordhints.Put((Object)("XENON"),(Object)(new String[]{"Hint 1: Rare gas","Hint 2: Noble gas","Hint 3: Used in lamps","Hint 4: Symbol Xe"}));
 //BA.debugLineNum = 1189;BA.debugLine="WordHints.Put(\"ZEBRA\", Array As String(\"Hint 1: S";
mostCurrent._wordhints.Put((Object)("ZEBRA"),(Object)(new String[]{"Hint 1: Striped animal","Hint 2: African species","Hint 3: Black and white","Hint 4: Wild horse"}));
 //BA.debugLineNum = 1190;BA.debugLine="WordHints.Put(\"SMILEY\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SMILEY"),(Object)(new String[]{"Hint 1: Emoticon symbol","Hint 2: Represents emotions","Hint 3: :) or :-D","Hint 4: Used in chats and messages"}));
 //BA.debugLineNum = 1191;BA.debugLine="WordHints.Put(\"SNOBOL\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SNOBOL"),(Object)(new String[]{"Hint 1: Programming language","Hint 2: String manipulation focused","Hint 3: Developed in 1960s","Hint 4: Known for text processing"}));
 //BA.debugLineNum = 1192;BA.debugLine="WordHints.Put(\"SPIDER\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SPIDER"),(Object)(new String[]{"Hint 1: Web crawler","Hint 2: Indexes web pages","Hint 3: Used by search engines","Hint 4: Internet bot"}));
 //BA.debugLineNum = 1193;BA.debugLine="WordHints.Put(\"SPRITE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SPRITE"),(Object)(new String[]{"Hint 1: 2D graphic object","Hint 2: Used in games","Hint 3: Moves independently","Hint 4: Often animated"}));
 //BA.debugLineNum = 1194;BA.debugLine="WordHints.Put(\"STATUS\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("STATUS"),(Object)(new String[]{"Hint 1: Current condition","Hint 2: Shown in apps","Hint 3: Online/offline indicator","Hint 4: Social media update"}));
 //BA.debugLineNum = 1195;BA.debugLine="WordHints.Put(\"SYSTEM\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("SYSTEM"),(Object)(new String[]{"Hint 1: Interconnected components","Hint 2: Operating environment","Hint 3: Software + hardware","Hint 4: Manages resources"}));
 //BA.debugLineNum = 1196;BA.debugLine="WordHints.Put(\"TABLET\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("TABLET"),(Object)(new String[]{"Hint 1: Touchscreen device","Hint 2: Larger than phone","Hint 3: Portable computer","Hint 4: Runs mobile OS"}));
 //BA.debugLineNum = 1197;BA.debugLine="WordHints.Put(\"TELNET\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("TELNET"),(Object)(new String[]{"Hint 1: Remote login protocol","Hint 2: Text-based interface","Hint 3: Uses port 23","Hint 4: Predecessor to SSH"}));
 //BA.debugLineNum = 1198;BA.debugLine="WordHints.Put(\"TROJAN\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("TROJAN"),(Object)(new String[]{"Hint 1: Malware type","Hint 2: Disguised as legit","Hint 3: Hidden threat","Hint 4: Gains unauthorized access"}));
 //BA.debugLineNum = 1199;BA.debugLine="WordHints.Put(\"UPDATE\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("UPDATE"),(Object)(new String[]{"Hint 1: Software improvement","Hint 2: Fixes bugs","Hint 3: Adds features","Hint 4: Requires download"}));
 //BA.debugLineNum = 1200;BA.debugLine="WordHints.Put(\"UPLOAD\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("UPLOAD"),(Object)(new String[]{"Hint 1: Send data online","Hint 2: Opposite of download","Hint 3: To server or cloud","Hint 4: File transfer"}));
 //BA.debugLineNum = 1201;BA.debugLine="WordHints.Put(\"UPTIME\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("UPTIME"),(Object)(new String[]{"Hint 1: Time since last reboot","Hint 2: System availability","Hint 3: Tracked in servers","Hint 4: Indicates reliability"}));
 //BA.debugLineNum = 1202;BA.debugLine="WordHints.Put(\"USENET\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("USENET"),(Object)(new String[]{"Hint 1: Online forum system","Hint 2: Pre-web messaging","Hint 3: Uses newsgroups","Hint 4: Popular in early internet"}));
 //BA.debugLineNum = 1203;BA.debugLine="WordHints.Put(\"VECTOR\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("VECTOR"),(Object)(new String[]{"Hint 1: Scalable graphics","Hint 2: Based on paths","Hint 3: Unlike bitmap","Hint 4: Used in illustrations"}));
 //BA.debugLineNum = 1204;BA.debugLine="WordHints.Put(\"WEBLOG\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("WEBLOG"),(Object)(new String[]{"Hint 1: Online journal","Hint 2: Also called blog","Hint 3: Time-stamped entries","Hint 4: Personal or professional"}));
 //BA.debugLineNum = 1205;BA.debugLine="WordHints.Put(\"WINDOW\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("WINDOW"),(Object)(new String[]{"Hint 1: GUI element","Hint 2: Contains content","Hint 3: Can be resized","Hint 4: Part of desktop UI"}));
 //BA.debugLineNum = 1206;BA.debugLine="WordHints.Put(\"WIZARD\", Array As String(\"Hint 1:";
mostCurrent._wordhints.Put((Object)("WIZARD"),(Object)(new String[]{"Hint 1: Setup assistant","Hint 2: Step-by-step help","Hint 3: Common in software installs","Hint 4: Simplifies tasks"}));
 //BA.debugLineNum = 1208;BA.debugLine="End Sub";
return "";
}
public static String  _initializegamearrays() throws Exception{
anywheresoftware.b4a.objects.EditTextWrapper[] _row1 = null;
anywheresoftware.b4a.objects.EditTextWrapper[] _row2 = null;
anywheresoftware.b4a.objects.EditTextWrapper[] _row3 = null;
anywheresoftware.b4a.objects.EditTextWrapper[] _row4 = null;
anywheresoftware.b4a.objects.EditTextWrapper[] _row5 = null;
anywheresoftware.b4a.objects.EditTextWrapper[] _row6 = null;
int _i = 0;
anywheresoftware.b4a.objects.EditTextWrapper[] _etrow = null;
anywheresoftware.b4a.objects.EditTextWrapper _et = null;
 //BA.debugLineNum = 145;BA.debugLine="Sub InitializeGameArrays";
 //BA.debugLineNum = 146;BA.debugLine="pu2_uses = 0";
_pu2_uses = (int) (0);
 //BA.debugLineNum = 147;BA.debugLine="pu1_uses = 0";
_pu1_uses = (int) (0);
 //BA.debugLineNum = 148;BA.debugLine="pow2 = 0";
_pow2 = (int) (0);
 //BA.debugLineNum = 149;BA.debugLine="pu3Used = False";
_pu3used = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 152;BA.debugLine="InitHints";
_inithints();
 //BA.debugLineNum = 158;BA.debugLine="panelsAll = Array As Object( _     Array As Panel";
mostCurrent._panelsall = new Object[]{(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn11,mostCurrent._pn12,mostCurrent._pn13,mostCurrent._pn14,mostCurrent._pn15,mostCurrent._pn16}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn21,mostCurrent._pn22,mostCurrent._pn23,mostCurrent._pn24,mostCurrent._pn25,mostCurrent._pn26}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn31,mostCurrent._pn32,mostCurrent._pn33,mostCurrent._pn34,mostCurrent._pn35,mostCurrent._pn36}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn41,mostCurrent._pn42,mostCurrent._pn43,mostCurrent._pn44,mostCurrent._pn45,mostCurrent._pn46}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn51,mostCurrent._pn52,mostCurrent._pn53,mostCurrent._pn54,mostCurrent._pn55,mostCurrent._pn56}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn61,mostCurrent._pn62,mostCurrent._pn63,mostCurrent._pn64,mostCurrent._pn65,mostCurrent._pn66})};
 //BA.debugLineNum = 168;BA.debugLine="Dim row1(6) As EditText = Array As EditText(l1t1,";
_row1 = new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l1t1,mostCurrent._l1t2,mostCurrent._l1t3,mostCurrent._l1t4,mostCurrent._l1t5,mostCurrent._l1t6};
 //BA.debugLineNum = 169;BA.debugLine="Dim row2(6) As EditText = Array As EditText(l2t1,";
_row2 = new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l2t1,mostCurrent._l2t2,mostCurrent._l2t3,mostCurrent._l2t4,mostCurrent._l2t5,mostCurrent._l2t6};
 //BA.debugLineNum = 170;BA.debugLine="Dim row3(6) As EditText = Array As EditText(l3t1,";
_row3 = new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l3t1,mostCurrent._l3t2,mostCurrent._l3t3,mostCurrent._l3t4,mostCurrent._l3t5,mostCurrent._l3t6};
 //BA.debugLineNum = 171;BA.debugLine="Dim row4(6) As EditText = Array As EditText(l4t1,";
_row4 = new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l4t1,mostCurrent._l4t2,mostCurrent._l4t3,mostCurrent._l4t4,mostCurrent._l4t5,mostCurrent._l4t6};
 //BA.debugLineNum = 172;BA.debugLine="Dim row5(6) As EditText = Array As EditText(l5t1,";
_row5 = new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l5t1,mostCurrent._l5t2,mostCurrent._l5t3,mostCurrent._l5t4,mostCurrent._l5t5,mostCurrent._l5t6};
 //BA.debugLineNum = 173;BA.debugLine="Dim row6(6) As EditText = Array As EditText(l6t1,";
_row6 = new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l6t1,mostCurrent._l6t2,mostCurrent._l6t3,mostCurrent._l6t4,mostCurrent._l6t5,mostCurrent._l6t6};
 //BA.debugLineNum = 183;BA.debugLine="etAll = Array As Object( _     Array As EditText(";
mostCurrent._etall = new Object[]{(Object)(new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l1t1,mostCurrent._l1t2,mostCurrent._l1t3,mostCurrent._l1t4,mostCurrent._l1t5,mostCurrent._l1t6}),(Object)(new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l2t1,mostCurrent._l2t2,mostCurrent._l2t3,mostCurrent._l2t4,mostCurrent._l2t5,mostCurrent._l2t6}),(Object)(new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l3t1,mostCurrent._l3t2,mostCurrent._l3t3,mostCurrent._l3t4,mostCurrent._l3t5,mostCurrent._l3t6}),(Object)(new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l4t1,mostCurrent._l4t2,mostCurrent._l4t3,mostCurrent._l4t4,mostCurrent._l4t5,mostCurrent._l4t6}),(Object)(new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l5t1,mostCurrent._l5t2,mostCurrent._l5t3,mostCurrent._l5t4,mostCurrent._l5t5,mostCurrent._l5t6}),(Object)(new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l6t1,mostCurrent._l6t2,mostCurrent._l6t3,mostCurrent._l6t4,mostCurrent._l6t5,mostCurrent._l6t6}),(Object)(new anywheresoftware.b4a.objects.EditTextWrapper[]{mostCurrent._l7t1,mostCurrent._l7t2,mostCurrent._l7t3,mostCurrent._l7t4,mostCurrent._l7t5,mostCurrent._l7t6})};
 //BA.debugLineNum = 193;BA.debugLine="panelsAll = Array As Object( _     Array As Panel";
mostCurrent._panelsall = new Object[]{(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn11,mostCurrent._pn12,mostCurrent._pn13,mostCurrent._pn14,mostCurrent._pn15,mostCurrent._pn16}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn21,mostCurrent._pn22,mostCurrent._pn23,mostCurrent._pn24,mostCurrent._pn25,mostCurrent._pn26}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn31,mostCurrent._pn32,mostCurrent._pn33,mostCurrent._pn34,mostCurrent._pn35,mostCurrent._pn36}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn41,mostCurrent._pn42,mostCurrent._pn43,mostCurrent._pn44,mostCurrent._pn45,mostCurrent._pn46}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn51,mostCurrent._pn52,mostCurrent._pn53,mostCurrent._pn54,mostCurrent._pn55,mostCurrent._pn56}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn61,mostCurrent._pn62,mostCurrent._pn63,mostCurrent._pn64,mostCurrent._pn65,mostCurrent._pn66}),(Object)(new anywheresoftware.b4a.objects.PanelWrapper[]{mostCurrent._pn71,mostCurrent._pn72,mostCurrent._pn73,mostCurrent._pn74,mostCurrent._pn75,mostCurrent._pn76})};
 //BA.debugLineNum = 205;BA.debugLine="TargetWord = currentWord ' or use a randomly sele";
mostCurrent._targetword = mostCurrent._currentword;
 //BA.debugLineNum = 207;BA.debugLine="currentAttempt = 0";
_currentattempt = (int) (0);
 //BA.debugLineNum = 209;BA.debugLine="hintCount = 0";
_hintcount = (int) (0);
 //BA.debugLineNum = 210;BA.debugLine="h1.Text = \"Hint 1: \"";
mostCurrent._h1.setText(BA.ObjectToCharSequence("Hint 1: "));
 //BA.debugLineNum = 211;BA.debugLine="h2.Text = \"Hint 2: \"";
mostCurrent._h2.setText(BA.ObjectToCharSequence("Hint 2: "));
 //BA.debugLineNum = 212;BA.debugLine="h3.Text = \"Hint 3: \"";
mostCurrent._h3.setText(BA.ObjectToCharSequence("Hint 3: "));
 //BA.debugLineNum = 213;BA.debugLine="h4.Text = \"Hint 4: \"";
mostCurrent._h4.setText(BA.ObjectToCharSequence("Hint 4: "));
 //BA.debugLineNum = 214;BA.debugLine="UsedHints.Initialize";
mostCurrent._usedhints.Initialize();
 //BA.debugLineNum = 217;BA.debugLine="UsedHints.Initialize";
mostCurrent._usedhints.Initialize();
 //BA.debugLineNum = 218;BA.debugLine="PowerUp1Used = False";
_powerup1used = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 220;BA.debugLine="ShowPrimaryHint(currentWord)";
_showprimaryhint(mostCurrent._currentword);
 //BA.debugLineNum = 228;BA.debugLine="EditTexts(0,0) = l1t1";
mostCurrent._edittexts[(int) (0)][(int) (0)] = mostCurrent._l1t1;
 //BA.debugLineNum = 229;BA.debugLine="EditTexts(0,1) = l1t2";
mostCurrent._edittexts[(int) (0)][(int) (1)] = mostCurrent._l1t2;
 //BA.debugLineNum = 230;BA.debugLine="EditTexts(0,2) = l1t3";
mostCurrent._edittexts[(int) (0)][(int) (2)] = mostCurrent._l1t3;
 //BA.debugLineNum = 231;BA.debugLine="EditTexts(0,3) = l1t4";
mostCurrent._edittexts[(int) (0)][(int) (3)] = mostCurrent._l1t4;
 //BA.debugLineNum = 232;BA.debugLine="EditTexts(0,4) = l1t5";
mostCurrent._edittexts[(int) (0)][(int) (4)] = mostCurrent._l1t5;
 //BA.debugLineNum = 233;BA.debugLine="EditTexts(0,5) = l1t6";
mostCurrent._edittexts[(int) (0)][(int) (5)] = mostCurrent._l1t6;
 //BA.debugLineNum = 235;BA.debugLine="EditTexts(1,0) = l2t1";
mostCurrent._edittexts[(int) (1)][(int) (0)] = mostCurrent._l2t1;
 //BA.debugLineNum = 236;BA.debugLine="EditTexts(1,1) = l2t2";
mostCurrent._edittexts[(int) (1)][(int) (1)] = mostCurrent._l2t2;
 //BA.debugLineNum = 237;BA.debugLine="EditTexts(1,2) = l2t3";
mostCurrent._edittexts[(int) (1)][(int) (2)] = mostCurrent._l2t3;
 //BA.debugLineNum = 238;BA.debugLine="EditTexts(1,3) = l2t4";
mostCurrent._edittexts[(int) (1)][(int) (3)] = mostCurrent._l2t4;
 //BA.debugLineNum = 239;BA.debugLine="EditTexts(1,4) = l2t5";
mostCurrent._edittexts[(int) (1)][(int) (4)] = mostCurrent._l2t5;
 //BA.debugLineNum = 240;BA.debugLine="EditTexts(1,5) = l2t6";
mostCurrent._edittexts[(int) (1)][(int) (5)] = mostCurrent._l2t6;
 //BA.debugLineNum = 242;BA.debugLine="EditTexts(2,0) = l3t1";
mostCurrent._edittexts[(int) (2)][(int) (0)] = mostCurrent._l3t1;
 //BA.debugLineNum = 243;BA.debugLine="EditTexts(2,1) = l3t2";
mostCurrent._edittexts[(int) (2)][(int) (1)] = mostCurrent._l3t2;
 //BA.debugLineNum = 244;BA.debugLine="EditTexts(2,2) = l3t3";
mostCurrent._edittexts[(int) (2)][(int) (2)] = mostCurrent._l3t3;
 //BA.debugLineNum = 245;BA.debugLine="EditTexts(2,3) = l3t4";
mostCurrent._edittexts[(int) (2)][(int) (3)] = mostCurrent._l3t4;
 //BA.debugLineNum = 246;BA.debugLine="EditTexts(2,4) = l3t5";
mostCurrent._edittexts[(int) (2)][(int) (4)] = mostCurrent._l3t5;
 //BA.debugLineNum = 247;BA.debugLine="EditTexts(2,5) = l3t6";
mostCurrent._edittexts[(int) (2)][(int) (5)] = mostCurrent._l3t6;
 //BA.debugLineNum = 249;BA.debugLine="EditTexts(3,0) = l4t1";
mostCurrent._edittexts[(int) (3)][(int) (0)] = mostCurrent._l4t1;
 //BA.debugLineNum = 250;BA.debugLine="EditTexts(3,1) = l4t2";
mostCurrent._edittexts[(int) (3)][(int) (1)] = mostCurrent._l4t2;
 //BA.debugLineNum = 251;BA.debugLine="EditTexts(3,2) = l4t3";
mostCurrent._edittexts[(int) (3)][(int) (2)] = mostCurrent._l4t3;
 //BA.debugLineNum = 252;BA.debugLine="EditTexts(3,3) = l4t4";
mostCurrent._edittexts[(int) (3)][(int) (3)] = mostCurrent._l4t4;
 //BA.debugLineNum = 253;BA.debugLine="EditTexts(3,4) = l4t5";
mostCurrent._edittexts[(int) (3)][(int) (4)] = mostCurrent._l4t5;
 //BA.debugLineNum = 254;BA.debugLine="EditTexts(3,5) = l4t6";
mostCurrent._edittexts[(int) (3)][(int) (5)] = mostCurrent._l4t6;
 //BA.debugLineNum = 256;BA.debugLine="EditTexts(4,0) = l5t1";
mostCurrent._edittexts[(int) (4)][(int) (0)] = mostCurrent._l5t1;
 //BA.debugLineNum = 257;BA.debugLine="EditTexts(4,1) = l5t2";
mostCurrent._edittexts[(int) (4)][(int) (1)] = mostCurrent._l5t2;
 //BA.debugLineNum = 258;BA.debugLine="EditTexts(4,2) = l5t3";
mostCurrent._edittexts[(int) (4)][(int) (2)] = mostCurrent._l5t3;
 //BA.debugLineNum = 259;BA.debugLine="EditTexts(4,3) = l5t4";
mostCurrent._edittexts[(int) (4)][(int) (3)] = mostCurrent._l5t4;
 //BA.debugLineNum = 260;BA.debugLine="EditTexts(4,4) = l5t5";
mostCurrent._edittexts[(int) (4)][(int) (4)] = mostCurrent._l5t5;
 //BA.debugLineNum = 261;BA.debugLine="EditTexts(4,5) = l5t6";
mostCurrent._edittexts[(int) (4)][(int) (5)] = mostCurrent._l5t6;
 //BA.debugLineNum = 263;BA.debugLine="EditTexts(5,0) = l6t1";
mostCurrent._edittexts[(int) (5)][(int) (0)] = mostCurrent._l6t1;
 //BA.debugLineNum = 264;BA.debugLine="EditTexts(5,1) = l6t2";
mostCurrent._edittexts[(int) (5)][(int) (1)] = mostCurrent._l6t2;
 //BA.debugLineNum = 265;BA.debugLine="EditTexts(5,2) = l6t3";
mostCurrent._edittexts[(int) (5)][(int) (2)] = mostCurrent._l6t3;
 //BA.debugLineNum = 266;BA.debugLine="EditTexts(5,3) = l6t4";
mostCurrent._edittexts[(int) (5)][(int) (3)] = mostCurrent._l6t4;
 //BA.debugLineNum = 267;BA.debugLine="EditTexts(5,4) = l6t5";
mostCurrent._edittexts[(int) (5)][(int) (4)] = mostCurrent._l6t5;
 //BA.debugLineNum = 268;BA.debugLine="EditTexts(5,5) = l6t6";
mostCurrent._edittexts[(int) (5)][(int) (5)] = mostCurrent._l6t6;
 //BA.debugLineNum = 270;BA.debugLine="EditTexts(6,0) = l7t1";
mostCurrent._edittexts[(int) (6)][(int) (0)] = mostCurrent._l7t1;
 //BA.debugLineNum = 271;BA.debugLine="EditTexts(6,1) = l7t2";
mostCurrent._edittexts[(int) (6)][(int) (1)] = mostCurrent._l7t2;
 //BA.debugLineNum = 272;BA.debugLine="EditTexts(6,2) = l7t3";
mostCurrent._edittexts[(int) (6)][(int) (2)] = mostCurrent._l7t3;
 //BA.debugLineNum = 273;BA.debugLine="EditTexts(6,3) = l7t4";
mostCurrent._edittexts[(int) (6)][(int) (3)] = mostCurrent._l7t4;
 //BA.debugLineNum = 274;BA.debugLine="EditTexts(6,4) = l7t5";
mostCurrent._edittexts[(int) (6)][(int) (4)] = mostCurrent._l7t5;
 //BA.debugLineNum = 275;BA.debugLine="EditTexts(6,5) = l7t6";
mostCurrent._edittexts[(int) (6)][(int) (5)] = mostCurrent._l7t6;
 //BA.debugLineNum = 278;BA.debugLine="Panels(0,0) = pn11";
mostCurrent._panels[(int) (0)][(int) (0)] = mostCurrent._pn11;
 //BA.debugLineNum = 279;BA.debugLine="Panels(0,1) = pn12";
mostCurrent._panels[(int) (0)][(int) (1)] = mostCurrent._pn12;
 //BA.debugLineNum = 280;BA.debugLine="Panels(0,2) = pn13";
mostCurrent._panels[(int) (0)][(int) (2)] = mostCurrent._pn13;
 //BA.debugLineNum = 281;BA.debugLine="Panels(0,3) = pn14";
mostCurrent._panels[(int) (0)][(int) (3)] = mostCurrent._pn14;
 //BA.debugLineNum = 282;BA.debugLine="Panels(0,4) = pn15";
mostCurrent._panels[(int) (0)][(int) (4)] = mostCurrent._pn15;
 //BA.debugLineNum = 283;BA.debugLine="Panels(0,5) = pn16";
mostCurrent._panels[(int) (0)][(int) (5)] = mostCurrent._pn16;
 //BA.debugLineNum = 285;BA.debugLine="Panels(1,0) = pn21";
mostCurrent._panels[(int) (1)][(int) (0)] = mostCurrent._pn21;
 //BA.debugLineNum = 286;BA.debugLine="Panels(1,1) = pn22";
mostCurrent._panels[(int) (1)][(int) (1)] = mostCurrent._pn22;
 //BA.debugLineNum = 287;BA.debugLine="Panels(1,2) = pn23";
mostCurrent._panels[(int) (1)][(int) (2)] = mostCurrent._pn23;
 //BA.debugLineNum = 288;BA.debugLine="Panels(1,3) = pn24";
mostCurrent._panels[(int) (1)][(int) (3)] = mostCurrent._pn24;
 //BA.debugLineNum = 289;BA.debugLine="Panels(1,4) = pn25";
mostCurrent._panels[(int) (1)][(int) (4)] = mostCurrent._pn25;
 //BA.debugLineNum = 290;BA.debugLine="Panels(1,5) = pn26";
mostCurrent._panels[(int) (1)][(int) (5)] = mostCurrent._pn26;
 //BA.debugLineNum = 292;BA.debugLine="Panels(2,0) = pn31";
mostCurrent._panels[(int) (2)][(int) (0)] = mostCurrent._pn31;
 //BA.debugLineNum = 293;BA.debugLine="Panels(2,1) = pn32";
mostCurrent._panels[(int) (2)][(int) (1)] = mostCurrent._pn32;
 //BA.debugLineNum = 294;BA.debugLine="Panels(2,2) = pn33";
mostCurrent._panels[(int) (2)][(int) (2)] = mostCurrent._pn33;
 //BA.debugLineNum = 295;BA.debugLine="Panels(2,3) = pn34";
mostCurrent._panels[(int) (2)][(int) (3)] = mostCurrent._pn34;
 //BA.debugLineNum = 296;BA.debugLine="Panels(2,4) = pn35";
mostCurrent._panels[(int) (2)][(int) (4)] = mostCurrent._pn35;
 //BA.debugLineNum = 297;BA.debugLine="Panels(2,5) = pn36";
mostCurrent._panels[(int) (2)][(int) (5)] = mostCurrent._pn36;
 //BA.debugLineNum = 299;BA.debugLine="Panels(3,0) = pn41";
mostCurrent._panels[(int) (3)][(int) (0)] = mostCurrent._pn41;
 //BA.debugLineNum = 300;BA.debugLine="Panels(3,1) = pn42";
mostCurrent._panels[(int) (3)][(int) (1)] = mostCurrent._pn42;
 //BA.debugLineNum = 301;BA.debugLine="Panels(3,2) = pn43";
mostCurrent._panels[(int) (3)][(int) (2)] = mostCurrent._pn43;
 //BA.debugLineNum = 302;BA.debugLine="Panels(3,3) = pn44";
mostCurrent._panels[(int) (3)][(int) (3)] = mostCurrent._pn44;
 //BA.debugLineNum = 303;BA.debugLine="Panels(3,4) = pn45";
mostCurrent._panels[(int) (3)][(int) (4)] = mostCurrent._pn45;
 //BA.debugLineNum = 304;BA.debugLine="Panels(3,5) = pn46";
mostCurrent._panels[(int) (3)][(int) (5)] = mostCurrent._pn46;
 //BA.debugLineNum = 306;BA.debugLine="Panels(4,0) = pn51";
mostCurrent._panels[(int) (4)][(int) (0)] = mostCurrent._pn51;
 //BA.debugLineNum = 307;BA.debugLine="Panels(4,1) = pn52";
mostCurrent._panels[(int) (4)][(int) (1)] = mostCurrent._pn52;
 //BA.debugLineNum = 308;BA.debugLine="Panels(4,2) = pn53";
mostCurrent._panels[(int) (4)][(int) (2)] = mostCurrent._pn53;
 //BA.debugLineNum = 309;BA.debugLine="Panels(4,3) = pn54";
mostCurrent._panels[(int) (4)][(int) (3)] = mostCurrent._pn54;
 //BA.debugLineNum = 310;BA.debugLine="Panels(4,4) = pn55";
mostCurrent._panels[(int) (4)][(int) (4)] = mostCurrent._pn55;
 //BA.debugLineNum = 311;BA.debugLine="Panels(4,5) = pn56";
mostCurrent._panels[(int) (4)][(int) (5)] = mostCurrent._pn56;
 //BA.debugLineNum = 313;BA.debugLine="Panels(5,0) = pn61";
mostCurrent._panels[(int) (5)][(int) (0)] = mostCurrent._pn61;
 //BA.debugLineNum = 314;BA.debugLine="Panels(5,1) = pn62";
mostCurrent._panels[(int) (5)][(int) (1)] = mostCurrent._pn62;
 //BA.debugLineNum = 315;BA.debugLine="Panels(5,2) = pn63";
mostCurrent._panels[(int) (5)][(int) (2)] = mostCurrent._pn63;
 //BA.debugLineNum = 316;BA.debugLine="Panels(5,3) = pn64";
mostCurrent._panels[(int) (5)][(int) (3)] = mostCurrent._pn64;
 //BA.debugLineNum = 317;BA.debugLine="Panels(5,4) = pn65";
mostCurrent._panels[(int) (5)][(int) (4)] = mostCurrent._pn65;
 //BA.debugLineNum = 318;BA.debugLine="Panels(5,5) = pn66";
mostCurrent._panels[(int) (5)][(int) (5)] = mostCurrent._pn66;
 //BA.debugLineNum = 320;BA.debugLine="Panels(6,0) = pn71";
mostCurrent._panels[(int) (6)][(int) (0)] = mostCurrent._pn71;
 //BA.debugLineNum = 321;BA.debugLine="Panels(6,1) = pn72";
mostCurrent._panels[(int) (6)][(int) (1)] = mostCurrent._pn72;
 //BA.debugLineNum = 322;BA.debugLine="Panels(6,2) = pn73";
mostCurrent._panels[(int) (6)][(int) (2)] = mostCurrent._pn73;
 //BA.debugLineNum = 323;BA.debugLine="Panels(6,3) = pn74";
mostCurrent._panels[(int) (6)][(int) (3)] = mostCurrent._pn74;
 //BA.debugLineNum = 324;BA.debugLine="Panels(6,4) = pn75";
mostCurrent._panels[(int) (6)][(int) (4)] = mostCurrent._pn75;
 //BA.debugLineNum = 325;BA.debugLine="Panels(6,5) = pn76";
mostCurrent._panels[(int) (6)][(int) (5)] = mostCurrent._pn76;
 //BA.debugLineNum = 327;BA.debugLine="For i = 1 To etAll.Length - 1";
{
final int step110 = 1;
final int limit110 = (int) (mostCurrent._etall.length-1);
_i = (int) (1) ;
for (;_i <= limit110 ;_i = _i + step110 ) {
 //BA.debugLineNum = 328;BA.debugLine="Dim etRow() As EditText = etAll(i)";
_etrow = (anywheresoftware.b4a.objects.EditTextWrapper[])(mostCurrent._etall[_i]);
 //BA.debugLineNum = 329;BA.debugLine="For Each et As EditText In etRow";
{
final anywheresoftware.b4a.objects.EditTextWrapper[] group112 = _etrow;
final int groupLen112 = group112.length
;int index112 = 0;
;
for (; index112 < groupLen112;index112++){
_et = group112[index112];
 //BA.debugLineNum = 330;BA.debugLine="et.Enabled = False";
_et.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }
};
 }
};
 //BA.debugLineNum = 336;BA.debugLine="End Sub";
return "";
}
public static String  _l1t1_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 613;BA.debugLine="Sub l1t1_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 614;BA.debugLine="HandleTextChanged(l1t1, Old, New)";
_handletextchanged(mostCurrent._l1t1,_old,_new);
 //BA.debugLineNum = 615;BA.debugLine="End Sub";
return "";
}
public static String  _l1t2_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 616;BA.debugLine="Sub l1t2_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 617;BA.debugLine="HandleTextChanged(l1t2, Old, New)";
_handletextchanged(mostCurrent._l1t2,_old,_new);
 //BA.debugLineNum = 618;BA.debugLine="End Sub";
return "";
}
public static String  _l1t3_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 619;BA.debugLine="Sub l1t3_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 620;BA.debugLine="HandleTextChanged(l1t3, Old, New)";
_handletextchanged(mostCurrent._l1t3,_old,_new);
 //BA.debugLineNum = 621;BA.debugLine="End Sub";
return "";
}
public static String  _l1t4_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 622;BA.debugLine="Sub l1t4_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 623;BA.debugLine="HandleTextChanged(l1t4, Old, New)";
_handletextchanged(mostCurrent._l1t4,_old,_new);
 //BA.debugLineNum = 624;BA.debugLine="End Sub";
return "";
}
public static String  _l1t5_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 625;BA.debugLine="Sub l1t5_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 626;BA.debugLine="HandleTextChanged(l1t5, Old, New)";
_handletextchanged(mostCurrent._l1t5,_old,_new);
 //BA.debugLineNum = 627;BA.debugLine="End Sub";
return "";
}
public static String  _l1t6_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 628;BA.debugLine="Sub l1t6_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 629;BA.debugLine="HandleTextChanged(l1t6, Old, New)";
_handletextchanged(mostCurrent._l1t6,_old,_new);
 //BA.debugLineNum = 630;BA.debugLine="End Sub";
return "";
}
public static String  _l2t1_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 631;BA.debugLine="Sub l2t1_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 632;BA.debugLine="HandleTextChanged(l2t1, Old, New)";
_handletextchanged(mostCurrent._l2t1,_old,_new);
 //BA.debugLineNum = 633;BA.debugLine="End Sub";
return "";
}
public static String  _l2t2_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 634;BA.debugLine="Sub l2t2_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 635;BA.debugLine="HandleTextChanged(l2t2, Old, New)";
_handletextchanged(mostCurrent._l2t2,_old,_new);
 //BA.debugLineNum = 636;BA.debugLine="End Sub";
return "";
}
public static String  _l2t3_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 637;BA.debugLine="Sub l2t3_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 638;BA.debugLine="HandleTextChanged(l2t3, Old, New)";
_handletextchanged(mostCurrent._l2t3,_old,_new);
 //BA.debugLineNum = 639;BA.debugLine="End Sub";
return "";
}
public static String  _l2t4_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 640;BA.debugLine="Sub l2t4_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 641;BA.debugLine="HandleTextChanged(l2t4, Old, New)";
_handletextchanged(mostCurrent._l2t4,_old,_new);
 //BA.debugLineNum = 642;BA.debugLine="End Sub";
return "";
}
public static String  _l2t5_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 643;BA.debugLine="Sub l2t5_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 644;BA.debugLine="HandleTextChanged(l2t5, Old, New)";
_handletextchanged(mostCurrent._l2t5,_old,_new);
 //BA.debugLineNum = 645;BA.debugLine="End Sub";
return "";
}
public static String  _l2t6_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 646;BA.debugLine="Sub l2t6_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 647;BA.debugLine="HandleTextChanged(l2t6, Old, New)";
_handletextchanged(mostCurrent._l2t6,_old,_new);
 //BA.debugLineNum = 648;BA.debugLine="End Sub";
return "";
}
public static String  _l3t1_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 649;BA.debugLine="Sub l3t1_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 650;BA.debugLine="HandleTextChanged(l3t1, Old, New)";
_handletextchanged(mostCurrent._l3t1,_old,_new);
 //BA.debugLineNum = 651;BA.debugLine="End Sub";
return "";
}
public static String  _l3t2_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 652;BA.debugLine="Sub l3t2_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 653;BA.debugLine="HandleTextChanged(l3t2, Old, New)";
_handletextchanged(mostCurrent._l3t2,_old,_new);
 //BA.debugLineNum = 654;BA.debugLine="End Sub";
return "";
}
public static String  _l3t3_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 655;BA.debugLine="Sub l3t3_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 656;BA.debugLine="HandleTextChanged(l3t3, Old, New)";
_handletextchanged(mostCurrent._l3t3,_old,_new);
 //BA.debugLineNum = 657;BA.debugLine="End Sub";
return "";
}
public static String  _l3t4_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 658;BA.debugLine="Sub l3t4_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 659;BA.debugLine="HandleTextChanged(l3t4, Old, New)";
_handletextchanged(mostCurrent._l3t4,_old,_new);
 //BA.debugLineNum = 660;BA.debugLine="End Sub";
return "";
}
public static String  _l3t5_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 661;BA.debugLine="Sub l3t5_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 662;BA.debugLine="HandleTextChanged(l3t5, Old, New)";
_handletextchanged(mostCurrent._l3t5,_old,_new);
 //BA.debugLineNum = 663;BA.debugLine="End Sub";
return "";
}
public static String  _l3t6_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 664;BA.debugLine="Sub l3t6_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 665;BA.debugLine="HandleTextChanged(l3t6, Old, New)";
_handletextchanged(mostCurrent._l3t6,_old,_new);
 //BA.debugLineNum = 666;BA.debugLine="End Sub";
return "";
}
public static String  _l4t1_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 667;BA.debugLine="Sub l4t1_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 668;BA.debugLine="HandleTextChanged(l4t1, Old, New)";
_handletextchanged(mostCurrent._l4t1,_old,_new);
 //BA.debugLineNum = 669;BA.debugLine="End Sub";
return "";
}
public static String  _l4t2_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 670;BA.debugLine="Sub l4t2_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 671;BA.debugLine="HandleTextChanged(l4t2, Old, New)";
_handletextchanged(mostCurrent._l4t2,_old,_new);
 //BA.debugLineNum = 672;BA.debugLine="End Sub";
return "";
}
public static String  _l4t3_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 673;BA.debugLine="Sub l4t3_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 674;BA.debugLine="HandleTextChanged(l4t3, Old, New)";
_handletextchanged(mostCurrent._l4t3,_old,_new);
 //BA.debugLineNum = 675;BA.debugLine="End Sub";
return "";
}
public static String  _l4t4_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 676;BA.debugLine="Sub l4t4_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 677;BA.debugLine="HandleTextChanged(l4t4, Old, New)";
_handletextchanged(mostCurrent._l4t4,_old,_new);
 //BA.debugLineNum = 678;BA.debugLine="End Sub";
return "";
}
public static String  _l4t5_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 679;BA.debugLine="Sub l4t5_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 680;BA.debugLine="HandleTextChanged(l4t5, Old, New)";
_handletextchanged(mostCurrent._l4t5,_old,_new);
 //BA.debugLineNum = 681;BA.debugLine="End Sub";
return "";
}
public static String  _l4t6_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 682;BA.debugLine="Sub l4t6_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 683;BA.debugLine="HandleTextChanged(l4t6, Old, New)";
_handletextchanged(mostCurrent._l4t6,_old,_new);
 //BA.debugLineNum = 684;BA.debugLine="End Sub";
return "";
}
public static String  _l5t1_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 685;BA.debugLine="Sub l5t1_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 686;BA.debugLine="HandleTextChanged(l5t1, Old, New)";
_handletextchanged(mostCurrent._l5t1,_old,_new);
 //BA.debugLineNum = 687;BA.debugLine="End Sub";
return "";
}
public static String  _l5t2_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 688;BA.debugLine="Sub l5t2_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 689;BA.debugLine="HandleTextChanged(l5t2, Old, New)";
_handletextchanged(mostCurrent._l5t2,_old,_new);
 //BA.debugLineNum = 690;BA.debugLine="End Sub";
return "";
}
public static String  _l5t3_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 691;BA.debugLine="Sub l5t3_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 692;BA.debugLine="HandleTextChanged(l5t3, Old, New)";
_handletextchanged(mostCurrent._l5t3,_old,_new);
 //BA.debugLineNum = 693;BA.debugLine="End Sub";
return "";
}
public static String  _l5t4_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 694;BA.debugLine="Sub l5t4_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 695;BA.debugLine="HandleTextChanged(l5t4, Old, New)";
_handletextchanged(mostCurrent._l5t4,_old,_new);
 //BA.debugLineNum = 696;BA.debugLine="End Sub";
return "";
}
public static String  _l5t5_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 697;BA.debugLine="Sub l5t5_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 698;BA.debugLine="HandleTextChanged(l5t5, Old, New)";
_handletextchanged(mostCurrent._l5t5,_old,_new);
 //BA.debugLineNum = 699;BA.debugLine="End Sub";
return "";
}
public static String  _l5t6_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 700;BA.debugLine="Sub l5t6_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 701;BA.debugLine="HandleTextChanged(l5t6, Old, New)";
_handletextchanged(mostCurrent._l5t6,_old,_new);
 //BA.debugLineNum = 702;BA.debugLine="End Sub";
return "";
}
public static String  _l6t1_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 703;BA.debugLine="Sub l6t1_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 704;BA.debugLine="HandleTextChanged(l6t1, Old, New)";
_handletextchanged(mostCurrent._l6t1,_old,_new);
 //BA.debugLineNum = 705;BA.debugLine="End Sub";
return "";
}
public static String  _l6t2_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 706;BA.debugLine="Sub l6t2_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 707;BA.debugLine="HandleTextChanged(l6t2, Old, New)";
_handletextchanged(mostCurrent._l6t2,_old,_new);
 //BA.debugLineNum = 708;BA.debugLine="End Sub";
return "";
}
public static String  _l6t3_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 709;BA.debugLine="Sub l6t3_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 710;BA.debugLine="HandleTextChanged(l6t3, Old, New)";
_handletextchanged(mostCurrent._l6t3,_old,_new);
 //BA.debugLineNum = 711;BA.debugLine="End Sub";
return "";
}
public static String  _l6t4_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 712;BA.debugLine="Sub l6t4_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 713;BA.debugLine="HandleTextChanged(l6t4, Old, New)";
_handletextchanged(mostCurrent._l6t4,_old,_new);
 //BA.debugLineNum = 714;BA.debugLine="End Sub";
return "";
}
public static String  _l6t5_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 715;BA.debugLine="Sub l6t5_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 716;BA.debugLine="HandleTextChanged(l6t5, Old, New)";
_handletextchanged(mostCurrent._l6t5,_old,_new);
 //BA.debugLineNum = 717;BA.debugLine="End Sub";
return "";
}
public static String  _l6t6_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 718;BA.debugLine="Sub l6t6_TextChanged (Old As String, New As String";
 //BA.debugLineNum = 719;BA.debugLine="HandleTextChanged(l6t6, Old, New)";
_handletextchanged(mostCurrent._l6t6,_old,_new);
 //BA.debugLineNum = 720;BA.debugLine="End Sub";
return "";
}
public static String  _menu_click() throws Exception{
 //BA.debugLineNum = 1071;BA.debugLine="Private Sub menu_Click";
 //BA.debugLineNum = 1072;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 1074;BA.debugLine="If menu_panel.Visible = False Then";
if (mostCurrent._menu_panel.getVisible()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1075;BA.debugLine="menu_panel.Visible = True";
mostCurrent._menu_panel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1076;BA.debugLine="menu_panel.BringToFront";
mostCurrent._menu_panel.BringToFront();
 //BA.debugLineNum = 1077;BA.debugLine="cover.Visible = True";
mostCurrent._cover.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1078;BA.debugLine="nexl.Enabled = False";
mostCurrent._nexl.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1079;BA.debugLine="submit.Enabled = False";
mostCurrent._submit.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1080;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1081;BA.debugLine="pu2.Enabled = False";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1082;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1083;BA.debugLine="surr.Enabled = False";
mostCurrent._surr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 1087;BA.debugLine="If time.Enabled Then";
if (mostCurrent._time.getEnabled()) { 
 //BA.debugLineNum = 1088;BA.debugLine="submit.Enabled = True";
mostCurrent._submit.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1089;BA.debugLine="pu1.Enabled = True";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1090;BA.debugLine="pu2.Enabled = True";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1091;BA.debugLine="pu3.Enabled = True";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1092;BA.debugLine="surr.Enabled = True";
mostCurrent._surr.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 1094;BA.debugLine="nexl.Enabled = True";
mostCurrent._nexl.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 1097;BA.debugLine="menu_panel.Visible = False";
mostCurrent._menu_panel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1098;BA.debugLine="cover.Visible = False";
mostCurrent._cover.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1101;BA.debugLine="End Sub";
return "";
}
public static String  _nexl_click() throws Exception{
 //BA.debugLineNum = 1209;BA.debugLine="Private Sub nexl_Click";
 //BA.debugLineNum = 1210;BA.debugLine="Main.gp = Main.gp + 1";
mostCurrent._main._gp /*int*/  = (int) (mostCurrent._main._gp /*int*/ +1);
 //BA.debugLineNum = 1211;BA.debugLine="File.WriteString(File.DirInternal, \"gp.txt\", Main";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"gp.txt",BA.NumberToString(mostCurrent._main._gp /*int*/ ));
 //BA.debugLineNum = 1212;BA.debugLine="Main.mods = \"game\"";
mostCurrent._main._mods /*String*/  = "game";
 //BA.debugLineNum = 1213;BA.debugLine="StartActivity(\"loading\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("loading"));
 //BA.debugLineNum = 1214;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 1216;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="End Sub";
return "";
}
public static String  _pu1_click() throws Exception{
String[] _hints = null;
boolean _foundhint = false;
String _hinttext = "";
int _i = 0;
String _h = "";
 //BA.debugLineNum = 1002;BA.debugLine="Sub pu1_Click";
 //BA.debugLineNum = 1004;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 1005;BA.debugLine="If WordHints.ContainsKey(currentWord) Then";
if (mostCurrent._wordhints.ContainsKey((Object)(mostCurrent._currentword))) { 
 //BA.debugLineNum = 1006;BA.debugLine="Dim hints() As String = WordHints.Get(currentWor";
_hints = (String[])(mostCurrent._wordhints.Get((Object)(mostCurrent._currentword)));
 //BA.debugLineNum = 1007;BA.debugLine="Dim foundHint As Boolean = False";
_foundhint = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1008;BA.debugLine="Dim hintText As String = \"\"";
_hinttext = "";
 //BA.debugLineNum = 1010;BA.debugLine="For i = hintIndex To hints.Length - 1";
{
final int step6 = 1;
final int limit6 = (int) (_hints.length-1);
_i = _hintindex ;
for (;_i <= limit6 ;_i = _i + step6 ) {
 //BA.debugLineNum = 1011;BA.debugLine="Dim h As String = hints(i)";
_h = _hints[_i];
 //BA.debugLineNum = 1012;BA.debugLine="If UsedHints.IndexOf(h) = -1 Then";
if (mostCurrent._usedhints.IndexOf((Object)(_h))==-1) { 
 //BA.debugLineNum = 1013;BA.debugLine="hintText = h";
_hinttext = _h;
 //BA.debugLineNum = 1014;BA.debugLine="UsedHints.Add(h)";
mostCurrent._usedhints.Add((Object)(_h));
 //BA.debugLineNum = 1015;BA.debugLine="hintIndex = i + 1 ' move forward";
_hintindex = (int) (_i+1);
 //BA.debugLineNum = 1016;BA.debugLine="foundHint = True";
_foundhint = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1017;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 1021;BA.debugLine="If foundHint Then";
if (_foundhint) { 
 //BA.debugLineNum = 1022;BA.debugLine="pu1_uses = pu1_uses + 1";
_pu1_uses = (int) (_pu1_uses+1);
 //BA.debugLineNum = 1023;BA.debugLine="Select UsedHints.Size ' Because primary hint is";
switch (BA.switchObjectToInt(mostCurrent._usedhints.getSize(),(int) (2),(int) (3),(int) (4))) {
case 0: {
 //BA.debugLineNum = 1025;BA.debugLine="h2.Text = hintText";
mostCurrent._h2.setText(BA.ObjectToCharSequence(_hinttext));
 break; }
case 1: {
 //BA.debugLineNum = 1027;BA.debugLine="h3.Text = hintText";
mostCurrent._h3.setText(BA.ObjectToCharSequence(_hinttext));
 break; }
case 2: {
 //BA.debugLineNum = 1029;BA.debugLine="h4.Text = hintText";
mostCurrent._h4.setText(BA.ObjectToCharSequence(_hinttext));
 break; }
default: {
 //BA.debugLineNum = 1031;BA.debugLine="ToastMessageShow(\"No more hints available.\",";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No more hints available."),anywheresoftware.b4a.keywords.Common.False);
 break; }
}
;
 }else {
 //BA.debugLineNum = 1034;BA.debugLine="ToastMessageShow(\"No more hints available.\", Fa";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No more hints available."),anywheresoftware.b4a.keywords.Common.False);
 };
 }else {
 //BA.debugLineNum = 1037;BA.debugLine="ToastMessageShow(\"Hints not available for this w";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Hints not available for this word."),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 1039;BA.debugLine="End Sub";
return "";
}
public static String  _pu2_click() throws Exception{
anywheresoftware.b4a.objects.collections.List _revealedletters = null;
int _i = 0;
String _txt = "";
anywheresoftware.b4a.objects.collections.List _unrevealedletters = null;
String _correctletter = "";
int _tryindex = 0;
String _lettertoreveal = "";
anywheresoftware.b4a.objects.collections.List _validwrongpos = null;
String _userletter = "";
int _postoreveal = 0;
 //BA.debugLineNum = 896;BA.debugLine="Sub pu2_click";
 //BA.debugLineNum = 897;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 898;BA.debugLine="If pu2_uses >= 2 Then";
if (_pu2_uses>=2) { 
 //BA.debugLineNum = 899;BA.debugLine="ToastMessageShow(\"Power-Up 2 can only be used tw";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Power-Up 2 can only be used twice!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 900;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 903;BA.debugLine="Dim currentRow As Int = currentLine - 1";
_currentrow = (int) (_currentline-1);
 //BA.debugLineNum = 904;BA.debugLine="If currentRow < 0 Then";
if (_currentrow<0) { 
 //BA.debugLineNum = 905;BA.debugLine="ToastMessageShow(\"Use Power-Up after the first g";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Use Power-Up after the first guess!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 906;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 909;BA.debugLine="Dim CorrectWord As String = currentWord.ToUpperCa";
mostCurrent._correctword = mostCurrent._currentword.toUpperCase();
 //BA.debugLineNum = 912;BA.debugLine="Dim revealedLetters As List";
_revealedletters = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 913;BA.debugLine="revealedLetters.Initialize";
_revealedletters.Initialize();
 //BA.debugLineNum = 914;BA.debugLine="For i = 0 To 5";
{
final int step14 = 1;
final int limit14 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit14 ;_i = _i + step14 ) {
 //BA.debugLineNum = 915;BA.debugLine="Dim txt As String = EditTexts(currentRow, i).Tex";
_txt = mostCurrent._edittexts[_currentrow][_i].getText().toUpperCase();
 //BA.debugLineNum = 916;BA.debugLine="If txt.Length > 0 And revealedLetters.IndexOf(tx";
if (_txt.length()>0 && _revealedletters.IndexOf((Object)(_txt))==-1) { 
 //BA.debugLineNum = 917;BA.debugLine="revealedLetters.Add(txt)";
_revealedletters.Add((Object)(_txt));
 };
 }
};
 //BA.debugLineNum = 922;BA.debugLine="Dim unrevealedLetters As List";
_unrevealedletters = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 923;BA.debugLine="unrevealedLetters.Initialize";
_unrevealedletters.Initialize();
 //BA.debugLineNum = 924;BA.debugLine="For i = 0 To 5";
{
final int step22 = 1;
final int limit22 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit22 ;_i = _i + step22 ) {
 //BA.debugLineNum = 925;BA.debugLine="Dim correctLetter As String = CorrectWord.SubStr";
_correctletter = mostCurrent._correctword.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 926;BA.debugLine="If revealedLetters.IndexOf(correctLetter) = -1 A";
if (_revealedletters.IndexOf((Object)(_correctletter))==-1 && _unrevealedletters.IndexOf((Object)(_correctletter))==-1) { 
 //BA.debugLineNum = 927;BA.debugLine="unrevealedLetters.Add(correctLetter)";
_unrevealedletters.Add((Object)(_correctletter));
 };
 }
};
 //BA.debugLineNum = 931;BA.debugLine="If unrevealedLetters.Size = 0 Then";
if (_unrevealedletters.getSize()==0) { 
 //BA.debugLineNum = 932;BA.debugLine="ToastMessageShow(\"No unrevealed correct letters";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No unrevealed correct letters available!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 933;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 937;BA.debugLine="For tryIndex = 0 To unrevealedLetters.Size - 1";
{
final int step32 = 1;
final int limit32 = (int) (_unrevealedletters.getSize()-1);
_tryindex = (int) (0) ;
for (;_tryindex <= limit32 ;_tryindex = _tryindex + step32 ) {
 //BA.debugLineNum = 938;BA.debugLine="Dim letterToReveal As String = unrevealedLetters";
_lettertoreveal = BA.ObjectToString(_unrevealedletters.Get(_tryindex));
 //BA.debugLineNum = 941;BA.debugLine="Dim validWrongPos As List";
_validwrongpos = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 942;BA.debugLine="validWrongPos.Initialize";
_validwrongpos.Initialize();
 //BA.debugLineNum = 943;BA.debugLine="For i = 0 To 5";
{
final int step36 = 1;
final int limit36 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit36 ;_i = _i + step36 ) {
 //BA.debugLineNum = 944;BA.debugLine="Dim correctLetter As String = CorrectWord.SubSt";
_correctletter = mostCurrent._correctword.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 945;BA.debugLine="Dim userLetter As String = EditTexts(currentRow";
_userletter = mostCurrent._edittexts[_currentrow][_i].getText().toUpperCase();
 //BA.debugLineNum = 946;BA.debugLine="If correctLetter <> letterToReveal And userLett";
if ((_correctletter).equals(_lettertoreveal) == false && (_userletter).equals("")) { 
 //BA.debugLineNum = 947;BA.debugLine="validWrongPos.Add(i)";
_validwrongpos.Add((Object)(_i));
 };
 }
};
 //BA.debugLineNum = 951;BA.debugLine="If validWrongPos.Size > 0 Then";
if (_validwrongpos.getSize()>0) { 
 //BA.debugLineNum = 952;BA.debugLine="Dim posToReveal As Int = validWrongPos.Get(Rnd(";
_postoreveal = (int)(BA.ObjectToNumber(_validwrongpos.Get(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),_validwrongpos.getSize()))));
 //BA.debugLineNum = 953;BA.debugLine="EditTexts(currentRow, posToReveal).Text = lette";
mostCurrent._edittexts[_currentrow][_postoreveal].setText(BA.ObjectToCharSequence(_lettertoreveal));
 //BA.debugLineNum = 954;BA.debugLine="EditTexts(currentRow, posToReveal).Enabled = Fa";
mostCurrent._edittexts[_currentrow][_postoreveal].setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 955;BA.debugLine="Panels(currentRow, posToReveal).Color = Colors.";
mostCurrent._panels[_currentrow][_postoreveal].setColor(anywheresoftware.b4a.keywords.Common.Colors.Blue);
 //BA.debugLineNum = 956;BA.debugLine="pu2_uses = pu2_uses + 1";
_pu2_uses = (int) (_pu2_uses+1);
 //BA.debugLineNum = 957;BA.debugLine="ToastMessageShow(\"Power-Up 2 used (\" & pu2_uses";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Power-Up 2 used ("+BA.NumberToString(_pu2_uses)+"/2)"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 958;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 962;BA.debugLine="For i = 0 To 5";
{
final int step52 = 1;
final int limit52 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit52 ;_i = _i + step52 ) {
 //BA.debugLineNum = 963;BA.debugLine="Dim correctLetter As String = CorrectWord.SubSt";
_correctletter = mostCurrent._correctword.substring(_i,(int) (_i+1));
 //BA.debugLineNum = 964;BA.debugLine="Dim userLetter As String = EditTexts(currentRow";
_userletter = mostCurrent._edittexts[_currentrow][_i].getText().toUpperCase();
 //BA.debugLineNum = 965;BA.debugLine="If correctLetter = letterToReveal And userLette";
if ((_correctletter).equals(_lettertoreveal) && (_userletter).equals("")) { 
 //BA.debugLineNum = 966;BA.debugLine="EditTexts(currentRow, i).Text = letterToReveal";
mostCurrent._edittexts[_currentrow][_i].setText(BA.ObjectToCharSequence(_lettertoreveal));
 //BA.debugLineNum = 967;BA.debugLine="EditTexts(currentRow, i).Enabled = False";
mostCurrent._edittexts[_currentrow][_i].setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 968;BA.debugLine="Panels(currentRow, i).Color = Colors.Green";
mostCurrent._panels[_currentrow][_i].setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 969;BA.debugLine="pow2 = pow2 + 1";
_pow2 = (int) (_pow2+1);
 //BA.debugLineNum = 970;BA.debugLine="ToastMessageShow(\"Power-Up 2 used (\" & pu2_use";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Power-Up 2 used ("+BA.NumberToString(_pu2_uses)+"/2)"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 971;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 }
};
 //BA.debugLineNum = 976;BA.debugLine="ToastMessageShow(\"No available position to reveal";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No available position to reveal any letter!"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 977;BA.debugLine="End Sub";
return "";
}
public static String  _pu3_click() throws Exception{
anywheresoftware.b4a.objects.EditTextWrapper[] _etfields = null;
anywheresoftware.b4a.objects.collections.Map _revealedletters = null;
int _row = 0;
anywheresoftware.b4a.objects.EditTextWrapper[] _previousrow = null;
int _i = 0;
anywheresoftware.b4a.objects.collections.List _unrevealedpositions = null;
int _randindex = 0;
int _pos = 0;
String _correctletter = "";
 //BA.debugLineNum = 848;BA.debugLine="Private Sub pu3_Click";
 //BA.debugLineNum = 849;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 850;BA.debugLine="If pu3Used Then";
if (_pu3used) { 
 //BA.debugLineNum = 851;BA.debugLine="Msgbox(\"Power-up already used.\", \"Empty\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Power-up already used."),BA.ObjectToCharSequence("Empty"),mostCurrent.activityBA);
 //BA.debugLineNum = 852;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 855;BA.debugLine="Dim etFields() As EditText = etAll(currentAttempt";
_etfields = (anywheresoftware.b4a.objects.EditTextWrapper[])(mostCurrent._etall[_currentattempt]);
 //BA.debugLineNum = 856;BA.debugLine="Dim revealedLetters As Map";
_revealedletters = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 857;BA.debugLine="revealedLetters.Initialize";
_revealedletters.Initialize();
 //BA.debugLineNum = 860;BA.debugLine="For row = 0 To currentAttempt - 1";
{
final int step9 = 1;
final int limit9 = (int) (_currentattempt-1);
_row = (int) (0) ;
for (;_row <= limit9 ;_row = _row + step9 ) {
 //BA.debugLineNum = 861;BA.debugLine="Dim previousRow() As EditText = etAll(row)";
_previousrow = (anywheresoftware.b4a.objects.EditTextWrapper[])(mostCurrent._etall[_row]);
 //BA.debugLineNum = 862;BA.debugLine="For i = 0 To 5";
{
final int step11 = 1;
final int limit11 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit11 ;_i = _i + step11 ) {
 //BA.debugLineNum = 863;BA.debugLine="If previousRow(i).Text.ToUpperCase = currentWor";
if ((_previousrow[_i].getText().toUpperCase()).equals(BA.ObjectToString(mostCurrent._currentword.charAt(_i)))) { 
 //BA.debugLineNum = 864;BA.debugLine="revealedLetters.Put(i, True)";
_revealedletters.Put((Object)(_i),(Object)(anywheresoftware.b4a.keywords.Common.True));
 };
 }
};
 }
};
 //BA.debugLineNum = 870;BA.debugLine="Dim unrevealedPositions As List";
_unrevealedpositions = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 871;BA.debugLine="unrevealedPositions.Initialize";
_unrevealedpositions.Initialize();
 //BA.debugLineNum = 872;BA.debugLine="For i = 0 To 5";
{
final int step19 = 1;
final int limit19 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit19 ;_i = _i + step19 ) {
 //BA.debugLineNum = 873;BA.debugLine="If Not(revealedLetters.ContainsKey(i)) And etFie";
if (anywheresoftware.b4a.keywords.Common.Not(_revealedletters.ContainsKey((Object)(_i))) && _etfields[_i].getEnabled()) { 
 //BA.debugLineNum = 874;BA.debugLine="unrevealedPositions.Add(i)";
_unrevealedpositions.Add((Object)(_i));
 };
 }
};
 //BA.debugLineNum = 878;BA.debugLine="If unrevealedPositions.Size = 0 Then";
if (_unrevealedpositions.getSize()==0) { 
 //BA.debugLineNum = 879;BA.debugLine="Msgbox(\"All correct letters already revealed.\",";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("All correct letters already revealed."),BA.ObjectToCharSequence("Info"),mostCurrent.activityBA);
 //BA.debugLineNum = 880;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 884;BA.debugLine="Dim randIndex As Int = Rnd(0, unrevealedPositions";
_randindex = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),_unrevealedpositions.getSize());
 //BA.debugLineNum = 885;BA.debugLine="Dim pos As Int = unrevealedPositions.Get(randInde";
_pos = (int)(BA.ObjectToNumber(_unrevealedpositions.Get(_randindex)));
 //BA.debugLineNum = 886;BA.debugLine="Dim correctLetter As String = currentWord.CharAt(";
_correctletter = BA.ObjectToString(mostCurrent._currentword.charAt(_pos));
 //BA.debugLineNum = 888;BA.debugLine="etFields(pos).Text = correctLetter";
_etfields[_pos].setText(BA.ObjectToCharSequence(_correctletter));
 //BA.debugLineNum = 889;BA.debugLine="etFields(pos).Enabled = False";
_etfields[_pos].setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 890;BA.debugLine="etFields(pos).Color = Colors.Green";
_etfields[_pos].setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 892;BA.debugLine="pu3Used = True";
_pu3used = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 893;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 894;BA.debugLine="pu3.Text = \"Used\"";
mostCurrent._pu3.setText(BA.ObjectToCharSequence("Used"));
 //BA.debugLineNum = 895;BA.debugLine="End Sub";
return "";
}
public static String  _resetgame() throws Exception{
 //BA.debugLineNum = 120;BA.debugLine="Sub ResetGame";
 //BA.debugLineNum = 131;BA.debugLine="currentLine = 1";
_currentline = (int) (1);
 //BA.debugLineNum = 132;BA.debugLine="pu1_uses = 0";
_pu1_uses = (int) (0);
 //BA.debugLineNum = 133;BA.debugLine="pu2_uses = 0";
_pu2_uses = (int) (0);
 //BA.debugLineNum = 134;BA.debugLine="pu3Used = False";
_pu3used = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 135;BA.debugLine="gameIsRunning = True";
_gameisrunning = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 138;BA.debugLine="currentWord = wordList.Get(Rnd(0, wordList.Size))";
mostCurrent._currentword = BA.ObjectToString(mostCurrent._wordlist.Get(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),mostCurrent._wordlist.getSize())));
 //BA.debugLineNum = 141;BA.debugLine="elapsedTime = 0";
_elapsedtime = (int) (0);
 //BA.debugLineNum = 142;BA.debugLine="time.Enabled = True";
mostCurrent._time.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 143;BA.debugLine="InitializeGameArrays";
_initializegamearrays();
 //BA.debugLineNum = 144;BA.debugLine="End Sub";
return "";
}
public static String  _showprimaryhint(String _word) throws Exception{
String[] _hints = null;
 //BA.debugLineNum = 1040;BA.debugLine="Sub ShowPrimaryHint(word As String)";
 //BA.debugLineNum = 1041;BA.debugLine="If WordHints.ContainsKey(word) Then";
if (mostCurrent._wordhints.ContainsKey((Object)(_word))) { 
 //BA.debugLineNum = 1042;BA.debugLine="Dim hints() As String = WordHints.Get(word)";
_hints = (String[])(mostCurrent._wordhints.Get((Object)(_word)));
 //BA.debugLineNum = 1043;BA.debugLine="If hints.Length > 0 Then";
if (_hints.length>0) { 
 //BA.debugLineNum = 1044;BA.debugLine="h1.Text = hints(0)  ' Always show first hint as";
mostCurrent._h1.setText(BA.ObjectToCharSequence(_hints[(int) (0)]));
 //BA.debugLineNum = 1045;BA.debugLine="UsedHints.Clear";
mostCurrent._usedhints.Clear();
 //BA.debugLineNum = 1046;BA.debugLine="UsedHints.Add(hints(0)) ' Mark primary hint as";
mostCurrent._usedhints.Add((Object)(_hints[(int) (0)]));
 //BA.debugLineNum = 1047;BA.debugLine="hintIndex = 1 ' Start from second hint for powe";
_hintindex = (int) (1);
 //BA.debugLineNum = 1048;BA.debugLine="h2.Text = \"\"";
mostCurrent._h2.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 1049;BA.debugLine="h3.Text = \"\"";
mostCurrent._h3.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 1050;BA.debugLine="h4.Text = \"\"";
mostCurrent._h4.setText(BA.ObjectToCharSequence(""));
 }else {
 //BA.debugLineNum = 1052;BA.debugLine="h1.Text = \"\"";
mostCurrent._h1.setText(BA.ObjectToCharSequence(""));
 };
 }else {
 //BA.debugLineNum = 1055;BA.debugLine="h1.Text = \"\"";
mostCurrent._h1.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 1057;BA.debugLine="End Sub";
return "";
}
public static String  _size() throws Exception{
double _xx = 0;
double _yy = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper _view = null;
 //BA.debugLineNum = 383;BA.debugLine="Sub Size";
 //BA.debugLineNum = 384;BA.debugLine="Dim xx,yy As Double";
_xx = 0;
_yy = 0;
 //BA.debugLineNum = 385;BA.debugLine="xx=((100%x)/450)/Density";
_xx = ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA))/(double)450)/(double)anywheresoftware.b4a.keywords.Common.Density;
 //BA.debugLineNum = 386;BA.debugLine="yy=((100%y)/ 800)/Density";
_yy = ((anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA))/(double)800)/(double)anywheresoftware.b4a.keywords.Common.Density;
 //BA.debugLineNum = 387;BA.debugLine="For Each View As View In Activity.GetAllViewsRecu";
_view = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group4 = mostCurrent._activity.GetAllViewsRecursive();
final int groupLen4 = group4.getSize()
;int index4 = 0;
;
for (; index4 < groupLen4;index4++){
_view = (anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(group4.Get(index4)));
 //BA.debugLineNum = 388;BA.debugLine="View.Width=(View.Width) * xx";
_view.setWidth((int) ((_view.getWidth())*_xx));
 //BA.debugLineNum = 389;BA.debugLine="View.Left=(View.Left) * xx";
_view.setLeft((int) ((_view.getLeft())*_xx));
 //BA.debugLineNum = 390;BA.debugLine="View.Height=(View.Height)* yy";
_view.setHeight((int) ((_view.getHeight())*_yy));
 //BA.debugLineNum = 391;BA.debugLine="View.Top=(View.Top)*yy";
_view.setTop((int) ((_view.getTop())*_yy));
 }
};
 //BA.debugLineNum = 393;BA.debugLine="End Sub";
return "";
}
public static String  _submit_click() throws Exception{
anywheresoftware.b4a.objects.EditTextWrapper[] _etfields = null;
anywheresoftware.b4a.objects.EditTextWrapper _et = null;
String _match = "";
boolean[] _used = null;
boolean[] _matchletters = null;
int _i = 0;
int _j = 0;
anywheresoftware.b4a.objects.PanelWrapper[] _pl = null;
anywheresoftware.b4a.objects.EditTextWrapper[] _nextrow = null;
 //BA.debugLineNum = 395;BA.debugLine="Private Sub submit_Click";
 //BA.debugLineNum = 396;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 398;BA.debugLine="Dim pc As Map";
mostCurrent._pc = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 399;BA.debugLine="pc.Initialize";
mostCurrent._pc.Initialize();
 //BA.debugLineNum = 402;BA.debugLine="If currentAttempt >= etAll.Length Then";
if (_currentattempt>=mostCurrent._etall.length) { 
 //BA.debugLineNum = 403;BA.debugLine="Msgbox(\"No more attempts left!\", \"Game Over\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("No more attempts left!"),BA.ObjectToCharSequence("Game Over"),mostCurrent.activityBA);
 //BA.debugLineNum = 404;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 408;BA.debugLine="Dim etFields() As EditText = etAll(currentAttempt";
_etfields = (anywheresoftware.b4a.objects.EditTextWrapper[])(mostCurrent._etall[_currentattempt]);
 //BA.debugLineNum = 409;BA.debugLine="For Each et As EditText In etFields";
{
final anywheresoftware.b4a.objects.EditTextWrapper[] group9 = _etfields;
final int groupLen9 = group9.length
;int index9 = 0;
;
for (; index9 < groupLen9;index9++){
_et = group9[index9];
 //BA.debugLineNum = 410;BA.debugLine="If et.Text.Trim = \"\" Then";
if ((_et.getText().trim()).equals("")) { 
 //BA.debugLineNum = 411;BA.debugLine="MsgboxAsync(\"Fill all the blanks first!\", \"Note";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Fill all the blanks first!"),BA.ObjectToCharSequence("Note"),processBA);
 //BA.debugLineNum = 412;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 //BA.debugLineNum = 417;BA.debugLine="Dim match As String = \"\"";
_match = "";
 //BA.debugLineNum = 418;BA.debugLine="For Each et As EditText In etFields";
{
final anywheresoftware.b4a.objects.EditTextWrapper[] group16 = _etfields;
final int groupLen16 = group16.length
;int index16 = 0;
;
for (; index16 < groupLen16;index16++){
_et = group16[index16];
 //BA.debugLineNum = 419;BA.debugLine="match = match & et.Text.Trim";
_match = _match+_et.getText().trim();
 }
};
 //BA.debugLineNum = 422;BA.debugLine="match = match.ToUpperCase";
_match = _match.toUpperCase();
 //BA.debugLineNum = 425;BA.debugLine="If match.Length <> 6 Then";
if (_match.length()!=6) { 
 //BA.debugLineNum = 426;BA.debugLine="Msgbox(\"Fill all 6 letters!\", \"Note\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Fill all 6 letters!"),BA.ObjectToCharSequence("Note"),mostCurrent.activityBA);
 //BA.debugLineNum = 427;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 431;BA.debugLine="Dim used(6) As Boolean";
_used = new boolean[(int) (6)];
;
 //BA.debugLineNum = 432;BA.debugLine="Dim matchLetters(6) As Boolean";
_matchletters = new boolean[(int) (6)];
;
 //BA.debugLineNum = 435;BA.debugLine="For i = 0 To 5";
{
final int step26 = 1;
final int limit26 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit26 ;_i = _i + step26 ) {
 //BA.debugLineNum = 436;BA.debugLine="If match.CharAt(i) = TargetWord.CharAt(i) Then";
if (_match.charAt(_i)==mostCurrent._targetword.charAt(_i)) { 
 //BA.debugLineNum = 437;BA.debugLine="pc.Put(i, Colors.Green)";
mostCurrent._pc.Put((Object)(_i),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Green));
 //BA.debugLineNum = 438;BA.debugLine="used(i) = True";
_used[_i] = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 439;BA.debugLine="matchLetters(i) = True";
_matchletters[_i] = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 441;BA.debugLine="pc.Put(i, Colors.Red)";
mostCurrent._pc.Put((Object)(_i),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Red));
 };
 }
};
 //BA.debugLineNum = 446;BA.debugLine="For i = 0 To 5";
{
final int step35 = 1;
final int limit35 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit35 ;_i = _i + step35 ) {
 //BA.debugLineNum = 447;BA.debugLine="If pc.Get(i) = Colors.Red Then";
if ((mostCurrent._pc.Get((Object)(_i))).equals((Object)(anywheresoftware.b4a.keywords.Common.Colors.Red))) { 
 //BA.debugLineNum = 448;BA.debugLine="For j = 0 To 5";
{
final int step37 = 1;
final int limit37 = (int) (5);
_j = (int) (0) ;
for (;_j <= limit37 ;_j = _j + step37 ) {
 //BA.debugLineNum = 449;BA.debugLine="If match.CharAt(i) = TargetWord.CharAt(j) And";
if (_match.charAt(_i)==mostCurrent._targetword.charAt(_j) && anywheresoftware.b4a.keywords.Common.Not(_used[_j]) && anywheresoftware.b4a.keywords.Common.Not(_matchletters[_j])) { 
 //BA.debugLineNum = 450;BA.debugLine="pc.Put(i, Colors.Blue)";
mostCurrent._pc.Put((Object)(_i),(Object)(anywheresoftware.b4a.keywords.Common.Colors.Blue));
 //BA.debugLineNum = 451;BA.debugLine="used(j) = True";
_used[_j] = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 452;BA.debugLine="matchLetters(j) = True";
_matchletters[_j] = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 453;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 };
 }
};
 //BA.debugLineNum = 460;BA.debugLine="Dim pl() As Panel = panelsAll(currentAttempt)";
_pl = (anywheresoftware.b4a.objects.PanelWrapper[])(mostCurrent._panelsall[_currentattempt]);
 //BA.debugLineNum = 461;BA.debugLine="For i = 0 To 5";
{
final int step48 = 1;
final int limit48 = (int) (5);
_i = (int) (0) ;
for (;_i <= limit48 ;_i = _i + step48 ) {
 //BA.debugLineNum = 462;BA.debugLine="pl(i).Color = pc.Get(i)";
_pl[_i].setColor((int)(BA.ObjectToNumber(mostCurrent._pc.Get((Object)(_i)))));
 //BA.debugLineNum = 463;BA.debugLine="etFields(i).Enabled = False";
_etfields[_i].setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }
};
 //BA.debugLineNum = 467;BA.debugLine="If match = TargetWord Then";
if ((_match).equals(mostCurrent._targetword)) { 
 //BA.debugLineNum = 468;BA.debugLine="GameOver(True)";
_gameover(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 471;BA.debugLine="currentAttempt = currentAttempt + 1";
_currentattempt = (int) (_currentattempt+1);
 //BA.debugLineNum = 474;BA.debugLine="If currentAttempt < etAll.Length Then";
if (_currentattempt<mostCurrent._etall.length) { 
 //BA.debugLineNum = 475;BA.debugLine="Dim nextRow() As EditText = etAll(currentAttemp";
_nextrow = (anywheresoftware.b4a.objects.EditTextWrapper[])(mostCurrent._etall[_currentattempt]);
 //BA.debugLineNum = 476;BA.debugLine="For Each et As EditText In nextRow";
{
final anywheresoftware.b4a.objects.EditTextWrapper[] group58 = _nextrow;
final int groupLen58 = group58.length
;int index58 = 0;
;
for (; index58 < groupLen58;index58++){
_et = group58[index58];
 //BA.debugLineNum = 477;BA.debugLine="et.Enabled = True";
_et.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }
};
 //BA.debugLineNum = 480;BA.debugLine="nextRow(0).RequestFocus";
_nextrow[(int) (0)].RequestFocus();
 }else {
 //BA.debugLineNum = 482;BA.debugLine="GameOver(False) ' No more attempts left";
_gameover(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 484;BA.debugLine="atmp = atmp + 1";
_atmp = (int) (_atmp+1);
 };
 //BA.debugLineNum = 487;BA.debugLine="If currentAttempt = 6 Then";
if (_currentattempt==6) { 
 //BA.debugLineNum = 488;BA.debugLine="GameOver(False)";
_gameover(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 491;BA.debugLine="End Sub";
return "";
}
public static String  _surr_click() throws Exception{
 //BA.debugLineNum = 1058;BA.debugLine="Sub surr_click";
 //BA.debugLineNum = 1059;BA.debugLine="Main.ts = Main.ts + 1";
mostCurrent._main._ts /*int*/  = (int) (mostCurrent._main._ts /*int*/ +1);
 //BA.debugLineNum = 1060;BA.debugLine="File.WriteString(File.DirInternal, \"ts.txt\", Main";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"ts.txt",BA.NumberToString(mostCurrent._main._ts /*int*/ ));
 //BA.debugLineNum = 1061;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 1064;BA.debugLine="GameOver(False)";
_gameover(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1066;BA.debugLine="submit.Enabled = False";
mostCurrent._submit.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1067;BA.debugLine="pu1.Enabled = False";
mostCurrent._pu1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1068;BA.debugLine="pu2.Enabled = False";
mostCurrent._pu2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1069;BA.debugLine="pu3.Enabled = False";
mostCurrent._pu3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1070;BA.debugLine="End Sub";
return "";
}
public static String  _time_tick() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub time_Tick";
 //BA.debugLineNum = 99;BA.debugLine="seconds = seconds + 1";
_seconds = (int) (_seconds+1);
 //BA.debugLineNum = 100;BA.debugLine="Log(\"Timer tick: \" & seconds) ' Check if this pri";
anywheresoftware.b4a.keywords.Common.LogImpl("61048578","Timer tick: "+BA.NumberToString(_seconds),0);
 //BA.debugLineNum = 101;BA.debugLine="updatelabel";
_updatelabel();
 //BA.debugLineNum = 102;BA.debugLine="elapsedTime = elapsedTime + 1";
_elapsedtime = (int) (_elapsedtime+1);
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}
public static String  _to_main_click() throws Exception{
 //BA.debugLineNum = 1235;BA.debugLine="Private Sub to_main_Click";
 //BA.debugLineNum = 1236;BA.debugLine="Main.cl1.play";
mostCurrent._main._cl1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 1237;BA.debugLine="Main.bg2.Stop";
mostCurrent._main._bg2 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Stop();
 //BA.debugLineNum = 1238;BA.debugLine="Main.bg1.Initialize()";
mostCurrent._main._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Initialize();
 //BA.debugLineNum = 1239;BA.debugLine="Main.bg1.Load(File.DirAssets, \"nbgm1.mp3\")";
mostCurrent._main._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"nbgm1.mp3");
 //BA.debugLineNum = 1240;BA.debugLine="Main.bg1.SetVolume(Main.vl1, Main.vl1)";
mostCurrent._main._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .SetVolume(mostCurrent._main._vl1 /*float*/ ,mostCurrent._main._vl1 /*float*/ );
 //BA.debugLineNum = 1241;BA.debugLine="Main.bg1.Looping = True";
mostCurrent._main._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .setLooping(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1243;BA.debugLine="Main.bg1.play";
mostCurrent._main._bg1 /*anywheresoftware.b4a.objects.MediaPlayerWrapper*/ .Play();
 //BA.debugLineNum = 1244;BA.debugLine="StartActivity(\"loading\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("loading"));
 //BA.debugLineNum = 1245;BA.debugLine="Main.mods = \"Main\"";
mostCurrent._main._mods /*String*/  = "Main";
 //BA.debugLineNum = 1246;BA.debugLine="Activity.finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 1247;BA.debugLine="End Sub";
return "";
}
public static String  _updatelabel() throws Exception{
int _mins = 0;
int _sec = 0;
 //BA.debugLineNum = 104;BA.debugLine="Sub updatelabel";
 //BA.debugLineNum = 105;BA.debugLine="Dim mins As Int = Floor(seconds/60)";
_mins = (int) (anywheresoftware.b4a.keywords.Common.Floor(_seconds/(double)60));
 //BA.debugLineNum = 106;BA.debugLine="Dim sec As Int = seconds Mod 60";
_sec = (int) (_seconds%60);
 //BA.debugLineNum = 107;BA.debugLine="l2.Text = NumberFormat(mins, 2, 0) & \":\" & Number";
mostCurrent._l2.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.NumberFormat(_mins,(int) (2),(int) (0))+":"+anywheresoftware.b4a.keywords.Common.NumberFormat(_sec,(int) (2),(int) (0))));
 //BA.debugLineNum = 110;BA.debugLine="End Sub";
return "";
}
}
