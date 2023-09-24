AppTitle("Clay Shooting 3D v1.1","Finished Shooting?")

If CommandLine$()="walrus32.dll" Then
	AppTitle ("Yes, Tim farted on this too.","Smell it yet?")
EndIf

Include "CDKeyTest.bb"

m1#=4
m2#=1
m3#=6
m4#=4

inputkey(0)

Include "launcher3d.bb"

fps=CreateTimer(30)

AutoMidHandle True

SeedRnd(MilliSecs())

Global mode=2
Global shot

If mode=1 Then
shot=3
Else If mode=2 Then
shot=1
Else If mode=3 Then
shot=3
EndIf

Global MRND=0
Global need=1
Global delayed=0

ChangeDir "Data"

Global crosshair=LoadImage("crosshair.bmp")

Global hud=LoadImage("hud.bmp")
MaskImage hud,255,0,255

Global go=LoadAnimImage("Go.bmp",160,115,0,4)
MaskImage go,255,0,255

Global over=LoadImage("over.bmp")
MaskImage over,255,0,255

Global disc=CreateCylinder()
ScaleEntity disc,1,.25,1
HideEntity disc

Global countdiscs
Global score#
Global Round#=1
Global Throwndiscs#=0

Global spark=LoadSprite( "spark.bmp" )
EntityAlpha spark, 0

Global gfxshot=LoadAnimImage("Shot.bmp",60,25,0,4)

Global world=LoadMesh("world.3ds")
EntityType world, type_world
ScaleEntity world,2,2,2
MoveEntity world,0,0,50

;load sounds
Global sfxreload=LoadSound("reload.wav")
Global sfxSreload=LoadSound("Sreload.wav")
Global sfxshot=LoadSound("Shoot.wav")
Global chnshot

Global camera=CreateCamera()
CameraClsColor camera,0,232,216
PositionEntity camera,0,20,-5
RotateEntity camera,5,0,0

Global light=CreateLight()

Const Type_Disc=1,Type_World=2

Collisions type_disc,type_world,2,2

Type disc
Field Obj
Field XLand#,Yland#,Zland#
Field XCanon#,YCanon#,ZCanon#
Field Hangtime#
Field ShotVelY#,ShotVelR#,ShotVel#
Field Time#
Field THETA#,PHI#
Field XNew#,YNew#,ZNew#
End Type

Type Sprite
Field entity
Field decay
End Type

font=LoadFont("System",38,1,0,0)
SetFont font

Type frags
	Field speed#, entity, alpha#
End Type

Global Start=True
Global starttime=0

While Not KeyHit(1)

	Cls
	starttime=starttime+1
	If starttime>50 Then
		start=False
	EndIf
	
	If mode=2 Then
		If starttime=50 Then
			throwdisc()
		EndIf
	
		If starttime=80 Then
			throwdisc()
		EndIf
	EndIf
	
	If KeyHit(28) Then
		Flip
	EndIf
	
	updatediscs()
	updateparticles()
	updatesprites()
	
	
	UpdateWorld()
	RenderWorld()
	
	
	If mode=3 Then
		throwdisc()
		need=1
		Delayed=0
		MRND=MRND+1
		SHOT=3
		need=1
	EndIf
	
	If mode=1 And discscount=0 And delayed>50 Then
		throwdisc()
		need=1
		Delayed=0
		MRND=MRND+1
		chnshoot=PlaySound(sfxreload)
		SHOT=3
	EndIf
	
	If mode=1 And need=1 Then
		Delayed=delayed+1
		If Delayed=20 Then
			throwdisc()
			need=0
		EndIf
	EndIf
	
	
	If MouseHit(1) Or MouseHit(2) Then
		If SHOT>0 Then ;if not out of amo
			CameraPick camera, MouseX(), MouseY()
			shot=shot-1
			chnshot=PlaySound(SfxShot)
		EndIf
	EndIf
	
	If countdiscs=0 Then
		If MODE=1 Then
			;????
		EndIf
		If MODE=2 And starttime>100 Then
			Gameover()
		EndIf
	EndIf
	
	If mode=2 And MRND=10 Then
		Round#=Round#+1
		MRND=0
	EndIf
	
	If start=True Then
			DrawImage go,400,100,frme
			frme=frme+1
			If frme=4 Then frme=0
	EndIf
	
	If mode=2 And MouseHit(3) Then
		chnshoot=PlaySound(sfxSreload)
		SHOT=3
	EndIf
	
	;hud
	
	DrawImage hud,400,550

	For RA=1 To MRnd
		Color 255,0,0
		Text 284+(RA*24),530,"X"
		Color 255,255,255
	Next
	
