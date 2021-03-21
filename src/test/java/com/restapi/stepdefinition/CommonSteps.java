package com.restapi.stepdefinition;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.JSONArray;
import org.junit.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.restapi.utilities.TestContext;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ResponseBody;

public class CommonSteps extends BaseTest {

	public static Properties properties = new Properties();

	URL resourceFile = getClass().getClassLoader().getResource("resources.properties");
	ResponseBody body;

	TestContext testContext;

	public CommonSteps(TestContext testContext) {
		// TODO Auto-generated constructor stub
		this.testContext = testContext;

	}

	@Before
	public void beforeTest(Scenario s) throws IOException {
		FileInputStream fis = new FileInputStream(resourceFile.getFile());
		properties.load(fis);
	}

	@Given("Start Testing Environment")
	public void getBaseURI() throws FileNotFoundException {

		PrintStream printStream = new PrintStream(new FileOutputStream("application.log"));
		RestAssured.baseURI = "https://rahulshettyacademy.com";
		RestAssured.useRelaxedHTTPSValidation();
		testContext.requestSpecs = RestAssured.given().baseUri("https://rahulshettyacademy.com")
				.filter(RequestLoggingFilter.logRequestTo(printStream))
				.filter(ResponseLoggingFilter.logResponseTo(printStream));
	}

	@And("I pass headers")
	public void setHeaders(Map<String, String> headers) throws FileNotFoundException {
		headers.forEach((k, v) -> {
			testContext.requestSpecs = testContext.requestSpecs.header(k, v);
		});
	}

	@And("I pass query paramaters")
	public void setQueryParam(Map<String, String> queryParam) {
		Map<String, String> actualFieldMap = getActualValues(queryParam);
		actualFieldMap.forEach((k, v) -> {
			testContext.requestSpecs = testContext.requestSpecs.queryParam(k, v);
		});
	}

	@And("I pass path parameters")
	public void setPathParam(Map<String, String> pathParam) {
		pathParam.forEach((k, v) -> {
			testContext.requestSpecs = testContext.requestSpecs.pathParam(k, v);
		});
	}

	@And("I pass body from file {string}")
	public void setBody(String jsonFileName, Map<String, String> bodyFields) throws IOException {
		Map<String, String> actualFieldMap = getActualValues(bodyFields);
		String file = readPayloadFromJsonFile("src/test/resources/payload/" + jsonFileName + ".json");

		Iterator<Entry<String, String>> iterator = actualFieldMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> pair = iterator.next();
			if (file.contains("%" + pair.getKey())) {
				file = file.replaceAll("%" + pair.getKey(), pair.getValue());
			}
		}
		testContext.requestSpecs.body(file);

	}

	private Map<String, String> getActualValues(Map<String, String> bodyFields) {
		Map<String, String> actualValues = new HashMap<>();
		for (String field : bodyFields.keySet()) {
			actualValues.put(field, getActualValues(bodyFields.get(field)));
		}
		return actualValues;
	}

	private String getActualValues(String field) {
		String actual = field;
		if (testContext.getScenarioContext().getContext(actual) != null)
			return testContext.getScenarioContext().getContext(actual).toString().replaceAll("\"", "");
		return actual;
	}

	private String readPayloadFromJsonFile(String path) throws IOException {
		return new String(Files.readAllBytes(Paths.get(path)));
	}

	@When("I perform POST operation {string}")
	public void invokePOSTOperation(String resourceName) {
		String resourceURL = properties.getProperty(resourceName);
		testContext.response = testContext.requestSpecs.when().post(resourceURL);
	}

	@Then("I verify response body contains")
	public void verifyResponseBodyValues(List<String> tableFields) {
		Iterator<String> iterator = tableFields.listIterator();
		body = testContext.response.getBody();
		String bodyAsString = body.asString();
		while (iterator.hasNext()) {
			String value = iterator.next();
			Assert.assertTrue(bodyAsString.contains(value));
		}

	}

	@And("I verify status code is {int}")
	public void verifyStatusCode(int statusCode) {
		Assert.assertTrue(testContext.response.getStatusCode() == statusCode);
	}

	@And("I verify response body has below value pairs")
	public void verifyResponseBodyPair(Map<String, String> responseBody) {
		String mapValue = "";
		String bodyStringValue = testContext.response.getBody().asString();
		if (testContext.response.getStatusCode() == 200 || testContext.response.getStatusCode() == 201) {
			Iterator<Entry<String, String>> iterator = responseBody.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> pair = iterator.next();
				mapValue = pair.getValue();
				Object value = JsonPath.parse(bodyStringValue).read("$." + pair.getKey());
				if (value instanceof List<?>) {
					List<?> authorsList = (List<?>) value;
					JSONArray jsonArray = new JSONArray(authorsList);
				}

				Assert.assertTrue(value.toString().contains(mapValue));
			}
		}

	}

	@When("I perform DELETE operation {string}")
	public void invokeDELETEOperation(String resourceName) {
		String resourceURL = properties.getProperty(resourceName);
		testContext.response = testContext.requestSpecs.when().delete(resourceURL);
	}

	@When("I perform PUT operation {string}")
	public void invokePUTOperation(String resourceName) {
		String resourceURL = properties.getProperty(resourceName);
		testContext.response = testContext.requestSpecs.when().put(resourceURL);
	}

	@When("I perform GET operation {string}")
	public void invokeGETOperation(String resourceName) {
		String resourceURL = properties.getProperty(resourceName);
		testContext.response = testContext.requestSpecs.when().get(resourceURL);
	}

	@And("I save the values from response as below")
	public void saveBodyValues(Map<String, String> fields) throws JsonMappingException, JsonProcessingException {
		String bodyStringValue = testContext.response.getBody().asString();
		ObjectMapper mapper = new ObjectMapper();
		String value = "";
		String varName = "";
		JsonNode rootNode = mapper.readTree(bodyStringValue);
		for (String key : fields.keySet()) {
			varName = fields.get(key);
			value = rootNode.findValue(key).toString();
			testContext.getScenarioContext().setContext(varName, value);
		}

	}

}
