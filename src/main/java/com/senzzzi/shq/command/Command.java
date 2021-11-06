package com.senzzzi.shq.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface Command {

    public String getCommand();

    public String getDescription();

    public Options getOptions();

    public void run(CommandLine commandLine);

}
