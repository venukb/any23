package org.deri.any23.vocab;


import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Vocabulary definitions from ical.rdf
 *
 * @author Auto-generated by schemagen on 23 mag 2008 15:28
 */
public class ICAL {
    private static final Map<String, URI> localNamesMap = new HashMap<String, URI>(10);

    /**
     * <p>The RDF model that holds the vocabulary terms</p>
     */
    private static ValueFactory factory = ValueFactoryImpl.getInstance();

    /**
     * <p>The namespace of the vocabulary as a string</p>
     */
    public static final String NS = "http://www.w3.org/2002/12/cal/icaltzd#";

    /**
     * <p>The namespace of the vocabulary as a string</p>
     *
     * @see #NS
     */
    public static String getURI() {
        return NS;
    }

    /**
     * <p>The namespace of the vocabulary as a URI</p>
     */
    public static final URI NAMESPACE = createResource(NS);

    public static final URI DomainOf_rrule = createResource("DomainOf_rrule");

    public static final URI List_of_Float = createResource("List_of_Float");

    /**
     * <p>Provide a grouping of component properties that define an alarm.</p>
     */
    public static final URI Valarm = createResource("Valarm");

    public static final URI Value_CAL_ADDRESS = createResource("Value_CAL-ADDRESS");

    public static final URI Value_DATE = createResource("Value_DATE");

    public static final URI Value_DURATION = createResource("Value_DURATION");

    public static final URI Value_PERIOD = createResource("Value_PERIOD");

    public static final URI Value_RECUR = createResource("Value_RECUR");

    public static final URI Vcalendar = createResource("Vcalendar");

    /**
     * <p>Provide a grouping of component properties that describe an event.</p>
     */
    public static final URI Vevent = createResource("Vevent");

    /**
     * <p>Provide a grouping of component properties that describe either a request
     * for free/busy time, describe a response to a request for free/busy time or
     * describe a published set of busy time.</p>
     */
    public static final URI Vfreebusy = createResource("Vfreebusy");

    /**
     * <p>Provide a grouping of component properties that describe a journal entry.</p>
     */
    public static final URI Vjournal = createResource("Vjournal");

    /**
     * <p>Provide a grouping of component properties that defines a time zone.</p>
     */
    public static final URI Vtimezone = createResource("Vtimezone");

    /**
     * <p>Provide a grouping of calendar properties that describe a to-do.</p>
     */
    public static final URI Vtodo = createResource("Vtodo");


    /**
     * <p>The URI provides the capability to associate a document object with a
     * calendar component.default value type: URI</p>
     */
    public static final URI attach = createProperty("attach");

    /**
     * <p>The URI defines an "Attendee" within a calendar component.value type:
     * CAL-ADDRESS</p>
     */
    public static final URI attendee = createProperty("attendee");

    public static final URI calAddress = createProperty("calAddress");

    public static final URI component = createProperty("component");

    public static final URI daylight = createProperty("daylight");

    /**
     * <p>The URI specifies a positive duration of time.value type: DURATION</p>
     */
    public static final URI duration = createProperty("duration");

    /**
     * <p>This URI defines a rule or repeating pattern for an exception to a recurrence
     * set.value type: RECUR</p>
     */
    public static final URI exrule = createProperty("exrule");

    /**
     * <p>The URI defines one or more free or busy time intervals.value type: PERIOD</p>
     */
    public static final URI freebusy = createProperty("freebusy");

    /**
     * <p>value type: list of FLOATThis URI specifies information related to the
     * global position for the activity specified by a calendar component.</p>
     */
    public static final URI geo = createProperty("geo");

    /**
     * <p>value type: CAL-ADDRESSThe URI defines the organizer for a calendar component.</p>
     */
    public static final URI organizer = createProperty("organizer");

    /**
     * <p>This URI defines a rule or repeating pattern for recurring events, to-dos,
     * or time zone definitions.value type: RECUR</p>
     */
    public static final URI rrule = createProperty("rrule");

