#!/bin/bash

# Run all tests with Jest
echo "Running all tests..."
npm test -- --watchAll=false

# Exit with the status code from Jest
exit $?