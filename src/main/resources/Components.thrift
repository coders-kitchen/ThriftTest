namespace java com.coderskitchen.serializerPerformance.gen.thrift

struct Header {
  1: required string name;
  2: optional i32 version = 1;
}

struct SimpleStructure {
  1: required Header header;
  2: optional map<string, string> body;
}

struct ObjectMap {
  1: required map<string, string> data;
}

struct EventObject {
  1: required i32 id;
   2: required string name;
   3: required bool sunshine;
   4: required string uuid;
   5: string a;
   6: string b;
   7: string c;
   8: string d;
   9: string e;

}

struct CombinedStructure {
 1: required Header header;
 2: optional binary body;
}