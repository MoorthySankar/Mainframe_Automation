package framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.http.auth.AuthenticationException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jagacy.util.JagacyException;
import com.sun.jersey.api.client.ClientHandlerException;

public class DriverScript extends Sys_FuncLib {
	String fileName;

	@BeforeTest
	public void Setup() throws IOException, AuthenticationException, ClientHandlerException {
		System.setProperty("jagacy.properties.dir", "./src/main/resources");
		System.setProperty("test.window", "true");

		// Read data from JSON File
		data = null;
		Gson gson = new Gson();
		String JsonFile = Project_Loc + "src/main/resources/TestData.json";

		try {
			JsonReader reader = new JsonReader(new FileReader(JsonFile));
			data = gson.fromJson(reader, TestData.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Data captured

		OutpuFolder_Init();
	}

	@Test
	public void MainAction() throws Exception {
		Input_XLFile = new FileInputStream(new File(Input_Loc + "MF_Input.xlsx"));
		workbook = new XSSFWorkbook(Input_XLFile);
		XSSFSheet MA_Sheet = workbook.getSheet("Main_Action");

		int TC_rowCount = MA_Sheet.getLastRowNum();
		int TC_noOfColumns = MA_Sheet.getRow(0).getLastCellNum();
		int TC_EID = 0, TC_ExecFlg = 0, TC_ID = 0;

		for (int j = 0; j < TC_noOfColumns; j++) {
			String current_Sheet = MA_Sheet.getRow(0).getCell(j).getStringCellValue();
			if (current_Sheet.equals("Execution_ID"))
				TC_EID = j;
			else if (current_Sheet.equals("Execution_Flag"))
				TC_ExecFlg = j;
			else if (current_Sheet.equals("Testcase_ID"))
				TC_ID = j;
		}

		int Tot_TC_Cnt = 0, TC_Cnt = 0;
		Boolean Sessio_Flag = true;

		for (int i = 0; i < TC_rowCount; i++) {
			if ("Yes".equalsIgnoreCase(MA_Sheet.getRow(i).getCell(TC_ExecFlg).getStringCellValue().trim()))
				++Tot_TC_Cnt;
		}

		for (int TC_row_num = 1; TC_row_num <= TC_rowCount; TC_row_num++) {
			if ("Yes".equalsIgnoreCase(MA_Sheet.getRow(TC_row_num).getCell(TC_ExecFlg).getStringCellValue().trim())) {
				Exec_Testcase = MA_Sheet.getRow(TC_row_num).getCell(TC_ID).getStringCellValue().trim();
				Execution_ID = MA_Sheet.getRow(TC_row_num).getCell(TC_EID).getStringCellValue().trim();
				try {

					ProgressBar(TC_Cnt++, Tot_TC_Cnt);

					if (Sessio_Flag) {
						Session = new MF_Session();
						Session.waitForUnlock(10000);
						Sessio_Flag = false;

						fileName = Path_HTML_Result + "/Execution_Report.html";

						htmlReporter = new ExtentHtmlReporter(fileName);
						htmlReporter.config().setTestViewChartLocation(ChartLocation.BOTTOM);
						htmlReporter.config().setChartVisibilityOnOpen(true);
						htmlReporter.config().setTheme(Theme.DARK);
						htmlReporter.config().setDocumentTitle(fileName);
						htmlReporter.config().setEncoding("utf-8");
						htmlReporter.config().setReportName(fileName);
						htmlReporter.loadXMLConfig(Project_Loc + "Lib/extent-config.xml");

						extent = new ExtentReports();
						extent.attachReporter(htmlReporter);
						extent.setSystemInfo("Project Name", "co-operative Bank");
						extent.setSystemInfo("Environment", "QA - Mainframe");
						extent.setSystemInfo("User Name", "Moorthy");
					}

					logger = extent.createTest(Exec_Testcase + "_" + Execution_ID);
					Class.forName("testScript." + Exec_Testcase).getConstructor(data.getClass(), Session.getClass())
							.newInstance(data, Session);
				} catch (JagacyException | SecurityException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException e) {
					e.printStackTrace();
					Sys_FuncLib.ErrorMsg(e.toString());
				}
				if (TC_Status == "Fail") {
					logger.log(Status.FAIL, MarkupHelper.createLabel("Test Case Failed", ExtentColor.RED));
					++TC_Count_Fail;
				} else if (TC_Status == "Warning")
					logger.log(Status.WARNING,
							MarkupHelper.createLabel("Test Case Passed with Warning message", ExtentColor.AMBER));
				else {
					logger.log(Status.PASS, MarkupHelper.createLabel("Test Case Passed", ExtentColor.GREEN));
					++TC_Count_Pass;
				}
			}
		}
	}

	@AfterClass
	public void closeTest() throws Exception {
		if (ProgressBar_Add != 1)
			ProgressBar.dispose();

		Session.close();
		extent.flush();
		workbook.close();
		Input_XLFile.close();
		Output_Zip();

		String SendMail = data.Common_Data.SendMail;
		String JIRA_Id = data.Common_Data.JIRA_Id;

		if (Strings.isNotNullAndNotEmpty(JIRA_Id)) {
			JIRA_Attachment(JIRA_Id);
			JIRA_Comments(JIRA_Id);
		}

		if (Strings.isNotNullAndNotEmpty(fileName))
			Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start chrome " + fileName });
//		{
//			 File htmlFile = new File(fileName);
//			 Desktop.getDesktop().browse(htmlFile.toURI());
//		}
		if (Strings.isNotNullAndNotEmpty(SendMail)) {
			String[] Arg_Pass = { "wscript", Project_Loc + "/Lib/Mail.vbs", Zip_FileName,
					TC_Count_Pass + "#" + TC_Count_Fail, SendMail };
			Runtime.getRuntime().exec(Arg_Pass);
		}
	}
}