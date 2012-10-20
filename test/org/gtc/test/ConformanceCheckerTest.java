package org.gtc.test;

import static org.junit.Assert.*;

import japa.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.gtc.compiler.ClassWrapper;
import org.gtc.compiler.Compiler;
import org.gtc.compiler.CompilerException;
import org.gtc.sourcecode.SourceCode;
import org.gtc.test.ConformanceResult.ClassConformanceResult;
import org.gtc.test.ConformanceResult.MethodConformanceResult;
import org.junit.Test;

public class ConformanceCheckerTest {

	@Test
	public void testCheck() throws IOException, ParseException, CompilerException {
		Compiler compiler = new Compiler();
		compiler.addCodes(new SourceCode(resource("examples/Ideal.src")));
		compiler.compile();
		ClassWrapper cls = compiler.getClasses().get("com.example.Calculator");

		ClassRules clsRules = new ClassRules(cls.getName());
		for (Method method : cls.getDeclaredPublicMethods())
			clsRules.addMethodRule(method);
		ConformanceRules rules = new ConformanceRules(clsRules);

		ConformanceChecker checker = new ConformanceChecker(rules);

		compiler = new Compiler();
		compiler.addCodes(new SourceCode(resource("examples/NonConform.src")));
		compiler.compile();
		Map<String, ClassWrapper> classes = compiler.getClasses();

		ConformanceResult result = checker.check(classes);
		ClassConformanceResult classCR = result.iterator().next();
		assertEquals("com.example.Calculator", classCR.getName());
		assertFalse(classCR.isMissing());

		Iterator<MethodConformanceResult> it = classCR.iterator();

		assertTrue(it.hasNext());
		MethodConformanceResult methodCR = it.next();
		assertEquals("plus", methodCR.getName());
		assertTrue(methodCR.isMissing());

		assertTrue(it.hasNext());
		methodCR = it.next();
		assertEquals("getCurrentNumber", methodCR.getName());
		assertFalse(methodCR.isMissing());
		assertTrue(methodCR.getVisibility().equals(Visibility.PUBLIC));
		assertFalse(methodCR.isReturnTypeConforming());
	}

	private InputStream resource(String name) {
		return getClass().getResourceAsStream(name);
	}

}
