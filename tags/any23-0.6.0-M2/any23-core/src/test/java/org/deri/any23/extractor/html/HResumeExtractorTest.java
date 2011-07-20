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

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.DOAC;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.SINDICE;
import org.deri.any23.vocab.VCARD;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.HashSet;
import java.util.Set;

/**
 * Reference Test class for the {@link org.deri.any23.extractor.html.HResumeExtractor} extractor.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class HResumeExtractorTest extends AbstractExtractorTestCase {

    private static final SINDICE vSINDICE = SINDICE.getInstance();
    private static final FOAF    vFOAF    = FOAF.getInstance();
    private static final DOAC    vDOAC    = DOAC.getInstance();
    private static final VCARD   vVCARD   = VCARD.getInstance();

    protected ExtractorFactory<?> getExtractorFactory() {
        return HResumeExtractor.factory;
    }

    @Test
    public void testNoMicroformats() throws RepositoryException {
        assertExtracts("html/html-without-uf.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 2);
        assertStatementsSize(vSINDICE.getProperty(SINDICE.DATE), (Value) null, 1);
        assertStatementsSize(vSINDICE.getProperty(SINDICE.SIZE), (Value) null, 1);
    }

    @Test
    public void testLinkedIn() throws RepositoryException {
        assertExtracts("microformats/hresume/steveganz.html");
        Assert.assertFalse(conn.isEmpty());
        assertStatementsSize(RDF.TYPE, vFOAF.Person, 1);

        Resource person = findExactlyOneBlankSubject(RDF.TYPE, vFOAF.Person);

        assertContains(person, vDOAC.summary, (Resource) null);

        assertContains(
                person,
                vDOAC.summary,
                "Steve Ganz is passionate about connecting people,\n" +
                        "semantic markup, sushi, and disc golf - not necessarily in that order.\n" +
                        "Currently obsessed with developing the user experience at LinkedIn,\n" +
                        "Steve is a second generation Silicon Valley geek and a veteran web\n" +
                        "professional who has been building human-computer interfaces since 1994.");


        assertContains(person, vFOAF.isPrimaryTopicOf, (Resource) null);

        assertStatementsSize(RDF.TYPE, vVCARD.VCard, 0);

        assertStatementsSize(vDOAC.experience , (Value) null, 7);
        assertStatementsSize(vDOAC.education  , (Value) null, 2);
        assertStatementsSize(vDOAC.affiliation, (Value) null, 8);
    }

    @Test
    public void testLinkedInComplete() throws RepositoryException {

        assertExtracts("microformats/hresume/steveganz.html");
        Assert.assertFalse(conn.isEmpty());

        assertStatementsSize(RDF.TYPE, vFOAF.Person, 1);

		assertStatementsSize(vDOAC.experience , (Value) null, 7 );
		assertStatementsSize(vDOAC.education  , (Value) null, 2 );
		assertStatementsSize(vDOAC.affiliation, (Value) null, 8 );
		assertStatementsSize(vDOAC.skill      , (Value) null, 17);

        RepositoryResult<Statement> statements = conn.getStatements(null, vDOAC.organization, null, false);

        Set<String> checkSet = new HashSet<String>();

        try {
            while(statements.hasNext()) {
                Statement statement = statements.next();
                checkSet.add(statement.getObject().stringValue());
                System.out.println(statement.getObject().stringValue());
            }

        } finally {
            statements.close();
        }

        String[] names = new String[]{
                "LinkedIn Corporation",
                "PayPal, an eBay Company",
                "McAfee, Inc.",
                "Printable Technologies",
                "Collabria, Inc.",
                "Self-employed",
                "3G Productions",
                "Lee Strasberg Theatre and Film\n" +
                        "\tInstitute",
                "Leland High School"};

        for(String name: names)
            Assert.assertTrue(checkSet.contains(name));

		Resource person = findExactlyOneBlankSubject(RDF.TYPE, vFOAF.Person);
        assertContains(person, vFOAF.isPrimaryTopicOf, (Value) null);
        findExactlyOneObject(person, vFOAF.isPrimaryTopicOf);
    }

    @Test
    public void testAnt() throws RepositoryException {
        assertExtracts("microformats/hresume/ant.html");
        Assert.assertFalse(conn.isEmpty());
        assertModelNotEmpty();

        assertStatementsSize(RDF.TYPE, vFOAF.Person, 1);


        Resource person = findExactlyOneBlankSubject(RDF.TYPE, vFOAF.Person);
        assertContains(person, vDOAC.summary, (Resource) null);

        assertContains(
                person,
                vDOAC.summary,
                "Senior Systems\n              Analyst/Developer.\n              " +
                        "Experienced in the analysis, design and\n              " +
                        "implementation of distributed, multi-tier\n              " +
                        "applications using Microsoft\n              technologies.\n" +
                        "              Specialising in data capture applications on the\n" +
                        "              Web.");


        assertContains(person, vFOAF.isPrimaryTopicOf, (Resource) null);

        assertStatementsSize(RDF.TYPE, vVCARD.VCard, 0);

        assertStatementsSize(vDOAC.experience , (Value) null, 16);
        assertStatementsSize(vDOAC.education  , (Value) null, 2 );
        assertStatementsSize(vDOAC.affiliation, (Value) null, 0 );
        assertStatementsSize(vDOAC.skill      , (Value) null, 4 );
    }

}