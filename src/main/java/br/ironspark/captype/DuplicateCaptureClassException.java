package br.ironspark.captype;

public class DuplicateCaptureClassException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DuplicateCaptureClassException() {
		super("Multiple fields declared with same name");
	};
}
