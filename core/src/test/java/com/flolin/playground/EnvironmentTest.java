package com.flolin.playground;

import com.flolin.playground.app.Env;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the Bootstrap class.
 */
@SuppressWarnings("JavaDoc")
public class EnvironmentTest
{
   public EnvironmentTest()
   {
   }

   @Test
   public void testGetEnvironmentFallback()
   {
      Assert.assertEquals("Wrong environment.", Env.LOCAL, Env.CURRENT);
   }

}
