# Big Data Pipelining For Java (bdp4j)

This project is a simple pipeline implementation derived from the pipeline of Mallet 
(http://mallet.cs.umass.edu, some source has brought from there as you can find in comments) with 
some interesting features (some of them to appear). Interesting features are:

* Input-Output Type checking: support is developed and enabled but I am afraid that checking should 
  be invoked manually (not while building pipes as implemented now).
* alwaysBefore and notAfter constraints. The former are pipes that should be executed before. 
  For instance in textMining, if you have a StopWordRemoverPipe (that find and remove stopwords using 
  a list of uppercase stopwords), this pipe would have an alwaysBefore dependence with TransformTextToUppercase. 
  NotAfter dependencies represent pipes that cannot be executed after one. For instance, if you have a pipe 
  to drop HTML tags (DropHTMLTags) this pipe would have a notAfter dependence with another pipe that detects 
  if the content is HTML (DetectHTML).
* Load pipe infrastructure from XML (under development).
* Dynamic loading of jars containing pipes (currently studying libraries and methods to implement this feature).
* Different kind of pipes (that would imply newer checkings in future). Pipes can be 
  PropertyComputingPipe (they only computes properties and do not transform the data), 
  TransformDataPipe (they transform data), 
  TargetAssigningPipes (they detect the target attribute for classification of prediction issues) 
  and TeePipes (that save to file/s all instances and detect the last instance to save in order to allow pipe programmers 
  to have open files, i.e. CSV files and close them with isLast()).
* Instance invalidation. An instance can be invalidated at any moment of the process and will not be further processed.
* Some dataset utilities to facilitate integration with Weka. (see Main class in the provided example)
* Parallel pipe (in progress but... the usage would be limited to the API and development is currently stopped).
* A javax/swing GUI to build a pipe-based task (future)
* Although basic pipe functionalities are currently working, but we are still developing some interesting ones. 

In the repository https://github.com/sing-group/bdp4j_sample an example of use can be found to process SMS messages from 
http://www.esp.uem.es/jmgomez/smsspamcorpus/ a make a simple Weka 10-fold crosvalidation experiment. It is very simple 
but you can find in the example several pipes of different pipes working together. 

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
