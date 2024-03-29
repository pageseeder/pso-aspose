package org.pageseeder.aspose.ant;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Properties;

public class AsposeWordsTaskTest {

  private static final File CASES = new File("src/test/aspose/cases");

  private static final File RESULTS = new File("test/aspose/results");

  private static final File CLIENT = new File("aspose.properties");

  @Test
  public void testBasic() throws IOException, SAXException {
    testIndividual("basic", false);
  }

  @Test
  public void testPsGenerate() throws IOException, SAXException {
    testIndividual("ps-generated", false);
  }

  @Test
  public void testUpdateFields() throws IOException, SAXException {
    testIndividual("update-fields", true);
  }

  public void testIndividual(String folderName, boolean updateFields) throws IOException {
    File dir = new File(CASES, folderName);
    if (dir.isDirectory()) {

      if (new File(dir, dir.getName() + ".docx").exists()) {
        System.out.println(dir.getName());
        File result_dir = new File(RESULTS, dir.getName());
        if (result_dir.exists()) {
          FileUtils.deleteDirectory(result_dir);
        }
        result_dir.mkdirs();
        File result = new File(result_dir, dir.getName() + ".pdf");
        File actual = process(dir, result, updateFields);
        File expected = new File(dir, "expected.pdf");

        // Check that the files exist
        Assert.assertTrue(actual.exists());
        Assert.assertTrue(expected.exists());

        Assert.assertTrue(actual.length() > 0);
        Assert.assertTrue(expected.length() > 0);
        Assert.assertTrue("Expected PDF file size:" + expected.length() + " but was " + actual.length(),
            Math.abs(expected.length() - actual.length()) <= 30); // might be slight variation
      } else {
        throw new IOException("Unable to find DOCX file for test:" + dir.getName());
      }
    }
  }

  private File process(File test, File result, boolean updateFields) throws IOException {
    AsposeWordsTask task = new AsposeWordsTask();
    task.setSrc(new File(test, test.getName() + ".docx"));
    task.setDest(result);
    try (InputStream input = new FileInputStream(CLIENT)) {
      Properties props = new Properties();
      props.load(input);
      String clientId = props.getProperty("clientid");
      String clientSecret = props.getProperty("clientsecret");
      String url = props.getProperty("baseurl");
      if (clientId == null || clientSecret == null) {
        throw new IOException("The clientid and clientsecret must be defined in pso-aspose-ant/aspose.properties");
      }
      task.setClientId(clientId);
      task.setClientSecret(clientSecret);
      task.setBaseUrl(url);
      if (updateFields) {
        task.setUpdateFields(true);
      }
    } catch (FileNotFoundException ex) {
      throw new IOException("The clientid and clientsecret must be defined in pso-aspose-ant/aspose.properties");
    }
    task.execute();

    return result;
  }

}
