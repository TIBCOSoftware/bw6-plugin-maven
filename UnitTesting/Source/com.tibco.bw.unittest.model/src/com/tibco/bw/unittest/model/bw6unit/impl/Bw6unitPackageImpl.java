/**
 */
package com.tibco.bw.unittest.model.bw6unit.impl;

import com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals;
import com.tibco.bw.unittest.model.bw6unit.AssertionMode;
import com.tibco.bw.unittest.model.bw6unit.Bw6unitFactory;
import com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Bw6unitPackageImpl extends EPackageImpl implements Bw6unitPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass assertNodeEqualsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum assertionModeEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Bw6unitPackageImpl() {
		super(eNS_URI, Bw6unitFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link Bw6unitPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static Bw6unitPackage init() {
		if (isInited) return (Bw6unitPackage)EPackage.Registry.INSTANCE.getEPackage(Bw6unitPackage.eNS_URI);

		// Obtain or create and register package
		Bw6unitPackageImpl theBw6unitPackage = (Bw6unitPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof Bw6unitPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new Bw6unitPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theBw6unitPackage.createPackageContents();

		// Initialize created meta-data
		theBw6unitPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theBw6unitPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(Bw6unitPackage.eNS_URI, theBw6unitPackage);
		return theBw6unitPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAssertNodeEquals() {
		return assertNodeEqualsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssertNodeEquals_ReferenceActivityName() {
		return (EAttribute)assertNodeEqualsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssertNodeEquals_ProjectName() {
		return (EAttribute)assertNodeEqualsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssertNodeEquals_PackageName() {
		return (EAttribute)assertNodeEqualsEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssertNodeEquals_ProcessName() {
		return (EAttribute)assertNodeEqualsEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssertNodeEquals_AssertionMode() {
		return (EAttribute)assertNodeEqualsEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssertNodeEquals_GoldOuputFromFile() {
		return (EAttribute)assertNodeEqualsEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAssertNodeEquals_TestId() {
		return (EAttribute)assertNodeEqualsEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getAssertionMode() {
		return assertionModeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bw6unitFactory getBw6unitFactory() {
		return (Bw6unitFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		assertNodeEqualsEClass = createEClass(ASSERT_NODE_EQUALS);
		createEAttribute(assertNodeEqualsEClass, ASSERT_NODE_EQUALS__REFERENCE_ACTIVITY_NAME);
		createEAttribute(assertNodeEqualsEClass, ASSERT_NODE_EQUALS__PROJECT_NAME);
		createEAttribute(assertNodeEqualsEClass, ASSERT_NODE_EQUALS__PACKAGE_NAME);
		createEAttribute(assertNodeEqualsEClass, ASSERT_NODE_EQUALS__PROCESS_NAME);
		createEAttribute(assertNodeEqualsEClass, ASSERT_NODE_EQUALS__ASSERTION_MODE);
		createEAttribute(assertNodeEqualsEClass, ASSERT_NODE_EQUALS__GOLD_OUPUT_FROM_FILE);
		createEAttribute(assertNodeEqualsEClass, ASSERT_NODE_EQUALS__TEST_ID);

		// Create enums
		assertionModeEEnum = createEEnum(ASSERTION_MODE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(assertNodeEqualsEClass, AssertNodeEquals.class, "AssertNodeEquals", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAssertNodeEquals_ReferenceActivityName(), ecorePackage.getEString(), "referenceActivityName", null, 0, 1, AssertNodeEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssertNodeEquals_ProjectName(), ecorePackage.getEString(), "projectName", null, 0, 1, AssertNodeEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssertNodeEquals_PackageName(), ecorePackage.getEString(), "packageName", null, 0, 1, AssertNodeEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssertNodeEquals_ProcessName(), ecorePackage.getEString(), "processName", null, 0, 1, AssertNodeEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssertNodeEquals_AssertionMode(), this.getAssertionMode(), "assertionMode", null, 0, 1, AssertNodeEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssertNodeEquals_GoldOuputFromFile(), ecorePackage.getEBoolean(), "goldOuputFromFile", "false", 0, 1, AssertNodeEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAssertNodeEquals_TestId(), ecorePackage.getEString(), "testId", "", 0, 1, AssertNodeEquals.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(assertionModeEEnum, AssertionMode.class, "AssertionMode");
		addEEnumLiteral(assertionModeEEnum, AssertionMode.ACTIVITY);
		addEEnumLiteral(assertionModeEEnum, AssertionMode.PRIMITIVE);

		// Create resource
		createResource(eNS_URI);
	}

} //Bw6unitPackageImpl
