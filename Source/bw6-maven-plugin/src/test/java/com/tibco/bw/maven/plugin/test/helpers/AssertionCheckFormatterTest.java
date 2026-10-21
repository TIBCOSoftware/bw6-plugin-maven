package com.tibco.bw.maven.plugin.test.helpers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tibco.bw.maven.plugin.test.dto.AssertionResultDTO;
import com.tibco.bw.maven.plugin.test.dto.CompleteReportDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetResultDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteResultDTO;
import com.tibco.bw.maven.plugin.test.rest.GeneratejunitReport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for AMBW-55154 : an activity assertion with several checks is reported by the
 * runtime as one assertionResult per check (checkIndex greater than 0), and the test
 * case carries the number of activity assertions. Older runtimes send neither field,
 * so everything must print exactly as before for them.
 */
public class AssertionCheckFormatterTest {

	@Test
	public void totalAssertionSuffixShowsChecksPerActivityAssertion() {
		assertEquals(" (3 checks in 1 activity assertion)", AssertionCheckFormatter.assertionCountSuffix(3, 1));
		assertEquals(" (5 checks in 2 activity assertions)", AssertionCheckFormatter.assertionCountSuffix(5, 2));
	}

	@Test
	public void totalAssertionSuffixIsEmptyForOlderRuntimesAndSingleChecks() {
		// older runtime : activityAssertions not sent
		assertEquals("", AssertionCheckFormatter.assertionCountSuffix(3, 0));
		// every activity assertion is a single check : nothing to add
		assertEquals("", AssertionCheckFormatter.assertionCountSuffix(2, 2));
	}

	@Test
	public void summaryTotalFallsBackOnceATestCaseDidNotReportActivityAssertions() {
		int total = 0;
		total = AssertionCheckFormatter.sumActivityAssertions(total, 1);
		total = AssertionCheckFormatter.sumActivityAssertions(total, 2);
		assertEquals(3, total);
		total = AssertionCheckFormatter.sumActivityAssertions(total, 0);
		assertEquals(-1, total);
		total = AssertionCheckFormatter.sumActivityAssertions(total, 4);
		assertEquals(-1, total);
		assertEquals("", AssertionCheckFormatter.assertionCountSuffix(7, total));
	}

	@Test
	public void checkRowUsesCheckNumberAndRuntimeMessage() {
		AssertionResultDTO check = checkRow(2, "failed", "Check 2 failed: actual [90], expected [100]", "90", "100");
		assertTrue(AssertionCheckFormatter.isCheckRow(check));
		assertEquals("[Mapper] check [2]", AssertionCheckFormatter.activityLabel(check));
		assertEquals("Mapper [check 2]", AssertionCheckFormatter.activityDisplayName(check));
		assertEquals("Check 2 failed: actual [90], expected [100]", AssertionCheckFormatter.failureReason(check));
	}

	@Test
	public void legacyRowIsUnchanged() {
		AssertionResultDTO legacy = new AssertionResultDTO();
		legacy.setActivityName("Mapper");
		legacy.setAssertionStatus("failed");
		legacy.setMessage("some engine message");
		assertFalse(AssertionCheckFormatter.isCheckRow(legacy));
		assertEquals("[Mapper]", AssertionCheckFormatter.activityLabel(legacy));
		assertEquals("Mapper", AssertionCheckFormatter.activityDisplayName(legacy));
		assertEquals(AssertionCheckFormatter.GOLD_FILE_REASON, AssertionCheckFormatter.failureReason(legacy));
	}

	@Test
	public void checkRowWithoutMessageFallsBackToGoldFileReason() {
		AssertionResultDTO check = checkRow(1, "failed", null, "a", "b");
		assertEquals(AssertionCheckFormatter.GOLD_FILE_REASON, AssertionCheckFormatter.failureReason(check));
	}

