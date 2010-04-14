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

import java.util.Arrays;
import java.util.List;

/**
 * This interface models a specific {@link org.deri.any23.extractor.ExtractionResult}
 * able to collect resource roots generated by <i>HTML Microformat</i> extractions.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TagSoupExtractionResult extends ExtractionResult {

    /**
     * Adds a root resource to the extraction result, specifying also
     * the <i>path</i> corresponding to the root of data which generated the resource
     * and the extractor responsible for such addition.
     *
     * @param path the <i>path</i> from the document root to the local root of the data generating the resource.
     * @param root the resource root node.
     * @param extractor the extractor responsible of such extraction.
     */
    void addResourceRoot(String[] path, Resource root, String extractor);

    /**
     * Returns all the collected resource roots.
     *
     * @return an <b>unmodifiable</b> list of
     *         {@link org.deri.any23.extractor.TagSoupExtractionResult.ResourceRoot}s.
     */
    List<ResourceRoot> getResourceRoots();

    /**
     * Adds a property path to the list of the extracted data.
     *
     * @param propertySubject the subject of the property.
     * @param property the property URI.
     * @param path the path of the <i>HTML</i> node from which the property literal has been extracted.
     */
    void addPropertyPath(Resource propertySubject, Resource property, String[] path);

    /**
     * Returns all the collected property paths.
     *
     * @return a valid list of property paths.
     */
    List<PropertyPath> getPropertyPaths();

    /**
     * Defines a resource root object.
     */
    class ResourceRoot {
        private String[] path;
        private Resource root;
        private String   extractor;

        public ResourceRoot(String[] path, Resource root, String extractor) {
            if(path == null || path.length == 0) {
                throw new IllegalArgumentException( String.format("Invalid xpath: '%s'.", Arrays.toString(path) ) );
            }
            if(root == null) {
                throw new IllegalArgumentException("Invalid root, cannot be null.");
            }
            if(extractor == null) {
                throw new IllegalArgumentException("Invalid extractor, cannot ne null");
            }
            this.path      = path;
            this.root      = root;
            this.extractor = extractor;
        }

        public String[] getPath() {
            return path;
        }

        public Resource getRoot() {
            return root;
        }

        public String getExtractor() {
            return extractor;
        }

        @Override
        public String toString() {
            return String.format(
                    "%s-%s-%s %s",
                    this.getClass().getCanonicalName(),
                    Arrays.toString(path), 
                    root,
                    extractor
            );
        }
    }

    /**
     * Defines a property path object.
     */
    class PropertyPath {
        private String[] path;
        private Resource resource;

        public PropertyPath(String[] path, Resource resource) {
            this.path = path;
            this.resource = resource;
        }

        public String[] getPath() {
            return path;
        }

        public Resource getResource() {
            return resource;
        }

        @Override
         public String toString() {
            return String.format("%s %s - %s", this.getClass().getCanonicalName(), Arrays.toString(path), resource);
        }
    }

}
