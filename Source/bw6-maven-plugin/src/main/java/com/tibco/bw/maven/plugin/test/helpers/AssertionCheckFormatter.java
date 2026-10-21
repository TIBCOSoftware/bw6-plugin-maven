package com.tibco.bw.maven.plugin.test.helpers;

import com.tibco.bw.maven.plugin.test.dto.AssertionResultDTO;

/**
 * AMBW-55154 : an activity assertion can carry several checks. The BW runtime then
 * reports one assertionResult per check (checkIndex greater than 0) and counts assertions per
 * check, plus the number of activity assertions in activityAssertions.
 *
 * Older runtimes send neither field, so checkIndex and activityAssertions are 0 and
 * every helper here falls back to the legacy output.
 */
public final class AssertionCheckFormatter {

	public static final String GOLD_FILE_REASON = "Validation failed against Gold file. Please compare Activity output against Gold output values";

	private AssertionCheckFormatter() {
	}

	/**
	 * Suffix for the "Total Assertion: N" line, e.g. " (3 checks in 1 activity assertion)".
	 * Empty when the runtime did not report activity assertions, or when every activity
	 * assertion is a single check (the numbers would be identical).
	 */
	public static String assertionCountSuffix(int assertions, int activityAssertions) {
		if (activityAssertions <= 0 || activityAssertions == assertions) {
			return "";
		}
		return " (" + assertions + (assertions == 1 ? " check" : " checks") + " in "
				+ activityAssertions + (activityAssertions == 1 ? " activity assertion" : " activity assertions") + ")";
	}

	/**
	 * Adds a test case's activity assertions to a running total. Returns -1 once any
	 * test case has not reported them (older runtime), so the summary keeps its
	 * previous format; start the total at 0.
	 */
	public static int sumActivityAssertions(int runningTotal, int activityAssertions) {
		if (runningTotal < 0 || activityAssertions <= 0) {
			return -1;
		}
		return runningTotal + activityAssertions;
	}

	public static boolean isCheckRow(AssertionResultDTO assertion) {
		return assertion != null && assertion.getCheckIndex() > 0;
	}

	/**
	 * Activity part of the failure line : "[Mapper]" or, for a check row, "[Mapper] check [2]".
	 */
	public static String activityLabel(AssertionResultDTO assertion) {
		String label = "[" + assertion.getActivityName() + "]";
		if (isCheckRow(assertion)) {
			label = label + " check [" + assertion.getCheckIndex() + "]";
		}
		return label;
	}

	/**
	 * Activity name used in the list of failed activities : "Mapper" or "Mapper [check 2]".
	 */
	public static String activityDisplayName(AssertionResultDTO assertion) {
		if (isCheckRow(assertion)) {
			return assertion.getActivityName() + " [check " + assertion.getCheckIndex() + "]";
		}
		return assertion.getActivityName();
	}

	/**
	 * Failure reason : the runtime message for a check row (e.g. "Check 2 failed: actual [90],
	 * expected [100]"), the generic Gold file text otherwise.
	 */
	public static String failureReason(AssertionResultDTO assertion) {
		if (isCheckRow(assertion) && assertion.getMessage() != null && !assertion.getMessage().trim().isEmpty()) {
			return assertion.getMessage().trim();
		}
		return GOLD_FILE_REASON;
	}
}
