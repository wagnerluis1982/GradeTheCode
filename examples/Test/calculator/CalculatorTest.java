package calculator;

public class CalculatorTest {

    private Calculator calc;

    public CalculatorTest() {
    	calc = new Calculator(99.5);
    }

	public void testCurrentNumber() throws InterruptedException {
		assert calc.getCurrentNumber() == 99.5;
	}

	public void testPlus() throws InterruptedException {
		assert calc.plus(0.1) == 99.6;
	}

}
