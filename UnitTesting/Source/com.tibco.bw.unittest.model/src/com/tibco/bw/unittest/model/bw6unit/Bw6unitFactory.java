/**
 */
package com.tibco.bw.unittest.model.bw6unit;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage
 * @generated
 */
public interface Bw6unitFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Bw6unitFactory eINSTANCE = com.tibco.bw.unittest.model.bw6unit.impl.Bw6unitFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Assert Node Equals</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Assert Node Equals</em>'.
	 * @generated
	 */
	AssertNodeEquals createAssertNodeEquals();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	Bw6unitPackage getBw6unitPackage();

} //Bw6unitFactory
