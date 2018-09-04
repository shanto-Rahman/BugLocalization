package common_term_Finding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;
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
static File dataSourceCodeFile;
static Document dataDoc;
static XPath xPath;
static int Id=1;
public static void main(String[] args) {
	try {
		// For finding reelation with method
	
		HashMap<String, HashMap<String, Integer>> totalPackageWord = new HashMap<String, HashMap<String, Integer>>();
		
		List<String> bugWordList= new ArrayList<String>();
		File buggyFile = new File("..\\class_Research\\src\\bugs\\BugReport.xml");
		DocumentBuilderFactory buggyFileDbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder buggyFileDBuilder = buggyFileDbFactory.newDocumentBuilder();
		Document dataDocBuggyFile = buggyFileDBuilder.parse(buggyFile);
		dataDocBuggyFile.getDocumentElement().normalize();
		XPath xPathBuggyFile = XPathFactory.newInstance().newXPath();
		//NodeList Bu = dataDoc.getElementsByTagName("Bugs");
		String bugExpression = "/Bugs/Bug[contains(@id,'"+ "1" + "')]/Word";
		NodeList bugNodeList = (NodeList) xPathBuggyFile.compile(bugExpression).evaluate(dataDocBuggyFile, XPathConstants.NODESET);
		for(int iw=0;iw<bugNodeList.getLength();iw++)
		{
			Node wordNode = bugNodeList.item(iw);
			Element WordNodeElement = (Element) wordNode;
			String WordNodeName = WordNodeElement.getAttribute("content");	
			//System.out.println("Bug Terms = "+WordNodeName);
			bugWordList.add(WordNodeName);
		}
		
		HashMap<String, HashMap<String, Integer> > packageRelMap = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<String>>>>> packageClasHash =new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<String>>>>>();
		dataSourceCodeFile = new File("..\\class_Research\\src\\extractNodeNameCorpus\\1_BankTreeNode.xml");
		DocumentBuilderFactory dataDependencyDbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dataDependencyDBuilder = dataDependencyDbFactory.newDocumentBuilder();
		dataDoc = dataDependencyDBuilder.parse(dataSourceCodeFile);
		dataDoc.getDocumentElement().normalize();
		xPath = XPathFactory.newInstance().newXPath();
		
		NodeList dataDependencyPackageList = dataDoc.getElementsByTagName("Package");
		HashMap<String, HashMap<String, HashMap<String, Integer>>> totalPackageWordM = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
		HashMap<String, HashMap<String, HashMap<String, Integer>>> PackageWordM = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
		
		for (int temp = 0; temp < dataDependencyPackageList.getLength(); temp++) {
			Node PackageNode = dataDependencyPackageList.item(temp);
			if (PackageNode.getNodeType() == Node.ELEMENT_NODE) {
				Element PackageElement = (Element) PackageNode;
				String packageName = PackageElement.getAttribute("name");
				String ClassExpression = "/Source_Code/Package[@name='"+ packageName + "']/Class";
				NodeList ClassNodeList = (NodeList) xPath.compile(ClassExpression).evaluate(dataDoc, XPathConstants.NODESET);
			//	System.out.println("Class Node list " +ClassNodeList.getLength());
				HashMap<String, HashMap<String, HashMap<String, List<String>>>> classMethodHash =new HashMap<String, HashMap<String, HashMap<String, List<String>>>>();
				//finding relation with method
				HashMap<String, HashMap<String, Integer>> totalclassWordM = new HashMap<String, HashMap<String, Integer>>();
				HashMap<String, HashMap<String, Integer>> classWordM = new HashMap<String, HashMap<String, Integer>>();
				//Only For finding relation with class
				HashMap<String, Integer> classRelMap= new HashMap<String, Integer>();
				HashMap<String, Integer> totalClassWord = new HashMap<String, Integer>();
				for (int i = 0; i < ClassNodeList.getLength(); i++) {
					int classTotalWord=0;
					int classWordRelCount=0;
					List<String> NodeList = new ArrayList<String>();
					Node classNode = ClassNodeList.item(i);
					Element classElement = (Element) classNode;
					String ClassName = classElement.getAttribute("name");				
					String methodExpression = "/Source_Code/Package[@name='"+ packageName + "']/Class[@name='"+ ClassName + "']/Method";
					NodeList MethodList = (NodeList) xPath.compile(methodExpression).evaluate(dataDoc, XPathConstants.NODESET);
					//System.out.println(" MethodList "+MethodList);
					HashMap<String, HashMap<String, List<String>>> methodNodeHash =new HashMap<String, HashMap<String, List<String>>>();
					HashMap<String, Integer> methodRelMap= new HashMap<String, Integer>();
					HashMap<String, Integer> totalMethodWordM = new HashMap<String, Integer>();
					
					for(int iM=0;iM<MethodList.getLength();iM++){
						
						int methodTotalWord=0;
						int methodWordRelCount=0;
						Node methodNode = MethodList.item(iM);
						Element methodElement = (Element) methodNode;
						String methodName = methodElement.getAttribute("name");		
						//String methodScore= methodElement.getAttribute("score");
						
					String statementExpression = "/Source_Code/Package[@name='"+ packageName + "']/Class[@name='"+ ClassName + "']/Method[@name='"+ methodName + "']/Node"; 
					NodeList statementNodeList = (NodeList) xPath.compile(statementExpression).evaluate(dataDoc, XPathConstants.NODESET);
					//System.out.println(statementNodeList.getLength());
					HashMap<String, List<String>> nodeIdWordHash =new HashMap<String, List<String>>();
					
					for (int iS = 0; iS < statementNodeList.getLength(); iS++) {
						Node statementNode = statementNodeList.item(iS);
						Element nodeElement = (Element) statementNode;
						String nodeId = nodeElement.getAttribute("Id");						
						List<String> codeWordList= new ArrayList<String>();
					//	nodeId.equals(arg0);
						
						String WordExpression = "/Source_Code/Package[@name='"+ packageName + "']/Class[@name='"+ ClassName + "']/Method[@name='"+ methodName + "']/Node[@Id='"+ nodeId + "']/Word"; 
						NodeList WordNodeList = (NodeList) xPath.compile(WordExpression).evaluate(dataDoc, XPathConstants.NODESET);
						for(int iw=0;iw<WordNodeList.getLength();iw++)
						{
							classTotalWord++;
							methodTotalWord++;
							Node wordNode = WordNodeList.item(iw);
							Element WordNodeElement = (Element) wordNode;
							String WordNodeName = WordNodeElement.getAttribute("content");
								int count=	compareWord(WordNodeName,bugWordList);
								if(count>0)
								{
									classWordRelCount+=count;
									methodWordRelCount+=count;
								}														
							codeWordList.add(WordNodeName);
						}						
						//System.out.println(" Node name OR statement Name = " +nodeId);
						nodeIdWordHash.put(nodeId, codeWordList);
					}
					methodNodeHash.put(methodName, nodeIdWordHash);
					// For finding reelation with method
					methodRelMap.put(methodName, methodWordRelCount);
					totalMethodWordM.put(methodName, methodTotalWord);
				}
				classMethodHash.put(ClassName, methodNodeHash);
				classRelMap.put(ClassName, classWordRelCount);
				totalClassWord.put(ClassName, classTotalWord);
					
					// For finding reelation with method
				totalclassWordM.put(ClassName, totalMethodWordM);
				classWordM.put(ClassName, methodRelMap);		
			}
				packageClasHash.put(packageName, classMethodHash);
				packageRelMap.put(packageName, classRelMap);
				totalPackageWord.put(packageName, totalClassWord);
				// For finding relation with method
				totalPackageWordM.put(packageName, totalclassWordM);
				PackageWordM.put(packageName, classWordM);
		}		
	}
		
		printWordMatchClass(packageRelMap);
		printParentVSMSCount(packageClasHash, bugWordList);
		calulateClassPercentage(packageClasHash, bugWordList);
		calulateMethodPercentage(PackageWordM, totalPackageWordM);
		calculateStatementPercentage(packageClasHash,bugWordList);
	}
catch(Exception ex){
	
}
}

