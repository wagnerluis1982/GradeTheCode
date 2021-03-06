package calculator;

import static java.lang.Thread.sleep;

public class Calculator {

    private double currentNumber;

    public Calculator(double currentNumber) {
    	this.currentNumber = currentNumber;
    }

	public double getCurrentNumber() throws InterruptedException {
		sleep(2);
		return this.currentNumber;
	}

	public double plus(double newNumber) throws InterruptedException {
		sleep(1);
		this.currentNumber += newNumber;
		return this.getCurrentNumber();
	}

}
