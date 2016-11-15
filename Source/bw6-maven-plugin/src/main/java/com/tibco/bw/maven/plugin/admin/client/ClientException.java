package com.tibco.bw.maven.plugin.admin.client;

public class ClientException extends Exception {
	private static final long serialVersionUID = -4148210972308062778L;
	private final int code;

    ClientException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public String toString() {
        return ClientException.class.getName() + ": " + getMessage() + " (" + getCode() + ")";
    }
}
