import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment extends Content implements Serializable {

    public Comment(String text, String sender) {
        super(text,sender);
    }

    @Override
    public void Print_Content() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        System.out.println(getSender()+ ": " + getText()+"         "+getTime().format(formatter));
    }

}
