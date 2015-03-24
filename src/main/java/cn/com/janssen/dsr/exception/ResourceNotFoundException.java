package cn.com.janssen.dsr.exception;

/**
 * Created by Ali on 2014/7/5.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
