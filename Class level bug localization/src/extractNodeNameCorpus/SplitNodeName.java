package extractNodeNameCorpus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import utils.Stem;
import utils.Stopword;

public class SplitNodeName {
	ArrayList<String> XpckgList= new ArrayList<>();
	ArrayList<String> XclassList= new ArrayList<>();
	ArrayList<String> XmethodList= new ArrayList<>();
	Document doc;
	Element rootElement;
	Element packageElement, classElement, statementElement;
	Element expressionElement;

	private void createRoot() throws ParserConfigurationException {
		// TODO Auto-generated method stub
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.newDocument();
		rootElement = doc.createElement("Source_Code");
		doc.appendChild(rootElement);
	}
	private void createPackage(String packageName){
		// TODO Auto-generated method stub
		packageElement = doc.createElement("Package");
		rootElement.appendChild(packageElement);
		
		Attr attrPackage = doc.createAttribute("name");
		attrPackage.setValue(packageName);
		packageElement.setAttributeNode(attrPackage);
		XpckgList.add(packageName);
		XclassList = new ArrayList<>();
	}

	private void createClass(String className) {
		// TODO Auto-generated method stub
		classElement = doc.createElement("Class");
		packageElement.appendChild(classElement);

		Attr attrClass = doc.createAttribute("name");
		attrClass.setValue(className);
		classElement.setAttributeNode(attrClass);
		XclassList.add(className);
		XmethodList=new ArrayList<>();
	}

	private void createMethod(String methodName, String methodScore) {
		// TODO Auto-generated method stub
		Element methodElement= doc.createElement("Method");
		classElement.appendChild(methodElement);
		
		Attr attrMethod=doc.createAttribute("name");
		attrMethod.setValue(methodName);
		methodElement.setAttributeNode(attrMethod);
		XmethodList.add(methodName);
	}
	
	private void wordPreProcess(String statementName, int i) {
		String validStatementWord = statementName.replaceAll("[{}.(\");=]", " ");
		validStatementWord = validStatementWord.trim().replaceAll(" +", " ");
		System.out.println("Result After Replace " + validStatementWord);
		String[] contentArray = validStatementWord.split(" ");
		int len = contentArray.length;
		String[] corpusArray = new String[1000];
		for (int i1 = 0; i1 < len; i1++) {
			String word = contentArray[i1];
			String wordFinal="";
			// MultiWordIdentifier
			if (!word.isEmpty() && (!word.equals("\n"))) {
				String[] splitWord = word.split("(?=\\p{Upper})");
				if (splitWord.length > 1) {
					for (int j = 0; j < splitWord.length; j++) {
						if (!splitWord[j].isEmpty() && (!splitWord[j].equals("\n"))) {
							word = Stem.stem(splitWord[j]);
							word = word.toLowerCase();
							if (!Stopword.isEnglishStopword(word)) {
								corpusArray[j] = word;
								wordFinal= corpusArray[j];
								System.err.println("Split Corpus "+ corpusArray[j]);	
								semanticMeaningExtraction(corpusArray[j]);
								}
						}
					}
				}
				else
				{
					word = word.toLowerCase();
					word = Stem.stem(word);
					if (!Stopword.isEnglishStopword(word)) {
						corpusArray[i] = word;
						wordFinal= corpusArray[i];
						System.out.println("Corpus "+ corpusArray[i]);
						semanticMeaningExtraction(corpusArray[i]);
					}
				}
			}
		}
	}
	
