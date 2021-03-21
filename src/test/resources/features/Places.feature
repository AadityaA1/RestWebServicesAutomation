@places
Feature: Places
  Feature to add, edit and delete places

  @places01
  Scenario: Add Place
    Given Start Testing Environment
    And I pass headers
      | Content-Type | application/json |
    And I pass query paramaters
      | key | qaclick123 |
    And I pass body from file "addPlaces"
      | lat |  34.9987 |
      | lng | -33.1234 |
    When I perform POST operation "postPlaces"
    Then I verify status code is 200
    And I save the values from response as below
      | place_id | place_id |
    And I verify response body has below value pairs
      | scope | APP |

  @places02
  Scenario: Delete Place
    ###Add Place###
    Given Start Testing Environment
    And I pass headers
      | Content-Type | application/json |
    And I pass query paramaters
      | key | qaclick123 |
    And I pass body from file "addPlaces"
      | lat |  34.9987 |
      | lng | -33.1234 |
    When I perform POST operation "postPlaces"
    Then I verify status code is 200
    And I save the values from response as below
      | place_id | place_id |
    And I verify response body has below value pairs
      | scope | APP |
    ###Delete Place####
    And I pass body from file "deletePlaces"
      | place_id | place_id |
    When I perform POST operation "deletePlace"
    Then I verify status code is 200
    ###Get Place#####
    And I pass query paramaters
      | key      | qaclick123 |
      | place_id | place_id   |
    When I perform GET operation "getPlace"
    Then I verify status code is 404
    
     @places03
  Scenario: Get Place
    ###Add Place###
    Given Start Testing Environment
    And I pass headers
      | Content-Type | application/json |
    And I pass query paramaters
      | key | qaclick123 |
    And I pass body from file "addPlaces"
      | lat |  34.9987 |
      | lng | -33.1234 |
    When I perform POST operation "postPlaces"
    Then I verify status code is 200
    And I save the values from response as below
      | place_id | place_id |
    And I verify response body has below value pairs
      | scope | APP |
   
    ###Get Place#####
    And I pass query paramaters
      | key      | qaclick123 |
      | place_id | place_id   |
    When I perform GET operation "getPlace"
    Then I verify status code is 200
    And I verify response body has below value pairs
    	| location.latitude | 34.9987 |