    public static final URI standard = createProperty("standard");

    /**
     * <p>This URI specifies when an alarm will trigger.default value type: DURATION</p>
     */
    public static final URI trigger = createProperty("trigger");

    /**
     * <p>The TZURL provides a means for a VTIMEZONE component to point to a network
     * location that can be used to retrieve an up-to- date version of itself.value
     * type: URI</p>
     */
    public static final URI tzurl = createProperty("tzurl");

    /**
     * <p>This URI defines a Uniform URI Locator (URL) associated with the
     * iCalendar object.value type: URI</p>
     */
    public static final URI url = createProperty("url");

    /**
     * <p>value type: TEXTThis class of URI provides a framework for defining non-standard
     * properties.</p>
     */
    public static final URI X_ = createProperty("X-");

    /**
     * <p>value type: TEXTThis URI defines the action to be invoked when an alarm
     * is triggered.</p>
     */
    public static final URI action = createProperty("action");

    /**
     * <p>To specify an alternate text representation for the URI value.</p>
     */
    public static final URI altrep = createProperty("altrep");

    public static final URI byday = createProperty("byday");

    public static final URI byhour = createProperty("byhour");

    public static final URI byminute = createProperty("byminute");

    public static final URI bymonth = createProperty("bymonth");

    public static final URI bysecond = createProperty("bysecond");

    public static final URI bysetpos = createProperty("bysetpos");

    public static final URI byweekno = createProperty("byweekno");

    public static final URI byyearday = createProperty("byyearday");

    /**
     * <p>value type: TEXTThis URI defines the calendar scale used for the calendar
     * information specified in the iCalendar object.</p>
     */
    public static final URI calscale = createProperty("calscale");

    /**
     * <p>value type: TEXTThis URI defines the categories for a calendar component.</p>
     */
    public static final URI categories = createProperty("categories");

    /**
     * <p>value type: TEXTThis URI defines the access classification for a calendar
     * component.</p>
     */
    public static final URI class_ = createProperty("class");

    /**
     * <p>To specify the common name to be associated with the calendar user specified
     * by the URI.</p>
     */
    public static final URI cn = createProperty("cn");

    /**
     * <p>value type: TEXTThis URI specifies non-processing information intended
     * to provide a comment to the calendar user.</p>
     */
    public static final URI comment = createProperty("comment");

    /**
     * <p>value type: DATE-TIMEThis URI defines the date and time that a to-do
     * was actually completed.</p>
     */
    public static final URI completed = createProperty("completed");

    /**
     * <p>value type: TEXTThe URI is used to represent contact information or alternately
     * a reference to contact information associated with the calendar component.</p>
     */
    public static final URI contact = createProperty("contact");

    public static final URI count = createProperty("count");

    /**
     * <p>This URI specifies the date and time that the calendar information was
     * created by the calendar user agent in the calendar store. Note: This is analogous
     * to the creation date and time for a file in the file system.value type: DATE-TIME</p>
     */
    public static final URI created = createProperty("created");

    /**
     * <p>To specify the type of calendar user specified by the URI.</p>
     */
    public static final URI cutype = createProperty("cutype");

    /**
     * <p>To specify the calendar users that have delegated their participation to the
     * calendar user specified by the URI.</p>
     */
    public static final URI delegatedFrom = createProperty("delegatedFrom");

    /**
     * <p>To specify the calendar users to whom the calendar user specified by the URI
     * has delegated participation.</p>
     */
    public static final URI delegatedTo = createProperty("delegatedTo");

    /**
     * <p>value type: TEXTThis URI provides a more complete description of the
     * calendar component, than that provided by the "SUMMARY" URI.</p>
     */
    public static final URI description = createProperty("description");

    /**
     * <p>To specify reference to a directory entry associated with the calendar user
     * specified by the URI.</p>
     */
    public static final URI dir = createProperty("dir");

