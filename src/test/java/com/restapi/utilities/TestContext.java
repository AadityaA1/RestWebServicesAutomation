package com.restapi.utilities;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TestContext {

	public RequestSpecification requestSpecs;
	public Response response;

	private ScenarioContext scenarioContext;

	public TestContext() {
		this.scenarioContext = new ScenarioContext();
	}

	public ScenarioContext getScenarioContext() {
		return scenarioContext;
	}

}
