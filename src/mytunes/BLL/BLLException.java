package mytunes.BLL;

/**
 * Represents an exception thrown in the BLL. 
 * @author sebok
 */
public class BLLException extends Exception
{
    
    /**
     * Create a new exception using the parameter as the message
     * @param message The message of the exception
     */
    public BLLException(String message)
    {
        super(message);
    }
    
    /**
     * Create a new exception using the message from another exception
     * @param ex The message from this exception will be used to create a new exception
     */
    public  BLLException(Exception ex)
    {
        super(ex.getMessage());
    }
    
    /**
     * Return the message of the exception
     * @return String The message of the exception
     */
    @Override
    public String getMessage()
    {
        return super.getMessage();
    }
    
    
}
