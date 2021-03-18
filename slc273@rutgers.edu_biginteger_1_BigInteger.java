package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;
	
	/**
	 * Number of digits in this integer
	 */
	int numDigits;
	
	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 *    
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 *    5 --> 3 --> 2  (No zeros after the last 2)        
	 */
	 DigitNode front;
	
	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}
	
	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * Leading and trailing spaces are ignored. So "  +123  " will still parse 
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12  345" will not parse as
	 * an integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the BigInteger
	 * constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) 
					throws IllegalArgumentException {
		
		/* IMPLEMENT THIS METHOD */
		
		// initialize bigInt LL to return
		BigInteger bigInt = new BigInteger();
		
		// trim input integer of leading/trailing spaces if they exist
		String integerStr = integer.trim();
		
		if(integerStr.equals("")) {
			throw new IllegalArgumentException("please enter valid input");
		}
		//assigns sign, stores whether or not sign is explicitly stated
		// numDigits is number of numbers in string
		boolean signGiven;
		int numDigits;
			
		if (integerStr.charAt(0) == '-') {
			bigInt.negative = true;
			numDigits = integerStr.length() - 1;
			signGiven = true; 
					
		}else if (integerStr.charAt(0) == '+') {
			bigInt.negative = false;
			numDigits = integerStr.length() - 1;
			signGiven = true; 
		}else {
			bigInt.negative = false;
			numDigits = integerStr.length();
			signGiven = false;
		}
		

		//Checking for errors...
				// empty input string
		if((integerStr.length() == 0) || (integerStr == "")) {
			System.out.println("empty");
			return bigInt ;
		}
				// just a sign without numbers
		if (signGiven && integerStr.length() == 1) {
			throw new IllegalArgumentException("please enter valid input");
		}
				//use of illegal characters(letters,symbols,spaces) 
					//if sign is given start at the input, skip over it;
		
		int i = 0;
		if (signGiven) i ++;
		if (integerStr.substring(i).matches("\\d+") == false) {
			throw new IllegalArgumentException("please enter valid input: remove invalid symbols");
		}
		
				//check if input is all zeros
					// return zero case
		int check = 0;
		for (int j = i; j < integerStr.length();j++ ) {
			if (integerStr.charAt(j)!= '0') check++;
		}
		if (check == 0) {
			bigInt.negative = false;
			bigInt.numDigits = 0;
			bigInt.front = null;
			return bigInt;
			
		}
				//check for leading zeros
		int firstNonZero = 0 , zeroCount = 0;
		for (int k = 0; k<integerStr.length(); k ++) {
			if ((integerStr.charAt(k) == '+')||(integerStr.charAt(k) == '-')) {
				continue;
			}else if ((integerStr.charAt(k) == '0')){
				zeroCount ++;
			}else {
			
				firstNonZero = k;
				break;
			}
		}
					// delete zeroCount from numDigits
		bigInt.numDigits = numDigits - zeroCount;
					// delete leading zeros and sign from integer input
		integerStr = integerStr.substring(firstNonZero);
		 
		// input now cleaned and prepared - get to parsing
			// zero case already accounted for
			// 235 : 5 -->3 -->2
		
		bigInt.front = new DigitNode(Integer.parseInt(integerStr.substring(bigInt.numDigits-1)), null);
		
		DigitNode ptr = bigInt.front;
		
		for(int k = bigInt.numDigits -2 ; k >= 0 ; k--) {
			ptr.next = new DigitNode(Integer.parseInt(integerStr.substring(k,k+1)),null);
			ptr = ptr.next;
		}
		
		return bigInt;
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * Adds the first and second big integers, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY the input big integers.
	 * 
	 * NOTE that either or both of the input big integers could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return Result big integer
	 */
	public static BigInteger add(BigInteger first, BigInteger second) {
		
		/* IMPLEMENT THIS METHOD */
		
		
		//initialize new big integer LL to return after adding
		// and pointers, integers
		BigInteger bigInt = new BigInteger(), zero = new BigInteger();
		int firstDigits = first.numDigits, secondDigits = second.numDigits;
		zero.front = new DigitNode(0,null);
		DigitNode big = first.front, small = second.front, ptr;
		boolean bigNeg = first.negative, smallNeg = second.negative;
		int add = 0, carryOver = 0, timesToAdd = 0;
		
	
		//determine if output should be negative
		//if mismatched lengths : timesToadd should be the longer LL
		if (firstDigits > secondDigits) {						//first > second
			
			//store values and negatives
			big = first.front;
			bigNeg = first.negative;
			small = second.front;
			smallNeg = second.negative;
			timesToAdd = first.numDigits;
			
		}else if (firstDigits < secondDigits){					//second > first
			
			//store values and negatives
			big = second.front;
			bigNeg = second.negative;
			small = first.front;
			smallNeg = first.negative;
			timesToAdd = second.numDigits;
		
		}else if (firstDigits == secondDigits) {				// same length
			
			timesToAdd = firstDigits;
			DigitNode ptrC1 = big;
			DigitNode ptrC2 = small;
			boolean found = false;
			
			for (int i = 0; i < firstDigits ; i ++)   {
				int j = firstDigits - 1 - i;
				ptrC1 = big;
				ptrC2 = small;
				
				//sets pointer to highest value place --> lowest value place
				while(j>0) {
					ptrC1 = ptrC1.next;
					ptrC2 = ptrC2.next;
					j--;
				}
				if (ptrC1.digit > ptrC2.digit) {
					big = first.front;
					bigNeg = first.negative;
					small = second.front;
					smallNeg = second.negative;
					found = true;
					break;
					
				}else if (ptrC1.digit < ptrC2.digit) {
					
					big = second.front;
					bigNeg = second.negative;
					small = first.front;
					smallNeg = first.negative;
					found = true;
				
					break;
	
				}else {
					continue;
				}
			}
			//goes through all conditionals - if both numbers are exactly the same
				// if one negative and one positive - return zero
				// if not previous case - continue to adding (*2)
			if (((first.negative && !second.negative)||(!first.negative && second.negative))&& !found) {
				bigInt.negative = false;
				bigInt.numDigits = 0;
				bigInt.front = null;
				return bigInt;
			}
		}
		
		//if zero LL, front node will be empty(previous check will not work)
		if((first.front == null) && (second.front ==null)) {
			bigInt.negative = false;
			bigInt.numDigits = 0;
			bigInt.front = null;
			return bigInt;
			
		}else if (second.front == null) {
			big = first.front;
			bigNeg = first.negative;
			small = zero.front;
			smallNeg = false;
			secondDigits ++;
			timesToAdd = first.numDigits;
			
		}else if (first.front == null) {
			big = second.front;
			bigNeg = second.negative;
			small = zero.front;
			smallNeg = false;
			firstDigits ++;
			timesToAdd = second.numDigits;
		}
		
		// determine output sign
		if ( (bigNeg && !smallNeg) || (bigNeg  && smallNeg ) ) {
			bigInt.negative = true;
		}else {
			bigInt.negative = false;
		}
		
		// adding
		// add ones place //create front 
		if (timesToAdd > 0) {
			if((!bigNeg && smallNeg)||(bigNeg && !smallNeg))  {
				add = big.digit - small.digit;
			}else {
				add = big.digit + small.digit;
			}
			if (add >= 10) {
				add -= 10;
				carryOver ++;
			}else if (add < 0) {
				add +=10;
				carryOver --;
			}
			bigInt.front = new DigitNode(add, null);
			timesToAdd --;
			bigInt.numDigits ++;
		}
			
		//create pointer for front
		DigitNode potr =  bigInt.front;
		
		//add other places
		while(timesToAdd > 0) {
			
			// check if going to next place over is possible - if not just add by zero
			///if (firstDigits != secondDigits) {
			if (big.next != null) {
				big = big.next;
			}else {
				big = new DigitNode(0,null);
			}
			if (small.next != null) {
				small = small.next;
			}else {
				small = new DigitNode(0,null);
			}
			//}
			
			// add or subtract based on signs given
			if((!bigNeg && smallNeg)||(bigNeg && !smallNeg))  {
				add = big.digit - small.digit + carryOver;
			}else {
				add = big.digit + small.digit + carryOver;
			}
			
			// edit carry value based on sum of add
			if (add >= 10) {
				add -= 10;
				carryOver = 1;
			}else if (add < 0) {
				add +=10;
				carryOver = -1;
			}else {
				carryOver = 0;
			}
			
			// assign value to bigInt and step over
			potr.next = new DigitNode(add, null);
			potr = potr.next;
			timesToAdd --;
			bigInt.numDigits ++;
		}
		// adding case if carrOver still holds a value after adding - (ex 50 + 50 = 100)
		if (carryOver > 0) {
			potr.next = new DigitNode(1, null);
			bigInt.numDigits ++;
		} 
	
		// delete leading zeros
		int numOfChecks = bigInt.numDigits;
		for (int i = 1; i <= numOfChecks ; i ++) {
			small = bigInt.front;
			ptr = bigInt.front;
			ptr = ptr.next;
			
			int j = numOfChecks - i -1;
			while(j > 0) {
				ptr = ptr.next;
				small = small.next;
				j--;
			}
			if (small.next == null) {
				return bigInt;
			}else if(ptr.digit == 0){
				small.next = null;
				bigInt.numDigits --;

			}else {
				return bigInt;
			}
		}
		
		//return biginteger now that its all done		
		return bigInt;
	}
	
	/**
	 * Returns the BigInteger obtained by multiplying the first big integer
	 * with the second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {
		
		/* IMPLEMENT THIS METHOD */
		
		BigInteger bigInt = new BigInteger(), temp = new BigInteger();
		DigitNode ptr1 = first.front, ptr2 = second.front, ptr;
		int multiply, carryOver = 0, secondDigits = second.numDigits, timesToMultiply = first.numDigits;
		
		//if either number is zero return zero
		if ((first.front ==null)||(second.front ==null)){
			bigInt.negative = false;
			bigInt.numDigits = 0;
			bigInt.front = null;
			return bigInt;
		}	
			
		//do first multiplier's ones place
		multiply = ptr1.digit * ptr2.digit;
		if (multiply >= 10) {
			carryOver = (multiply - multiply % 10)/10 ;
			multiply = multiply % 10 ;
		}
		bigInt.front = new DigitNode(multiply, null);
		bigInt.numDigits ++;
		timesToMultiply --;
		
		ptr = bigInt.front;
		
		
		//continue first multiplier - go through first number and multiply all digits by second number ones place ex) 123*1
		while(timesToMultiply > 0) {
			ptr1 = ptr1.next;
			multiply = ptr1.digit * ptr2.digit + carryOver;
			if (multiply >= 10) {
				carryOver = (multiply - multiply % 10)/10 ;
				multiply = multiply % 10 ;
			}else {
				carryOver = 0;
			}
			ptr.next = new DigitNode(multiply, null);
			bigInt.numDigits ++;
			ptr = ptr.next;
			timesToMultiply --;
		}
		if (carryOver>0) {
			ptr.next = new DigitNode(carryOver, null);
			bigInt.numDigits ++;
		}
		
		
		// if second input has more than one digit continue to multiply by other numbers in second multiplicant
		
		if (secondDigits > 1) {
			timesToMultiply = first.numDigits;
			int zerosToAdd = 0;
			
			for (int timestoAddM = 0 ; timestoAddM < secondDigits-1; timestoAddM++) {
				timesToMultiply = first.numDigits;
				temp.numDigits = 0;
				ptr1 = first.front;
				ptr2 = ptr2.next;
				ptr = temp.front;
				
				
				// check how many add zeros needed
				
					temp.front = new DigitNode(0,null);
					temp.numDigits ++;
					ptr = temp.front;
					for (int k = zerosToAdd ; k > 0 ; k --) {
						ptr.next = new DigitNode(0,null);
						ptr = ptr.next;
						temp.numDigits ++;
					}
				
				//do first multiplier's first nonzero item
				multiply = ptr1.digit * ptr2.digit;
				if (multiply >= 10) {
					carryOver = (multiply - multiply % 10)/10 ;
					multiply = multiply % 10 ;
				}else {
					carryOver = 0;
				}
		
				ptr.next = new DigitNode(multiply , null);
				ptr = ptr.next;
				temp.numDigits ++;
				
	
				timesToMultiply --;
				
				//continue first multiplier - go through first number and multiply all digits by second number ones place ex) 123*1
				while(timesToMultiply > 0) {
					ptr1 = ptr1.next;
					multiply = ptr1.digit * ptr2.digit + carryOver;
					if (multiply >= 10) {
						carryOver = (multiply - multiply % 10)/10 ;
						multiply = multiply % 10 ;
					}else {
						carryOver = 0;
					}
					ptr.next = new DigitNode(multiply, null);
					temp.numDigits ++;
					ptr = ptr.next;
					timesToMultiply --;
					
				}
				if (carryOver>=10) {
					carryOver = (multiply - multiply % 10)/10 ;
					multiply = carryOver % 10;
					ptr.next = new DigitNode(multiply, null);
					temp.numDigits ++;
				}
				if (carryOver>0) {
					ptr.next = new DigitNode(carryOver, null);
					temp.numDigits ++;
				}
				
				bigInt = BigInteger.add(bigInt, temp); //  bigint += temp value
				temp.front = null;   // reset temp value
				zerosToAdd ++;
				

			}// for loop secondDigits end
				
		}// end statement (if secondDigits>1)
		
		//determine sign
				if((first.negative && !second.negative)|| (!first.negative && second.negative)) {
					bigInt.negative = true;
				}else {
					bigInt.negative = false;
				}
		 
	
		return bigInt;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
				retval = curr.digit + retval;
		}
		
		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}
	
	
	
	
	
	
//		public static void main(String[] args) {
//			String test = "        -874  ";
//			String test1 = "         800 ";
//			BigInteger tst = BigInteger.parse(test);
//			BigInteger tst1 = BigInteger.parse(test1);
//			System.out.println("\t\tValue = " + tst); 
//			System.out.println("\t\tValue = " + tst1);
//			
//			BigInteger x = BigInteger.multiply(tst,tst1);
//			System.out.println("\t\tSum: " + x );
//			System.out.println("numberOfDigits: " + x.numDigits);
//			System.out.println("isNegative: "  + x.negative);
//			
//		}
		/*
		 * BigInteger bigInt = new BigInteger(); bigInt.front = new DigitNode(0,null);
		 * DigitNode ptr = bigInt.front; int xs = 4; while(xs>0) { ptr.next = new
		 * DigitNode(0,null); bigInt.numDigits ++; }
		 */
		 
			
			
			//System.out.println("front-->"  + x.front +"-->"+ x.front.next.digit  );
			
			
			// work: 1, 001, -00124, +500005000, 00005---> 5, 0000--> 0
			// error: +, =, -, ---, 1-1, a, 14A   +"-->"+ x.front.next.next.digit
			
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
