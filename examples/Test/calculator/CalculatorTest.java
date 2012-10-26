package calculator;

public class CalculatorTest {

    private Calculator calc;

	public void testCurrentNumber() throws InterruptedException {
		calc = new Calculator(99.5);
		assert calc.getCurrentNumber() == 99.5;
	}

	public void testPlus() throws InterruptedException {
		calc = new Calculator(99.5);
		assert calc.plus(0.1) == 99.6;
	}

}
