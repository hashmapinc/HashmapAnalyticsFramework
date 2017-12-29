# Functions API

## Introduction
This service is responsible for
1. Dynamically discovering new functions
2. Persisting them in Database
3. Deploy functions as Ignite service (Which will be executed through tasks in a workflow) 
4. Provides endpoint to access metadata (service name, task class name and configurations) of discovered functions

## Details

`FileSystemPersistentDiscoveryService` is an implementation of DiscoveryService which triggers File system lookup to find functions with annotations to be registered for workflow.

1. It Just polls a directory specified against property `functions.input.location` (Value should be valid URI string e.g. file:///home) for any jars added
2. functions-core provides a functionality to identify class with `IgniteFunction` annotation and provide instance of `IgniteFunctionType` with all annotation information.
3. Discovery service simply persists this newly found information in database.
4. 2 endpoints are provided to access discovered functions metadata
    1. GET /api/functions : Returns all discovered function, mostly useful for client to display as tasks while creating workflow
    2. GET /api/functions/{clazzName} : Returns function with associated class name, useful while generating source of Task dynamically at the time of workflow execution  
