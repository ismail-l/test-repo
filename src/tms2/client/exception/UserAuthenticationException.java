package tms2.client.exception;

public class UserAuthenticationException extends TMSException 
{
	private static final long serialVersionUID = 8034436058205044376L;
	
	public static final int USERNAME_NOT_FOUND = 1;
	public static final int PASSWORD_INCORRECT = 2;
	public static final int ACCOUNT_INACTIVE = 3;
	public static final int ACCOUNT_EXPIRED = 4;

	private int accountStatus = 0;
	
	public UserAuthenticationException() 
	{
		super();
	}

	public UserAuthenticationException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	public UserAuthenticationException(String message) 
	{
		super(message);
	}

	public UserAuthenticationException(Throwable cause) 
	{
		super(cause);
	}

	public int getAccountStatus() 
	{
		return accountStatus;
	}

	public void setAccountStatus(int accountStatus) 
	{
		this.accountStatus = accountStatus;
	}
}
