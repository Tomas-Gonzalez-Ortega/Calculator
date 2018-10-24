import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

public class Calc implements Callable<Integer> {

	private final Scanner sc;
	private final Deque<Integer> stack;

	public Calc(String anIn) {
		this(new Scanner(anIn));
	}

	public Calc(Scanner aScanner) {
		stack = new ArrayDeque<>();
		sc = aScanner;
	}

	public Integer call() {
		try {
			while(sc.hasNext()) {
				if(sc.hasNextInt()) {
					int i = sc.nextInt();
					stack.push(i);
				} else {
					String s = sc.next();
					stack.push(operate(s, stack.pop(), stack.pop()));
				}
			}
			if(stack.size() > 1) {
				throw new IllegalStateException(Integer.toString(stack.size()));
			}
			return stack.pop();
		} catch(NoSuchElementException e) {
			throw new IllegalStateException(e);
		} finally {
			if(sc.ioException() != null) {
				throw new RuntimeException(sc.ioException());
			}
		}
	}

	private static int operate(String operator, int i2, int i1) {
		switch (operator) {
			case "+" :
				return i1 + i2;
			case "-" :
				return i1 - i2;
			case "*" :
				return i1 * i2;
			case "/" :
				return i1 / i2;
			case "%":
				return i1 % i2;
			case "^":
				return (int) Math.pow(i1, i2);
			default:
				throw new IllegalArgumentException(operator);
		}
	}
}
