import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;

public class FuzzAnalysis 
{
	private static boolean isCase = false;
	private static boolean isTest = false;
	private static boolean isFailed = false;
	private static boolean isTime = false;
	private static String testCaseName = null;
	private static float testCaseDuration;
	private static int failedCount;

	private static Map<String, TestCase> testCaseMap = new HashMap<>();
	private static final String FILEPATH = "/var/lib/jenkins/jobs/iTrust_Fuzzer/builds";
	
	private static TestCase parsedCase = null;

	
	public static void main(String[] args) throws Exception {
		File junitFile;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		int buildNumber = Integer.parseInt(args[0]);
		int originalBuildNumber = buildNumber;

		while(buildNumber > 0){
			junitFile = new File(FILEPATH + "/" + buildNumber + "/junitResult.xml");

			if(!junitFile.exists()){
				buildNumber--;
				continue;
			}
					
			parser.parse(junitFile, new SaxHandler());
			buildNumber--;
		}
		
		final AtomicInteger index = new AtomicInteger();
		try(PrintWriter writer = new PrintWriter(new FileWriter(FILEPATH + "/" + originalBuildNumber + "/analysis.txt"))){
			

			writer.println("iTrust Analysis report after " + originalBuildNumber + " Fuzzes" );
			writer.println("");
			writer.println("******************************************");
			writer.println("Based on Time taken to execute");
			writer.println("******************************************");
			writer.printf("%s %s  %s  %s\n", padRight("INDEX",10), padRight("TEST CASE NAME",100), padRight("RUN COUNT",10) ,padRight( "TOTAL EXECUTION TIME",20));
			
		    List<TestCase> testCasesByExecTime = new ArrayList<TestCase>(testCaseMap.values());
			Collections.sort(testCasesByExecTime, new Comparator<TestCase>() {

		        public int compare(TestCase t1, TestCase t2) {
		            if(t1.getExecTime() > t2.getExecTime())
		            	return -1;
		            else if(t1.getExecTime() < t2.getExecTime())
		            	return 1;
		            else
		            	return 0;
		        }
		    });
			
			testCasesByExecTime.forEach((testCase) -> {
				writer.printf("%s %s  %s  %s\n", padRight(index.incrementAndGet(), 10), 
						padRight(testCase.getName(),100), padRight(testCase.getRunCount(),10), padRight(String.valueOf(testCase.getExecTime()),20));
			});
			index.set(0);
			List<TestCase> testCasesByFailCount = new ArrayList<TestCase>(testCaseMap.values());
			Collections.sort(testCasesByFailCount, new Comparator<TestCase>() {

		        public int compare(TestCase t1, TestCase t2) {
		            return t2.getFailCount() - t1.getFailCount();
		        }
		    });
			writer.println("******************************************");
			writer.println("Based on Number of failures");
			writer.println("******************************************");
			writer.printf("%s %s  %s  %s \n", padRight("INDEX",10), padRight("TEST CASE NAME",100), padRight("RUN COUNT",10) ,padRight("FAIL COUNT",10));
			testCasesByFailCount.forEach((testCase) -> {
				writer.printf("%s %s  %s  %s\n", padRight(index.incrementAndGet(), 10), 
						padRight(testCase.getName(),100), padRight(testCase.getRunCount(),10) ,padRight(testCase.getFailCount(),10));
			});
		}
	}
	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}
	
	public static String padRight(int s, int n) {
	    return padRight(String.valueOf(s), n);  
	}
	
	private static class TestCase{
		
		private int failCount = 0;
		private int runCount = 0;
		private float execTime = 0;
		private String name = null;
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getFailCount() {
			return failCount;
		}
		public void incrementFailCount() {
			this.failCount += 1;
		}
		public int getRunCount() {
			return runCount;
		}
		public void incrementRunCount() {
			this.runCount += 1;
		}
		public float getExecTime() {
			return execTime;
		}
		public void addExecTime(float execTime) {
			this.execTime += execTime;
		}
		
		
	}
	
	
	private static class SaxHandler extends DefaultHandler{

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			switch(qName){
			case "case":
				isCase = true;
				break;
			case "duration":
				isTime = true;
				break;
			case "testName":
				isTest = true;
				break;
			case "errorStackTrace":
				isFailed = true;
				break;
			}
			
			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if(isCase){
				if(isTime)
					testCaseDuration = (Float.parseFloat(String.copyValueOf(ch, start, length)));
				else if(isTest)
					testCaseName = (String.copyValueOf(ch, start, length));
				else if(isFailed)
					failedCount = 1;
			}
			super.characters(ch, start, length);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			switch(qName){
			
			case "duration":
				isTime = false;
				break;
				
			case "testName":
				isTest = false;
				break;
				
			case "errorStackTrace":
				isFailed = false;
				break;
			
			case "case":
				if(testCaseMap.containsKey(testCaseName)){
					TestCase tmpCase = testCaseMap.get(testCaseName);
					tmpCase.addExecTime(testCaseDuration);
					tmpCase.incrementRunCount();
					if(failedCount != 0)
						tmpCase.incrementFailCount();
				}else{
					parsedCase = new TestCase();
					parsedCase.setName(testCaseName);
					parsedCase.incrementRunCount();
					parsedCase.addExecTime(testCaseDuration);
					if(failedCount != 0)
						parsedCase.incrementFailCount();
					testCaseMap.put(testCaseName, parsedCase);
				}
				isCase = false;
				testCaseName = null;
				failedCount = 0;
				testCaseDuration =0;
				break;
			
			}
			super.endElement(uri, localName, qName);
		}
	}
}
