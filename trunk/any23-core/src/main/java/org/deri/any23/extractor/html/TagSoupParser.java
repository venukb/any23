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

import org.cyberneko.html.parsers.DOMParser;
import org.deri.any23.validator.DefaultValidator;
import org.deri.any23.validator.Validator;
import org.deri.any23.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Parses an {@link java.io.InputStream}
 * into an <io>HTML DOM</i> tree using a <i>TagSoup</i> parser.
 * <p/>
 * <strong>Note:</strong> The resulting <i>DOM</i> tree will not be namespace
 * aware, and all element names will be upper case, while attributes
 * will be lower case. This is because the
 * <a href="http://nekohtml.sourceforge.net/">NekoHTML</a> based <i>TagSoup</i> parser
 * by default uses the <a href="http://xerces.apache.org/xerces2-j/dom.html">Xerces HTML DOM</a>
 * implementation, which doesn't support namespaces and forces uppercase element names. This works
 * with the <i>RDFa XSLT Converter</i> and with </i>XPath</i>, so we left it this way.
 *
 * @author Richard Cyganiak (richard at cyganiak dot de)
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class TagSoupParser {

    private final static Logger log = LoggerFactory.getLogger(TagSoupParser.class);

    private final InputStream input;

    private final String documentURI;

    private final String encoding;
    
    private Document result = null;

    public TagSoupParser(InputStream input, String documentURI) {
        this.input = input;
        this.documentURI = documentURI;
        this.encoding = null;
    }

    public TagSoupParser(InputStream input, String documentURI, String encoding) {
        if(encoding != null && !Charset.isSupported(encoding))
            throw new UnsupportedCharsetException(String.format("Charset %s is not supported", encoding));

        this.input = input;
        this.documentURI = documentURI;
        this.encoding = encoding;
    }

    /**
     * Returns the DOM of the given document URI. 
     *
     * @return the <i>HTML</i> DOM.
     * @throws IOException
     */
    public Document getDOM() throws IOException {
        if (result == null) {
            long startTime = System.currentTimeMillis();
            try {
                result = parse();
            } catch (SAXException ex) {
                // should not happen, it's a tag soup parser
                throw new RuntimeException("Shouldn not happen, it's a tag soup parser", ex);
            } catch (TransformerException ex) {
                // should not happen, it's a tag soup parser
                throw new RuntimeException("Shouldn not happen, it's a tag soup parser", ex);
            } catch (NullPointerException ex) {
                if (ex.getStackTrace()[0].getClassName().equals("java.io.Reader")) {
                    throw new RuntimeException("Bug in NekoHTML, try upgrading to newer release!", ex);
                } else {
                    throw ex;
                }
            } finally {
                long elapsed = System.currentTimeMillis() - startTime;
                log.debug("Parsed " + documentURI + " with NekoHTML, " + elapsed + "ms");
            }
        }
        result.setDocumentURI(documentURI);
        return result;
    }

    /**
     * Returns the validated DOM and applies fixes on it if <i>applyFix</i>
     * is set to <code>true</code>.
     *
     * @param applyFix
     * @return a report containing the <i>HTML</i> DOM that has been validated and fixed if <i>applyFix</i>
     *         if <code>true</code>. The reports contains also information about the activated rules and the
     *         the detected issues.
     * @throws IOException
     * @throws ValidatorException
     */
    public DocumentReport getValidatedDOM(boolean applyFix) throws IOException, ValidatorException {
        final URI dURI;
        try {
            dURI = new URI(documentURI);
        } catch (URISyntaxException urise) {
            throw new ValidatorException("Error while performing validation, invalid document URI.", urise);
        }
        Validator validator = new DefaultValidator();
        Document document = getDOM();
        return new DocumentReport( validator.validate(dURI, document, applyFix), document );
    }

    private Document parse() throws IOException, SAXException, TransformerException {
        DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        parser.setFeature("http://cyberneko.org/html/features/scanner/script/strip-cdata-delims", true);
        if (this.encoding != null)
            parser.setProperty("http://cyberneko.org/html/properties/default-encoding", this.encoding);

        /*
         * NOTE: the SpanCloserInputStream has been added to wrap the stream passed to the CyberNeko
         *       parser. This will ensure the correct handling of inline HTML SPAN tags.
         *       This fix is documented at issue #78.       
         */
        parser.parse(new InputSource( new SpanCloserInputStream(input)));
        return parser.getDocument();
    }
    
}