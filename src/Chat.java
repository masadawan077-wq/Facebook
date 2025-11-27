import java.io.*;

public abstract class Chat implements Serializable {

    private String folder_path;

    public String getFolder_path() {
        return folder_path;
    }

    public void setFolder_path(String folder_path) {
        this.folder_path = folder_path;
    }

    public abstract void Print_Chat_Outside();

    public abstract void filepathinitilize(String sender);

}
