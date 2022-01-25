# Bid Server

Forked from: https://github.com/ivarunbansal/RealTimeBiddingAgent.

## Changes made

### (1) Update `.gitignore` file.

Since I'm using IntelliJ I noted that git wants to add `./bsp` folder to the repo.
I've update `.gitignore` file with the setting I use.

### (2) Added scalafmt plugin to the project

It's not mandatory. But it's a good idea to have the code properly formatted.
I use [scalfmt](https://scalameta.org/scalafmt/) plugin.
It adds new tasks in `sbt`, for example:

- `sbt scalafmtCheckAll`: to check if sources are formatted
- `sbt scalafmtAll`: apply format to sources

### (3) Dependencies in `build.sbt`

A good practice is to define dependencies out of `build.sbt` to keep it clean.
I've created the file `/project/Dependencies.scala` for that.
To keep consistency I've created variables for `logback` and `scalatest` libraries versions.

### (4) Format in `application.conf`

This file should be formatted accordingly.
Also for `host` you should use `0.0.0.0` instead of `localhost`.
If I'm not wrong when using Docker `localhost` will not work.
And just in case you want to change `host` and/or `port` using environment variables you can add:

```
    host = ${?HTTP_INTERFACE}
    ...
    host = ${?HTTP_PORT}
```

### (5) Definitions of public/private variables/methods

Made some changes chaning from public to private on some variables and methods.
Also if a method/variable is public type must be annotated. 

### (6) New package "api"

I'm starting to rearrange object and classes in the project.
For the sake of this exercise I'm going to remove `realTimeBiddingAgent` from the path of the packages.
(It doesn't mean the name it's wrong)

I've moved file 'Main.scala' under a new `api` package.
Also file `CampaignRoutes` under `api/routes` package.

### (7) Move sample files (json) to a different directory

I moved both files `sampleRequest.json` and `sampleResponse.json` to a `doc/samples` directory.
These files are not part of the code of the service.

### (8) New package "common"

New package `common` for classes that other components may use, as for example `api` use classes from domain package.
'JsonFormats' can not be part of the domain. If in the future you change the implementation and "formats" are not needed anymore you should not change domain classes.
Also in `Domain` object is not a good idea to mention `StatusCodes` from `Akka Http`.

### (9) Added graceful termination in actors-akka implementation

### (10) Services vs Actors

I have renamed class 'CampaignBiddingService' because it is an Actor.
Also changed the name of the package to 'actor'.

### (11) Remove Akka Http StatusCodes from domain

'StatusCodes' from 'Akka Http' is not related to domain at all.

### (12) Little change when creating an instance of logger

### (13) Move data for unit tests to a separate object

### (14) New trait Repository

Actual implementation of 'CampaignRepository' IMHO is wrong:
    - only responsibility should be handle data in the repo
    - contains code related to Akka Http
    - contains code for validations, a better place would be in the service layer

I've created a trait for Repositories and an implementation storing data in memory.
This implementation is only for this exercise. Must be replace for another implementation i.e.:
    - database
    - file
    - etc

### (15) Create an object to contain dummy data

## Original readme.md file

1) Main Class is in Main.scala 

2) To run application do sbt run( by default it will start the server at localhost:8080)

3) we have only one route as per requirement specified  http://localhost:8080/bidCampaign

on posting a bid request to  http://localhost:8080/bidCampaign it will try find the matching campaigns which is defined in repository/CampaignRepository.scala

if a matching campaign found then client will receive the BidResponse with Status 200 OK otherwise client will receive the Status 204 No Content

4) Testcases are available in CampaignRoutesSpec

sample BidRequest and Bid responses are available in sampleRequest.json and sampleResponse.json respectively.

