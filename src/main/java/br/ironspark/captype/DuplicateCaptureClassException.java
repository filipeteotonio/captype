package br.ironspark.captype;

public class DuplicateCaptureClassException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DuplicateCaptureClassException() {
		super("Multiple fields declared with same name");
	};
}
