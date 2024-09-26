/* Name: Yiling Hou & Yang Wu
* File: QuadTree.java
* Desc:
*
* The QuadTree class. Represents the image. After a Boundary is split,
* the four children of it are the four new smaller Boundaries.
*
*/

public class QuadTree {

    final int MAX_CAPACITY = 4;
    int level = 0;
    private Boundary root;

    public QuadTree(int xmin, int xmax, int ymin, int ymax) {
        root = new Boundary(xmin, xmax, ymin, ymax, 0);
    }

    public Boundary getRoot() {
        return this.root;
    }

    public void edgeDetect(Pixel[][] image, int gear) {
        edgeDetectRec(root, image, gear);
    }

    /**
     * Checks if the Boundaries are edges
     *
     * @param root  The root of the QuadTree
     * @param image The Pixel array of the image
     * @param gear  The threshold
     * 
     */
    private void edgeDetectRec(Boundary root, Pixel[][] image, int gear) {
        if (root == null) {
            return;
        }
        if (root.isLeaf()) {
            if (root.getBoundaryLevel() == this.level) {
                root.convolution(image, gear);
            } else if (root.getBoundaryLevel() < this.level) {
                root.blackenBoundary(image);
            }
        }
        edgeDetectRec(root.getNE(), image, gear);
        edgeDetectRec(root.getNW(), image, gear);
        edgeDetectRec(root.getSE(), image, gear);
        edgeDetectRec(root.getSW(), image, gear);
    }

    public void compressionSplit(double compLevel, Pixel[][] image) {
        int leaves = (int) (compLevel * root.getXmax() * root.getYmax());
        compressionSplitRec(root, leaves, image);
        // 改进分解误差
        // System.out.println("number of target leaves: " + leaves);
        // System.out.println("current: " + this.numLeaves());
        // System.out.println("larger: " + this.numLeaves() * 4);
        // if ((leaves - this.numLeaves()) > (this.numLeaves() * 4 - leaves)){
        // root.split();
        // }
    }

    /**
     * Splits the image
     *
     * @param root   The root of the QuadTree
     * @param leaves number of target leaves
     * @param image  The Pixel array of the image
     * 
     */
    public void compressionSplitRec(Boundary root, int leaves, Pixel[][] image) {
        if (this.numLeaves() >= leaves) { // 为啥大于能够让叶子小于
            // it does not match the target leaves number exactly, and we keep it smaller
            // than the target, so that the compression level will not exceed 1
            return;
        }
        if (root.needSplit(image) > 2000) { // 越大split越少
            level++;
            root.split(level);
            compressionSplitRec(root.getNW(), leaves / 4, image);
            compressionSplitRec(root.getNE(), leaves / 4, image);
            compressionSplitRec(root.getSW(), leaves / 4, image);
            compressionSplitRec(root.getSE(), leaves / 4, image);
        } else {
            root.blackenBoundary(image); // image会不会，
            return;
        }
    }

    public int numLeaves() {
        return (int) Math.pow(4, level);
    }

    public int numInternal() {
        return numInternalRec(root, 0);
    }

    private int numInternalRec(Boundary root, int count) {
        if (root.getNW() != null && root.getNE() != null && root.getSW() != null && root.getSE() != null) {
            // If all child nodes exist, this is an internal node
            count = 1;
            // Recursively count internal nodes in child nodes
            count += numInternalRec(root.getNW(), count) + numInternalRec(root.getNE(), count)
                    + numInternalRec(root.getSW(), count) + numInternalRec(root.getSE(), count);
        }
        return count;
    }

    public static void newPPM(Pixel[][] image, Boundary current) {
        // System.out.println("xmin "+current.getXmin());
        // System.out.println("xmax "+(current.getXmax() + 1));
        // System.out.println("ymin"+current.getYmin());
        // System.out.println("ymax"+(current.getYmax() + 1));
        int size = (current.getXmax() - current.getXmin() + 1) * (current.getYmax() - current.getYmin() + 1);
        //System.out.println(current.getQuadrant(image)[0].length);
        //System.out.println("size "+size);
        int reds = 0;
        int greens = 0;
        int blues = 0;
        for (int i = current.getXmin(); i <= current.getXmax(); i++) {
            for (int j = current.getYmin(); j <= current.getYmax(); j++) {
                reds += image[i][j].getColor().getRed();
                greens += image[i][j].getColor().getGreen();
                blues += image[i][j].getColor().getBlue();
            }
        }
        Color color = new Color(reds/size, greens/size, blues/size);
        for (int i = current.getXmin(); i <= current.getXmax(); i++) {
            for (int j = current.getYmin(); j <= current.getYmax(); j++) {
                image[i][j].setColor(color);
            }
        }
    }

    public Pixel[][] compressMerge(Pixel[][] image) {
        Pixel[][] empty = new Pixel[image.length][image[0].length];
        return compressMergeRec(root, image, empty);
    }
    private Pixel[][] compressMergeRec(Boundary current, Pixel[][] image, Pixel[][] empty) {
        //System.out.println(this.level);
        if (current.isLeaf()) {
            //System.out.println("长度"+current.getQuadrant(image).length);
            //System.out.println(current.isLeaf() +" "+ current.getBoundaryLevel());
            newPPM(image, current);
            for (int i = current.getXmin(); i <= current.getXmax(); i++) {
                for (int j = current.getYmin(); j <= current.getYmax(); j++) {
                    empty[i][j] = image[i][j];
                }
            }
            return empty;
        }
        //System.out.println(current.isLeaf());
        empty = compressMergeRec(current.getNW(), image, empty);
        empty = compressMergeRec(current.getNE(), image, empty);
        empty = compressMergeRec(current.getSW(), image, empty);
        empty = compressMergeRec(current.getSE(), image, empty);
        return empty;
    }

    public String toStringPreOrder() { 
        return toStringPreOrderRec(root, ""); 
     }
    private String toStringPreOrderRec(Boundary current, String s) {
        if (current != null) {
            s += current.toString();
            s = toStringPreOrderRec(current.getNW(), s);
            s = toStringPreOrderRec(current.getNE(), s);
            s = toStringPreOrderRec(current.getSW(), s);
            s = toStringPreOrderRec(current.getSE(), s);
        }
        return s;
    }

}