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

	Private pn11 As Panel
	Private pn12 As Panel
	Private pn13 As Panel
	Private pn14 As Panel
	Private pn15 As Panel
	Private pn16 As Panel
	Private l2 As Label
	Private l2t1 As EditText
	Private l2t2 As EditText
	Private l2t3 As EditText
	Private l2t4 As EditText
	Private l2t5 As EditText
	Private l2t6 As EditText
	Private l3t1 As EditText
	Private l3t2 As EditText
	Private l3t3 As EditText
	Private l3t4 As EditText
	Private l3t5 As EditText
	Private l3t6 As EditText
	Private l7t1 As EditText
	Private l7t2 As EditText
	Private l7t3 As EditText
	Private l7t4 As EditText
	Private l7t5 As EditText
	Private l7t6 As EditText
	Private line7 As Panel
	Private line1 As Panel
	Private line2 As Panel
	Private line3 As Panel
	Private hint1 As Panel
	Private hint2 As Panel
	Private hint3 As Panel
	Private hint4 As Panel
	Private menu As Button
	Private pu1 As Button
	Private pu2 As Button
	Private pu3 As Button
	Private submit As Button
	Private surr As Button
	Private nex As Button
	Dim tut As Int = 0
	Private pntut As Panel
	Private bac As Button
	Private lbt1 As Label
	Private lbt10 As Label
	Private lbt11 As Label
	Private lbt12 As Label
	Private lbt13 As Label
	Private lbt14 As Label
	Private lbt15 As Label
	Private lbt16 As Label
	Private lbt17 As Label
	Private lbt18 As Label
	Private lbt19 As Label
	Private lbt2 As Label
	Private lbt20 As Label
	Private lbt21 As Label
	Private lbt22 As Label
	Private lbt23 As Label
	Private lbt24 As Label
	Private lbt3 As Label
	Private lbt4 As Label
	Private lbt5 As Label
	Private lbt6 As Label
	Private lbt7 As Label
	Private lbt8 As Label
	Private lbt9 As Label
	Private Panel1 As Panel
	Private pn21 As Panel
	Private pn22 As Panel
	Private pn23 As Panel
	Private pn24 As Panel
	Private pn25 As Panel
	Private pn26 As Panel
	Private pn31 As Panel
	Private pn32 As Panel
	Private pn33 As Panel
	Private pn34 As Panel
	Private pn35 As Panel
	Private pn36 As Panel
	Dim time As Timer
	Dim seconds As Int = 0 
End Sub

Sub time_Tick
	seconds = seconds + 1
	Log("Timer tick: " & seconds) ' Check if this prints every second
	updatelabel
End Sub

Sub updatelabel
	Dim mins As Int = Floor(seconds/60)
	Dim sec As Int = seconds Mod 60
	l2.Text = NumberFormat(mins, 2, 0) & ":" & NumberFormat(sec,2, 0)


End Sub

Sub Activity_Create(FirstTime As Boolean)
	Size
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("mg_main1")
	bac.Color = Colors.ARGB(0, 0, 0, 0)
	nex.Color = Colors.ARGB(0, 0, 0, 0)

	pn11.Color = Colors.Green
	pn12.Color = Colors.Red
	pn13.Color = Colors.Red
	pn14.Color = Colors.Green
	pn15.Color = Colors.Green
	pn16.Color = Colors.Blue
	If FirstTime Then
		ShowTutorialHighlight
		nex.Visible = True
		pntut.BringToFront
		lbt1.BringToFront
		submit.Enabled = False
		pu1.Enabled = False
		pu2.Enabled = False
		pu3.Enabled = False
		surr.Enabled = False
		menu.Enabled = False
	End If
	
'	pn11.Color = 0xFFD5D5D5
	time.Initialize("time", 1000) ' 1000 ms = 1 second
	time.Enabled = True
	Main.bg2.Play
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


Sub ShowTutorialHighlight()
	Dim Target As View
	' Show the overlay
	pntut.Visible = True
	pntut.BringToFront

	' Set dark semi-transparent background
	Dim darkBG As ColorDrawable
	darkBG.Initialize(Colors.ARGB(180, 0, 0, 0), 0)
	pntut.Background = darkBG

	' Remove previous highlights (if any)
