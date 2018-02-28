/* 
 * Copyright(c)2010-2018 WUDAOSOFT.COM
 * 
 * Email:changsoul.wu@gmail.com
 * 
 * QQ:275100589
 */

package com.wudaosoft.filecrush;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @author Changsoul Wu
 * 
 */
public class Runner implements Callable<String> {

	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	static final Random RAN = new Random();
	static final int BUFF_SIZE = 2048;

	private File file;
	private byte b;
	private boolean del = false;
	private long len = 0l;

	public Runner(File file, byte b, boolean del, long len) {
		this.file = file;
		this.b = b;
		this.del = del;
		this.len = len;
	}

	void genBuff(byte[] buff, final byte b) {
		for (int i = 0; i < BUFF_SIZE; i++) {
			buff[i] = b;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	public String call() throws Exception {
		BufferedOutputStream out = null;
		boolean ong = (b == App.B0X00 || b == App.B0XFF);
		try {
			long l = len > 0l ? len : file.length();
			out = new BufferedOutputStream(new FileOutputStream(file), BUFF_SIZE);
			
			int wrl = BUFF_SIZE;
			byte[] buff = new byte[BUFF_SIZE];
			genBuff(buff, b);

			for (long i = 0; i < l; i += wrl) {
				
				if(wrl > l)
					wrl = (int)l;
				
				long tl = l - i;
				
				if(wrl > tl)
					wrl = (int)tl;

				if (!ong)
					RAN.nextBytes(buff);
				
				out.write(buff, 0, wrl);
			}
			
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
		
		String rs = null;
		String byteStr = null;
		
		if(ong)
			byteStr = Integer.toHexString(b);
		else 
			byteStr = "random bytes";
		
		rs = String.format("write %s %s", byteStr, file.getAbsolutePath());

		if (del) {
			String parent = file.getParent();
			File o = new File(parent, UUID.randomUUID().toString() + ".fuck");
			File o1 = new File(parent, UUID.randomUUID().toString() + ".fuck");
			File o2 = new File(parent, UUID.randomUUID().toString() + ".fuck");

			rs += LINE_SEPARATOR + String.format("rename %s to %s", file.getAbsolutePath(), o.getAbsolutePath());
			file.renameTo(o);
			
			rs += LINE_SEPARATOR + String.format("rename %s to %s", o.getAbsolutePath(), o1.getAbsolutePath());
			o.renameTo(o1);
			
			rs += LINE_SEPARATOR + String.format("rename %s to %s", o1.getAbsolutePath(), o2.getAbsolutePath());
			o1.renameTo(o2);

			rs += LINE_SEPARATOR + String.format("delete %s", o2.getAbsolutePath());
			o2.delete();
		}
		
		return rs;
	}

}
