package models;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jaya.Krishna on 09-01-2018.
 */

public class ContactUsModel {
    int noteId;
    String subject, authorName, noteDate, body,displayDate;
    boolean byClient;


    public ContactUsModel(String result, boolean vic) {
        try {
            JSONObject job = new JSONObject(result);
            if (vic) {
                noteId = job.isNull("Id") ? 0 : job.getInt("Id");
                subject = job.isNull("Description") ? "" : job.getString("Description");
                authorName = job.isNull("AuthorName") ? "" : job.getString("AuthorName");
                noteDate = job.isNull("DateCreated") ? "" : job.getString("DateCreated");
                body = job.isNull("Notes") ? "" : job.getString("Notes");
                byClient = false;
            } else {
                noteId = job.isNull("noteid") ? 0 : job.getInt("noteid");
                subject = job.isNull("subject") ? "" : job.getString("subject");
                authorName = job.isNull("authorname") ? "" : job.getString("authorname");
                noteDate = job.isNull("notedate") ? "" : job.getString("notedate");
                body = job.isNull("body") ? "" : job.getString("body");
                byClient = job.isNull("byclient") ? false : job.getBoolean("byclient");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat mdyFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            try {
                Date complitionDate = sdf.parse(noteDate);
                displayDate = mdyFormat.format(complitionDate);
            } catch (Exception ex) {
                displayDate = "";
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isByClient() {
        return byClient;
    }

    public void setByClient(boolean byClient) {
        this.byClient = byClient;
    }
}
