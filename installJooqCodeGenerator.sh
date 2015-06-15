#!/bin/bash

mvn install:install-file -DgroupId=org.jooq -DartifactId=jooq-codegen -Dpackaging=jar -Dversion=3.7.0.lvl6 -Dfile=lib/jooq-codegen-3.7.0.lv6.jar -DgeneratePom=true
