B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=13.1
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: true
	#IncludeTitle: false
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private prog As ProgressBar
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("ls")
	Size
	prog.Progress = 0


	' Set red progress drawable (only works with Horizontal style)
	Dim bg As ColorDrawable
	bg.Initialize(Colors.ARGB(195, 173, 216, 230), 10dip) ' Light blue with 50% opacity
	prog.Background = bg



	' Set red progress drawable (only works with Horizontal style)


	StartProgress

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

Sub StartProgress
	For i = 0 To 100
		prog.Progress = i
		Sleep(50) 
	Next
	
	If prog.Progress = 100 Then
		StartActivity(Main.mods)
		Main.mods = ""
		Activity.Finish
		
	End If
	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
