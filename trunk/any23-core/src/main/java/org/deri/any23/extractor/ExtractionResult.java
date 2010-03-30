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

package org.deri.any23.extractor;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Interface defining the methods that a representation of an extraction result must have.
 */
public interface ExtractionResult extends ErrorReporter {

    /**
     * Returns the extraction context associated to this extraction result.
     *
     * @return a valid extraction context.
     */
    ExtractionContext getExtractionContext();

    /**
     * Write a triple.
     * Parameters can be null, then the triple will be silently ignored.
     *
     * @param s Subject
     * @param p Predicate
     * @param o Object
     */
    void writeTriple(Resource s, URI p, Value o);

    /**
     * Write a namespace.
     *
     * @param prefix the prefix of the namespace
     * @param uri    the long URI identifying the namespace
     */
    void writeNamespace(String prefix, String uri);

    /**
     * Close the result.
     * <p/>
     * Extractors should close their results as soon as possible, but
     * don't have to, the environment will close any remaining ones.
     * Implementations should be robust against multiple close()
     * invocations.
     */
    void close();

    /**
     * Open a result nested in the current one.
     *
     * @param context
     * @return the instance of the nested extraction result.
     */
    ExtractionResult openSubResult(Object context);

}