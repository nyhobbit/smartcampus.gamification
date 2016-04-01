# Gamification Engine challenge Generator

Gamification Engine challenge generator create challenges (a set of drools rules) for [Gamification Engine](https://github.com/smartcommunitylab/smartcampus.gamification) from [Smart Community Lab](https://github.com/smartcommunitylab)

## Prerequisites 

* Java 1.7 or higher
* Maven 3.2 or higher
* Gamification engine, [setup guide here](https://github.com/smartcommunitylab/smartcampus.gamification/wiki/Setup)

## How to build

1. Clone repository with git
2. Compile with maven using mvn install

## How to run

Launch generated jar using:

java -jar challengeGenerator-jar-with-dependencies.jar

### Command line arguments

Command line arguments:

```
 usage: challengeGeneratorTool

 -host <host> -gameId <gameId> -input <input csv file> -template <template directory> [-output output file]
 -gameId        uuid for gamification engine
 -help          display this help
 -host          gamification engine host
 -input         challenge definition as csv file
 -output        generated file name, default challenge.json
 -templateDir   challenges templates
```

### Example

```
java -jar challengeGenerator-jar-with-dependencies.jar -host http://localhost:8080/gamification/ -gameId 56e7bf3b570ac89331c37262 -input challengesRules.csv -templateDir rules\templates -output output.json
``` 




 