private static void calulateClassPercentage(
		HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<String>>>>> packageClasHash,
		List<String> bugWordList) {
	// TODO Auto-generated method stub
	for (Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, List<String>>>>> packageEntry : packageClasHash.entrySet()) {
		String packageName = packageEntry.getKey();
		for (Map.Entry<String, HashMap<String, HashMap<String, List<String>>>> classEntry : packageEntry.getValue().entrySet()) {
			int countSimilarTerms=0;
			double similarTermPercentage=0.0;
			int classWordCount=0;
			String className = classEntry.getKey();
			for (Map.Entry<String, HashMap<String, List<String>>> methodEntry : classEntry.getValue().entrySet()) {
				String methodName= methodEntry.getKey();
			for (Map.Entry<String, List<String>> statementEntry : methodEntry.getValue().entrySet()) {
				String NodeId = statementEntry.getKey();
				//int totalCount=0;
				
				List<String> wordList =statementEntry.getValue(); //new ArrayList<String>();
				classWordCount+=wordList.size();
					for(int i=0;i<wordList.size();i++)
					{
					//	totalCount++;
						String codeWord = wordList.get(i);	
						
						for(int iout=0;iout<bugWordList.size();iout++)
						{
							String bugWord= bugWordList.get(iout);	
							if(bugWord.equals(codeWord))
							{
								countSimilarTerms++;
							}
						}
					}				
				}
			}
			double similarTerms= (double) countSimilarTerms;
			int totalCount= classWordCount + bugWordList.size();
			double totalTerms= (double) totalCount;
			if(countSimilarTerms>0.0)
			{
				similarTermPercentage=similarTerms/totalTerms;
				if(similarTermPercentage>0.0){
					System.out.println(" Class Name = "+ className + " similarity score=  "  +similarTermPercentage +"%");
				}
			}
		}	
	}
}

