import java.time.LocalDateTime;

public class Peer {

    private boolean alive;
    private int number;
    private Thread timer; //FIXME
    //inetAddress - IPaddress???


    public Peer() {
        this.alive = true;
        this.number = 0;

        //TODO: add countdown timer
    }


    //Mutators
    public void set_alive(boolean alive) { this.alive = alive; }

    public void set_number(int number) { this.number = number; }

    //Accessors
    public boolean get_alive() { return  alive; }

    public int get_number() { return number; }

}
