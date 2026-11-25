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
	'    Public ActionBarHomeClicked As Boolean
'	Private time As Timer
End Sub


Sub Globals
	Private Root As B4XView
	Private xui As XUI
	Private b1 As Button
	
	Dim pow2 As Int = 0
	Dim gameIsRunning As Boolean
	
	Dim usedWords As List
	Dim levelCounter As Int = 0
	Dim TargetWord As String
	Dim hintCount As Int = 0
	Dim maxHints As Int = 3
	Dim currentRow As Int = 1
	Dim pcAll(6) As Map
	Dim selectedWord As String
	Dim currentLine As Int = 1 ' Start from line 1
	Dim pu2_uses As Int = 0
	Dim pu1_uses As Int = 0 
	Dim PowerUpUsed2 As Boolean
	Dim PowerUpUsed3 As Boolean = False
	Dim panelColors(6, 6) As Int ' To keep track of each panel's color, 6x6 grid
	Dim EditTexts(7, 6) As EditText
	Dim Panels(7, 6) As Panel
	Dim elapsedTime As Int = 0

	Dim PowerUp1Used As Boolean = False
	Dim UsedHints As List
	Dim WordHints As Map
	Dim CorrectWord As String
	
	Dim isProgrammaticChange As Boolean = False
	Dim hintIndex As Int = 0
	Dim currentWord As String = ""
	Dim pu3Used As Boolean = False
	Dim pu2Positions As List

	Public ActionBarHomeClicked As Boolean
	Private time As Timer
	Private seconds As Int

	' EditTexts Line 1 to 6
	Private l1t1, l1t2, l1t3, l1t4, l1t5, l1t6 As EditText
	Private l2t1, l2t2, l2t3, l2t4, l2t5, l2t6 As EditText
	Private l3t1, l3t2, l3t3, l3t4, l3t5, l3t6 As EditText
	Private l4t1, l4t2, l4t3, l4t4, l4t5, l4t6 As EditText
	Private l5t1, l5t2, l5t3, l5t4, l5t5, l5t6 As EditText
	Private l6t1, l6t2, l6t3, l6t4, l6t5, l6t6 As EditText
	Private l7t1, l7t2, l7t3, l7t4, l7t5, l7t6 As EditText

	' Panels Line 1 to 6
	Private pn11, pn12, pn13, pn14, pn15, pn16 As Panel
	Private pn21, pn22, pn23, pn24, pn25, pn26 As Panel
	Private pn31, pn32, pn33, pn34, pn35, pn36 As Panel
	Private pn41, pn42, pn43, pn44, pn45, pn46 As Panel
	Private pn51, pn52, pn53, pn54, pn55, pn56 As Panel
	Private pn61, pn62, pn63, pn64, pn65, pn66 As Panel
	Private pn71, pn72, pn73, pn74, pn75, pn76 As Panel

	Private submit As Button
	Private deleteBtn As Button
	Private pu1, pu2, pu3 As Button
	Private cl, h1, h2, h3, h4, l1, l2, l3, l4, l5 As Label

	Private hintList As List
	Private pc As Map
	Private currentAttempt As Int = 0
	Private hc As Int = 3
	Private pu2Uses As Int = 2
	Private surr As Button

	Dim wordList As List
	Dim etAll() As Object
	Dim panelsAll() As Object
	Dim solvedWords As List
	Private nexl As Button
	Private ress As Label
	Private gover As Panel
	Private srd As Panel
	Private gsum As Panel
	Private gov As Panel
	Dim atmp As Int = 1
	Private to_main As Button
	Private cancel As Button
	Private menu_panel As Panel
	Private cover As Panel
End Sub
Sub time_Tick
	seconds = seconds + 1
	Log("Timer tick: " & seconds) ' Check if this prints every second
	updatelabel
	elapsedTime = elapsedTime + 1
End Sub
Sub updatelabel
	Dim mins As Int = Floor(seconds/60)
	Dim sec As Int = seconds Mod 60
	l2.Text = NumberFormat(mins, 2, 0) & ":" & NumberFormat(sec,2, 0)


End Sub
Sub Activity_Resume
	Main.bg2.play
	If gameIsRunning = False Then
		ResetGame
	End If
End Sub
Sub Activity_Pause (UserClosed As Boolean)
	Main.bg2.pause
End Sub
Sub ResetGame
	' Clear EditTexts
'	For row = 0 To 5
'		For col = 0 To 5
'			EditTexts(row)(col).Text = ""
'			EditTexts(row)(col).Enabled = (row = 0)
''			Panels(row)(col).Color = Colors.LightGray
'		Next
'	Next

	' Reset logic variables
	currentLine = 1
	pu1_uses = 0
	pu2_uses = 0
	pu3Used = False
	gameIsRunning = True

	' Get a new word
	currentWord = wordList.Get(Rnd(0, wordList.Size))

	' Start timer
	elapsedTime = 0
	time.Enabled = True
	InitializeGameArrays
End Sub
Sub InitializeGameArrays
	pu2_uses = 0
	pu1_uses = 0
	pow2 = 0
	pu3Used = False
	
	
	InitHints


	' Word list setup

	' Store all panels and fields
	panelsAll = Array As Object( _
    Array As Panel(pn11, pn12, pn13, pn14, pn15, pn16), _ 
    Array As Panel(pn21, pn22, pn23, pn24, pn25, pn26), _ 
    Array As Panel(pn31, pn32, pn33, pn34, pn35, pn36), _ 
    Array As Panel(pn41, pn42, pn43, pn44, pn45, pn46), _ 
    Array As Panel(pn51, pn52, pn53, pn54, pn55, pn56), _ 
	Array As Panel(pn61, pn62, pn63, pn64, pn65, pn66) _ 
)


	Dim row1(6) As EditText = Array As EditText(l1t1, l1t2, l1t3, l1t4, l1t5, l1t6)
	Dim row2(6) As EditText = Array As EditText(l2t1, l2t2, l2t3, l2t4, l2t5, l2t6)
	Dim row3(6) As EditText = Array As EditText(l3t1, l3t2, l3t3, l3t4, l3t5, l3t6)
	Dim row4(6) As EditText = Array As EditText(l4t1, l4t2, l4t3, l4t4, l4t5, l4t6)
	Dim row5(6) As EditText = Array As EditText(l5t1, l5t2, l5t3, l5t4, l5t5, l5t6)
	Dim row6(6) As EditText = Array As EditText(l6t1, l6t2, l6t3, l6t4, l6t5, l6t6)