private static void calculateStatementPercentage(HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<String>>>>> packageClasHash,
		List<String> bugWordList) {
	// TODO Auto-generated method stub
			for (Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, List<String>>>>> packageEntry : packageClasHash.entrySet()) {
				String packageName = packageEntry.getKey();
				for (Map.Entry<String, HashMap<String, HashMap<String, List<String>>>> classEntry : packageEntry.getValue().entrySet()) {
					String className = classEntry.getKey();
					for (Map.Entry<String, HashMap<String, List<String>>> methodEntry : classEntry.getValue().entrySet()) {
						String methodName= methodEntry.getKey();
					for (Map.Entry<String, List<String>> statementEntry : methodEntry.getValue().entrySet()) {
						String NodeId = statementEntry.getKey();
						//int totalCount=0;
						int countSimilarTerms=0;
						List<String> wordList =statementEntry.getValue(); //new ArrayList<String>();
						
						double similarTermPercentage=0.0;
							for(int i=0;i<wordList.size();i++)
							{
							//	totalCount++;
								String codeWord = wordList.get(i);	
								
								for(int iout=0;iout<bugWordList.size();iout++)
								{
									String bugWord= bugWordList.get(iout);	
									if(bugWord.equals(codeWord))
									{
										countSimilarTerms++;
									}
								}
							}
							double similarTerms= (double) countSimilarTerms;
							int totalCount= wordList.size() + bugWordList.size();
							double totalTerms= (double) totalCount;
							if(countSimilarTerms>0.0)
							{
								similarTermPercentage=similarTerms/totalTerms;
								if(similarTermPercentage>0.3){
									System.out.println(" Node Id = "+ NodeId + " similarity score=  "  +similarTermPercentage +"%");
								}
							}
						}
					}
				}	
			}
}

private static void calulateMethodPercentage(
		HashMap<String, HashMap<String, HashMap<String, Integer>>> packageWordM,
		HashMap<String, HashMap<String, HashMap<String, Integer>>> totalPackageWordM) {
	// TODO Auto-generated method stub
	for (Map.Entry<String, HashMap<String, HashMap<String, Integer>>> packageParentEntry : packageWordM.entrySet()) {
		String packageName = packageParentEntry.getKey();
		for (Map.Entry<String, HashMap<String, Integer>> classEntry : packageParentEntry.getValue().entrySet()) {
			String className = classEntry.getKey();
			for (Map.Entry<String, Integer> methodEntry : classEntry.getValue().entrySet()) {
				String methodName = methodEntry.getKey();
				Integer similarWordCount =methodEntry.getValue();
			for (Map.Entry<String, HashMap<String, HashMap<String, Integer>>> packageTotalEntry : totalPackageWordM.entrySet()) {
				String packageTotalName = packageTotalEntry.getKey();
				for (Map.Entry<String, HashMap<String, Integer>> classTotalEntry : packageTotalEntry.getValue().entrySet()) {
					String classTotalName = classTotalEntry.getKey();
					for (Map.Entry<String, Integer> methodTotalEntry : classTotalEntry.getValue().entrySet()) {
						String methodTotalName = methodTotalEntry.getKey();
						Integer totalWordCount =methodTotalEntry.getValue();
					
					if(packageName.equals(packageTotalName) && className.equals(classTotalName) && methodName.equals(methodTotalName))
					{
						double similarWord= (double) similarWordCount;
						double totalWord= (double) totalWordCount;
						double percentageSimilarity= (similarWord/totalWord)*100;
						System.out.println("Package = "+packageName + " ClassName= " +className + " MethodName = " + methodName + " Percentage  = "+ percentageSimilarity + "%");
					}
				}
			}			
		}				
			}
		}
	}
	
}



