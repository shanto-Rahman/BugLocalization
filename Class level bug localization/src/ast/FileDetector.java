package ast;

import java.io.File;
import java.util.LinkedList;

public class FileDetector
{

    private LinkedList fileList;
    private String fileType;

    public FileDetector()
    {
        fileList = new LinkedList();
        fileType = null;
    }

    public FileDetector(String fileType)
    {
        fileList = new LinkedList();
        this.fileType = null;
        this.fileType = fileType;
    }

    private File[] listFiles(String absoluteFilePath)
    {
        File dir = new File(absoluteFilePath);
        return dir.listFiles();
    }

    public File[] detect(String absoluteFilePath)
    {
        File files[] = listFiles(absoluteFilePath);
        if(files != null)
        {
            classifyFileAndDirectory(files);
        }
        return (File[])fileList.toArray(new File[fileList.size()]);
    }

    private void classifyFileAndDirectory(File files[])
    {
        File afile[];
        int j = (afile = files).length;
        for(int i = 0; i < j; i++)
        {
            File file = afile[i];
            if(file.isDirectory())
            {
                detect(file.getAbsolutePath());
            } else
            {
                addFile(file);
            }
        }

    }

    private void addFile(File file)
    {
        if(fileType == null)
        {
            fileList.add(file);
        } else
        {
            addFileBySuffix(file);
        }
    }

    private void addFileBySuffix(File file)
    {
        if(file.getName().endsWith(fileType))
        {
            fileList.addLast(file);
        }
    }
}
