package com.tibco.bw.maven.plugin.test.helpers;

import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xmlunit.util.Predicate;

/**
 * BWCE-9093 : support for Gold Input files that carry non-deterministic values.
 *
 * The BusinessStudio "Select fields to Omit" dialog marks such fields in the Gold
 * Input file with <code>optional="true"</code>. The engine-side assertion skips
 * them, and so must the XMLUnit diff this plugin prints when an assertion fails -
 * otherwise the report blames fields that were never compared.
 *
 * The omitted fields are read back out of the Gold file itself rather than carried
 * on {@link com.tibco.bw.maven.plugin.test.dto.AssertionDTO}, so that no new field
 * has to survive the round trip through the engine's assertion report.
 */
public final class GoldFileOmitFilter {

	/** Marker attribute written into the Gold Input file by BusinessStudio. */
	public static final String OMIT_ATTRIBUTE = "optional";

	private GoldFileOmitFilter() {
	}

	/**
	 * Collects the elements marked as omitted in the Gold Input file, as paths of
	 * local element names rooted at the document element - for example
	 * <code>Order/Header/timestamp</code>.
	 *
	 * Paths rather than bare element names, so that omitting <code>Header/id</code>
	 * does not silently omit an unrelated <code>Body/id</code>. Local names rather
	 * than qualified names, because the activity output and the Gold file are free
	 * to bind the same namespace to different prefixes.
	 *
	 * @param goldInput raw contents of the Gold Input file
	 * @return the omitted paths; empty if there are none or the Gold file cannot be
	 *         parsed, never <code>null</code>
	 */
	public static Set<String> collectOmittedPaths(String goldInput) {
		if (StringUtils.isBlank(goldInput)) {
			return Collections.emptySet();
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			// The default handler writes parse errors straight to stderr, which would
			// surface as unattributed "[Fatal Error]" noise in the middle of the test
			// report. Rethrow instead and let the catch below decide.
			builder.setErrorHandler(new ErrorHandler() {
				@Override
				public void warning(SAXParseException e) {
					// not fatal for our purposes - the Gold file is still readable
				}

				@Override
				public void error(SAXParseException e) throws SAXException {
					throw e;
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					throw e;
				}
			});
			Document document = builder.parse(new InputSource(new StringReader(goldInput)));
			Set<String> omittedPaths = new HashSet<String>();
			collect(document.getDocumentElement(), omittedPaths);
			return omittedPaths;
		} catch (Exception e) {
			// A malformed Gold file is already reported elsewhere. Comparing everything
			// is the safe fallback - it can only add detail to the failure report.
			return Collections.emptySet();
		}
	}

	private static void collect(Node node, Set<String> omittedPaths) {
		if (node == null) {
			return;
		}
		if (node.getNodeType() == Node.ELEMENT_NODE
				&& "true".equals(((Element) node).getAttribute(OMIT_ATTRIBUTE))) {
			omittedPaths.add(getLocalNamePath(node));
		}
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			collect(children.item(i), omittedPaths);
		}
	}

	/**
	 * Node filter that drops the omitted elements - and their subtrees - from both
	 * sides of the comparison. Mirrors
	 * <code>org.xmlunit.diff.NodeFilters.Default</code> for everything else.
	 */
	public static Predicate<Node> nodeFilter(final Set<String> omittedPaths) {
		return new Predicate<Node>() {
			@Override
			public boolean test(Node node) {
				if (node.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
					return false;
				}
				if (node.getNodeType() != Node.ELEMENT_NODE) {
					return true;
				}
				return !omittedPaths.contains(getLocalNamePath(node));
			}
		};
	}

	/**
	 * Attribute filter that hides the marker itself. It is design-time metadata and
	 * never appears in the activity output, so comparing it would report a
	 * difference on every element the user marked.
	 */
	public static Predicate<Attr> attributeFilter() {
		return new Predicate<Attr>() {
			@Override
			public boolean test(Attr attribute) {
				return !OMIT_ATTRIBUTE.equals(getLocalName(attribute.getNodeName()));
			}
		};
	}

	/**
	 * Builds the <code>/</code>-separated path of local element names from the
	 * document element down to the given node.
	 */
	static String getLocalNamePath(Node node) {
		Deque<String> segments = new ArrayDeque<String>();
		for (Node current = node; current != null
				&& current.getNodeType() == Node.ELEMENT_NODE; current = current.getParentNode()) {
			segments.addFirst(getLocalName(current.getNodeName()));
		}
		return StringUtils.join(segments, "/");
	}

	static String getLocalName(String qualifiedName) {
		String localName = StringUtils.substringAfterLast(qualifiedName, ":");
		return localName.isEmpty() ? qualifiedName : localName;
	}

}
