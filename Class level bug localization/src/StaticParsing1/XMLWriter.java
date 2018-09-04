package StaticParsing1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLWriter {

	Document doc;
	Element rootElement, packageElement, classElement, methodElement; 
	ArrayList<String> Methodlist;
	ArrayList<String> Classlist ;

	private void createRoot() {
		// TODO Auto-generated method stub
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("Source_Code");
			doc.appendChild(rootElement);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createPackage(String packageName) throws ParserConfigurationException{
		packageElement = doc.createElement("Package");
		rootElement.appendChild(packageElement);
		
		Attr attr = doc.createAttribute("name");
		attr.setValue(packageName);
		packageElement.setAttributeNode(attr);
		Packagelist.add(packageName);
		Classlist = new ArrayList<>();
	}
	
	ArrayList<String> Packagelist = new ArrayList<>();
	
	
	public void ClassCreation(String className){
			classElement= doc.createElement("Class");
			packageElement.appendChild(classElement);
			
			Attr attrClass=doc.createAttribute("name");
			attrClass.setValue(className);
			classElement.setAttributeNode(attrClass);
			Classlist .add(className);
			Methodlist = new ArrayList<>();
	}
	public void MethodCreation(String methodName, double methodScore){
		methodElement= doc.createElement("Method");
		classElement.appendChild(methodElement);
		
		Attr attrClass=doc.createAttribute("name");
		attrClass.setValue(methodName);
		methodElement.setAttributeNode(attrClass);
		
		Attr attrMethodScore=doc.createAttribute("score");
		String methodScoreStr=+ methodScore +"";
		attrMethodScore.setValue(methodScoreStr);
		methodElement.setAttributeNode(attrMethodScore);
		
		
		Methodlist.add(methodName);
		
}
	
	public void write(String packageName, String className, String methodName, double methodScore,  String[] nodeNames, int nodeNamesCount, int[] nodeNamesLine, Map<String, List<String>> hashdependenciesLine, int iCount, int m) {
		try {
			if(m==0)
			{
				createRoot();
			}
			if(!Packagelist.contains(packageName)){
				createPackage(packageName);
			}
				
			if(!Classlist .contains(className)){
			ClassCreation(className);
			System.out.println("className =" + className);
			}
			
			if(!Methodlist.contains(methodName)){
				MethodCreation(methodName, methodScore);
				System.out.println("methodName =" + methodName);
				}	
	        		for(int x=0;x<nodeNamesCount;x++)
	        		{
		        		Element expressionElement= doc.createElement("Node");
		        		methodElement.appendChild(expressionElement);
	        				Attr attrNode=doc.createAttribute("name");
	        				attrNode.setValue(nodeNames[x]);
	        				expressionElement.setAttributeNode(attrNode);
	        			
	        				Attr attrNodeLine=doc.createAttribute("lineNo");
	        				//String lineNum= methodLineNumber[i].;
	        				attrNodeLine.setValue(String.valueOf(nodeNamesLine[x]));
	        				expressionElement.setAttributeNode(attrNodeLine);
	        				
	        				Attr attrNodedependency=doc.createAttribute("dependencies");
	        				List<String> dependencyList = hashdependenciesLine.get(nodeNames[x]);
	        				String dependent="";
	        				if(dependencyList!=null){
	        					for (String dependency: dependencyList) {
	        					String dependenciesValue=dependency;
	        					dependent=dependent+ dependenciesValue +",";
	        					}
	        				}
	        				if(dependent!="")
	        					dependent=	dependent.substring(0,dependent.length()-1);
	        				
	        				attrNodedependency.setValue(dependent);
	        				expressionElement.setAttributeNode(attrNodedependency);
	        		}
	       			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("..\\class_Research\\src\\StaticParsing1\\1_BankStaticDataDependency.xml"));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
	 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
		}

}
