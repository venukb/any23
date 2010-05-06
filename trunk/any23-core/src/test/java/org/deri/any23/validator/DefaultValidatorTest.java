package org.deri.any23.validator;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.deri.any23.extractor.html.TagSoupParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Test case for {@link org.deri.any23.validator.DefaultValidator}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultValidatorTest {

    private static final Logger logger = LoggerFactory.getLogger(DefaultValidatorTest.class);

    private DefaultValidator validator;

    @Before
    public void setUp() {
        validator = new DefaultValidator();
    }

    @After
    public void tearDown() {
        validator = null;
    }

    @Test
    public void testRegisterRule() {
        validator.addRule(FakeRule.class, FakeFix.class);
        List<Class<? extends Fix>> fixes = validator.getFixes(FakeRule.class);
        Assert.assertEquals("Unexpected fixes size.", 1, fixes.size());
        Assert.assertEquals("Unexpected fix.", FakeFix.class,  fixes.get(0));
        validator.removeRule(FakeRule.class);
        Assert.assertEquals("Unexpected fixes size.", 0, validator.getFixes(FakeRule.class).size());
    }

    @Test
    public void testMissingOGNamespace() throws IOException, ValidatorException {
        DOMDocument document = loadDocument("missing-og-namespace.html");
        Assert.assertNull( document.getNode("/HTML").getAttributes().getNamedItem("xmlns:og") );
        Report report = validator.validate(document, true);
        Assert.assertNotNull( document.getNode("/HTML").getAttributes().getNamedItem("xmlns:og") );
        if(logger.isDebugEnabled()) {
            logger.debug( report.toString() );
        }
    }

    @Test
    public void testMetaNameMisuse() throws Exception, ValidatorException {
        DOMDocument document = loadDocument("meta-name-misuse.html");
        Report report = validator.validate(document, true);
        logger.info( report.toString() );
        if(logger.isDebugEnabled()) {
            logger.debug( serialize(document) );
        }

        List<Node> metas = document.getNodes("/HTML/HEAD/META");
        for(Node meta : metas) {
            Node name = meta.getAttributes().getNamedItem("name");
            if(name != null) {
                Assert.assertFalse( name.getTextContent().contains(":") );
            }
        }
    }

    private DOMDocument loadDocument(String document) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(document);
        TagSoupParser tsp = new TagSoupParser(is, "http://test.com");
        return new DefaultDOMDocument( tsp.getDOM() );
    }

    private String serialize(DOMDocument document) throws Exception {
        OutputFormat format = new OutputFormat(document.getOriginalDocument());
        format.setLineSeparator("\n");
        format.setIndenting(true);
        format.setLineWidth(0);
        format.setPreserveSpace(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer(
                baos,
                format
        );
        serializer.asDOMSerializer();
        serializer.serialize( document.getOriginalDocument() );
        return baos.toString();
    }

    class FakeRule implements Rule {
        public boolean applyOn(DOMDocument document, RuleContext context, Report report) {
            throw new UnsupportedOperationException();
        }
    }

    class FakeFix implements Fix {
        public void execute(Rule rule, RuleContext context, DOMDocument document) {
              throw new UnsupportedOperationException();
        }
    }

}
