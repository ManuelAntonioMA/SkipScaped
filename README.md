# Spring2026Team8

Members:  
Cao, Mark,	xmc2@sfu.ca  
Lu, Phung,	hnl8@sfu.ca  
Meng, Jiyang,	jma289@sfu.ca  
Mopewou, Manuel,	mam65@sfu.ca  
Nguyen, Nguyen,	nnn4@sfu.ca  

# Grid Chase Game
## Overview
This project is a grid-based chase game developed for CMPT276. The player navigates through a board, collects rewards, avoids traps, and escapes enemies that follow the player’s path.

The game includes multiple levels, different types of items, and a real-time game engine that updates based on ticks.

---

## Requirements

Before running the project, make sure you have:
- Java JDK 17 or higher
- Maven (Apache Maven)
- (Optional) IntelliJ IDEA or any Java IDE

---

## Build, Run, and Test the Project
To build the project and generate the game artifacts, run:

```bash
mvn clean package
```
This will:
- Compile the project
- Run all JUnit tests
- Generate the game JAR file
- Generate the Javadocs

Generated artifacts:
- Game JAR file: target/ChaseGame-1.0-SNAPSHOT.jar
- Javadocs: target/reports/apidocs/index.html

To run the game, running directly main class GameMain in your IDE or using:
```bash 
mvn exec:java
```
To run the packaged JAR file directly, use:
```bash 
java -jar target/ChaseGame-1.0-SNAPSHOT.jar
```
To execute all unit and integration tests:
```bash
mvn test
```
This will:
- Run all JUnit tests
- Output test results in the terminal
- Generate coverage reports using JaCoCo
---

## Javadocs
The Javadocs are generated automatically from the Javadoc comments in the source code during the Maven build.

open the generated documentation at:
```bash
target/reports/apidocs/index.html
```
---

## Test Coverage
After running tests, coverage reports can be found in:
```bash
target/site/jacoco/index.html
```
The report includes line and branch coverage for all packages. The results show all classes cover from 90% to higher than that inspite of absolute coverage.

Note: Some branches (e.g., null checks) are intentionally not covered because they represent invalid states that cannot occur during normal gameplay (e.g., starting the game without selecting a level).

---
## Gameplay
1. Launch the game
2. Select a level
2. Observe the board during preview mode
4. Move the player to collect rewards
5. Avoid traps and moving enemies
6. Reach the exit to win