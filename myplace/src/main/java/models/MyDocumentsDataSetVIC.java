package models;

/**
 * Created by Ashish.Kumar on 26-10-2016.
 */
public class MyDocumentsDataSetVIC {
    String title,name,extension,uploadedOn,path;
    public MyDocumentsDataSetVIC(String title, String name, String extension, String uploadedOn, String path)
    {
        this.title=title;
        this.name=name;
        this.extension=extension;
        this.uploadedOn=uploadedOn;
        this.path=path;
    }
    public String getTitle(){return title;}
    public String getName(){return name;}
    public String getExtension(){return extension;}
    public String getUploadedOn(){return uploadedOn;}
    public String getPath(){return path;}

}
