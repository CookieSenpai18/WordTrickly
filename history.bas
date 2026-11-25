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
	Private cw As Label
	Private gp As Label
	Private lview As ListView
	Private ts As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
		Activity.LoadLayout("history")
		
		lview.TwoLineslayout.ItemHeight = 75dip
		lview.TwoLinesLayout.Label.TextSize = 10
		lview.TwoLinesLayout.SecondLabel.TextSize = 12
		lview.TwoLinesLayout.Label.textColor = Colors.Cyan
		lview.TwoLinesLayout.SecondLabel.textColor = Colors.black
		lview.TwoLineslayout.Label.Typeface = Typeface.MONOSPACE
		lview.TwoLinesLayout.SecondLabel.Typeface = Typeface.MONOSPACE
		
		If lview.Size > 20 Then
			lview.RemoveAt(20)
		End If
		
	
		Main.bg1.Play
		
		gp.text = Main.gp
		cw.text = Main.cw
	ts.text = Main.ts
		
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
	If Main.bg1.IsPlaying = False Then
		Main.bg1.play
	End If
	
	lview.Clear
'	For Each item() As String In Main.datas
'		Dim name As String = item(0)
'		Dim att As Int = item(1)
'		Dim time As String = item(2)
'		Dim score As Int = item(3)
'		Dim header As String = "IGN             Attempts No.    Time Finished     Score"
'		Dim lbl2 As String = name & "         " & att & "            " & time & "          " & score
'		lview.AddTwoLines(header, lbl2)
'	Next

	Dim reversedList As List
	reversedList.Initialize

	' Copy items in reverse order
	For i = Main.datlist.Size - 1 To 0 Step -1
		reversedList.Add(Main.datlist.Get(i))
	Next
	For Each line As String In Main.datlist
		Dim parts() As String = Regex.Split(",", line)
		If parts.Length >= 4 Then
			Dim name As String = parts(0)
			Dim att As Int = parts(1)
			Dim time As String = parts(2)
			Dim score As Int = parts(3)
        
			Dim header As String = "IGN             Attempts No.    Time Finished     Score"
			Dim lbl2 As String = name & "       " & att & "            " & time & "          " & score
			lview.AddTwoLines(header, lbl2)
		End If
	Next
End Sub

Sub Activity_Pause (UserClosed As Boolean)
Main.bg1.play

End Sub


Private Sub b1_Click
	Main.cl1.play
	StartActivity("Main")
	Activity.Finish
End Sub