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

	Private b1 As Button
End Sub



Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("credits")

	b1.Color = Colors.ARGB(0, 0, 0, 0)
	Main.bg1.play
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

Private Sub b1_Click
	Main.cl1.play
	StartActivity("Main")
	Activity.Finish
End Sub