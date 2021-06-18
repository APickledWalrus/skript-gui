package io.github.apickledwalrus.skriptgui.util;

public class UnexpectedParameterException extends RuntimeException {

	public UnexpectedParameterException(String message) {
		super("[skript-gui] " + message);
	}

}
