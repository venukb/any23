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

package org.deri.any23.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction for opening an {@link InputStream} on an
 * HTTP URI.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface HTTPClient {

    public abstract void init(String userAgent, String acceptHeader);

    public abstract InputStream openInputStream(String uri) throws IOException;

    /**
     * Release all static resources help by the instance. Call this
     * method only if you are sure you will not use it again in your
     * application, like for example when shutting down a servlet
     * context.
     */
    public abstract void close();

    /**
     * The value of the Content-Type header reported by the server.
     * Can be <tt>null</tt>.
     */
    public abstract String getContentType();

    public abstract long getContentLength();

    /**
     * Returns the actual URI from which the document was fetched.
     * This might differ from the URI passed to openInputStream()
     * if a redirect was performed. A return value of <tt>null</tt>
     * means that the URI is unchanged and the original URI was used.
     */
    public abstract String getActualDocumentURI();
    
}