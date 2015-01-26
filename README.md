# ThriftTest
Thrift Test is a small project that tries to figure out how thrift performs in several situations 

## Covered so far are

  * Structure with header information and a map of type \<string, string\> as body
  * Structure with header information and a binary data type as body
    * First variant provides a map of type \<string, string\> as body content
    * Second variant provides a structure with 10 fields as body content

## Requirements

  * Thrift compiler > 0.9
  * Java 1.8+

## Installation

After checkout the thrift compiler must be execute with the following parameters

     -out ./src/gen/java -gen java ./src/main/resources/Components.thrift
     
from the source directory of the project

## Parameters

  * --elementsPerRound : elements created, serialized, deserialized within one round per test runner (Default: 200_000)
  * --mapElements : Number of elements created for the map related structures (Default: 10)
  * --rounds : Number of overall rounds (Default 5)
  * --warmUpPercentile : How many of the first elements per round should be ignored for statistic gathering (Default: 10%)
  * --runnerToExecute : Which runner should be executed? Enables solely runs. (Default: all)
  * --verbose : Should the tool also print information about the current round and test runner? (Default: false)
  * --listOnly : List the existing registered test runner and exits.
  
## Processing

  1. Register runners
  2. Register statistics collector / runner
  3. set warm up rounds per test runner and round (at the moment the first 10% of the NUMBER\_OF\_ELEMENTS\_TO\_SERIALIZE are discarded for performance counting)
  4. if all runners should be executed: shuffle registered runners and run each of them. Otherwise run single runner.
  5. print result
 
## Adding new PerformanceTestRunner

Simply implement the class PerformanceTestRun and add your class within the Application.registerRunner method.


## Example output

    Starting test run with parameters
    	   Elements in Map : 10
    	Elements per Round : 100000
    	            Rounds : 5
    	Warm up percentile : 10% of 100000
    	 Runner to execute : all
    	      Verbose mode : false
                                                 Class,                    Event, #Events,    Min (Nanos),Average (Nanos),    Max (Nanos)
                        SimpleStructurePerformanceTest,                SERIALIZE,  449995,         2138.0,           2699,      8739530.0
    CombinedStructureWithConcreteObjectPerformanceTest,                SERIALIZE,  449995,         1282.0,           2029,    6.2491741E7
                      CombinedStructurePerformanceTest,                SERIALIZE,  449995,         2138.0,           3356,    8.1037795E7
                        SimpleStructurePerformanceTest,              DESERIALIZE,  449995,         1282.0,           1983,    3.8963094E7
    CombinedStructureWithConcreteObjectPerformanceTest,              DESERIALIZE,  449995,          427.0,           1037,    4.3336279E7
                      CombinedStructurePerformanceTest,              DESERIALIZE,  449995,         1282.0,           2202,    9.2002834E7

    Overall execution time PT23.408S