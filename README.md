# ThriftTest
Thrift Test is a small project that tries to figure out how thrift performs in several situations 

## Covered so far are

  * Structure with header information and a map of type \<string, string\> as body
  * Structure with header information and a binary data type as body
    * First variant provides a map of type \<string, string\> as body content
    * Second variant provides a structure with 10 fields as body content

## Requirements

  * Thrift compiler > 0.9

## Installation

After checkout the thrift compiler must be execute with the following parameters

     -out ./src/gen/java -gen java ./src/main/resources/Components.thrift
     
from the source directory of the project

## Parameters

For now all settings needs to be done within the Application class.

  * NUMBER\_OF\_ELEMENTS\_TO\_SERIALIZE : elements created, serialized, deserialized within one round per test runner
  * NUMBER\_OF\_MAP\_ELEMENTS : Number of elements created for the map related structures
  * ROUNDS : Number of overall rounds
  
## Processing

  1. Register runners
  2. Register statistics collector / runner
  3. set warm up rounds per test runner and round (at the moment the first 10% of the NUMBER\_OF\_ELEMENTS\_TO\_SERIALIZE are discarded for performance counting)
  4. shuffle registered runners and run each of them
  5. print result
 
## Adding new PerformanceTestRunner

Simply implement the class PerformanceTestRun and add your class within the Application.registerRunner method.
