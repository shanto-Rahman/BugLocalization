package StaticParsing1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ast.FileDetector;

public class MyMain {
	
	static String ASTclassName, ASTpackageName,ASTmethodName;
	static String[] nodeNames = new String[10000];
	static int[] nodeNamesLine = new int[10000];
	//static boolean exist=false;
	static int iCount, nodeNameI;
	static int lineI, m=0;
	//static boolean flagNotAssignment=false;
	static Map<String, List <String>> hashdependenciesLine = new HashMap<String, List <String>>();
	static XPath xPath ;
	static Document xmlDocument;
	static HashMap<String ,HashMap<String, HashMap<String, Double>>> codeStruct = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
	static HashMap<String, HashMap <String, Double>> ClasswithIdParentCount;
	static HashMap<String, Double> IdwithParentCount;
	static int totalMethodCount=0;
//	static int flag=0;
	
	public static void parse(String str){
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		
	
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
	//String hello= "sfdgdffh \\fnvjfbjk nm, bnjkh"
		/*
		for (Comment comment : (List<Comment>) cu.getCommentList()) {

		    comment.accept(new CommentVisitor(cu, classSource.split("\n")));
		}
		*/
		
		ASTpackageName=cu.getPackage().getName().getFullyQualifiedName();
		//System.out.println("packageName = " +ASTpackageName);
		lineI=0;
		
		//ignoreNodeNameI=0;
		cu.accept(new ASTVisitor() {
			HashMap<String, Integer> variableNamesHash = new HashMap<String, Integer>();
			//int m_nStatementCount;
			List<String> dependencyList ;
			public boolean visit(MethodDeclaration md) {
				totalMethodCount++;
				nodeNameI=0;
				//System.out.println("************START "+ md.getName().getFullyQualifiedName()+"**************");
				ASTmethodName= md.getName().getFullyQualifiedName();
				//exist= checkXMLwithAST(ASTpackageName, ASTclassName, ASTmethodName);
				//if(exist)
				//{
					dependencyList = new ArrayList<String>();
				//}
				//String content= md.getBody().toString();
				//System.err.println("CCCCCOntent "+ content);
				return true;
			}
			
			public boolean visit(BlockComment node) {
				  int startLineNumber = cu.getLineNumber(node.getStartPosition()) - 1;
			        int endLineNumber = cu.getLineNumber(node.getStartPosition() + node.getLength()) - 1;

			        StringBuffer blockComment = new StringBuffer();

			        for (int lineCount = startLineNumber ; lineCount<= endLineNumber; lineCount++) {

			            int blockCommentLine = node.getStartPosition();
			            //System.out.println("blockCommentLine "+ blockCommentLine);
			        }

			        //System.out.println(blockComment.toString());

			        return true;
			}
			
			public boolean visit(VariableDeclarationFragment node) {
				
				//exist= checkXMLwithAST(ASTpackageName, ASTclassName, ASTmethodName);				
					SimpleName name = node.getName();
					String exp  =  node.getParent().toString();
					nodeNames[nodeNameI]=exp;
					nodeNamesLine[nodeNameI]=cu.getLineNumber(name.getStartPosition());				
					nodeNameI++;
					String usage=node.toString();
					if(usage.contains("="))
					{
						String[] parts = usage.split("\\="); // escape .
						String leftSide = parts[0];
						variableNamesHash.put(leftSide, cu.getLineNumber(node.getStartPosition()));
						hashdependenciesLine.put(exp,null);
					}				
				return false; // do not continue
				
			}
			// the visitors below increment the statement count field
			public boolean visit (ReturnStatement node) {
				//m_nStatementCount++;
					String usage=node.toString();
					String[] partUsage = usage.split(" ");
					//String exp= node.getExpression().toString();
					if(usage!=null && usage!="" && partUsage.length>1 ){
						nodeNames[nodeNameI]="";
						for(int pu=1; pu < partUsage.length; pu++){
						
							nodeNames[nodeNameI]=nodeNames[nodeNameI]+partUsage[pu]+" ";
						}
						nodeNamesLine[nodeNameI]=cu.getLineNumber(node.getStartPosition());				
						nodeNameI++;	
					}				
				return true;
			}
			public boolean visit (SwitchStatement node) {
					String exp= node.getExpression().toString();
					nodeNames[nodeNameI]=exp;
					nodeNamesLine[nodeNameI]=cu.getLineNumber(node.getStartPosition());				
					nodeNameI++;
					String usage=node.toString();				
				return true;
			} 
			public boolean visit (ExpressionStatement node) {				
				String exp= node.getExpression().toString();
				nodeNames[nodeNameI]=exp;
				nodeNamesLine[nodeNameI]=cu.getLineNumber(node.getStartPosition());				
				nodeNameI++;
				String usage=node.toString();
				//System.err.println("Usage ExpressionStatement ="+ usage+ "line Number = "+ +cu.getLineNumber(node.getStartPosition()));
				
				 List <String> secendaryStorage =new ArrayList<>();
				//Dependencies find
				// Nich portion kora hoyeche to ignore the effect of book.open()
				 dependencyList = new ArrayList<String>();
				// System.out.println( "SHHHHHH " +exp);
				 if(exp.contains("()"))
					{
					 exp=	exp.replaceAll("()", " ");
					}
					if(exp.contains("="))
					{
						String[] parts = exp.split("\\="); // escape .
						String leftSide = parts[0];
						String rightSide = parts[1];
						
						if(rightSide.contains("."))
						{
							String[] rightSideDotParts = exp.split("\\."); // escape .
							for(int l1=0;l1<rightSideDotParts.length;l1++){
								String term=rightSideDotParts[l1];							
								for (Map.Entry<String, Integer> statementEntry : variableNamesHash.entrySet()) {
									String NodeName = statementEntry.getKey();
									int NodeLineNumber = statementEntry.getValue();
									if((!secendaryStorage.equals(NodeName))&& term.equals(NodeName))
									{
										dependencyList.add(NodeLineNumber+",");
										secendaryStorage.add(NodeName);
									}
								}		
							}
						}
						else
						{
							for (Map.Entry<String, Integer> statementEntry : variableNamesHash.entrySet()) {
								String NodeName = statementEntry.getKey();
								int NodeLineNumber = statementEntry.getValue();
									if((!secendaryStorage.equals(NodeName))&& rightSide.equals(NodeName))
									{
										dependencyList.add(NodeLineNumber+",");
										secendaryStorage.add(NodeName);
									}
							}
						}
						
						if(!(leftSide.contains(".")))
						{
							variableNamesHash.put(leftSide, cu.getLineNumber(node.getStartPosition()));
						}
						else{
							String[] leftSideDotParts = leftSide.split("\\."); 
							String	variableValue=leftSideDotParts[(leftSideDotParts.length-1)];
							variableNamesHash.put(variableValue, cu.getLineNumber(node.getStartPosition()));
						}
						hashdependenciesLine.put(exp,dependencyList);
					}
					
					else
					{
						//String usageLeft;
						if(usage.contains("."))
						{
							String[] usageParts = exp.split("\\."); // escape .
							usage= usageParts[0];
							//System.out.println("usagesssssssssss "+usage);
						}
						for (Map.Entry<String, Integer> statementEntry : variableNamesHash.entrySet()) {
						String NodeName = statementEntry.getKey();
						int NodeLineNumber = statementEntry.getValue();
							if((!secendaryStorage.equals(NodeName))&& usage.equals(NodeName))
							//if(usage.equals(NodeName))
							{
							//	System.out.println("YYYYYYY usage = "+usage + " NodeName = "+NodeName + " lineNumber = "+NodeLineNumber);
								dependencyList.add(NodeLineNumber+"");
								secendaryStorage.add(NodeName);
							}
						}
						hashdependenciesLine.put(exp,dependencyList);
					}				
				return true;
			}
			 
			public boolean visit (IfStatement node) {
					String exp= node.getExpression().toString();
					nodeNames[nodeNameI]=exp;
					nodeNamesLine[nodeNameI]=cu.getLineNumber(node.getStartPosition());				
					nodeNameI++;				
				return true;
			}
			
			public void endVisit(MethodDeclaration md) {				
				//	System.err.println("************END "+ md.getName().getFullyQualifiedName()+"**************");
					//System.out.println();
					callXML();				
			}
		});
	}
		
