/*
 * Copyright (c) 1999-2012 weborganic systems pty. ltd.
 */
package org.pageseeder.aspose.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aspose.words.cloud.*;
import com.aspose.words.cloud.api.*;
import com.aspose.words.cloud.model.*;
import com.aspose.words.cloud.model.requests.*;
import com.aspose.words.cloud.model.responses.*;
import java.nio.file.Files;

/**
 * An ANT task to convert a DOCX file to PDF using Aspose cloud
 *
 * @author Philip Rutherford
 */
public final class AsposeWordsTask extends Task {

  /**
   * The Word document to convert.
   */
  private File source;

  /**
   * The PDF file to create.
   */
  private File destination;

  /**
   * The name of the working directory
   */
  private File working;

  /**
   * The configuration.
   */
  private File config;

  /**
   * List of parameters specified for the transformation into PSML
   */
  private List<Parameter> params = new ArrayList<>();

  /**
   * The Aspose cloud base URL
   */
  private String baseUrl = null;

  /**
   * The Aspose cloud client ID
   */
  private String clientId;

  /**
   * The Aspose cloud client secret
   */
  private String clientSecret;

  /**
   * Whether to update DOCX fields
   */
  private boolean updateFields = false;

  // Set properties
  // ----------------------------------------------------------------------------------------------

  /**
   * Set the source file (a DOCX file).
   *
   * @param docx The Word document (DOCX) to import.
   */
  public void setSrc(File docx) {
    if (!(docx.exists())) throw new BuildException("the document " + docx.getName()+ " doesn't exist");
    if (docx.isDirectory()) throw new BuildException("the document " + docx.getName() + " can't be a directory");
    String name = docx.getName();
    if (!name.endsWith(".docx")) {
      log("Word document file should generally end with .docx - but was "+name);
    }
    this.source = docx;
  }

  /**
   * Set the destination The PDF file to create.
   *
   * @param destination The destination file.
   */
  public void setDest(File destination) {
    this.destination = destination;
  }

  /**
   * @param url the Aspose cloud base URL.
   */
  public void setBaseUrl(String url) {
    this.baseUrl = url;
  }

  /**
   * @param id the Aspose cloud client ID.
   */
  public void setClientId(String id) {
    this.clientId = id;
  }

  /**
   * @param secret the Aspose cloud client secret.
   */
  public void setClientSecret(String secret) {
    this.clientSecret = secret;
  }

  /**
   * @param update whether to update DOCX fields
   */
  public void setUpdateFields(boolean update) {
    this.updateFields = update;
  }

  // Execute
  // ----------------------------------------------------------------------------------------------

  @Override
  public void execute() throws BuildException {
    if (this.source == null)
      throw new BuildException("Source document must be specified using 'src' attribute");
    if (this.destination == null)
      throw new BuildException("Destination document must be specified using 'dest' attribute");
    if (this.clientId == null)
      throw new BuildException("Client ID must be specified using 'clientid' attribute");
    if (this.clientSecret == null)
      throw new BuildException("Client secret must be specified using 'clientsecret' attribute");

    log("Converting DOCX " + this.source.getName() + " to PDF " + this.destination.getName());

    ApiClient apiClient = new ApiClient(this.clientId, this.clientSecret, this.baseUrl);
    WordsApi wordsApi = new WordsApi(apiClient);
    try {
      byte[] requestDocument = Files.readAllBytes(this.source.toPath());
      long temp_folder = System.nanoTime();
      if (this.updateFields) {
        String temp_result = temp_folder + "/" + this.source.getName();
        UpdateFieldsOnlineRequest request = new UpdateFieldsOnlineRequest(requestDocument,
            null, null, null, temp_result);
        UpdateFieldsOnlineResponse result = wordsApi.updateFieldsOnline(request);
        requestDocument = result.getDocument().get(temp_result);
      }
      PdfSaveOptionsData requestSaveOptionsData = new PdfSaveOptionsData();
      String temp_result = temp_folder + "/" + this.destination.getName();
      requestSaveOptionsData.setFileName(temp_result);
      OutlineOptionsData outlineOptions = new OutlineOptionsData();
      outlineOptions.headingsOutlineLevels(6);
      requestSaveOptionsData.outlineOptions(outlineOptions);
      SaveAsOnlineRequest request = new SaveAsOnlineRequest(requestDocument,
          requestSaveOptionsData,null, null,null,null);
      SaveAsOnlineResponse result = wordsApi.saveAsOnline(request);
      Files.write(this.destination.toPath(),result.getDocument().get(temp_result));
    } catch (IOException | MessagingException | ApiException ex) {
      throw new BuildException(ex);
    }

    log("Conversion complete");
  }

}