    /**
     * <p>This URI specifies the date and time that a calendar component ends.default
     * value type: DATE-TIME</p>
     */
    public static final URI dtend = createProperty("dtend");

    /**
     * <p>value type: DATE-TIMEThe URI indicates the date/time that the instance
     * of the iCalendar object was created.</p>
     */
    public static final URI dtstamp = createProperty("dtstamp");

    /**
     * <p>default value type: DATE-TIMEThis URI specifies when the calendar component
     * begins.</p>
     */
    public static final URI dtstart = createProperty("dtstart");

    /**
     * <p>default value type: DATE-TIMEThis URI defines the date and time that
     * a to-do is expected to be completed.</p>
     */
    public static final URI due = createProperty("due");

    /**
     * <p>To specify an alternate inline encoding for the URI value.</p>
     */
    public static final URI encoding = createProperty("encoding");

    /**
     * <p>default value type: DATE-TIMEThis URI defines the list of date/time exceptions
     * for a recurring calendar component.</p>
     */
    public static final URI exdate = createProperty("exdate");

    /**
     * <p>To specify the free or busy time type.</p>
     */
    public static final URI fbtype = createProperty("fbtype");

    /**
     * <p>To specify the content type of a referenced object.</p>
     */
    public static final URI fmttype = createProperty("fmttype");

    public static final URI freq = createProperty("freq");

    public static final URI interval = createProperty("interval");

    /**
     * <p>To specify the language for text values in a URI or URI parameter.</p>
     */
    public static final URI language = createProperty("language");

    /**
     * <p>value type: DATE-TIMEThe URI specifies the date and time that the information
     * associated with the calendar component was last revised in the calendar store.
     * Note: This is analogous to the modification date and time for a file in the
     * file system.</p>
     */
    public static final URI lastModified = createProperty("lastModified");

    /**
     * <p>value type: TEXTThe URI defines the intended venue for the activity defined
     * by a calendar component.</p>
     */
    public static final URI location = createProperty("location");

    /**
     * <p>To specify the group or list membership of the calendar user specified by
     * the URI.</p>
     */
    public static final URI member = createProperty("member");

    /**
     * <p>value type: TEXTThis URI defines the iCalendar object method associated
     * with the calendar object.</p>
     */
    public static final URI method = createProperty("method");

    /**
     * <p>To specify the participation status for the calendar user specified by the
     * URI.</p>
     */
    public static final URI partstat = createProperty("partstat");

    /**
     * <p>value type: INTEGERThis URI is used by an assignee or delegatee of a
     * to-do to convey the percent completion of a to-do to the Organizer.</p>
     */
    public static final URI percentComplete = createProperty("percentComplete");

    /**
     * <p>The URI defines the relative priority for a calendar component.value
     * type: INTEGER</p>
     */
    public static final URI priority = createProperty("priority");

    /**
     * <p>value type: TEXTThis URI specifies the identifier for the product that
     * created the iCalendar object.</p>
     */
    public static final URI prodid = createProperty("prodid");

    /**
     * <p>To specify the effective range of recurrence instances from the instance specified
     * by the recurrence identifier specified by the URI.</p>
     */
    public static final URI range = createProperty("range");

    /**
     * <p>default value type: DATE-TIMEThis URI defines the list of date/times
     * for a recurrence set.</p>
     */
    public static final URI rdate = createProperty("rdate");

    /**
     * <p>default value type: DATE-TIMEThis URI is used in conjunction with the
     * "UID" and "SEQUENCE" URI to identify a specific instance of a recurring
     * "VEVENT", "VTODO" or "VJOURNAL" calendar component. The URI value is
     * the effective value of the "DTSTART" URI of the recurrence instance.</p>
     */
    public static final URI recurrenceId = createProperty("recurrenceId");

    /**
     * <p>To specify the relationship of the alarm trigger with respect to the start
     * or end of the calendar component.</p>
     */
    public static final URI related = createProperty("related");

    /**
     * <p>The URI is used to represent a relationship or reference between one
     * calendar component and another.value type: TEXT</p>
     */
    public static final URI relatedTo = createProperty("relatedTo");