'	etAll.Add(row1)
'	etAll.Add(row2)
'	etAll.Add(row3)
'	etAll.Add(row4)
'	etAll.Add(row5)
'	etAll.Add(row6)
'	
	
	etAll = Array As Object( _
    Array As EditText(l1t1, l1t2, l1t3, l1t4, l1t5, l1t6), _
    Array As EditText(l2t1, l2t2, l2t3, l2t4, l2t5, l2t6), _
    Array As EditText(l3t1, l3t2, l3t3, l3t4, l3t5, l3t6), _
    Array As EditText(l4t1, l4t2, l4t3, l4t4, l4t5, l4t6), _
    Array As EditText(l5t1, l5t2, l5t3, l5t4, l5t5, l5t6), _
    Array As EditText(l6t1, l6t2, l6t3, l6t4, l6t5, l6t6), _
	Array As EditText(l7t1, l7t2, l7t3, l7t4, l7t5, l7t6) _
)

	panelsAll = Array As Object( _
    Array As Panel(pn11, pn12, pn13, pn14, pn15, pn16), _
    Array As Panel(pn21, pn22, pn23, pn24, pn25, pn26), _
    Array As Panel(pn31, pn32, pn33, pn34, pn35, pn36), _
    Array As Panel(pn41, pn42, pn43, pn44, pn45, pn46), _
    Array As Panel(pn51, pn52, pn53, pn54, pn55, pn56), _
    Array As Panel(pn61, pn62, pn63, pn64, pn65, pn66), _
	Array As Panel(pn71, pn72, pn73, pn74, pn75, pn76) _
)



	TargetWord = currentWord ' or use a randomly selected word from your word list

	currentAttempt = 0
'	EnableOnlyCurrentLine(currentAttempt)
	hintCount = 0
	h1.Text = "Hint 1: "
	h2.Text = "Hint 2: "
	h3.Text = "Hint 3: "
	h4.Text = "Hint 4: "
	UsedHints.Initialize


		UsedHints.Initialize
		PowerUp1Used = False

	ShowPrimaryHint(currentWord)
	
	
		' Initialize arrays
'		EditTexts.Initialize
'		Panels.Initialize

		' Assign EditTexts
		EditTexts(0,0) = l1t1
		EditTexts(0,1) = l1t2
		EditTexts(0,2) = l1t3
		EditTexts(0,3) = l1t4
		EditTexts(0,4) = l1t5
		EditTexts(0,5) = l1t6

		EditTexts(1,0) = l2t1
		EditTexts(1,1) = l2t2
		EditTexts(1,2) = l2t3
		EditTexts(1,3) = l2t4
		EditTexts(1,4) = l2t5
		EditTexts(1,5) = l2t6

		EditTexts(2,0) = l3t1
		EditTexts(2,1) = l3t2
		EditTexts(2,2) = l3t3
		EditTexts(2,3) = l3t4
		EditTexts(2,4) = l3t5
		EditTexts(2,5) = l3t6

		EditTexts(3,0) = l4t1
		EditTexts(3,1) = l4t2
		EditTexts(3,2) = l4t3
		EditTexts(3,3) = l4t4
		EditTexts(3,4) = l4t5
		EditTexts(3,5) = l4t6

		EditTexts(4,0) = l5t1
		EditTexts(4,1) = l5t2
		EditTexts(4,2) = l5t3
		EditTexts(4,3) = l5t4
		EditTexts(4,4) = l5t5
		EditTexts(4,5) = l5t6

		EditTexts(5,0) = l6t1
		EditTexts(5,1) = l6t2
		EditTexts(5,2) = l6t3
		EditTexts(5,3) = l6t4
		EditTexts(5,4) = l6t5
		EditTexts(5,5) = l6t6

		EditTexts(6,0) = l7t1
		EditTexts(6,1) = l7t2
		EditTexts(6,2) = l7t3
		EditTexts(6,3) = l7t4
		EditTexts(6,4) = l7t5
		EditTexts(6,5) = l7t6

		' Assign Panels
		Panels(0,0) = pn11
		Panels(0,1) = pn12
		Panels(0,2) = pn13
		Panels(0,3) = pn14
		Panels(0,4) = pn15
		Panels(0,5) = pn16

		Panels(1,0) = pn21
		Panels(1,1) = pn22
		Panels(1,2) = pn23
		Panels(1,3) = pn24
		Panels(1,4) = pn25
		Panels(1,5) = pn26

		Panels(2,0) = pn31
		Panels(2,1) = pn32
		Panels(2,2) = pn33
		Panels(2,3) = pn34
		Panels(2,4) = pn35
		Panels(2,5) = pn36

		Panels(3,0) = pn41
		Panels(3,1) = pn42
		Panels(3,2) = pn43
		Panels(3,3) = pn44
		Panels(3,4) = pn45
		Panels(3,5) = pn46

		Panels(4,0) = pn51
		Panels(4,1) = pn52
		Panels(4,2) = pn53
		Panels(4,3) = pn54
		Panels(4,4) = pn55
		Panels(4,5) = pn56

		Panels(5,0) = pn61
		Panels(5,1) = pn62
		Panels(5,2) = pn63
		Panels(5,3) = pn64
		Panels(5,4) = pn65
		Panels(5,5) = pn66
		
		Panels(6,0) = pn71
		Panels(6,1) = pn72
		Panels(6,2) = pn73
		Panels(6,3) = pn74
		Panels(6,4) = pn75
		Panels(6,5) = pn76

	For i = 1 To etAll.Length - 1
		Dim etRow() As EditText = etAll(i)
		For Each et As EditText In etRow
			et.Enabled = False
		Next
	Next



End Sub
Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("mg_main")
	Main.bg2.Stop
		Main.bg2.IsInitialized 
		Main.bg2.initialize
		Main.bg2.Load(File.DirAssets, "nbgm2.mp3")
		Main.bg2.SetVolume(Main.vl1, Main.vl1)
		Main.bg2.Looping = True
	cover.Color =  Colors.ARGB(0, 0, 0, 0)
	cover.Visible = False

	time.Initialize("time", 1000) 
	time.Enabled = True ' Start the timer
	seconds = 0
	updatelabel
	
	 pu2_uses = 0
	 pu1_uses = 0
	 pow2 = 0
	 pu3Used = False
	
	If FirstTime Then
		InitializeGameArrays
	End If
		
		' Wordlist
	wordList.Initialize
	wordList.AddAll(Array As String( _
"ACCESS", "ANALOG", "AVATAR", "BACKUP", "BANNER", "BINARY", "BITMAP", "BITNET", "BOOTUP", "BRANCH", _
"BRIDGE", "BROWSE", "CLIENT", "CURSOR", "DECODE", "DEFRAG", "DELETE", "DIALUP", "DOMAIN", "DONGLE", _
"DRIVER", "DUPLEX", "EDITOR", "ESCAPE", "EXPORT", "FILTER", "FLOPPY", "FOLDER", "FORMAT", "GOOGLE", _
"GOPHER", "HACKER", "HEADER", "IMPORT", "INKJET", "ITUNES", "JOVIAL", "KEYPAD", "LAPTOP", "LOGOFF", _
"LOGOUT", "MEMORY", "NEWBIE", "NIBBLE", "OFFICE", "ONLINE", "OUTPUT", "PACKET", "PARITY", "PARSER", _
"PASCAL", "PLUGIN", "PORTAL", "PROLOG", "PROMPT", "RASTER", "RECORD", "REMOTE", "ROUTER", "SCROLL", _
"SEARCH", "SECTOR", "SERVER", "SMILEY", "SNOBOL", "SPIDER", "SPRITE", "STATUS", "SYSTEM", "TABLET", _
"TELNET", "TROJAN", "UPDATE", "UPLOAD", "UPTIME", "USENET", "VECTOR", "WEBLOG", "WINDOW", "WIZARD"))
	currentWord = wordList.Get(Rnd(0, wordList.Size))
	currentWord = currentWord.ToUpperCase

	Main.bg1.Stop

	Main.bg2.play
	
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

