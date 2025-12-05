import java.io.Serializable;

public class Game_Invite implements Serializable {
    private Game game;
    private String sender;
    private String filepath;

    Game_Invite(Game game, String path,String sender){
        this.game = game;
        filepath = path;
        this.sender = sender;
    }

    public Game getGame() {
        return game;
    }

    public String getFilepath() {
        return filepath;
    }

    public void Print_Invite(){
        System.out.println("Invite from: "+ Main.Get_Fullname(sender));
        game.Print_Game_data();
    }
}
