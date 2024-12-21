package tim.labs.labs.exception;


public class InvalidUserCredentialsException extends Exception{
    public InvalidUserCredentialsException(){
        super("Received invalid user credentials.");
    }
}
