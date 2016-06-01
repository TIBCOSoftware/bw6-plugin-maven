/**
 */
package com.tibco.bw.unittest.model.bw6unit.impl;

import com.tibco.bw.unittest.model.bw6unit.AssertNodeEquals;
import com.tibco.bw.unittest.model.bw6unit.AssertionMode;
import com.tibco.bw.unittest.model.bw6unit.Bw6unitPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Assert Node Equals</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl#getReferenceActivityName <em>Reference Activity Name</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl#getProjectName <em>Project Name</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl#getPackageName <em>Package Name</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl#getProcessName <em>Process Name</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl#getAssertionMode <em>Assertion Mode</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl#isGoldOuputFromFile <em>Gold Ouput From File</em>}</li>
 *   <li>{@link com.tibco.bw.unittest.model.bw6unit.impl.AssertNodeEqualsImpl#getTestId <em>Test Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AssertNodeEqualsImpl extends MinimalEObjectImpl.Container implements AssertNodeEquals {
	/**
	 * The default value of the '{@link #getReferenceActivityName() <em>Reference Activity Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferenceActivityName()
	 * @generated
	 * @ordered
	 */
	protected static final String REFERENCE_ACTIVITY_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getReferenceActivityName() <em>Reference Activity Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferenceActivityName()
	 * @generated
	 * @ordered
	 */
	protected String referenceActivityName = REFERENCE_ACTIVITY_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getProjectName() <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROJECT_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProjectName() <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected String projectName = PROJECT_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getPackageName() <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageName()
	 * @generated
	 * @ordered
	 */
	protected static final String PACKAGE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPackageName() <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageName()
	 * @generated
	 * @ordered
	 */
	protected String packageName = PACKAGE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getProcessName() <em>Process Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcessName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROCESS_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProcessName() <em>Process Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProcessName()
	 * @generated
	 * @ordered
	 */
	protected String processName = PROCESS_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getAssertionMode() <em>Assertion Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssertionMode()
	 * @generated
	 * @ordered
	 */
	protected static final AssertionMode ASSERTION_MODE_EDEFAULT = AssertionMode.ACTIVITY;

	/**
	 * The cached value of the '{@link #getAssertionMode() <em>Assertion Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAssertionMode()
	 * @generated
	 * @ordered
	 */
	protected AssertionMode assertionMode = ASSERTION_MODE_EDEFAULT;

	/**
	 * The default value of the '{@link #isGoldOuputFromFile() <em>Gold Ouput From File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGoldOuputFromFile()
	 * @generated
	 * @ordered
	 */
	protected static final boolean GOLD_OUPUT_FROM_FILE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isGoldOuputFromFile() <em>Gold Ouput From File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGoldOuputFromFile()
	 * @generated
	 * @ordered
	 */
	protected boolean goldOuputFromFile = GOLD_OUPUT_FROM_FILE_EDEFAULT;

	/**
	 * The default value of the '{@link #getTestId() <em>Test Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTestId()
	 * @generated
	 * @ordered
	 */
	protected static final String TEST_ID_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getTestId() <em>Test Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTestId()
	 * @generated
	 * @ordered
	 */
	protected String testId = TEST_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AssertNodeEqualsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Bw6unitPackage.Literals.ASSERT_NODE_EQUALS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReferenceActivityName() {
		return referenceActivityName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReferenceActivityName(String newReferenceActivityName) {
		String oldReferenceActivityName = referenceActivityName;
		referenceActivityName = newReferenceActivityName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bw6unitPackage.ASSERT_NODE_EQUALS__REFERENCE_ACTIVITY_NAME, oldReferenceActivityName, referenceActivityName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProjectName(String newProjectName) {
		String oldProjectName = projectName;
		projectName = newProjectName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bw6unitPackage.ASSERT_NODE_EQUALS__PROJECT_NAME, oldProjectName, projectName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPackageName(String newPackageName) {
		String oldPackageName = packageName;
		packageName = newPackageName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bw6unitPackage.ASSERT_NODE_EQUALS__PACKAGE_NAME, oldPackageName, packageName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProcessName(String newProcessName) {
		String oldProcessName = processName;
		processName = newProcessName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bw6unitPackage.ASSERT_NODE_EQUALS__PROCESS_NAME, oldProcessName, processName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AssertionMode getAssertionMode() {
		return assertionMode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAssertionMode(AssertionMode newAssertionMode) {
		AssertionMode oldAssertionMode = assertionMode;
		assertionMode = newAssertionMode == null ? ASSERTION_MODE_EDEFAULT : newAssertionMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bw6unitPackage.ASSERT_NODE_EQUALS__ASSERTION_MODE, oldAssertionMode, assertionMode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isGoldOuputFromFile() {
		return goldOuputFromFile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGoldOuputFromFile(boolean newGoldOuputFromFile) {
		boolean oldGoldOuputFromFile = goldOuputFromFile;
		goldOuputFromFile = newGoldOuputFromFile;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bw6unitPackage.ASSERT_NODE_EQUALS__GOLD_OUPUT_FROM_FILE, oldGoldOuputFromFile, goldOuputFromFile));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTestId() {
		return testId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTestId(String newTestId) {
		String oldTestId = testId;
		testId = newTestId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Bw6unitPackage.ASSERT_NODE_EQUALS__TEST_ID, oldTestId, testId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case Bw6unitPackage.ASSERT_NODE_EQUALS__REFERENCE_ACTIVITY_NAME:
				return getReferenceActivityName();
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PROJECT_NAME:
				return getProjectName();
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PACKAGE_NAME:
				return getPackageName();
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PROCESS_NAME:
				return getProcessName();
			case Bw6unitPackage.ASSERT_NODE_EQUALS__ASSERTION_MODE:
				return getAssertionMode();
			case Bw6unitPackage.ASSERT_NODE_EQUALS__GOLD_OUPUT_FROM_FILE:
				return isGoldOuputFromFile();
			case Bw6unitPackage.ASSERT_NODE_EQUALS__TEST_ID:
				return getTestId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case Bw6unitPackage.ASSERT_NODE_EQUALS__REFERENCE_ACTIVITY_NAME:
				setReferenceActivityName((String)newValue);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PROJECT_NAME:
				setProjectName((String)newValue);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PACKAGE_NAME:
				setPackageName((String)newValue);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PROCESS_NAME:
				setProcessName((String)newValue);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__ASSERTION_MODE:
				setAssertionMode((AssertionMode)newValue);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__GOLD_OUPUT_FROM_FILE:
				setGoldOuputFromFile((Boolean)newValue);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__TEST_ID:
				setTestId((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case Bw6unitPackage.ASSERT_NODE_EQUALS__REFERENCE_ACTIVITY_NAME:
				setReferenceActivityName(REFERENCE_ACTIVITY_NAME_EDEFAULT);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PROJECT_NAME:
				setProjectName(PROJECT_NAME_EDEFAULT);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PACKAGE_NAME:
				setPackageName(PACKAGE_NAME_EDEFAULT);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PROCESS_NAME:
				setProcessName(PROCESS_NAME_EDEFAULT);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__ASSERTION_MODE:
				setAssertionMode(ASSERTION_MODE_EDEFAULT);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__GOLD_OUPUT_FROM_FILE:
				setGoldOuputFromFile(GOLD_OUPUT_FROM_FILE_EDEFAULT);
				return;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__TEST_ID:
				setTestId(TEST_ID_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case Bw6unitPackage.ASSERT_NODE_EQUALS__REFERENCE_ACTIVITY_NAME:
				return REFERENCE_ACTIVITY_NAME_EDEFAULT == null ? referenceActivityName != null : !REFERENCE_ACTIVITY_NAME_EDEFAULT.equals(referenceActivityName);
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PROJECT_NAME:
				return PROJECT_NAME_EDEFAULT == null ? projectName != null : !PROJECT_NAME_EDEFAULT.equals(projectName);
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PACKAGE_NAME:
				return PACKAGE_NAME_EDEFAULT == null ? packageName != null : !PACKAGE_NAME_EDEFAULT.equals(packageName);
			case Bw6unitPackage.ASSERT_NODE_EQUALS__PROCESS_NAME:
				return PROCESS_NAME_EDEFAULT == null ? processName != null : !PROCESS_NAME_EDEFAULT.equals(processName);
			case Bw6unitPackage.ASSERT_NODE_EQUALS__ASSERTION_MODE:
				return assertionMode != ASSERTION_MODE_EDEFAULT;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__GOLD_OUPUT_FROM_FILE:
				return goldOuputFromFile != GOLD_OUPUT_FROM_FILE_EDEFAULT;
			case Bw6unitPackage.ASSERT_NODE_EQUALS__TEST_ID:
				return TEST_ID_EDEFAULT == null ? testId != null : !TEST_ID_EDEFAULT.equals(testId);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (referenceActivityName: ");
		result.append(referenceActivityName);
		result.append(", projectName: ");
		result.append(projectName);
		result.append(", packageName: ");
		result.append(packageName);
		result.append(", processName: ");
		result.append(processName);
		result.append(", assertionMode: ");
		result.append(assertionMode);
		result.append(", goldOuputFromFile: ");
		result.append(goldOuputFromFile);
		result.append(", testId: ");
		result.append(testId);
		result.append(')');
		return result.toString();
	}

} //AssertNodeEqualsImpl
