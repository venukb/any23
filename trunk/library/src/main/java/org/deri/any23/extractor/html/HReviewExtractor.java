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

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.vocab.REVIEW;
import org.deri.any23.vocab.VCARD;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hreview">hReview</a>
 * microformat.
 *
 * @author Gabriele Renzi
 */
public class HReviewExtractor extends EntityBasedMicroformatExtractor {

    protected String getBaseClassName() {
        return "hreview";
    }

    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        BNode rev = getBlankNodeFor(node);
        out.writeTriple(rev, RDF.TYPE, REVIEW.Review);
        HTMLDocument fragment = new HTMLDocument(node);
        addRating(fragment, rev);
        addSummary(fragment, rev);
        addTime(fragment, rev);
        addType(fragment, rev);
        addDescription(fragment, rev);
        addItem(fragment, rev);
        addReviewer(fragment, rev);
        return true;
    }


    private void addType(HTMLDocument doc, Resource rev) {
        String value = doc.getSingularTextField("type");
        conditionallyAddStringProperty(rev, REVIEW.type, value);
    }

    private void addReviewer(HTMLDocument doc, Resource rev) {
        List<Node> nodes = doc.findAllByClassName("reviewer");
        if (nodes.size() > 0)
            out.writeTriple(rev, REVIEW.reviewer, getBlankNodeFor(nodes.get(0)));
    }

    private void addItem(HTMLDocument root, Resource rev) throws ExtractionException {
        List<Node> nodes = root.findAllByClassName("item");
        for (Node node : nodes) {
            Resource item = findDummy(new HTMLDocument(node));
            out.writeTriple(item, REVIEW.hasReview, rev);
        }
    }

    private Resource findDummy(HTMLDocument item) throws ExtractionException {
        Resource blank = getBlankNodeFor(item.getDocument());
        String val = item.getSingularTextField("fn");
        conditionallyAddStringProperty(blank, VCARD.fn, val);
        val = item.getSingularUrlField("url");
        conditionallyAddResourceProperty(blank, VCARD.url, document.resolveURI(val));
        String pics[] = item.getPluralUrlField("photo");
        for (String pic : pics) {
            out.writeTriple(blank, VCARD.photo, document.resolveURI(pic));
        }
        return blank;
    }

    private void addRating(HTMLDocument doc, Resource rev) {
        String value = doc.getSingularTextField("rating");
        conditionallyAddStringProperty(rev, REVIEW.rating, value);
    }


    private void addSummary(HTMLDocument doc, Resource rev) {
        String value = doc.getSingularTextField("summary");
        conditionallyAddStringProperty(rev, REVIEW.title, value);
    }

    private void addTime(HTMLDocument doc, Resource rev) {
        String value = doc.getSingularTextField("dtreviewed");
        conditionallyAddStringProperty(rev, DCTERMS.date, value);
    }

    private void addDescription(HTMLDocument doc, Resource rev) {
        String value = doc.getSingularTextField("description");
        conditionallyAddStringProperty(rev, REVIEW.text, value);
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

    public final static ExtractorFactory<HReviewExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-hreview",
                    PopularPrefixes.createSubset("rdf", "vcard", "rev"),
                    Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                    null,
                    HReviewExtractor.class);
}