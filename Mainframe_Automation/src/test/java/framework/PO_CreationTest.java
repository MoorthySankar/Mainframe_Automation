package framework;

import java.awt.AWTException;
import java.io.IOException;

import com.jagacy.Key;
import com.jagacy.util.JagacyException;

public class PO_CreationTest {
	
	MF_Session Session;
	public PO_CreationTest(TestData TD, MF_Session Session) throws AWTException, IOException {
		this.Session = Session;
		String UserName = TD.Common_Data.UserName;
		String Password = TD.Common_Data.Password;
		String Region = TD.Common_Data.Region;
		String PO = Sys_FuncLib.GetData().PO_Number;
		String Supplier = Sys_FuncLib.GetData().Supplier;
		String[] Arr_Prod = Sys_FuncLib.GetData().Prod;
		String[] Arr_Qty = Sys_FuncLib.GetData().Qty;

		try {
			if (Arr_Qty.length == Arr_Prod.length) {
				Session.WriteDataWithEnter(OR.Terminal_Screen, "TPX");				
				Session.WaitForScreen(OR.UserID_Screen, "Userid:");
				Session.WriteData(OR.UserID, UserName);
				Session.WriteDataWithEnter(OR.Password, Password);
				Session.WaitForScreen(OR.TPX_MENU_Screen, "TPX MENU");
				Session.WriteDataWithEnter(OR.Command_Input, "CICT" + Region);
				Session.WaitForScreen(OR.Region_SignOn_Screen, "Sign On");
				Session.WriteData(OR.Region_Login_UserID, UserName);
				Session.WriteDataWithEnter(OR.Region_Login_Password, Password);
				Session.WriteDataWithEnter(OR.Region_SignOn_SuccessMsg, "DENV");
				Session.WaitForScreen(OR.Denver_Menu, "DENVER MENU");
				Session.WriteDataWithEnter(OR.DenvMenu_Selection, "5");
//				Session.WaitForScreen(OR.FunctionCode_Screen, "FUNCTION CODE");
//				Session.WriteDataWithEnter(OR.FunctionCode, "REMA");
//				Session.WaitForScreen(OR.DSC_Screen, "DSC REMA");
				Enter_DenverMenu("REMA");
				Sys_FuncLib.LogMsg("Create Purchase Order");
				Session.WriteData(OR.REMA_PO, PO);
				Session.WriteData(OR.REMA_Funtion, "Add");
				Session.WriteData(OR.REMA_Wave, "2");
				Session.WriteData(OR.REMA_Supplier, Supplier);
				Session.WriteDataWithEnter(OR.REMA_RecordSource, "W");
				Session.WaitForScreen(OR.REMA_SuccessMsg, "WAS DONE");
				Session.SendKeys(Key.PF3);

				Enter_DenverMenu("REMB");
//				Session.WriteDataWithEnter(OR.FunctionCode, "REMB");
//				Session.WaitForScreen(OR.DSC_Screen, "DSC REMB");
				Session.WriteData(OR.REMB_PO, PO);

				Sys_FuncLib.LogMsg("Adding Product to Purchase Order");

				for (int i = 0; i < Arr_Prod.length; i++) {
					String Cur_Prod = Arr_Prod[i];
					String Cur_Qty = Arr_Qty[i];
					Cur_Prod = Cur_Prod + "        ";
					Session.WriteData(OR.REMB_Prod, Cur_Prod.substring(0, 7));
					Session.WriteData(OR.REMB_Funtion, "Add");
					Session.WriteData(OR.REMB_Qty, Cur_Qty);
					Session.WriteDataWithEnter(OR.REMB_BackOrder, "Y");
					Session.WaitForScreen(OR.REMB_SuccessMsg, "WAS DONE");
				}

				Session.SendKeys(Key.PF3);

				Enter_DenverMenu("REQA");
//				Session.WaitForScreen(OR.FunctionCode_Screen, "FUNCTION CODE");
//				Session.WriteDataWithEnter(OR.FunctionCode, "REQA");
//				Session.WaitForScreen(OR.DSC_Screen, "DSC REQA");
				Session.WriteDataWithEnter(OR.REQA_PO, PO);

				Sys_FuncLib.LogMsg("Checking Product and Quantity in Purchase Order");
				
				String[] AllProd = User_FuncLib.GetAllData_fromScreen(Session,OR.REQA_Prod,7).split("\\|");
								
				
//				for(int i=0;i<Arr_Prod.length;i++)
//				{
//					int Temp=0;					
//					for (String Cur_Prod: AllProd)
//					{
//						if(Cur_Prod.contains(Arr_Prod[i]))
//						{
//							Temp=1;
//							Sys_FuncLib.LogMsg("Prod is available in REQA Screen$$"+Arr_Prod[i]);
//						}
//					}
//					if(Temp==0)
//						Sys_FuncLib.ErrorMsg("Prod is not available in REQA Screen$$"+Arr_Prod[i]);	
//				}
//				
//				for(int i=0;i<Arr_Prod.length;i++)
//					if(Arrays.asList(AllProd).contains(Arr_Prod[i]))
//						Sys_FuncLib.LogMsg("Prod is available in REQA Screen$$"+Arr_Prod[i]);

				String[] AllQty = User_FuncLib.GetAllData_fromScreen(Session,OR.REQA_Qty,5).split("\\|");
				
//				for(int i=0;i<AllQty.length;i++)
//					if(Arrays.asList(AllQty).contains(Arr_Qty[i]))
//						Sys_FuncLib.LogMsg("Qty is available in REQA Screen$$"+Arr_Qty[i]);

				for(int i=0;i<Arr_Prod.length;i++)
				{
					int Temp=0;
					for(int j=0;j<AllProd.length;j++)
					{
						if(AllProd[j].contains(Arr_Prod[i]))
						{
							Temp=1;
							Sys_FuncLib.LogMsg("Prod is available in REQA Screen$$"+Arr_Prod[i]);
						}
					}
					if(Temp==0)
						Sys_FuncLib.ErrorMsg("Prod is not available in REQA Screen$$"+Arr_Prod[i]);	
				}
				
				AllQty = User_FuncLib.GetAllData_fromScreen(Session,OR.REQA_Qty,5).split("\\|");

				for(int i=0;i<Arr_Qty.length;i++)
				{
					int Temp=0;
					for(int j=0;j<AllQty.length;j++)
					{
						if(AllQty[j].contains(Arr_Qty[i]))
						{
							Temp=1;
							Sys_FuncLib.LogMsg("Qty is available in REQA Screen$$"+Arr_Qty[i]);
						}
					}
					if(Temp==0)
						Sys_FuncLib.ErrorMsg("Qty is not available in REQA Screen$$"+Arr_Qty[i]);
				}
				
				
//				Session.readField(arg0, arg1, arg2)
								
				for(int i=0;i<3;i++)
					Session.SendKeys(Key.PF3);
				Session.WriteDataWithEnter(OR.Terminal_Screen, "OFF");
			}
			else
				Sys_FuncLib.ErrorMsg("QTY & Prod Length is not matched");

		} catch (JagacyException | AWTException | IOException e) {
			e.printStackTrace();
			Sys_FuncLib.ErrorMsg("Unexpected Error");
		}
	}
	
	public void Enter_DenverMenu(String ScreenName) throws JagacyException, AWTException, IOException
	{
		Session.WaitForScreen(OR.FunctionCode_Screen, "FUNCTION CODE");
		Session.WriteDataWithEnter(OR.FunctionCode, ScreenName);
		Session.WaitForScreen(OR.DSC_Screen, "DSC "+ScreenName);
	}
}
