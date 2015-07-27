package edu.pdx.cs410J.dconradt;

import edu.pdx.cs410J.AbstractPhoneBill;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author Dan Conradt 7/27/2015
 *
 * This servlet provides a REST API for working with an
 * <code>PhoneBill</code>.   It processes requests to query the phone bill
 * and add to a phone bill.
 */
public class PhoneBillServlet extends HttpServlet
{
    static PhoneBill newBill = new PhoneBill(); // Instance of a phone bill to pass the new call to.
    /**
     * Handles an HTTP GET request from a client by writing the values of the phone bill given
     * a customer in the HTTP parameter list to the HTTP response.  If the "customer"
     * parameter is not specified, then then a message is sent to the HTTP response
     * showing the customer parameter is required.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType("text/plain");
        String customer = getParameter( "customer", request );
        String start = getParameter("startTime", request);
        String end = getParameter("endTime", request);
        if(start != null && end !=null && customer.equalsIgnoreCase(newBill.getCustomer())){
            searchCalls(newBill, response, start, end);
        }
        else if (customer.equalsIgnoreCase(newBill.getCustomer())) {
            prettyDump(newBill, response);

        } else {
            missingRequiredParameter(response, "Missing or invalid customer, date/time");
        }
    }

    /**
     * Search and display a range of phone calls given a start time and an end time.
     * @param newBill // Instance of a phoneBill to get the customer name and phone calls list.
     * @param response // Instance of the HttpServletResponse object.
     * @param start// Start time to search for calls
     * @param end // End time to stop search for calls.
     * @throws IOException
     */
    private void searchCalls(PhoneBill newBill, HttpServletResponse response, String start, String end) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        String customerName = newBill.getCustomer();
        Collection phoneCalls = newBill.getPhoneCalls();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date newStart = null;
        Date newEnd = null;
        try {
            newStart = dateFormatter.parse(start);
        } catch (ParseException e) {
            missingRequiredParameter(response, "date time");
            return;
        }
        try {
            newEnd = dateFormatter.parse(end);
        } catch (ParseException e) {
            missingRequiredParameter(response, "date time");
            return;

        }
        pw.println("Customer Phone Bill\nCustomer Name: " + customerName + "\n\n\tCaller Number\tCallee Number\t" +
                "Starting Call Time\t\tEnding Call Time\t\tDuration of call\n");
        for (Object billRecord : phoneCalls) {
            if(((PhoneCall) billRecord).getStartTime().compareTo(newStart)>= 0 && ((PhoneCall) billRecord).getStartTime().compareTo(newEnd)<= 0) {
                consolePrint((PhoneCall) billRecord, response, pw);
                pw.flush();
            }
        }
        response.setStatus( HttpServletResponse.SC_OK );
    }

    /**
     * Handles an HTTP POST request by storing the phone call specified by the
     * request given parameters.  It writes the phone call to the
     * HTTP response.
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        PhoneCall newCall = new PhoneCall();// Instance of a newCall to record
        response.setContentType( "text/plain" );
        String customerName = getParameter( "customer", request );
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date sDate = null;
        Date eDate = null;
        if (customerName == null) {
            missingRequiredParameter( response, "name" );
        }else if ( customerName == newBill.getCustomer()) {
            newCall.setCallerNumber(getParameter("callerNumber", request));
            newCall.setCalleeNumber(getParameter("calleeNumber", request));
            try {
                sDate = dateFormatter.parse(getParameter("startTime", request));
                newCall.setStartTime(sDate);
            } catch (ParseException e) {
                response.setStatus( HttpServletResponse.SC_PRECONDITION_FAILED );
                return;
            }
            try {
                eDate = dateFormatter.parse(getParameter("endTime", request));
                newCall.setEndTime(eDate);
            } catch (ParseException e) {
                response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
            this.newBill.addPhoneCall(newCall);
        }else{
            this.newBill.setCustomer(customerName);
            newCall.setCallerNumber(getParameter("callerNumber", request));
            newCall.setCalleeNumber(getParameter("calleeNumber", request));

            try {
                sDate = dateFormatter.parse(getParameter("startTime", request));
                newCall.setStartTime(sDate);
            } catch (ParseException e) {
                response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
            try {
                eDate = dateFormatter.parse(getParameter("endTime", request));
                newCall.setEndTime(eDate);
            } catch (ParseException e) {
                response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
            this.newBill.addPhoneCall(newCall);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
        throws IOException
    {
        PrintWriter pw = response.getWriter();
        pw.println( Messages.missingRequiredParameter(parameterName));
        pw.flush();
        response.setStatus( HttpServletResponse.SC_PRECONDITION_FAILED );
    }


    /**
     * Prints the output in a pretty printer format.
     * @param abstractPhoneBill// takes an instance of AbstractPhoneBill
     * @throws IOException// Exception handling for file IO
     */
    public void prettyDump( AbstractPhoneBill abstractPhoneBill, HttpServletResponse response)throws IOException{
        PrintWriter pw = response.getWriter();
        String customerName = abstractPhoneBill.getCustomer();
        Collection phoneCalls = abstractPhoneBill.getPhoneCalls();
        pw.println("Customer Phone Bill\nCustomer Name: " + customerName + "\n\n\tCaller Number\tCallee Number\t" +
                "Starting Call Time\t\tEnding Call Time\t\tDuration of call\n");
        for (Object billRecord : phoneCalls) {
            consolePrint((PhoneCall) billRecord, response, pw);
            pw.flush();
        }
        response.setStatus( HttpServletResponse.SC_OK );
    }

    /**
     * Function to format the call and pretty print the phone bill to the console.
     * @param billRecord a record of a phone call to be formatted to a user friendly output.
     */
    private void consolePrint(PhoneCall billRecord,HttpServletResponse response, PrintWriter pw) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date endCall = null;
        try {
            endCall = dateFormatter.parse(billRecord.getEndTimeString());
        } catch (ParseException e) {
            pw.println("Error calculating phone call duration.");
            return;
        }
        Date startCall = null;
        try {
            startCall = dateFormatter.parse(billRecord.getStartTimeString());
        } catch (ParseException e) {
            pw.println("Error calculating phone call duration.");
            return;
        }
        long timeDifference = endCall.getTime() - startCall.getTime();
        int duration = (int)(timeDifference / (60 * 1000));
        pw.println("\t" + billRecord.getCaller() + "\t" + billRecord.getCallee() + "\t"
                + billRecord.getStartTimeString() + "\t\t" + billRecord.getEndTimeString() + "\t\t" + duration + " minutes");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
      String value = request.getParameter(name);
      if (name == null || "".equals(value)) {
        return null;

      } else {
        return value;
      }
    }

}
