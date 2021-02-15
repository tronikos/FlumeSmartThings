#!/usr/bin/env bash
cd $(dirname "$0")
forever stop index.js
rm -rf ~/.forever/*.log
forever start index.js
forever list
cd -
