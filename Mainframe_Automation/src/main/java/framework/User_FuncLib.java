package framework;

import java.awt.AWTException;
import java.io.IOException;
import java.util.Arrays;

import com.jagacy.Key;
import com.jagacy.util.JagacyException;

public class User_FuncLib {
	
	public static String GetAllData_fromScreen(MF_Session s,String Field_Loc,int length) throws JagacyException
	{
		int[] Axis = Split_Axis(Field_Loc);		
		String ASD = "";
		for(int i=0;i<10;i++)
		{
			ASD += s.readPosition(Axis[0]++, Axis[1], length) + "|";
		}
		
		return ASD;
		
	}
	
	public static int[] Split_Axis(String Loc)
	{
		String[] CoOrdiantes = Loc.split("\\,");
		int[] Axis = Arrays.stream(CoOrdiantes).mapToInt(Integer::parseInt).toArray();
		return Axis ;	
	}
	
	public static void Login_MF(MF_Session Session,String UserName,String Password) throws JagacyException, AWTException, IOException
	{
		Session.WriteDataWithEnter(OR.Type_TerminalScreen, "TPX");
		Session.WaitForScreen(OR.UserID_Screen, "Userid:");
		Session.WriteData(OR.UserID, UserName);
		Session.WriteData(OR.Password, Password);
		Sys_FuncLib.LogMsg("User credentials entered successfully");
		Session.SendKeys(Key.ENTER);
		Session.WaitForScreen(OR.TPX_MENU_Screen, "TPX MENU");
	}
	
//	public void login(MF_Session Session,String UserName,String Password,String Region)
//	{
//		try {
//			if (Session.ReadData(OR.Terminal_Screen, 13).contains("THIS TERMINAL")) {
//				Session.WriteDataWithEnter(OR.Type_TerminalScreen, "TPX");
//				Session.WaitForScreen(OR.UserID_Screen, "Userid:");
//				Session.WriteData(OR.UserID, UserName);
//				Session.WriteData(OR.Password, Password);
//				Sys_FuncLib.LogMsg("User credentials entered successfully");
//				Session.SendKeys(Key.ENTER);
//				Session.WaitForScreen(OR.TPX_MENU_Screen, "TPX MENU");
//			}
//		} catch (JagacyException | AWTException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			if (Session.ReadData(OR.TPX_MENU_Screen, 8).contains("TPX MENU")) {
//				Session.WriteDataWithEnter(OR.Command_Input, "CICT" + Region);
//				Session.WaitForScreen(OR.Region_SignOn_Screen, "Sign On");
//				Session.WriteData(OR.Region_Login_UserID, UserName);
//				Session.WriteDataWithEnter(OR.Region_Login_Password, Password);
//				Session.WriteDataWithEnter(OR.Region_SignOn_SuccessMsg, "DENV");
//				if (Session.WaitForScreen(OR.Denver_Menu, "DENVER MENU")) {
////				PO_From_DenverMenu(PO, Supplier, Arr_Prod, Arr_Qty);
//					System.out.println("Hi");
//				} else
//					Sys_FuncLib.ErrorMsg("Denver Menu is not displayed");
//			} else
//				Sys_FuncLib.ErrorMsg("Error in displaying TPX Menu");
//		} catch (JagacyException | AWTException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
}