/*
private static void calulateClassPercentage(
		HashMap<String, HashMap<String, Integer>> packageRelMap,
		HashMap<String, HashMap<String, Integer>> totalPackageWord) {
	// TODO Auto-generated method stub
	
	for (Map.Entry<String, HashMap<String, Integer>> packageParentEntry : packageRelMap.entrySet()) {
		String packageName = packageParentEntry.getKey();
		for (Map.Entry<String, Integer> classEntry : packageParentEntry.getValue().entrySet()) {
			String className = classEntry.getKey();
			Integer similarWordCount =classEntry.getValue();
			for (Map.Entry<String, HashMap<String, Integer>> packageTotalEntry : totalPackageWord.entrySet()) {
				String packageTotalName = packageTotalEntry.getKey();
				for (Map.Entry<String, Integer> classTotalEntry : packageTotalEntry.getValue().entrySet()) {
					String classTotalName = classTotalEntry.getKey();
					Integer totalWordCount =classTotalEntry.getValue();
					
					if(packageName.equals(packageTotalName) && className.equals(classTotalName))
					{
						double similarWord= (double) similarWordCount;
						double totalWord= (double) totalWordCount;
						double percentageSimilarity= (similarWord/totalWord)*100;
						System.err.println("Package = "+packageName + " ClassName= " +className + " Percentage  = "+ percentageSimilarity + "%");
					}
				}
			}			
		}				
	}
}*/



private static void printWordMatchClass(HashMap<String, HashMap<String, Integer>> packageRelMap) {
	// TODO Auto-generated method stub
	for (Map.Entry<String, HashMap<String, Integer>> packageParentEntry : packageRelMap.entrySet()) {
		String packageName = packageParentEntry.getKey();
		for (Map.Entry<String, Integer> classEntry : packageParentEntry.getValue().entrySet()) {
			String className = classEntry.getKey();
			Integer wordCount =classEntry.getValue();
			System.err.println("Package = "+packageName + "Class= "+className + " Similar Words="+ wordCount);
					}				
				}
		}



private static int  compareWord(String wordNodeName, List<String> bugWordList) {
	// TODO Auto-generated method stub
	int countSimilarWord=0;
	for(int i=0;i<bugWordList.size();i++)
	{
		String bugWord = bugWordList.get(i);		
		//	System.err.println(" Parrent Package Name = "+ packageName + " className  =" +className+  " methodName ="+ methodName+ " NodeId=  "+NodeId +" word "+ word);
		if(wordNodeName.equals(bugWord))
		{
			//System.err.println(" Bug Word =" +bugWord + " Code Word = "+bugWord);
			countSimilarWord++;
		}
	}
	return countSimilarWord;
}



public static void printParentVSMSCount(HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<String>>>>> packageClasHash, List<String> bugWordList) {
// TODO Auto-generated method stub
	int count=0;
	for(int iout=0;iout<bugWordList.size();iout++)
	{
		String bugWord= bugWordList.get(iout);
			for (Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, List<String>>>>> packageParentEntry : packageClasHash.entrySet()) {
				String packageName = packageParentEntry.getKey();
				for (Map.Entry<String, HashMap<String, HashMap<String, List<String>>>> classEntry : packageParentEntry.getValue().entrySet()) {
					String className = classEntry.getKey();
					for (Map.Entry<String, HashMap<String, List<String>>> methodEntry : classEntry.getValue().entrySet()) {
						String methodName= methodEntry.getKey();
					for (Map.Entry<String, List<String>> statementEntry : methodEntry.getValue().entrySet()) {
						String NodeId = statementEntry.getKey();
						List<String> wordList =statementEntry.getValue(); //new ArrayList<String>();
						//System.out.println(" wordList = " +wordList.size());
							for(int i=0;i<wordList.size();i++)
							{
								String codeWord = wordList.get(i);		
								//	System.err.println(" Parrent Package Name = "+ packageName + " className  =" +className+  " methodName ="+ methodName+ " NodeId=  "+NodeId +" word "+ word);
								if(bugWord.equals(codeWord))
								{
								//	System.err.println(" Bug Word =" +bugWord + " Code Word = "+codeWord);
									count++;
								}
							}				
						}
					}
				}	
			}
		}
//	System.out.println( "Total count = "+count);
	}
}
