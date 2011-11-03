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

package org.deri.any23.extractor;

import org.deri.any23.validator.ValidationReport;

import java.util.Collection;
import java.util.Map;

/**
 * This class provides the report for a {@link org.deri.any23.extractor.SingleDocumentExtraction} run.
 *
 * @see org.deri.any23.extractor.SingleDocumentExtraction
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SingleDocumentExtractionReport {

    private final ValidationReport validationReport;

    private final Map<String, Collection<ErrorReporter.Error>> extractorToErrors;

    public SingleDocumentExtractionReport(
            ValidationReport validationReport,
            Map<String, Collection<ErrorReporter.Error>> extractorToErrors
    ) {
        if(validationReport  == null) throw new NullPointerException("validation report cannot be null.");
        if(extractorToErrors == null) throw new NullPointerException("extractor errors map cannot be null.");
        this.validationReport  = validationReport;
        this.extractorToErrors = extractorToErrors;
    }

    public ValidationReport getValidationReport() {
        return validationReport;
    }

    public Map<String, Collection<ErrorReporter.Error>> getExtractorToErrors() {
        return extractorToErrors;
    }

}
