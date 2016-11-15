package com.tibco.bw.unittest.runtime.utils;

public enum PrimitiveSelector 
{
	STRING("string"), //$NON-NLS-1$
	BOOLEAN("boolean"), //$NON-NLS-1$
	INTEGER("int"), //$NON-NLS-1$
	DATETIME("dateTime"), //$NON-NLS-1$
	FLOAT("float"), //$NON-NLS-1$
	DOUBLE("double"); //$NON-NLS-1$
	

	protected final String name;	

	PrimitiveSelector(String path) {
		this.name = path;
	}

	public synchronized String getValue() {
		
		return this.name;
	}
	
	
}