Private Sub submit_Click
	Main.cl1.play
	' Ensure pc is initialized
	Dim pc As Map
	pc.Initialize

	' Ensure the attempt is within bounds
	If currentAttempt >= etAll.Length Then
		Msgbox("No more attempts left!", "Game Over")
		Return
	End If

	' Ensure all fields in current row are filled
	Dim etFields() As EditText = etAll(currentAttempt)
	For Each et As EditText In etFields
		If et.Text.Trim = "" Then
			MsgboxAsync("Fill all the blanks first!", "Note")
			Return
		End If
	Next

	' Create match string from user input
	Dim match As String = ""
	For Each et As EditText In etFields
		match = match & et.Text.Trim
	Next

	match = match.ToUpperCase

	' Validate match length
	If match.Length <> 6 Then
		Msgbox("Fill all 6 letters!", "Note")
		Return
	End If

	' Initialize boolean arrays for checking letters
	Dim used(6) As Boolean
	Dim matchLetters(6) As Boolean

	' First pass: correct letters in correct positions (Green)
	For i = 0 To 5
		If match.CharAt(i) = TargetWord.CharAt(i) Then
			pc.Put(i, Colors.Green)
			used(i) = True
			matchLetters(i) = True
		Else
			pc.Put(i, Colors.Red)
		End If
	Next

	' Second pass: correct letters in wrong positions (Blue)
	For i = 0 To 5
		If pc.Get(i) = Colors.Red Then
			For j = 0 To 5
				If match.CharAt(i) = TargetWord.CharAt(j) And Not(used(j)) And Not(matchLetters(j)) Then
					pc.Put(i, Colors.Blue)
					used(j) = True
					matchLetters(j) = True
					Exit
				End If
			Next
		End If
	Next

	' Apply colors to panels and disable current row's EditTexts
	Dim pl() As Panel = panelsAll(currentAttempt)
	For i = 0 To 5
		pl(i).Color = pc.Get(i)
		etFields(i).Enabled = False
	Next

	' Check win condition
	If match = TargetWord Then
		GameOver(True)
	Else
		' Move to next row
		currentAttempt = currentAttempt + 1
		
		' Enable next row's EditTexts if within bounds
		If currentAttempt < etAll.Length Then
			Dim nextRow() As EditText = etAll(currentAttempt)
			For Each et As EditText In nextRow
				et.Enabled = True
			Next
			' Optional: set focus to first EditText
			nextRow(0).RequestFocus
		Else
			GameOver(False) ' No more attempts left
		End If
		atmp = atmp + 1
	End If
	
	If currentAttempt = 6 Then
		GameOver(False)
		
	End If
End Sub


Sub GameOver(success As Boolean)
	time.Enabled = False
	Dim mins As Int = Floor(seconds/60)
	Dim sec As Int = seconds Mod 60
	Main.tim = NumberFormat(mins, 2, 0) & ":" & NumberFormat(sec,2, 0)

	AnimateGovToMiddle	' Game Over Panel
	
	Sleep(3000)
	gov.Visible = False
	nexl.Enabled = True
	nexl.Visible = True
	submit.visible = False
	submit.Enabled = False
	pu1.Enabled = False
	pu2.Enabled = False
	pu3.Enabled = False
	surr.Enabled = False
	
	Dim CorrectWord As String = currentWord.ToUpperCase

	' Fill in EditTexts of line 7 (l7t1 to l7t6)
	For i = 0 To 5
		EditTexts(6, i).Text = currentWord.SubString2(i, i + 1)
	Next
	' Disable all EditTexts
	For Each etRow() As EditText In etAll
		For Each et As EditText In etRow
			et.Enabled = False
		Next
	Next

	' Stop the timer
	time.Enabled = False

	' === Scoring ===
	Dim score As Int = 100
Dim atmps As Int = 0

Select atmp
	Case 1:
			atmps = atmps + 15
	Case 2:
			atmps = atmps + 10
	Case 3:
			atmps = atmps + 7
	Case 4: 
			atmps = atmps + 5
	Case 5:
			atmps = atmps + 3
	Case 6:
		  	atmps = atmps + 1
End Select

	' Time penalty: 5 points per 20 seconds
	Dim timePenalty As Int = (elapsedTime / 20) * 5
	score = score - timePenalty

	' Power-Up penalties
	score = score - (pu1_uses * 2)
	score = score - (pow2 * 5)
	score = score + atmps
	If pu3Used Then
		score = score - 15
	End If
	If score > 100 Then
		score = 100
	End If

	If score < 0 Then score = 0 

	
	Dim result As String
	If success = True Then
		Main.cw = Main.cw + 1
		gsum.BringToFront
		gsum.Visible = True
		result = "Congratulations! You guessed the word!" & CRLF
	Else
		srd.BringToFront
		srd.Visible = True
		score = 0
		result = "Game Over. Better luck next time!" & CRLF & CRLF
	End If



	result = result & "Number of Attempts: " & atmp & " Attempt(s)" & "   +" & atmps & CRLF & CRLF
	result = result & "Time taken: " & elapsedTime & " seconds" & CRLF & CRLF
	result = result & "Hints Used: " & pu1_uses & " times   -" & (pu1_uses * 2) & "" & CRLF
	result = result & "Power Up 2 used: " & pu2_uses & " times   -" & (pu2_uses * 5) & "" & CRLF

	Dim pu3Text As String
	If pu3Used Then
		pu3Text = "Yes (-15)"
	Else
		pu3Text = "No"
	End If
	result = result & "Power Up 3 used:     " & pu3Text & CRLF & CRLF

	result = result & "Final Score:   " & score
	Main.sc = score
	Main.atm = atmp
	gover.BringToFront
	gover.SetVisibleAnimated(1800, True)
	gover.Visible = True
	ress.Text = result

	'Msgbox(result, "Game Summary")

	' btnRestart.Visible = True
	If success Then
		Dim entry As String = Main.usr & "," & Main.atm & "," & Main.tim & "," & Main.sc
		Main.datlist.Add(entry)
		File.WriteList(File.DirInternal, "history.txt", Main.datlist)
		File.WriteString(File.DirInternal, "cw.txt", Main.cw)
	End If

End Sub
Sub l1t1_TextChanged (Old As String, New As String)
	HandleTextChanged(l1t1, Old, New)
End Sub
Sub l1t2_TextChanged (Old As String, New As String)
	HandleTextChanged(l1t2, Old, New)
End Sub
Sub l1t3_TextChanged (Old As String, New As String)
	HandleTextChanged(l1t3, Old, New)
End Sub
Sub l1t4_TextChanged (Old As String, New As String)
	HandleTextChanged(l1t4, Old, New)
End Sub
Sub l1t5_TextChanged (Old As String, New As String)
	HandleTextChanged(l1t5, Old, New)
End Sub
Sub l1t6_TextChanged (Old As String, New As String)
	HandleTextChanged(l1t6, Old, New)
End Sub
Sub l2t1_TextChanged (Old As String, New As String)
	HandleTextChanged(l2t1, Old, New)
