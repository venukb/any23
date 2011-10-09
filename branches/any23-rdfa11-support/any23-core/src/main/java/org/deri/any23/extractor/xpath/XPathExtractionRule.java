/**
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
 *
 */

package org.deri.any23.extractor.xpath;

import org.deri.any23.extractor.ExtractionResult;
import org.openrdf.model.URI;
import org.w3c.dom.Document;


/**
 * Defines an extraction rule for the {@link XPathExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface XPathExtractionRule {

    /**
     * @return the human readable rule name.
     */
    String getName();

    /**
     * Checks if the rule can be applied on the given document <i>URI</i>.
     *
     * @param uri input document URI.
     * @return <code>true</code> if applied, <code>false</code> otherwise.
     */
    boolean acceptURI(URI uri);

    /**
     * Processes this extraction rule on the given document.
     *
     * @param in input document to be processed.
     * @param out output result writer.
     */
    void process(Document in, ExtractionResult out);

}
