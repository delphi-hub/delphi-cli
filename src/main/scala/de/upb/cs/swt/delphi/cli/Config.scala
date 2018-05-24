package de.upb.cs.swt.delphi.cli

/**
  * Represents a configuration for the Delphi CLI
  * @param server A server base uri (Defaults to env variable DELPHI_SERVER)
  * @param verbose Marker if logging should be verbose
  * @param mode The command to be run
  */
case class Config (server : String = sys.env.getOrElse("DELPHI_SERVER", "https://delphi.cs.uni-paderborn.de/api/"), verbose: Boolean = false, mode : String = "") {

}
