# Lottery System

## Scope
- The service will allow anyone to register as a lottery participant.
- Lottery participants will be able to submit as many ballots as they want for any lottery that isn't yet closed.
- Every day at midnight the lottery event will be closed and a random lottery winner will be selected.
- All users will be able to check the winning ballot for any specific date.
- The service will have to persist the data regarding the lottery.

## Tools and Technologies Used
- Spring Boot : 2.7.13
- Java : 1.8
- Maven : 3.8.8
- Mongo : 5.0
- IDE : STS
- Sonarlint
- Postman

## Database Design
In total, 4 collections are created in Mongo:

**LOTTERY**
- lotteryId
- name
- prizeMoney
- status
- winnerBallot
- startDate
- endDate

**BALLOT**
- ballotId
- lotteryId
- userId
- createdDate

**USER**
  - userId
  - userName
  - firstName
  - lastName

**SEQUENCES**
- seqName
- seqValue

SEQUENCES collection stores 3 sequence details for user, ballot and lottery respectively and value starts with 1 and is incremented when corresponding object is created.

## Testing the service
Create a database named LOTTERY_DB in Mongo.
Then either start the application from IDE as a Java application or use following command in terminal:
 ```sh
mvn spring-boot:run
```
You can find all the API queries in the attached postman collection [here](Lottery-System.postman_collection.json).

Different test scenarios including exceptions:
- #### Lottery Creation
  Lottery creation can be done via createLottery API. Assumption is this API is exposed to user with admin rights.
- #### Get Lotteries
  Get Lotteries present in db. API takes optional input parameter status(value can be OPEN/CLOSED) based on which open/closed lotteries will be returned. In case no status is given as input, all lotteries present in db is returned.
- #### Get Lottery Result
  Get lottery result of a particular lottery. If no lottery is present/lottery is not yet closed, appropriate error is returned.
- #### Register User
  A new user can register via registerUser API. If given username is already present in db, appropriate error is returned.
- #### Ballot Creation
  A new ballot can be created for a lottery. If the user is not registered or ballot is being created for a closed lottery, appropriate error is returned.
- #### Get Ballots
  Get Ballots present in db. API takes optional input parameters userId and lotteryId. Based on the combination or absence/presence of 2 parameters, appropriate ballots are returned. If both are not present in request, all ballots present in db is returned.

A scheduler job runs at midnight, selects a random ballot as winner and closes all the lotteries which are in OPEN status.
In case no ballots are associated with a lottery, -1 is updated as winner to indicate there is no winner for that particular lottery.