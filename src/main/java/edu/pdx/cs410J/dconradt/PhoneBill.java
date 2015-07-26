package edu.pdx.cs410J.dconradt;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

/**
 * @author Dan Conradt on 6/28/2015.
 * The PhomeBill class will keep an array list of phone calls and manage the PhoneBill customer name.
 * Private variable customer to hold the phone bill customer name and an arrayList of phone calls associated
 * with the phone bill.
 */
public class PhoneBill extends AbstractPhoneBill {

    private String customer;// Phone Bill Customer
    private ArrayList phoneCalls = new ArrayList();// Array list to hold phone calls

    /** Add a phone call to the array list of phonce calls
     *
     * @param abstractPhoneCall Parent class
     */
    @Override
    public void addPhoneCall(AbstractPhoneCall abstractPhoneCall) {

        phoneCalls.add(abstractPhoneCall);

    }

    /** Get the phone call list
     *
     * @return  Return phone calls collection
     */
    @Override
    public Collection getPhoneCalls() {
        Collections.sort(phoneCalls);
        return phoneCalls;
    }


    /** Get the customer name
     *
     * @return  Return the customer name
     */
    @Override
    public String getCustomer(){

        return customer;
    }

    /** Set the customer name
     *
     * @param customer  The customer name
     */
    public void setCustomer(String customer) {

        this.customer = customer;
    }
}
