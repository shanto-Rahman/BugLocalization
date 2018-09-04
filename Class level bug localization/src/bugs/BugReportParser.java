package bugs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import ast.FileDetector;
import utils.Stem;
import utils.Stopword;

public class BugReportParser {

	Document doc;
	Element rootElement;

	public BugReportParser() throws IOException, ParserConfigurationException {
		// TODO Auto-generated constructor stub

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.newDocument();

		rootElement = doc.createElement("Bugs");
		doc.appendChild(rootElement);
		// root elements
	}

	public void getBugReport() throws IOException,
			ParserConfigurationException, TransformerException {
		
		FileDetector detector = new FileDetector("txt");
        File files[] = detector.detect("E:\\Research\\Eclipse_Code\\class_Research\\src\\bugs");//;Property.getInstance().getSourceCodeDir());
        File afile[];
        int j = (afile = files).length;
        for(int i = 0; i < j; i++)
        {
            File file = afile[i];
            if(file.isFile()){
				 
				 String fileName=file.getName();
				 String filePath = file.getCanonicalPath();
				 String[] fn = fileName.split("\\.");
				 String bugId=fn[0];
				 BufferedReader br = new BufferedReader(new FileReader(filePath));
				 try {
					 StringBuilder sb = new StringBuilder();
					 String line = br.readLine();
					 //String[] corpus = new String[1000];
					 List<String> corpus = new ArrayList<String>(); 

					 int ln = 1;
					 while (line != null) {

						 line = line.toLowerCase();
						 String quires[] = line.split(" ");
						 corpus = removeStopWord(quires);
						 // System.out.println(corpus.toString());
						 
						 
						 createBugXML(corpus, bugId);
						 line = br.readLine();
						 ln++;
					 }

				 }finally {
					 br.close();
				 }
            }
        }
        try {
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
					"..\\class_Research\\src\\bugs\\BugReport.xml"));

			transformer.transform(source, result);
		} catch (Exception e) {
			// TODO: handle exception
		}

		System.out.println("File saved!");

	}

	private void createBugXML(List<String> corpus, String bugId) {
		// TODO Auto-generated method stub
		try {
			Element node = doc.createElement("Bug");
			rootElement.appendChild(node);
			// set attribute to class element
			Attr attr = doc.createAttribute("id");
			attr.setValue(bugId);
			node.setAttributeNode(attr);

			for (int l = 0; l < corpus.size(); l++) {

				if (corpus.get(l) != null) {

					addWord(doc, node, corpus.get(l), String.valueOf(l + 1));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void addWord(Document doc, Element node, String word, String id) {
		// TODO Auto-generated method stub
		Element childNode = doc.createElement("Word");
		Attr attr = doc.createAttribute("content");
		attr.setValue(word);
		childNode.setAttributeNode(attr);
		node.appendChild(childNode);
	}

	private List<String> removeStopWord(String[] queries) {
		// TODO Auto-generated method stub
		int queryLength = queries.length;
		List<String> corpusArray = new ArrayList<String>();
		//int j = 0;
		for (int i = 0; i < queryLength; i++) {
			String word = queries[i];
			//Including semantic meanings
		List<String> semanticWordList=	semanticMeaningExtraction(word);
		 for(int j=0;j<semanticWordList.size();j++)
		 {
				word = Stem.stem(semanticWordList.get(j));
				if (!Stopword.isEnglishStopword(word)) {
					corpusArray.add(word);
				}
		}
			//Excluding semantic meanings
		/*word = Stem.stem(word);
		if (!Stopword.isEnglishStopword(word)) {
			corpusArray.add(word);	
		}*/	 
	}
	return corpusArray;
}
	
	
	private List<String> semanticMeaningExtraction(String word) {
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
		if(value.contains("."))
		{
			value=	value.replace(".","");
		}
		
		
		Synset[] synsets = database.getSynsets(value);
		List <String> synonymWords= new ArrayList<String>();
		// Display the word forms and definitions for synsets retrieved
		if (synsets.length > 0) {
			System.out.println("The following synsets contain '" + value );
			System.out.println("");
			String[] wordForms = synsets[0].getWordForms();
			for (int j = 0; j < wordForms.length; j++) {
				System.out.print((j > 0 ? ", " : "") + wordForms[j]);
				synonymWords.add(wordForms[j]);
				//tempMethodVocabulary.add(wordForms[j]);
				//if(!wordForms[j].contains(" "))
				/*{
				//	wordAddingIntoNode(wordForms[j]);
				}
				else {
					System.out.println("Multiple word");
				}*/
			}
		} else {
			System.err.println("No synsets exist that contain "
					+ "the word form '" + value + "'");
			synonymWords.add(value);
			//synonymWords.add(wordForms[j]);
			//if(!value.contains(" "))
			//wordAddingIntoNode(value);
		}
		return synonymWords; 
		
	}

}
