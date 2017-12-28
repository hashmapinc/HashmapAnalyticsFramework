# HashmapAnalyticsFramework
Repository fo the analytics framework work.

## Introduction
Hashmap Analytics framework is collection of multiple independent microservices which utilizes API Gateway pattern for communication.

### Service
1. **api-discovery**: Eureka discovery server which acts as a reistry for all microservices
2. **api-gateway** : Zuul api gateway which acts as a proxy for all client-service and service-service communication
3. **auth-service**: Authentication and Authorization service which provides adapters for different authentication mechanisms and for authorization it depends on JWt token.
4. **functions-api**: Responsible for scanning newly added functions and registering them in database which in turn can be requested by client application wo create a workflow and by workflow executor while executing workflow
5. **workflow**: It's a co-ordinator service which can be used to Create/Update/Delete workflow from client
6. **workflow-executor**: Workflow Executor service which uses dexecutor to create a task dependency graph to execute it on ignite.
7. **scheduler**: Holds a schedule for every workflow and schedules execution of workflow depending upon schedule.

Details about each service and libraries is added in Individual Readme of each service.
