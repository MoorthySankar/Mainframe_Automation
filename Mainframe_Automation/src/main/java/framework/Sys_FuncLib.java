package framework;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.util.Strings;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import framework.TestData.PO_Creation;

public class Sys_FuncLib {

	static File RootPath = new File(".");
	static String Project_Loc = RootPath.getAbsolutePath().substring(0, RootPath.getAbsolutePath().length() - 1);

	static String Output_Loc = Project_Loc + "Output\\";
	static String Input_Loc = Project_Loc + "Input\\";

	static SimpleDateFormat Date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	static String File_Name = CreateFileName();

	static String TC_Status = "";
	static int TC_Count_Fail = 0;
	static int TC_Count_Pass = 0;
	static int ScreenShot_Cnt = 0;
	static String Exec_Testcase;
	static String Execution_ID;
	static String Start_Time;
	static String Smry_Start_Time = CurrentDateTime();

	static FileInputStream Input_XLFile;
	static XSSFWorkbook workbook;
	static TestData data;
	static MF_Session Session;

	ExtentHtmlReporter htmlReporter;
	ExtentReports extent;
	static ExtentTest logger;
	
	static String Path_HTML_Result = Output_Loc + File_Name + "\\HTML_Result";
	static String Path_LogMsg_Screenshot = Output_Loc + File_Name + "\\LogMsg_Screenshot";
	static String Path_ErrorMsg_Screenshot = Output_Loc + File_Name + "\\ErrorMsg_Screenshot";

	protected static String Zip_FileName = Output_Loc + File_Name + "\\" + File_Name + ".zip";

	static JFrame ProgressBar = new JFrame("JProgressBar Sample");
	static JProgressBar progressBar;
	static int ProgressBar_Add = 1;
	static Border border;

	public static void OutpuFolder_Init() throws IOException {
		FolderExist(Path_HTML_Result);
		FolderExist(Path_LogMsg_Screenshot);
		FolderExist(Path_ErrorMsg_Screenshot);
	}

	public static void FolderExist(String FolderName) {
		File Res_Img_File = new File(FolderName);
		if (!Res_Img_File.exists())
			Res_Img_File.mkdirs();
	}

	public static String CreateFileName() {
		Calendar cal = Calendar.getInstance();
		String CurTime = Date_format.format(cal.getTime());
		return CurTime.replace("/", "-").replace(" ", "_").replace(":", "-");
	}

	public static String CurrentDateTime() {
		Calendar cal = Calendar.getInstance();
		return Date_format.format(cal.getTime()).toString();
	}

	public static void LogMsg(String LogMessage) throws AWTException, IOException {
		String Capture_Result = Path_LogMsg_Screenshot +"\\"+ Exec_Testcase + "_" + (++ScreenShot_Cnt) + ".png";
		try {
			getScreenshot(Capture_Result);
		} catch (Exception e) {
			CaptureScreen(Capture_Result);
		}

		// logger.pass(LogMessage);

		logger.pass("<a href=\"#\" data-featherlight=\"" + Capture_Result + "\">" + LogMessage + "</a>");
//		logger.addScreenCaptureFromPath(Capture_Result, LogMessage);
	}

	public static void ErrorMsg(String ErrorMessage) throws AWTException, IOException {
		String Capture_Result = Path_ErrorMsg_Screenshot +"\\"+ Exec_Testcase + "_" + (++ScreenShot_Cnt) + ".png";

		if (Strings.isNullOrEmpty(TC_Status))
			TC_Status = "Fail";
		try {
			getScreenshot(Capture_Result);
		} catch (Exception e) {
			CaptureScreen(Capture_Result);
		}
		logger.fail("<a href=\"#\" data-featherlight=\"" + Capture_Result + "\">" + ErrorMessage + "</a>");
		// logger.addScreenCaptureFromPath(Capture_Result,ErrorMessage);
	}

	public static void WarningMsg(String WarningMessage) throws AWTException, IOException {
		String Capture_Result = Path_ErrorMsg_Screenshot +"\\"+ Exec_Testcase + "_" + (++ScreenShot_Cnt) + ".png";

		if (Strings.isNullOrEmpty(TC_Status))
			TC_Status = "Warning";
		try {
			getScreenshot(Capture_Result);
		} catch (Exception e) {
			CaptureScreen(Capture_Result);
		}
		logger.warning("<a href=\"#\" data-featherlight=\"" + Capture_Result + "\">" + WarningMessage + "</a>");
//		logger.addScreenCaptureFromPath(Capture_Result, WarningMessage);
	}

	public static void InfoMsg(String ErrorMessage) throws AWTException, IOException {
		logger.info(ErrorMessage);
	}

