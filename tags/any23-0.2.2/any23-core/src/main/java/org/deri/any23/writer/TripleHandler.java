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

package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/*
   TODO: #2 - Throw a TripleHandlerException from all methods (maybe unchecked?),
         and use it in implementing classes instead of RuntimeException,
         e.g. in {@link RDFWriterTripleHandler} and {@link RepositoryWriter}
 */

/**
 * Defines a document based triple handler.
 */
public interface TripleHandler {

    void startDocument(URI documentURI);

    /**
     * Informs the handler that a new context has been established.
     * Contexts are not guaranteed to receive any triples, so they
     * might be closed without any triple.
     */
    void openContext(ExtractionContext context);

    /**
     * Invoked with a currently open context,
     * notifies the detection of a triple.
     *
     * @param s triple subject.
     * @param p triple predicate.
     * @param o triple object.
     * @param context extraction context.
     */
    void receiveTriple(Resource s, URI p, Value o, ExtractionContext context);

    /**
     * Invoked with a currently open context, notifies the detection of a
     * namespace.
     *
     * @param prefix namespace prefix.
     * @param uri namespace <i>URI</i>.
     * @param context namespace context.
     */
    void receiveNamespace(String prefix, String uri, ExtractionContext context);

    /**
     * Informs the handler that no more triples will come from a
     * previously opened context. All contexts are guaranteed to
     * be closed before the final close(). The document context
     * for each document is guaranteed to be closed after all
     * local contexts of that document.
     *
     * @param context the context to be closed.
     */
    void closeContext(ExtractionContext context);

    /**
     * Informa the handler that the end of the document
     * has been reached.
     *
     * @param documentURI document URI.
     */
    void endDocument(URI documentURI);

    /**
     * Sets the length of the content to be processed.
     *
     * @param contentLength
     */
    void setContentLength(long contentLength);

    /**
     * Will be called last and exactly once.
     */
    void close();

}