package patwa.aman.com.amanpatwa;

public class InstantMessage {

    private String message;
    private String author;
    private String mtype;
    private String messtype;

    public InstantMessage(String message, String author, String type, String messagetype) {
        this.message = message;
        this.author = author;
        mtype = type;
        messtype=messagetype;
    }

    public InstantMessage() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMesstype() {
        return messtype;
    }

    public void setMesstype(String messtype) {
        this.messtype = messtype;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}


