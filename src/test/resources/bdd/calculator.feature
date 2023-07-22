Feature: Calculations

  Scenario: 001 - All pipelines OK
    Given User wants to see the carbon footprint of all Jenkins pipelines
    When User requests the data
    Then Request is successful
    And User sees the carbon footprint of all pipelines

  Scenario: 002 - Per pipeline OK
    Given User wants to see the carbon footprint of a specific Jenkins pipeline
    When User requests the data
    Then Request is successful
    And User sees the carbon footprint of a specific Jenkins pipeline

  Scenario: 003 - Per pipeline KO
    Given User wants to see the carbon footprint of a non-existant Jenkins pipeline
    When User requests the data
    Then Request is successful
    And User sees the no carbon footprint

  Scenario: 004 - Per pipeline per build OK
    Given User wants to see the carbon footprint of a specific Jenkins pipeline
    And A specific build
    When User requests the data
    Then Request is successful
    And User sees the carbon footprint of a specific build

  Scenario: 005 - Per pipeline per branch OK
    Given User wants to see the carbon footprint of a specific Jenkins pipeline
    And A specific branch
    When User requests the data
    Then Request is successful
    And User sees the carbon footprint of a specific branch

  Scenario: 006 - Per pipeline per branch per build OK
    Given User wants to see the carbon footprint of a specific Jenkins pipeline
    And A specific branch
    And A specific build
    When User requests the data
    Then Request is successful
    And User sees the carbon footprint of a specific build