package com.tibco.bw.maven.plugin.test.helpers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tibco.bw.maven.plugin.test.dto.AssertionDTO;
import com.tibco.bw.maven.plugin.test.dto.TestCaseDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSetDTO;
import com.tibco.bw.maven.plugin.test.dto.TestSuiteDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the parse-time fan-out of a parameterized .bwt (BWCE-11850).
 *
 * The customer scenario: one process, the same assertions, but a dozen different
 * input payloads. Before this change a .bwt could hold several &lt;Inputs&gt; rows but
 * the parser read only {@code item(0)}, so the other rows were silently dropped and
 * the workaround was to copy the .bwt once per payload. Now each row becomes its own
 * {@link TestCaseDTO} - the same shape the engine already gets from N separate files,
 * which is why none of this needs an engine change.
 *
 * The two things that must not regress:
 * - a single-input .bwt keeps its exact previous identity, so existing reports are unchanged;
 * - each row gets its own assertion instances, since the gold path is bound per row.
 */
public class TestFileParserFanOutTest
{
	@TempDir
	Path baseDir;

	private TestFileParser parser;

	@BeforeEach
	public void setUp()
	{
		BWTestConfig.INSTANCE.setLogger(new SystemStreamLog());
		BWTestConfig.INSTANCE.setTestSuiteName(null);

		// TestFileParser is a mutable singleton, so the flags have to be put back
		// to their defaults or one test leaks into the next.
		parser = TestFileParser.INSTANCE;
		parser.disableMocking = false;
		parser.disableAssertions = false;
		parser.showFailureDetails = false;
	}

	@Test
	public void twoInputRowsBecomeTwoTestCases() throws Exception
	{
		writeInput("input1.xml", "<order id=\"1\"/>");
		writeInput("input2.xml", "<order id=\"2\"/>");

		List<TestCaseDTO> cases = run(bwt(
				row("input1.xml", null, null),
				row("input2.xml", null, null)));

		assertEquals(2, cases.size());
		assertEquals("<order id=\"1\"/>", cases.get(0).getXmlInput());
		assertEquals("<order id=\"2\"/>", cases.get(1).getXmlInput());
	}

	@Test
	public void eachRowIsIdentifiedByItsInputFileName() throws Exception
	{
		writeInput("input1.xml", "<a/>");
		writeInput("input2.xml", "<b/>");

		List<TestCaseDTO> cases = run(bwt(
				row("input1.xml", null, null),
				row("input2.xml", null, null)));

		assertEquals("/Mod/Tests/Order.bwt#input1.xml", cases.get(0).getTestCaseFile());
		assertEquals("/Mod/Tests/Order.bwt#input2.xml", cases.get(1).getTestCaseFile());
		assertEquals("/Mod/Tests/Order.bwt", TestFileParser.stripRowLabel(cases.get(0).getTestCaseFile()));
		assertEquals("input1.xml", TestFileParser.rowLabelOf(cases.get(0).getTestCaseFile()));
	}

	@Test
	public void anExplicitLabelWinsOverTheFileName() throws Exception
	{
		writeInput("input1.xml", "<a/>");
		writeInput("input2.xml", "<b/>");

		List<TestCaseDTO> cases = run(bwt(
				row("input1.xml", null, "valid-order"),
				row("input2.xml", null, "missing-customer")));

		assertEquals("/Mod/Tests/Order.bwt#valid-order", cases.get(0).getTestCaseFile());
		assertEquals("/Mod/Tests/Order.bwt#missing-customer", cases.get(1).getTestCaseFile());
	}

	/**
	 * Rows pointing at same-named files in different folders would otherwise collapse
	 * into one result, because the label is part of the test case identity.
	 */
	@Test
	public void collidingLabelsAreMadeUnique() throws Exception
	{
		writeInput("a/input.xml", "<a/>");
		writeInput("b/input.xml", "<b/>");

		List<TestCaseDTO> cases = run(bwt(
				row("a/input.xml", null, null),
				row("b/input.xml", null, null)));

		assertEquals("/Mod/Tests/Order.bwt#input.xml", cases.get(0).getTestCaseFile());
		assertEquals("/Mod/Tests/Order.bwt#input.xml-2", cases.get(1).getTestCaseFile());
	}

	/**
	 * The whole point of making the suffix conditional: one input row means one test
	 * case with the identity it has always had, so existing JUnit reports do not move.
	 */
	@Test
	public void aSingleInputRowKeepsItsLegacyIdentity() throws Exception
	{
		writeInput("input1.xml", "<a/>");

		List<TestCaseDTO> cases = run(bwt(row("input1.xml", null, null)));

		assertEquals(1, cases.size());
		assertEquals("/Mod/Tests/Order.bwt", cases.get(0).getTestCaseFile());
		assertFalse(cases.get(0).getTestCaseFile().contains(TestFileParser.ROW_SEPARATOR));
	}

