# ThriftTest
Thrift Test is a small project that tries to figure out how thrift performs in several situations 

## Covered so far are

  * Structure with header information and a map of type <string, string> as body
  * Structure with header information and a binary data type as body
    * First variant provides a map of type <string, string> as body content
    * Second variant provides a structure with 10 fields as body content

## Requirements

  * Thrift compiler > 0.9

## Installation

After checkout the thrift compiler must be execute with the following parameters

     -out ./src/gen/java -gen java ./src/main/resources/Components.thrift
     
from the source directory of the project
