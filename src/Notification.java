import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification implements Serializable {

    public enum Type { MESSAGE, LIKE, COMMENT, TAG }

    private Type type;
    private String text;
    private LocalDateTime createdAt;
    private boolean read;

    public Notification(Type type, String text) {
        this.type = type;
        this.text = text;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public Type getType() { return type; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isRead() { return read; }
    public void markRead() { this.read = true; }

    public void Print_Notificaton(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        System.out.println("| "+getType()+" | "+getText());
        System.out.println("| "+getCreatedAt().format(formatter)+" | "+ (isRead()? "Read": "Unread"));
    }
}
