package edu.pdx.cs410J.dconradt;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dan Conradt 7/26/2015
 * Project4 class acts as a client and passes command line information to the server to do posts
 * and gets.
 */
public class Project4 {

    private static String hostName = null; /** Holds hostname*/
    private static String portString = null;/** Holds port */
    private static String customerName = null;/** Holds phone bill customer name*/
    private static String callerNumber = null; /** Holds caller number */
    private static String calleeNumber = null;/** Holds callee number */
    private static String startTime = null;/** Start time of call */
    private static String endTime = null; /** End time of call */
    private static boolean search = false; /** Specifies if a search is requested.*/
    private static boolean print = false; /** Print flag set when option to print is requested. */

    /**
     * Main method to handle parsing command line arguments, printing current phone call and requesting search
     * for phone calls from the web service.
     * @param args
     */
    public static void main(String... args) {

        processCommandLine(args);/** Process the command line arguments*/
        int port;
        try {
            port = Integer.parseInt( portString );
        } catch (NumberFormatException ex) {
            System.out.println("Port \"" + portString + "\" must be an integer");
            return;
        }
        PhoneBillRestClient client = new PhoneBillRestClient(hostName, port);
        HttpRequestHelper.Response response = null;
        if(search) {
            try {
                response = client.getSearchValues(customerName, startTime, endTime);
                System.out.println(response.getContent());
            } catch (IOException e) {
                System.out.println("Unable to add call information, jetty may not be running.");
                System.exit(1);
            }
            System.exit(0);
        }
        try {
           response = client.addPhoneCall(customerName,callerNumber,calleeNumber,startTime,endTime);

        }catch (IOException e) {
            System.out.println("Unable to process request, invalid host, port or jetty may not be running.");
            System.exit(1);
        }
        checkResponseCode( HttpURLConnection.HTTP_OK, response);
        if (print)
            System.out.println("\nPhone call from " + callerNumber + " to " + calleeNumber + " from " + startTime + " to " + endTime);

        System.exit(0);
    }

