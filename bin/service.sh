#!/bin/sh

# Convenience shell script for running the service from the command line, passing along all command line arguments.
# This must be run in the root directory of the project as it expects all of the build artifacts created by the
# appassembler plugin to be present in the target directory.

sh target/bin/service "$@"
