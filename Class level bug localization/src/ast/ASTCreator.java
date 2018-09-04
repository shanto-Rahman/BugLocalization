package ast;

import java.io.*;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTCreator
{

    private String content;

    public ASTCreator()
    {
        content = null;
    }

    public void getFileContent(File file)
    {
        getFileContent(file.getAbsolutePath());
    }

    public void getFileContent(String absoluteFilePath)
    {
        try
        {
            StringBuffer contentBuffer = new StringBuffer();
            String line = null;
            BufferedReader reader = new BufferedReader(new FileReader(absoluteFilePath));
            while((line = reader.readLine()) != null) 
            {
                contentBuffer.append((new StringBuilder(String.valueOf(line))).append("\r\n").toString());
            }
            content = contentBuffer.toString();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public CompilationUnit getCompilationUnit()
    {
        ASTParser parser = ASTParser.newParser(3);
        parser.setSource(content.toCharArray());
        CompilationUnit cu = (CompilationUnit)parser.createAST(null);
        return cu;
    }
}
