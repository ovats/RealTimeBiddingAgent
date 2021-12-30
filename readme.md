1) Main Class is in Main.scala 

2) To run application do sbt run( by default it will start the server at localhost:8080)

3) we have only one route as per requirement specified  http://localhost:8080/bidCampaign

on posting a bid request to  http://localhost:8080/bidCampaign it will try find the matching campaigns which is defined in repository/CampaignRepository.scala

if a matching campaign found then client will receive the BidResponse with Status 200 OK otherwise client will receive the Status 204 No Content

4) Testcases are available in CampaignRoutesSpec

sample BidRequest and Bid responses are available in sampleRequest.json and sampleResponse.json respectively.

