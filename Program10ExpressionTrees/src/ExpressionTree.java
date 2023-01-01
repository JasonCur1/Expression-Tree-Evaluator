import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.MalformedInputException;
import java.util.*;

public class ExpressionTree extends ZTree {

	public Queue<String> parse(String expression) {
		Queue<String> infix = new LinkedList<>();
		StringTokenizer tokenizer = new StringTokenizer(expression, "(?<=[-+*()%/])|(?=[-+*()%/])", true);
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			if(!token.trim().isEmpty()) {
				infix.add(token.trim());
			}
		}
		return infix;
	}

	public List<String> convert(Queue<String> infix) {
		List<String> postfix = new ArrayList<>();
		Deque<String> operators = new ArrayDeque<>(); // used as a stack
		String token;

		while (infix.size() > 0) {
			token = infix.poll();
			if (!isOperator(token) && !token.equals("(") && !token.equals(")")) {
				postfix.add(token);
				continue;
			}

			if (isOperator(token)) {
				if (!operators.isEmpty() && precedence(operators.peek()) <= precedence(token)) {
					while (!operators.isEmpty() && !operators.peek().equals("(")
							&& precedence(operators.peek()) <= precedence(token)) {
						postfix.add(operators.pop());
					}
				}
				operators.push(token);
			}

			if (token.equals("(")) {
				operators.push(token);
			}

			if (token.equals(")")) {
				while (!operators.isEmpty() && !operators.peek().equals("(")) {
					assert !operators.isEmpty();

					postfix.add(operators.pop());
				}

				assert operators.peek().equals("(");
				operators.pop();

				if (!operators.isEmpty() && isOperator(operators.peek())) {
					postfix.add(operators.pop());
				}
			}

		}

		while (!operators.isEmpty()) {
			assert !operators.peek().equals("(");
			postfix.add(operators.pop());
		}

		return postfix;
	}

	@Override
	public void build(List<String> postfix) {
		Collections.reverse(postfix);
		for (String token : postfix)
			buildRecursive(root, token);
	}

	/**
	 * Builds an expression tree from the postfix representation returned from the convert method.
	 * @param current the current Node being checked
	 * @param token the token to add
	 * @return {@code true}, if successful
	 */
	public boolean buildRecursive(Node current, String token) {
		if (root == null) {
			Node temp = new Node(token);
			root = temp;
			return true;
		}

		if (current.right == null) {
			Node temp = new Node(token);
			current.right  = temp;
			return true;
		}

		if (isOperator(current.right.token)) {
			if (buildRecursive(current.right, token)) {
				return true;
			}
		}

		if (current.left == null) {
			Node temp = new Node(token);
			current.left = temp;
			return true;
		}

		if (isOperator(current.left.token)) {
			if (buildRecursive(current.left, token)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String prefix() {
		return prefixRecursive(root);
	}

	/**
	 * Concatenates the tokens in the expression tree returned from the
	 * {@link #build(List)} method in prefix order.
	 * @param current the root node
	 * @return the tokens in prefix order
	 */
	public String prefixRecursive(Node current) {
		String prefix = "";
		prefix += current.token + " ";

		if (current.left != null) {
			prefix += prefixRecursive(current.left);
		}

		if (current.right != null) {
			prefix += prefixRecursive(current.right);
		}

		return prefix;
	}

	@Override
	public String infix() {
		return infixRecursive(root);
	}

	/**
	 * Concatenates the tokens in the expression tree returned from the
	 * {@link #build(List)} method in infix order.
	 * @param current
	 * @return the tokens in infix order
	 */
	public String infixRecursive(Node current) {
		String infix = "";

		if (current.left != null) {
			infix += "(" + infixRecursive(current.left);
		}

		infix += current.token;

		if (current.right != null) {
			infix += infixRecursive(current.right) + ")";
		}

		return infix;
	}

	@Override
	public String postfix() {
		return postfixRecursive(root);
	}

	/**
	 * Concatenates the tokens in the expression tree returned from the
	 * {@link #build(List)} method in postfix order.
	 * @param current reference to the current node (starts with root)
	 * @return a String representing the tree in postfix order
	 */
	public String postfixRecursive(Node current) {
		String postfix = "";

		if (current.left != null) {
			postfix += postfixRecursive(current.left);
		}

		if (current.right != null) {
			postfix += postfixRecursive(current.right);
		}

		postfix += current.token + " ";

		return postfix;
	}

	public int evaluate() {
		return evaluateRecursive(root);
	}

	/**
	 * Traverses the expression tree and produces the correct answer, which should be an integer.
	 * @param current
	 * @return
	 */
	public int evaluateRecursive(Node current) {
		if (current == null) {
			return 0;
		}

		if (current.left == null && current.right == null) {
			return valueOf(current.token);
		}

		int left = evaluateRecursive(current.left);
		int right = evaluateRecursive(current.right);

		if (current.token.equals("+")) {
			return left + right;
		} else if (current.token.equals("-")) {
			return left - right;
		} else if (current.token.equals("*")) {
			return left * right;
		} else if (current.token.equals("%")) {
			return left % right;
		}

		return left / right;
	}

	// Test code for 
	public static void main(String[] args) {

		// Instantiate student code
		ExpressionTree eTree = new ExpressionTree();

		Scanner input = new Scanner(System.in);
		String expression = input.nextLine();

		System.out.println("Original Expression: " + expression);

		// Verify parse
		Queue<String> infix = eTree.parse(expression);
		System.out.println("Infix Tokens: " + infix.toString());

		// Verify convert
		List<String> postfix = eTree.convert(infix);
		System.out.println("Postfix Tokens: " + postfix.toString());

		// Verify build
		eTree.build(postfix);
		System.out.println("Build: complete");

		// Verify prefix
		System.out.println("Prefix: " + eTree.prefix());

		// Verify infix
		System.out.println("Infix: " + eTree.infix());

		// Verify postfix
		System.out.println("Postfix: " + eTree.postfix());

		// Verify evaluate
		System.out.println("Evaluate: " + eTree.evaluate());

		// Verify display
		System.out.println("Display: complete");
	}
}