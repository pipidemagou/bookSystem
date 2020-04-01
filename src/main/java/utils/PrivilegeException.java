package utils;

//自定义的权限异常类
public class PrivilegeException extends Exception {

    public PrivilegeException() {
        super();
    }

    public PrivilegeException(String message) {
        super(message);
    }

    public PrivilegeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrivilegeException(Throwable cause) {
        super(cause);
    }
}


