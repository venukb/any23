package org.deri.any23.extractor;

import org.deri.any23.extractor.html.HTMLFixture;
import org.deri.any23.mime.TikaMIMETypeDetector;
import org.deri.any23.vocab.ICAL;
import org.deri.any23.vocab.REVIEW;
import org.deri.any23.writer.CompositeTripleHandler;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.RepositoryWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test case for {@link org.deri.any23.extractor.SingleDocumentExtraction}. 
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class SingleDocumentExtractionTest {

    private static final Logger logger = LoggerFactory.getLogger(SingleDocumentExtractionTest.class);

    private SingleDocumentExtraction singleDocumentExtraction;

    private ExtractorGroup extractorGroup;

    private Sail store;

    private RepositoryConnection conn;

    RepositoryWriter repositoryWriter;

    ByteArrayOutputStream baos;

    RDFXMLWriter rdfxmlWriter;

    @Before
    public void setUp() throws RepositoryException, SailException {
        extractorGroup = ExtractorRegistry.getInstance().getExtractorGroup();
        store = new MemoryStore();
        store.initialize();
        conn = new SailRepository(store).getConnection();
    }

    @After
    public void tearDown() throws SailException, RepositoryException {
        rdfxmlWriter.close();
        repositoryWriter.close();
        logger.info( baos.toString() );

        singleDocumentExtraction = null;
        extractorGroup = null;
        conn.close();
        conn = null;
        store.shutDown();
        store = null;
    }

    /**
     * Tests the existence of the domain triples.
     *
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testMicroformatDomains() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("microformats/microformat-domains.html");
        singleDocumentExtraction.run();
        logStorageContent();
        assertTripleCount(SingleDocumentExtraction.DOMAIN_PROPERTY, "nested.test.com", 1);
    }

    /**
     * Tests the nested microformat relationships. This test verifies the first supported approach
     * for microformat nesting. Such approach foreseen to add a microformat HTML node within the
     * property of a container microformat.
     *
     * For further details see
     * {@link org.deri.any23.extractor.SingleDocumentExtraction#
     * consolidateResources(java.util.List, java.util.List, org.deri.any23.writer.TripleHandler)}
     *
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testNestedMicroformats() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("microformats/nested-microformats-a1.html");
        singleDocumentExtraction.run();

        logStorageContent();

        assertTripleCount(SingleDocumentExtraction.DOMAIN_PROPERTY, "nested.test.com", 2);
        assertTriple(SingleDocumentExtraction.NESTING_PROPERTY, (Value) null);
        assertTriple(SingleDocumentExtraction.NESTING_ORIGINAL_PROPERTY  , ICAL.summary);
        assertTriple(SingleDocumentExtraction.NESTING_STRUCTURED_PROPERTY, (Value) null);
    }

    /**
     *  Tests the nested microformat relationships. This test verifies the second supported approach
     * for microformat nesting. Such approach foreseen to use the same node attributes to declare both
     * a microformat container property and a nested microformat root class.
     *
     * For further details see
     * {@link org.deri.any23.extractor.SingleDocumentExtraction#
     * consolidateResources(java.util.List, java.util.List, org.deri.any23.writer.TripleHandler)}
     *
     * See also the <a href="http://www.google.com/support/webmasters/bin/answer.py?answer=146862">Nested Entities</a>
     * article that is linked by the official microformats.org doc page.
     *
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testNestedMicroformatsInduced() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("microformats/nested-microformats-a2.html");
        singleDocumentExtraction.run();

        logStorageContent();

        assertTripleCount(SingleDocumentExtraction.DOMAIN_PROPERTY, "nested.test.com", 2);
        assertTriple(SingleDocumentExtraction.NESTING_PROPERTY, (Value) null);
        assertTriple(SingleDocumentExtraction.NESTING_ORIGINAL_PROPERTY  , ICAL.summary);
        assertTriple(SingleDocumentExtraction.NESTING_STRUCTURED_PROPERTY, (Value) null);
    }

    /**
     * Tests the nested microformat relationships. This test verifies the behavior of the nested microformats
     * when the nesting relationship is handled by the microformat extractor itself (like the HReview that is
     * able to detect an inner VCard).
     *
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testNestedMicroformatsManaged() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("microformats/nested-microformats-managed.html");
        singleDocumentExtraction.run();

        logStorageContent();

        assertTripleCount(SingleDocumentExtraction.DOMAIN_PROPERTY, "nested.test.com", 3);
        assertTriple(SingleDocumentExtraction.NESTING_PROPERTY, (Value) null);
        assertTriple(SingleDocumentExtraction.NESTING_ORIGINAL_PROPERTY  , REVIEW.hasReview);
        Value object = getTripleObject(null, REVIEW.hasReview);
        assertTriple(SingleDocumentExtraction.NESTING_STRUCTURED_PROPERTY, object);
    }

    private SingleDocumentExtraction getInstance(String file) {
        baos = new ByteArrayOutputStream();
        rdfxmlWriter = new RDFXMLWriter(baos);
        repositoryWriter = new RepositoryWriter(conn);

        final CompositeTripleHandler cth = new CompositeTripleHandler();
        cth.addChild(rdfxmlWriter);
        cth.addChild(repositoryWriter);

        SingleDocumentExtraction instance =  new SingleDocumentExtraction(
                new HTMLFixture(file).getOpener("http://nested.test.com"),
                extractorGroup,
                cth
        );
        instance.setMIMETypeDetector( new TikaMIMETypeDetector() );
        return instance;
    }

    /**
     * Logs the storage content.
     * 
     * @throws RepositoryException
     */
    private void logStorageContent() throws RepositoryException {
        RepositoryResult<Statement> result = conn.getStatements(null, null, null, false);
        while (result.hasNext()) {
            Statement statement = result.next();
            logger.info( statement.toString() );
        }
    }

    /**
     * Asserts that the triple pattern is present within the storage exactly n times.
     * 
     * @param predicate
     * @param value
     * @param occurrences
     * @throws RepositoryException
     */
    private void assertTripleCount(URI predicate, Value value, int occurrences) throws RepositoryException {
        RepositoryResult<Statement> statements = conn.getStatements(
                null, predicate, value, false
        );
        int count = 0;
        while (statements.hasNext()) {
            statements.next();
            count++;
        }
        Assert.assertEquals(
                String.format("Cannot find triple (* %s %s) %d times", predicate, value, occurrences),
                occurrences,
                count
        );
    }

    /**
     * Asserts that the triple pattern is present within the storage exactly n times.
     *
     * @param predicate
     * @param value
     * @param occurrences
     * @throws RepositoryException
     */
    private void assertTripleCount(URI predicate, String value, int occurrences) throws RepositoryException {
        assertTripleCount(predicate, ValueFactoryImpl.getInstance().createLiteral(value), occurrences);
    }

    /**
     * Asserts that a triple exists exactly once.
     *
     * @param predicate
     * @param value
     * @throws RepositoryException
     */
    private void assertTriple(URI predicate, Value value) throws RepositoryException {
        assertTripleCount(predicate, value, 1);
    }

    /**
     * Asserts that a triple exists exactly once.
     *
     * @param predicate
     * @param value
     * @throws RepositoryException
     */
    private void assertTriple(URI predicate, String value) throws RepositoryException {
        assertTriple(predicate, ValueFactoryImpl.getInstance().createLiteral(value) );
    }

    /**
     * Retrieves the triple object matching with the given pattern that is expected to be just one.
     * 
     * @param sub
     * @param prop
     * @return
     * @throws RepositoryException
     */
    private Value getTripleObject(Resource sub, URI prop) throws RepositoryException {
        RepositoryResult<Statement> statements = conn.getStatements(sub, prop, null, false);
        Assert.assertTrue(statements.hasNext());
        Statement statement = statements.next();
        Value value = statement.getObject();
        Assert.assertFalse( "Expected just one result.", statements.hasNext() );
        statements.close();
        return value;
    }

}
