package framework;

import java.awt.AWTException;
import java.io.IOException;
import java.util.Arrays;

import com.jagacy.Key;
import com.jagacy.Session3270;
import com.jagacy.util.JagacyException;

public class MF_Session extends Session3270  {
	public static String SessionName = "Test";
	public MF_Session session;
	public static int DEFAULT_TIMEOUT = 1000;
	
	// Jagacy - Functions
		
	public MF_Session() throws JagacyException {
		super("test");
		open();
	}
	
	public int[] Split_Axis(String Loc)
	{
		String[] CoOrdiantes = Loc.split("\\,");
		int[] Axis = Arrays.stream(CoOrdiantes).mapToInt(Integer::parseInt).toArray();
		return Axis ;	
	}
	
	public void WriteData(String Loc,String Data) throws JagacyException, AWTException, IOException 
	{
		if(Loc.split("\\,").length>0)
		{
			int[] Axis = Split_Axis(Loc);
			writePosition(Axis[0], Axis[1], Data);
		}	
		else
			Sys_FuncLib.ErrorMsg("Unexpected Error");
	}
	
	public void WriteDataWithEnter(String Loc,String Data) throws JagacyException, AWTException, IOException 
	{
		if(Loc.split("\\,").length>0)
		{
			int[] Axis = Split_Axis(Loc);
			writePosition(Axis[0], Axis[1], Data);
			writeKey(Key.ENTER);
			waitForChange(DEFAULT_TIMEOUT);
			waitForUnlock(DEFAULT_TIMEOUT);
		}	
		else
			Sys_FuncLib.ErrorMsg("Unexpected Error");
	}
	
	public String ReadData(String Loc,int length) throws JagacyException, AWTException, IOException 
	{
		String Return_Data = "";
		if(Loc.split("\\,").length>0)
		{
			int[] Axis = Split_Axis(Loc);
			Return_Data = readPosition(Axis[0], Axis[1], length);
		}	
		else
			Sys_FuncLib.ErrorMsg("Unexpected Error");
		return Return_Data;
	}
	
	public Boolean WaitForScreen(String Loc,String Data) throws JagacyException, AWTException, IOException 
	{
		Boolean Return_Value=true;
		if(Loc.split("\\,").length>0)
		{
			int[] Axis = Split_Axis(Loc);		
			waitForPosition(Axis[0], Axis[1], Data,DEFAULT_TIMEOUT);
			
			if(!readRow(Axis[0]).contains(Data))
				Return_Value=false;
		}	
		else
		{
			Sys_FuncLib.ErrorMsg("Screen not found$$"+Data);			
			Return_Value=false;
		}
		
		return Return_Value;
	}
	
	public void SendKeys(Key Action) throws JagacyException
	{
		writeKey(Action);
		waitForChange(DEFAULT_TIMEOUT);
	}
	
//	protected boolean logon() throws JagacyException {
//
//		waitForUnlock("loan.timeout.seconds");
//		if (!waitForPosition("logon.wait", "logon.timeout.seconds")) {
//			writeKey(Key.CLEAR);
//			System.out.println("logon");
//		}
//
//		return true;
//	}

}
