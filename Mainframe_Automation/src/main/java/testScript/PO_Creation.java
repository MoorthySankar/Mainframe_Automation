package testScript;

import java.awt.AWTException;
import java.io.IOException;

import com.jagacy.Key;
import com.jagacy.util.JagacyException;

import framework.MF_Session;
import framework.OR;
import framework.Sys_FuncLib;
import framework.TestData;
import framework.User_FuncLib;

public class PO_Creation {

	MF_Session Session;

	public PO_Creation(TestData TD, MF_Session Session) throws AWTException, IOException {
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
				if (Session.ReadData(OR.Terminal_Screen, 13).contains("THIS TERMINAL")) {
					User_FuncLib.Login_MF(Session, UserName, Password);
				}
				if (Session.ReadData(OR.TPX_MENU_Screen, 8).contains("TPX MENU")) {
					Session.WriteDataWithEnter(OR.Command_Input, "CICT" + Region);
					Session.WaitForScreen(OR.Region_SignOn_Screen, "Sign On");
					Session.WriteData(OR.Region_Login_UserID, UserName);
					Session.WriteDataWithEnter(OR.Region_Login_Password, Password);
					Session.WriteDataWithEnter(OR.Region_SignOn_SuccessMsg, "DENV");
					if (Session.WaitForScreen(OR.Denver_Menu, "DENVER MENU")) {
						PO_From_DenverMenu(PO, Supplier, Arr_Prod, Arr_Qty);
					} else
						Sys_FuncLib.ErrorMsg("Denver Menu is not displayed");
				} else
					Sys_FuncLib.ErrorMsg("Error in displaying TPX Menu");
			} else
				Sys_FuncLib.ErrorMsg("QTY & Prod Length is not matched");
		} catch (JagacyException | AWTException | IOException e) {
			e.printStackTrace();
			Sys_FuncLib.ErrorMsg(e.toString());
		}
	}

	private void PO_From_DenverMenu(String PO, String Supplier, String[] Arr_Prod, String[] Arr_Qty)
			throws JagacyException, AWTException, IOException {
		Session.WriteDataWithEnter(OR.DenvMenu_Selection, "5");
		Enter_DenverMenu("REMA");
		Sys_FuncLib.InfoMsg("Creating Purchase Order");
		Session.WriteData(OR.REMA_PO,PO);
		Session.WriteData(OR.REMA_Funtion, "Add");
		Session.WriteData(OR.REMA_Wave, "2");
		Session.WriteData(OR.REMA_Supplier, Supplier);
		Session.WriteDataWithEnter(OR.REMA_RecordSource, "W");
		if (Session.WaitForScreen(OR.REMA_SuccessMsg, "WAS DONE")) {
			Sys_FuncLib.LogMsg("Purchase Order Created - "+PO);
			Session.SendKeys(Key.PF3);
			Enter_DenverMenu("REMB");
			Session.WriteData(OR.REMB_PO, PO);
			Sys_FuncLib.InfoMsg("Adding Product to Purchase Order");

			for (int i = 0; i < Arr_Prod.length; i++) {
				String Cur_Prod = Arr_Prod[i];
				String Cur_Qty = Arr_Qty[i];
				Cur_Prod = Cur_Prod + "        ";
				Session.WriteData(OR.REMB_Prod, Cur_Prod.substring(0, 7));
				Session.WriteData(OR.REMB_Funtion, "Add");
				Session.WriteData(OR.REMB_Qty, Cur_Qty);
				Session.WriteDataWithEnter(OR.REMB_BackOrder, "Y");
				if (Session.WaitForScreen(OR.REMB_SuccessMsg, "WAS DONE"))
					Sys_FuncLib.LogMsg("Product \"" + Cur_Prod + "\" added to Purchase Order \"" + PO + "\"");
				else
					Sys_FuncLib.ErrorMsg(
							"Error in adding the Product \"" + Cur_Prod + "\" to Purchase Order \"" + PO + "\"");
			}

			Session.SendKeys(Key.PF3);
			Enter_DenverMenu("REQA");
			Session.WriteDataWithEnter(OR.REQA_PO, PO);

			Sys_FuncLib.InfoMsg("Checking Products and Quantity in Purchase Order");
			String[] AllProd = User_FuncLib.GetAllData_fromScreen(Session, OR.REQA_Prod, 7).split("\\|");
			String[] AllQty = User_FuncLib.GetAllData_fromScreen(Session, OR.REQA_Qty, 5).split("\\|");

			Array_Matching(Arr_Prod, AllProd, "Prod");
			AllQty = User_FuncLib.GetAllData_fromScreen(Session, OR.REQA_Qty, 5).split("\\|");
			Array_Matching(Arr_Qty, AllQty, "Qty");
			Exit_DenverMenu();

		} else {
			String Err_Msg = Session.ReadData(OR.REMA_SuccessMsg, 17);
			Sys_FuncLib.ErrorMsg("Error in creating Purchase Order "+PO+". Expected:\"WAS DONE\" Actual:\"" + Err_Msg + "\"");
//			Exit_DenverMenu();
		}
	}

	private void Enter_DenverMenu(String ScreenName) throws JagacyException, AWTException, IOException {
		Session.WaitForScreen(OR.FunctionCode_Screen, "FUNCTION CODE");
		Session.WriteDataWithEnter(OR.FunctionCode, ScreenName);
		Session.WaitForScreen(OR.DSC_Screen, "DSC " + ScreenName);
	}

	private void Exit_DenverMenu() throws JagacyException, AWTException, IOException {
		for (int i = 0; i < 3; i++)
			Session.SendKeys(Key.PF3);
		Session.WriteDataWithEnter(OR.Type_TerminalScreen, "OFF");
		Session.WaitForScreen(OR.TPX_MENU_Screen, "TPX MENU");
		Session.waitForUnlock(1000);
	}

	private void Array_Matching(String[] Arr_Data1, String[] Arr_Data2, String Type)
			throws JagacyException, AWTException, IOException {
		for (int i = 0; i < Arr_Data1.length; i++) {
			int Temp = 0;
			for (String Cur_Qty : Arr_Data2) {
				if (Cur_Qty.contains(Arr_Data1[i])) {
					Temp = 1;
					Sys_FuncLib.LogMsg(Type + "-" + Arr_Data1[i] + " is available in REQA Screen");
				}
			}
			if (Temp == 0)
				Sys_FuncLib.ErrorMsg(Type + "-" + Arr_Data1[i] + " is not available in REQA Screen ");
		}
	}
}
