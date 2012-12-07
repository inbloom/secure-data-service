@RALLY_US4006
Feature:  SARJE - consume a queue written by oplogagent

Scenario:  Can bind and read from search queue
Given I bind to queue "search"
And I send a terminate message to the queue
And I read the queue
And I expect "94" messages to be dequeued
And I expect "Matt" to be in one of the messages written on the queue
And I expect "2a49acfb-0da9-4983-8a20-4462584f59c7" to be in one of the messages written on the queue
And I expect "sli.student" to be in one of the messages written on the queue

