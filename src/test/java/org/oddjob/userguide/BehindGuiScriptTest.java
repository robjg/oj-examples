package org.oddjob.userguide;

import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.tools.ConsoleCapture;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BehindGuiScriptTest {

    @Test
    void firstScript() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File("examples/userguide/BehindGuiScript.xml"));

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close ignored = console.captureConsole()) {
            oddjob.run();
        }

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        assertThat(console.getLines()[0], is("2 + 2 is 4"));

        oddjob.destroy();
    }

    @Test
    void session() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File("examples/userguide/BehindGuiScriptSession.xml"));

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close ignored = console.captureConsole()) {
            oddjob.run();
        }

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        assertThat(console.getLines()[1], is("Hello, World!"));

        oddjob.destroy();
    }

    @Test
    void vars() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File("examples/userguide/BehindGuiScriptVars.xml"));

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close ignored = console.captureConsole()) {
            oddjob.run();
        }

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        assertThat(console.getLines()[1], is("Variable x is: undefined"));

        oddjob.destroy();
    }

    @Test
    void funcs() {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File("examples/userguide/BehindGuiScriptFuncs.xml"));

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close ignored = console.captureConsole()) {
            oddjob.run();
        }

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        assertThat(console.getLines()[0], is("2 + 3 is 5.0, 2 * 3 is 6.0."));

        oddjob.destroy();
    }
}
