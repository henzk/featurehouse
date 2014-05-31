package fstcomp;

import static fstcomp.ComposerTestUtil.compose;
import static org.junit.Assert.assertEquals;

import integrationtests.Checksum;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class JavaFeatureAnnotationVarEncTest {

	@Test
	public void testFeatureAnnotatedVarEncComposition() throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("test/fstcomp/Java/GPL/model.cnf"));
		bufferedWriter.write("(Base)");
		bufferedWriter.close();

		String expression = "test/fstcomp/Java/GPL/GPLComp.features";
		String outputDir = "result/fstcomp/output/Java_GPL_GPLComp__FeaAnnVarEnc";
		
		compose(expression, outputDir, null, new String[] {"--liftJava", "--featureAnnotationJava"});
		
		assertEquals("E9292CBAD6EF0D6E732BE680E2E94F50", Checksum.calculateChecksum(new File(outputDir)));
		
		Scanner scan = new Scanner(new File("result/fstcomp/output/Java_GPL_GPLComp__FeaAnnVarEnc/GPLComp/GPL/Graph.java"));  
		scan.useDelimiter("\\Z");  
		String content = scan.next();
		
		System.out.println("## Graph.java ###" + content + "#####");
		
		scan = new Scanner(new File("result/fstcomp/output/Java_GPL_GPLComp__FeaAnnVarEnc/GPLComp/GPL/Vertex.java"));  
		scan.useDelimiter("\\Z");  
		content = scan.next();
		
		System.out.println("## Vertex.java ###" + content + "#####");
	
	}
}
