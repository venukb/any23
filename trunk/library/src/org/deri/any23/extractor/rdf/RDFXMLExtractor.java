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
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;

public class RDFXMLExtractor implements ContentExtractor {

	public void run(InputStream in, ExtractionResult out)
			throws IOException, ExtractionException {
		try {
			final ExtractionContext context = out.getDocumentContext(this);
			RDFParser parser = new RDFXMLParser();
			parser.setValueFactory(new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance()));
			parser.setRDFHandler(new RDFHandlerAdapter(out, context));
			parser.parse(in, out.getDocumentURI().stringValue());
		} catch (RDFHandlerException ex) {
			throw new RuntimeException(ex);	// should not happen
		} catch (RDFParseException ex) {
			throw new ExtractionException(ex);
		}
	}
	
	public ExtractorDescription getDescription() {
		return factory;
	}

	public final static ExtractorFactory<RDFXMLExtractor> factory = 
		SimpleExtractorFactory.create(
				"rdf-xml",
				null,
				Arrays.asList("application/rdf+xml", "text/rdf", 
						"text/rdf+xml", "application/rdf", 
						"application/xml;q=0.2", "text/xml;q=0.2"),
				"example-rdfxml.rdf",
				RDFXMLExtractor.class);
}
