package framework;

import java.util.List;

public class TestData {

	public Common_Data Common_Data;
	public List<PO_Creation> PO_Creation;

	public class Common_Data {
		public String UserName;
		public String Password;
		public String Region;
		public String SendMail;
		public String JIRA_Id;

//		public Common_Data() {
//			this.UserName = "";
//			this.Password = "";
//		}
	}
	
	public class PO_Creation {
		public String Testcase_ID;
		public String Supplier;
		public String PO_Number;
		public String[] Prod;
		public String[] Qty;
//		public PO_Creation() {
//			this.Testcase_ID = "";
//			this.Supplier = "";
//			this.Prod = null;
//			this.Qty = null;
//		}
	}
	
}