	public static void callXML() {		 
			 double score=0.0;
				for (Map.Entry<String, HashMap<String, HashMap<String, Double>>> packageParentEntry : codeStruct.entrySet()) {
					String packageName = packageParentEntry.getKey();
					for (Map.Entry<String, HashMap<String, Double>> classEntry : packageParentEntry.getValue().entrySet()) {
						String className = classEntry.getKey();
						for (Map.Entry<String, Double> statementEntry : classEntry.getValue().entrySet()) {
							String methodName = statementEntry.getKey();
							double methodScore = statementEntry.getValue();
							
							if(packageName.equals(ASTpackageName) && className.equals(ASTclassName) && methodName.equals(ASTmethodName))
							{
								score=methodScore;
								break;
								//System.out.println("Matched");
							//	return true;
								
							}	
							//System.err.println(" Parrent Package Name = "+ packageName + " className  =" +className+ " methodName=  "+methodName +" methodScore "+ methodScore);
					}
				}
			}
			 xmlWriter.write(ASTpackageName, ASTclassName, ASTmethodName, score, nodeNames, nodeNameI, nodeNamesLine,hashdependenciesLine, iCount++, m);
			 m++;
	}		
	 
	//read file content into a string
	public static String readFileToString(File filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
		//	System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return  fileData.toString();	
	} 
	//loop directory to get file list
	 static XMLWriter xmlWriter;
	public static void ParseFilesInDir() throws IOException, ParserConfigurationException, SAXException{		
		FileDetector detector = new FileDetector("java");
		//File files[] = detector.detect("E:\\BankProject\\BankProject\\src\\bank");// Bug 
       File files[] = detector.detect("E:\\Research\\Eclipse_Code\\Feroz_project\\src\\sudoku");//;Property.getInstance().getSourceCodeDir());
		//String filePath = null;
		File afile[];
        int j = (afile = files).length;
        xmlWriter =new XMLWriter();
        for(int i = 0; i < j; i++)
        {
            File file = afile[i];
			 if(file.isFile()){
				 String fileName=file.getName();
				 String[] fn = fileName.split("\\.");
				 ASTclassName=fn[0];
				 parse(readFileToString(file));
				// System.out.println(" fileName "+fileName);
			 }
		 }
	}
 
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		//System.out.println("Ohh");
		//rankedMethodXMLRead();
	//	xmlPrint();
		ParseFilesInDir();
		System.out.println("Total method = " +totalMethodCount);
	} 

}
