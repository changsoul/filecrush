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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author changsoul.wu
 *
 */
public class App {

	static final byte B0X00 = 0x00;
	static final byte B0XFF = (byte) 0xFF;
	static final Random RAN = new Random();

	public static void main(String[] args) {
		System.out.println(Arrays.asList(args));

		if (args == null || args.length == 0) {

			System.out.println("==================no file or dir=======================");
			return;
		}

		System.out.println("==================write 0x00=======================");
		for (String path : args) {

			File file = new File(path);
			crush(file, B0X00);
		}

		System.out.println("==================write 0xFF=======================");
		for (String path : args) {

			File file = new File(path);
			crush(file, B0XFF);
		}

		System.out.println("==================write random=======================");
		for (String path : args) {

			File file = new File(path);
			crushRandom(file);
		}

		System.out.println("==================crush finish=======================");
	}

	static void crush(File file, byte b) {

		if (!file.exists()) {
			System.out.println(
					"==================file[" + file.getAbsolutePath() + "] not exists=======================");
			return;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			if (files == null)
				return;

			for (File f : files)
				crush(f, b);

		} else if (file.isFile()) {

			FileOutputStream out = null;
			try {
				long l = file.length();
				System.out.println("==================file length[" + l + "]=======================");
				out = new FileOutputStream(file);
				for (int i = 0; i < l; i++) {
					out.write(b);
				}
				out.flush();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	static void crushRandom(File file) {

		if (!file.exists()) {
			System.out.println(
					"==================file[" + file.getAbsolutePath() + "] not exists=======================");
			return;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			if (files == null)
				return;

			for (File f : files)
				crushRandom(f);

			file.delete();
		} else if (file.isFile()) {

			FileOutputStream out = null;
			try {
				long l = file.length();
				out = new FileOutputStream(file);
				for (int i = 0; i < l; i++) {
					out.write((byte) RAN.nextInt());
				}
				out.flush();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// file.delete();
		}
	}
}
