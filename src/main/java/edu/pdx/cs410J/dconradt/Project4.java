package edu.pdx.cs410J.dconradt;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;

/**
 * The main class that parses the command line and communicates with the
 * Phone Bill server using REST.
 */
public class Project4 {

    public static final String MISSING_ARGS = "Missing command line arguments";

    public static void main(String... args) {
        String hostName = null;
        String portString = null;
        String timeStamp = null; /** String to hold the concatenation of the date and time arguments */
        int argIndex = 0;/** index for the argument list */
        int argLength = 0;/** holds the length of the argument list */
        int optionCount = 0;/** Counter for the number of options input in the command line */
        boolean success = false; /** Used to verify the success of reading command line arguments. */
        boolean print = false; /** Print flag set when option to print is requested. */
        boolean pretty = false;/** Determines if pretty file was requested.*/
        boolean search = true; /** Specifies if a search is requested.*/

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
                System.out.println("\n***README***\n\nDan Conradt - Project3\n\nI have implemented formatting the dates/time using the java.util.date and added the class PrettyPrinter to the project2 and updated it\nto Project3, PrettyPrinter implements PhoneBillDumper and " +
                        "This program takes an input of upto 4 options and requires 5 arguments describing\na phone call.  The addtional class will parse and write to a user friendly formatted phone bill file or to the console if '-' is provided\n" +
                        "after -pretty option.  The TextParser will parse the file into a phone bill collection at the start of the programe and validate the text file\ndata to ensure the file elements are of the correct format.  It validates the options and arguments " +
                        "for validity and formatting.  The date and\ntime must be actual dates in the form specified as mm/dd/yyyy hh:mm am|pm using a 12 hour clock.  The Phone numbers must be of the form nnn-nnn-nnnn.\nErrors in formating or validity of the phone " +
                        "numbers or date and time will output a message and the program will exit.  If all arguments are valid\nthe program will create a new phone call record and add it to the array list of phone calls in the " +
                        "phone bill class.  If the option -README is\nprovided then the program will output the README text description of the program and then exit. If no file exists for either -textFile or -pretty\nthen a new one will be created and written too, by the " +
                        "TextDumper class.");
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
                search = true;
                ++optionCount;
            }
        }

        /** Check to make sure no more or less arguments than required are in the command line */
        if (args.length < 9 || args.length > 12) {
            System.out.println("\nThere are too few or too many arguments");
            System.exit(1);
        }
        else if( portString == null) {
            System.out.println("Missing port.");
            System.exit(1);
        }else if( hostName == null){
            System.out.println("Missing host name.");
            System.exit(1);
        }

        int port;
        try {
            port = Integer.parseInt( portString );
            
        } catch (NumberFormatException ex) {
            System.out.println("Port \"" + portString + "\" must be an integer");
            return;
        }

        PhoneBillRestClient client = new PhoneBillRestClient(hostName, port);

        HttpRequestHelper.Response response;
        try {
            if (key == null) {
                // Print all key/value pairs
                response = client.getAllKeysAndValues();

            } else if (value == null) {
                // Print all values of key
                response = client.getValues(key);

            } else {
                // Post the key/value pair
                response = client.addKeyValuePair(key, value);
            }

            checkResponseCode( HttpURLConnection.HTTP_OK, response);

        } catch ( IOException ex ) {
            error("While contacting server: " + ex);
            return;
        }

        System.out.println(response.getContent());

        System.exit(0);
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