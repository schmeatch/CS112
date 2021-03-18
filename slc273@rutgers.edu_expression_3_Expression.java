package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	System.out.println(vars+"VAR | ARR"+arrays); //vars and arrays are already created but empty///// can delete after code done
    	
    	String exprNew = expr.trim(), currentItem;
    	
    	String delims2 = " \t*+-/()"; 
    	StringTokenizer st = new StringTokenizer(exprNew,delims2);
    	
    	while(st.hasMoreTokens()){
    		currentItem = st.nextToken();
    		
    		 if ( currentItem.contains("[") ){
	 			 	boolean duplicateA = false;
	 			 	String arrName = "";
	 				 
	 			 	for (int j = 0; j < currentItem.length(); j ++) {
	 					 
	 			 		if (currentItem.charAt(j)== '[') {
	 						
	 			 			if (arrays.size() == 0) {
	 								arrays.add( new Array(arrName) );
	 								arrName ="";
	 						
	 			 			}else if (arrays.size() != 0) {
	 							for (int k = 0; k < arrays.size(); k++) {
	 								if (arrays.get(k).name.equals(arrName) ) {
	 									arrName ="";
	 									duplicateA = true;
	 								}
	 							}
	 							if (duplicateA == false) {
	 								arrays.add( new Array(arrName) );
	 								arrName ="";
	 							}
	 						}
	 					
	 			 		}else if (Character.toString(currentItem.charAt(j)).matches("[a-zA-Z]*") ){
	 			 			arrName += currentItem.charAt(j);
	 			 		}
	 				 }
	 			 	//after looping if arrName isn't empty, that is a variable: add to variables if it is not already in
	 			 	if (arrName != "") {
	 			 		boolean duplicateV = false;
	 			 		 for (int k = 0; k < vars.size(); k++) {
	    						if (vars.get(k).name.contentEquals(arrName) ) {
	    							duplicateV = true;
	    						}
	    				} 
	    				 if(duplicateV == false) {
	 						vars.add(new Variable(arrName));
	 					}
	 			 	}
 				 
    		 }else if ( currentItem.matches("[a-zA-Z\\]]*") ) {
    			 if (currentItem.contains("]")){
    				 currentItem = currentItem.replace("]", "");
    			 }
    			
    			 //set duplicate boolean to false each time a possible variable is found
    			 boolean duplicateV = false;
    			 
    			 //if variablelist is empty, need not worry about duplicates
    			 if (vars.size() == 0) {
    				 vars.add( new Variable(currentItem) );
    				 
    			// if variablelist has items, check each of them with current possible variable
    				 // if no matches found, add current p. var into list
    			 }else{
    				 for (int k = 0; k < vars.size(); k++) {
    						if (vars.get(k).name.contentEquals(currentItem) ) {
    							duplicateV = true;
    						}
    				} 
    				 if(duplicateV == false) {
 						vars.add(new Variable(currentItem));
 					}
    			 }
    		 }
    				 
    	}
		
    	System.out.println(vars+"VAR | ARR"+arrays); // vars and arrays should now be filled/////
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	double temp = 0;
    	
    	///build arrList////////////////////////////////////////
    	ArrayList<String> exprArrL = buildArrayListfromString(expr,vars);
        	
    	///replace strings w bracket ---> numbers///////////////
    	for (int i = 0; i < exprArrL.size() ; i++) {
    		if (exprArrL.get(i).contains("[")) {
    			temp = convertStrInArray(exprArrL.get(i), arrays, vars);
    			exprArrL.set(i,Double.toString(temp));
    		}
    	}
    	
    	///can now evaluate/////////////////////////////////////
    	return (float)evaluateSAL(exprArrL, arrays, vars) ;
    	
    }
    	
    //createArrayListfromString self explanatory/////////////////////////////////////////////////////////////////////////////////////////////////////
    private static ArrayList<String> buildArrayListfromString(String expr, ArrayList<Variable> vars) {
    	ArrayList<String> exprArrL = new ArrayList<>(); 
    	String currentItem = "";
    	int bracketcount = 0;
    	char currC;
    	
    	for (int i = 0; i < expr.length(); i++) {
    		
    		currC = expr.charAt(i);
    		
    		if (currC == '[') {
    			bracketcount++;
    			currentItem += currC;
    			
    		}else if (currC == ']') {
    			bracketcount--;
    			currentItem += expr.charAt(i);
    			if (bracketcount == 0) {
    				exprArrL.add(currentItem);
    				currentItem = "";
    			}
    			
    		}else if ( (currC == '+' || currC == '-' || currC == '/' || currC == '*' || currC == '(' || currC == ')') && bracketcount == 0){
    			
    			if (!currentItem.equals("")) {
    				exprArrL.add(currentItem);
    				currentItem = "";
    			}	
    			currentItem += expr.charAt(i);
    			exprArrL.add(currentItem);
    			currentItem = "";
    			
    		}else if ( (currC == '+' || currC == '-' || currC == '/' || currC == '*' || currC == '(' || currC == ')') && bracketcount > 0){
    			currentItem += expr.charAt(i);
    			
    		}else if(!(currC == '+' || currC == '-' || currC == '/' || currC == '*' || currC == '(' || currC == ')') ){
    			currentItem += currC;
    		}
    	}
    	if (!currentItem.equals("")) {
			exprArrL.add(currentItem);
			currentItem = "";
		}
    	
    	//arraylist built change variables into respective values
    	for (int i = 0; i < exprArrL.size(); i++) {
    		if (exprArrL.get(i).contains("[a-zA-Z]*") && !exprArrL.get(i).contains("[//]//[]*")) {
    			for (int j = 0; j < vars.size() ; j++) {
    				if(vars.get(j).equals(exprArrL.get(i))) {
    					exprArrL.set(i,Integer.toString(vars.get(j).value));
    				}
    			}
    		}
    	}
    		
    	
    	return exprArrL ;
    }
    
    //push number or value of number into Stack for evaluateSAL//////////////////////////////////////////////////////////////////////////////////////
    private static void pushNum(String currentItem, ArrayList<Variable> vars, Stack<Double> numbers ) {
    	
		//variable -push num value
		if (currentItem.matches("^[a-zA-Z]*$")){
			for(int v = 0; v < vars.size() ; v ++) {
				if (vars.get(v).name.contentEquals( currentItem ) ) {
					numbers.push((double)vars.get(v).value);
				}
			}
	
		//is a number - push num value
		}else{
			numbers.push(Double.parseDouble(currentItem));
		}
    }
    
    
    //converts a string with arrname + brackets into a single number//**///////////////////////////////////////////////////////////////////////////////
    private static double convertStrInArray(String exprPiece, ArrayList<Array> arrays, ArrayList<Variable> vars ) {
    	
    	String firstHalf, middle, secondHalf;
    	
    	while (exprPiece.contains("]") ) {
	     	
	     	String arrName = "";
	     	int arrNameStartIndex = 0;
	     	int arrNameEndIndex;
	     	
	     	String strInBracket = "";
	     	
	     	int startOfStr = 0; ////////// substring of (0,1) (how substring works) - returns whatever is at zero and not at one
	     	int endOfStr = 0;
	     	   	
	    	//find last [
	      	for (int s = 0; s < exprPiece.length(); s++) {
	    		if (exprPiece.charAt(s) == '[') {
	    			startOfStr = s+1;
	    		}
	    	}
	    	//find first ] after last [
	    	for (int s = startOfStr; s < exprPiece.length(); s++) {
	    		if (exprPiece.charAt(s) == ']') {
	    				endOfStr = s;
	    				break;
	    			}
	    	}
	    	
	    	//brackets are found, find respective name of Array
	    	arrNameEndIndex = startOfStr-1;				//start of str is first char after bracket //bracket should be one less
	    	for (int i = startOfStr -2; i > 0 ;i--) {
	    		if (Character.isLetter(exprPiece.charAt(i))) {
	    			arrNameStartIndex = i;
	    		}else {
	    			break;
	    		}
	    		
	    	}
	    	arrName = exprPiece.substring(arrNameStartIndex,arrNameEndIndex);
	    	
	    	//string inside bracket is now found 
	    	strInBracket = exprPiece.substring(startOfStr,endOfStr);
	    	
	    	//call create exprArrlist on it and then evaluate the created string array list
	    	ArrayList<String> ArrLfromStr =  buildArrayListfromString(strInBracket,vars); 
	    	double numResult =  evaluateSAL(ArrLfromStr, arrays, vars);		//this is the number in the bracket
	    	
	    	//evaluate number in bracket with convertArray
	    	numResult = convertArray(arrName,numResult, arrays);
	    	
	    	
	    	//replace evaluated string with number in exprPiece
	    	
	    	firstHalf = exprPiece.substring(0,arrNameStartIndex);
	    	middle = Double.toString(numResult);
	    	if(!(endOfStr+2 > exprPiece.length()) ) {
	    		secondHalf = exprPiece.substring(endOfStr+1,exprPiece.length());
	    	}else {
	    		secondHalf = "";
	    	}
	    	
	    	
	    	exprPiece = firstHalf + middle + secondHalf;
	    	
	    	
    	}
    	//exprPiece should now be a single number
    	return Double.parseDouble(exprPiece);
    }
    
    //private function for convertStrInArray
    	 private static double convertArray(String inputArrayName, double numInBracket, ArrayList<Array> arrays) {
	    	// take in something like "b[0]" 
    		 //number in bracket already evaluated to a single number
    		double ans = 0; 
    		 
    		 for (int a = 0; a < arrays.size() ; a ++) {
 	    		if (arrays.get(a).name.equals(inputArrayName) ){
 	    					ans = arrays.get(a).values[(int)numInBracket];	
 	    		}
 	    	}
    	return ans;
    }
    
    
   
    
    
    //input already created ArrayList of Strings ex)||| a + b - (A[0]) ||| and evaluates it returning an int
    // evaluateStringArrayList
    //**////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static double evaluateSAL(ArrayList<String> exprArrL, ArrayList<Array> arrays, ArrayList<Variable> vars) {
      	Stack<String> operators = new Stack<String>();
    	Stack<Double> numbers = new Stack<Double>();
    	String currentItem = "";
    	
    	
    	
	    	for (int x = 0; x < exprArrL.size(); x++) {
					
				currentItem = exprArrL.get(x);
				
				
				if ( currentItem.equals("(") ) {
				operators.push(currentItem);
               
				}else if (currentItem.equals(")")) { 
		              if (!operators.peek().equals("(") && numbers.size() >= 2 ) {
		                 numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop())); 
		                 operators.pop(); 
		               }
		               
		              //array needs evaluation before push
				}else if ( currentItem.contains("[") ){
					//currentItem = Integer.toString(convertArray(currentItem, arrays, vars)) ;
					pushNum(currentItem,vars,numbers);
					
					// push operator into ops stack
				}else if (currentItem.equals( "+") || currentItem.equals( "-" )|| currentItem.equals( "*" )|| currentItem.equals( "/")) { 
		               // While top of 'ops' has same or greater precedence to current 
		               // token, which is an operator. Apply operator on top of 'ops' 
		               // to top two elements in values stack 
		               while (!operators.isEmpty() && hasPrecedence(currentItem, operators.peek())) 
		            	   numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop())); 
		 
		               // Push current token to 'ops'. 
		               operators.push(currentItem); 
				
		               //variable or num, can be pushed in 
				}else {
					pushNum(currentItem,vars,numbers);
					
				}
				
			}
	    	while (!operators.isEmpty()) {
	    		if( operators.peek().equals("(")) {
	    			operators.pop();
	    		}else {
	    			numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop())); 
	    		}
	    	}
	  
	        // Top of 'values' contains result, return it 
	        return numbers.pop(); 
    }
    
    // Apply operation used in evaluateSAL ////////////////////////////////////////////////////////////
    private static double applyOperation(String op, double  b, double a) { 
    	
        switch (op) 
        { 
        case "+": 
            return a + b; 
        case "-": 
            return a - b; 
        case "*": 
            return a * b; 
        case "/": 
            if (b == 0) 
                throw new
                UnsupportedOperationException("Cannot divide by zero"); 
            return a / b; 
        } 
        return 0; 
    } 
    
    // Precedence checker used in evaluateSAL ///////////////////////////////////////////////////////////
    private static boolean hasPrecedence(String op1, String op2) { 
        if (op2.equals("(") || op2.equals( ")")) {
        	   return false; 
        }
        if ((op1.equals( "*") || op1.equals( "/")) && (op2.equals( "+" )|| op2.equals( "-")) ) {
        	 return false; 
        }else {
        	 return true; 
        }
           
    }
}
