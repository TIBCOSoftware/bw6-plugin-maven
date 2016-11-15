/**
 */
package com.tibco.bw.unittest.model.bw6unit;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitFactory
 * @model kind="package"
 * @generated
 */
public interface Bw6unitPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "bw6unit";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://tibco.com/bw/bw6unit";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "bwunit";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Bw6unitPackage eINSTANCE = com.tibco.bw.unittest.model.bw6unit.impl.Bw6unitPackageImpl.init();

	/**
	 * The meta object id for the '{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl <em>Assert Node Equals</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl
	 * @see com.tibco.bw.unittest.model.bw6unit.impl.Bw6unitPackageImpl#getAssertNodeEquals()
	 * @generated
	 */
	int ASSERT_NODE_EQUALS = 0;

	/**
	 * The feature id for the '<em><b>Reference Activity Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS__REFERENCE_ACTIVITY_NAME = 0;

	/**
	 * The feature id for the '<em><b>Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS__PROJECT_NAME = 1;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS__PACKAGE_NAME = 2;

	/**
	 * The feature id for the '<em><b>Process Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS__PROCESS_NAME = 3;

	/**
	 * The feature id for the '<em><b>Assertion Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS__ASSERTION_MODE = 4;

	/**
	 * The feature id for the '<em><b>Gold Ouput From File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS__GOLD_OUPUT_FROM_FILE = 5;

	/**
	 * The feature id for the '<em><b>Test Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS__TEST_ID = 6;

	/**
	 * The number of structural features of the '<em>Assert Node Equals</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Assert Node Equals</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ASSERT_NODE_EQUALS_OPERATION_COUNT = 0;


	/**
	 * The meta object id for the '{@link com.tibco.bw.unittest.model.bw6unit.AssertionMode <em>Assertion Mode</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertionMode
	 * @see com.tibco.bw.unittest.model.bw6unit.impl.Bw6unitPackageImpl#getAssertionMode()
	 * @generated
	 */
	int ASSERTION_MODE = 1;


	/**
	 * Returns the meta object for class '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals <em>Assert Node Equals</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Assert Node Equals</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals
	 * @generated
	 */
	EClass getAssertNodeEquals();

	/**
	 * Returns the meta object for the attribute '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getReferenceActivityName <em>Reference Activity Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Reference Activity Name</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getReferenceActivityName()
	 * @see #getAssertNodeEquals()
	 * @generated
	 */
	EAttribute getAssertNodeEquals_ReferenceActivityName();

	/**
	 * Returns the meta object for the attribute '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getProjectName <em>Project Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Project Name</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getProjectName()
	 * @see #getAssertNodeEquals()
	 * @generated
	 */
	EAttribute getAssertNodeEquals_ProjectName();

	/**
	 * Returns the meta object for the attribute '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getPackageName <em>Package Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Name</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getPackageName()
	 * @see #getAssertNodeEquals()
	 * @generated
	 */
	EAttribute getAssertNodeEquals_PackageName();

	/**
	 * Returns the meta object for the attribute '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getProcessName <em>Process Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Process Name</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getProcessName()
	 * @see #getAssertNodeEquals()
	 * @generated
	 */
	EAttribute getAssertNodeEquals_ProcessName();

	/**
	 * Returns the meta object for the attribute '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getAssertionMode <em>Assertion Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Assertion Mode</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getAssertionMode()
	 * @see #getAssertNodeEquals()
	 * @generated
	 */
	EAttribute getAssertNodeEquals_AssertionMode();

	/**
	 * Returns the meta object for the attribute '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#isGoldOuputFromFile <em>Gold Ouput From File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gold Ouput From File</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#isGoldOuputFromFile()
	 * @see #getAssertNodeEquals()
	 * @generated
	 */
	EAttribute getAssertNodeEquals_GoldOuputFromFile();

	/**
	 * Returns the meta object for the attribute '{@link com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getTestId <em>Test Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Test Id</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals#getTestId()
	 * @see #getAssertNodeEquals()
	 * @generated
	 */
	EAttribute getAssertNodeEquals_TestId();

	/**
	 * Returns the meta object for enum '{@link com.tibco.bw.unittest.model.bw6unit.AssertionMode <em>Assertion Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Assertion Mode</em>'.
	 * @see com.tibco.bw.unittest.model.bw6unit.AssertionMode
	 * @generated
	 */
	EEnum getAssertionMode();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	Bw6unitFactory getBw6unitFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl <em>Assert Node Equals</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl
		 * @see com.tibco.bw.unittest.model.bw6unit.impl.Bw6unitPackageImpl#getAssertNodeEquals()
		 * @generated
		 */
		EClass ASSERT_NODE_EQUALS = eINSTANCE.getAssertNodeEquals();

		/**
		 * The meta object literal for the '<em><b>Reference Activity Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSERT_NODE_EQUALS__REFERENCE_ACTIVITY_NAME = eINSTANCE.getAssertNodeEquals_ReferenceActivityName();

		/**
		 * The meta object literal for the '<em><b>Project Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSERT_NODE_EQUALS__PROJECT_NAME = eINSTANCE.getAssertNodeEquals_ProjectName();

		/**
		 * The meta object literal for the '<em><b>Package Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSERT_NODE_EQUALS__PACKAGE_NAME = eINSTANCE.getAssertNodeEquals_PackageName();

		/**
		 * The meta object literal for the '<em><b>Process Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSERT_NODE_EQUALS__PROCESS_NAME = eINSTANCE.getAssertNodeEquals_ProcessName();

		/**
		 * The meta object literal for the '<em><b>Assertion Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSERT_NODE_EQUALS__ASSERTION_MODE = eINSTANCE.getAssertNodeEquals_AssertionMode();

		/**
		 * The meta object literal for the '<em><b>Gold Ouput From File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSERT_NODE_EQUALS__GOLD_OUPUT_FROM_FILE = eINSTANCE.getAssertNodeEquals_GoldOuputFromFile();

		/**
		 * The meta object literal for the '<em><b>Test Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ASSERT_NODE_EQUALS__TEST_ID = eINSTANCE.getAssertNodeEquals_TestId();

		/**
		 * The meta object literal for the '{@link com.tibco.bw.unittest.model.bw6unit.AssertionMode <em>Assertion Mode</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.tibco.bw.unittest.model.bw6unit.AssertionMode
		 * @see com.tibco.bw.unittest.model.bw6unit.impl.Bw6unitPackageImpl#getAssertionMode()
		 * @generated
		 */
		EEnum ASSERTION_MODE = eINSTANCE.getAssertionMode();

	}

} //Bw6unitPackage
