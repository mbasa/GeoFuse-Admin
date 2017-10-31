Prerequisites
=============

A fully installed GeoFuse package which contains the following components:

* [Tomcat](http://tomcat.apache.org)
* [GeoServer](http://www.geoserver.org)
* [PostGIS](http://www.postgis.org)

To Install
----------
* download the source code and build a WAR file using Maven

```
  mvn clean install
```

* copy the created WAR file into tomcat_dir/webapps directory

* start tomcat

To Test
-------
* to test without deploying to Tomcat, run the Maven command:

```
  mvn jetty:run
```

* and use the following URL to access the application: 

```
http://localhost:8080/geofuse-admin
```

