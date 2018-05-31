#Metadata Ingestion Service

##Metadata API Service 
This Service will be used to create MetadataConfig Objects with Source and Sink details.
Metadata Service provide REST API's.

### Run Metadata Service
1. Run service by MetadataConfigApplication class
2. Configuration file is metadata-api.yml in resource directory
3. Default port is 9090
4. Default database is HSQL. To configure Postgres keep the tempus running with postgres docker container.
5. Also replace the Host and Port of postgres url in metadata-api.yml file.
6. Database schema is in schema.sql file in resource/sql directory.

###Currently Supported API's : 
1. Creating new Metadata Config (POST): 
```text
http://localhost:9090/api/metaconfig
```
Sample Data
```text
{
	"name": "MetadataConfigName",
	"source": {
		"type": "JDBC",
		"dbUrl": "string1://test/sfaa",
		"username": "username",
		"password": "password"
	},
	"sink": {
		"type": "REST",
		"url": "string2",
		"username": "username2",
		"password": "password2"
	},
	"triggerType": "CRON",
	"triggerSchedule": "CRON for scheduled/NA for ON_DEMAND"
}
```
2.  Get Metadata Config By Id (GET) :
```text
http://localhost:9090/api/metaconfig/{ID}
```
3. Delete Metadata Config By Id (DELETE) :
```text
http://localhost:9090/api/metaconfig/{ID}
```
4. TODO are written on top of different classes, which is regarding different api's and adding some more functionality.

