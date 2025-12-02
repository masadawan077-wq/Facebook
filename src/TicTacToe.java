import java.io.Serializable;
import java.util.List;

public class TicTacToe extends Game implements Serializable {

    String cross = "✖";
    String tick = "✔";
    String turn;
    String mark;
    String [] board = new String[9];
    final String gap = "   ";
    String filename;
    String[] players = new String[2];
    String[] marks = new String[2];

    TicTacToe(){
        super("TIC TAC TOE", "DUAL PLAYER");
    }

    public void board_cleaner(){
        for (int i = 0; i < board.length; i++) board[i]=" ";
    }

    public void online(){
        board_cleaner();
        String curr = Main.current.getCredentials().getUsername();
        List<String> friendo = Database.Load_Friends(curr);
        while (true){
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
                            Chose_Marker(curr,f);
                            filename = Database.Alphabetizefilename(curr,f);
                            Database.Create_tic_tac(filename);
                            Database.Write_tic_tac(board,filename,turn,players,marks);
                            Database.Write_Score_board(filename,new Scoreboard());
                            Database.Write_Notification(f,new Notification(Notification.Type.GAME,(Main.current.getFullName())+" Invited you to play TIC TAC TOE"));
                            Database.Write_Game_Invite(f,new Game_Invite( new TicTacToe(),filename,Main.current.getCredentials().getUsername()));
                            Online_game_launch(filename);
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

    public void Chose_Marker(String curr,String f){
        players[0] = curr;
        players[1] = f;
        while (true){
            System.out.println("--------------------------------------");
            System.out.println("Chose Marker");
            System.out.println("1- Cross "+cross);
            System.out.println("2- Tick "+ tick);
            System.out.println("--------------------------------------");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    marks[0] = cross;
                    marks[1] = tick;
                    turn = curr;
                    return;
                }case 2->{
                    marks[0] = tick;
                    marks[1] = cross;
                    turn = f;
                    return;
                }
            }
        }
    }