End Sub
Sub l2t2_TextChanged (Old As String, New As String)
	HandleTextChanged(l2t2, Old, New)
End Sub
Sub l2t3_TextChanged (Old As String, New As String)
	HandleTextChanged(l2t3, Old, New)
End Sub
Sub l2t4_TextChanged (Old As String, New As String)
	HandleTextChanged(l2t4, Old, New)
End Sub
Sub l2t5_TextChanged (Old As String, New As String)
	HandleTextChanged(l2t5, Old, New)
End Sub
Sub l2t6_TextChanged (Old As String, New As String)
	HandleTextChanged(l2t6, Old, New)
End Sub
Sub l3t1_TextChanged (Old As String, New As String)
	HandleTextChanged(l3t1, Old, New)
End Sub
Sub l3t2_TextChanged (Old As String, New As String)
	HandleTextChanged(l3t2, Old, New)
End Sub
Sub l3t3_TextChanged (Old As String, New As String)
	HandleTextChanged(l3t3, Old, New)
End Sub
Sub l3t4_TextChanged (Old As String, New As String)
	HandleTextChanged(l3t4, Old, New)
End Sub
Sub l3t5_TextChanged (Old As String, New As String)
	HandleTextChanged(l3t5, Old, New)
End Sub
Sub l3t6_TextChanged (Old As String, New As String)
	HandleTextChanged(l3t6, Old, New)
End Sub
Sub l4t1_TextChanged (Old As String, New As String)
	HandleTextChanged(l4t1, Old, New)
End Sub
Sub l4t2_TextChanged (Old As String, New As String)
	HandleTextChanged(l4t2, Old, New)
End Sub
Sub l4t3_TextChanged (Old As String, New As String)
	HandleTextChanged(l4t3, Old, New)
End Sub
Sub l4t4_TextChanged (Old As String, New As String)
	HandleTextChanged(l4t4, Old, New)
End Sub
Sub l4t5_TextChanged (Old As String, New As String)
	HandleTextChanged(l4t5, Old, New)
End Sub
Sub l4t6_TextChanged (Old As String, New As String)
	HandleTextChanged(l4t6, Old, New)
End Sub
Sub l5t1_TextChanged (Old As String, New As String)
	HandleTextChanged(l5t1, Old, New)
End Sub
Sub l5t2_TextChanged (Old As String, New As String)
	HandleTextChanged(l5t2, Old, New)
End Sub
Sub l5t3_TextChanged (Old As String, New As String)
	HandleTextChanged(l5t3, Old, New)
End Sub
Sub l5t4_TextChanged (Old As String, New As String)
	HandleTextChanged(l5t4, Old, New)
End Sub
Sub l5t5_TextChanged (Old As String, New As String)
	HandleTextChanged(l5t5, Old, New)
End Sub
Sub l5t6_TextChanged (Old As String, New As String)
	HandleTextChanged(l5t6, Old, New)
End Sub
Sub l6t1_TextChanged (Old As String, New As String)
	HandleTextChanged(l6t1, Old, New)
End Sub
Sub l6t2_TextChanged (Old As String, New As String)
	HandleTextChanged(l6t2, Old, New)
End Sub
Sub l6t3_TextChanged (Old As String, New As String)
	HandleTextChanged(l6t3, Old, New)
End Sub
Sub l6t4_TextChanged (Old As String, New As String)
	HandleTextChanged(l6t4, Old, New)
End Sub
Sub l6t5_TextChanged (Old As String, New As String)
	HandleTextChanged(l6t5, Old, New)
End Sub
Sub l6t6_TextChanged (Old As String, New As String)
	HandleTextChanged(l6t6, Old, New)
End Sub
Sub HandleTextChanged(edtSender As EditText, Old As String, New As String)
	If isProgrammaticChange Then Return

	Dim nextEt As EditText = Null
	Dim prevEt As EditText = Null

	' Auto-capitalize and limit to one character
	isProgrammaticChange = True
	If New.Length > 1 Then
		edtSender.Text = New.SubString2(0, 1).ToUpperCase
	Else
		edtSender.Text = New.ToUpperCase
	End If
	isProgrammaticChange = False

	' Set cursor at the end
	If edtSender.Text.Length > 0 Then
		edtSender.SelectionStart = edtSender.Text.Length
	End If

	' Move focus to the next EditText
	Select edtSender
		Case l1t1: nextEt = l1t2
		Case l1t2: nextEt = l1t3
		Case l1t3: nextEt = l1t4
		Case l1t4: nextEt = l1t5
		Case l1t5: nextEt = l1t6
		Case l1t6: nextEt = l1t6 ' Last in row
			
		Case l2t1: nextEt = l2t2
		Case l2t2: nextEt = l2t3
		Case l2t3: nextEt = l2t4
		Case l2t4: nextEt = l2t5
		Case l2t5: nextEt = l2t6
		Case l2t6: nextEt = l2t6
			
		Case l3t1: nextEt = l3t2
		Case l3t2: nextEt = l3t3
		Case l3t3: nextEt = l3t4
		Case l3t4: nextEt = l3t5
		Case l3t5: nextEt = l3t6
		Case l3t6: nextEt = l3t6
			
		Case l4t1: nextEt = l4t2
		Case l4t2: nextEt = l4t3
		Case l4t3: nextEt = l4t4
		Case l4t4: nextEt = l4t5
		Case l4t5: nextEt = l4t6
		Case l4t6: nextEt = l4t6
			
		Case l5t1: nextEt = l5t2
		Case l5t2: nextEt = l5t3
		Case l5t3: nextEt = l5t4
		Case l5t4: nextEt = l5t5
		Case l5t5: nextEt = l5t6
		Case l5t6: nextEt = l5t6
			
		Case l6t1: nextEt = l6t2
		Case l6t2: nextEt = l6t3
		Case l6t3: nextEt = l6t4
		Case l6t4: nextEt = l6t5
		Case l6t5: nextEt = l6t6
		Case l6t6: nextEt = l6t6
	End Select
	
	' Move focus forward after typing a character
	If New.Length > Old.Length And nextEt <> Null And nextEt <> edtSender Then
		nextEt.RequestFocus
	End If
	
	' Move focus back on backspace
	If New.Length < Old.Length Then
		Select edtSender
			Case l1t1: prevEt = l1t1
			Case l1t2: prevEt = l1t1
			Case l1t3: prevEt = l1t2
			Case l1t4: prevEt = l1t3
			Case l1t5: prevEt = l1t4
			Case l1t6: prevEt = l1t5
				
			Case l2t1: prevEt = l2t1
			Case l2t2: prevEt = l2t1
			Case l2t3: prevEt = l2t2
			Case l2t4: prevEt = l2t3
			Case l2t5: prevEt = l2t4
			Case l2t6: prevEt = l2t5
				
			Case l3t1: prevEt = l3t1
			Case l3t2: prevEt = l3t1
			Case l3t3: prevEt = l3t2
			Case l3t4: prevEt = l3t3
			Case l3t5: prevEt = l3t4
			Case l3t6: prevEt = l3t5
				
			Case l4t1: prevEt = l4t1
			Case l4t2: prevEt = l4t1
			Case l4t3: prevEt = l4t2
			Case l4t4: prevEt = l4t3
			Case l4t5: prevEt = l4t4
			Case l4t6: prevEt = l4t5
				
			Case l5t1: prevEt = l5t1
			Case l5t2: prevEt = l5t1
			Case l5t3: prevEt = l5t2
			Case l5t4: prevEt = l5t3
			Case l5t5: prevEt = l5t4
			Case l5t6: prevEt = l5t5
				
			Case l6t1: prevEt = l6t1
			Case l6t2: prevEt = l6t1
			Case l6t3: prevEt = l6t2
			Case l6t4: prevEt = l6t3
			Case l6t5: prevEt = l6t4
			Case l6t6: prevEt = l6t5
		End Select
		
		If prevEt <> Null Then
			prevEt.RequestFocus
			prevEt.SelectionStart = prevEt.Text.Length
		End If
	End If
	
	' Ensure cursor is at the end for the last fields
	If edtSender = l1t6 Or edtSender = l2t6 Or edtSender = l3t6 Or edtSender = l4t6 Or edtSender = l5t6 Or edtSender = l6t6 Then
		edtSender.SelectionStart = edtSender.Text.Length
	End If
