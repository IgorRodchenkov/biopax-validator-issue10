package org.fileGeneration.biopax;

import org.biopax.validator.BiopaxIdentifier;
import org.biopax.validator.api.Validator;
import org.biopax.validator.api.ValidatorException;
import org.biopax.validator.api.ValidatorUtils;
import org.biopax.validator.api.beans.Validation;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public final class Biopax {

  public static void main(String ...args) throws Exception
  {
    //the first arg. should be absolute path to the biopax data directory
    List<Path> biopaxFiles = dataFiles(args[0]);

    //Initialization and loading bio ontologies can take quite a few minutes
    ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
      "META-INF/spring/appContext-validator.xml",
      "META-INF/spring/appContext-loadTimeWeaving.xml");
    final Validator validator = (Validator) ctx.getBean("biopaxValidator");

    for(Path path : biopaxFiles) {
      try {
        validate(validator, path);
      } catch (ValidatorException e) {
        System.out.println(String.format("Could not validate %s due to: %s", path, e));
      }
    }
  }

  private static void validate(Validator validator, Path biopaxFilePath)
    throws IOException
  {
    System.out.println("Processing: " + biopaxFilePath);
    // define a new  validation result for the input data
    Validation result = new Validation(new BiopaxIdentifier(), biopaxFilePath.toString(),
      false, null, 0, "notstrict");
    InputStream is = Files.newInputStream(biopaxFilePath);
    if(is==null)
      throw new IOException("Null input stream of: " + biopaxFilePath);
    validator.importModel(result, is);
    is.close();
    validator.validate(result);
    result.setModel(null);
    result.setModelData(null);

    // Save the validation results
    PrintWriter writer = new PrintWriter(biopaxFilePath.toString() + "_validation.xml");
    ValidatorUtils.write(result, writer, null);
    writer.close();

    //cleanup between files (though validator could instead check several resources and then write one report for all)
    validator.getResults().remove(result);
  }

  private static List<Path> dataFiles(String directory) throws IOException {
    List<Path> files = new ArrayList<>();
    Files.newDirectoryStream(Paths.get(directory), path->path.toString().endsWith(".owl"))
      .forEach(f->files.add(f));
    return files;
  }

}
