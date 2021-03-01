package com.tibco.bw.maven.plugin.admin.client;

public class ClientException extends Exception {
	private static final long serialVersionUID = -4148210972308062778L;
	private final int code;

    public ClientException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    public ClientException(String message) {
        super(message);
        this.code = 100;
    }

    public int getCode() {
        return this.code;
    }

    public String toString() {
        return ClientException.class.getName() + ": " + getMessage() + " (" + getCode() + ")";
    }
}
