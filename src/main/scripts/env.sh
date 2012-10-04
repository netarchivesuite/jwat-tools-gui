# If JAVA_HOME is not set, use the java in the execution path
if [ ${JAVA_HOME} ] ; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

# ${assembly.home.env.name} must point to home directory of JWAT-Tools install.
PRG="$0"

# need this for relative symlinks
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG="`dirname "$PRG"`/$link"
    fi
done

${assembly.home.env.name}=`dirname "$PRG"`

# make it fully qualified
${assembly.home.env.name}=`cd "${assembly.home.env.name.ref}" && pwd`

# CP must contain a colon-separated list of resources used by JWAT-Tools.
CP=${assembly.home.env.name.ref}/
for i in `ls ${assembly.home.env.name.ref}/lib/*.jar`
do
  CP=${CP}:${i}
done
#echo $CP
