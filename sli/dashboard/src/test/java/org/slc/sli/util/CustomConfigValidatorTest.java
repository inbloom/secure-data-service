package org.slc.sli.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slc.sli.entity.CustomConfigString;
import org.slc.sli.unit.entity.CustomConfigTest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

public class CustomConfigValidatorTest {

	@Test
	public void nullTest() {
		String testString = null;
		CustomConfigString customConfigString = new CustomConfigString(testString);
    	DataBinder binder = new DataBinder(customConfigString);
    	binder.setValidator(new CustomConfigValidator());

    	// validate the target object
    	binder.validate();

    	// get BindingResult that includes any validation errors
    	BindingResult results = binder.getBindingResult();
    	
    	assertEquals(results.hasErrors(), true);
	}
	
	@Test
	public void emptyTest() {
		String testString = "";
		CustomConfigString customConfigString = new CustomConfigString(testString);
    	DataBinder binder = new DataBinder(customConfigString);
    	binder.setValidator(new CustomConfigValidator());

    	// validate the target object
    	binder.validate();

    	// get BindingResult that includes any validation errors
    	BindingResult results = binder.getBindingResult();
    	assertEquals(results.hasErrors(), true);
	}
	
	@Test
	public void emptyJsonTest() {
		String testString = "{}";
		CustomConfigString customConfigString = new CustomConfigString(testString);
    	DataBinder binder = new DataBinder(customConfigString);
    	binder.setValidator(new CustomConfigValidator());

    	// validate the target object
    	binder.validate();

    	// get BindingResult that includes any validation errors
    	BindingResult results = binder.getBindingResult();
    	assertEquals(results.hasErrors(), false);
	}
	
	@Test
	public void brokenJsonTest() {
		String testString = CustomConfigTest.DEFAULT_CUSTOM_CONFIG_JSON;
		testString = testString.replaceAll(",", "@");
		CustomConfigString customConfigString = new CustomConfigString(testString);
    	DataBinder binder = new DataBinder(customConfigString);
    	binder.setValidator(new CustomConfigValidator());

    	// validate the target object
    	binder.validate();

    	// get BindingResult that includes any validation errors
    	BindingResult results = binder.getBindingResult();
    	assertEquals(results.hasErrors(), true);
	}
	
	@Test
	public void validJsonTest() {
		String testString = CustomConfigTest.DEFAULT_CUSTOM_CONFIG_JSON;
		CustomConfigString customConfigString = new CustomConfigString(testString);
    	DataBinder binder = new DataBinder(customConfigString);
    	binder.setValidator(new CustomConfigValidator());

    	// validate the target object
    	binder.validate();

    	// get BindingResult that includes any validation errors
    	BindingResult results = binder.getBindingResult();
    	assertEquals(results.hasErrors(), false);
	}
}
