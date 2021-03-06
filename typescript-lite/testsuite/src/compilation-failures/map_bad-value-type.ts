import {WithPrimitivesMap} from "./tslite-bindings/org.coursera.maps.WithPrimitivesMap";

const wpm: WithPrimitivesMap = {
  "bytes" : {
    "b" : "\u0003\u0004\u0005",
    "c" : "\u0006\u0007\b",
    "a" : "\u0000\u0001\u0002"
  },
  "longs" : {
    "b" : 20,
    "c" : 30,
    "a" : "what am I doing here?" // oops! not a number
  },
  "strings" : {
    "b" : "string2",
    "c" : "string3",
    "a" : "string1"
  },
  "doubles" : {
    "b" : 22.2,
    "c" : 33.3,
    "a" : 11.1
  },
  "booleans" : {
    "b" : false,
    "c" : true,
    "a" : true
  },
  "floats" : {
    "b" : 2.2,
    "c" : 3.3,
    "a" : 1.1
  },
  "ints" : {
    "b" : 2,
    "c" : 3,
    "a" : 1
  }
};
