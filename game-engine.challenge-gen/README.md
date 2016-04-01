# Gamification Engine Challenge Generator

Gamification Engine challenge generator create challenges (a set of drools rules) for [Gamification Engine](https://github.com/smartcommunitylab/smartcampus.gamification) from [Smart Community Lab](https://github.com/smartcommunitylab)

## Description

Starting from a csv file that defines challenge types, gamification engine challenge generator get users from a game defined inside gamification engine and using challenges criterias, get matching users and create rules for them. 
Generated rules in json file can be inserted into 

## Prerequisites 

* Java 1.7 or higher
* Maven 3.2 or higher
* Gamification engine, [setup guide here](https://github.com/smartcommunitylab/smartcampus.gamification/wiki/Setup)

## How to build

1. Clone repository with git
2. Compile with maven using mvn install

## How to generate tools

Challenge generator have two tools available:

1. Challenge Generator, created using
```
mvn clean install -Pgenerator
```

2. Rule uploader, created using
```
mvn clean install -Puploader
```

Different zips (with related dependencies) are created inside target


## Challenge generator

Launch using:

```
java -jar challengeGenerator.jar
```

### Command line arguments

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

#### Example

```
java -jar challengeGenerator-jar-with-dependencies.jar -host http://localhost:8080/gamification/ -gameId 56e7bf3b570ac89331c37262 -input challengesRules.csv -templateDir rules\templates -output output.json
``` 

## Challenge uploader

Launch using:

```
java -jar challengeUploader.jar
```

### Command line arguments:

```
usage: challengeUploader
-host <host> -gameId <gameId> -input <input json file>
 -gameId   uuid for gamification engine
 -help     display this help
 -host     gamification engine host
 -input    rules to upload in json format
```

#### Example

```
java -jar challengeUploader.jar -host http://localhost:8080/gamification/ -gameId 56e7bf3b570ac89331c37262 -input C:\Lavoro\workspace\soa\smartcampus.gamification\game -engine.challenge-gen\output.json
```




 