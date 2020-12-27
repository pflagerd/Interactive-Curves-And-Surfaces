JGLUT is a free implementaion of the GLUT specification for Java.

It's based on freeglut and includes glew.

This version uses freeglut 3.0.0 and glew 2.0.0.

It is currently compiled with MINGW64, which must be installed and included in your path.

## Getting It
Get it here: <http://freeglut.sourceforge.net/index.php#download>

Get glew here: <http://glew.sourceforge.net/>

Here's some important advice: <http://www.transmissionzero.co.uk/computing/using-glut-with-mingw/>

JNI information:
 * <https://en.wikipedia.org/wiki/Java_Native_Interface>
 * <https://en.wikipedia.org/wiki/Java_AWT_Native_Interface>

## Building
* Build freeglut

### To build freeglut 
I'm sick of futzing with bloody MSVC. I want to build the whole thing with 64-bit MinGW64 and Linux.
1. Try getting autotools to build in 2.8.1
  * ./configure

  failed like this:
  
  ![clipboard](http://i.imgur.com/gSGcgoA.png)

2. Try getting autotools to build in 3.0.0.
  * ./configure doesn't exist

3. Install freeglut and glew with pacman.
  * Select the 64 bit libraries