    /**
     * <p>To specify the type of hierarchical relationship associated with the calendar
     * component specified by the URI.</p>
     */
    public static final URI reltype = createProperty("reltype");

    /**
     * <p>This URI defines the number of time the alarm should be repeated, after
     * the initial trigger.value type: INTEGER</p>
     */
    public static final URI repeat = createProperty("repeat");

    /**
     * <p>value type: TEXTThis URI defines the status code returned for a scheduling
     * request.</p>
     */
    public static final URI requestStatus = createProperty("requestStatus");

    /**
     * <p>value type: TEXTThis URI defines the equipment or resources anticipated
     * for an activity specified by a calendar entity..</p>
     */
    public static final URI resources = createProperty("resources");

    /**
     * <p>To specify the participation role for the calendar user specified by the URI.</p>
     */
    public static final URI role = createProperty("role");

    /**
     * <p>To specify whether there is an expectation of a favor of a reply from the
     * calendar user specified by the URI value.</p>
     */
    public static final URI rsvp = createProperty("rsvp");

    /**
     * <p>To specify the calendar user that is acting on behalf of the calendar user
     * specified by the URI.</p>
     */
    public static final URI sentBy = createProperty("sentBy");

    /**
     * <p>value type: integerThis URI defines the revision sequence number of the
     * calendar component within a sequence of revisions.</p>
     */
    public static final URI sequence = createProperty("sequence");

    /**
     * <p>value type: TEXTThis URI defines the overall status or confirmation for
     * the calendar component.</p>
     */
    public static final URI status = createProperty("status");

    /**
     * <p>This URI defines a short summary or subject for the calendar component.value
     * type: TEXT</p>
     */
    public static final URI summary = createProperty("summary");

    /**
     * <p>This URI defines whether an event is transparent or not to busy time
     * searches.value type: TEXT</p>
     */
    public static final URI transp = createProperty("transp");

    /**
     * <p>value type: TEXTTo specify the identifier for the time zone definition for
     * a time component in the URI value.This URI specifies the text value
     * that uniquely identifies the "VTIMEZONE" calendar component.</p>
     */
    public static final URI tzid = createProperty("tzid");

    /**
     * <p>value type: TEXTThis URI specifies the customary designation for a time
     * zone description.</p>
     */
    public static final URI tzname = createProperty("tzname");

    /**
     * <p>value type: UTC-OFFSETThis URI specifies the offset which is in use prior
     * to this time zone observance.</p>
     */
    public static final URI tzoffsetfrom = createProperty("tzoffsetfrom");

    /**
     * <p>value type: UTC-OFFSETThis URI specifies the offset which is in use in
     * this time zone observance.</p>
     */
    public static final URI tzoffsetto = createProperty("tzoffsetto");

    /**
     * <p>This URI defines the persistent, globally unique identifier for the calendar
     * component.value type: TEXT</p>
     */
    public static final URI uid = createProperty("uid");

    public static final URI until = createProperty("until");

    /**
     * <p>value type: TEXTThis URI specifies the identifier corresponding to the
     * highest version number or the minimum and maximum range of the iCalendar specification
     * that is required in order to interpret the iCalendar object.</p>
     */
    public static final URI version = createProperty("version");

    public static URI getResource(String name) {
        URI res = localNamesMap.get(name);
        if (null == res)
            throw new RuntimeException("heck, you are using a non existing URI:" + name);
        return res;
    }

    private static URI createResource(String string) {
        URI res = factory.createURI("http://www.w3.org/2002/12/cal/icaltzd#" + string);
        localNamesMap.put(string, res);
        return res;
    }

    private static URI createProperty(String string) {
        URI res = factory.createURI("http://www.w3.org/2002/12/cal/icaltzd#" + string);
        localNamesMap.put(string, res);
        return res;
    }

    public static URI getProperty(String name) {
        return getResource(name);
    }
}
