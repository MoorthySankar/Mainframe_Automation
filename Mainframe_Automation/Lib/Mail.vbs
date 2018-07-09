ResultPath = WScript.Arguments.Item(0)
Total_TestCase = WScript.Arguments.Item(1)
Email_Address = WScript.Arguments.Item(2)
Dim objOutl
Set fso = CreateObject("Scripting.FileSystemObject")
Set objOutl = CreateObject("Outlook.Application")
Set objMailItem = objOutl.CreateItem(olMailItem)
objMailItem.Display

Set objFile = fso.GetFile(ResultPath)
Temp_ResultPath = fso.GetParentFolderName(objFile)

'BodyText = fso.OpenTextFile(Temp_ResultPath & "\Summary Report.html",1).ReadAll

strEmailAddr = Split(Email_Address,",")
for each Address in strEmailAddr    
	objMailItem.Recipients.Add Address
next

TestCase_Cnt = Split(Total_TestCase,"#")

objMailItem.Subject = "Mainframe Automation Test Execution Result"
'objMailItem.Attachments.Add Temp_ResultPath & "\Summary.png", olByValue, 0

objMailItem.HTMLBody = objMailItem.HTMLBody & "Hi Team,<br><br>Greetings!<br><br>Please find the Mainframe Automation Test Execution Summary below.<br><br>" _
                &"<table>"_
				  &"<tr>"_
					&"<th>Type</h1>"_
					&"<th>Number of Test Case</h1>"_
				  &"</tr>"_
				  &"<tr>"_
					&"<td>Passed Test Case :     </td>"_
					&"<td>"&TestCase_Cnt(0)&"</td>"_
				  &"</tr>"_
				  &"<tr>"_
					&"<td>Failed Test Case :     </td>"_
					&"<td>"&TestCase_Cnt(1)&"</td>"_
				  &"</tr>"_
				&"</table>"_
				&"<br><br>"_
				&"We have attached the test results here and uploaded it in "_
				& "<a href=" & chr(34)  & "http://localhost:8080/browse/MFA-24" & chr(34) & ">JIRA</a>"_
				&"<br><br>"_
				& "<br>Best Regards, <br>Moorthy</font></span>"_
	&"</table>"
objMailItem.Display
objMailItem.Attachments.Add(ResultPath)
objMailItem.Send
Set objMailItem = nothing
Set objOutl = nothing
Set fso = nothing