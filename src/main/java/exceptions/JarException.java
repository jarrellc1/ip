package exceptions;

public class JarException extends Exception {
    public JarException(String message) {
        super(message); //exception specifically for jar
    }
}
