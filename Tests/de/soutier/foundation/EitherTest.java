package de.soutier.foundation;

import junit.framework.Assert;

import org.junit.Test;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class EitherTest {
  @Test public void badRequestReturnsLeft() {
	 final String password = "wrong";
	 Either<IllegalAccessException, String> result = checkPassword(password);
	 Assert.assertEquals(IllegalAccessException.class, result.value().getClass()); 
	 Assert.assertTrue(result.isLeft());
  }
  
  @Test public void correctRequestReturnsRight() {
	  final String password = "right";
	  Either<IllegalAccessException, String> result = checkPassword(password);
	  Assert.assertEquals(String.class, result.value().getClass());
	  Assert.assertTrue(result.isRight());
  }
  
  /** This is you would generate an Either */
  private Either<IllegalAccessException, String> checkPassword(final String password) {
	  if (password.equals("right"))
		  return Either.right("UserObject");
	  return Either.left(new IllegalAccessException("Password wrong!"));
  }
  
  public void useExample() {
	  final String password = "right";
	  Either<IllegalAccessException, String> result = checkPassword(password);
	  if (result.isRight())
		  result.right(); // User log-in successful
	  else
		  result.left().getMessage(); // Show error message to user
  }
  
  /** WO-specific example, you could call the method and redirect to the returned page, no questions asked. */
  public Either<WOComponent, WOComponent> login(final String user, final String password, final WOContext context) {
	  if (password.equals("right"))
		  return Either.right(WOApplication.application().pageWithName("de.soutier.app.components.LoginPage", context));
	  return Either.left(WOApplication.application().pageWithName("de.soutier.app.components.ErrorPage", context));
  }
}
