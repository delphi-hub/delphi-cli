# Delphi Command-Line Interface (CLI)

The command-line interface for the Delphi platform.

We are currently in pre-alpha state! There is no release and the code in
this repository is purely experimental!

|branch | status | codacy | snyk |
| :---: | :---: | :---: | :---: |  
| master | [![Build Status](https://travis-ci.org/delphi-hub/delphi-cli.svg?branch=master)](https://travis-ci.org/delphi-hub/delphi-cli) | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/47046de0e8d64ae4b76191b7dae80075)](https://www.codacy.com/app/delphi-hub/delphi-cli?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=delphi-hub/delphi-cli&amp;utm_campaign=Badge_Grade)| [![Known Vulnerabilities](https://snyk.io/test/github/delphi-hub/delphi-cli/badge.svg?targetFile=build.sbt)](https://snyk.io/test/github/delphi-hub/delphi-cli?targetFile=build.sbt) |
| develop | [![Build Status](https://travis-ci.org/delphi-hub/delphi-cli.svg?branch=develop)](https://travis-ci.org/delphi-hub/delphi-cli) | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/47046de0e8d64ae4b76191b7dae80075?branch=develop)](https://www.codacy.com/app/delphi-hub/delphi-cli?branch=develop&amp;utm_source=github.com&amp;utm_medium=referral&amp;utm_content=delphi-hub/delphi-cli&amp;utm_campaign=Badge_Grade)| [![Known Vulnerabilities](https://snyk.io/test/github/delphi-hub/delphi-cli/develop/badge.svg?targetFile=build.sbt)](https://snyk.io/test/github/delphi-hub/delphi-cli/develop?targetFile=build.sbt)

## What is the Delphi Command-Line Interface?

The Delphi CLI is a tool to access the data on the Delphi platform.
It enables you to search for items matching a query or to inspect a dataset for an item in detail.
It can be used in an automated context to automatically construct fitting item sets.

## How does it work?

The Delphi CLI checks the provided query and passes it on to the web API of the configured platform.
The results are printed to the console by default.

## How can I use it?

The Delphi CLI is running on the Java Virtual Machine.
We require a Java Runtime Environment (JRE) in version 8 or newer.

Our software is available as a binary release on [GitHub](https://github.com/delphi-hub/delphi-cli/releases).

```
$ delphi-cli --help
Delphi Command Line Tool (1.0.0-SNAPSHOT)
Usage: delphi-cli [test|search|retrieve] [options]

  --version         Prints the version of the command line tool.
  --help            Prints this help text.
  --server <value>  The url to the Delphi server
```
By default the command-line tool uses the official Delphi server at https://delphi.cs.uni-paderborn.de to process queries.
You can override this setting using the `--server` option or by setting the `DELPHI_SERVER` environment variable.

## Community

Feel welcome to join our chatroom on Gitter: [![Join the chat at https://gitter.im/delphi-hub/delphi](https://badges.gitter.im/delphi-hub/delphi.svg)](https://gitter.im/delphi-hub/delphi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


## Contributing

Contributions are *very* welcome!

Before contributing, please read our [Code of Conduct](CODE_OF_CONDUCT.md).

Refer to the [Contribution Guide](CONTRIBUTING.md) for details about the workflow.
We use Pull Requests to collect contributions. Especially look out for "help wanted" issues
[![GitHub issues by-label](https://img.shields.io/github/issues/delphi-hub/delphi-cli/help%20wanted.svg)](https://github.com/delphi-hub/delphi-cli/issues?q=is%3Aopen+is%3Aissue+label%3A%22help+wanted%22),
but feel free to work on other issues as well.
You can ask for clarification in the issues directly, or use our Gitter
chat for a more interactive experience.

[![GitHub issues](https://img.shields.io/github/issues/delphi-hub/delphi-cli.svg)](https://github.com/delphi-hub/delphi-cli/issues)


## License

The Delphi CLI is open source and available under Apache 2 License.

[![GitHub license](https://img.shields.io/github/license/delphi-hub/delphi-cli.svg)](https://github.com/delphi-hub/delphi-cli/blob/master/LICENSE)
