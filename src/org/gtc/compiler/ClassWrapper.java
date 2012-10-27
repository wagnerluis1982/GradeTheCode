package org.gtc.compiler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.gtc.sourcecode.SourceCode;

/**
 * {@code ClassWrapper} is a wrapper around {@link java.lang.Class}
 *
 * <p> This class doesn't have neither half of {@code Class} methods. Some
 * methods here are bridge methods and others are helper methods.
 *
 * <p> If some needed {@code Class} method is not currently available, can be
 * accessible through {@link #getJavaClass()} method.
 *
 * <p> The {@code ClassWrapper} class carry too the {@link SourceCode} object
 * related to the java {@code Class}.
 *
 * @author Wagner Macedo
 */
public class ClassWrapper {

	private final Class<?> javaClass;
	private Method[] declaredMethods;
	private Method[] publicMethods;
	private Method[] testMethods;

	private SourceCode sourceCode;

	/**
	 * Construct a ClassWrapper with a {@code Class} and {@link SourceCode}
	 * objects related to the class. This class is instantiated by the compiler
	 * {@link Compiler} process of load classes.
	 *
	 * @param javaClass a java class
	 * @param sourceCode the {@link SourceCode} object related to this class
	 */
	protected ClassWrapper(Class<?> javaClass, SourceCode sourceCode) {
		this.javaClass = javaClass;
		this.sourceCode = sourceCode;
	}

	/**
	 * The actual java class wrapped
	 *
	 * @return the wrapped {@link Class}
	 */
	public Class<?> getJavaClass() {
		return javaClass;
	}

	/**
	 * Bridge method to get the full class name
	 *
	 * @return the name of the class or interface represented by the wrapped
	 * {@code Class}
	 * @see java.lang.Class#getName()
	 */
	public String getName() {
		return this.javaClass.getName();
	}

	/**
	 * Bridge method to get the wrapped class name
	 *
	 * @return @return the simple name of the wrapped class
	 * @see java.lang.Class#getSimpleName()
	 */
	public String getSimpleName() {
		return this.javaClass.getSimpleName();
	}

	/**
	 * The wrapped class package name
	 *
	 * @return the package name associated to the wrapped class
	 */
	public String getPackageName() {
		Package pkg = this.javaClass.getPackage();
		if (pkg != null)
			return pkg.getName();

		return null;
	}

	/**
	 * Bridge method to get a specific declared method in the wrapped class
	 *
	 * <p> In contrary to {@link Class#getDeclaredMethod(String, Class...)},
	 * this method doesn't throw any exception, instead returns null silently.
	 *
	 * @param name the name of the method
     * @param parameterTypes the parameter array
	 * @return the {@code Method} object for the method of the warpped class
     * matching the specified name and parameters
     *
     * @see java.lang.Class#getDeclaredMethod(String, Class...)
	 */
	public Method getDeclaredMethod(String name, Class<?>... parameterTypes) {
		try {
			return this.javaClass.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}

		return null;
	}

	/**
	 * Bridge method to get all declared methods in the wrapped class
	 *
	 * @return the array of {@code Method} objects representing all the
     * declared methods of the wrapped class
	 *
	 * @see java.lang.Class#getDeclaredMethods()
	 */
	public Method[] getDeclaredMethods() {
		this.declaredMethods = this.declaredMethods != null ? this.declaredMethods
				: this.javaClass.getDeclaredMethods();

		return declaredMethods;
	}

	/**
	 * Helper method to get all declared public methods in the wrapped class
	 *
	 * @return the array of {@code Method} objects representing all the
     * declared public methods of the wrapped class
	 */
	public Method[] getDeclaredPublicMethods() {
		if (this.publicMethods != null)
			return this.publicMethods;

		List<Method> methods = new ArrayList<Method>();

		for (Method method : this.getDeclaredMethods())
			if (Modifier.isPublic(method.getModifiers()))
				methods.add(method);

		return (this.publicMethods = methods.toArray(new Method[0]));
	}

	/**
	 * Get all methods in the wrapped class that is elegible to use test methods
	 *
	 * <p> A test method is a public void method which doesn't need parameters
	 * and it name starts with "test".
	 *
	 * @return an array of {@code Method} objects elegible as test methods.
	 */
	public Method[] getTestMethods() {
		if (this.testMethods != null)
			return this.testMethods;

		List<Method> methods = new ArrayList<Method>();

		for (Method method : this.getDeclaredPublicMethods())
			if (method.getName().startsWith("test")
					&& method.getParameterTypes().length == 0
					&& method.getReturnType().equals(Void.TYPE))
				methods.add(method);

		return (this.testMethods = methods.toArray(new Method[0]));
	}

	/**
	 * Create an {@link Instance} of the wrapped class
	 *
	 * <p> This method is very different from {@link Class#newInstance()},
	 * because uses the {@code Instance} object, that is also a wrapper around
	 * any instance.
	 *
	 * @return an {@code Instance} object, that represents a newly allocated
	 * instance of the wrapped class
	 */
	public Instance newInstance() {
		try {
			return new Instance(this.javaClass.newInstance());
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		return null;
	}

	/**
	 * Get the {@link SourceCode} object used to compile this class
	 *
	 * @return the associated {@code SourceCode} object
	 */
	public SourceCode getSourceCode() {
		return sourceCode;
	}

}
