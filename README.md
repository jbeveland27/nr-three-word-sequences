# New Relic Coding Challenge - Three Word Sequences

Table of Contents

  - [Objective](#Objective)
  - [Installation and Running Application](#Installation-and-Running-Application)
    - [Prerequisites](#Prerequisites)
    - [Clone and Build Project](#Clone-and-Build-Project)
    - [Running the application with `gradlew run` ( preferred method )](#Running-the-application-with-gradlew-run--preferred-method)
    - [Running the Application with `java` command](#Running-the-Application-with-java-command)
  - [Docker](#Docker)
  - [Running Tests](#Running-Tests)
  - [What would you do next, given more time (if anything)](#What-would-you-do-next-given-more-time-if-anything)
  - [Known Bugs/Issues](#Known-BugsIssues)

## Objective

Create a program executable from the command line that when given text(s) will return a list of the 100 most common three word sequences.

## Installation and Running Application

This project utilizes [Gradle](https://gradle.org/) for convenience in building and running the application. Additionally, the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) (`gradlew`) will handle downloading the Gradle runtime, so you won't need to install Gradle ahead of time.

### Prerequisites

Java JDK or JRE version 8 or higher must be installed. To check, run `java -version`:

```bash
$ java -version
java version "1.8.0_121"
```

### Clone and Build Project

```bash
# Clone Repository
git clone https://github.com/jbeveland27/nr-three-word-sequences.git

# Build Application - Invoke gradlew to execute the build
cd nr-three-word-sequences
./gradlew build
```

### Running the application with `gradlew run` ( preferred method )

```bash
# Runs the main method from App.java with the supplied file
./gradlew run --args='src/test/resources/pg_moby_dick.txt'

Or

# Runs the main method from App.java with input from stdin
cat src/test/resources/pg_moby_dick.txt | ./gradlew run
```

### Running the Application with `java` command

```bash
# Execute Main Class with file args
cd nr-three-word-sequences
java -cp build/libs/nr-three-word-sequences.jar com.eveland.app.App "src/test/resources/counts.txt" "src/test/resources/pg_moby_dick.txt"

Or

# Execute App with input from stdin
cat "src/test/resources/counts.txt" | java -cp build/libs/nr-three-word-sequences.jar com.eveland.app.App
```

Output:

```bash
$ cat "src/test/resources/counts.txt" | java -cp build/libs/nr-three-word-sequences.jar com.eveland.app.App
Processing stdin...
Phrase                                   | Count  
==================================================
five four three                          | 4
one two three                            | 4
four three two                           | 3
two three four                           | 3
three two one                            | 2
three four five                          | 2
two one two                              | 1
eleven ten nine                          | 1
one five four                            | 1
fifteen fifteen fourteen                 | 1
four three five                          | 1
thirteen twelve eleven                   | 1
four five five                           | 1
four five six                            | 1
three two five                           | 1
three four one                           | 1
two one five                             | 1
nine ten eleven                          | 1
twelve thirteen fourteen                 | 1
four one two                             | 1
seven eight nine                         | 1
five five four                           | 1
two five four                            | 1
two three one                            | 1
eight seven six                          | 1
eight nine ten                           | 1
one one two                              | 1
ten eleven twelve                        | 1
fourteen thirteen twelve                 | 1
nine eight seven                         | 1
twelve eleven ten                        | 1
two one one                              | 1
five six seven                           | 1
ten nine eight                           | 1
thirteen fourteen fifteen                | 1
six five four                            | 1
fifteen fourteen thirteen                | 1
one two one                              | 1
six seven eight                          | 1
eleven twelve thirteen                   | 1
one one one                              | 1
seven six five                           | 1
three one two                            | 1
three five four                          | 1
fourteen fifteen fifteen                 | 1
five four five                           | 1
==================================================
Finished Processing => StdIn
```

## Docker

The extra credit Docker requirement was achieved by providing a [Dockerfile](./Dockerfile) that can be used for building a Docker image and running the app within a Docker container.

By default, a test file within `src/test/resources` is used when running the app. Override is provided via command line argument.

```bash
# Build the Docker Image
docker build -t nr-three-word-sequences .

# Run the Docker Image - by default it uses one of the test files
# in the repository
docker run -it --rm --name nr-three-word-sequences-running nr-three-word-sequences

# Optional - the file used can be overriden by supplying an --args='$file1 $file2 ...' parameter. File must be located in the repository prior to building the image.
docker run -it --rm --name nr-three-word-sequences-running nr-three-word-sequences --args='src/test/resources/war-and-peace.txt src/test/resources/counts.txt'
```

## Running Tests

There are various jUnit tests in [AppTest.java](src/test/java/com/eveland/app/AppTest.java). These primarily cover the use of stdin/file arguments for IO, as well as regex tests.

Two implementations are included for processing the input itself. These can be switched by modifying this global variable in [App.java](src/main/java/com/eveland/app/App.java):

```java
public static boolean PROCESS_WHOLE_FILE = false;
```

The first approach reads the whole file into memory and parses the contents as one contiguous string. The second approach reads the file line-by-line, continually takes three word phrases, then drops the first word until all content is processed.

To run the unit tests, execute the following:

```bash
cd nr-three-word-sequences
./gradlew test
```

You can view the test report by opening the HTML output file, located at `build/reports/tests/test/index.html`.

## What would you do next, given more time (if anything)

- Currently, file inputs are processed one at a time. A substantial performance improvement could be achieved by running each file on it's own thread, so that there's no waiting to begin processing the next file. I've utilized this approach in the past for similar work and had great results with it.

- Tests that give a better indicator of performance trade-offs between the two implementations used for input processing.

- Unicode support is lacking due to the nature of the regex that is used to select words. My first pass at utilizing regex for the solution involved trying to strip punctuation out of the current line being processed, but that proved to be error prone and I wasn't confident I was handling all punctuation accurately. The regex handling I landed on tries to detect words, defining words as:

  - contiguous letters
  - letters with a single quote + more letters
  - letters + single hyphen(-) + more letters

  This is a bit clunkier than I'd prefer, and highlights how unicode support is lacking, but seems like the best approach for now given the somewhat ambiguious nature of the requirements around what character set _punctuation_ is defined as.

## Known Bugs/Issues

No real known issues except for the lack of Unicode support.
