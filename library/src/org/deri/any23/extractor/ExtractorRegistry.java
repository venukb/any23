package org.deri.any23.extractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.any23.extractor.html.AdrExtractor;
import org.deri.any23.extractor.html.HeadLinkExtractor;
import org.deri.any23.extractor.html.ICBMExtractor;
import org.deri.any23.extractor.html.LicenseExtractor;
import org.deri.any23.extractor.html.TitleExtractor;
import org.deri.any23.extractor.html.XFNExtractor;
import org.deri.any23.extractor.rdf.NTriplesExtractor;
import org.deri.any23.extractor.rdf.RDFXMLExtractor;
import org.deri.any23.extractor.rdf.TurtleExtractor;
import org.deri.any23.extractor.rdfa.RDFaExtractor;

public class ExtractorRegistry {
	private static ExtractorRegistry instance = null;
	
	public static ExtractorRegistry get() {
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
		}
		return instance;
	}
	
	private Map<String, ExtractorFactory<?>> factories = new HashMap<String, ExtractorFactory<?>>();
	
	public void register(ExtractorFactory<?> factory) {
		if (factories.containsKey(factory.getExtractorName())) {
			throw new IllegalArgumentException("Extractor name clash: " + factory.getExtractorName());
		}
		factories.put(factory.getExtractorName(), factory);
	}
	
	public ExtractorFactory<?> getFactory(String name) {
		if (!factories.containsKey(name)) {
			throw new IllegalArgumentException("Unregistered extractor name: " + name);
		}
		return factories.get(name);
	}
	
	public ExtractorGroup getExtractorGroup() {
		return getExtractorGroup(getAllNames());
	}
	
	public ExtractorGroup getExtractorGroup(List<String> names) {
		List<ExtractorFactory<?>> members = new ArrayList<ExtractorFactory<?>>(names.size());
		for (String name : names) {
			members.add(getFactory(name));
		}
		return new ExtractorGroup(members);
	}
	
	public boolean isRegisteredName(String name) {
		return factories.containsKey(name);
	}
	
	public List<String> findUnregisteredNames(List<String> names) {
		List<String> result = new ArrayList<String>();
		for (String name : names) {
			if (!isRegisteredName(name)) {
				result.add(name);
			}
		}
		return result;
	}
	
	/**
	 * Returns the names of all registered extractors, sorted alphabetically.
	 */
	public List<String> getAllNames() {
		List<String> result = new ArrayList<String>(factories.keySet());
		Collections.sort(result);
		return result;
	}
}
