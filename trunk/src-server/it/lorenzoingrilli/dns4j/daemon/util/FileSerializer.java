/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.util;

import java.io.File;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.scalar.ScalarSerializer;

public class FileSerializer implements ScalarSerializer<File> {
	@Override
	public File read(String filename) throws YamlException {
		return new File(filename);
	}

	@Override
	public String write(File file) throws YamlException {
		return file.getAbsolutePath();
	}
}
