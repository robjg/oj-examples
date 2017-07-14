package org.oddjob.userguide;

import java.util.Properties;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Stateful;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.input.InputHandler;
import org.oddjob.input.InputRequest;
import org.oddjob.state.JobState;
import org.oddjob.state.ParentState;
import org.oddjob.state.ServiceState;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;
import org.oddjob.tools.StateSteps;

import junit.framework.TestCase;

public class TasksTest extends TestCase {

	private class OurInputHandler implements InputHandler {
		
		@Override
		public Properties handleInput(InputRequest[] requests) {
			
			Properties properties = new Properties();
			
			properties.setProperty("some.greeting", "Hello");
			
			return properties;
		}
	}
	
	public void testInputWaitExample() throws InterruptedException, ArooaPropertyException, ArooaConversionException {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob server = new Oddjob();
		server.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/tasks1.xml")));
		
		server.load();
		
		Stateful waitJob = new OddjobLookup(server).lookup("wait", Stateful.class);
		
		StateSteps waitState = new StateSteps(waitJob);
		waitState.startCheck(JobState.READY, 
				JobState.EXECUTING);	
		
		Stateful echoJob = new OddjobLookup(server).lookup("echo", Stateful.class);
		
		StateSteps echoState = new StateSteps(echoJob);
		echoState.startCheck(JobState.READY, 
				JobState.EXECUTING, JobState.COMPLETE);	
		
		Thread t = new Thread(server);
		t.start();
		
		waitState.checkWait();

		Oddjob client = new Oddjob();
		client.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/tasks2.xml")));
		client.setInputHandler(new OurInputHandler());
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
	        client.run();
		
	        assertEquals(ParentState.COMPLETE, client.lastStateEvent().getState());
		
	        echoState.checkWait();
		
		}

		String[] lines = console.getLines();

		assertEquals("Hello", lines[0].trim());
		
		client.destroy();
		server.destroy();
		
	}
	
	public void testTaskExample() throws InterruptedException, ArooaPropertyException, ArooaConversionException {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob server = new Oddjob();
		server.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/tasks3.xml")));
		
		server.load();
		
		Stateful waitJob = new OddjobLookup(server).lookup("greeting-service", Stateful.class);
		
		StateSteps waitState = new StateSteps(waitJob);
		waitState.startCheck(ServiceState.STARTABLE, 
				ServiceState.STARTING, ServiceState.STARTED);	
		
		Stateful echoJob = new OddjobLookup(server).lookup("echo", Stateful.class);
		
		StateSteps echoState = new StateSteps(echoJob);
		echoState.startCheck(JobState.READY, 
				JobState.EXECUTING, JobState.COMPLETE);	
		
		Thread t = new Thread(server);
		t.start();
		
		waitState.checkWait();

		Oddjob client = new Oddjob();
		client.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/tasks4.xml")));
		client.setInputHandler(new OurInputHandler());
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
	        client.run();
		
	        assertEquals(ParentState.COMPLETE, client.lastStateEvent().getState());
		
	        echoState.checkWait();
		
		}

		String[] lines = console.getLines();

		assertEquals("Hello", lines[0].trim());
		
		client.destroy();
		server.destroy();
		
	}
}
