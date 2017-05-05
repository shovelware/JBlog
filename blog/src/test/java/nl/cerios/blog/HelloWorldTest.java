package nl.cerios.blog;

import junit.framework.TestCase;

public class HelloWorldTest extends TestCase {
	
	public void testHelloWorld(){
		BlogServlet bs = new BlogServlet();
		String result = "";
		
		result = bs.hallo("Davy");
		
		assertEquals("Hello, Davy.", result);
	}
}