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
