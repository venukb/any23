package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.XHTML;
import org.openrdf.model.URI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Arrays;

/**
 * Extractor for the <a href="http://microformats.org/wiki/rel-license">rel-license</a>
 * microformat.
 * <p/>
 * TODO: What happens to license links that are not valid URIs?
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak
 */
public class LicenseExtractor implements TagSoupDOMExtractor {

    public void run(Document in, URI documentURI, ExtractionResult out) throws IOException,
            ExtractionException {
        HTMLDocument document = new HTMLDocument(in);
        for (Node node : DomUtils.findAll(in, "//A[@rel='license']/@href")) {
            String link = node.getNodeValue();
            if ("".equals(link)) continue;
            out.writeTriple(documentURI, XHTML.license, document.resolveURI(link));
        }
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

    public final static ExtractorFactory<LicenseExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-license",
                    PopularPrefixes.createSubset("xhtml"),
                    Arrays.asList("text/html;q=0.01", "application/xhtml+xml;q=0.01"),
                    null,
                    LicenseExtractor.class);
}