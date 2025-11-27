import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Content implements Serializable {

    final private LocalDateTime time;
    final private String text;
    final private String sender;

    public Content(String text, String sender) {
        this.time = LocalDateTime.now();
        this.text = text;
        this.sender = sender;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public abstract void Print_Content();

}
