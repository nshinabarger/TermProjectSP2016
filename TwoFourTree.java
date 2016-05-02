package TermProjectSP2016;

/**
 * Title: Term Project 2-4 Trees Description: Copyright: Copyright (c) 2001
 * Company:
 *
 * @author Nathan Shinabarger and Christopher Daniel Brauns II
 * @version 1.0
 */
public class TwoFourTree
        implements Dictionary {

    private Comparator treeComp;
    private int size = 0;
    private TFNode treeRoot = null;

    public TwoFourTree(Comparator comp) {
        treeComp = comp;
    }

    private TFNode root() {
        return treeRoot;
    }

    private void setRoot(TFNode root) {
        treeRoot = root;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Searches dictionary to determine if key is present
     *
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    public Object findElement(Object key) {
        TFNode temp = this.treeRoot;
        int index = this.findFirstGreaterThanOrEqual(temp, key);
        if (treeComp.isEqual(temp.getItem(index).key(), key)) {
            return temp.getItem(index).element();
        } //        while (temp.getChild(index) != null) {
        //
        //            temp = temp.getChild(index);
        //            index = this.findFirstGreaterThanOrEqual(temp, key);
        //            if (treeComp.isEqual(temp.getItem(index).key(), key)) {
        //                return temp.getItem(index).element();
        //            }
        //
        //        }
        else {
            return null;
        }
    }

    /**
     * Inserts provided element into the Dictionary
     *
     * @param key of object to be inserted
     * @param element to be inserted
     */
    public void insertElement(Object key, Object element) {
        Item item = new Item(key, element);
        if (this.treeRoot == null) {
            this.setRoot(new TFNode());
            this.treeRoot.setParent(null);
            this.treeRoot.addItem(0, item);

            //Account for the OVERFLOW case
        } else {
            TFNode node = this.treeRoot;
            int index = this.findFirstGreaterThanOrEqual(node, key);

            while (node.getChild(index) != null) {

                node = node.getChild(index);
                index = this.findFirstGreaterThanOrEqual(node, key);
            }
            node.insertItem(index, item);
            if (node.getNumItems() > node.getMaxItems()) {
                this.overflow(node);
            }
        }
    }

    /**
     * Searches dictionary to determine if key is present, then removes and
     * returns corresponding object
     *
     * @param key of data to be removed
     * @return object corresponding to key
     * @exception ElementNotFoundException if the key is not in dictionary
     */
    public Object removeElement(Object key) throws ElementNotFoundException {
        TFNode temp = this.treeRoot;

        return null;
    }

    private int findFirstGreaterThanOrEqual(TFNode node, Object key) {

        //Will break if a null pointer is passed
        int i = 0;
        while (i < node.getNumItems()) {
            if (treeComp.isLessThan(node.getItem(i).key(), key)) {
                i++;
            } else {
                return i;
            }
        }
        return i;
    }

    private int whatChildisThis(TFNode node) {
        if (node == this.treeRoot) {
            return -1;
        }
        TFNode parent = node.getParent();
        for (int i = 0; i <= parent.getNumItems(); i++) {
            if (parent.getChild(i) == node) {
                return i;
            }
        }
        return -1;
    }

    private void overflow(TFNode node) {
        if (node.equals(treeRoot)) {
            this.rootOverflow();
        } else {
            TFNode parent = node.getParent();
            TFNode leftChild = new TFNode();
            TFNode rightChild = new TFNode();

            // Use WCIT to find where index insert
            int index = this.whatChildisThis(node);

            int middle = (parent.getMaxItems() / 2) + 1;
            parent.insertItem(index, node.getItem(middle)); //shifts data

            // Note, this setup gets rid of old node, makes two new.
            parent.setChild(index, leftChild);
            parent.setChild(index + 1, rightChild);

            for (int i = 0; i < middle; i++) {
                leftChild.addItem(i, node.getItem(i));
            }
            for (int i = middle; i < rightChild.getMaxItems(); i++) {
                rightChild.addItem(i - middle, node.getItem(i + 1));
            }
            for (int i = 0; i <= middle; i++) {
                leftChild.setChild(i, node.getChild(i));
            }
            for (int i = middle; i <= node.getMaxItems(); i++) {
                rightChild.setChild(i - middle, node.getChild(i));
            }
            leftChild.setParent(parent);
            rightChild.setParent(parent);
            if (parent.getNumItems() > parent.getMaxItems()) {
                this.overflow(parent);
            }
        }

    }

    private void rootOverflow() {
        TFNode parent = new TFNode();
        TFNode leftChild = new TFNode();
        TFNode rightChild = new TFNode();

        int middle = (treeRoot.getMaxItems() / 2) + 1;
        parent.addItem(0, treeRoot.getItem(middle));
        leftChild.setParent(parent);
        rightChild.setParent(parent);

        for (int i = 0; i < middle; i++) {
            leftChild.addItem(i, treeRoot.getItem(i));
        }
        for (int i = middle; i < rightChild.getMaxItems(); i++) {
            rightChild.addItem(i - middle, treeRoot.getItem(i + 1));
        }
        for (int i = 0; i <= middle; i++) {
            leftChild.setChild(i, this.treeRoot.getChild(i));
            if (leftChild.getChild(i) != null) {
                leftChild.getChild(i).setParent(leftChild);
            }
        }
        for (int i = middle + 1; i <= this.treeRoot.getMaxItems() + 1; i++) {
            rightChild.setChild(i - (middle + 1), this.treeRoot.getChild(i));
            if (rightChild.getChild(i - (middle + 1)) != null) {
                rightChild.getChild(i - (middle + 1)).setParent(rightChild);
            }
        }
        this.setRoot(parent);
        treeRoot.setChild(0, leftChild);
        treeRoot.setChild(1, rightChild);
    }

    private void underflow(TFNode node) {
        int index = this.whatChildisThis(node);
        //Nathan Shinabarger sucks!!!!!
        if ((index != node.getMaxItems() - 1) && (node.getParent().getChild(index + 1).getNumItems() > 1)) {
            this.leftTransfer(node);
        }
        if ((index != 0) && (node.getParent().getChild(index - 1).getNumItems() + 1 > 1)) {
            this.rightTransfer(node);
        }
        if ((index != node.getMaxItems() - 1) && (node.getParent().getChild(index+1).getNumItems() == 1)){
            this.leftFusion(node);
        }
        if ((index != 0) && (node.getParent().getChild(index-1).getNumItems() == 1)){
            this.rightFusion(node);
        }
    }

    private void leftTransfer(TFNode node) {
        int index = this.whatChildisThis(node);
        TFNode parent = node.getParent();
        Item temp = parent.getChild(index).getItem(0);
        parent.insertItem(index, temp);
        parent.getChild(index).removeItem(0);
        node.insertItem(0, parent.removeItem(index));
    }

    private void rightTransfer(TFNode node) {

    }

    private void leftFusion(TFNode node) {

    }

    private void rightFusion(TFNode node) {

    }

    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        Integer myInt1 = new Integer(47);
        myTree.insertElement(myInt1, myInt1);
        myTree.printTree(myTree.root(), 20);

        Integer myInt2 = new Integer(83);
        myTree.insertElement(myInt2, myInt2);
        myTree.printTree(myTree.root(), 20);

        Integer myInt3 = new Integer(22);
        myTree.insertElement(myInt3, myInt3);
        myTree.printTree(myTree.root(), 20);

        Integer myInt4 = new Integer(16);
        myTree.insertElement(myInt4, myInt4);
        myTree.printTree(myTree.root(), 20);

        Integer myInt5 = new Integer(49);
        myTree.insertElement(myInt5, myInt5);
        myTree.printTree(myTree.root(), 20);

        Integer myInt6 = new Integer(100);
        myTree.insertElement(myInt6, myInt6);
        myTree.printTree(myTree.root(), 20);

        Integer myInt7 = new Integer(38);
        myTree.insertElement(myInt7, myInt7);
        myTree.printTree(myTree.root(), 20);

        Integer myInt8 = new Integer(3);
        myTree.insertElement(myInt8, myInt8);
        myTree.printTree(myTree.root(), 20);

        Integer myInt9 = new Integer(53);
        myTree.insertElement(myInt9, myInt9);

        Integer myInt10 = new Integer(66);
        myTree.insertElement(myInt10, myInt10);

        Integer myInt11 = new Integer(19);
        myTree.insertElement(myInt11, myInt11);

        Integer myInt12 = new Integer(23);
        myTree.insertElement(myInt12, myInt12);
        //myTree.printTree(myTree.root(), 20);

        Integer myInt13 = new Integer(24);
        myTree.insertElement(myInt13, myInt13);

        Integer myInt14 = new Integer(88);
        myTree.insertElement(myInt14, myInt14);
        myTree.printTree(myTree.root(), 20);

        Integer myInt15 = new Integer(1);
        myTree.insertElement(myInt15, myInt15);
        myTree.printTree(myTree.root(), 20);

        Integer myInt16 = new Integer(97);
        myTree.insertElement(myInt16, myInt16);
        myTree.printTree(myTree.root(), 20);

        Integer myInt17 = new Integer(94);
        myTree.insertElement(myInt17, myInt17);
        myTree.printTree(myTree.root(), 20);

        Integer myInt18 = new Integer(35);
        myTree.insertElement(myInt18, myInt18);
        myTree.printTree(myTree.root(), 20);

        Integer myInt19 = new Integer(51);
        myTree.insertElement(myInt19, myInt19);

        myTree.printAllElements();
        myTree.checkTree();

        int test1 = (int) myTree.findElement(66);
        int test2 = (int) myTree.findElement(1);
        System.out.println("done");

        myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;

        for (int i = 0; i < TEST_SIZE; i++) {
            myTree.insertElement(new Integer(i), new Integer(i));
            //          myTree.printAllElements();
            //         myTree.checkTree();
        }
        System.out.println("removing");
        for (int i = 0; i < TEST_SIZE; i++) {
            int out = (Integer) myTree.removeElement(new Integer(i));
            if (out != i) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
            if (i > TEST_SIZE - 15) {
                myTree.printAllElements();
            }
        }
        System.out.println("done");
    }

    public void printAllElements() {
        int indent = 0;
        if (root() == null) {
            System.out.println("The tree is empty");
        } else {
            printTree(root(), indent);
        }
    }

    public void printTree(TFNode start, int indent) {
        if (start == null) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        printTFNode(start);
        indent += 4;
        int numChildren = start.getNumItems() + 1;
        for (int i = 0; i < numChildren; i++) {
            printTree(start.getChild(i), indent);
        }
        System.out.println();
    }

    public void printTFNode(TFNode node) {
        int numItems = node.getNumItems();
        for (int i = 0; i < numItems; i++) {
            System.out.print(((Item) node.getItem(i)).element() + " ");
        }
        System.out.println();
    }

    // checks if tree is properly hooked up, i.e., children point to parents
    public void checkTree() {
        checkTreeFromNode(treeRoot);
    }

    private void checkTreeFromNode(TFNode start) {
        if (start == null) {
            return;
        }

        if (start.getParent() != null) {
            TFNode parent = start.getParent();
            int childIndex = 0;
            for (childIndex = 0; childIndex <= parent.getNumItems(); childIndex++) {
                if (parent.getChild(childIndex) == start) {
                    break;
                }
            }
            // if child wasn't found, print problem
            if (childIndex > parent.getNumItems()) {
                System.out.println("Child to parent confusion");
                printTFNode(start);
            }
        }

        if (start.getChild(0) != null) {
            for (int childIndex = 0; childIndex <= start.getNumItems(); childIndex++) {
                if (start.getChild(childIndex) == null) {
                    System.out.println("Mixed null and non-null children");
                    printTFNode(start);
                } else {
                    if (start.getChild(childIndex).getParent() != start) {
                        System.out.println("Parent to child confusion");
                        printTFNode(start);
                    }
                    for (int i = childIndex - 1; i >= 0; i--) {
                        if (start.getChild(i) == start.getChild(childIndex)) {
                            System.out.println("Duplicate children of node");
                            printTFNode(start);
                        }
                    }
                }

            }
        }

        int numChildren = start.getNumItems() + 1;
        for (int childIndex = 0; childIndex < numChildren; childIndex++) {
            checkTreeFromNode(start.getChild(childIndex));
        }

    }

}
