/* Name: Yiling Hou & Yang Wu
* File: Main.java
* Desc:
*
* The driver code of the project. It detects the flags
* in the command line and run the corresponding command
*
*/

import java.io.*;
import java.util.Scanner;

public class Main {

    /**
     * write an image to ppm file
     *
     * @param filename The filename
     * @param image    The Pixel array of the image
     * 
     */
    public static void writeImg(String filename, Pixel[][] img) throws IOException {
        PrintWriter out = new PrintWriter(filename);
        out.print("P3 ");
        out.println(img[0].length + " " + img.length + " 255");
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length; j++) {
                out.print(img[i][j].getColor().getRed() + " " + img[i][j].getColor().getGreen() + " "
                        + img[i][j].getColor().getBlue() + " ");
            }
            out.println();
        }
        out.close();
    }

    /**
     * Parses the ppm file to a Pixel array
     *
     * @param filename The filename
     * @return The Pixel array of the image
     * 
     */
    public static Pixel[][] parseFile(String filename) throws FileNotFoundException {
        Scanner input = new Scanner(new File(filename));
        int numRows = 0;
        int numColumns = 0;
        input.nextLine();
        String test = input.nextLine();
        if (!test.contains("#")) {
            String[] rowCol = test.split(" ");
            numRows = Integer.parseInt(rowCol[0]);
            numColumns = Integer.parseInt(rowCol[1]);
        } else {
            numRows = input.nextInt();
            numColumns = input.nextInt();
            input.nextLine();
        }
        Pixel[][] image = new Pixel[numRows][numColumns];
        input.nextLine();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                int rIndex = input.nextInt();
                int gIndex = input.nextInt();
                int bIndex = input.nextInt();
                Color current = new Color(rIndex, gIndex, bIndex);
                Pixel newest = new Pixel(i, j, current);
                image[i][j] = newest;
            }
        }
        return image;
    }

    /**
     * Sets the Colors to black or white
     *
     * @param image The Pixel array of the image
     * @return The Pixel array of the image after edge detection
     * 
     */
    public static Pixel[][] edgeDetection(Pixel[][] image) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                if (image[i][j].isWhite == true) {
                    image[i][j].getColor().setWhite();
                } else {
                    image[i][j].getColor().blackenColor();
                    ;
                }
            }
        }
        return image;
    }

    /**
     * prints a ppm file
     *
     * @param image The Pixel array of the image
     * 
     */
    public static void printPPM(Pixel[][] image) {
        System.out.println("P3");
        int x = image.length;
        int y = image[0].length;
        System.out.println(x + " " + y);
        System.out.println("255");
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                System.out.print(image[i][j].getColor().toString());
            }
            System.out.println();
        }
    }

    /**
     * A filter that uses gray to display an image
     *
     * @param image The Pixel array of the image
     * 
     */
    public static void shadowFilter(Pixel[][] image) {
        System.out.println("P3");
        int x = image.length;
        int y = image[0].length;
        System.out.println(x + " " + y);
        System.out.println("255");
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                image[i][j].getColor().shadowColor();
                System.out.print(image[i][j].getColor().toString());
            }
            System.out.println();
        }
    }


    

    public static void main(String args[]) throws IOException {
        String filename = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("-i")) {
                filename += args[i + 1];
                break;
            }
        }
        Pixel[][] image = parseFile(filename);
        QuadTree qtree = new QuadTree(0, image[0].length - 1, 0, image.length - 1);

        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("-")) {
                if (args[i].contains("o")) {
                    System.out.println("root name: " + filename);
                }
                // only one of -c -e -x will be given
                if (args[i].contains("c")) {
                    qtree.compressionSplit(0.002, image);
                    QuadTree.newPPM(image, qtree.getRoot());
                    image = qtree.compressMerge(image);
                    printPPM(image);
                    // printPPM(newest);
                    // qtree.compressionSplit(0.004, image);
                    // qtree.compressionSplit(0.01, image);
                    // qtree.compressionSplit(0.033, image);
                    // qtree.compressionSplit(0.077, image);
                    // qtree.compressionSplit(0.2, image);
                    // qtree.compressionSplit(0.5, image);
                    // qtree.compressionSplit(0.75, image);
                } else if (args[i].contains("e")) {
                    int gear = qtree.getRoot().determineThreshold(image);
                    qtree.edgeDetect(image, gear);
                    edgeDetection(image);
                    printPPM(image);
                } else if (args[i].contains("x")) {
                    shadowFilter(image);
                }
                if (args[i].contains("t")) {
                    // quadtree outline
                }
            }
        } // complevel越大允许的leaves越多，越小internal越少 //convert compressed images into ppm
        System.out.println(qtree.toStringPreOrder());
        // System.out.println("internals: " + qtree.numInternal());
        // System.out.println("level: " + qtree.level);
        // System.out.println("expected leaves: " + qtree.numLeaves());
    }

}