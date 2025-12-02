import java.io.Serializable;

public class Scoreboard implements Serializable {
    private int score1;
    private int score2;
    private int totolgame;

    Scoreboard(){
        totolgame = 0;
        score2 = 0;
        score1 = 0;
    }

    public void increment_Score1(){
        score1++;
        increment_Total();
    }
    public void increment_Score2(){
        score2++;
        increment_Total();
    }
    public void increment_Total(){
        totolgame++;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public int getTotolgame() {
        return totolgame;
    }

    public void setTotolgame(int totolgame) {
        this.totolgame = totolgame;
    }
}
