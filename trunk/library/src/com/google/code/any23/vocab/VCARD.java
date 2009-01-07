/* CVS $Id: $ */
package com.google.code.any23.vocab; 
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from vcard.owl 
 * @author Auto-generated by schemagen on 28 apr 2008 14:01 
 */
public class VCARD {
	private static final Map<String, Property> propertyMap = new HashMap<String, Property>();

    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2006/vcard/ns#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>An additional part of a person's name</p> */
    public static final Property additional_name = createProperty("additional-name" );
    
    /** <p>A postal or street address of a person</p> */
    public static final Property adr = createProperty("adr" );
    
    /** <p>A person that acts as one's agent</p> */
    public static final Property agent = createProperty("agent" );
    
    /** <p>The birthday of a person</p> */
    public static final Property bday = createProperty("bday" );
    
    /** <p>A category of a vCard</p> */
    public static final Property category = createProperty("category" );
    
    /** <p>A class (e.g., public, private, etc.) of a vCard</p> */
    public static final Property class_ = createProperty("class" );
    
    /** <p>The country of a postal address</p> */
    public static final Property country_name = createProperty("country-name" );
    
    /** <p>An email address</p> */
    public static final Property email = createProperty("email" );
    
    /** <p>The extended address of a postal address</p> */
    public static final Property extended_address = createProperty("extended-address" );
    
    /** <p>A family name part of a person's name</p> */
    public static final Property family_name = createProperty("family-name" );
    
    /** <p>A fax number of a person</p> */
    public static final Property fax = createProperty("fax" );
    
    /** <p>A formatted name of a person</p> */
    public static final Property fn = createProperty("fn" );
    
    /** <p>A geographic location associated with a person</p> */
    public static final Property geo = createProperty("geo" );
    
    /** <p>A given name part of a person's name</p> */
    public static final Property given_name = createProperty("given-name" );
    
    /** <p>A home address of a person</p> */
    public static final Property homeAdr = createProperty("homeAdr" );
    
    /** <p>A home phone number of a person</p> */
    public static final Property homeTel = createProperty("homeTel" );
    
    /** <p>An honorific prefix part of a person's name</p> */
    public static final Property honorific_prefix = createProperty("honorific-prefix" );
    
    /** <p>An honorific suffix part of a person's name</p> */
    public static final Property honorific_suffix = createProperty("honorific-suffix" );
    
    /** <p>A key (e.g, PKI key) of a person</p> */
    public static final Property key = createProperty("key" );
    
    /** <p>The formatted version of a postal address (a string with embedded line breaks, 
     *  punctuation, etc.)</p>
     */
    public static final Property label = createProperty("label" );
    
    /** <p>The latitude of a geographic location</p> */
    public static final Property latitude = createProperty("latitude" );
    
    /** <p>The locality (e.g., city) of a postal address</p> */
    public static final Property locality = createProperty("locality" );
    
    /** <p>A logo associated with a person or their organization</p> */
    public static final Property logo = createProperty("logo" );
    
    /** <p>The longitude of a geographic location</p> */
    public static final Property longitude = createProperty("longitude" );
    
    /** <p>A mailer associated with a vCard</p> */
    public static final Property mailer = createProperty("mailer" );
    
    /** <p>A mobile email address of a person</p> */
    public static final Property mobileEmail = createProperty("mobileEmail" );
    
    /** <p>A mobile phone number of a person</p> */
    public static final Property mobileTel = createProperty("mobileTel" );
    
    /** <p>The components of the name of a person</p> */
    public static final Property n = createProperty("n" );
    
    /** <p>The nickname of a person</p> */
    public static final Property nickname = createProperty("nickname" );
    
    /** <p>Notes about a person on a vCard</p> */
    public static final Property note = createProperty("note" );
    
    /** <p>An organization associated with a person</p> */
    public static final Property org = createProperty("org" );
    
    /** <p>The name of an organization</p> */
    public static final Property organization_name = createProperty("organization-name" );
    
    /** <p>The name of a unit within an organization</p> */
    public static final Property organization_unit = createProperty("organization-unit" );
    
