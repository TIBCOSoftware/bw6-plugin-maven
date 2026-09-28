package com.tibco.bw.maven.plugin.test.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonControllers;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for GoldFileOmitFilter (BWCE-9093).
 *
 * The scenario: an activity emits a value that changes on every run - a timestamp,
 * a generated id - so the Gold-file assertion can never pass twice. The user marks
 * that field in BusinessStudio, which writes optional="true" into the Gold Input
 * file, and the comparison has to skip it.
 *
 * The trap the first attempt at this fell into (BWCE-6329, later reverted) is that
 * the marker is itself an attribute. Anything that compares attributes - XPath 2.0
 * deep-equal on the engine side, XMLUnit here - sees an attribute present in the
 * Gold file and absent from the activity output, so adding the marker made the
 * comparison strictly stricter instead of looser. Hence
 * {@link #markerAttributeIsNeverReportedAsADifference()}.
 */
public class GoldFileOmitFilterTest {

	private static final String GOLD_WITH_OMIT =
			"<Order>"
			+ "<Header><id>42</id><timestamp optional=\"true\">2026-01-01T00:00:00Z</timestamp></Header>"
			+ "<Body><id>99</id></Body>"
			+ "</Order>";

	private static final String OUTPUT_DIFFERENT_TIMESTAMP =
			"<Order>"
			+ "<Header><id>42</id><timestamp>2026-09-28T14:32:52Z</timestamp></Header>"
			+ "<Body><id>99</id></Body>"
			+ "</Order>";

	@Test
	public void collectsMarkedFieldsAsPathsFromTheDocumentElement() {
		Set<String> omitted = GoldFileOmitFilter.collectOmittedPaths(GOLD_WITH_OMIT);

		assertEquals(1, omitted.size());
		assertTrue(omitted.contains("Order/Header/timestamp"), omitted.toString());
	}

	@Test
	public void unmarkedGoldFileOmitsNothing() {
		assertTrue(GoldFileOmitFilter.collectOmittedPaths(
				"<Order><Header><id>42</id></Header></Order>").isEmpty());
	}

	@Test
	public void malformedOrEmptyGoldFileOmitsNothingRatherThanFailing() {
		assertTrue(GoldFileOmitFilter.collectOmittedPaths("<Order><unclosed>").isEmpty());
		assertTrue(GoldFileOmitFilter.collectOmittedPaths("").isEmpty());
		assertTrue(GoldFileOmitFilter.collectOmittedPaths(null).isEmpty());
	}

	/** Paths, not bare names - Header/id must not drag Body/id out of the comparison. */
	@Test
	public void sameElementNameUnderADifferentParentIsNotOmitted() {
		String gold = "<Order>"
				+ "<Header><id optional=\"true\">42</id></Header>"
				+ "<Body><id>99</id></Body>"
				+ "</Order>";
		String output = "<Order>"
				+ "<Header><id>SOMETHING-ELSE</id></Header>"
				+ "<Body><id>CHANGED-TOO</id></Body>"
				+ "</Order>";

		assertEquals(1, diffCount(gold, output), "Body/id must still be compared");
	}

	@Test
	public void omittedFieldNoLongerProducesADifference() {
		assertEquals(0, diffCount(GOLD_WITH_OMIT, OUTPUT_DIFFERENT_TIMESTAMP));
	}

	@Test
	public void fieldsOutsideTheOmitListAreStillCompared() {
		String output = OUTPUT_DIFFERENT_TIMESTAMP.replace("<id>42</id>", "<id>WRONG</id>");

		assertTrue(diffCount(GOLD_WITH_OMIT, output) > 0, "Header/id must still be compared");
	}

	/**
	 * The regression that sank the first attempt: the marker must not itself show up
	 * as a difference on elements that are still being compared.
	 */
	@Test
	public void markerAttributeIsNeverReportedAsADifference() {
		// marker on an element whose content matches - nothing at all should be reported
		String gold = "<Order><Header optional=\"true\"><id>42</id></Header></Order>";
		String output = "<Order><Header><id>42</id></Header></Order>";

		assertEquals(0, diffCount(gold, output));
	}

	/**
	 * Gold file and activity output may bind the same namespace to different
	 * prefixes, so omitted paths are matched on local names.
	 *
	 * Note what is *not* asserted here: the prefix mismatch on the root element
	 * still shows up as an (xmlunit-SIMILAR) difference. That is pre-existing
	 * doXmlDiff behaviour and outside BWCE-9093 - the engine's deep-equal compares
	 * namespace URIs and does not care about prefixes, so the report is noisier
	 * than the assertion that produced it. What matters here is that the omitted
	 * timestamp is gone.
	 */
	@Test
	public void prefixesAreIgnoredWhenMatchingOmittedPaths() {
		String gold = "<tns:Order xmlns:tns=\"urn:x\">"
				+ "<tns:timestamp optional=\"true\">2026-01-01T00:00:00Z</tns:timestamp>"
				+ "</tns:Order>";
		String output = "<ns0:Order xmlns:ns0=\"urn:x\">"
				+ "<ns0:timestamp>2026-09-28T14:32:52Z</ns0:timestamp>"
				+ "</ns0:Order>";

		assertTrue(GoldFileOmitFilter.collectOmittedPaths(gold).contains("Order/timestamp"));
		for (String difference : differences(gold, output)) {
			assertFalse(difference.contains("timestamp"), "omitted field still reported: " + difference);
		}
	}

	@Test
	public void attributeFilterHidesOnlyTheMarker() {
		assertFalse(GoldFileOmitFilter.attributeFilter().test(attribute("optional")));
		assertTrue(GoldFileOmitFilter.attributeFilter().test(attribute("currency")));
	}

	private int diffCount(String gold, String output) {
		return differences(gold, output).size();
	}

	/**
	 * Runs the same XMLUnit pipeline BWTestRunner.doXmlDiff uses and returns the
	 * differences that survive, rendered exactly as the failure report renders them.
	 */
	private List<String> differences(String gold, String output) {
		Set<String> omitted = GoldFileOmitFilter.collectOmittedPaths(gold);
		DiffBuilder builder = DiffBuilder
				.compare(gold)
				.withTest(output)
				.ignoreComments()
				.ignoreWhitespace()
				.withComparisonController(ComparisonControllers.StopWhenDifferent);
		if (!omitted.isEmpty()) {
			builder = builder
					.withNodeFilter(GoldFileOmitFilter.nodeFilter(omitted))
					.withAttributeFilter(GoldFileOmitFilter.attributeFilter());
		}
		Diff diff = builder.build();

		List<String> rendered = new ArrayList<String>();
		for (Difference difference : diff.getDifferences()) {
			rendered.add(difference.toString());
		}
		return rendered;
	}

	private org.w3c.dom.Attr attribute(String name) {
		try {
			return javax.xml.parsers.DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.newDocument()
					.createAttribute(name);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
