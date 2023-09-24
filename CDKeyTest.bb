; Test CD key include for Nate Works internal programs only!!!
; Not cleared for public release.

Global m1#=4
Global m2#=12
Global m3#=8
Global m4#=3

Function testkey(inputkey$,Returnthere)
Cls
Text 0,0,"Checking key..."
Flip

	If Instr(inputkey$," ")<>0 Or Instr(inputkeys$,"-")<>0 Then
		testingkey$=killspaces$(inputkey$)
	Else
		testingkey$=inputkey$
	EndIf

	Local keygood=True
	
	;4,12,8,3
	
	If Not Mid$(testingkey$,m1#,1)=Mid$(testingkey$,13,1) Then
		keygood=False
		code#=1
	Else
		If Not Mid$(testingkey$,m2#,1)=Mid$(testingkey$,14,1) Then
			keygood=False
			code#=2
		Else
			If Not Mid$(testingkey$,m3#,1)=Mid$(testingkey$,15,1) Then
				keygood=False
				code#=3
			Else	
				If Not Mid$(testingkey$,m4#,1)=Mid$(testingkey$,16,1) Then
					keygood=False
					code#=4
				Else
					code#=0
				EndIf
			EndIf
		EndIf					
	EndIf
	
	If Not Len(Trim(testingkey$))=16 Then
		keygood=False
		code#=-1
	EndIf
	
	If returnthere=False
		Cls
		If keygood=True Then
			Text 0,0,"Key is good."
		Else
			RuntimeError "Key is no good. Error code #"+code#
		EndIf	
		Flip
		Delay 1000
		FlushKeys()
	Else
		Cls
		If keygood=True Then
			Text 0,0,"Key is good."
		Else
			Text 0,0, "Key is no good. Error code #"+code#
			inputkey(1)
		EndIf	
		Flip
		Delay 1000
		FlushKeys()
	EndIf

End Function

Function KillSpaces$(datastring$)
	datastring$=Replace$(datastring$," ","")
	datastring$=Replace$(datastring$,"-","")
	Return datastring$
End Function

Function inputkey(error)

	If FileType("key.inf")<>1 Then
		If error=True Then Print "There was an error."
		mycdkey$=Input$("Please enter your CD key: ")
		testkey(mycdkey$,0)
		filestream=WriteFile("key.inf")
		WriteLine(filestream,mycdkey$)
		CloseFile filestream
	Else
		filestream=OpenFile("key.inf")
		mycdkey$=ReadLine(filestream)
		CloseFile filestream
		testkey(mycdkey$,1)
	EndIf

End Function