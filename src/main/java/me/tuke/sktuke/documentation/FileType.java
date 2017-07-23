package me.tuke.sktuke.documentation;

import java.util.List;
import java.util.Map;

/**
 * @author Tuke_Nuke on 21/07/2017
 */
public abstract class FileType {

	private String extension;
	public FileType(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}

	public abstract String write(Map<String, Map<String, List<SyntaxInfo>>> docs);

}
