package com.flolin.playground.app;

import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;

/**
 * A custom Vert.x starter.
 * <p/>
 * @author last revision $Author: flintzen $
 * @version $Revision: 1.1 $ $Date: 7/8/15 5:15 PM $
 */
public class AppLauncher extends Launcher
{
   /**
    * Main entry point.
    * @param args the user command line arguments.
    */
   @SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
   public static void main(final String... args)
   {
      new AppLauncher().dispatch(args);
   }

   @Override
   public void beforeStartingVertx(final VertxOptions aOptions)
   {
      aOptions.setAddressResolverOptions(AddressResolverOptionsFactory.create());
   }
}
