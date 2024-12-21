package tim.labs.labs.exception;

public class UsernameOccupiedException extends Exception{
    public UsernameOccupiedException(){
        super("Received username already in use!");
    }
}
