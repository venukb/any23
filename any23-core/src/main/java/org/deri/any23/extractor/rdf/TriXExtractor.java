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

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.openrdf.rio.helpers.RDFParserBase;

import java.util.Arrays;

/**
 * Concrete implementation of {@link ContentExtractor}
 * to perform extraction on <a href="http://www.w3.org/2004/03/trix/">TriX</a> documents.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TriXExtractor extends BaseRDFExtractor {

    public final static ExtractorFactory<TriXExtractor> factory =
            SimpleExtractorFactory.create(
                    "rdf-trix",
                    null,
                    Arrays.asList(
                            "application/trix"
                    ),
                    "example-trix.trx",
                    TriXExtractor.class
            );

    /**
     * Constructor, allows to specify the validation and error handling policies.
     *
     * @param verifyDataType   if <code>true</code> the data types will be verified,
     *                         if <code>false</code> will be ignored.
     * @param stopAtFirstError if <code>true</code> the parser will stop at first parsing error,
     *                         if <code>false</code> will ignore non blocking errors.
     */
    public TriXExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    /**
     * Default constructor, with no verification of data types and not stop at first error.
     */
    public TriXExtractor() {
        this(true, true);
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

    @Override
    protected RDFParserBase getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getTriXParser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }


}