    /**
     * Parses the commmand line arguments
     * @param args  Command line arguments
     */
    public static void processCommandLine(String [] args) {
        String timeStamp = null; /** String to hold the concatenation of the date and time arguments */
        int argIndex = 0;/** index for the argument list */
        int argLength = 0;/** holds the length of the argument list */
        int optionCount = 0;/** Counter for the number of options input in the command line */
        boolean success = false; /** Used to verify the success of reading command line arguments. */
        boolean pretty = false;/** Determines if pretty file was requested.*/
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        /** If not Argurments exit program */
        if(args.length == 0) {
            System.out.println("\nThere are too few command line argurments.");
            System.exit(1);
        }

        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("-print")) {
                print = true;
                ++optionCount;
            }else if (args[i].equalsIgnoreCase("-README")) {
                System.out.println("\n***README***\n\nDan Conradt - Project4\n\nI have implemented a client server application for a phone bill.  There is a PhoneBillServlet which responds to http calls using a RESTful client, PhoneBillRestClient," +
                        ".  The program will take input from command line arguments or url requests. It maintains the functionality of adding a new phone call to a phone bill, displaying all calls " +
                        "in a phone bill, search for calls given a date/time range, print a phone call that was just added and print the README file.  The input phone numbers and call dates are verified" +
                        " to be of the correct format.  The purpose of this program is to understand working with a server application and client interfaces all within the same " +
                        "code body.");
                System.exit(1);
            } else if (args[i].equalsIgnoreCase("-host")) {
                hostName = args[i + 1];
                optionCount = optionCount + 2;
                ++i;
            } else if (args[i].equalsIgnoreCase("-port")) {
                portString = args[i+1];
                optionCount = optionCount + 2;
                ++i;
            }else if (args[i].equalsIgnoreCase("-search")) {
                if(args.length >= 12) {
                    customerName = args[i + 1];
                    timeStamp = args[i + 2] + " " + args[i + 3] + " " + args[i + 4];
                    success = verifyDateFormat(timeStamp); // verify data and time format
                    if (success)
                        startTime = timeStamp;
                    else
                        exitProgram("\nInvalid or missing Start Time argument.\nA date and time must be of the format dd/mm/yyy/ hh:mm am");
                    timeStamp = args[i + 5] + " " + args[i + 6] + " " + args[i + 7];
                    success = verifyDateFormat(timeStamp);// verify data and time format
                    if (success)
                        endTime = timeStamp;
                    else
                        exitProgram("\nInvalid or missing End Time Argument.\nA date and time must be of the format dd/mm/yyy/ hh:mm am");

                    search = true;
                    return;
                }else
                    System.out.println("Missing the start time and end time parameters.");
            }else if( args[i].startsWith("-")) {
                System.out.println("The option " + args[i] + " is not valid.");
                System.exit(1);
            }
        }

        /** Check to make sure no more or less arguments than required are in the command line */
        if (args.length < 9 || args.length > 17) {
            System.out.println("\nThere are too few or too many arguments");
            System.exit(1);
        }
        else if( portString == null) {
            System.out.println("Missing port option or port name.");
            System.exit(1);
        }else if( hostName == null){
            System.out.println("Missing host option or host name.");
            System.exit(1);
        }
        argIndex = optionCount;
        customerName = args[argIndex];
        success = verifyPhoneNumber(args[argIndex + 1]);// verify phone format
        if (success)
            callerNumber = args[argIndex + 1];
        else
            exitProgram("\nInvalid or missing phone number arguments.\nA phone number must be of the form nnn-nnn-nnnn.");
        success = verifyPhoneNumber(args[argIndex + 2]); // verify phone format
        if (success)
            calleeNumber = args[argIndex + 2];
        else
            exitProgram("\nInvalid or missing phone number argument.\nA phone number must be of the form nnn-nnn-nnnn.");
        timeStamp = args[argIndex + 3] + " " + args[argIndex + 4] + " " + args[argIndex + 5];
        success = verifyDateFormat(timeStamp); // verify data and time format
        if (success)
            startTime = timeStamp;
        else
            exitProgram("\nInvalid or missing Start Time argument.\nA date and time must be of the format dd/mm/yyy/ hh:mm am");
        timeStamp = args[argIndex + 6] + " " + args[argIndex + 7] + " " + args[argIndex + 8];
        success = verifyDateFormat(timeStamp);// verify data and time format
        if (success)
            endTime = timeStamp;
        else
            exitProgram("\nInvalid or missing End Time Argument.\nA date and time must be of the format dd/mm/yyy/ hh:mm am");
        if (args.length > argIndex + 9) {
            System.out.println("There are too many arguments listed in the phone call information.");
            System.exit(1);
        }
    }

    /**
     * Verify the date formatting is correct. Java regex pattern and matcher are used to describe the required input. Returns the value
     * of the successful or failure of a match.
     * @param date  Date to be verified
     * @return Return true if date is valid otherwise false
     */
    public static boolean verifyDateFormat(String date) {
        String dateToCheck = date;
        Pattern datePattern = Pattern.compile("(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/(\\d\\d\\d\\d) (1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)");
        Matcher dateCorrect = datePattern.matcher(dateToCheck);
        return dateCorrect.matches();
    }


    /**
     * Verify the phone number formatting is correct. Java regex pattern and matcher are used to describe the required input. Returns the value
     * of the successful or failur of a match.
     * @param phoneNumber   Phone number to be verified
     * @return  Return true if a phone number is valid otherwise false
     */
    public static boolean verifyPhoneNumber(String phoneNumber) {
        String checkPhoneNumber = phoneNumber;
        Pattern numberPattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher numberPatternMatch = numberPattern.matcher(checkPhoneNumber);
        return numberPatternMatch.matches();
    }


    /**
     *  Notifies the user of the problem and exits the program.
     * @param description   String message to print out before exiting the program
     */
    public static void exitProgram(String description){
        System.out.println(description);
        System.exit(1);
    }




    /**
     * Makes sure that the give response has the expected HTTP status code
     * @param code The expected status code
     * @param response The response from the server
     */
    private static void checkResponseCode( int code, HttpRequestHelper.Response response )
    {
        if (response.getCode() != code) {
            error(String.format("Expected HTTP code %d, got code %d.\n\n%s", code,
                                response.getCode(), response.getContent()));
        }
    }

    /**
     * Prints out error messages.
     * @param message
     */
    private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);
        System.exit(1);
    }

    /**
     * Prints usage information for this program and exits
     * @param message An error message to print
     */
    private static void usage( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java Project4 host port [key] [value]");
        err.println("  host    Host of web server");
        err.println("  port    Port of web server");
        err.println("  key     Key to query");
        err.println("  value   Value to add to server");
        err.println();
        err.println("This simple program posts key/value pairs to the server");
        err.println("If no value is specified, then all values are printed");
        err.println("If no key is specified, all key/value pairs are printed");
        err.println();
        System.exit(1);
    }


}