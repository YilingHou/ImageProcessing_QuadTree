Names: Yiling Hou & Yang Wu

How to run: 
edge detection: java Main -i filename.ppm -e > out.ppm
apply filter: java Main -i filename.ppm -x > out.ppm
compresson: java Main -i filename.ppm -c
tree representation: add a "-t" after the above commands

Implementation Explanation: During the edge detection,
a gear is used to set different thresholds for different
images. Because images like Fuji have very light color,
so it needs a lower threshold.

Known bugs and limitations:
In image compression, when generating some pictures, the height (Ymax - Ymin + 1) becoms 0 and thus the error says "cannot be divided by zero" while splitting a node.
this is because Ymin wrongly becomes the same as Ymax. It should be ymax/2 - 1. We cannot find why it becomes the same as Ymax.

Extra Credits:
Able to read not only squaure images, but any rectangular
This is done by parsing the rows and columns separately.

Design of filter: This filter turns all the colors into gray.
                  It separates all original pixels into two
                  parts, lighter and darker. Then sets the
                  lighter pixels with a light gray, the darker
                  pixels with a darker gray.