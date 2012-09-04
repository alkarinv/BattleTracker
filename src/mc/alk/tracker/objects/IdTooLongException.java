package mc.alk.tracker.objects;

public class IdTooLongException extends Exception {
	public IdTooLongException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;
}
