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

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * TODO Validate comments/documentation throughout this file
 * <p/>
 * The abstract base class for any Microformat extractor.
 * It requires a method that returns the name of the microformat,
 * and a method that performs the extraction and writes the results
 * to an RDF model.
 * <p/>
 * The nodes generated in the model can have any name or implicit label
 * but if possible they SHOULD have names (either URIs or AnonId) that
 * are uniquely derivable from their position in the DOM tree, so that
 * multiple extractors can merge information.
 * <p/>
 * TODO: Deep class hierarchies are ugly, we should do something without protected fields
 */
public abstract class MicroformatExtractor implements TagSoupDOMExtractor {

    protected HTMLDocument document;
    protected URI documentURI;
    protected ExtractionResult out;
    protected final Any23ValueFactoryWrapper valueFactory =
            new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());

    public abstract ExtractorDescription getDescription();
    
    public void run(Document in, URI documentURI, ExtractionResult out)
    throws IOException, ExtractionException {
        this.document = new HTMLDocument(in);
        this.documentURI = documentURI;
        this.out = out;
        extract();
    }

    /**
     * Performs the extraction of the data and writes them to the model.
     */
    protected abstract boolean extract() throws ExtractionException;

    /**
     * Helper method that adds a literal property to a node.
     */
    protected boolean conditionallyAddStringProperty(Resource subject, URI p, String value) {
        if (value == null) return false;
        value = value.trim();
        if ("".equals(value)) return false;
        out.writeTriple(subject, p, valueFactory.createLiteral(value));
        return true;
    }

    protected URI fixLink(String link) {
        return fixLink(link, null);
    }

    /**
     * Helper method to conditionally add a schema to a URI unless it's there, or null if link is empty.
     * TODO: Move this to the same class as fixURI()
     */
    protected URI fixLink(String link, String defaultSchema) {
        if (link == null) return null;
        link = fixWhiteSpace(link);
        if ("".equals(link)) return null;
        if (defaultSchema != null && !link.startsWith(defaultSchema + ":")) {
            link = defaultSchema + ":" + link;
        }
        return valueFactory.fixURI(link);
    }

    protected String fixWhiteSpace(String name) {
        return name.replaceAll("\\s+", " ").trim();
    }

    /**
     * Helper method that adds a URI property to a node.
     */
    protected boolean conditionallyAddResourceProperty(Resource subject,
                                                       URI property, URI uri) {
        if (uri == null) return false;
        out.writeTriple(subject, property, uri);
        return true;
    }

}