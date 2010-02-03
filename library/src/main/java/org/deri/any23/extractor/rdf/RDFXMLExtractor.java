/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.rdf;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParser.DatatypeHandling;
import org.openrdf.rio.rdfxml.RDFXMLParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Concrete implementation of {@link org.deri.any23.extractor.Extractor.ContentExtractor}
 * able to perform the extraction on <a href="http://www.w3.org/TR/REC-rdf-syntax/">RDF/XML</a>
 * documents.
 */
public class RDFXMLExtractor implements ContentExtractor {

    public void run(InputStream in, URI documentURI, ExtractionResult out)
            throws IOException, ExtractionException {
        try {
            RDFParser parser = new RDFXMLParser();
            parser.setValueFactory(new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance()));
            parser.setDatatypeHandling(DatatypeHandling.IGNORE);
            parser.setRDFHandler(new RDFHandlerAdapter(out));
            parser.parse(in, documentURI.stringValue());
        } catch (RDFHandlerException ex) {
            throw new RuntimeException("Should not happen, RDFHandlerAdapter does not throw this", ex);
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
