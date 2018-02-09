package de.upb.cs.swt.delphi.cli

/**
  * Represents the implementation of a command of the CLI
  */
trait Command {

  /**
    * Executes the command implementation
    * @param config The current configuration for the command
    */
  def execute(config: Config) : Unit

}
