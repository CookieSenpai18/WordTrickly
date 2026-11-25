B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: True
	#IncludeTitle: false
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	Private b1 As Button
	Private cb1 As CheckBox
	Private cb2 As CheckBox
	Private sb1 As SeekBar
	Private sb2 As SeekBar
	Dim v1 As Float = Main.vl1
	Dim v2 As Float = Main. vl2
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("settings")
	
	b1.Color = Colors.ARGB(0, 0, 0, 0)
	Main.bg1.play
	sb1.Max = 100
'	Dim vol As Int = GetSavedVolume

	sb2.Max = 100



	If MusicService.bg1.IsInitialized Then
		Log("MusicService.bg1 is initialized")
	Else
		Log("MusicService.bg1 is NOT initialized")
	End If
	sb1.Value = Main.vl1 * 100
	sb2.Value = Main.vl2 * 100
	Size
End Sub


Sub Size
	Dim xx,yy As Double
	xx=((100%x)/450)/Density
	yy=((100%y)/ 800)/Density
	For Each View As View In Activity.GetAllViewsRecursive
		View.Width=(View.Width) * xx
		View.Left=(View.Left) * xx
		View.Height=(View.Height)* yy
		View.Top=(View.Top)*yy
	Next
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
'Sub GetSavedVolume As Int
'	If File.Exists(File.DirInternal, "volume.txt") Then
'		Return File.ReadString(File.DirInternal, "volume.txt")
'	Else
'		Return 50 ' Default volume
'	End If
'End Sub
Sub SaveVolume(v As Int)
	File.WriteString(File.DirInternal, "volume.txt", v1 & "," & v2)
End Sub


Private Sub sb2_ValueChanged (Value As Int, UserChanged As Boolean)
	v2 = Value / 100
	Main.cl1.SetVolume(v2, v2)
	Main.cl2.SetVolume(v2, v2)
	File.WriteString(File.DirInternal, "volume.txt", v1 & "," & v2)

Sleep(5)
	If v2 <=0 Then
		cb2.Checked = True
	Else
		cb2.Checked = False
	End If
End Sub

Private Sub sb1_ValueChanged (Value As Int, UserChanged As Boolean)
	v1 = Value / 100
	Main.bg1.SetVolume(v1, v1)
	Main.bg2.SetVolume(v1, v1)
	File.WriteString(File.DirInternal, "volume.txt", v1 & "," & v2)
Sleep(5)
	If v1 <=0 Then
		cb1.Checked = True
	Else
		cb1.Checked = False
	End If
	
End Sub

Private Sub cb2_CheckedChange(Checked As Boolean)
	If cb2.Checked = True Then
		sb2.Value = 0
		Else
		Main.cl2.play
			sb2.Value = Main.vl2 * 100
	End If
End Sub

Private Sub cb1_CheckedChange(Checked As Boolean)
	If cb1.Checked = True Then
		Main.cl2.play
		sb1.Value = 0
	Else
		Main.cl2.play
		sb1.Value = Main.vl1 * 100
	End If
End Sub

Private Sub b1_Click
	
	
	Dim data As String = v1 & "," & v2
	File.WriteString(File.DirInternal, "volume.txt", data)
	
	Main.vl1 = v1
	Main.vl2 = v2
	Main.cl1.play
	StartActivity("Main")
	Activity.Finish
End Sub

Sub Activity_KeyPress (KeyCode As Int) As Boolean
	If KeyCode = KeyCodes.KEYCODE_BACK Then
	Main.vl1 = v1
	Main.vl2 = v2
	Main.cl1.play
	StartActivity("Main")
	Activity.Finish
	End If
	Return False ' Allow default behavior for other keys
End Sub