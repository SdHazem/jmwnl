/**
 * Java Multi WordNet Library (JMWNL)
 * See the documentation of the original JWNL and of its JMWNL extension for
copyright information.
 * This class file is contributed by:
 * University of Roma Tor Vergata.
 * Portions created by University of Roma Tor Vergata are Copyright (C)
2008.
 * All Rights Reserved.
 */

package it.uniroma2.art.jmwnl.princeton.file;

import it.uniroma2.art.jmwnl.ewn.JMWNL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import net.didion.jwnl.JWNLRuntimeException;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.file.DictionaryFile;
import net.didion.jwnl.dictionary.file.DictionaryFileType;
import net.didion.jwnl.princeton.file.AbstractPrincetonRandomAccessDictionaryFile;

/**
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 * @author Armando Stellato <stellato@info.uniroma2.it>
 * 
 * A <code>RandomAccessDictionaryFile</code> that accesses files named with Princeton's dictionary file naming convention.
 * The code of this class is a mere copy of <code>net.didion.jwnl.princeton.file.PrincetonChannelDictionaryFile</code>, with the sole exception of the
 * encoding reader, which parses the encoding parameter from the property file and use it to parse the Princeton data files
 */
public class EWNChannelDictionaryFile extends AbstractPrincetonRandomAccessDictionaryFile {
	/** The random-access file. */
	private CharBuffer _buffer = null;
	private FileChannel _channel = null;

	public EWNChannelDictionaryFile() {}

	public DictionaryFile newInstance(String path, POS pos, DictionaryFileType fileType) {
		return new EWNChannelDictionaryFile(path, pos, fileType);
	}

	public EWNChannelDictionaryFile(String path, POS pos, DictionaryFileType fileType) {
		super(path, pos, fileType);
	}

	public String readLine() throws IOException {
		if (isOpen()) {
			//The following lines gratuitously lifted from java.io.RandomAccessFile.readLine()
			StringBuffer input = new StringBuffer();
			char c = (char)-1;
			boolean eol = false;

			while (!eol) {
				c = _buffer.get((int)getFilePointer());
				_buffer.position((int)getFilePointer() + 1);

				switch (c) {
					case (char)-1:
					case '\n':
						eol = true;
						break;
					case '\r':
						eol = true;
						if ((_buffer.get((int)getFilePointer() + 1)) == '\n')
							_buffer.position((int)getFilePointer() + 1);
						break;
					default:
						input.append(c);
						break;
				}
			}
			return ((c == -1) && (input.length() == 0)) ? null : input.toString();
		} else {
			throw new JWNLRuntimeException("PRINCETON_EXCEPTION_001");
		}
	}

	public void seek(long pos) throws IOException {
		_buffer.position((int)pos);
	}

	public long getFilePointer() throws IOException {
		return (long)_buffer.position();
	}

	public boolean isOpen() {
		return _channel != null;
	}

	public void close() {
		try {
			_buffer = null;
			_channel.close();
		} catch (IOException ex) {
		} finally {
			_channel = null;
		}
	}

	protected void openFile(File file) throws IOException {
		_channel = new FileInputStream(file).getChannel();
		_buffer = Charset.forName(JMWNL.getEncoding()).newDecoder().decode(
					_channel.map(FileChannel.MapMode.READ_ONLY,0,_channel.size()));
	}

	public long length() throws IOException {
		// Do not use "_buffer.length()" because it returns the
		// buffer length, not the total length of the file
		return _channel.size();
	}

	public int read() throws IOException {
		return (int)_buffer.get();
	}
}