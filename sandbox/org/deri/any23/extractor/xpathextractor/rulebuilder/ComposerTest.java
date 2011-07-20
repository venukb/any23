package org.deri.any23.extractor.xpathextractor.rulebuilder;

import org.deri.any23.extractor.xpathextractor.XPathExtractionRule;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ComposerTest {

    private static final Logger logger = LoggerFactory.getLogger(ComposerTest.class);

    @Test
    public void testComposition() {
        createComposer();
    }

    @Test
    public void testProcessStack() {
        final Composer composer = createComposer();
        final XPathExtractionRule xPathExtractionRule = composer.processStack();
        logger.info( xPathExtractionRule.toString() );
    }

    @Test
    public void testScripting() throws ScriptException {
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        final ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("js");
        final Bindings bindings = scriptEngine.createBindings();
        final Composer composer = new Composer();
        final Out out = new Out();
        bindings.put("composer", composer);
        bindings.put("out", out);
        scriptEngine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        final String SCRIPT =
                "var v = " +
                        "composer.extractor.match('http://www.xxx.com')" +
                        "   .variable('v1', 'a/b/c')  " +
                        "   .variable('v2', 'd/e/f') " +
                        "   .write" +
                        "   .subject.uri('http://sub').predicate('http://pred').object.uri('http://obj')" +
                        "   .write" +
                        "   .subject.bnode.$('v1').predicate().$('v2').object.literal('this is a literal')" +
                        "   .write" +
                        "   .subject.uri('http://').predicate('http://pred').object.literal().$('v1');" +
                        "for(var key in v) { out.print(key + '');}";
        scriptEngine.eval(SCRIPT);
        scriptEngine.eval(SCRIPT);

        final List<XPathExtractionRule> extractorList = composer.getComposedExtractors();
        Assert.assertEquals(2, extractorList.size());
        System.out.println("XXX" + out.getOutString());
    }

    private Composer createComposer() {
        final Composer composer = new Composer();
        composer.getExtractor()
                .match("http://www.site.com/articles/*")
                .variable("v1", "/x/path/exp[1]")
                .variable("v2", "/x/path/exp[2]")
                .variable("v3", "/x/path/exp[3]")
                .variable("v4", "/x/path/exp[4]")
                .variable("v5", "/x/path/exp[5]")
                .variable("v6", "/x/path/exp[6]")
                .getWrite()
                .getSubject().uri("http://sub1").predicate("http://pred1").getObject().uri("http://obj1")
                .getWrite()
                .getSubject().uri("http://sub2").predicate("http://pred2").getObject().uri("http://obj2")
                .getWrite()
                .getSubject().getBnode().$("v1").predicate().$("v2").getObject().literal("A literal.")
                .getWrite()
                .getSubject().uri("http://sub3").predicate("http://pred3").getObject().literal().$("v3")
                .getWrite()
                .getSubject().getUri().$("v4").predicate("http://pred4").getObject().getBnode("1234")
                .getWrite()
                .getSubject().getUri().$("v5").predicate("http://pre5").getObject().getBnode().$("v6");
        return composer;
    }

    class Out {
        final StringBuilder sb = new StringBuilder();
        public void print(String str) {
            sb.append(str).append('\n');
        }
        String getOutString() {
            return sb.toString();
        }
    }


}
