N26 Statistics API Task

## Task Description
We would like to have a restful API for our statistics. The main use case for our API is to
calculate realtime statistic from the last 60 seconds. There will be two APIs, one of them is
called every time a transaction is made. It is also the sole input of this rest API. The other one
returns the statistic based of the transactions of the last 60 seconds.

The project is based on a small web service which uses the following technologies:

## Technology Stack
* Java 1.8
* Spring MVC with Spring Boot
* Swagger
* Maven
* Lombok
* Eclipse.


## Conventions Used

 * All the Data Transfer Objects are wrapped in a package and these will be taversed only from Front  End till Service Layer
 
 * In future if we are going to persist these Transactions we should prepare Data Access Objects(DAO) and this will take care of persisting the objects to DB.
 
 * Orika Mapper can be used to do the conversions from DTO to DAO.
 
 * Controller: All Controllers preset in this package are the End points and I haven't produced any extra end point apart from the requirement.
  
 * Service: Implements the business logic and handles the data.
 
 * Test Driven approach is followed by both swagger and Junit Test Cases.
 
## Approach  
 
 * I see that the major challenge here is to select the Data Structure.
 	- I have initially experimented this challenge with Priority Blocking Queue, but as the 	                   	requirement states the retrieval should hava a T-Complexity of O(1) , it was ruled out.
 	
 	- I have to choose ConcurrentHashMap as internally ConcurrentHashMap allows concurrent threads to 	read values without locking at all, except in a minority of cases, and for concurrent writers to 	add and update values while only acquiring locks over localized segments of the internal data structure
 	
 * A small scheduler is also added in order to remove the old data so that the statistics API always     returns the statistics of the past minute. 
 	
 	   
