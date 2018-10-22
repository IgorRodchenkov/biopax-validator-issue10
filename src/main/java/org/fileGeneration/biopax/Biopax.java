package org.fileGeneration.biopax;

import org.biopax.validator.api.Validator;
import org.biopax.validator.api.ValidatorUtils;
import org.biopax.validator.api.beans.Validation;
import org.biopax.validator.impl.IdentifierImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Biopax {
  static ApplicationContext ctx;
  static boolean autofix = false;
  static int maxErrors = 0;
  static String profile = "notstrict";
  static String outFormat = "xml";
  static Validator validator;

  static {
    System.out.println("Init BioPAX Validator...");
    // Biopax validation requires loading of bio ontologies, which takes a few minutes
    ctx = new ClassPathXmlApplicationContext(new String[]{
      "META-INF/spring/appContext-validator.xml",
      "META-INF/spring/appContext-loadTimeWeaving.xml"
    });
    validator = (Validator) ctx.getBean("validator");
  }

  public static void main(String ...args) throws Exception {
    //the first arg is a path to biopax owl files to validate
    execute(args[0]);
  }

  public static void execute(String directory) throws Exception {
    //TODO: rename orig. owl files; validate them; generate output file names and write the result
    Files.newDirectoryStream(Paths.get(directory),
      path -> path.toString().endsWith(".owl"))
      .forEach(f -> {
        try {
          String spath = f.toFile().getPath();
          System.out.println("\tValidating file:" + spath);
          validate(ctx.getResource("file:" + spath));
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
  }

  // Function taken from the Biopax validator project, largely imitating their own 'main' function
  // but leaving out much that we don't need for this
  static void validate(Resource owlResource) throws IOException {
    // define a new  validation result for the input data
    Validation result = new Validation(new IdentifierImpl(), owlResource.getDescription(), autofix, null, maxErrors, profile);
    result.setDescription(owlResource.getDescription());

    validator.importModel(result, owlResource.getInputStream());
    validator.validate(result);
    result.setModel(null);
    result.setModelData(null);

    // Save the validation results
    PrintWriter writer = new PrintWriter(owlResource.getFile().getPath() + "_validation." + outFormat);
//    Source xsltSrc = (outFormat.equalsIgnoreCase("html"))
//      ? new StreamSource(ctx.getResource("classpath:html-result.xsl").getInputStream())
//        : null;
//    ValidatorUtils.write(result, writer, xsltSrc);
    ValidatorUtils.write(result, writer, null);
    writer.close();

    //cleanup between files (though validator could instead check several resources and then write one report for all)
    validator.getResults().remove(result);
  }

}
