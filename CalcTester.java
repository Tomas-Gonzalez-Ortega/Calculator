import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.*;

public class CalcTester {
	public static void main(String[] args) {
		new TestRunner(CalcTester.class).run();
	}

	// special... no input
	public void testEmpty() {
		try {
			new Calc("").call();
			assert false;
		} catch (IllegalStateException e) {
			assert true;
		}
	}
	public void testEmpty_cause() {
		try {
			new Calc("").call();
			assert false;
		} catch (IllegalStateException e) {
			assert e.getCause() instanceof NoSuchElementException;
		}
	}

	// typical ... 1 number
	public void testSingle() {
		assert 0 == new Calc("0").call();
		assert 3 == new Calc("3").call();
		assert -1 == new Calc("-1").call();
	}

	// special... malformed input: too many operands
	public void testTooMany() {
		try { // boundary... 1 too many
			new Calc("1 2").call();
			assert false;
		} catch (IllegalStateException e) {
			assert true;
		}

		try { // typical... many too many
			new Calc("1 2 3 4 5").call();
			assert false;
		} catch (IllegalStateException e) {
			assert true;
		}

		try { // boundary... 1 too many (given test case)
			new Calc("1 1 1 +").call();
			assert false;
		} catch (IllegalStateException e) {
			assert true;
		}
	}
	public void testTooMany_message() {
		try { // boundary... 1 too many
			new Calc("1 2").call();
			assert false;
		} catch (IllegalStateException e) {
			assert e.getMessage().contains("2");
		}

		try { // typical... many too many
			new Calc("1 2 3 4 5").call();
			assert false;
		} catch (IllegalStateException e) {
			assert e.getMessage().contains("5");
		}

		try { // boundary... 1 too many (given test case)
			new Calc("1 1 1 +").call();
			assert false;
		} catch (IllegalStateException e) {
			assert e.getMessage().contains("2");
		}
	}

	// special... malformed: not enough Operands
	public void testNotEnoughOperands() {
		try { // none
			new Calc("+").call();
			assert false;
		} catch (IllegalStateException e) {
			assert true;
		}

		try { // 1
			new Calc("1 +").call();
			assert false;
		} catch (IllegalStateException e) {
			assert true;
		}
	}
	public void testNotEnoughOperands_cause() {
		try { // none
			new Calc("+").call();
			assert false;
		} catch (IllegalStateException e) {
			assert e.getCause() instanceof NoSuchElementException;
		}

		try { // 1
			new Calc("1 +").call();
			assert false;
		} catch (IllegalStateException e) {
			assert e.getCause() instanceof NoSuchElementException;
		}
	}
		

	// special... malformed: bad operator
	public void testBadOperator() {
		try { // (given)
			new Calc("1 1 #").call();
			assert false;
		} catch (IllegalArgumentException e) {
			assert true;
		}
	}
	public void testBadOperator_message() {
		try { // (given)
			new Calc("1 1 #").call();
			assert false;
		} catch (IllegalArgumentException e) {
			assert e.getMessage().contains("#");
		}
	}

	
	public void testAdd() {
		assert 8 == new Calc("3 5 +").call();
	}

	public void testSub() {
		assert -2 == new Calc("3 5 -").call();
		assert 2 == new Calc("5 3 -").call();
	}

	public void testMul() {
		assert 15 == new Calc("3 5 *").call();
	}

	public void testDiv() {
		assert 3/5 == new Calc("3 5 /").call();
		assert 5/3 == new Calc("5 3 /").call();
	}

	public void testMod() {
		assert 3%5 == new Calc("3 5 %").call();
		assert 5%3 == new Calc("5 3 %").call();
	}

	public void testPow() {
		assert Math.pow(3,5) == new Calc("3 5 ^").call();
		assert Math.pow(5,3) == new Calc("5 3 ^").call();
	}

	public void testMultiOperators() {
		assert 6 == new Calc("1 2 3 + +").call();
		assert 4 == new Calc("10 1 2 3 + + -").call();
	}
	
	public void testLong() {
		// adds 1 million 1s together
		final int num = 1 * 1024 * 1024;
		String in = Stream.concat(
			IntStream.range(0, num)
				.map(i -> 1)
				.mapToObj(Integer::toString),
			IntStream.range(0, num - 1)
				.mapToObj(i -> "+")
		).collect(Collectors.joining(" "));

		assert num == new Calc(in).call();
	}
	
	// IOException in scanner
	public void testIOException() {
		Scanner sc = new Scanner((cb) -> { throw new IOException(); });
		try {
			new Calc(sc).call();
			assert false;
		} catch (RuntimeException e) {
			assert true;
		}
	}
	public void testIOException_cause() {
		Scanner sc = new Scanner((cb) -> { throw new IOException(); });
		try {
			new Calc(sc).call();
			assert false;
		} catch (RuntimeException e) {
			assert e.getCause() instanceof IOException;
		}
	}

	public void testGivenTests() throws IOException {
		try (
			BufferedReader expected = Files.newBufferedReader(Paths.get("test.expected"));
			BufferedReader inputs = Files.newBufferedReader(Paths.get("test.in"))
		) {
			String exp = expected.readLine();
			String inp = inputs.readLine();
			while(exp != null) {
				int expI = Integer.parseInt(exp);
				int actI = new Calc(inp).call();
				assert expI == actI : inp + " ? " + expI + " : " + actI;
				exp = expected.readLine();
				inp = inputs.readLine();
			}
			assert null == inp;
		}
	}
}
