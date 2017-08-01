package me.tuke.sktuke.documentation;

import java.io.BufferedWriter;
import java.io.IOException;

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

	public abstract void write(BufferedWriter writer, AddonInfo addon) throws IOException;

}