End Sub
Private Sub pu3_Click
	Main.cl1.play
	If pu3Used Then
		Msgbox("Power-up already used.", "Empty")
		Return
	End If

	Dim etFields() As EditText = etAll(currentAttempt)
	Dim revealedLetters As Map
	revealedLetters.Initialize

	' Collect already correctly guessed letters from all previous attempts
	For row = 0 To currentAttempt - 1
		Dim previousRow() As EditText = etAll(row)
		For i = 0 To 5
			If previousRow(i).Text.ToUpperCase = currentWord.CharAt(i) Then
				revealedLetters.Put(i, True)
			End If
		Next
	Next

	' Find unrevealed correct letters
	Dim unrevealedPositions As List
	unrevealedPositions.Initialize
	For i = 0 To 5
		If Not(revealedLetters.ContainsKey(i)) And etFields(i).Enabled Then
			unrevealedPositions.Add(i)
		End If
	Next

	If unrevealedPositions.Size = 0 Then
		Msgbox("All correct letters already revealed.", "Info")
		Return
	End If

	' Pick a random unrevealed correct letter position
	Dim randIndex As Int = Rnd(0, unrevealedPositions.Size)
	Dim pos As Int = unrevealedPositions.Get(randIndex)
	Dim correctLetter As String = currentWord.CharAt(pos)

	etFields(pos).Text = correctLetter
	etFields(pos).Enabled = False
	etFields(pos).Color = Colors.Green

	pu3Used = True
	pu3.Enabled = False
	pu3.Text = "Used"
End Sub
Sub pu2_click
	Main.cl1.play
	If pu2_uses >= 2 Then
		ToastMessageShow("Power-Up 2 can only be used twice!", True)
		Return
	End If

	Dim currentRow As Int = currentLine - 1
	If currentRow < 0 Then
		ToastMessageShow("Use Power-Up after the first guess!", True)
		Return
	End If

	Dim CorrectWord As String = currentWord.ToUpperCase

	' Gather letters already revealed in current row
	Dim revealedLetters As List
	revealedLetters.Initialize
	For i = 0 To 5
		Dim txt As String = EditTexts(currentRow, i).Text.ToUpperCase
		If txt.Length > 0 And revealedLetters.IndexOf(txt) = -1 Then
			revealedLetters.Add(txt)
		End If
	Next

	' Find unrevealed correct letters
	Dim unrevealedLetters As List
	unrevealedLetters.Initialize
	For i = 0 To 5
		Dim correctLetter As String = CorrectWord.SubString2(i, i + 1)
		If revealedLetters.IndexOf(correctLetter) = -1 And unrevealedLetters.IndexOf(correctLetter) = -1 Then
			unrevealedLetters.Add(correctLetter)
		End If
	Next

	If unrevealedLetters.Size = 0 Then
		ToastMessageShow("No unrevealed correct letters available!", True)
		Return
	End If

	' Try each unrevealed letter to find a valid spot
	For tryIndex = 0 To unrevealedLetters.Size - 1
		Dim letterToReveal As String = unrevealedLetters.Get(tryIndex)

		' 1. Try wrong positions first
		Dim validWrongPos As List
		validWrongPos.Initialize
		For i = 0 To 5
			Dim correctLetter As String = CorrectWord.SubString2(i, i + 1)
			Dim userLetter As String = EditTexts(currentRow, i).Text.ToUpperCase
			If correctLetter <> letterToReveal And userLetter = "" Then
				validWrongPos.Add(i)
			End If
		Next

		If validWrongPos.Size > 0 Then
			Dim posToReveal As Int = validWrongPos.Get(Rnd(0, validWrongPos.Size))
			EditTexts(currentRow, posToReveal).Text = letterToReveal
			EditTexts(currentRow, posToReveal).Enabled = False
			Panels(currentRow, posToReveal).Color = Colors.Blue
			pu2_uses = pu2_uses + 1
			ToastMessageShow("Power-Up 2 used (" & pu2_uses & "/2)", False)
			Return
		End If

		' 2. Try correct position if empty
		For i = 0 To 5
			Dim correctLetter As String = CorrectWord.SubString2(i, i + 1)
			Dim userLetter As String = EditTexts(currentRow, i).Text.ToUpperCase
			If correctLetter = letterToReveal And userLetter = "" Then
				EditTexts(currentRow, i).Text = letterToReveal
				EditTexts(currentRow, i).Enabled = False
				Panels(currentRow, i).Color = Colors.Green
				pow2 = pow2 + 1
				ToastMessageShow("Power-Up 2 used (" & pu2_uses & "/2)", False)
				Return
			End If
		Next
	Next

	ToastMessageShow("No available position to reveal any letter!", True)
End Sub
Sub GetEditTextArray(obj As Object) As EditText()
	Return obj
End Sub
Sub GetCurrentLineEditTexts(lineNumber As Int) As EditText()
	Dim ets(6) As EditText ' for 6 letters, index 0 to 5
	Select lineNumber
		Case 1
			ets(0) = l1t1
			ets(1) = l1t2
			ets(2) = l1t3
			ets(3) = l1t4
			ets(4) = l1t5
			ets(5) = l1t6
		Case 2
			ets(0) = l2t1
			ets(1) = l2t2
			ets(2) = l2t3
			ets(3) = l2t4
			ets(4) = l2t5
			ets(5) = l2t6
			' Repeat for other lines...
	End Select
	Return ets
End Sub
Sub pu1_Click
	
	Main.cl1.play
	If WordHints.ContainsKey(currentWord) Then
		Dim hints() As String = WordHints.Get(currentWord)
		Dim foundHint As Boolean = False
		Dim hintText As String = ""
        
		For i = hintIndex To hints.Length - 1
			Dim h As String = hints(i)
			If UsedHints.IndexOf(h) = -1 Then
				hintText = h
				UsedHints.Add(h)
				hintIndex = i + 1 ' move forward
				foundHint = True
				Exit
			End If
		Next
        
		If foundHint Then
			pu1_uses = pu1_uses + 1
			Select UsedHints.Size ' Because primary hint is in UsedHints(0), next hints will be 2,3,4
				Case 2
					h2.Text = hintText
				Case 3
					h3.Text = hintText
				Case 4
					h4.Text = hintText
				Case Else
					ToastMessageShow("No more hints available.", False)
			End Select
		Else
			ToastMessageShow("No more hints available.", False)
		End If
	Else
		ToastMessageShow("Hints not available for this word.", False)
	End If
