package org.oddjob.devguide;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;

public class ServicesJobExamplesTest extends TestCase {
	
	private static final Logger logger = Logger.getLogger(
			ServicesJobExamplesTest.class);

	// #Snack {
	interface Snack {
		// A marker interface only.		
	}
	// } #Snack
	
	// #SnackProvider {
	interface SnackProvider {
		
		public Snack provideSnack();
	}
	// } #SnackProvider
	
	
	// #Cafe {
	public static class Cafe implements SnackProvider {
			
		@Override
		public Snack provideSnack() {
			return new Snack() {
				@Override
				public String toString() {
					return "Green Eggs and Ham";
				}
			};
		}
	}
	// } #Cafe
	
	// #VegetarianCafe {
	public static class VegetarianCafe implements SnackProvider {
		
		public Snack provideSnack() {
			return new Snack() {
				@Override
				public String toString() {
					return "Salad";
				}
			};
		}
	}
	// } #VegetarianCafe
	
	// #HungryJob {
	public static class HungryJob implements Runnable {
		
		private SnackProvider snackProvider;
		
		@Override
		public void run() {
			Snack snack = snackProvider.provideSnack();
			System.out.println("Snack eaten: " + snack.toString() + ".");
		}
		
		@Inject
		public void setSnackProvider(SnackProvider snackProvider) {
			this.snackProvider = snackProvider;
		}
	}
	// } #HungryJob
	
	public void testSimpleExample() {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/ServicesExample.xml");
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(config));
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			oddjob.run();
		}
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals(1, lines.length);
		
		assertEquals("Snack eaten: Green Eggs and Ham.", lines[0].trim());

		oddjob.destroy();
	}
	
	
	// #HungryVegetarianJob {
	public static class HungryVegetarianJob implements Runnable {
		
		private SnackProvider snackProvider;
		
		@Override
		public void run() {
			Snack snack = snackProvider.provideSnack();
			System.out.println("Snack eaten: " + snack.toString() + ".");
		}
		
		@Inject @Named("Vegetarian")
		public void setSnackProvider(SnackProvider snackProvider) {
			this.snackProvider = snackProvider;
		}
	}
	// } #HungryVegetarianJob
	
	public void testQualifiedExample() {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/ServicesQualifiedExample.xml");
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(config));
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			oddjob.run();
		}
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals(2, lines.length);
		
		assertEquals("Snack eaten: Green Eggs and Ham.", lines[0].trim());
		assertEquals("Snack eaten: Salad.", lines[1].trim());

		oddjob.destroy();
	}
	
	public void testAnyExample() {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/ServicesAnyExample.xml");
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(config));
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			oddjob.run();
		}
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals(1, lines.length);
		
		assertEquals("Snack eaten: Green Eggs and Ham.", lines[0].trim());

		oddjob.destroy();
	}
	
	public void testIntransigentExample() {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/ServicesIntransigentExample.xml");
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(config));
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			oddjob.run();
		}
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals(1, lines.length);
		
		assertEquals("Snack eaten: Salad.", lines[0].trim());

		oddjob.destroy();
	}
	
	// #ResilientVegetarianJob {
	public static class ResilientVegetarianJob implements Runnable {
		
		private SnackProvider snackProvider;
		
		@Override
		public void run() {
			if (snackProvider == null) {
				System.out.println("No Snack!");
			}
			else {
				Snack snack = snackProvider.provideSnack();
				System.out.println("Snack eaten: " + snack.toString() + ".");
			}
		}
		
		@Inject @Named("Vegetarian")
		public void setSnackProvider(SnackProvider snackProvider) {
			this.snackProvider = snackProvider;
		}
	}
	// } #ResilientVegetarianJob
	
	public void testIntransigentExample2() {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/ServicesIntransigentExample2.xml");
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(config));
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			oddjob.run();
		}
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals(1, lines.length);
		
		assertEquals("No Snack!", lines[0].trim());

		oddjob.destroy();
	}
}