	@Test
	public void everyRowKeepsTheStarterIdAndTheSharedAssertions() throws Exception
	{
		writeInput("input1.xml", "<a/>");
		writeInput("input2.xml", "<b/>");

		List<TestCaseDTO> cases = run(bwt(
				row("input1.xml", null, null),
				row("input2.xml", null, null)));

		for (TestCaseDTO testcase : cases)
		{
			assertEquals("bw.rest.", testcase.getProcessStarterID());
			assertEquals(1, testcase.getAssertionList().size());
		}
		// Separate instances - the gold path is rewritten per row, so a shared
		// AssertionDTO would let the last row overwrite the others.
		assertNotSame(cases.get(0).getAssertionList().get(0), cases.get(1).getAssertionList().get(0));
	}

	@Test
	public void aRowLevelGoldFileOverridesTheOneInTheAssertion() throws Exception
	{
		writeInput("input1.xml", "<a/>");
		writeInput("input2.xml", "<b/>");

		List<TestCaseDTO> cases = run(bwt(
				row("input1.xml", null, null),
				row("input2.xml", "Gold/expected2.xml", null)));

		String base = baseDir.toString().replace("\\", "/");
		assertEquals("doc('file:///" + base + "/Gold/expected.xml')", expressionOf(cases.get(0)));
		assertEquals("doc('file:///" + base + "/Gold/expected2.xml')", expressionOf(cases.get(1)));
	}

	/**
	 * With more than one gold-file assertion a single row-level goldFile is not enough -
	 * each activity needs its own expected output.
	 */
	@Test
	public void aPerActivityGoldFileOverridesTheRowLevelOne() throws Exception
	{
		writeInput("input1.xml", "<a/>");
		writeInput("input2.xml", "<b/>");

		String perActivity = row("input1.xml", "Gold/row-wide.xml", null).replace("/>",
				"><GoldFile activityId=\"bw.rest.Mapper\" path=\"Gold/mapper.xml\"/></Inputs>");
		List<TestCaseDTO> cases = run(bwt(perActivity, row("input2.xml", "Gold/row-wide.xml", null)));

		String base = baseDir.toString().replace("\\", "/");
		assertEquals("doc('file:///" + base + "/Gold/mapper.xml')", expressionOf(cases.get(0)));
		assertEquals("doc('file:///" + base + "/Gold/row-wide.xml')", expressionOf(cases.get(1)));
	}

	@Test
	public void anInlineInputRowCannotBeCombinedWithFileRows() throws Exception
	{
		writeInput("input1.xml", "<a/>");

		String inline = "<Inputs Id=\"bw.rest.Start\" Name=\"Start\" type=\"REST\"/>";
		Exception failure = assertThrows(Exception.class,
				() -> run(bwt(row("input1.xml", null, null), inline)));

		assertTrue(failure.getMessage().contains("isInputFile"), failure.getMessage());
	}

	// ---------------------------------------------------------------- helpers

	private List<TestCaseDTO> run(String bwtContents) throws Exception
	{
		TestSuiteDTO suite = new TestSuiteDTO();
		parser.collectAssertions(bwtContents, suite, baseDir.toString());

		@SuppressWarnings("unchecked")
		List<TestSetDTO> sets = suite.getTestSetList();
		assertEquals(1, sets.size(), "expected exactly one test set for the one ProcessNode");

		@SuppressWarnings("unchecked")
		List<TestCaseDTO> cases = sets.get(0).getTestCaseList();
		return cases;
	}

	private String expressionOf(TestCaseDTO testcase)
	{
		AssertionDTO assertion = (AssertionDTO) testcase.getAssertionList().get(0);
		return assertion.getExpression();
	}

	private void writeInput(String relativePath, String contents) throws Exception
	{
		File file = new File(baseDir.toFile(), relativePath);
		file.getParentFile().mkdirs();
		Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
	}

	private String row(String inputFile, String goldFile, String label)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<Inputs Id=\"bw.rest.Mod.Start\" Name=\"Start\" type=\"REST\" isInputFile=\"true\"");
		builder.append(" inputFile=\"").append(inputFile).append("\"");
		if (goldFile != null)
		{
			builder.append(" goldFile=\"").append(goldFile).append("\"");
		}
		if (label != null)
		{
			builder.append(" label=\"").append(label).append("\"");
		}
		builder.append("/>");
		return builder.toString();
	}

	/**
	 * A minimal .bwt: one ProcessNode with one gold-file assertion and the given input rows.
	 */
	private String bwt(String... rows)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		builder.append("<emulation:EmulationData xmlns:emulation=\"http:///emulation.ecore\" location=\"/Mod/Tests/Order.bwt\">");
		builder.append("<ProcessNode Id=\"Mod.Order.bwp\" Name=\"Order\" moduleName=\"Mod\">");
		builder.append("<Assertion Id=\"bw.rest.Mapper\" Name=\"Mapper\" goldOutputFromFile=\"true\">");
		builder.append("<Lang>urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0</Lang>");
		builder.append("<Expression>doc('file:///Gold/expected.xml')</Expression>");
		builder.append("</Assertion>");
		builder.append("<Operation Name=\"post\" serviceName=\"OrderService\">");
		for (String row : rows)
		{
			builder.append(row);
		}
		builder.append("</Operation>");
		builder.append("</ProcessNode>");
		builder.append("</emulation:EmulationData>");
		return builder.toString();
	}
}