End Sub
Sub ShowPrimaryHint(word As String)
	If WordHints.ContainsKey(word) Then
		Dim hints() As String = WordHints.Get(word)
		If hints.Length > 0 Then
			h1.Text = hints(0)  ' Always show first hint as primary
			UsedHints.Clear
			UsedHints.Add(hints(0)) ' Mark primary hint as used
			hintIndex = 1 ' Start from second hint for power-up
			h2.Text = ""
			h3.Text = ""
			h4.Text = ""
		Else
			h1.Text = ""
		End If
	Else
		h1.Text = ""
	End If
End Sub
Sub surr_click
	Main.ts = Main.ts + 1
	File.WriteString(File.DirInternal, "ts.txt", Main.ts)
	Main.cl1.play
	' Show a toast and trigger the game over sequence
	'ToastMessageShow("You surrendered! The word was: " & CorrectWord, True)
	GameOver(False)
	
	submit.Enabled = False
	pu1.Enabled = False
	pu2.Enabled = False
	pu3.Enabled = False
End Sub
Private Sub menu_Click
	Main.cl1.play
	
	If menu_panel.Visible = False Then
		menu_panel.Visible = True
		menu_panel.BringToFront
		cover.Visible = True
		nexl.Enabled = False
		submit.Enabled = False
		pu1.Enabled = False
		pu2.Enabled = False
		pu3.Enabled = False
		surr.Enabled = False
		
		
	Else
		If time.Enabled Then
			submit.Enabled = True
			pu1.Enabled = True
			pu2.Enabled = True
			pu3.Enabled = True
			surr.Enabled = True
		Else
			nexl.Enabled = True
		End If
		
		menu_panel.Visible = False
		cover.Visible = False
	End If
	
End Sub
Sub Activity_KeyPress (KeyCode As Int) As Boolean
	If KeyCode = KeyCodes.KEYCODE_BACK Then
		menu_Click
	End If