	private void semanticMeaningExtraction(String word) {
		// TODO Auto-generated method stub
		System.setProperty("wordnet.database.dir", "dict");
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		String value=word;
		
		if(value.contains(";"))
		{
			value=	value.replace(";"," ");
		}
		if(value.contains(","))
		{
			value=	value.replace(","," ");
		}
				
		if(value.contains(">"))
		{
			value=	value.replace(">"," ");
		}
		
		if(value.contains("*"))
		{
			value=	value.replace("*"," ");
		}
		
		if(value.contains("<"))
		{
			value=	value.replace("<"," ");
		}
		
		if(value.contains("string"))
		{
			value=	value.replace("string"," ");
		}
		
		
		if(value.contains("&#10"))
		{
			value=	value.replace("&#10"," ");
		}
		
		if(value.contains("&"))
		{
			value=	value.replace("&"," ");
		}
		if(value.contains("#"))
		{
			value=	value.replace("#"," ");
		}
		
		
		
		Synset[] synsets = database.getSynsets(value);
		// Display the word forms and definitions for synsets retrieved
		if (synsets.length > 0) {
			System.out.println("The following synsets contain '" + value );
			System.out.println("");
			String[] wordForms = synsets[0].getWordForms();
			for (int j = 0; j < wordForms.length; j++) {
				System.out.print((j > 0 ? ", " : "") + wordForms[j]);
				//tempMethodVocabulary.add(wordForms[j]);
				if(!wordForms[j].contains(" "))
				{
					wordAddingIntoNode(wordForms[j]);
				}
				else {
					System.out.println("Multiple word");
				}
			}
		} else {
			System.err.println("No synsets exist that contain "
					+ "the word form '" + value + "'");
			if(!value.contains(" "))
			wordAddingIntoNode(value);
		}
		
	}

private void wordAddingIntoNode(String wordName){
		Element corpusElement= doc.createElement("Word");
		expressionElement.appendChild(corpusElement);
		Attr attrNodeWord=doc.createAttribute("content");
		String corpusArrayElement =wordName;
		
		attrNodeWord.setValue(corpusArrayElement);
		corpusElement.setAttributeNode(attrNodeWord);
	}
	
	public void write(String packageName, String className, String methodName, String methodScore, String statementName, String statementLineNumber, String statementDependenciesNumber, int nodeId, int m) {
		try {
			//boolean flag=false;
			if(m==0)
			{
				createRoot();
			}
			if (!XpckgList.contains(packageName)) {
				createPackage(packageName);
			}
			if (!XclassList.contains(className)) {
				//flag=true;
				createClass(className);
			}
			
			NodeList classNodes = doc.getElementsByTagName("Class");
			// Class ar vitor Node and Node ar vitor pre-processed word
			
			for (int i = 0; i < classNodes.getLength(); i++) 
			{
				System.out.println("Class length = "+classNodes.getLength());
				Element classElement1 = (Element) classNodes.item(i);
	        	String name =classElement1.getAttributes().getNamedItem("name").getNodeValue();
	        	if(name.equals(className)){
	        		
	        		//if(!flag)
	        			
		        		if(!XmethodList.contains(methodName))
		        		{
		        			createMethod(methodName, methodScore);
		        		}
	        	}
			}
	    			NodeList methodNodes = doc.getElementsByTagName("Method");
	        		System.out.println("methodNodes. legngth = "+ methodNodes.getLength());
		    			for (int im = 0; im < methodNodes.getLength(); im++)		
		    			{	
		    				Element methodElement1 = (Element) methodNodes.item(im);
		    	        	String mName =methodElement1.getAttributes().getNamedItem("name").getNodeValue();
		    	        
		    	        	if(mName.equals(methodName))
		    	        	{
			        			expressionElement= doc.createElement("Node");
				        		methodElement1.appendChild(expressionElement);
		        				Attr attrNode=doc.createAttribute("Id");
		        				String stringNodeId =nodeId +"";
		        				attrNode.setValue(stringNodeId);
		        				expressionElement.setAttributeNode(attrNode);
		        				// Word pre-processing 
		        				wordPreProcess(statementName, im);
		    	        	}
		        	}
	        	
			
			
			//removeStopWord(statementName,statementDependenciesNumber, statementLineNumber);
			
			//CreateMethodAndContent(methodName, methodContent);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(
					"..\\class_Research\\src\\extractNodeNameCorpus\\1_BankTreeNode.xml"));

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}


}
