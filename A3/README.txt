CS 349 Assignment 3 README
Elisa Lou 20372456


How to Run:
-The makefile is located in the "src" directory. 
-"make all" or "make" will compile and run the program.
-"make run" will run the compiled program.
-"make clean" will remove all .class files in the "src", "src/view", and "src/model" directories, and all of the generated .gif files in the "src" folder.


Notes:
-The top toolbar contains buttons for drawing (pencil), erasing (eraser), selecting (dotted square), and inserting frame (copy), as required. Users can deselect selected objects by toggling the select button if they want to reselect different objects. 
	-There is also a button for deleting the animation (trash can) to allow users to easily restart from scratch.
	-There is also a button for exporting the animation as a gif. The gifs expired are automatically saved in the "src" directory. Every frame is recorded. The exported gif will have the same dimensions as the canvas dimensions. After export, the program is set to 'selection' mode.

-The slider below the canvas area is only activated when there is at least one frame. Frame ticks appear as users begin to animate. I chose only to show the 0th and last frame label to avoid clutter that occurs when the animation consists of hundreds of frames. The tick spacing is set to number of frames divided by 10 (thus we should only see 10 ticks between the 0th and last frame). 

-The bottom toolbar contains buttons typical of any video player.  |< allows the user to skip to frame 0, while >| allows user to skip to the last frame. << allows users to decrease by 1 frame, and >> allows users to increase by 1 frame. The play/pause button will allow user to play the animation and pause when desired. if the slider is at the last frame and the play button is pressed, the animation will be played from the beginning. Otherwise, it will be played from wherever the slider left off.

-The right toolbar is reserved for drawing options. Users may choose from preselected colours, or choose their own colour via the colour picker dialog that appears when clicking the default grey "Picker". They also have the option of selecting from 3 stroke sizes. These stroke sizes are also reflected in the cursor size once selected.

-The UML diagram is in the root directory, named "uml.pdf", created using the ObjectAid UML Class Diagram plugin. I have 1 main model, and I have my own "segment" class that holds information for each drawn segment. I have an enum class for which "state" the program is in - drawing, erasing, selection, dragging, or playing. I have 5 views for each visual component of the program - the main toolbar, slider, the playback toolbar, the colour/stroke editing toolbar, and the canvas. I did not include the classes from the "gif" folder as they were 3rd party resources (source stated in the files).


Assumptions & Scenarios:
-If we animate an object from time frame 0 to 10, and reanimate this object from time from 2 to 5, assume the object's animation from time 6 to 10 should stay put. If they want to get rid of the future animation, they can easily erase the object at time 6. I wanted to assume this because it is very silly for a user to have a 200 frame animation, and they only wanted to reanimate frames 4-6. It is more user friendly to allow manual deletion of future frames.
-The total number of frames will decrement to time frame t-1 if an object is erased at time frame t, and no other object exists at time frame t. The slider will then be adjusted to time frame t-1.


Enhancements:
-Colour and stroke customization for drawing.
-Custom cursors for drawing (varies for stroke size) and erasing.
-Button enable/disable logic: 
	-disallow users from clicking play until an animation exists.
	-guides users to tell if they are in draw or erase mode as the respective buttons would be disabled during either mode. 
-Export animation as gif.


