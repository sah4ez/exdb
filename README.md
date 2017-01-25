exdb
==========
[![Build Status](https://travis-ci.org/sah4ez/exdb.svg?branch=master)](https://travis-ci.org/sah4ez/exdb)
[![Coverage Status](https://coveralls.io/repos/github/sah4ez/exdb/badge.svg?branch=master)](https://coveralls.io/github/sah4ez/exdb?branch=master)
[![Code Climate](https://codeclimate.com/github/sah4ez/exdb/badges/gpa.svg)](https://codeclimate.com/github/sah4ez/exdb)

### ABOUT ###

Maven dependency

```xml
        <dependency>
            <groupId>com.github.sah4ez</groupId>
            <artifactId>exdb</artifactId>
            <version>1.0.2</version>
        </dependency>
```

This is a library for safe extraction from database through the ResultSet.

##### Version #####

- 1.0.3
Has issue with lost connection.

##### Why user Extractor? #####

In Extractor realized simple and short API for extraction data from database.

You have this code:
```java

    public Integer getInt(String columnName) {
        Integer i = 0;
        try {
            i = rs.getInt(columnName);
        } catch (SQLException ignored) {
        }
        return i;
    }
    
```
This code allow get int value, even if we have exception. 

So we create instance class with exception in Runtime. 

### EXAMPLE ###
```java
public class Example{
    //you load data from your DB in ResultSet and create instance of class used this ResultSet
    private Integer id;
    private String name;
    private Float price;
    private java.time.ZonedDateTime dateTime;
    
    private final com.github.sah4ez.exdb.Extractor extractor;
    
    public Example(java.sql.ResultSet resultSet){
        extractor = new com.github.sah4ez.exdb.Extractor(resultSet);
   
        this.id = extractor.getInt("id");
        this.name = extractor.getString("name");
        this.dateTime = extractor.getLocalDateTime("date");
        this.price = extractor.getFloat("price");
    }
}
```

### License ###
## [**GPL**](http://www.gnu.org/licenses/gpl.txt) ##
