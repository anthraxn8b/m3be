package eu.kairat.apps.m3.exception;

@SuppressWarnings("serial")
public class SpecialException extends Exception
{
	private Enum<?> exceptionCode;
	
	public SpecialException(Enum<?> exceptionCode, Exception e)
	{
		super(e);
		
		this.exceptionCode = exceptionCode;
	}
	
	public SpecialException(Enum<?> exceptionCode)
	{
		super();
		
		this.exceptionCode = exceptionCode;
	}
	
	public Enum<?> getExceptionCode()
	{
		return exceptionCode;
	}
}
