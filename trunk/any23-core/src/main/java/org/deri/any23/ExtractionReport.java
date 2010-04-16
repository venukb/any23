/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23;


/**
 * This class contains some statistics and general information about
 * an extraction.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionReport {

    private boolean hasMatchingExtractors;

    private String encoding;

    private String detectedMimeType;

    public ExtractionReport(boolean hasMatchingExtractors, String encoding, String detectedMimeType) {
        this.hasMatchingExtractors = hasMatchingExtractors;
        this.encoding              = encoding;
        this.detectedMimeType      = detectedMimeType;
    }

    /**
     * @return <code>true</code> if the extraction has activated
     *         at least an extractor, <code>false</code> otherwise.
     */
    public boolean hasMatchingExtractors() {
        return hasMatchingExtractors;
    }

    /**
     * @return the detected encoding for the source stream.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @return the tetected mimetype for the input stream.      
     */
    public String getDetectedMimeType() {
        return detectedMimeType;
    }
}
