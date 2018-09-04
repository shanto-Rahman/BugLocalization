package extractNodeNameCorpus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {
static File dataDependencySourceCodeFile;
static Document dataDependencyDoc;
static XPath xPath;
static int Id=1, m=0;
public static void main(String[] args) {
	try {
		SplitNodeName xmlContent =new SplitNodeName();
		dataDependencySourceCodeFile = new File("..\\class_Research\\src\\StaticParsing1\\1_BankStaticDataDependency.xml");
		DocumentBuilderFactory dataDependencyDbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dataDependencyDBuilder = dataDependencyDbFactory.newDocumentBuilder();
		dataDependencyDoc = dataDependencyDBuilder.parse(dataDependencySourceCodeFile);
		dataDependencyDoc.getDocumentElement().normalize();
		xPath = XPathFactory.newInstance().newXPath();
		
		NodeList dataDependencyPackageList = dataDependencyDoc.getElementsByTagName("Package");
		
		for (int temp = 0; temp < dataDependencyPackageList.getLength(); temp++) {

			Node PackageNode = dataDependencyPackageList.item(temp);
			// System.out.println("\n Dyn Current Element :" +
			if (PackageNode.getNodeType() == Node.ELEMENT_NODE) {

				Element PackageElement = (Element) PackageNode;
				String packageName = PackageElement.getAttribute("name");
				// System.out.println("Dyn Package Name : " +packageName);

				String ClassExpression = "/Source_Code/Package[@name='"+ packageName + "']/Class";
				NodeList ClassNodeList = (NodeList) xPath.compile(ClassExpression).evaluate(dataDependencyDoc, XPathConstants.NODESET);
				System.out.println("Class Node list " +ClassNodeList.getLength());
				for (int i = 0; i < ClassNodeList.getLength(); i++) {
					List<String> NodeList = new ArrayList<String>();
					Node classNode = ClassNodeList.item(i);
					Element classElement = (Element) classNode;
					String ClassName = classElement.getAttribute("name");
					// System.out.println(" Dyn ClassName " +ClassName);
					if(ClassName.equals("Withdraw")){
						System.out.println("Withdraw ");
					}
					String methodExpression = "/Source_Code/Package[@name='"+ packageName + "']/Class[@name='"+ ClassName + "']/Method";
					NodeList MethodList = (NodeList) xPath.compile(methodExpression).evaluate(dataDependencyDoc, XPathConstants.NODESET);
					System.out.println(" MethodList "+MethodList);
					for(int iM=0;iM<MethodList.getLength();iM++){
						int nodeNum=0;
						Node methodNode = MethodList.item(iM);
						Element methodElement = (Element) methodNode;
						String methodName = methodElement.getAttribute("name");		
						String methodScore= methodElement.getAttribute("score");
						
					String statementExpression = "/Source_Code/Package[@name='"+ packageName + "']/Class[@name='"+ ClassName + "']/Method[@name='"+ methodName + "']/Node"; 
					NodeList statementNodeList = (NodeList) xPath.compile(statementExpression).evaluate(dataDependencyDoc, XPathConstants.NODESET);
					System.out.println(statementNodeList.getLength());

					for (int iS = nodeNum; iS < statementNodeList.getLength(); iS++) {
						Node statementNode = statementNodeList.item(iS);
						Element statementElement = (Element) statementNode;
						String statementName = statementElement.getAttribute("name");
						String statementLineNumber=statementElement.getAttribute("lineNo");
						String statementDependenciesNumber=statementElement.getAttribute("dependencies");
						System.out.println(" Node name OR statement Name = " +statementName);
						xmlContent.write(packageName,ClassName, methodName, methodScore, statementName, statementLineNumber, statementDependenciesNumber, (Id++), m);
						nodeNum++;
						m++;
					}
				}
			}
		}
	}

}
	catch(Exception ex){
		
	}
}
}
