package edu.pdx.cs410J.dconradt;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;


/**
 * @author Dan Conradt 7/27/2015
 *
 * A helper class for accessing the rest client.  This class provides
 *  gets and posts to a URL.  The class handles adding a phone call
 *  and searching for phone calls between to provided times.
 */
public class PhoneBillRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "phonebill";
    private static final String SERVLET = "calls";

    private final String url;


    /**
     * Creates a client to the Phone Bil REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public PhoneBillRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    /**
     * Returns all phone call keys and values from the server
     */
    public Response getPhoneBill() throws IOException
    {
        return get(this.url);
    }

    /**
     * Returns all values for the given customer
     */
    public Response getValues( String customer ) throws IOException
    {
        return get(this.url, "customer", customer);
    }

    /**
     * adds the phone call data to the list of calls
     *
     * @param customerName
     * @param callerNumber
     * @param calleeNumber
     * @param startTime
     * @param endTime
     * @return
     * @throws IOException
     */
    public Response addPhoneCall(String customerName, String callerNumber, String calleeNumber, String startTime, String endTime) throws IOException{

        return post(this.url, "customer", customerName, "callerNumber", callerNumber, "calleeNumber", calleeNumber, "startTime", startTime, "endTime", endTime);
    }

    /**
     * Used to search for calls between a start and stop time.
     * @param customer
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public Response getSearchValues(String customer, String start, String end )throws IOException{

        return get(this.url, "customer", customer, "startTime", start, "endTime", end);
    }
}
