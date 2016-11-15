/**
 */
package com.tibco.bw.unittest.model.bw6unit;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Assert Node Equals</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getReferenceActivityName <em>Reference Activity Name</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getProjectName <em>Project Name</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getPackageName <em>Package Name</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getProcessName <em>Process Name</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getAssertionMode <em>Assertion Mode</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#isGoldOuputFromFile <em>Gold Ouput From File</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getTestId <em>Test Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#getAssertNodeEquals()
 * @model
 * @generated
 */
public interface AssertNodeEquals extends EObject {
	/**
	 * Returns the value of the '<em><b>Reference Activity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reference Activity Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reference Activity Name</em>' attribute.
	 * @see #setReferenceActivityName(String)
	 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#getAssertNodeEquals_ReferenceActivityName()
	 * @model
	 * @generated
	 */
	String getReferenceActivityName();

	/**
	 * Sets the value of the '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getReferenceActivityName <em>Reference Activity Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reference Activity Name</em>' attribute.
	 * @see #getReferenceActivityName()
	 * @generated
	 */
	void setReferenceActivityName(String value);

	/**
	 * Returns the value of the '<em><b>Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Project Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Project Name</em>' attribute.
	 * @see #setProjectName(String)
	 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#getAssertNodeEquals_ProjectName()
	 * @model
	 * @generated
	 */
	String getProjectName();

	/**
	 * Sets the value of the '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getProjectName <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Project Name</em>' attribute.
	 * @see #getProjectName()
	 * @generated
	 */
	void setProjectName(String value);

	/**
	 * Returns the value of the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Package Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Package Name</em>' attribute.
	 * @see #setPackageName(String)
	 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#getAssertNodeEquals_PackageName()
	 * @model
	 * @generated
	 */
	String getPackageName();

	/**
	 * Sets the value of the '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getPackageName <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package Name</em>' attribute.
	 * @see #getPackageName()
	 * @generated
	 */
	void setPackageName(String value);

	/**
	 * Returns the value of the '<em><b>Process Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Process Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Process Name</em>' attribute.
	 * @see #setProcessName(String)
	 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#getAssertNodeEquals_ProcessName()
	 * @model
	 * @generated
	 */
	String getProcessName();

	/**
	 * Sets the value of the '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getProcessName <em>Process Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Process Name</em>' attribute.
	 * @see #getProcessName()
	 * @generated
	 */
	void setProcessName(String value);

	/**
	 * Returns the value of the '<em><b>Assertion Mode</b></em>' attribute.
	 * The literals are from the enumeration {@link com.tibco.bw.unittest.model.bw6unit.AssertionMode}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Assertion Mode</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Assertion Mode</em>' attribute.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertionMode
	 * @see #setAssertionMode(AssertionMode)
	 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#getAssertNodeEquals_AssertionMode()
	 * @model
	 * @generated
	 */
	AssertionMode getAssertionMode();

	/**
	 * Sets the value of the '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getAssertionMode <em>Assertion Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Assertion Mode</em>' attribute.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertionMode
	 * @see #getAssertionMode()
	 * @generated
	 */
	void setAssertionMode(AssertionMode value);

	/**
	 * Returns the value of the '<em><b>Gold Ouput From File</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gold Ouput From File</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gold Ouput From File</em>' attribute.
	 * @see #setGoldOuputFromFile(boolean)
	 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#getAssertNodeEquals_GoldOuputFromFile()
	 * @model default="false"
	 * @generated
	 */
	boolean isGoldOuputFromFile();

	/**
	 * Sets the value of the '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#isGoldOuputFromFile <em>Gold Ouput From File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gold Ouput From File</em>' attribute.
	 * @see #isGoldOuputFromFile()
	 * @generated
	 */
	void setGoldOuputFromFile(boolean value);

	/**
	 * Returns the value of the '<em><b>Test Id</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Test Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Test Id</em>' attribute.
	 * @see #setTestId(String)
	 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#getAssertNodeEquals_TestId()
	 * @model default=""
	 * @generated
	 */
	String getTestId();

	/**
	 * Sets the value of the '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getTestId <em>Test Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Test Id</em>' attribute.
	 * @see #getTestId()
	 * @generated
	 */
	void setTestId(String value);

} // AssertNodeEquals
