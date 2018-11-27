package mytunes.gui.Model;

/**
 * Represents an error thrown in during operations in the MediaPlayerModel class
 * @author sebok
 */
public class ModelException extends Exception
{

    /**
     * Constructs a new exception using the parameter string as the message
     * @param message The string that will be used as the message
     */
    public ModelException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new exception using the message from another exception
     * @param ex The exception from which the message will be used
     */
    public ModelException(Exception ex)
    {
        super(ex.getMessage());
    }
    
    /**
     * Returns the message stored in this exception
     * @return The message stored in this exception
     */
    @Override
    public String getMessage()
    {
        return super.getMessage(); 
    }
       
}
