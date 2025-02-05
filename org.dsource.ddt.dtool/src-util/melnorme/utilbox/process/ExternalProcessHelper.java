/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.process;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

import melnorme.utilbox.misc.ByteArrayOutputStreamExt;
import melnorme.utilbox.misc.ExceptionTrackingRunnable;
import melnorme.utilbox.misc.StreamUtil;

/**
 * Helper for running external processes.
 * Reads all stdout and stderr output into a byte array (using worker threads)
 * 
 * @see AbstractExternalProcessHelper
 */
public class ExternalProcessHelper extends AbstractExternalProcessHelper {
	
	protected ReadAllBytesTask mainReader;
	protected ReadAllBytesTask stderrReader;
	
	public ExternalProcessHelper(ProcessBuilder pb) throws IOException {
		super(pb);
	}
	
	public ExternalProcessHelper(Process process, boolean readStdErr, boolean startReaders) {
		super(process, readStdErr, startReaders);
	}
	
	@Override
	protected boolean isCanceled() {
		return false;
	}
	
	@Override
	protected ReadAllBytesTask createMainReaderTask() {
		return mainReader = new ReadAllBytesTask(process.getInputStream());
	}
	
	@Override
	protected ReadAllBytesTask createStdErrReaderTask() {
		return stderrReader = new ReadAllBytesTask(process.getErrorStream());
	}
	
	protected static class ReadAllBytesTask extends ExceptionTrackingRunnable<ByteArrayOutputStreamExt, IOException> {
		
		protected final InputStream is;
		protected final ByteArrayOutputStreamExt byteArray = new ByteArrayOutputStreamExt(32);
		
		public ReadAllBytesTask(InputStream is) {
			this.is = is;
		}
		
		@Override
		public ByteArrayOutputStreamExt doRun() throws IOException {
			// BM: Hum, should we treat an IOException not as an error, but just like an EOF?
			try {
				final int BUFFER_SIZE = 1024;
				byte[] buffer = new byte[BUFFER_SIZE];
				
				int read;
				while((read = is.read(buffer)) != StreamUtil.EOF) {
					byteArray.write(buffer, 0, read);
					notifyReadChunk(buffer, 0, read);
				}
				return byteArray;
			} finally {
				is.close();
			}
		}
		
		@SuppressWarnings("unused")
		protected void notifyReadChunk(byte[] buffer, int offset, int readCount) {
			// Default implementation: do nothing
		}
		
	}
	
	protected ByteArrayOutputStreamExt getStdOutBytes() {
		assertTrue(isFullyTerminated());
		return mainReader.byteArray;
	}
	
	protected ByteArrayOutputStreamExt getStdErrBytes() {
		assertTrue(isFullyTerminated());
		assertTrue(readStdErr);
		return stderrReader.byteArray;
	}
	
	public void tryStrictAwaitTermination(int timeoutMs) throws InterruptedException, TimeoutException, IOException {
		awaitTermination(timeoutMs);
		mainReader.getResult();
		stderrReader.getResult();
	}
	
	/** 
	 * Awaits for successful process termination, as well as successful termination of reader threads,
	 * throws an exception otherwise (and destroys the process).
	 * @return the process result encapsulated in data class {@link ExternalProcessResult}. 
	 * @throws InterruptedException if interrupted
	 * @throws TimeoutException if timeout occurs, or cancel requested.
	 * @throws IOException if an IO error occured in the reader threads.
	 */
	public ExternalProcessResult strictAwaitTermination(int timeoutMs) 
			throws InterruptedException, TimeoutException, IOException {
		try {
			tryStrictAwaitTermination(timeoutMs);
		} catch (Exception e) {
			process.destroy();
			throw e;
		}
		return new ExternalProcessResult(process.exitValue(), getStdOutBytes(), getStdErrBytes());
	}
	
	public ExternalProcessResult strictAwaitTermination() throws InterruptedException, TimeoutException, IOException {
		return strictAwaitTermination(NO_TIMEOUT);
	}
	
	public class ExternalProcessResult {
		
		public final int exitValue;
		public final ByteArrayOutputStreamExt stdout;
		public final ByteArrayOutputStreamExt stderr;
		
		public ExternalProcessResult(int exitValue, ByteArrayOutputStreamExt stdout, ByteArrayOutputStreamExt stderr) {
			this.exitValue = exitValue;
			this.stdout = stdout;
			this.stderr = stderr;
		}
		
		public ByteArrayOutputStreamExt getStdOutBytes() {
			return stdout;
		}
		
		public ByteArrayOutputStreamExt getStdErrBytes() {
			return stderr;
		}
		
	}
	
	/* ----------------- writing helpers ----------------- */
	
	public void writeInput(String input, Charset charset) throws IOException {
		if(input == null)
			return;
		
		OutputStream processInputStream = getProcess().getOutputStream();
		StreamUtil.writeStringToStream(input, processInputStream, charset);
	}
	
}