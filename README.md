test
AnyType
=============

This contains the AnyType android application project. It currently only runs on the Asus Transformer Prime tablet. 
The code in this repository is only for the "photo" version of the application. I plan to merge the photo and video 
versions of the application into this single repository for the sake of streamlining and making update to the video 
version of the next few weeks. 


Links
=============
www.artfordorks.com/anytype
www.ischool.berkeley.edu/~ldevendorf/anytype


Upates
=============
1-4-13
Tried to fix the repositories which had some serious mismatching issues. The code I uploaded at this stage turned out to 
be old buggy code that I had previously fixed

2-14-13
Finally took some time to fix the project and make sure all current the most up to date code is in these files and working.
I added a few comments to the classes to make things clearer.

2-19-13
Got the video fully integrated with the photo version. The video is playing back much cleaner than it was in previous versions but I have some bug fixing to do as far as switching between playing letters on the canvas. Also, the thread that is saving out the video frames tends to take its sweet time so I might look to speeding that up as well for the future.

2-21-13
Got the video working much nicer. Now the code is able to support both sequential playback in the letter as well as playing all of the shapes within the letter at once. Multiple letters can play at one time. 

2-28-13
Switching between sequenial and concurrent playback working in canvas. Improved the workflow of sequential playback. 

To Do:
- Debug custom video shapes - mostly done
- Debug the letter editor
- Save custom paths and reload ... make datafile for each font
- make shape paths sharper and less rigid

