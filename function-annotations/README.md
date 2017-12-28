# Function annotations

It's very important module for HAF. This module contains

1. Annotations to identify functions from JAR
2. Type classes to extract metadata from annotations values
3. JPA implementation to store types


## Function Types

1. **IgniteFunction** : This is an annotation used if functions are Ignite Services and take 3 parameters
    1. **functionClazz**: Name of Task class to be generated which can be then added to a workflow
    2. **service** : Name of service, to be accessible from Ignite Service Grid (Every function, on which this annotation is added, will be deployed as a named service on Ignite service grid)
    3. **configs** : Ignite Service configurations required while deploying function(Ignite Service)
    
2. **LivyFunction** : This annotation is used if functions are spark jobs posted to Livy as Task