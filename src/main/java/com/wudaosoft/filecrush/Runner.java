/**
 *    Copyright 2009-2018 Wudao Software Studio(wudaosoft.com)
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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

	static final int BUFF_SIZE = 2048;

	private final Random ran = new Random();

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

	@Override
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

				long tl = l - i;

				if (wrl > tl)
					wrl = (int) tl;

				if (!ong)
					ran.nextBytes(buff);

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

		if (ong) {
			byteStr = Integer.toHexString(b & 0xff);
			byteStr = byteStr.length() == 1 ? "0x0" + byteStr : "0x" + byteStr;
		} else
			byteStr = "random bytes";

		rs = String.format("write %s %s", byteStr, file.getAbsolutePath().replace('%', '_'));

		if (del) {
			String parent = file.getParent();
			File o = new File(parent, UUID.randomUUID().toString() + ".fuck");
			File o1 = new File(parent, UUID.randomUUID().toString() + ".fuck");
			File o2 = new File(parent, UUID.randomUUID().toString() + ".fuck");

			rs += LINE_SEPARATOR + String.format("rename %s to %s", file.getAbsolutePath().replace('%', '_'), o.getAbsolutePath().replace('%', '_'));
			file.renameTo(o);

			rs += LINE_SEPARATOR + String.format("rename %s to %s", o.getAbsolutePath().replace('%', '_'), o1.getAbsolutePath().replace('%', '_'));
			o.renameTo(o1);

			rs += LINE_SEPARATOR + String.format("rename %s to %s", o1.getAbsolutePath().replace('%', '_'), o2.getAbsolutePath().replace('%', '_'));
			o1.renameTo(o2);

			rs += LINE_SEPARATOR + String.format("delete %s %s", o2.getAbsolutePath().replace('%', '_'), o2.delete());

		}

		return rs;
	}

}
