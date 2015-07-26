package edu.pdx.cs410J.dconradt;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.PhoneBillDumper;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author Dan Conradt
 * Created on 7/19/2015.
 * PrettyPrinter implements PhoneBillDumper with a formatted output. The options are to print to a provided file or if "-" is
 * provided it will print the same format to the console.
 */
public class PrettyPrinter implements PhoneBillDumper {
    @Override
    public void dump(AbstractPhoneBill abstractPhoneBill) throws IOException {

    }
    /**
     *
     * @param abstractPhoneBill// takes an instance of AbstractPhoneBill
     * @throws IOException// Exception handling for file IO
     */
    public void prettyDump( AbstractPhoneBill abstractPhoneBill)throws IOException{
        String customerName = abstractPhoneBill.getCustomer();
        Collection phoneCalls = abstractPhoneBill.getPhoneCalls();
        System.out.println("Customer Phone Bill\nCustomer Name: " + customerName + "\n\n\tCaller Number\tCallee Number\t" +
                "Starting Call Time\t\tEnding Call Time\t\tDuration of call\n");
        for (Object billRecord : phoneCalls) {
            consolePrint((PhoneCall) billRecord);
        }
    }

    /**
     * Function to pretty print the phone bill to the console.
     * @param billRecord a record of a phone call to be formatted to a user friendly output.
     */
    private void consolePrint(PhoneCall billRecord) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date endCall = null;
        try {
            endCall = dateFormatter.parse(billRecord.getEndTimeString());
        } catch (ParseException e) {
            System.out.println("Error calculating phone call duration.");
            System.exit(1);
        }
        Date startCall = null;
        try {
            startCall = dateFormatter.parse(billRecord.getStartTimeString());
        } catch (ParseException e) {
            System.out.println("Error calculating phone call duration.");
            System.exit(1);
        }
        long timeDifference = endCall.getTime() - startCall.getTime();
        int duration = (int)(timeDifference / (60 * 1000));
        String phoneBillRecord = "\t" + billRecord.getCaller() + "\t" + billRecord.getCallee() + "\t"
                + billRecord.getStartTimeString() + "\t\t" + billRecord.getEndTimeString() + "\t\t" + duration + " minutes";
        System.out.println(phoneBillRecord);
    }


    /**
     * Formats a phone bill call record to tab delimited formatting and prints it to the file.
     * @param billRecord A phone call record
     * @param newRecord PrintWriter instance to print the phone call to the file.
     */
    private void buildRecord(PhoneCall billRecord, PrintWriter newRecord) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        Date endCall = null;
        try {
            endCall = dateFormatter.parse(billRecord.getEndTimeString());
        } catch (ParseException e) {
            System.out.println("Error calculating phone call duration.");
            System.exit(1);
        }
        Date startCall = null;
        try {
            startCall = dateFormatter.parse(billRecord.getStartTimeString());
        } catch (ParseException e) {
            System.out.println("Error calculating phone call duration.");
            System.exit(1);
        }
        long timeDifference = endCall.getTime() - startCall.getTime();
        int duration = (int)(timeDifference / (60 * 1000));
        String phoneBillRecord = "\t" + billRecord.getCaller() + "\t" + billRecord.getCallee() + "\t"
                + billRecord.getStartTimeString() + "\t\t" + billRecord.getEndTimeString() + "\t\t" + duration + " minutes";
        newRecord.println(phoneBillRecord);
    }
}