# Bid Server

Forked from: https://github.com/ivarunbansal/RealTimeBiddingAgent.

## Changes made

### (1) Update `.gitignore` file.

Since I'm using IntelliJ I noted that git wants to add `./bsp` folder to the repo.
I've update `.gitignore` file with the setting I use.

## Original readme.md file

1) Main Class is in Main.scala 

2) To run application do sbt run( by default it will start the server at localhost:8080)

3) we have only one route as per requirement specified  http://localhost:8080/bidCampaign

on posting a bid request to  http://localhost:8080/bidCampaign it will try find the matching campaigns which is defined in repository/CampaignRepository.scala

if a matching campaign found then client will receive the BidResponse with Status 200 OK otherwise client will receive the Status 204 No Content

4) Testcases are available in CampaignRoutesSpec

sample BidRequest and Bid responses are available in sampleRequest.json and sampleResponse.json respectively.

