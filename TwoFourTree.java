package TermProjectSP2016;

import java.util.Random;

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
        TFNode temp = this.find(key);
        if (temp == null) {
            return null;
        }
        int index = this.findFirstGreaterThanOrEqual(temp, key);
        return temp.getItem(index).element();
    }

    /**
     * Searches tree and returns TFNode
     *
     * @param key to be searched for
     * @return TFNode corresponding to key; null if not found
     */
    public TFNode find(Object key) {
        TFNode temp = this.treeRoot;
        int index = this.findFirstGreaterThanOrEqual(temp, key);
        if (index < temp.getNumItems()) {
            if (treeComp.isEqual(temp.getItem(index).key(), key)) {
                return temp;
            }
        }
        while (temp.getChild(index) != null) {
            temp = temp.getChild(index);
            index = this.findFirstGreaterThanOrEqual(temp, key);
            if (index < temp.getNumItems()) {
                if (treeComp.isEqual(temp.getItem(index).key(), key)) {
                    return temp;
                }
            }

        }
        return null;
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
        Item holder = new Item();
        TFNode top = this.find(key);
        TFNode bottom = new TFNode();
        if (top == null) {
            top = this.treeRoot;
        }
        int topIndex = this.findFirstGreaterThanOrEqual(top, key);
        if (top.getChild(topIndex) == null) {
            holder = top.removeItem(topIndex);
            bottom = top;
        } else {
            bottom = this.inorderSuccessor(top, key);
            holder = top.getItem(topIndex);
            top.addItem(topIndex, bottom.getItem(0));
            bottom.addItem(0, holder);
            holder = bottom.removeItem(0);
        }
        if (bottom.getNumItems() == 0 && bottom != this.treeRoot) {
            underflow(bottom);
        }

        return holder.element();
    }

    /**
     * Finds in-order successor of a given node
     *
     * @param node TFNode that you want the in-order successor of
     * @param key Key within that node that you want the in-order successor of
     * @return TFNode that is the in-order successor
     */
    private TFNode inorderSuccessor(TFNode top, Object key) {
        int index = this.findFirstGreaterThanOrEqual(top, key);
        TFNode bottom = top.getChild(index + 1);

        while (bottom.getChild(0) != null) {
            bottom = bottom.getChild(0);
        }
        return bottom;
    }

    /**
     * Finds within the passed node, the index of what item is greater than or
     * equal to the key value passed
     *
     * @param node The node to be searched for an item
     * @param key The value you wish to find an index for
     * @return The integer value of what index the value, or a number greater
     * than it, is at
     */
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

    /**
     * Returns an integer value of what child a node is of its parent
     *
     * @param node The node you wish to know a child value for
     * @return An integer value representing what child it is
     */
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

    /**
     * Overflow takes a node that has too many values, and restructures the tree
     * in a proper manner
     *
     * @param node
     */
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
                if (leftChild.getChild(i) != null) {
                    leftChild.getChild(i).setParent(leftChild);
                }
            }
            for (int i = middle + 1; i <= node.getMaxItems() + 1; i++) {
                rightChild.setChild(i - (middle + 1), node.getChild(i));
                if (rightChild.getChild(i - (middle + 1)) != null) {
                    rightChild.getChild(i - (middle + 1)).setParent(rightChild);
                }
            }
            leftChild.setParent(parent);
            rightChild.setParent(parent);
            if (parent.getNumItems() > parent.getMaxItems()) {
                this.overflow(parent);
            }
        }

    }

    /**
     * Root overflow is a special case of the overflow method
     */
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

    /**
     * Underflow is a method to properly re-organize a tree when a node has no
     * items
     *
     * @param node
     */
    private void underflow(TFNode node) {
        int index = this.whatChildisThis(node);

        if ((index != 0) && (node.getParent().getChild(index - 1).getNumItems() > 1)) {
            this.leftTransfer(node);
        } else if ((index != node.getParent().getNumItems()) && (node.getParent().getChild(index + 1).getNumItems() > 1)) {
            this.rightTransfer(node);
        } //        else if ((index != node.getParent().getNumItems())) {
        //            this.rightFusion(node); } 
        else if (index != 0) {
            this.leftFusion(node);
        } else {
            this.rightFusion(node);
        }
    }

    private void rightTransfer(TFNode node) {
        int index = this.whatChildisThis(node);
        TFNode parent = node.getParent();
        TFNode sibling = parent.getChild(index + 1);

        node.setChild(1, sibling.getChild(0));
        if (node.getChild(1) != null) {
            node.getChild(1).setParent(node);
        }
        Item temp = sibling.removeItem(0);
        parent.insertItem(index + 1, temp);
        parent.setChild(index + 1, node);
        node.addItem(0, parent.removeItem(index));
    }

    private void leftTransfer(TFNode node) {
        int index = this.whatChildisThis(node);
        TFNode parent = node.getParent();
        TFNode sibling = parent.getChild(index - 1);

        // Do we need -1 here?
        node.setChild(1, node.getChild(0));
        node.setChild(0, sibling.getChild(sibling.getNumItems()));
        if (node.getChild(0) != null) {
            node.getChild(0).setParent(node);
        }
        if (node.getChild(1) != null) {
            node.getChild(1).setParent(node);
        }
        TFNode child = sibling.getChild(sibling.getNumItems() - 1);
        Item temp = sibling.removeItem(sibling.getNumItems() - 1);
        sibling.setChild(sibling.getNumItems(), child);
        if (sibling.getChild(sibling.getNumItems())!= null){
            sibling.getChild(sibling.getNumItems()).setParent(sibling);
        }
        node.addItem(0, parent.getItem(index - 1));
        parent.addItem(index - 1, temp);
//        parent.setChild(index - 1, node);
    }

    private void rightFusion(TFNode node) {
        TFNode parent = node.getParent();
        TFNode sibling = parent.getChild(this.whatChildisThis(node) + 1);

        node.addItem(0, parent.getItem(this.whatChildisThis(node)));
        node.addItem(1, sibling.getItem(0));
        parent.setChild(this.whatChildisThis(node) + 1, node);
        parent.removeItem(this.whatChildisThis(node));
        node.setChild(1, sibling.getChild(0));
        if (node.getChild(1) != null) {
            node.getChild(1).setParent(node);
        }
        node.setChild(2, sibling.getChild(1));
        if (node.getChild(2) != null) {
            node.getChild(2).setParent(node);
        }
        if (parent.getNumItems() == 0) {
            if (parent == this.treeRoot) {
                this.setRoot(node);
                node.setParent(null);
                if (node.getChild(0) != null) {
                    node.getChild(0).setParent(node);
                }
                if (node.getChild(1) != null) {
                    node.getChild(1).setParent(node);
                }
                return;
            } else {
                this.underflow(parent);
            }
        }
    }

    private void leftFusion(TFNode node) {
        TFNode parent = node.getParent();
        TFNode sibling = parent.getChild(this.whatChildisThis(node) - 1);
        node.addItem(0, sibling.getItem(0));
        node.addItem(1, parent.getItem(this.whatChildisThis(node) - 1));
        parent.removeItem(this.whatChildisThis(node) - 1);
        node.setChild(2, node.getChild(0));
        if (node.getChild(2) != null){
            node.getChild(2).setParent(node);
        }
        node.setChild(1, sibling.getChild(1));
        if (node.getChild(1) != null){
            node.getChild(1).setParent(node);
        }
        node.setChild(0, sibling.getChild(0));
        if (node.getChild(0) != null){
            node.getChild(0).setParent(node);
        }
        if (parent.getNumItems() == 0) {
            if (parent == this.treeRoot) {
                this.setRoot(node);;
                node.setParent(null);
                if (node.getChild(0) != null) {
                    node.getChild(0).setParent(node);
                }
                if (node.getChild(1) != null) {
                    node.getChild(1).setParent(node);
                }
            } else {
                this.underflow(parent);
            }
        }
    }

    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        System.out.println("done");

        myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;

        Integer[] numbers = new Integer[TEST_SIZE];
        Random random = new Random();
        int temp;
        for (int i = 0; i < TEST_SIZE; i++) {
            //        System.out.println(i);
            temp = random.nextInt(TEST_SIZE);
            numbers[i] = temp;
            myTree.insertElement(temp, temp);
        }

        myTree.printAllElements();
        System.out.println("removing");
        for (int i = 0; i < TEST_SIZE; i++) {
            System.out.println();
            System.out.println("removing " + numbers[i]);
            int out = (Integer) myTree.removeElement(numbers[i]);
            if (out != numbers[i]) {
                throw new TwoFourTreeException("Wrong element output" + "\nExpected: " + numbers[i] + "\nOutput: " + out);
            }
            if (i > TEST_SIZE - 25) {
                myTree.printAllElements();
            }
            myTree.checkTree();
        }
        myTree.printAllElements();
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
        //this.printTree(treeRoot, 20);
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