;	Text 0,0,MouseX() + " , " + MouseY()
	
	Color 255,255,255
	Text 610,540, Int(Score#),0,1
	
	Color 0,255,0
	Text 146,480,Int(ROUND#),1
	
	Color 255,255,255
	
	DrawImage gfxshot,124,543,SHOT
	
	DrawImage crosshair,MouseX(),MouseY()
	Flip
	
	
	WaitTimer fps
	
Wend

End

Function throwdisc()

If mode=2 Then
shot=shot+1
EndIf

throwndiscs#=throwndiscs#+1

d.disc = New disc

d\obj=CopyEntity(Disc)
EntityPickMode d\obj,2


d\XLand=Rand(-20,20)
d\YLand=0
d\ZLand=Rand(70,85)


d\XCanon=Rand(-40,40)
d\YCanon=1
d\ZCanon=10

If (d\XLand - d\XCanon)=0 Then
d\THETA = 90
Else
d\THETA = ATan2(d\ZLand,(d\XLand - d\XCanon))
EndIf

d\PHI = 20

d\hangtime# = Rnd(3,3.5)
d\time=3

d\ShotvelY# = d\hangtime * 16
d\ShotVelR# = Sqr(((d\XLand-d\XCanon)^2) + (d\ZLand^2))/(d\hangtime)
; d\ShotVel# = Sqr( (d\ShotVelR^2) + (d\ShotVelY^2))

PositionEntity d\obj,d\XCanon#,d\YCanon#,d\ZCanon#
EntityType d\obj,Type_Disc

End Function

Function updatediscs()

countdiscs=0

For d.disc=Each disc
countdiscs=countdiscs+1

d\time=d\time+.35

d\XNew = d\ShotVelR *  Cos(D\Theta) * (d\time /30) + D\XCanon
d\YNew = -16 * ((D\time /30)^2) + d\SHOTVELY * (D\time /30)
d\ZNew = d\SHOTVELR * Sin(D\THETA) * (D\time /30) + D\ZCanon

PositionEntity d\obj,d\XNew,d\YNew,d\ZNew

If PickedEntity()=d\obj Then
blowupdisc(d)
Else If d\YNew=<0 Or EntityCollided(d\obj,TYPE_world)=True Then
crashdisc(d)
EndIf

Next

End Function

Function blowupdisc(d.disc)

MRND=MRND+1

If mode=2 Then
throwdisc()
EndIf

score#=score#+1000

CreateSSprite(EntityX(d\obj),EntityY(d\obj),EntityZ(d\obj))

createparticle(EntityX(d\obj),EntityY(d\obj),EntityZ(d\obj))
HideEntity d\obj
Delete d
End Function

Function crashdisc(d.disc)
HideEntity d\obj
Delete d
End Function

Function createparticle(x#,y#,z#)
	For a = 1 To 5
		f.frags = New frags
		f\entity = CopyEntity(spark)
		PositionEntity f\entity, x#, y#, z#
		f\speed# = Rnd(3,4)
		f\alpha# = 1
		RotateEntity f\entity, Rand(360), Rand(360), Rand(360)
;		EntityColor f\entity, Rand(255), Rand(255), Rand(255)
		EntityAlpha f\entity, f\alpha#
		ScaleSprite f\entity, .5, .5
	Next
End Function


Function updateparticles()
	For f.frags = Each frags
		If f\alpha# > 0
			MoveEntity f\entity, 0, 0, f\speed#
			f\alpha# = f\alpha# - 0.1
		Else
			FreeEntity f\entity
			Delete f
		EndIf
	Next
End Function

Function gameover()

Local goover=True
frme=0
Local clicks=0

FlushKeys()
FlushMouse()

While goover=True


UpdateWorld()
RenderWorld()

DrawImage hud,GraphicsWidth()/2,GraphicsHeight()-ImageHeight(hud)

clicks=clicks+1

If clicks=200 Then
goover=False
EndIf

;hud

	DrawImage hud,400,550

	For RA=1 To MRnd
		Color 255,0,0
		Text 284+(RA*24),530,"X"
		Color 255,255,255
	Next
	
	Color 255,255,255
	Text 610,540, Int(Score#),0,1
	
	Color 0,255,0
	Text 146,480,Int(ROUND#),1
	
	Color 255,255,255
	
	DrawImage gfxshot,124,543,SHOT


DrawImage over,400,100

Flip
Wend

End

End Function

Function CreateSSprite(x,y,z)

s.Sprite = New Sprite

s\entity = LoadSprite("1000.bmp")
s\decay = 20

ScaleSprite s\entity, 2,2

PositionEntity s\entity,x,y,z

End Function

Function updatesprites()
For S.Sprite = Each Sprite

s\decay=s\decay-1
If s\decay=0 Then
HideEntity s\entity
Delete s
EndIf

Next

End Function