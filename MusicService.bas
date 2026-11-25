B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@

Sub Process_Globals
	Public bg1 As MediaPlayer
	Public bg2 As MediaPlayer
	Private started As Boolean = False
End Sub

Sub Globals
	
End Sub

Sub Service_Create

	Log("bg1 initialized in Service_Create")
	bg1.Initialize()
	bg1.Load(File.DirAssets, "nbgm1.mp3")
	bg1.Looping = True

	bg2.Initialize()
	bg2.Load(File.DirAssets, "nbgm2.mp3")
	bg2.Looping = True
End Sub
Sub Service_Start (StartingIntent As Intent)


	Log("Service_Start")
	If started = False Then
		bg1.Play
		bg2.Play
		started = True
		Log("Music started")
	Else
	Log("Music already started")
	End If
End Sub

Sub Service_Destroy
	Log("Service_Destroy")
	bg1.Stop
	bg2.Stop
End Sub