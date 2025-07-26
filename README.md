# RyanAir Flights Finder 

## Assumptions for this API
* One-stop flight search results are limited to the same day, this is, the second (connection) flights that depart in later days than the date of the arrival of the first are not considered
### Business domain assumptions
* The available RyanAir routes are understood to change only eventually, so it made sense to preload all of RyanAir's routes in the cache and update it only when a change is detected. 

## Considerations of future improvements for the app
* Configuration flexibility can be improved
* No user rate-limiting is included
* The endpoint is not secured 
* Resilience and error handling can be greatly improved/extended. Same applies to the API feedback.
* Monitoring / health-check endpoint is not included
* Testing can (still) be extended for unhappy cases
