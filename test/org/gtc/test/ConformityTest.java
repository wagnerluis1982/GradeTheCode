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
import org.gtc.test.Conformity.Reason;
import org.gtc.test.ConformityResult.ClassConformityResult;
import org.gtc.test.ConformityResult.MethodConformityResult;
import org.junit.Test;

public class ConformityTest {

	@Test
	public void testCheck() throws IOException, ParseException, CompilerException {
		Compiler compiler = new Compiler();
		compiler.addCodes(new SourceCode(resource("examples/Ideal.src")));
		compiler.compile();
		ClassWrapper cls = compiler.getClasses().get("com.example.Calculator");

		ClassRules clsRules = new ClassRules(cls.getName());
		for (Method method : cls.getDeclaredPublicMethods())
			clsRules.addMethodRule(method);
		ConformityRules rules = new ConformityRules(clsRules);

		Conformity checker = new Conformity(rules);

		compiler = new Compiler();
		compiler.addCodes(new SourceCode(resource("examples/NonConform.src")));
		compiler.compile();
		Map<String, ClassWrapper> classes = compiler.getClasses();

		ConformityResult result = checker.check(classes);
		ClassConformityResult classCR = result.iterator().next();
		assertEquals("com.example.Calculator", classCR.getName());
		assertFalse(classCR.isMissing());

		Iterator<MethodConformityResult> it = classCR.iterator();

		assertTrue(it.hasNext());
		MethodConformityResult methodCR = it.next();
		assertEquals("plus", methodCR.getName());
		assertTrue(methodCR.isMissing());

		assertTrue(it.hasNext());
		methodCR = it.next();
		assertEquals("getCurrentNumber", methodCR.getName());
		assertFalse(methodCR.isMissing());
		assertTrue(methodCR.isNonConforming());
		assertEquals(Reason.RETURN_TYPE, methodCR.getNonConformingReason());
	}

	private InputStream resource(String name) {
		return getClass().getResourceAsStream(name);
	}

}
