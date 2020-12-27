package net.pflager;

public class Bezier {
	public static int factorial(int n) {
		int accumulator = 1;
		for (int i = n; i > 1; i--) {
			accumulator *= i;
		}
		return accumulator;
	}

	public static int choose(int n, int k) {
		return factorial(n) / factorial(k) / factorial(n - k);
	}

	public static String generatePolynomialLatex(int degree) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < degree + 1; i++) {
			if (i != 0) {
				stringBuilder.append(" + ");
			}

			stringBuilder.append("\\beta_{").append(i).append("}");

			int degreeChooseI = choose(degree, i);
			if (degreeChooseI < 99) {
				if (degreeChooseI > 1) {
					stringBuilder.append(choose(degree, i));
				}
			} else {
				stringBuilder.append("{").append(degree).append("\\choose ").append(i).append("}");
			}

			if (i > 0) {
				stringBuilder.append("t");
				if (i > 1) {
					stringBuilder.append("^").append(i);
				}
			}

			if (degree - i > 0) {
				stringBuilder.append("(1-t)");
				if (degree - i > 1) {
					stringBuilder.append("^").append(degree - i);
				}
			}
		}

		return stringBuilder.toString();
	}

}
