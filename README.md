# aspose
Aspose cloud API

To run `AsposeWordsTaskTest` create a `pso-aspose-ant/aspose.properties` file containing

```
clientid=
clientsecret=
```

with values generated from https://dashboard.aspose.cloud/

To use the ANT `AsposeWordsTask` in a PageSeeder project to convert a DOCX file to PDF
the `pso-aspose-ant-x.jar` and the libraries it depends on must be manually uploaded
to the project template in PageSeeder.
This is because the `javax.mail` library used by Aspose clashes with `jakarta.mail` used by PageSeeder.

To do this run the `publishing.copyToLib` gradle task under the `pso-aspose-ant` module
and upload the files under `build/output/lib` to a `lib` folder where your `build.xml`
file is in PageSeeder.

Your `build.xml` will need similar to the following commands to run the task:

```
<property file="aspose.properties" />
<path id="aspose.classpath"><fileset dir="${basedir}/lib"><include name="*.jar" /></fileset></path>
<taskdef name="aspose-words" classname="org.pageseeder.aspose.ant.AsposeWordsTask" classpathref="aspose.classpath"/>
<aspose-words src="[DOCX file path]"
              dest="[PDF file path]"
              updatefields="[true|false]"
              clientid="${aspose.clientid}"
              clientsecret="${aspose.clientsecret}"/>
```

where `aspose.properties` contains `aspose.clientid=` and `aspose.clientsecret=`
generated from https://dashboard.aspose.cloud

For information on Aspose Words Cloud see https://products.aspose.cloud/words/java/