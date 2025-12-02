import java.io.Serializable;

public abstract class Game implements Serializable {
    private String name;
    private String type;

    Game(String name, String Type){
        this.name = name;
        this.type = Type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract void Game_launch();

    public void Print_Game_data(){
        System.out.println("Name: "+name);
        System.out.println("Type: "+ type);
    }
}