	@Test
	public void junitReportHasOneTestCasePerCheck(@TempDir Path dir) throws Exception {
		TestCaseResultDTO testCase = new TestCaseResultDTO();
		testCase.setTestCaseFile("Tests/Process.bwt");
		testCase.setAssertions(3);
		testCase.setAssertionsRun(2);
		testCase.setAssertionFailure(1);
		testCase.setActivityAssertions(1);
		addResult(testCase, checkRow(1, "passed", "Check 1 passed", "a", "a"));
		addResult(testCase, checkRow(2, "failed", "Check 2 failed: actual [90], expected [100]", "90", "100"));
		addResult(testCase, checkRow(3, "passed", "Check 3 passed", "x", "x"));

		AssertionResultDTO legacy = new AssertionResultDTO();
		legacy.setActivityName("Log");
		legacy.setAssertionStatus("passed");
		legacy.setAssertionMode("XPath");
		TestCaseResultDTO legacyCase = new TestCaseResultDTO();
		legacyCase.setTestCaseFile("Tests/Legacy.bwt");
		legacyCase.setAssertions(1);
		legacyCase.setAssertionsRun(1);
		addResult(legacyCase, legacy);

		String junit = generateJunit(dir, testCase, legacyCase);

		assertTrue(junit.contains("<testcase name=\"Mapper [check 1]\""), junit);
		assertTrue(junit.contains("<testcase name=\"Mapper [check 2]\""), junit);
		assertTrue(junit.contains("<testcase name=\"Mapper [check 3]\""), junit);
		assertTrue(junit.contains("<failure message=\"Check 2 failed: actual [90], expected [100]\""), junit);
		// legacy row keeps its plain name
		assertTrue(junit.contains("<testcase name=\"Log\""), junit);
		// counts stay in step with assertions / assertionFailure
		assertTrue(junit.contains("tests=\"4\" failure=\"1\""), junit);
	}

	@SuppressWarnings("unchecked")
	private static void addResult(TestCaseResultDTO testCase, AssertionResultDTO result) {
		testCase.getAssertionResult().add(result);
	}

	@SuppressWarnings("unchecked")
	private static String generateJunit(Path dir, TestCaseResultDTO... testCases) throws Exception {
		TestSetResultDTO testSet = new TestSetResultDTO();
		testSet.setProcessName("demo.Process");
		testSet.setPackageName("demo");
		for (TestCaseResultDTO testCase : testCases) {
			testSet.getTestCaseResult().add(testCase);
		}
		TestSuiteResultDTO suite = new TestSuiteResultDTO();
		suite.getTestSetResult().add(testSet);
		CompleteReportDTO report = new CompleteReportDTO();
		report.getModuleResult().add(suite);

		File reportFile = dir.resolve("bwtestreport.xml").toFile();
		Marshaller marshaller = JAXBContext.newInstance(CompleteReportDTO.class).createMarshaller();
		marshaller.marshal(report, reportFile);

		String xml = new String(Files.readAllBytes(reportFile.toPath()), StandardCharsets.UTF_8);
		assertTrue(xml.contains("<checkIndex>2</checkIndex>"), xml);
		assertTrue(xml.contains("<activityAssertions>1</activityAssertions>"), xml);

		File junitFile = dir.resolve("generatedJunitReport.xml").toFile();
		new GeneratejunitReport().genereateReport(reportFile.getPath(), junitFile.getPath());
		return new String(Files.readAllBytes(junitFile.toPath()), StandardCharsets.UTF_8);
	}

	private static AssertionResultDTO checkRow(int index, String status, String message, String actual, String expected) {
		AssertionResultDTO result = new AssertionResultDTO();
		result.setActivityName("Mapper");
		result.setCheckIndex(index);
		result.setAssertionStatus(status);
		result.setMessage(message);
		result.setActivityOutput(actual);
		result.setGoldInput(expected);
		result.setAssertionMode("Primitive");
		return result;
	}
}
