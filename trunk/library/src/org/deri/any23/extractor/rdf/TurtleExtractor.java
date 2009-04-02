package org.deri.any23.extractor.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;

public class TurtleExtractor implements ContentExtractor {

	public void run(InputStream in, ExtractionResult out)
			throws IOException, ExtractionException {
		try {
			final ExtractionContext context = out.getDocumentContext(this);
			RDFParser parser = new TurtleParser();
			parser.setRDFHandler(new RDFHandlerAdapter(out, context));
			parser.parse(in, out.getDocumentURI());
		} catch (RDFHandlerException ex) {
			throw new RuntimeException(ex);	// should not happen
		} catch (RDFParseException ex) {
			throw new ExtractionException(ex);
		}
	}
	
	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<TurtleExtractor> factory = 
		SimpleExtractorFactory.create(
				"rdf-turtle",
				null,
				Arrays.asList(
						"text/n3+rdf", "text/n3", "application/n3", 
						"application/x-turtle", "application/turtle", "text/turtle"),
				"example-turtle.ttl",
				TurtleExtractor.class);
}
