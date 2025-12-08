import java.io.Serializable;
import java.util.List;

public class Hangman extends Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private String word;
    private char letter;
    private int tries;
    private char[] letters;
    private boolean[] found;
    private boolean win;
    private String filename;
    private String turn;
    private String[] players = new String[2];

    Hangman(){
        super("HANG MAN","Dual Player");
    }

    @Override
    public void Game_launch() {
        while (true){
            Print_Hangmanlogo();
            System.out.println("1- Offline MODE");
            System.out.println("2- Online MODE");
            System.out.println("0- Return");
            System.out.println("============================");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    offline_mode();
                }case 2->{
                    online_mode();
                }case 0->{
                    return;
                }
            }
        }
    }

    public void online_game_launch(String filepath){
        filename = filepath;
        String current = Main.current.getCredentials().getUsername();
        players = Database.Load_Players(Database.HangManfldr,filepath);
        Database.Write_Online_Game(Database.HangManfldr,filepath,current);
        String player1 = Main.Get_Fullname(players[0]);
        String player2 = Main.Get_Fullname(players[1]);
        word = Database.Load_Word(filename);
        boolean END, ZEEND;
        if(word == null){
            if(turn.equals(current)){
                word = Input_word();
                turn = turn.equals(players[0]) ? players[1] : players[0];
                Database.Write_tries(filename,tries);
                Database.Write_Word(word,filename);
                Database.Write_turn(Database.HangManfldr,filename,turn);
                Database.Write_found_arr(found,filename);
                Database.Write_letters(filename,letters);
            }
        }
        turn = Database.Load_turn(Database.HangManfldr,filename);
        word = Database.Load_Word(filename);
        String friend = turn.equals(players[0]) ? players[1] : players[0];
        while (true){
            letters = Database.Load_letters(filename);
            found = Database.Load_found_arr(filename);
            tries = Database.Load_tries(filename);
            END = Database.Check_END(Database.HangManfldr,filename,"END");
            ZEEND = Database.Check_END(Database.HangManfldr,filename,"END"+current);
            System.out.println(player1+ (Database.Check_Online_Game(Database.HangManfldr,filepath, players[0]) ? " Online 🟢": " Offline 🔴"));
            System.out.println(player2+ (Database.Check_Online_Game(Database.HangManfldr,filepath, players[1]) ? " Online 🟢": " Offline 🔴"));
            System.out.println("Turn: " + Main.Get_Fullname(turn));
            Print_s();
            if(tries==0){
                Database.Write_END(Database.HangManfldr,filename,friend,"END");
            }
            System.out.println("1- Guess the letter");
            System.out.println("2- Resign");
            System.out.println("3- Refresh");
            System.out.println("4- Check Scoreboard");
            System.out.println("5- Send Friend notification again");
            System.out.println("-----------------------------------");

            if(END || ZEEND){
                String winner = "";
                if(END){
                    Database.Write_Word(null,filename);
                    winner = Database.Load_END(Database.HangManfldr,filepath,"END");
                    Scoreboard score = Database.Load_Score_board(Database.HangManfldr,filepath);
                    if (winner.equals(players[0])){
                        score.increment_Score1();
                    }else{
                        score.increment_Score2();
                    }
                    Database.Write_Score_board(Database.HangManfldr,filepath,score);
                }if(ZEEND){
                    winner = Database.Load_END(Database.HangManfldr,filepath,"END"+current);
                }
                System.out.println("---------------------------");
                System.out.println("\t\t"+Main.Get_Fullname(winner)+ " WON");
                System.out.println("---------------------------");
                Scoreboard.Print_Score_board(Database.HangManfldr,filename,players);
                Game_End_Online(friend);
                return;
            }
            switch (Main.Input_Int("Choice")){
                case 1->{
                    if(turn.equals(current)){
                        letter = Main.Input_String("the Guess letter").toUpperCase().charAt(0);
                        if(!Game_MechanicsOnline(letter,word)){
                            Database.Write_tries(filename,--tries);
                        }
                        Database.Write_letters(filename,letters);
                        Database.Write_found_arr(found,filename);
                        Check_win();
                        if(win){
                            Database.Write_END(Database.HangManfldr,filename,turn,"END");
                        }
                    }else{
                        System.out.println("Wait for your Turn Please!");
                    }
                }case 2->{
                    if(current.equals(players[0])){
                        Database.Write_END(Database.HangManfldr,filename,players[1],"END");
                    }else {
                        Database.Write_END(Database.HangManfldr,filename,players[0],"END");
                    }
                }case 3->{

                }case 4->{
                    Scoreboard.Print_Score_board(Database.HangManfldr,filename,players);
                }case 5->{
                    if(Database.Check_Online_Game(Database.HangManfldr,filename,friend)){
                        System.out.println("Friend is Already Online");
                    }else{
                        Database.Write_Notification(players[1], new Notification(Notification.Type.GAME,(Main.current.getFullName())+" Invited you to play TIC TAC TOE"));
                    }
                }
            }
        }
    }


    public void Game_End_Online(String friend){
        while (true){
            System.out.println("====================================");
            System.out.println("              Game END");
            System.out.println("====================================");
            System.out.println("1- Play Again");
            System.out.println("0- Exit");
            System.out.println("====================================");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    if(!Database.Check_END(Database.HangManfldr,filename,"END"+Main.current.getCredentials().getUsername())){
                        String end = Database.Load_END(Database.HangManfldr,filename,"END");
                        Database.Write_END(Database.HangManfldr,filename,end,"END"+friend);
                        Database.Delete_END(Database.HangManfldr,filename,"END");
                    }else {
                        Database.Delete_END(Database.HangManfldr,filename,"END"+Main.current.getCredentials().getUsername());
                    }
                    online_game_launch(filename);
                }case 0->{
                    if(!Database.Check_Online_Game(Database.HangManfldr ,filename,friend)){
                        Database.Delete_Game_files(Database.HangManfldr,filename);
                    }else{
                        Database.Delete_Online_Game(Database.HangManfldr,filename,Main.current.getCredentials().getUsername());
                    }
                    return;
                }
            }
        }
    }

    public void online_mode(){
        String curr = Main.current.getCredentials().getUsername();
        List<String> friendo = Database.Load_Friends(curr);
        while (true){
            Print_Hangmanlogo();
            System.out.println("1- Chose friend to play with");
            System.out.println("0- Back");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    Main.Print_Friends_List(friendo);
                    int index = Main.Input_Int("Index");
                    if(index<1 || index> friendo.size()){
                        System.out.println("Invalid Index!");
                    }else{
                        String f = friendo.get(--index);
                        if(Database.Check_Online(f)){
                            filename = Database.Alphabetizefilename(curr,f);
                            turn = curr;
                            players[0] = f; players[1] = curr;
                            Database.Create_GameFiles(Database.HangManfldr,filename);
                            Database.Write_turn(Database.HangManfldr,filename,turn);
                            Database.Write_players(Database.HangManfldr,filename,players);
                            Database.Write_Notification(f,new Notification(Notification.Type.GAME,(Main.current.getFullName())+" Invited you to play HANGMAN"));
                            Database.Write_Game_Invite(f,new Game_Invite( new Hangman(),filename,curr));
                            Database.Write_Score_board(Database.HangManfldr,filename,new Scoreboard());
                            Database.Write_Word(null,filename);
                            online_game_launch(filename);
                        }else {
                            System.out.println("Friend is not Online!");
                            System.out.println("Chose a friend that is online !");
                        }
                    }
                }case 0->{
                    return;
                }

            }
        }
    }

    public void Print_s(){
        Print_Hangmanlogo();
        Print_Spaces();
        System.out.println("Tries Left: "+tries);
        Print_HangMan();
    }

    public void offline_mode(){
        Print_Hangmanlogo();
        word = Input_word();
        while (tries>0){
            Print_s();
            if(win){
                System.out.println("YOU WON!!");
                End_Screen();
                return;
            }
            System.out.println();
            letter = Input_letter();
            Game_Mechanics(letter);
            Check_win();
        }
        System.out.println("You LOST!");
        Print_s();
        End_Screen();
    }

    public void End_Screen(){
        while (true){
            Print_Hangmanlogo();
            System.out.println("1- Play again");
            System.out.println("0- Return");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    offline_mode();
                }case 0->{
                    return;
                }
            }
        }
    }

    public void Check_win(){
        for (int i = 0; i < found.length; i++) {
            if(!found[i]){
                win = false;
                return;
            }
        }win = true;
    }

    public void Game_Mechanics(char letter){
        boolean f = false;
        for (int i = 0; i < word.length(); i++) {
            if(found[i]) continue;
            if(word.charAt(i)==letter){
                f = true;
                found[i] = true;
                letters[i]= letter;
            }
        }if(!f){
            tries--;
        }
    }

    public boolean Game_MechanicsOnline(char letter,String word){
        boolean f = false;
        for (int i = 0; i < word.length(); i++) {
            if(found[i]) continue;
            if(word.charAt(i)==letter){
                f = true;
                found[i] = true;
                letters[i]= letter;
            }
        } return f;
    }

    public char Input_letter(){
        while (true){
            char c = Main.Input_String("Guess letter").toUpperCase().charAt(0);
            if(!Character.isLetter(c)){
                System.out.println("Must be a letter!");
            }else {
                return c;
            }
        }
    }

    public String Input_word(){
        while (true){
            String w = Main.Input_String("Word").toUpperCase();
            if(w.length()>40){
                System.out.println("Too long of a Word");
            } else if (w.contains(" ")) {
                System.out.println("No spaces allowed in word!");
            } else if (!islettercheck(w)) {
                System.out.println("Word must be letters only!");
            } else{
                Initilize_data(w.length());
                return w;
            }
        }
    }

    public boolean islettercheck(String w){
        for (char c : w.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }return true;
    }

    public void Print_HangMan(){
        if(tries==6){
            System.out.println(" ");
        } else if (tries == 5) {
            System.out.println("""
                     /
                    /
                    """);
        } else if (tries == 4) {
            System.out.println("""
                     / \\
                    /   \\
                    """);
        } else if (tries == 3) {
            System.out.println("""
                      |
                      |
                     / \\
                    /   \\
                    """);
        } else if (tries==2) {
            System.out.println("""
                     / |
                    /  |
                      / \\
                     /   \\
                    """);
        } else if (tries==1) {
            System.out.println("""
                     / | \\
                    /  |  \\
                      / \\
                     /   \\
                    """);
        }else {
            System.out.println("""
                       _
                     (   )
                     / | \\     I AM DEAD NOW MF
                    /  |  \\
                      / \\
                     /   \\
                    """);
        }
    }

    public void Print_Spaces(){
        String Sg = " ";
        String Bg = "  ";
        for (int i = 0; i < letters.length; i++) {
            System.out.print(Sg +letters[i]+ Sg +Bg);
        }
        System.out.println();
        for (int i = 0; i < letters.length; i++) {
            System.out.print("___" +Bg);
        }
        System.out.println();

    }

    public void Initilize_data(int length){
        letters = new char[length];
        found = new boolean[length];
        tries = 6;
        win = false;
        for (int i = 0; i < letters.length; i++) letters[i] = ' ';
    }

    public void Print_Hangmanlogo(){
        System.out.println("============================");
        System.out.println("          HANG MAN");
        System.out.println("============================");
    }

}
