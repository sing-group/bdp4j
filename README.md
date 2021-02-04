[![Build Status](https://travis-ci.com/sing-group/bdp4j.svg?branch=develop)](https://travis-ci.com/sing-group/bdp4j)
[![lifecycle](https://img.shields.io/badge/lifecycle-maturing-blue.svg)](https://www.tidyverse.org/lifecycle/#maturing)

# Big Data Pre-processing For Java (BDP4J)

This project is a simple pipeline implementation derived from the pipeline of Mallet (http://mallet.cs.umass.edu, some source has brought from there as you can find in comments) with some interesting features.
Mallet classes `Pipe`, `SerialPipes` and `Instance` were transformed into `AbstractPipe`, `SerialPipes` and `Instance` BDP4J classes respectively. Using this architecture as a starting point, we implemented some interesting features, achieving a product that is quite different from the Mallet pipeline implementation.

BDP4J tasks can be developed by extending and implementing Pipe interface or extending from AbstractPipe class. `SerialPipes` and `ParallelPipes` classes allows to organize the execution of tasks and build the pipeline.

BDP4J implements a burst-based scheme. This ensures a collection of instances (captured during a certain time period) can be processed as a transaction through calling the `pipeAll` method included in tasks and pipelines.

In the repository https://github.com/sing-group/bdp4j_sample an example of use can be found to process SMS messages from http://www.esp.uem.es/jmgomez/smsspamcorpus/ a make a simple Weka 10-fold crossvalidation experiment. It is very simple but you can find in the example several pipes of different pipes [MRPR1]working together. 

**Input-Output Type check**  

BDP4J Tasks must implement the methods `getInputType` and `getOutputType` included in `Pipe` interface. `getInputType` method defines the datatype that must be stored in `data` attribute of the instance before executing the task. Additionally, `getOutputType` stands for the datatype of the information stored the `data` attribute after executing the task. BDP4J (specially through `SerialPipes` and `ParallelPipes` classes) takes advantage of these methods to perform a data type check for the whole pipeline. BDP4J also checks dinamiycally the Instances after executing each task. BDP4J 2.0 allows to use `java.lang.Object.class` as the result of these methods to define generic Pipes and includes `org.bdp4j.pipe.CombinePropertiesPipe` generic task that allows to create a new property by combining properties previously computed in the Instance.

**`alwaysBefore` and `notAfter`** constraints. 

Extending the `AbstractPipe` class implies to call `super` constructor and provide specific values for `alwaysBeforeDeps` (“Always Before”) and `notAfterDeps` (“Not After”) attributes. These attributes contain a list of single tasks. The former indicates which ones must be executed before the current whilst the latest represents those that cannot be executed later than the actual one.

**Constraints based on the kind of pipes**. 

BDP4J incorporates some annotations to include each task into one of the following types:
 
  * `PropertyComputingPipe`: Those used only for calculating properties; 
  * `TeePipe`: Used to save the current status of an instance; 
  * `TargetAssigningPipe`: They associate labels to an instance (used only in classification contexts); 
  * `TransformationPipe`: Those performing changes in the `data` attribute. 

These categories allow BDP4J to know some information about the inner operation of tasks that can be used to define some constraints. As an example, the same datatype must be specified for input and output of any task except if it is annotated as a `TransformationPipe`, the number of instance properties should be increased after a `PropertyComputingPipe` task, `target` should not be `null` for the instances processed by a `TargetAssigningPipe` and, finally, only 1 (or zero) `TargetAssigningPipe` can be executed in a pipeline. 

**Instance invalidation**

BDP4J allows discarding Instances during the pipelining process. When an inconsistence is detected during the execution of a task, the developer can call to `invalidate` method included in the `Instance` class. This implies the instance will not be further processed avoiding runtime errors that could happen in tasks executed later (and hence, the need of implementing additional task checks) and saving processing time.

**Last instance notification**

`AbstractPipe` class provides an `isLast` method which returns `true` when only the current instance remains to be processed. This is especially useful when the execution of the pipeline is invoked for a collection of instances (e.g. `p.pipeAll(instCollection)`) and allows developers to ensure streams are flushed and closed after processing the last instance. Additionally, streams may remain opened (avoiding open/close operations) while processing a data burst.

**Parallel execution**

`ParallelPipes` supports parallel processing schemes to take advantage of multithread and multicore capabilities of current computers. Developers should take into consideration the usage of shared resources (disk files, database connections, etc.) when using this feature.

**Data sharing**

The communication between pipeline tasks may be not limited to task input-output instances. As an example a task could compute data that is not stored in instances (e.g. a dictionary of words). This data may be used by in latter tasks. To use this function, the task generating the data should implement `SharedDataProducer` interface and the one consuming the data `SharedDataConsumer`. When calling `pipeAll` method, each task is executed for all instances included in a burst before the execution of the next task. BDP4J will invoke `writeToDisk` method of `SharedDataProducer` tasks and `loadFromDisk` method of `SharedDataConsumer` tasks to keep/restore data in/from disk. 

**XML pipeline definition && dynamic loading of jars containing task definitions (pipes)**

Tasks can be loaded dynamically from `.jar` files. `Configurator` class implements the loading a pipeline from an XML file using DOM API. The XML file contains attributes such as   `configuration/general/pluginsFolder` (defined using xpath) used to define the directory where `.jar` files are located. The whole list of tasks is also loaded from the XML files.

Java service-provider loading facility (`java.util.ServiceLoader` included in Java 8) has been used to search for Pipe implementations in jar files stored in `pluginsFolder` directory. To use this functionality all classes implementing tasks should be annotated using `@AutoService(Pipe.class)`. The following XML code contains the definition of a pipeline.

```XML
<?xml version="1.0"?>

<configuration>	
  <!-- General properties -->
  <general>
    <samplesFolder>/home/user/samples</samplesFolder>
    <pluginsFolder>/home/user/plugins</pluginsFolder>
    <outputDir>/home/user/output</outputDir>
    <tempDir>/tmp</tempDir>
  </general>

  <!-- Tasks definitions can be downloaded from bdp4j_sample respository
     https://github.com/sing-group/bdp4j_sample
  -->
  
  <!-- the pipeline orchestration -->
  <pipeline resumable="yes" debug="no">
    <serialPipes>
	   <pipe>
       <name>File2TargetAssignPipe</name>
      </pipe>
	 
      <pipe>
        <name>File2StringPipe</name>
      </pipe>
	 
      <pipe>
        <name>String2TokenArray</name>
      </pipe>
		
      <pipe>
        <name>TokenArray2FeatureVector</name>
      </pipe>
		
      <pipe>
        <name>
          GenerateFeatureVectorOutputPipe
        </name>
        <params>
          <pipeParameter>
            <name>outFile</name>
            <value>out.csv</value>
          </pipeParameter>
        </params>
      </pipe>
    </serialPipes>
  </pipeline>
</configuration>
```

To load the previous pipeline, the following Java source can be used.

```Java
/* Load XML */
Configurator cfg = Configurator.getInstance("cfg.xml");

/*Load tasks*/
PipeProvider pipeProvider = new PipeProvider(
              cfg.getProp(Configurator.PLUGINS_FOLDER)
 );

HashMap<String, PipeInfo> pipes = pipeProvider.getPipes();

/*Load the pipeline*/
Pipe p = Configurator.configurePipeline(pipes);

System.out("Pipeline: " + p.toString() + "\n");

/*Check dependencyes*/
if (!p.checkDependencies()) {
  logger.fatal( "[CHECK DEPENDENCIES] "+ AbstractPipe.getErrorMessage() );
  System.exit(-1);
}

/*Load and pipe the current burst*/
ArrayList<Instance> burst = …
p.pipeAll(burst);

```

**Dataset utilities to facilitate integration with Weka.**

BDP4J includes the class `org.bdp4j.util.CSVDatasetWriter` which facilitates the creation of dynamic datasets from pipes. This class is very useful for the creation definition of TeePipes. An example of the functionality of this class can be found in `org.bdp4j.sample.pipe.impl.GenerateStringOutputPipe` class source included in bdp4jsample repository (https://github.com/sing-group/bdp4j_sample/blob/master/src/main/java/org/bdp4j/sample/pipe/impl/GenerateStringOutputPipe.java). By including such tasks into pipelines, data can be saved as CSV.

`Dataset` class represents a dataset stored in RAM. A Dataset can be dynamically computing by adding rows and columns from pipeline tasks or reading a CSV file using `CSVDatasetReader` class. `Dataset` class allows to automatically build a Weka dataset (`weka.core.Instances`) by simply invoking `getWekaDataset` method. An example of source combining BDP4J and Weka functionalities is included below.

```Java
CSVDatasetReader csvdr =  new CSVDatasetReader(“example.csv”);
Dataset ds =  csvdr.loadFile();
Instances wekaDS = ds.getWekaDataset();
        
wekaDS.deleteStringAttributes();

wekaDS.setClassIndex( wekaDS.numAttributes() - 1 );

int num = wekaDS.numInstances();
int start = (num * 80) / 100;
int end = num - start;

Instances trn = new Instances(wekaDS, 0, start);
Instances tst = new Instances(wekaDS, start, end);

try {
 Evaluation rfEval = new Evaluation(tst);
 RandomForest rf = new RandomForest();
 rf.buildClassifier(trn);
 rfEval.evaluateModel(rf, tst);
} catch (Exception ex) {

}

```

**Resuming pipeline execution**

BDP4J can resume the execution of a pipeline that has been stopped for any reason (an application failure, accidental power down of computer...). To achieve this behaviour, the usage of `SerialPipes` and `ParallelPipes` should be replaced by `ResumableSerialPipes` and `ResumableParallelPipes` respectively. These classes save the state of instances after executing each task with the goal of resuming the pipeline starting just after the last successfully executed task. This functionality is also compatible with data sharing between tasks when `SharedDataConsumer` and/or `SharedDataProducer` interfaces are implemented. The methods `readFromDisk` and `writeToDisk`, respectively, to allow the developer to define how the shared data should be saved and read for guaranteeing its availability when resuming a pipeline.

When pipeline is represented in an XML file, the `resumable` and `debug` modifiers can be used so activate/deactivate this feature. When `resumable` is set to “yes”/true”/“1”, the pipeline can be resumed. The `debug` modifier allows to decide whether storing in disk the results of all tasks (“yes”/true”/“1”) or only the minimum required ones to ensure resumability ("no"/"false"/"0"). The usage of parallel tasks with `debug=0` could cause results of more than one task are stored in disk.

**Task Developer Mode**

The developer of a certain task will use a small set of data to test if his/her task is running correctly. However, to execute a certain task, some additional ones should be previously executed to ensure the ´data´ attribute contains the necessary input data for the target task. In such a situation, this function allows to a task developer skipping the execution of all previous required tasks. These tasks are executed only once and their result is stored to disk. Then each time the pipeline is launched, the results of executing previous tasks are loaded into memory and directly processed by the target task. This mechanism allows developer to reduce the time required to test whether the new task is operating properly. 

To mark a task for debugging, in a XML pipeline, the concrete task should be marked with the `debug` tag. Next example shows how the previous XML example file is transformed for debugging the task `String2TokenArray`.

```XML
<?xml version="1.0"?>
<configuration>
 <!-- General properties -->
 <general>

   ...

 </general>

 <pipeline resumable="yes" debug="yes">
  <serialPipes>

   ...

   <pipe>
    <name>String2TokenArray</name>
    <debug/>
   </pipe>
   
	...
	
  </serialPipes>
 </pipeline>
</configuration>

```
The same behaviour can be implemented in Java using the method `setDebugging` with the parameter `true` as shown below.

```Java

//Set debug=on for String2TokenArray task
String2TokenArray s2ta =  
     new String2TokenArray();
s2ta.setDebugging(true);
        
//Create the pipeline
AbstractPipe p =  new ResumableSerialPipes(
  new AbstractPipe[]{
   new File2TargetAssignPipe(),
   ...
   s2ta,
   ...
  }
 );

```

**A javax/swing GUI (Graphical User Interface) to build a pipe-based task.**

BDP4J includes a graphical user interface for visually defining, executing and saving a pipeline. For launching it, simply use `java -jar target/bdp4j-2.0.0-SNAPSHOT.jar gui`. 



## Using BDP4J
Add the following repositories to your POM file:

```XML
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

```XML
    <dependencies>
        <dependency>
            <groupId>org.bdp4j</groupId>
            <artifactId>bdp4j</artifactId>
            <version>1.0.2</version>
        </dependency>
    <dependencies>
```

## References
McCallum, Andrew Kachites.  "MALLET: A Machine Learning for Language Toolkit." http://mallet.cs.umass.edu. 2002.

## Authors
This project has been conceived and developed by SING research group.
The development team is composed by:
* Yeray Lage: Developer 
* José R. Méndez: Subject Matter Expert
* María Novo: Software Architecture Design and Team Leader

## License
BDP4j implements a pipeline framework to allow defining 
project pipelines from XML. The main goal of the pipelines of this 
application is to transform input data received from multiple sources 
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

[MRPR1]¿??