	public static void CaptureScreen(String fileName) throws AWTException, IOException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		ImageIO.write(image, "png", new File(fileName));
	}

	public static PO_Creation GetData() {
		for (int i = 0; i < DriverScript.data.PO_Creation.size(); i++) {
			if (DriverScript.data.PO_Creation.get(i).Testcase_ID.contains(Execution_ID)) {
				return DriverScript.data.PO_Creation.get(i);
			}
		}
		return null;
	}

	public final static HashMap<RenderingHints.Key, Object> renderingProperties = new HashMap<>();

	public final static byte[] getScreenshot(String FileName) throws Exception {

		// below approach is inspired by solutions given
		// at http://stackoverflow.com/questions/18800717/convert-text-content-to-image

		String screenText = StringUtils.join(DriverScript.Session.readScreen(), "\n");

		renderingProperties.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderingProperties.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		renderingProperties.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		Font font = new Font("Consolas", Font.PLAIN, 12);
		FontRenderContext fontRenderContext = new FontRenderContext(null, true, true);
		BufferedImage bufferedImage = new BufferedImage(600, 300, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setRenderingHints(renderingProperties);
		graphics2D.setBackground(Color.black);
		graphics2D.setColor(Color.GREEN);
		graphics2D.setFont(font);
		graphics2D.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

		TextLayout textLayout = new TextLayout(screenText, font, fontRenderContext);

		int count = 0;
		for (String line : screenText.split("\n")) {
			graphics2D.drawString(line, 15, (int) (15 + count * textLayout.getBounds().getHeight() + 0.5));
			count++;
		}
		graphics2D.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "PNG", new File(FileName));
		return out.toByteArray();
	}

	public static void ProgressBar(int Cur_Loop, int Loop_Cnt) throws Exception {
		if (ProgressBar_Add++ == 1) {
			ProgressBar = new JFrame("Mainframe Test Automation");
			progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			Container content = ProgressBar.getContentPane();
			content.add(progressBar, BorderLayout.NORTH);
			ProgressBar.setSize(300, 100);
			ProgressBar.setVisible(true);
		}
		int as = 0;
		if (Loop_Cnt == 0) {
			border = BorderFactory.createTitledBorder(
					"\"Testcase Count\" value is Zero. So Progress Status would be Zero only");
			progressBar.setBorder(border);
		} else {
			border = BorderFactory.createTitledBorder("Executing : " + Exec_Testcase);
			progressBar.setBorder(border);
			as = (Cur_Loop * 100) / Loop_Cnt;
		}
		progressBar.setValue(as);
	}

	private static List<String> Zip_fileList;
	private static String OutputFolder;

	public static void Output_Zip() {
		OutputFolder = Output_Loc + File_Name;
		Zip_fileList = new ArrayList<String>();
		generateFileList(new File(OutputFolder));
		zipIt(Zip_FileName);
	}

	private static void zipIt(String zipFile) {
		byte[] buffer = new byte[1024];
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);
			FileInputStream in = null;
			for (String file : Zip_fileList) {
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);
				try {
					in = new FileInputStream(OutputFolder + File.separator + file);
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				} finally {
					in.close();
				}
			}
			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				zos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void generateFileList(File node) {
		if (node.isFile())
			Zip_fileList.add(generateZipEntry(node.toString()));

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote)
				generateFileList(new File(node, filename));
		}
	}

	private static String generateZipEntry(String file) {
		return file.substring(OutputFolder.length() + 1, file.length());
	}

	public boolean JIRA_Attachment(String issueKey) throws IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		String jira_attachment_authentication = new String(
				org.apache.commons.codec.binary.Base64.encodeBase64(("moorthy" + ":" + "Ben10Gwen").getBytes()));

		HttpPost httppost = new HttpPost("http://10.165.203.71:8080/rest/api/latest/issue/" + issueKey + "/attachments");
		httppost.setHeader("X-Atlassian-Token", "nocheck");
		httppost.setHeader("Authorization", "Basic " + jira_attachment_authentication);

		File fileToUpload = new File(Zip_FileName);
		FileBody fileBody = new FileBody(fileToUpload);

		HttpEntity entity = MultipartEntityBuilder.create().addPart("file", fileBody).build();

		httppost.setEntity(entity);

		CloseableHttpResponse response;

		try {
			response = httpclient.execute(httppost);
		} finally {
			httpclient.close();
		}

//		System.out.println(Set_Status(issueKey));

		if (response.getStatusLine().getStatusCode() == 200)
			return true;
		else
			return false;

	}
	
	protected static void JIRA_Comments(String issueKey) throws ClientProtocolException, IOException{		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String jira_attachment_authentication = new String(org.apache.commons.codec.binary.Base64.encodeBase64(("moorthy" + ":" + "Ben10Gwen").getBytes()));
		HttpPost httppost = new HttpPost("http://10.165.203.71:8080/rest/api/latest/issue/"+issueKey+"/comment");
		httppost.setHeader("X-Atlassian-Token", "nocheck");
		httppost.setHeader("Authorization", "Basic " + jira_attachment_authentication);	
		
		String json = "{\"body\":\"Test Execution Completed.\\n"
				+ "Summary Report :\\n"
				+ "Total No. of Passed TC : "+TC_Count_Pass + "\\n"
				+ " Total No. of Failed TC : "+TC_Count_Fail + "\"}";    
	    StringEntity entity = new StringEntity(json);
	    httppost.setEntity(entity);
	    httppost.setHeader("Accept", "application/json");
	    httppost.setHeader("Content-type", "application/json");
	    CloseableHttpResponse response = httpclient.execute(httppost);
//	    System.out.println(response.getStatusLine().getStatusCode());
	    httpclient.close();
	}
	

	public static boolean Set_Status(String issueKey) throws IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		String jira_attachment_authentication = new String(
				org.apache.commons.codec.binary.Base64.encodeBase64(("moorthy" + ":" + "Ben10Gwen").getBytes()));

		HttpPost httppost = new HttpPost(
				"http://localhost:8080/rest/api/latest/issue/" + issueKey + "/transitions?expand=transitions.fields");
		httppost.setHeader("X-Atlassian-Token", "nocheck");
		httppost.setHeader("Authorization", "Basic " + jira_attachment_authentication);
		
		StringEntity st = new StringEntity("{\"status\": {\"name\": \"Pass\"}}");

		httppost.setEntity(st);

		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-type", "application/json");

		CloseableHttpResponse response;

		try {
			response = httpclient.execute(httppost);
		} finally {
			httpclient.close();
		}

		if (response.getStatusLine().getStatusCode() == 200)
			return true;
		else
			return false;
	}
}
