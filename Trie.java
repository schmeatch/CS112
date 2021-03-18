package trie;
 
import java.util.ArrayList;
 
/**
 * This class implements a Trie.
 *
 * @author Sesh Venugopal
 *
 */
public class Trie {
   
    // prevent instantiation
    private Trie() { }
   
    /**
     * Builds a trie by inserting all words in the input array, one at a time,
     * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
     * The words in the input array are all lower case.
     *
     * @param allWords Input array of words (lowercase) to be inserted.
     * @return Root of trie with all words inserted from the input array
     */
    public static TrieNode buildTrie(String[] allWords) {
        // create root
    	TrieNode root = new TrieNode(null, null, null);
        int index = 0;
        
        //add first child
        TrieNode firstChildtoAdd = new TrieNode(null, null, null);
        firstChildtoAdd.substr = new Indexes(0, (short)0, (short)(allWords[0].length()-1));
		root.firstChild = firstChildtoAdd;
        
        //for loop for adding all other entries
    	for (int i = 1; i < allWords.length ; i ++) {
			addWord(root, i, allWords, 0, allWords[i]);
        }
        return root;
    }
   
    private static void addWord(TrieNode root, int i, String[] allWords, int start, String word) {
        TrieNode ptr = root.firstChild;
        TrieNode lastChild = ptr;
        int prefixLength = 0;
        while(ptr != null) {
        	//w
            if(allWords[ptr.substr.wordIndex].charAt(start) == word.charAt(start)) {
                while(allWords[ptr.substr.wordIndex].charAt(start + prefixLength) == word.charAt(start + prefixLength)) {
                    //if prefix length + start  is the whole word
                	// call addWord on whole 
                	if(ptr.substr.endIndex == start + prefixLength) {
                        addWord(ptr, i, allWords, start + prefixLength + 1, word);
                        return;
                    }
                    prefixLength++;
                }
                //store end of currentword at pointer
                int endIndex = ptr.substr.endIndex;
                //edit current ptr --keep word index --keep startindex --change endindex to max(prefix length) -1 + starting index
                // child and sibling left alone
                ptr.substr = new Indexes(ptr.substr.wordIndex,(short)start, (short)(prefixLength - 1 + start));
                
                //add ptr's child  --same word index --startindex is max(prefix length) + starting index --stored endindex is endindex
                //ptr's child .firstChild should retain ptr.firstChild
                ptr.firstChild = new TrieNode( new Indexes(ptr.substr.wordIndex, (short)(prefixLength + start),(short)endIndex), ptr.firstChild, null);
                
                //add ptr's child's sibling --word index is i  --startindex is same of prev sibling --endIndex is length of current word -1
                ptr.firstChild.sibling = new TrieNode(new Indexes(i, ptr.firstChild.substr.startIndex, (short)(word.length() - 1)), null, null);
                
                //trie edited, child and sibling added
                // return break out of method/////////////////////////////////////////////////////////////////////////////
                return;
            }
            //below happens if prefix length is 0(no match between current word and item at ptr)
            //if ptr has no siblings it is the new last child
           
            if(ptr.sibling == null) {
                lastChild = ptr;
            }
            // iterate to next sibling
            ptr = ptr.sibling;
        }
      // while loop completed ptr == null
      
       lastChild.sibling = new TrieNode(new Indexes(i, (short)start, (short)(word.length()-1)), null, null);
    }
   //helper method for completion list
    private static void togetherAdd(TrieNode ptr, ArrayList<TrieNode> returnArrL) {
        while(ptr != null) {
            if(ptr.firstChild == null) {
            	returnArrL.add(ptr);
            }else {
            	togetherAdd(ptr.firstChild, returnArrL);
            }
            ptr = ptr.sibling;
        }
    }
    /**
     * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the
     * trie whose words start with this prefix.
     * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
     * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell";
     * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell",
     * and for prefix "bell", completion would be the leaf node that holds "bell".
     * (The last example shows that an input prefix can be an entire word.)
     * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
     * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
     *
     * @param root Root of Trie that stores all words to search on for completion lists
     * @param allWords Array of words that have been inserted into the trie
     * @param prefix Prefix to be completed with words in trie
     * @return List of all leaf nodes in trie that hold words that start with the prefix,
     *          order of leaf nodes does not matter.
     *         If there is no word in the tree that has this prefix, null is returned.
     */
    public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
    	 ArrayList<TrieNode> returnArrL= new ArrayList<TrieNode>();
         return doRecursion(root, allWords, prefix, returnArrL);
    }
    
    //call recursive method
    private static ArrayList<TrieNode> doRecursion(TrieNode root, String[] allWords,String prefix, ArrayList<TrieNode> returnArrL) {
        TrieNode ptr = root.firstChild;
        while(ptr != null) {
            
        	//get indexes from ptr
        	int startI = ptr.substr.startIndex;
            int endI = ptr.substr.endIndex + 1;
            String currentWord = allWords[ptr.substr.wordIndex];
            //first letters match
            if(prefix.charAt(startI) == currentWord.charAt(startI)) {
            	
                if(prefix.length() < endI + 1) {       
                	
                    if(prefix.substring(startI).equals(currentWord.substring(startI, prefix.length()))) {
                    	
                    	
                        if(ptr.firstChild == null) {
                        	//ptr is "at leaf node"
                        	//can add ptr to returnArrL
                        	returnArrL.add(ptr);
                            return returnArrL;
                        }
                        togetherAdd(ptr.firstChild, returnArrL);
                        return returnArrL;
                    }
                    else {
                    	return null;
                    }
                }
                
                else {             //if length of prefix is greater than endindex on ptr
                    if(currentWord.substring(startI, endI).equals(prefix.substring(startI, endI))) {
                        return doRecursion(ptr, allWords, prefix, returnArrL);
                    }
                    else{
                    	return null;
                    }
                }
            }
            //if first letters don't match
            //iterate through
            ptr = ptr.sibling;
        }
        return null;
    }
    
 
    
    
    
    
    
    
    
    
    
    
    public static void print(TrieNode root, String[] allWords) {
        System.out.println("\nTRIE\n");
        print(root, 1, allWords);
    }
   
    private static void print(TrieNode root, int indent, String[] words) {
        if (root == null) {
            return;
        }
        for (int i=0; i < indent-1; i++) {
            System.out.print("    ");
        }
       
        if (root.substr != null) {
            String pre = words[root.substr.wordIndex]
                            .substring(0, root.substr.endIndex+1);
            System.out.println("      " + pre);
        }
       
        for (int i=0; i < indent-1; i++) {
            System.out.print("    ");
        }
        System.out.print(" ---");
        if (root.substr == null) {
            System.out.println("root");
        } else {
            System.out.println(root.substr);
        }
       
        for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
            for (int i=0; i < indent-1; i++) {
                System.out.print("    ");
            }
            System.out.println("     |");
            print(ptr, indent+1, words);
        }
    }
    
 }
