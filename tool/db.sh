#!/bin/sh

tool_dir=$(cd $(dirname $0);pwd)
cp=${tool_dir}:/home/hashiwa/.gradle/caches/modules-2/files-2.1/org.apache.derby/derby/10.12.1.1/75070c744a8e52a7d17b8b476468580309d5cd09/derby-10.12.1.1.jar

javac -cp $cp -d ${tool_dir} ${tool_dir}/DB.java
java -cp $cp DB "$*"

