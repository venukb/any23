package org.deri.any23.extractor;

import org.deri.any23.extractor.html.AdrExtractor;
import org.deri.any23.extractor.html.GeoExtractor;
import org.deri.any23.extractor.html.HCalendarExtractor;
import org.deri.any23.extractor.html.HCardExtractor;
import org.deri.any23.extractor.html.HListingExtractor;
import org.deri.any23.extractor.html.HResumeExtractor;
import org.deri.any23.extractor.html.HReviewExtractor;
import org.deri.any23.extractor.html.HeadLinkExtractor;
import org.deri.any23.extractor.html.ICBMExtractor;
import org.deri.any23.extractor.html.LicenseExtractor;
import org.deri.any23.extractor.html.TitleExtractor;
import org.deri.any23.extractor.html.XFNExtractor;
import org.deri.any23.extractor.rdf.NTriplesExtractor;
import org.deri.any23.extractor.rdf.RDFXMLExtractor;
import org.deri.any23.extractor.rdf.TurtleExtractor;
import org.deri.any23.extractor.rdfa.RDFaExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Singleton class acting as a register for all the various {@link org.deri.any23.extractor.Extractor}.
 * 
 */
public class ExtractorRegistry {

    /**
     * instance
     */
    private static ExtractorRegistry instance = null;

    /**
     * maps containing the related {@link org.deri.any23.extractor.ExtractorFactory} for each
     * registered {@link org.deri.any23.extractor.Extractor}
     */
    private Map<String, ExtractorFactory<?>> factories = new HashMap<String, ExtractorFactory<?>>();


    /**
     * @return returns the {@link org.deri.any23.extractor.ExtractorRegistry} instance
     */
    public static ExtractorRegistry get() {
        // TODO (low): this method should be called getInstance
        // Thread-safe
        synchronized (ExtractorRegistry.class) {
            if (instance == null) {
                instance = new ExtractorRegistry();
                instance.register(RDFXMLExtractor.factory);
                instance.register(TurtleExtractor.factory);
                instance.register(NTriplesExtractor.factory);
                instance.register(RDFaExtractor.factory);
                instance.register(HeadLinkExtractor.factory);
                instance.register(LicenseExtractor.factory);
                instance.register(TitleExtractor.factory);
                instance.register(XFNExtractor.factory);
                instance.register(ICBMExtractor.factory);
                instance.register(AdrExtractor.factory);
                instance.register(GeoExtractor.factory);
                instance.register(HCalendarExtractor.factory);
                instance.register(HCardExtractor.factory);
                instance.register(HListingExtractor.factory);
                instance.register(HResumeExtractor.factory);
                instance.register(HReviewExtractor.factory);
            }
        }
        return instance;
    }

    /**
     * Registers an {@link org.deri.any23.extractor.ExtractorFactory}.
     *
     * @param factory
     * @throws IllegalArgumentException if trying to register a {@link org.deri.any23.extractor.ExtractorFactory}
     * with a that already exists in the registry.
     */
    public void register(ExtractorFactory<?> factory) {
        if (factories.containsKey(factory.getExtractorName())) {
            throw new IllegalArgumentException(String.format("Extractor name clash: %s",
                    factory.getExtractorName()));
        }
        factories.put(factory.getExtractorName(), factory);
    }

    /**
     *
     * Retrieves a {@link org.deri.any23.extractor.ExtractorFactory} given its name
     *
     * @param name of the desired factory
     * @return the {@link org.deri.any23.extractor.ExtractorFactory} associated to the provided name
     * @throws IllegalArgumentException if there is not a {@link org.deri.any23.extractor.ExtractorFactory} associated to
     * the provided name
     */
    public ExtractorFactory<?> getFactory(String name) {
        if (!factories.containsKey(name)) {
            throw new IllegalArgumentException("Unregistered extractor name: " + name);
        }
        return factories.get(name);
    }

    /**
     * @return an {@link org.deri.any23.extractor.ExtractorGroup} with all the registered
     * {@link org.deri.any23.extractor.Extractor}.
     */
    public ExtractorGroup getExtractorGroup() {
        return getExtractorGroup(getAllNames());
    }

    /**
     * Returns an {@link org.deri.any23.extractor.ExtractorGroup} containing the
     * {@link org.deri.any23.extractor.ExtractorFactory} mathing the names provided as input.
     * @param names a {@link java.util.List} containing the names of the desired {@link ExtractorFactory}
     * @return the
     */
    public ExtractorGroup getExtractorGroup(List<String> names) {
        List<ExtractorFactory<?>> members = new ArrayList<ExtractorFactory<?>>(names.size());
        for (String name : names) {
            members.add(getFactory(name));
        }
        return new ExtractorGroup(members);
    }

    /**
     * 
     * @param name of the {@link org.deri.any23.extractor.ExtractorFactory}
     * @return true iff is there a {@link org.deri.any23.extractor.ExtractorFactory} associated to the provided name
     */
    public boolean isRegisteredName(String name) {
        return factories.containsKey(name);
    }

    /**
     * Returns the names of all registered extractors, sorted alphabetically.
     */
    public List<String> getAllNames() {
        // TODO (low) this method should be private since it's used only within this class
        List<String> result = new ArrayList<String>(factories.keySet());
        Collections.sort(result);
        return result;
    }

}