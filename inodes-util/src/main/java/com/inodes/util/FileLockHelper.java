/**
 * 
 */
package com.inodes.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * @author mhapanowicz
 *
 */
public class FileLockHelper {

	private File file;
	
	public FileLockHelper(File file) {
		this.file = file;
	}
	
	public void lock(long millis) throws OverlappingFileLockException, IOException {
		if(!file.exists()) {
			file.createNewFile();
		}
		@SuppressWarnings("resource")
		final FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				try {
					@SuppressWarnings("unused")
					final FileLock lock = channel.lock();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
		t.start();
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(t.isAlive()) {
			throw new OverlappingFileLockException();
		}
	}
	
}
