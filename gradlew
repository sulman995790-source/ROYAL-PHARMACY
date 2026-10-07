#!/bin/sh

#
# Copyright © 2015-2021 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://apache.org
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
#
#   Gradle start up script for POSIX systems
#
##############################################################################

# Attempt to set APP_HOME

# Resolve links: \$0 may be a link
app_path=\$0

# Need this for expr to work.
case `uname` in
  CYGWIN* | MINGW* | MSYS*)
    echo "ERROR: Windows environments are not supported by this script."
    exit 1
    ;;
esac

while [ -h "\$app_path" ] ; do
  ls=`ls -ld "$app_path"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "\$link" : '/.*' > /dev/null; then
    app_path="\$link"
  else
    app_path=`dirname "$app_path"`/"\$link"
  fi
done

APP_HOME=`dirname "$app_path"`
APP_BASE_NAME=`basename "$app_path"`

# Absolute path to the user's home directory
MAX_FD=maximum

# Warn if osx detected
warn () {
    echo "\$*"
}

die () {
    echo
    echo "\$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MSYS* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=\$APP_HOME/gradle/wrapper/gradle-wrapper.jar


# Determine the Java command to use to start the JVM.
if [ -n "\$JAVA_HOME" ] ; then
    if [ -x "\$JAVA_HOME/bin/java" ] ; then
        # Language target
        JAVACMD=\$JAVA_HOME/bin/java
    else
        die "ERROR: JAVA_HOME is set to an invalid directory: \$JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD=java
    if ! command -v java >/dev/null 2>&1; then
        die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
fi

# Increase the maximum file descriptors if we can.
if [ "\$cygwin" = "false" ] && [ "\(msys" = "false" ] && [ "\)nonstop" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ \$? -eq 0 ] ; then
        if [ "\(MAX_FD" = "maximum" ] \vert{}\vert{} [ "\)MAX_FD" = "max" ] ; then
            MAX_FD="\$MAX_FD_LIMIT"
        fi
        ulimit -n \$MAX_FD
        if [ \$? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: \$MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: \$MAX_FD_LIMIT"
    fi
fi

# Collect all arguments for the java command via the environment variable, if specified
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Escape application args
save () {
    for i do printf %s\\n "\$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/'/"; done
    echo " "
}
APP_ARGS=`save "$@"`

# Collect all arguments for the java command;
# install_jvm_opts is dumped directly into the command line
eval set -- "\(DEFAULT_JVM_OPTS" "\)JAVA_OPTS" "\(GRADLE_OPTS" "\"-Dorg.gradle.appname=\)APP_BASE_NAME\"" -classpath "\"\(CLASSPATH\"" org.gradle.wrapper.GradleWrapperMain "\)APP_ARGS"

exec "JAVACMD" "@"
