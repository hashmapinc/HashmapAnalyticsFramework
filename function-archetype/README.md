# function-archetype
Maven arcehtype to generate skeleton of a functions(spark job encapsulated in IgniteService)

1. Checkout this Archetype till it's not added to Maven Central
2. Execute ``mvn clean install`` this will add archetype to your local repo
3. cd to directory you want to generate project from this archetype
4. execute below command

        mvn archetype:generate -DarchetypeGroupId=com.hashmap.haf \
        -DarchetypeArtifactId=function-archetype    \
        -DarchetypeVersion=1.0.0-SNAPSHOT                       \
        -DgroupId=<Group id of your new project>              \
        -DartifactId=<Artifact id for your new project> -e

5. Accept all the default options, unless you want to override
6. Next build your project

         cd your_project
         mvn clean install
         
## Using archetype-catalog with IntelliJ Idea
1. Checkout this Archetype till it's not added to Maven Central
2. Install plugin "Maven Archetype Catalogs" in IDE
3. Restart Idea
4. Go to Settings(Preferences for Mac) -> Build,Execution,Deployment -> Build Tools -> Maven Archetype Catalogs
5. Add new URL (Local path) of archetype-catalog.xml e.g. /path/to/function-archetype/archetype-catalog.xml
6. Click apply and Ok
7. Go to File -> New -> Project, select Maven and in right hand panel click Create from archetype
8. Select function-archetype (It will be mostly within first 5-6 archetypes) and you are done, No need to execute command

Follow above steps every time you need to create function using this archetype
