package framework;

import java.awt.AWTException;
import java.io.IOException;
import java.util.Arrays;

import com.jagacy.Key;
import com.jagacy.Session3270;
import com.jagacy.util.JagacyException;

public class MF_SessionTest extends Session3270  {
	public MF_Session session;
	public static int DEFAULT_TIMEOUT = 10000;
	
	// Jagacy - Functions
		
	public MF_SessionTest() throws JagacyException {
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
			System.out.println(Axis[0]);
			System.out.println(Axis[1]);
			writePosition(Axis[0], Axis[1], Data);
			writeKey(Key.ENTER);
			waitForChange(DEFAULT_TIMEOUT);
			Sys_FuncLibTest.LogMsg("Value entered Successfully$$"+Data);
		}	
		else
				System.out.println("Error in OR");
	}
	
	public void WaitForScreen(String Loc,String Data) throws JagacyException 
	{
		if(Loc.split("\\,").length>0)
		{
			int[] Axis = Split_Axis(Loc);		
			session.waitForPosition(Axis[0], Axis[1], Data,DEFAULT_TIMEOUT);
		}	
		else
				System.out.println("Error in OR");
	}
	
	public void SendKeys(Key Action) throws JagacyException
	{
		session.writeKey(Action);
	}

}
