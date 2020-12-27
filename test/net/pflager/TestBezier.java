package net.pflager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestBezier extends Bezier {

	@Test
	void testFactorial_0() {
		// System.out.println(factorial(0));
		assertEquals( 1,factorial(0));
	}
	
	@Test
	void test1_factorial() {
		// System.out.println(factorial(1));
		assertEquals(1, factorial(1));
	}
	
	@Test
	void test2_factorial() {
		// System.out.println(factorial(2));
		assertEquals(2, factorial(2));
	}
	
	@Test
	void test3_factorial() {
		// System.out.println(factorial(3));
		assertEquals(6, factorial(3));
	}
	
	@Test
	void test4_factorial() {
		assertEquals(24, factorial(4));		
		// System.out.println(factorial(4));
	}
	
	@Test
	void test0_choose_0() {
		assertEquals(1, choose(0, 0));		
		// System.out.println(factorial(1));
	}
	
	@Test
	void test1_choose_0() {
		assertEquals(1, choose(1, 0));
		// System.out.println(choose(1, 0));
	}
	
	@Test
	void test1_choose_1() {
		assertEquals(1, choose(1, 1));
		// System.out.println(choose(1, 1));
	}
	
	@Test
	void test2_choose_0() {
		assertEquals(1, choose(2, 0));		
		// System.out.println(choose(2, 1));
	}
	
	@Test
	void test2_choose_1() {
		assertEquals(2, choose(2, 1));		
		// System.out.println(choose(2, 1));
	}
	
	@Test
	void test2_choose_2() {
		assertEquals(1, choose(2, 2));		
		// System.out.println(choose(2, 2));
	}
	
	@Test
	void test3_choose_0() {
		assertEquals(1, choose(3, 0));		
		// System.out.println(choose(3, 0));
	}
	
	@Test
	void test3_choose_1() {
		assertEquals(3, choose(3, 1));		
		// System.out.println(choose(3, 1));
	}
	
	@Test
	void test3_choose_2() {
		assertEquals(3, choose(3, 2));		
		// System.out.println(choose(3, 2));
	}
	
	@Test
	void test3_choose_3() {
		assertEquals(1, choose(3, 3));		
		// System.out.println(choose(3, 3));
	}
	
	@Test
	void test4_choose_0() {
		assertEquals(1, choose(4, 0));		
		// System.out.println(choose(4, 0));
	}
	
	@Test
	void test4_choose_1() {
		assertEquals(4, choose(4, 1));		
		// System.out.println(choose(4, 1));
	}
	
	@Test
	void test4_choose_2() {
		assertEquals(6, choose(4, 2));		
		// System.out.println(choose(4, 2));
	}
	
	@Test
	void test4_choose_3() {
		assertEquals(4, choose(4, 3));		
		// System.out.println(choose(4, 3));
	}
	
	@Test
	void test_generatePolynomialLatex_0() {
		System.out.println(generatePolynomialLatex(0));
	}

	@Test
	void test_generatePolynomialLatex_1() {
		System.out.println(generatePolynomialLatex(1));
	}

	@Test
	void test_generatePolynomialLatex_2() {
		System.out.println(generatePolynomialLatex(2));
	}

	@Test
	void test_generatePolynomialLatex_3() {
		System.out.println(generatePolynomialLatex(3));
	}

	@Test
	void test_generatePolynomialLatex_4() {
		System.out.println(generatePolynomialLatex(4));
	}

}
