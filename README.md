# Liquishit
Small cli tool to generate Liquibase changelogs 

## Installation
1. Download the jar in the release section

![Image showing where realease jars are](./release.png)

2. Put the jar in the root of your project

3. Create a dir named *migrations* in 'resources/db/changelog/migrations'

4. Use includeAll in the masterlog and point it towards /migrations

## Usage
It's a standard java jar.
So execute it like one.

``
java -jar liquishit.jar author changelog_name
``

*An execution with no arguments outputs the help command*