End Sub
Sub InitHints
	WordHints.Initialize
	WordHints.Put("ACCESS", Array As String("Hint 1: Login permission", "Hint 2: Gained via credentials", "Hint 3: Often restricted", "Hint 4: Network related"))
	WordHints.Put("ANALOG", Array As String("Hint 1: Not digital", "Hint 2: Continuous signals", "Hint 3: Older tech", "Hint 4: Used in radios"))
	WordHints.Put("AVATAR", Array As String("Hint 1: Online persona", "Hint 2: Used in games", "Hint 3: Virtual character", "Hint 4: Profile image"))
	WordHints.Put("BACKUP", Array As String("Hint 1: Data copy", "Hint 2: Recovery source", "Hint 3: Cloud or disk", "Hint 4: Essential for safety"))
	WordHints.Put("BANNER", Array As String("Hint 1: Web ad", "Hint 2: Found on websites", "Hint 3: Horizontal layout", "Hint 4: Marketing tool"))
	WordHints.Put("BINARY", Array As String("Hint 1: 0s and 1s", "Hint 2: Machine language", "Hint 3: Two states", "Hint 4: Base-2 system"))
	WordHints.Put("BITMAP", Array As String("Hint 1: Image format", "Hint 2: Pixel-based", "Hint 3: .BMP extension", "Hint 4: Graphics file"))
	WordHints.Put("BITNET", Array As String("Hint 1: Old network", "Hint 2: Pre-Internet", "Hint 3: Academic use", "Hint 4: 1980s system"))
	WordHints.Put("BOOTUP", Array As String("Hint 1: Start process", "Hint 2: OS loading", "Hint 3: Happens on power on", "Hint 4: BIOS involved"))
	WordHints.Put("BRANCH", Array As String("Hint 1: Git term", "Hint 2: Diverges code", "Hint 3: Version control", "Hint 4: Often merged"))
	WordHints.Put("BRIDGE", Array As String("Hint 1: Network device", "Hint 2: Connects segments", "Hint 3: Data traffic", "Hint 4: Reduces collisions"))
	WordHints.Put("BROWSE", Array As String("Hint 1: Internet action", "Hint 2: Using a browser", "Hint 3: Web surfing", "Hint 4: View content"))
	WordHints.Put("CLIENT", Array As String("Hint 1: Connected to server", "Hint 2: Requests services", "Hint 3: Part of network", "Hint 4: Software or hardware"))
	WordHints.Put("CURSOR", Array As String("Hint 1: Blinking line", "Hint 2: Shows position", "Hint 3: Text input", "Hint 4: Mouse pointer"))
	WordHints.Put("DECODE", Array As String("Hint 1: Convert data", "Hint 2: Reverse of encode", "Hint 3: Read format", "Hint 4: Decryption involved"))
	WordHints.Put("DEFRAG", Array As String("Hint 1: Disk utility", "Hint 2: Rearranges files", "Hint 3: Speeds access", "Hint 4: Windows tool"))
	WordHints.Put("DELETE", Array As String("Hint 1: Remove data", "Hint 2: Erase file", "Hint 3: Trash bin", "Hint 4: Cannot undo sometimes"))
	WordHints.Put("DIALUP", Array As String("Hint 1: Old internet", "Hint 2: Phone line", "Hint 3: Slow connection", "Hint 4: Modem sound"))
	WordHints.Put("DOMAIN", Array As String("Hint 1: Website address", "Hint 2: Internet identity", "Hint 3: Ends in .com", "Hint 4: Owned online"))
	WordHints.Put("DONGLE", Array As String("Hint 1: Small device", "Hint 2: USB tool", "Hint 3: Adds function", "Hint 4: License key"))
	WordHints.Put("DRIVER", Array As String("Hint 1: Software", "Hint 2: Hardware interface", "Hint 3: Allows device operation", "Hint 4: Often installed manually"))
	WordHints.Put("DUPLEX", Array As String("Hint 1: Communication type", "Hint 2: Both directions", "Hint 3: Two-way communication", "Hint 4: Full-duplex network"))
	WordHints.Put("EDITOR", Array As String("Hint 1: Text manipulation", "Hint 2: Source code editor", "Hint 3: Often used in programming", "Hint 4: Notepad++ or Sublime Text"))
	WordHints.Put("ESCAPE", Array As String("Hint 1: Key on keyboard", "Hint 2: Exit command", "Hint 3: Leave mode", "Hint 4: Often used for closing"))
	WordHints.Put("EXPORT", Array As String("Hint 1: Data transfer", "Hint 2: Moving files", "Hint 3: Output data", "Hint 4: Often done to CSV"))
	WordHints.Put("FILTER", Array As String("Hint 1: Block unwanted", "Hint 2: Used in image editing", "Hint 3: Network protection", "Hint 4: Email spam control"))
	WordHints.Put("FLOPPY", Array As String("Hint 1: Storage device", "Hint 2: Magnetic disk", "Hint 3: 3.5 inches", "Hint 4: Legacy technology"))
	WordHints.Put("FOLDER", Array As String("Hint 1: File organizer", "Hint 2: Virtual directory", "Hint 3: Can be opened", "Hint 4: Holds documents"))
	WordHints.Put("FORMAT", Array As String("Hint 1: Disk preparation", "Hint 2: Clears storage", "Hint 3: File system setup", "Hint 4: Often done to USB drives"))
	WordHints.Put("GOOGLE", Array As String("Hint 1: Search engine", "Hint 2: Founded by Larry Page", "Hint 3: Alphabet Inc.", "Hint 4: Famous for its algorithm"))
	WordHints.Put("GOPHER", Array As String("Hint 1: Old internet protocol", "Hint 2: Text-based", "Hint 3: Pre-Web", "Hint 4: 1990s use"))
	WordHints.Put("HACKER", Array As String("Hint 1: Code breaker", "Hint 2: Cybersecurity", "Hint 3: Penetration tester", "Hint 4: Often associated with computers"))
	WordHints.Put("HEADER", Array As String("Hint 1: Section title", "Hint 2: Top of page", "Hint 3: Web document element", "Hint 4: Displays info"))
	WordHints.Put("IMPORT", Array As String("Hint 1: Data input", "Hint 2: External source", "Hint 3: Reverse of export", "Hint 4: Data from another system"))
	WordHints.Put("INKJET", Array As String("Hint 1: Printer type", "Hint 2: Cartridge-based", "Hint 3: Uses liquid ink", "Hint 4: Common office printer"))
	WordHints.Put("ITUNES", Array As String("Hint 1: Music software", "Hint 2: Apple product", "Hint 3: Media management", "Hint 4: Syncs devices"))
	WordHints.Put("JOVIAL", Array As String("Hint 1: Cheerful", "Hint 2: Positive demeanor", "Hint 3: Bright personality", "Hint 4: Joyful"))
	WordHints.Put("KEYPAD", Array As String("Hint 1: Button interface", "Hint 2: On devices", "Hint 3: Numeric or QWERTY", "Hint 4: Often found on phones"))
	WordHints.Put("LAPTOP", Array As String("Hint 1: Portable computer", "Hint 2: Runs on battery", "Hint 3: Can be folded", "Hint 4: Personal device"))
	WordHints.Put("LOGOFF", Array As String("Hint 1: Exit session", "Hint 2: Disconnect", "Hint 3: End account", "Hint 4: User logout"))
	WordHints.Put("LOGOUT", Array As String("Hint 1: Sign off", "Hint 2: End session", "Hint 3: Close account", "Hint 4: Logout from system"))
	WordHints.Put("MEMORY", Array As String("Hint 1: Stores data", "Hint 2: RAM or ROM", "Hint 3: Temporary or permanent", "Hint 4: Computer storage"))
	WordHints.Put("NEWBIE", Array As String("Hint 1: Beginner", "Hint 2: Novice", "Hint 3: New to a field", "Hint 4: Often seen in online forums"))
	WordHints.Put("NIBBLE", Array As String("Hint 1: Half a byte", "Hint 2: Data unit", "Hint 3: 4 bits", "Hint 4: Smaller than byte"))
	WordHints.Put("OFFICE", Array As String("Hint 1: Work environment", "Hint 2: Microsoft product", "Hint 3: Desk and computer", "Hint 4: Business-related"))
	WordHints.Put("ONLINE", Array As String("Hint 1: Connected to the internet", "Hint 2: Web-based", "Hint 3: Real-time", "Hint 4: Available on the net"))
	WordHints.Put("OUTPUT", Array As String("Hint 1: Data produced", "Hint 2: Result of process", "Hint 3: Sent to external", "Hint 4: Device response"))
	WordHints.Put("PACKET", Array As String("Hint 1: Data unit", "Hint 2: Transmitted over networks", "Hint 3: Contains information", "Hint 4: Broken into smaller chunks"))
	WordHints.Put("PARITY", Array As String("Hint 1: Error checking", "Hint 2: Odd or even", "Hint 3: Data integrity", "Hint 4: Used in transmission"))
	WordHints.Put("PARSER", Array As String("Hint 1: Processes text", "Hint 2: Compiles code", "Hint 3: Breaks down data", "Hint 4: Important in compilers"))
	WordHints.Put("PASCAL", Array As String("Hint 1: Programming language", "Hint 2: Developed by Niklaus Wirth", "Hint 3: Old school language", "Hint 4: Used in education"))
	WordHints.Put("PLUGIN", Array As String("Hint 1: Add-on feature", "Hint 2: Extends software", "Hint 3: Works with other programs", "Hint 4: Enhances capabilities"))
	WordHints.Put("PORTAL", Array As String("Hint 1: Web gateway", "Hint 2: Website entry point", "Hint 3: Provides access", "Hint 4: Often used for services"))
	WordHints.Put("PROLOG", Array As String("Hint 1: Programming language", "Hint 2: Logic-based", "Hint 3: AI and databases", "Hint 4: Symbolic computation"))
	WordHints.Put("PROMPT", Array As String("Hint 1: Command line input", "Hint 2: User interaction", "Hint 3: Ask for input", "Hint 4: Displayed text"))
	WordHints.Put("RASTER", Array As String("Hint 1: Image grid", "Hint 2: Pixel-based", "Hint 3: Bitmap format", "Hint 4: Scanned image"))
	WordHints.Put("RECORD", Array As String("Hint 1: Data storage", "Hint 2: Music album", "Hint 3: Video tape", "Hint 4: Input data"))
	WordHints.Put("REMOTE", Array As String("Hint 1: Control device", "Hint 2: Wireless", "Hint 3: TV or computer", "Hint 4: Sends signals"))
	WordHints.Put("ROUTER", Array As String("Hint 1: Network device", "Hint 2: Routes data", "Hint 3: Internet gateway", "Hint 4: Wi-Fi connection"))
	WordHints.Put("SCROLL", Array As String("Hint 1: Move screen", "Hint 2: Down or up", "Hint 3: Read content", "Hint 4: Use mouse or keyboard"))
	WordHints.Put("SEARCH", Array As String("Hint 1: Find information", "Hint 2: Internet query", "Hint 3: Google function", "Hint 4: Keywords used"))
	WordHints.Put("SECTOR", Array As String("Hint 1: Disk division", "Hint 2: Storage part", "Hint 3: Geographical area", "Hint 4: File system structure"))
	WordHints.Put("SERVER", Array As String("Hint 1: Provides services", "Hint 2: Host computer", "Hint 3: Stores files", "Hint 4: Network endpoint"))
	WordHints.Put("SMILEY", Array As String("Hint 1: Expression", "Hint 2: Emoji", "Hint 3: Happy face", "Hint 4: Social media"))
	WordHints.Put("SNOBOL", Array As String("Hint 1: Programming language", "Hint 2: String processing", "Hint 3: Old language", "Hint 4: Pattern matching"))
	WordHints.Put("SPIDER", Array As String("Hint 1: Web crawler", "Hint 2: Search engine bot", "Hint 3: Indexing tool", "Hint 4: Collects web data"))
	WordHints.Put("SPRITE", Array As String("Hint 1: 2D image", "Hint 2: Animation", "Hint 3: Used in games", "Hint 4: Character representation"))
	WordHints.Put("STATUS", Array As String("Hint 1: Current state", "Hint 2: User condition", "Hint 3: System info", "Hint 4: Display message"))
	WordHints.Put("SYSTEM", Array As String("Hint 1: Set of components", "Hint 2: Operating unit", "Hint 3: Computer environment", "Hint 4: Interrelated parts"))
	WordHints.Put("TABLET", Array As String("Hint 1: Touchscreen device", "Hint 2: Portable", "Hint 3: Personal computing", "Hint 4: iPad or Android"))
	WordHints.Put("TELNET", Array As String("Hint 1: Remote login", "Hint 2: Old internet protocol", "Hint 3: Command line interface", "Hint 4: Used in networking"))
	WordHints.Put("TROJAN", Array As String("Hint 1: Malware", "Hint 2: Virus type", "Hint 3: Often disguised", "Hint 4: Used in cyber attacks"))
	WordHints.Put("UPDATE", Array As String("Hint 1: Software improvement", "Hint 2: New features", "Hint 3: Version change", "Hint 4: System upgrade"))
	WordHints.Put("UPLOAD", Array As String("Hint 1: Send data", "Hint 2: Internet transfer", "Hint 3: Files to server", "Hint 4: Cloud service"))
	WordHints.Put("USERID", Array As String("Hint 1: Login name", "Hint 2: Unique identifier", "Hint 3: Used in systems", "Hint 4: Personal account"))
	WordHints.Put("VIRUS", Array As String("Hint 1: Malicious code", "Hint 2: Infects systems", "Hint 3: Spreads rapidly", "Hint 4: Can corrupt data"))
	WordHints.Put("VIRTUAL", Array As String("Hint 1: Not physical", "Hint 2: Simulated environment", "Hint 3: Computer-generated", "Hint 4: Used in reality simulations"))
	WordHints.Put("WIFI", Array As String("Hint 1: Wireless connection", "Hint 2: Internet access", "Hint 3: Router connection", "Hint 4: Radio frequency"))
	WordHints.Put("WINDOWS", Array As String("Hint 1: Operating system", "Hint 2: Microsoft product", "Hint 3: GUI-based", "Hint 4: Runs on PCs"))
	WordHints.Put("XENON", Array As String("Hint 1: Rare gas", "Hint 2: Noble gas", "Hint 3: Used in lamps", "Hint 4: Symbol Xe"))
	WordHints.Put("ZEBRA", Array As String("Hint 1: Striped animal", "Hint 2: African species", "Hint 3: Black and white", "Hint 4: Wild horse"))
	WordHints.Put("SMILEY", Array As String("Hint 1: Emoticon symbol", "Hint 2: Represents emotions", "Hint 3: :) or :-D", "Hint 4: Used in chats and messages"))
	WordHints.Put("SNOBOL", Array As String("Hint 1: Programming language", "Hint 2: String manipulation focused", "Hint 3: Developed in 1960s", "Hint 4: Known for text processing"))
	WordHints.Put("SPIDER", Array As String("Hint 1: Web crawler", "Hint 2: Indexes web pages", "Hint 3: Used by search engines", "Hint 4: Internet bot"))
	WordHints.Put("SPRITE", Array As String("Hint 1: 2D graphic object", "Hint 2: Used in games", "Hint 3: Moves independently", "Hint 4: Often animated"))
	WordHints.Put("STATUS", Array As String("Hint 1: Current condition", "Hint 2: Shown in apps", "Hint 3: Online/offline indicator", "Hint 4: Social media update"))
	WordHints.Put("SYSTEM", Array As String("Hint 1: Interconnected components", "Hint 2: Operating environment", "Hint 3: Software + hardware", "Hint 4: Manages resources"))
	WordHints.Put("TABLET", Array As String("Hint 1: Touchscreen device", "Hint 2: Larger than phone", "Hint 3: Portable computer", "Hint 4: Runs mobile OS"))
	WordHints.Put("TELNET", Array As String("Hint 1: Remote login protocol", "Hint 2: Text-based interface", "Hint 3: Uses port 23", "Hint 4: Predecessor to SSH"))
	WordHints.Put("TROJAN", Array As String("Hint 1: Malware type", "Hint 2: Disguised as legit", "Hint 3: Hidden threat", "Hint 4: Gains unauthorized access"))
	WordHints.Put("UPDATE", Array As String("Hint 1: Software improvement", "Hint 2: Fixes bugs", "Hint 3: Adds features", "Hint 4: Requires download"))
	WordHints.Put("UPLOAD", Array As String("Hint 1: Send data online", "Hint 2: Opposite of download", "Hint 3: To server or cloud", "Hint 4: File transfer"))
	WordHints.Put("UPTIME", Array As String("Hint 1: Time since last reboot", "Hint 2: System availability", "Hint 3: Tracked in servers", "Hint 4: Indicates reliability"))
	WordHints.Put("USENET", Array As String("Hint 1: Online forum system", "Hint 2: Pre-web messaging", "Hint 3: Uses newsgroups", "Hint 4: Popular in early internet"))
	WordHints.Put("VECTOR", Array As String("Hint 1: Scalable graphics", "Hint 2: Based on paths", "Hint 3: Unlike bitmap", "Hint 4: Used in illustrations"))
	WordHints.Put("WEBLOG", Array As String("Hint 1: Online journal", "Hint 2: Also called blog", "Hint 3: Time-stamped entries", "Hint 4: Personal or professional"))
	WordHints.Put("WINDOW", Array As String("Hint 1: GUI element", "Hint 2: Contains content", "Hint 3: Can be resized", "Hint 4: Part of desktop UI"))
	WordHints.Put("WIZARD", Array As String("Hint 1: Setup assistant", "Hint 2: Step-by-step help", "Hint 3: Common in software installs", "Hint 4: Simplifies tasks"))

End Sub
Private Sub nexl_Click
	Main.gp = Main.gp + 1
	File.WriteString(File.DirInternal, "gp.txt", Main.gp)
	Main.mods = "game"
	StartActivity("loading")
	Activity.Finish

End Sub
Sub AnimateGovToMiddle
	' Make sure the panel starts off-screen at the top
	gov.Top = -gov.Height

	' Show the panel
	gov.Visible = True

	' Animate to the middle of the screen over 500ms
	Dim middleY As Int = (80%y - gov.Height) / 2
	gov.SetLayoutAnimated(1400, gov.Left, middleY, gov.Width, gov.Height)
End Sub


Private Sub cancel_Click
	Main.cl1.play
	menu_Click
End Sub

Private Sub to_main_Click
	Main.cl1.play
	Main.bg2.Stop
	Main.bg1.Initialize()
	Main.bg1.Load(File.DirAssets, "nbgm1.mp3")
	Main.bg1.SetVolume(Main.vl1, Main.vl1)
	Main.bg1.Looping = True
		
	Main.bg1.play
	StartActivity("loading")
	Main.mods = "Main"
	Activity.finish
End Sub