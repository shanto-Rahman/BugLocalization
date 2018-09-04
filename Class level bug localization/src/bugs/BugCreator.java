package bugs;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class BugCreator {
	public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
		   BugReportParser bugParser=new BugReportParser();
		   bugParser.getBugReport();
	}
}
