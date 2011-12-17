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

package org.deri.any23.vocab;

import org.deri.any23.io.nquads.NQuadsWriter;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.util.DiscoveryUtils;
import org.deri.any23.util.StringUtils;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * This class provides a set of methods for generating
 * <a href="http://www.w3.org/TR/rdf-schema/">RDF Schema</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFSchemaUtils {

    private static final String RDF_XML_SEPARATOR = StringUtils.multiply('=', 100);
    
    /**
     * Supported formats for vocabulary serialization.
     */
    public enum VocabularyFormat {
        NQuads,
        NTriples,
        RDFXML
    }

    /**
     * Serializes a vocabulary composed of the given <code>namespace</code>,
     * <code>resources</code> and <code>properties</code>.
     *
     * @param namespace vocabulary namespace.
     * @param classes list of classes.
     * @param properties list of properties.
     * @param comments map of resource comments.
     * @param writer writer to print out the RDF Schema triples.
     * @throws RDFHandlerException
     */
    public static void serializeVocabulary(
            URI namespace,
            URI[] classes,
            URI[] properties,
            Map<URI,String> comments,
            RDFWriter writer
    ) throws RDFHandlerException {
        writer.startRDF();
        for(URI clazz : classes) {
            writer.handleStatement( RDFUtils.quad(clazz, RDF.TYPE, RDFS.CLASS  , namespace) );
            writer.handleStatement( RDFUtils.quad(clazz, RDFS.MEMBER, namespace, namespace) );
            final String comment = comments.get(clazz);
            if(comment != null)
                writer.handleStatement( RDFUtils.quad(clazz, RDFS.COMMENT, RDFUtils.literal(comment), namespace) );
        }
        for(URI property : properties) {
            writer.handleStatement(RDFUtils.quad(property, RDF.TYPE, RDF.PROPERTY, namespace));
            writer.handleStatement(RDFUtils.quad(property, RDFS.MEMBER, namespace, namespace));
            final String comment = comments.get(property);
            if(comment != null)
                writer.handleStatement( RDFUtils.quad(property, RDFS.COMMENT, RDFUtils.literal(comment), namespace) );
        }
        writer.endRDF();
    }

    /**
     * Serializes the given <code>vocabulary</code> to triples over the given <code>writer</code>.
     *
     * @param vocabulary vocabulary to be serialized.
     * @param writer output writer.
     * @throws RDFHandlerException
     */
    public static void serializeVocabulary(Vocabulary vocabulary, RDFWriter writer)
    throws RDFHandlerException {
        serializeVocabulary(
                vocabulary.getNamespace(),
                vocabulary.getClasses(),
                vocabulary.getProperties(),
                vocabulary.getComments(),
                writer
        );
    }

    /**
     * Serializes the given <code>vocabulary</code> to <i>NQuads</i> over the given output stream.
     *
     * @param vocabulary vocabulary to be serialized.
     * @param format output format for vocabulary.
     * @param willFollowAnother if <code>true</code> another vocab will be printed in the same stream.
     * @param ps output stream.
     * @throws RDFHandlerException
     */
    public static void serializeVocabulary(
            Vocabulary vocabulary,
            VocabularyFormat format,
            boolean willFollowAnother,
            PrintStream ps
    ) throws RDFHandlerException {
        final RDFWriter rdfWriter;
        if(format == VocabularyFormat.RDFXML) {
            rdfWriter = new RDFXMLWriter(ps);
            if(willFollowAnother)
                ps.print("\n");
                ps.print(RDF_XML_SEPARATOR);
                ps.print("\n");
        } else if(format == VocabularyFormat.NTriples) {
            rdfWriter = new NTriplesWriter(ps);
        } else if(format == VocabularyFormat.NQuads) {
            rdfWriter = new NQuadsWriter(ps);
        }
        else {
            throw new IllegalArgumentException("Unsupported format " + format);
        }
        serializeVocabulary(vocabulary, rdfWriter);
    }

    /**
     * Serialized the given <code>vocabulary</code> to <i>NQuads</i> and return them as string.
     *
     * @param vocabulary vocabulary to be serialized.
     * @param format output format for vocabulary.
     * @return string contained serialization.
     * @throws RDFHandlerException
     */
    public static String serializeVocabulary(Vocabulary vocabulary, VocabularyFormat format)
    throws RDFHandlerException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        serializeVocabulary(vocabulary, format, false, ps);
        ps.close();
        return baos.toString();
    }

    /**
     * Serializes all the vocabularies to <i>NQuads</i> over the given output stream.
     *
     * @param format output format for vocabularies.
     * @param ps output print stream.
     */
    public static void serializeVocabularies(VocabularyFormat format, PrintStream ps) {
        final Class vocabularyClass = Vocabulary.class;
        final List<Class> vocabularies = DiscoveryUtils.getClassesInPackage(
                vocabularyClass.getPackage().getName(),
                vocabularyClass
        );
        int currentIndex = 0;
        for (Class vocabClazz : vocabularies) {
            final Vocabulary instance;
            try {
                final Constructor constructor = vocabClazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                instance = (Vocabulary) constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error while instantiating vocabulary class " + vocabClazz, e);
            }
            try {
                serializeVocabulary(instance, format, currentIndex < vocabularies.size() - 2, ps);
            } catch (RDFHandlerException rdfhe) {
                throw new RuntimeException("Error while serializing vocabulary.", rdfhe);
            }
        }
    }

    private RDFSchemaUtils() {}

}
