[![Build Status](https://travis-ci.com/sing-group/bdp4j.svg?branch=develop)](https://travis-ci.com/sing-group/bdp4j)
[![lifecycle](https://img.shields.io/badge/lifecycle-maturing-blue.svg)](https://www.tidyverse.org/lifecycle/#maturing)

# Big Data Pipelining For Java (BDP4J)

This project is a simple pipeline implementation derived from the pipeline of Mallet (http://mallet.cs.umass.edu, some source has brought from there as you can find in comments) with some interesting features (some of them to appear). 
Specifically, `Pipe`, `SerialPipes` and `Instance` classes from the Mallet framework were transformed into `AbstractPipe`, `SerialPipes` and `Instance` BDP4J classes respectively. Using this architecture as a starting point, we implemented some interesting features, achieving a product that is different from the Mallet pipeline implementation.

BDP4J tasks are represented as simple pipes. Moreover, the orchestration of pipes is defined through classes `SerialPipes` and `ParallelPipes`. These details together with the basic design of BDP4J are shown in Figure 1. To facilitate readability, only relevant methods/ attributes have been included in the classes shown in the diagram. Please refer to the javadoc documentation to obtain a complete list.

![Main clases comprising BDP4J](https://moncho.mdez-reboredo.info/imgbdp4j/Figure1.png)

Figure 1. Main clases comprising BDP4J

BDP4J `Pipe` interface was created to allow developers to implement their tasks by extending, if necessary, from other classes. However, the use of `AbstractPipe` abstract class provides the implementation of all methods of `Pipe` interface, except for pipe (which stands for the specific work that should be done), to simplify the development of tasks. `SerialPipes` class keeps the same functionality that was originally provided in Mallet software while slightly changing the instance processing flow. Finally, BDP4J adds the support for parallel tasks execution through the class `ParallelPipes`. `Configurator` class is used for loading pipelines from files and is explained below.

**Input-Output Type check**  

Each pipe or task implemented in BDP4J must implement the methods `getInputType` and `getOutputType` to indicate the type of data included in the Instance before and after executing it. This information is used by `SerialPipes` and `ParallelPipes` to check data types in the orchestration. Additionally, these types should dynamically be checked with the `Instance` after executing the task.

**`alwaysBefore` and `notAfter`** constraints. 

When creating a task by extending the `AbstractPipe` class, the developer must call on its constructor to specify “Always Before” (`alwaysBeforeDeps` attribute) and “Not After” (`notAfterDeps` attribute) task execution constraints. “Always Before” constraints indicate which tasks must be executed before the current one. `NotAfter` dependencies represent pipes that cannot be executed after one.

**Constraints based on the kind of pipes**. 

BDP4J has divided the tasks in the following categories:
 
  * `PropertyComputingPipe` used only for computing instance properties; 
  * `TeePipe` used for storing instances when needed by user; 
  * `TargetAssigningPipe` used for assigning the real target class on classification; 
  * `TransformationPipe`, which transforms the instance data. 

 These task categories allow including additional constraints on tasks. Hence, the input and output data types of a task (`getInputType` and `getOutputType`) should be the same for all tasks except for `TransformationPipe`, which could be different. Moreover, the number of instance properties should be increased after an instance is processed through a `PropertyComputingPipe`, and the target attribute of an `Instance` should not be null after the execution of a `TargetAssigningPipe`. Finally, the number of `TargetAssigningPipe` included in a pipeline must be 0 or 1. 

**Instance invalidation**

The instance invalidation is the capability of discarding a data instance when we detect its invalidity during the pipelining process. The invalidation of a data instance could be invoked in any task belonging to the pipeline and implies that it will not be further processed.

**Last instance notification**

The detection of the last data instance is provided in the `AbstractPipe` class by providing a default implementation for `isLast` method. This implementation marks an instance i as last when the instance is processed alone through a call to pipe method or when i is the last valid instance of a collection of data.

**Parallel processing**

Currently, the support of parallel processing schemes in BDP4J is limited to the use of `ParallelPipes` task orchestration class. When several tasks are marked to be executed in parallel, they are executed in separate threads. 

**Data sharing**

To adequately support burst data processing and data sharing, we need all instances included in a data burst to get processed by a certain task, in order to fill the shared information. To cope with these situations, BDP4J executes each task on all available instances of the burst before starting the execution of the next task. This behaviour was implemented in `pipeAll` methods of orchestration schemes (`SerialPipes` and `ParallelPipes`).

**Load pipeline orchestration from XML**

This functionality is connected with the dynamic loading of tasks from *.jar files and cannot be used in independently. `Configurator` class (see Figure 1) provides the functionality of loading the pipeline configuration using DOM API. The XML file should contain the `configuration/general/pluginsFolder` parameter, which allows defining the directory where the `.jar` files containing task definitions are located.

**Dynamic loading of jars containing task definitions (pipes).**

The dynamic loading of tasks from jar files was implemented through the default Java service-provider loading facility (`java.util.ServiceLoader` included in Java 8). Using this facility, BDP4J searches for Pipe implementations included in the location specified by `pluginsFolder` parameter. To facilitate the use of standard Java service loader by avoiding the manual  creation of file `META-INF/services/org.bdp4j.pipe.Pipe`, all classes implementing tasks can be annotated with the `@AutoService(Pipe.class)`. Figure 2 shows how the
orchestration of a pipeline can be easily represented in XML format (Figure 2a) and loaded for its execution using BDP4J framework (Figure 2b).

![XML orchestrating facilities included in BDP4J](https://moncho.mdez-reboredo.info/imgbdp4j/Figure2.png)

Figure 2. XML orchestrating facilities included in BDP4J

The source code included in Figure 2b (configurePipeline method) makes it possible to automatically instantiate as many SerialPipes and ParallelPipes as necessary to load the orchestration and configure tasks (because task configuration parameters are also included in XML, Figure 2a).


**Dataset utilities to facilitate integration with Weka.**

After processing some data bursts, they can be analysed by using external Weka ML library. We have implemented a feature to transform a BDP4J dataset stored in memory (`Dataset` class) to Weka dataset (`weka.core.Instances`) through the `getWekaDataset` method. Additionally, the `CSVDatasetReader` class implements the loading of a CSV file to instantiate a BDP4J Dataset (Figure 3b).

![Weka integration facilities included in BDP4J](https://moncho.mdez-reboredo.info/imgbdp4j/Figure3.png)

Figure 3. Weka integration facilities included in BDP4J


**Resume execution from a particular task**

BDP4J supports the resumption of the execution of a pipeline that has been stopped for any reason (an application failure, accidental power down of computer...). To this end, `ResumableSerialPipes` and `ResumableParallelPipes` were implemented. The inner operation of the mentioned classes includes saving the state of instances after executing each task to allow resuming the pipeline from the last successfully executed task. One of the most relevant challenges to implement the resuming functionality was its compatibility with the data sharing between tasks. To this end, pipes can be marked by implementing `SharedDataConsumer` and/or `SharedDataProducer` interfaces. These interfaces include the methods `readFromDisk` and `writeToDisk`, respectively, to allow the programmer defining how to save and read the shared information to make it available when resuming the execution of a pipeline.

Additionally, the resuming behavior can be achieved with an XML file using resumable and debug modifiers included in pipeline tag. When resumable is set to “yes” (“true” or “1”), the pipeline can be resumed. The debug modifier included in pipeline could be set
to “no” (“false” or “0”) only if the last complete result of a task remains stored on disk, or “yes” if all partial results are kept on disk (useful to manually drop steps and repeat tasks and only applicable to `ResumableSerialPipes`).

**Developer mode**

Given the support of resuming a pipeline from a certain position, it was implemented the debugging mode function. It allows developers of a task to avoid the processing of all previous required tasks (when they were previously executed) in order to reduce the time required to test whether the new task is operating properly. The code snippets (XML and Java) included in Figure 4 provide a detailed description of how to take advantage of this functionality when the orchestration is defined in XML (Figure 4a) or in Java (Figure 4b). We highlighted in bold the instructions used to select the task that is being debugged.

![BDP4J debug mode snippet](https://moncho.mdez-reboredo.info/imgbdp4j/Figure4.png)

Figure 4. BDP4J debug mode snippet

As we can see from Figure 4, the orchestrations defined in both columns are exactly the same. In the case of using the XML to define the orchestration, the task (pipe) that is being debugged should include a debug tag, and the entire pipeline should be executed in resumable mode (`resumable=”yes”`). Additionally, for source code orchestrations, the debug mode implies the use of `ResumableSerialPipes` and `ResumableParallelPipes` classes.


**A javax/swing GUI to build a pipe-based task.**

The GUI is launched when using `gui` as the first parameter for the execution of the main class `org.bdp4j.Main` and allows visually defining, executing and saving a pipeline orchestration.  Use `java -jar target/bdp4j-2.0.0-SNAPSHOT.jar gui`.



## Using BDP4J
Add the following repositories to your POM file:

```
    <repositories>
        <repository>
            <id>sing-maven-releases</id>
            <name>SING Maven Releases</name>
            <url>https://maven.sing-group.org/repository/maven-releases/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>sing-maven-snapshots</id>
            <name>SING Maven Snapshots</name>
            <url>https://maven.sing-group.org/repository/maven-snapshots/</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
     </repositories>
```
    
Add the following dependency to your project:

```
    <dependencies>
        <dependency>
            <groupId>org.bdp4j</groupId>
            <artifactId>bdp4j</artifactId>
            <version>1.0.1</version>
        </dependency>
    <dependencies>
```

In the repository https://github.com/sing-group/bdp4j_sample an example of use can be found to process SMS messages from http://www.esp.uem.es/jmgomez/smsspamcorpus/ a make a simple Weka 10-fold crosvalidation experiment. It is very simple but you can find in the example several pipes of different pipes working together. 

## References
McCallum, Andrew Kachites.  "MALLET: A Machine Learning for Language Toolkit." http://mallet.cs.umass.edu. 2002.

## Authors
This project has been conceived and developed by SING research group.
The development team is composed by:
* Yeray Lage: Developer 
* José R. Méndez: Subject Matter Expert
* María Novo: Software Architecture Design and Team Leader

## License
BDP4j implements a pipeline framework to allow definining 
project pipelines from XML. The main goal of the pipelines of this 
application is to transform imput data received from multiple sources 
into fully qualified datasets to be used with Machine Learning.

Copyright (C) 2018  Sing Group (University of Vigo)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
