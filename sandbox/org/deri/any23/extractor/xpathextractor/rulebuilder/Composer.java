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

package org.deri.any23.extractor.xpathextractor.rulebuilder;

import org.deri.any23.extractor.xpathextractor.QuadTemplate;
import org.deri.any23.extractor.xpathextractor.TemplateObject;
import org.deri.any23.extractor.xpathextractor.TemplatePredicate;
import org.deri.any23.extractor.xpathextractor.TemplateSubject;
import org.deri.any23.extractor.xpathextractor.TemplateXPathExtractionRuleImpl;
import org.deri.any23.extractor.xpathextractor.Variable;
import org.deri.any23.extractor.xpathextractor.XPathExtractionRule;
import org.deri.any23.extractor.xpathextractor.XPathExtractor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Extractor DSL composer.
 *
 * extractor.match("http://www.rotten.com/articles/*")
 *   .variable('v1', 'xpath1')
 *   .variable('v2', 'xpath2')
 *     .write
 *       .subject.uri('http://sub')
 *       .predicate('http://pred')
 *       .object.literal.variable('v1')
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Composer {

    private final Set<Class<?>> stackPersistant;

    private final Stack<Object> stack = new Stack<Object>();

    private final List<XPathExtractionRule> extractors = new ArrayList<XPathExtractionRule>();

    public Composer() {
        final Set<Class<?>> sp =  new HashSet<Class<?>>();
        sp.add(QuadTemplate.class);
        stackPersistant = Collections.unmodifiableSet(sp);
    }

    public List<XPathExtractionRule> getComposedExtractors() {
        flushStack();
        return extractors;
    }

    public CExtractor getExtractor() {
        flushStack();
        return pushObject( new CExtractor() );
    }

    private void flushStack() {
        if (!stack.isEmpty()) {
            final XPathExtractionRule xPathExtractionRule = processStack();
            extractors.add(xPathExtractionRule);
            stack.clear();
        }
    }

    class CExtractor implements StackProcessable {

        public CMatch match(String pattern) {
            pushObject(pattern);
            return pushObject(new CMatch());
        }

        public Object[] process(Object[] in) {
            final TemplateXPathExtractionRuleImpl extractionRule = new TemplateXPathExtractionRuleImpl(
                    "name",
                    (String) in[in.length - 2]
            );
            final Variable[] vars = filterType(Variable.class, in);
            for (final Variable var : vars) {
                extractionRule.add(var);
            }
            final QuadTemplate[] quadTemplates = filterType(QuadTemplate.class, in);
            for (final QuadTemplate quadTemplate : quadTemplates) {
                extractionRule.add(quadTemplate);
            }
            return new Object[]{extractionRule};
        }
    }

    private <T> T[] filterType(Class<T> type, Object[] in) {
        final List<T> result = new ArrayList<T>();
        for (Object o : in) {
            if (type.isInstance(o)) {
                result.add((T) o);
            }
        }
        return result.toArray((T[]) Array.newInstance(type, result.size()));
    }

    private <T> T filterTypeAndCheckOneInstance(Class<T> type, Object[] in) {
        final T[] filtered = filterType(type, in);
        if (filtered.length != 1) {
            throw new IllegalStateException();
        }
        return filtered[0];
    }

    private boolean containsType(Class type, Object[] in) {
        for (Object o : in) {
            if (type.isInstance(o)) {
                return true;
            }
        }
        return false;
    }

    private int indexOf(Class type, Object[] in) {
        for (int i = 0; i < in.length; i++) {
            if (type.isInstance(in[i])) {
                return i;
            }
        }
        throw new IllegalStateException();
    }

    public class CMatch implements StackProcessable {
        public CMatch variable(String varName, String xPath) {
            pushObject(varName);
            pushObject(xPath);
            return this;
        }

        public CWrite getWrite() {
            return pushObject(new CWrite());
        }

        public Object[] process(Object[] in) {
            List<Object> variables = new ArrayList<Object>();
            for (int i = in.length - 2; i > indexOf(CWrite.class, in); i -= 2) {
                variables.add(new Variable((String) in[i], (String) in[i - 1]));
            }
            return variables.toArray();
        }
    }

    public class CWrite {
        public CSubject getSubject() {
            return pushObject(new CSubject());
        }
    }

    public class CSubjectURI {}

    public class CSubjectBnode {}

    public class CSubjectVar {
        final boolean bnode;

        public CSubjectVar(boolean isBnode) {
            bnode = isBnode;
        }

        public CPostSubjectVar $(String var) {
            pushObject(var);
            return pushObject(new CPostSubjectVar(false));
        }
    }

    public class CSubject implements StackProcessable {
        public CPostSubjectVar uri(String uri) {
            pushObject(new CSubjectURI());
            pushObject(uri);
            return pushObject(new CPostSubjectVar(false));
        }

        public CPostSubjectVar getBnode(String bnode) {
            pushObject(new CSubjectBnode());
            pushObject(bnode);
            return pushObject(new CPostSubjectVar(true));
        }

        public CSubjectVar getUri() {
            return pushObject(new CSubjectVar(false));
        }

        public CSubjectVar getBnode() {
            return pushObject(new CSubjectVar(true));
        }

        public Object[] process(Object[] in) {
            boolean subIsVar = containsType(CSubjectVar.class, in);
            final String subjectVal = (String) in[in.length - 3];
            final TemplateSubject.Type type = in[in.length - 2] instanceof CSubjectURI
                    ?
                    TemplateSubject.Type.uri : TemplateSubject.Type.bnode;
            final TemplateSubject subject = new TemplateSubject(type, subjectVal, subIsVar);
            final QuadTemplate newQuadTemplate = new QuadTemplate(
                    subject,
                    filterTypeAndCheckOneInstance(TemplatePredicate.class, in),
                    filterTypeAndCheckOneInstance(TemplateObject.class, in),
                    null
            );
            return new Object[]{newQuadTemplate};
        }
    }

    private Object[] processPredicate(Object[] in) {
        ///////  REMOVE IT
        if (!(in[in.length - 2] instanceof String)) {
            return new Object[]{in[in.length - 2], in[in.length - 3]};
        }
        ///////
        final boolean isVar = containsType(CPredicateVar.class, in);
        final String predicateStr = (String) in[in.length - 2];
        return new Object[]{
                filterTypeAndCheckOneInstance(TemplateObject.class, in),
                new TemplatePredicate(predicateStr, isVar)
        };
    }

    public class CPostSubjectVar implements StackProcessable {
        final boolean bnode;

        public CPostSubjectVar(boolean bnode) {
            this.bnode = bnode;
        }

        public CPostPredicate predicate(String value) {
            pushObject(value);
            return pushObject(new CPostPredicate());
        }

        public CPredicateVar predicate() {
            return pushObject(new CPredicateVar());
        }

        public Object[] process(Object[] in) {
            return processPredicate(in);
        }
    }

    public class CPredicate {
        public CPredicateURI getURI(String predicate) {
            pushObject(predicate);
            return pushObject(new CPredicateURI());
        }

        public CPredicateVar getURI() {
            return pushObject(new CPredicateVar());
        }
    }

    public class CPredicateURI {
        public CPostPredicate getObject() {
            return pushObject(new CPostPredicate());
        }
    }

    public class CPredicateVar implements StackProcessable {
        public CPostPredicate $(String var) {
            pushObject(var);
            return pushObject(new CPostPredicate());
        }

        @Override
        public Object[] process(Object[] in) {
            return processPredicate(in);
        }
    }

    public class CPostPredicate {
        public CObject getObject() {
            return pushObject(new CObject());
        }
    }

    public class CObject implements StackProcessable {
        public CObjectURI uri(String value) {
            pushObject(new CObjectURI());
            pushObject(value);
            return new CObjectURI();
        }

        public CObjectVariable getUri() {
            return pushObject(new CObjectVariable());
        }

        public CObjectBnode getBnode(String bnode) {
            pushObject(new CObjectBnode());
            pushObject(bnode);
            return new CObjectBnode();
        }

        public CObjectVariable getBnode() {
            pushObject(new CObjectBnode());
            return pushObject(new CObjectVariable());
        }

        public CObjectLiteral literal(String value) {
            pushObject(new CObjectLiteral());
            pushObject(value);
            return new CObjectLiteral();
        }

        public CObjectVariable literal() {
            pushObject(new CObjectLiteral());
            return pushObject(new CObjectVariable());
        }

        public Object[] process(Object[] in) {
            final boolean isVar = containsType(CObjectVariable.class, in);
            final Object typeObject = in[in.length - 2];
            final TemplateObject.Type type;
            if (typeObject instanceof CObjectLiteral) {
                type = TemplateObject.Type.literal;
            } else if (typeObject instanceof CObjectBnode) {
                type = TemplateObject.Type.bnode;
            } else if (typeObject instanceof CObjectURI) {
                type = TemplateObject.Type.uri;
            } else {
                throw new IllegalStateException();
            }
            final String internalValue = (String) in[isVar ? in.length - 4 : in.length - 3];
            return new Object[]{
                    new TemplateObject(type, internalValue, isVar)
            };
        }
    }

    public class CObjectURI {
        public CWrite getWrite() {
            return new CWrite();
        }
    }

    public class CObjectBnode {
        public CWrite getWrite() {
            return new CWrite();
        }
    }

    public class CObjectLiteral {
        public CWrite getWrite() {
            return new CWrite();
        }
    }

    public class CObjectVariable {
        public CObjectVariableWrite $(String value) {
            pushObject(value);
            return new CObjectVariableWrite();
        }
    }

    public class CObjectVariableWrite {
        public CWrite getWrite() {
            return new CWrite();
        }
    }

    /*
      'v1'
      variable
      literal
      object         *  Object
      'http://pred'
      predicate      *  Predicate
      'http://sub'
      uri
      subject        *  Object Predicate
      write
      'xpath2'
      'v2'
      variable
      'xpath1'
      'v1'
      variable
      'http://www.rotten.com/articles/*'
      match          *
      extractor

    */

    protected <T> T processStack() {
        final List<Object> poppedElements = new ArrayList<Object>();
        Object current;
        while (stack.size() > 0) {
            current = stack.pop();
            poppedElements.add(current);
            if (current instanceof StackProcessable) {
                final StackProcessable stackProcessable = (StackProcessable) current;
                final Object[] outs = ((StackProcessable) current).process(poppedElements.toArray());
                for (Object out : outs) {
                    stack.push(out);
                }
                for (Object poppedElement : poppedElements) {
                    if (stackPersistant.contains(poppedElement.getClass())) {
                        stack.push(poppedElement);
                    }
                }
                poppedElements.clear();
            }
        }

        removeStackPersistentObjects(poppedElements);
        if (poppedElements.size() != 1) {
            throw new IllegalStateException();
        }
        return (T) poppedElements.get(0);
    }

    private void removeStackPersistentObjects(List in) {
        List nonPersistent = new ArrayList();
        for (Object o : in) {
            if (!stackPersistant.contains(o.getClass())) {
                nonPersistent.add(o);
            }
        }
        in.clear();
        in.addAll(nonPersistent);
    }

    private <O> O pushObject(O o) {
        stack.push(o);
        return o;
    }

    interface StackProcessable {
        Object[] process(Object[] in);
    }
}
