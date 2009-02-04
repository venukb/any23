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
import org.deri.any23.rdf.Prefixes;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;

public class RDFXMLExtractor implements ContentExtractor {

	public void run(InputStream in, ExtractionResult out)
			throws IOException, ExtractionException {
		Model m = ModelFactory.createDefaultModel();
		try {
			m.read(in, out.getDocumentURI(), "RDF/XML");
			ExtractionContext context = out.getDocumentContext(
					this, getPrefixes(m));
			StmtIterator it = m.listStatements();
			while (it.hasNext()) {
				Statement stmt = it.nextStatement();
				out.writeTriple(stmt.getSubject().asNode(), 
						stmt.getPredicate().asNode(), 
						stmt.getObject().asNode(), 
						context);
			}
		} catch (JenaException ex) {
			throw new ExtractionException(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Prefixes getPrefixes(Model m) {
		return Prefixes.createFromMap(m.getNsPrefixMap(), true);
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