    public void offline_game(){
        players[0] = cross;
        players[1] = tick;
        turn = players[0];
        board_cleaner();
        while (true){
            System.out.println("Player 1: "+ players[0]);
            System.out.println("Player 2: "+ players[1]);
            System.out.println("Turn: "+ turn);
            Print_board();
            System.out.println("1- Place Marker");
            System.out.println("2- Resign");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    int index = Main.Input_Int("Index(1-9)");
                    if(index<1 || index> board.length){
                        System.out.println("Invalid Choice!");
                    }else{
                        if(Place_marker(--index,turn)){
                            State state =  Game_mechanic(turn);
                            if(state == State.WIN){
                                if(turn.equals(players[0])){
                                    System.out.println("Player 1 Won:" + turn);
                                }else {
                                    System.out.println("Player 2 Won: "+ turn);
                                }
                                Print_board();
                                if(Game_End()){
                                    return;
                                }else {
                                    offline_game();
                                    return;
                                }
                            } else if (state== State.DRAW) {
                                System.out.println("Game DRAW");
                                Print_board();
                                if(Game_End()){
                                    return;
                                }else {
                                    offline_game();
                                    return;
                                }
                            }
                            turn = turn.equals(players[0]) ? players[1] : players[0];
                        }
                    }
                }case 2->{
                    if(turn.equals(players[0])){
                        System.out.println("Player 2 Won");
                    }else {
                        System.out.println("Player 1 Won: ");
                    }
                    if(Game_End()){
                        return;
                    }else {
                        offline_game();
                        return;
                    }
                }
            }
        }
    }

    public boolean Game_End(){
        while (true){
            System.out.println("====================================");
            System.out.println("              Game END");
            System.out.println("====================================");
            System.out.println("1- Play Again");
            System.out.println("0- Exit");
            System.out.println("====================================");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    return false;
                }case 0->{
                    return true;
                }
            }
        }

    }
    public boolean Game_End_Online(String friend){
        while (true){
            System.out.println("====================================");
            System.out.println("              Game END");
            System.out.println("====================================");
            System.out.println("1- Play Again");
            System.out.println("0- Exit");
            System.out.println("====================================");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    return false;
                }case 0->{
                    if(!Database.Check_Online_Game(filename,friend)){
                        Database.Delete_Tic_Tac_fldr(filename);
                    }else{
                        Database.Delete_Online_Game(filename,Main.current.getCredentials().getUsername());
                    }
                    return true;
                }
            }
        }

    }

    public State Game_mechanic(String turn){
        if(  (board[0].equals(turn) && board[0].equals(board[1]) && board[1].equals(board[2])) || (board[3].equals(turn) && board[3].equals(board[4]) && board[4].equals(board[5])) || (board[6].equals(turn) && board[6].equals(board[7]) && board[7].equals(board[8])) ){
            return State.WIN;
        }
        if( (board[0].equals(turn) && board[0].equals(board[3]) && board[3].equals(board[6])) || (board[1].equals(turn) && board[1].equals(board[4]) && board[4].equals(board[7])) || (board[2].equals(turn) && board[2].equals(board[5]) && board[5].equals(board[8])) ){
            return State.WIN;
        }
        if((board[0].equals(turn) && board[0].equals(board[4]) && board[4].equals(board[8])) || (board[2].equals(turn) && board[2].equals(board[4]) && board[4].equals(board[6])) ){
            return State.WIN;
        }
        for (int i = 0; i < board.length; i++) {
            if(board[i].equals(" ")) return State.CONTINUE;
        }
        return State.DRAW;
    }

    public boolean Place_marker(int index,String turn){
        if(board[index].equals(" ")){
            board[index] = turn;
            return true;
        }else{
            System.out.println("Already a marker there!");
            return false;
        }
    }


    public void Print_board(){
        System.out.println("\t\t      TIC TAC TOE       ");
        System.out.println("\t\t=========================");
        System.out.println("\t\t|"+gap+board[0]+gap+"|"+gap+board[1]+gap+"|"+gap+board[2]+gap+"|");
        System.out.println("\t\t-------------------------");
        System.out.println("\t\t|"+gap+board[3]+gap+"|"+gap+board[4]+gap+"|"+gap+board[5]+gap+"|");
        System.out.println("\t\t-------------------------");
        System.out.println("\t\t|"+gap+board[6]+gap+"|"+gap+board[7]+gap+"|"+gap+board[8]+gap+"|");
        System.out.println("\t\t=========================");
    }



    public void Online_game_launch(String filepath){
        filename = filepath;
        String current = Main.current.getCredentials().getUsername();
        players = Database.Load_Players(filepath);
        marks = Database.Load_marks(filepath);
        Database.Write_Online_Game(filepath,current);
        String player1 = Main.Get_Fullname(players[0]);
        String player2 = Main.Get_Fullname(players[1]);
        board_cleaner();
        Database.Write_board(filepath,board);
        while (true){
            board = Database.Load_tic_tac_board(filepath);
            turn = Database.Load_tic_tac_turn(filepath);
            mark = turn.equals(players[0])? marks[0] : marks[1];
            System.out.println(player1+ " : " +marks[0] + (Database.Check_Online_Game(filepath, players[0]) ? " Online 🟢": " Offline 🔴"));
            System.out.println(player2+ " : " +marks[1] + (Database.Check_Online_Game(filepath, players[1]) ? " Online 🟢": " Offline 🔴"));
            System.out.println("Turn: " + Main.Get_Fullname(turn));
            Print_board();
            System.out.println("1- Place Marker");
            System.out.println("2- Resign");
            System.out.println("3- Refresh");
            System.out.println("4- Check Scoreboard");
            System.out.println("5- Send Friend notification again");
            System.out.println("-----------------------------------");
            if(Database.Check_END(filepath)){
                String end = Database.Load_END(filepath);
                if(end==null){
                    System.out.println("GAME DRAW");
                }else{
                    Scoreboard score = Database.Load_Score_board(filepath);
                    if (end.equals(players[0])){
                        score.increment_Score1();
                    }else{
                        score.increment_Score2();
                    }
                    Database.Write_Score_board(filepath,score);
                    System.out.println("---------------------------");
                    System.out.println("\t\t"+Main.Get_Fullname(end)+ " WON");
                    System.out.println("---------------------------");
                }
                String friend = players[0].equals(Main.current.getCredentials().getUsername())? players[1] : players[0];
                if(Game_End_Online(friend)){
                    return;
                }else Online_game_launch(filepath);
                return;
            }
            switch (Main.Input_Int("Choice")){
                case 1->{
                    if(!turn.equals(current)){
                        System.out.println("Wait for you turn Please");
                        continue;
                    }
                    int index = Main.Input_Int("Index(1-9)");
                    if(index<1 || index> board.length){
                        System.out.println("Invalid Choice!");
                    }else{
                        if(Place_marker(--index,mark)){
                            State state =  Game_mechanic(mark);
                            if(state == State.WIN){
                                Database.Write_END(filepath,turn);
                            } else if (state== State.DRAW) {
                                Database.Write_END(filepath,null);
                            }
                            turn = turn.equals(players[0]) ? players[1] : players[0];
                            Database.Write_board(filepath,board);
                            Database.Write_turn(filepath, turn);
                        }
                    }
                }case 2->{
                    if(!turn.equals(current)){
                        System.out.println("Wait for you turn Please");
                        continue;
                    }
                    if(turn.equals(players[0])){
                        Database.Write_END(filepath,players[1]);
                    }else{
                        Database.Write_END(filepath,players[0]);
                    }
                }case 3->{

                }case 4->{
                    Print_Score_board(filepath);
                }case 5->{
                    if(turn.equals(players[0])){
                        if(Database.Check_Online_Game(filepath,players[1])){
                            System.out.println("Friend is already online");
                        }else{
                            Database.Write_Notification(players[1], new Notification(Notification.Type.GAME,(Main.current.getFullName())+" Invited you to play TIC TAC TOE"));
                        }
                    }else{
                        if(Database.Check_Online_Game(filepath,players[0])){
                            System.out.println("Friend is already online");
                        }else{
                            Database.Write_Notification(players[0], new Notification(Notification.Type.GAME,(Main.current.getFullName())+" Invited you to play TIC TAC TOE"));
                        }
                    }
                }
            }
        }
    }

    public void Game_launch(){
        while (true){
            System.out.println("===========================================");
            System.out.println("                TIC TAC TOE");
            System.out.println("===========================================");
            System.out.println("1- Offline MODE");
            System.out.println("2- Online MODE");
            System.out.println("0- Return");
            System.out.println("===========================================");
            switch (Main.Input_Int("Choice")){
                case 1->{
                    offline_game();
                }case 2->{
                    online();
                }case 0->{
                    return;
                }
            }
        }

    };

    public void Print_Score_board(String fldrname){
        Scoreboard board = Database.Load_Score_board(fldrname);
        System.out.println("======================================");
        System.out.println("            Score Board");
        System.out.println("======================================");
        System.out.println(Main.Get_Fullname(players[0])+" : " + board.getScore1());
        System.out.println(Main.Get_Fullname(players[1])+" : " + board.getScore2());
        System.out.println("Total Games: "+ board.getTotolgame());
        System.out.println("======================================");

    }

}
