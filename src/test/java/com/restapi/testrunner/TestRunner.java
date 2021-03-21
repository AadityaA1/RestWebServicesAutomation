package com.restapi.testrunner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)

@CucumberOptions(  monochrome = true,
                         tags = "@places03",
                     features = "src/test/resources/features/",
                       plugin = { "pretty","html:target/cucumber-reports"},
                       dryRun = false,
                         glue = "com.restapi.stepdefinition" )

public class TestRunner {
  //Run this
}