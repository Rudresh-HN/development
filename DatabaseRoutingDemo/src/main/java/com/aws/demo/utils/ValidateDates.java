package com.aws.demo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.aws.demo.exception.DateNotProperException;

public class ValidateDates {
	final static String DATE_FORMAT = "dd-MM-yyyy";
	
	public static boolean isDateValid(String date) {
		try {
			DateFormat df = new SimpleDateFormat(DATE_FORMAT);
			df.setLenient(false);
			df.parse(date);
			return true;
		} catch (ParseException e) {
			throw new DateNotProperException("Date is not proper format!!");
		}
	}

}
