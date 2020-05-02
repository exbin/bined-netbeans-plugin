BinEd - Binary/Hexadecimal Editor - NetBeans Plugin
===================================================

Hexadecimal viewer/editor plugin module for NetBeans platform.

Homepage: https://bined.exbin.org/netbeans-plugin  

Published as: http://plugins.netbeans.org/plugin/67898/  

Screenshot
----------

![BinEd-Editor Screenshot](images/bined-screenshot.png?raw=true)

Features
--------

  * Visualize data as numerical (hexadecimal) codes and text representation
  * Codes can be also binary, octal or decimal
  * Support for Unicode, UTF-8 and other charsets
  * Insert and overwrite edit modes
  * Searching for text / hexadecimal code with found matches highlighting
  * Support for undo/redo
  * Support for files with size up to exabytes
  * Show debug variables as binary data

Compiling
---------

Java Development Kit (JDK) version 8 or later is required to build this project.

Currently Gradle version 4.x is needed to build this project.

Use:  
gradle wrapper --gradle-version 4.10  
./gradlew build nbm  

License
-------

Apache License, Version 2.0 - see LICENSE-2.0.txt
