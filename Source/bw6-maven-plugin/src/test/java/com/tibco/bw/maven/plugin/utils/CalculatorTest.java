package com.tibco.bw.maven.plugin.utils;

import org.junit.jupiter.api.Test;

import junit.framework.TestCase;

public class CalculatorTest extends TestCase {

	 Calculator calculator = new Calculator();

	    @Test
	    void testAdd() {
	        assertEquals(5, calculator.add(2, 3));
	    }

	    @Test
	    void testMultiply() {
	        assertEquals(6, calculator.multiply(2, 3));
	    }

}
