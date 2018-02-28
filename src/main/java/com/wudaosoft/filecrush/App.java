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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.wudaosoft.filecrush.env.ApplicationArguments;
import com.wudaosoft.filecrush.env.DefaultApplicationArguments;

/**
 * 
 * @author changsoul.wu
 *
 */
public class App {

	static final ExecutorService executorService = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public static final byte B0X00 = 0x00;
	public static final byte B0XFF = (byte) 0xff;
	public static final byte BRAN = (byte) 0x20;

	static final List<Future<String>> tasks = new ArrayList<Future<String>>(3000);

	public static void main(String[] args) {

		ApplicationArguments arguments = new DefaultApplicationArguments(args);

		List<String> paths = arguments.getNonOptionArgs();
		boolean force = arguments.containsOption("force");

		System.out.println(paths);
		System.out.println("force: " + force);

		if (paths == null || paths.isEmpty()) {
			System.out.println("==================no file or dir=======================");
			return;
		}

		System.out.println("==================write 0x00=======================");

		for (String path : paths) {

			File file = new File(path);
			crush(file, B0X00, false);
		}

		printTasks();

		System.out.println("==================write 0xFF=======================");
		for (String path : paths) {

			File file = new File(path);
			crush(file, B0XFF, false);
		}

		printTasks();

		System.out.println("==================write random=======================");
		for (String path : paths) {

			File file = new File(path);
			crush(file, BRAN, true);
		}

		printTasks();

		if (force) {
			
			File up = new File(paths.get(0));
			long len = up.getFreeSpace() - 1024l * 10;

			if (len > 1024l) {
				String parent = up.isDirectory() ? up.getAbsolutePath() : up.getParent();
				File file = new File(parent, UUID.randomUUID().toString() + ".fuck-you");

				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				crush(file, B0X00, false, len);
				printTasks();
				crush(file, B0XFF, false, len);
				printTasks();
				crush(file, BRAN, true, len);
				printTasks();
			}
		}

		System.out.println("==================crush dir=======================");
		for (String path : paths) {

			File file = new File(path);
			crushDir(file);
		}

		System.out.println("==================crush finish=======================");
	}

	static void printTasks() {
		for (Future<String> f : tasks) {
			try {
				System.out.println(f.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		tasks.clear();
	}

	static void crush(File file, byte b, boolean del) {

		crush(file, b, del, 0);
	}

	static void crush(File file, byte b, boolean del, long len) {

		if (!file.exists()) {
			System.out.println(
					"==================file[" + file.getAbsolutePath() + "] do not exists=======================");
			return;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			if (files == null || files.length == 0)
				return;

			for (File f : files)
				crush(f, b, del);

		} else if (file.isFile()) {

			try {
				tasks.add(executorService.submit(new Runner(file, b, del, len)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static void crushDir(File file) {

		if (!file.isDirectory() || file.getParent() == null) {
			return;
		}

		File[] files = file.listFiles();

		if (files != null && files.length >= 0) {
			for (File f : files)
				crushDir(f);
		}

		boolean rs = file.delete();

		System.out.println(String.format("delete %s %s", file.getAbsolutePath(), rs));
	}

}