'	pntut.RemoveAllViews

	Select tut
		Case 0:
			pntut.BringToFront
			lbt1.Visible = True
			lbt2.Visible = False
			lbt1.BringToFront
		Case 1:
			pntut.BringToFront
			lbt1.Visible = False
			lbt2.Visible = True
			lbt3.Visible = False
			line1.BringToFront
			lbt2.BringToFront
			pn11.BringToFront
			pn12.BringToFront
			pn13.BringToFront
			pn14.BringToFront
			pn15.BringToFront
			pn16.BringToFront

			
		Case 2:
			pntut.BringToFront
			lbt2.Visible = False
			lbt3.Visible = True
			lbt4.Visible = False
			lbt3.BringToFront
			
			pn11.BringToFront
			pn14.BringToFront
			pn15.BringToFront
			
			
		Case 3:
			pntut.BringToFront
			lbt3.Visible = False
			lbt4.Visible = True
			lbt5.Visible = False
			lbt4.BringToFront
			
			pn16.BringToFront
			
		Case 4:
			pntut.BringToFront
			lbt4.Visible = False
			lbt5.Visible = True
			lbt6.Visible = False
			lbt5.BringToFront
			
			pn12.BringToFront
			pn13.BringToFront
			
		Case 5:
			pntut.BringToFront
			lbt5.Visible = False
			lbt6.Visible = True
			lbt7.Visible = False
			lbt6.BringToFront
			
			hint1.BringToFront
			hint2.BringToFront
			hint3.BringToFront
			hint4.BringToFront
			
		Case 6:
			pntut.BringToFront
			lbt6.Visible = False
			lbt7.Visible = True
			lbt8.Visible = False
			lbt7.BringToFront
			
			hint1.BringToFront
			pu1.Enabled = False
			pu2.Enabled = False
			pu3.Enabled = False
			
		Case 7:
			pntut.BringToFront
			lbt7.Visible = False
			lbt8.Visible = True
			lbt9.Visible = False
			lbt8.BringToFront
			
			pu1.Enabled =True
			pu2.Enabled = True
			pu3.Enabled = True
			
		Case 8:
			pntut.BringToFront
			lbt8.Visible = False
			lbt9.Visible = True
			lbt10.Visible = False
			lbt9.BringToFront
			pu1.Enabled = True
			pu2.Enabled = False
			pu3.Enabled = False
			
		Case 9:
			pntut.BringToFront
			lbt9.Visible = False
			lbt10.Visible = True
			lbt11.Visible = False
			lbt10.BringToFront
			pu1.Enabled = False
			pu2.Enabled = True
			pu3.Enabled = False
			
		Case 10:
			pntut.BringToFront
			lbt10.Visible = False
			lbt11.Visible = True
			lbt12.Visible = False
			lbt11.BringToFront
			pu1.Enabled = False
			pu2.Enabled = False
			pu3.Enabled = True
			
		Case 11:
			pntut.BringToFront
			lbt11.Visible = False
			lbt12.Visible = True
			lbt13.Visible = False
			lbt12.BringToFront
			pu1.Enabled = True
			pu2.Enabled = True
			pu3.Enabled = True
			
		Case 12:
			pntut.BringToFront
			lbt12.Visible = False
			lbt13.Visible = True
			lbt14.Visible = False
			lbt13.BringToFront
			pu1.Enabled = False
			pu2.Enabled = True
			pu3.Enabled = True
			
		Case 13:
			pntut.BringToFront
			lbt13.Visible = False
			lbt14.Visible = True
			lbt15.Visible = False
			lbt14.BringToFront
			pu1.Enabled = True
			pu2.Enabled = True
			pu3.Enabled = True
			l2t3.Text = ""
			pn23.Color = 0xFFD5D5D5
		Case 14:
			pntut.BringToFront
			lbt14.Visible = False
			lbt15.Visible = True
			lbt16.Visible = False	
			lbt15.BringToFront
			
			pu1.Enabled = False
			pu2.Enabled = True
			pu3.Enabled = False
			
			line2.BringToFront
			pn22.BringToFront
			pn23.BringToFront
			l2t3.Text = "S"
			pn23.color = Colors.Blue
			l2t2.text = ""
			pn22.Color = 0xFFD5D5D5
			
		Case 15:
			pntut.BringToFront
			lbt15.Visible = False
			lbt16.Visible = True
			lbt17.Visible = False
			lbt16.BringToFront
			
			pu1.Enabled = False
			pu2.Enabled = False
			pu3.Enabled = True
			
			line2.BringToFront
			pn22.BringToFront
			pn23.BringToFront
			l2t2.Text = "A"
			pn22.Color = Colors.Green
			
		Case 16:
			pntut.BringToFront
			lbt16.Visible = False
			lbt17.Visible = True
			lbt18.Visible = False
			lbt17.BringToFront
			
			pn22.BringToFront
			pn23.BringToFront
			pu1.Enabled = False
			pu2.Enabled = True
			pu3.Enabled = True
			
						
			l2t1.Text = ""
			l2t4.Text = ""
			l2t5.Text = ""
			l2t6.Text = ""
			
		Case 17:
			pntut.BringToFront
			lbt17.Visible = False
			lbt18.Visible = True
			lbt19.Visible = False
			lbt18.BringToFront
			
			pu1.Enabled = False
			pu2.Enabled = False
			pu3.Enabled = False
			
			line2.BringToFront
			pn22.BringToFront
			pn23.BringToFront
			
			l2t1.Text = "G"
			l2t4.Text = "E"
			l2t5.Text = "R"
			l2t6.Text = "M"
			
			submit.Enabled = False
			
		Case 18:
			line7.BringToFront
			pntut.BringToFront
			lbt18.Visible = False
			lbt19.Visible = True
			lbt20.Visible = False
			lbt19.BringToFront
			
			submit.Enabled = True
			
			pn21.Color = 0xFFD5D5D5
			pn24.Color = 0xFFD5D5D5
			pn25.Color =0xFFD5D5D5
			pn26.Color = 0xFFD5D5D5
			
		Case 19:
			pntut.BringToFront
			lbt19.Visible = False
			lbt20.Visible = True
			lbt21.Visible = False
			lbt20.BringToFront
			
			Panel1.BringToFront
			line1.BringToFront
			line3.BringToFront
			line2.BringToFront
			pn11.BringToFront
			pn12.BringToFront
			pn13.BringToFront
			pn14.BringToFront
			pn15.BringToFront
			pn16.BringToFront
			pn22.BringToFront
			pn23.BringToFront
			
			pn21.Color = Colors.Green
			pn24.Color = Colors.Green
			pn25.Color = Colors.Green
			pn26.Color = Colors.Blue
			
			
			submit.Enabled = False
			surr.Enabled = False
			
			
		Case 20:
			line7.BringToFront
			pntut.BringToFront
			lbt20.Visible = False
			lbt21.Visible = True
			lbt22.Visible = False
			lbt21.BringToFront
			surr.Enabled = True
			
			
		Case 21:
			pntut.BringToFront
			lbt21.Visible = False
			lbt22.Visible = True
			lbt23.Visible = False
			lbt22.BringToFront
			
			surr.Enabled = True
			line7.BringToFront
			l7t1.Text = "G"
			l7t2.Text = "A"
			l7t3.Text = "M"
			l7t4.Text = "E"
			l7t5.Text = "R"
			l7t6.Text = "S"
			menu.Enabled = False
			
			
		Case 22:
			pntut.BringToFront
			lbt22.Visible = False
			lbt23.Visible = True
			lbt24.Visible = False
			lbt23.BringToFront
			nex.Visible = True
			menu.Visible = True
			menu.Enabled = True
			surr.Enabled = False
			
		Case 23:
			pntut.BringToFront
			lbt23.Visible = False
			lbt24.Visible = True
			lbt24.BringToFront
			menu.Enabled = False
		Case 24:
			File.WriteString(File.DirInternal, "tutorial.txt", "true")
			Main.bg2.Stop
			StartActivity("loading")
			Main.mods = "Main"
			Activity.Finish
	End Select
End Sub

Private Sub nex_Click
Main.cl1.play
	tut = tut + 1
	If tut > 0 Then
		bac.Visible = True
	Else
		bac.Visible = False
	End If
	ShowTutorialHighlight
End Sub

Private Sub bac_Click
	Main.cl1.play
	tut = tut - 1
	If tut <= 0 Then
		bac.Visible = False
		Else 
			bac.Visible = True
	End If
	ShowTutorialHighlight
End Sub