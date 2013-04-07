CS 349 Assignment 5 README
Elisa Lou 20372456


How to Run:
-The makefile is located in the "src" directory. 
-"make all" or "make" will compile and run the program.
-"make run" will run the compiled program.
-"make clean" will remove all .class files in the "src", "src/view", and "src/model" directories, and all of the generated .gif files in the "src" folder.


Notes:
-Sample loadable xml files are located in the xml-files directory.
-The expected directory for opening xml files from the device (or emulator) is mnt/sdcard/.
-To load from the app, press the menu button and select Load, then select the file to load.
-To access config settings, press the menu button and select Settings. The user can change background color and FPS here.

Assumptions & Scenarios:
-If user already loaded a file named "file.xml" and update it, they won't be able to load it again ....

Enhancements:
-filter by file extension when in the file dialog for loading. It filters by .xml extension so users do not have to see unrelated items. 

