package org.oddjob.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.io.FilesType;
import org.oddjob.OurDirs;

/**
 * Processes reference and user guide to load the examples from file.
 * 
 * @author rob
 *
 */
public class PostProcessDocs {
	private static final Logger logger = 
		LoggerFactory.getLogger(PostProcessDocs.class);

	OurDirs dirs;
	
	public PostProcessDocs() {
		dirs = new OurDirs();		
	}
	
	public void postProcessUserGuideDocs() throws IOException {
		
		File dir = dirs.relative("build/docs/userguide");
		
		postProcess(dir);		
	}
	
	public void postProcessDevGuideDocs() throws IOException {
		
		File dir = dirs.relative("build/docs/devguide");
		
		postProcess(dir);		
	}
	
	void postProcess(File dir) throws IOException {
		
		if (!dir.exists()) {
			throw new FileNotFoundException(dir.getAbsolutePath());
		}

		DocPostProcessor processor = new DocPostProcessor();
		processor.setBaseDir(dirs.base());
		
		FilesType files = new FilesType();
		files.setFiles(dir.getPath() + "/*.html");
		
		File[] htmls = files.toFiles();
		
		for (File html : htmls)  {
			
			logger.info("Processing " + html);
			
			File tmp = new File(html + ".tmp");
			
			processor.setInput(new FileInputStream(html));
			processor.setOutput(new FileOutputStream(tmp));
			
			processor.run();
			
			html.delete();
			tmp.renameTo(html);
		}
	}
	
	public static void main(String[] args) throws IOException {
		PostProcessDocs processor = new PostProcessDocs();
		
		processor.postProcessUserGuideDocs();
		processor.postProcessDevGuideDocs();
	}
}