    /** <p>An email address unaffiliated with any particular organization or employer; 
     *  a personal email address</p>
     */
    public static final Property personalEmail = createProperty("personalEmail" );
    
    /** <p>A photograph of a person</p> */
    public static final Property photo = createProperty("photo" );
    
    /** <p>The post office box of a postal address</p> */
    public static final Property post_office_box = createProperty("post-office-box" );
    
    /** <p>The postal code (e.g., U.S. ZIP code) of a postal address</p> */
    public static final Property postal_code = createProperty("postal-code" );
    
    /** <p>The region (e.g., state or province) of a postal address</p> */
    public static final Property region = createProperty("region" );
    
    /** <p>The timestamp of a revision of a vCard</p> */
    public static final Property rev = createProperty("rev" );
    
    /** <p>A role a person plays within an organization</p> */
    public static final Property role = createProperty("role" );
    
    /** <p>A version of a person's name suitable for collation</p> */
    public static final Property sort_string = createProperty("sort-string" );
    
    /** <p>A sound (e.g., a greeting or pronounciation) of a person</p> */
    public static final Property sound = createProperty("sound" );
    
    /** <p>The street address of a postal address</p> */
    public static final Property street_address = createProperty("street-address" );
    
    /** <p>A telephone number of a person</p> */
    public static final Property tel = createProperty("tel" );
    
    /** <p>A person's title</p> */
    public static final Property title = createProperty("title" );
    
    /** <p>A timezone associated with a person</p> */
    public static final Property tz = createProperty("tz" );
    
    /** <p>A UID of a person's vCard</p> */
    public static final Property uid = createProperty("uid" );
    
    /** <p>An (explicitly) unlabeled address of a person</p> */
    public static final Property unlabeledAdr = createProperty("unlabeledAdr" );
    
    /** <p>An (explicitly) unlabeled email address of a person</p> */
    public static final Property unlabeledEmail = createProperty("unlabeledEmail" );
    
    /** <p>An (explicitly) unlabeled phone number of a person</p> */
    public static final Property unlabeledTel = createProperty("unlabeledTel" );
    
    /** <p>A URL associated with a person</p> */
    public static final Property url = createProperty("url" );
    
    /** <p>A work address of a person</p> */
    public static final Property workAdr = createProperty("workAdr" );
    
    /** <p>A work email address of a person</p> */
    public static final Property workEmail = createProperty("workEmail" );
    
    /** <p>A work phone number of a person</p> */
    public static final Property workTel = createProperty("workTel" );
    
    /** <p>Resources that are vCard (postal) addresses</p> */
    public static final Resource Address = m_model.createResource( "http://www.w3.org/2006/vcard/ns#Address" );
    
    /* XXX TODO FIXME
     * added by GR: this is not existing in the spec but we should support it
     */
    public static final Property addressType = createProperty("addressType");
    /*
     * XXX TODO FIXME see above
     */
    /** <p>Resources that are vCard Telephones</p> */
    public static final Resource Telephone = m_model.createResource( "http://www.w3.org/2006/vcard/ns#Address" );
    
    
    /** <p>Resources that are vCard geographic locations</p> */
    /** GR: aka Geo **/
    public static final Resource Location = m_model.createResource( "http://www.w3.org/2006/vcard/ns#Location" );
    
    /** <p>Resources that are vCard personal names</p> */
    public static final Resource Name = m_model.createResource( "http://www.w3.org/2006/vcard/ns#Name" );
    
    /** <p>Resources that are vCard organizations</p> */
    public static final Resource Organization = m_model.createResource( "http://www.w3.org/2006/vcard/ns#Organization" );
    
    /** <p>Resources that are vCards</p> */
    public static final Resource VCard = m_model.createResource( "http://www.w3.org/2006/vcard/ns#VCard" );
    
    public static Property getProperty(String name) {
    	return propertyMap.get(name);
    }

    private static Property createProperty(String localName) {
		Property result = m_model.createProperty( "http://www.w3.org/2006/vcard/ns#"+localName);
		propertyMap.put(localName, result);
		return result;
	}

}
