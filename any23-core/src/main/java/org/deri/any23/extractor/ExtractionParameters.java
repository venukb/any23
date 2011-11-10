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

package org.deri.any23.extractor;

import org.deri.any23.configuration.Configuration;
import org.deri.any23.configuration.DefaultConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * This class models the parameters to be used to perform an extraction.
 *
 * @see org.deri.any23.Any23
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ExtractionParameters {

    /**
     * @param c the underlying configuration.
     * @return the default extraction parameters.
     */
    public static final ExtractionParameters newDefault(Configuration c) {
        return new ExtractionParameters(c, ValidationMode.None);
    }

    /**
     * Creates the default extraction parameters with {@link DefaultConfiguration}.
     *
     * @return the default extraction parameters.
     */
    public static final ExtractionParameters newDefault() {
        return new ExtractionParameters(DefaultConfiguration.singleton(), ValidationMode.None);
    }

    /**
     * Declares the supported validation actions.
     */
    public enum ValidationMode {
        None,
        Validate,
        ValidateAndFix
    }

    private final Configuration configuration;

    private final ValidationMode extractionMode;

    private final Map<String, Boolean> extractionFlags;

    private final Map<String,String> extractionProperties;

    /**
     * Constructor.
     *
     * @param configuration underlying configuration.
     * @param extractionMode specifies the required extraction mode.
     * @param extractionFlags map of specific flags used for extraction. If not specified they will
     *        be retrieved by the default {@link org.deri.any23.configuration.Configuration}.
     * @param extractionProperties map of specific properties used for extraction. If not specified
     *        they will ne retrieved by the default {@link org.deri.any23.configuration.Configuration}.
     */
    public ExtractionParameters(
            Configuration configuration,
            ValidationMode extractionMode,
            Map<String, Boolean> extractionFlags,
            Map<String,String> extractionProperties
    ) {
        if(configuration == null) {
            throw new NullPointerException("Configuration cannot be null.");
        }
        if(extractionMode == null) {
            throw new NullPointerException("Extraction mode cannot be null.");
        }
        this.configuration  = configuration;
        this.extractionMode = extractionMode;
        this.extractionFlags =
                extractionFlags == null
                        ?
                new HashMap<String,Boolean>()
                        :
                new HashMap<String,Boolean>(extractionFlags);
        this.extractionProperties =
                extractionProperties == null
                        ?
                new HashMap<String,String>()
                        :
                new HashMap<String,String>(extractionProperties);
    }

    /**
     * Constructor.
     *
     * @param configuration underlying configuration.
     * @param extractionMode specifies the required extraction mode.
     */
    public ExtractionParameters(Configuration configuration, ValidationMode extractionMode) {
        this(configuration, extractionMode, null, null);
    }

    /**
     * Constructor, allows to set explicitly the value for flag
     * {@link SingleDocumentExtraction#METADATA_NESTING_FLAG}.
     *
     * @param configuration the underlying configuration.
     * @param extractionMode specifies the required extraction mode.
     * @param nesting if <code>true</code> nesting triples will be expressed.
     */
    public ExtractionParameters(Configuration configuration, ValidationMode extractionMode, final boolean nesting) {
        this(
                configuration,
                extractionMode,
                new HashMap<String, Boolean>(){{
                    put(SingleDocumentExtraction.METADATA_NESTING_FLAG, nesting);
                }},
                null
        );
    }

    /**
     * @return <code>true</code> if validation is active.
     */
    public boolean isValidate() {
        return extractionMode == ValidationMode.Validate || extractionMode == ValidationMode.ValidateAndFix;
    }

    /**
     * @return <code>true</code> if fix is active.
     */
    public boolean isFix() {
        return extractionMode == ValidationMode.ValidateAndFix;
    }

    /**
     * Returns the value of the specified extraction flag, if the flag is undefined
     * it will be retrieved by the default {@link org.deri.any23.configuration.Configuration}.
     *
     * @param flagName name of flag.
     * @return flag value.
     */
    public boolean getFlag(String flagName) {
        final Boolean value = extractionFlags.get(flagName);
        if(value == null) {
            return configuration.getFlagProperty(flagName);
        }
        return value;
    }

    /**
     * Sets the value for an extraction flag.
     *
     * @param flagName flag name.
     * @param value new flag value.
     * @return the previous flag value.
     */
    public Boolean setFlag(String flagName, boolean value) {
        checkPropertyExists(flagName);
        validateValue("flag name", flagName);
        return extractionFlags.put(flagName, value);
    }

    /**
     * Returns the value of the specified extraction property, if the property is undefined
     * it will be retrieved by the default {@link org.deri.any23.configuration.Configuration}.
     *
     * @param propertyName the property name.
     * @return the property value.
     * @throws IllegalArgumentException if the property name is not defined in configuration.
     */
    public String getProperty(String propertyName) {
        final String propertyValue = extractionProperties.get(propertyName);
        if(propertyValue == null) {
            return configuration.getPropertyOrFail(propertyName);
        }
        return propertyValue;
    }

    /**
     * Sets the value for an extraction property.
     *
     * @param propertyName the property name.
     * @param propertyValue the property value.
     * @return the previous property value.
     */
    public String setProperty(String propertyName, String propertyValue) {
        checkPropertyExists(propertyName);
        validateValue("property name" , propertyName);
        validateValue("property value", propertyValue);
        return extractionProperties.put(propertyName, propertyValue);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj instanceof ExtractionParameters) {
            ExtractionParameters other = (ExtractionParameters) obj;
            return
                    extractionMode == other.extractionMode
                            &&
                    extractionFlags.equals( other.extractionFlags)
                            &&
                    extractionProperties.equals( other.extractionProperties );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return extractionMode.hashCode() * 2 * extractionFlags.hashCode() * 3 * extractionProperties.hashCode() * 5;
    }

    private void checkPropertyExists(String propertyName) {
        if(! configuration.defineProperty(propertyName) ) {
            throw new IllegalArgumentException(
                    String.format("Property '%s' is unknown and cannot be set.", propertyName)
            );
        }
    }

    private void validateValue(String desc, String value) {
        if(value == null || value.trim().length() == 0)
            throw new IllegalArgumentException( String.format("Invalid %s: '%s'", desc, value) );
    }
}
