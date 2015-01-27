/*
 * Copyright (c) 2008, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 *
 * -----------------------------------------------------------------
 *
 * MoteProgrammer
 *
 * Authors : Joakim Eriksson, Niclas Finne
 * Created : 10 jul 2008
 * Updated : $Date: 2010/11/03 14:53:05 $
 *           $Revision: 1.1 $
 */

package collect;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class MoteProgrammer {

	private MoteProgrammerProcess[] processes;
	private String[] motes;
	private String firmwareFile;

	private boolean isDone;

	public MoteProgrammer() {
	}

	public boolean hasMotes() {
		return motes != null && motes.length > 0;
	}

	public String[] getMotes() {
		return motes;
	}

	public void setMotes(String[] motes) {
		this.motes = motes;
	}

	public void searchForMotes() throws IOException {
		MoteFinder finder = new MoteFinder();
		motes = finder.getMotes();
		finder.close();
	}

	public String getFirmwareFile() {
		return firmwareFile;
	}

	public void setFirmwareFile(String firmwareFile) {
		this.firmwareFile = firmwareFile;
	}

	public void programMotes() throws IOException {
		if (firmwareFile == null) {
			throw new IllegalStateException("no firmware");
		}
		if (!hasMotes()) {
			throw new IllegalStateException("no motes");
		}
		File fp = new File(firmwareFile);
		if (!fp.canRead()) {
			throw new IllegalStateException("can not read firmware file '"
					+ fp.getAbsolutePath() + '\'');
		}

		processes = new MoteProgrammerProcess[motes.length];
		isDone = false;
		try {
			log("Programming " + motes.length + " motes with '" + firmwareFile
					+ '\'', null);
			for (int i = 0, n = processes.length; i < n; i++) {
				processes[i] = new MoteProgrammerProcess(motes[i], firmwareFile) {
					protected void logLine(String line, boolean stderr,
							Throwable e) {
						if (!handleLogLine(this, line, stderr, e)) {
							super.logLine(line, stderr, e);
						}
					}

					protected void processEnded() {
						handleProcessEnded(this);
					}
				};
				processes[i].start();
			}
		} catch (Exception e) {
			throw (IOException) new IOException("Failed to program motes")
					.initCause(e);
		}
	}

	public synchronized void waitForProcess() throws InterruptedException {
		while (!isDone) {
			wait();
		}
	}

	public void close() {
		MoteProgrammerProcess[] processes = this.processes;
		if (processes != null) {
			this.processes = null;
			for (int i = 0, n = processes.length; i < n; i++) {
				if (processes[i] != null) {
					processes[i].stop();
				}
			}
		}

		isDone = true;
		synchronized (this) {
			notifyAll();
		}
	}

	protected void handleProcessEnded(MoteProgrammerProcess process) {
		// Another process has finished
		log("Mote@" + process.getMoteID() + "> finished"
				+ (process.hasError() ? " with errors" : ""), null);
		MoteProgrammerProcess[] processes = this.processes;
		if (processes != null) {
			int running = 0;
			int errors = 0;
			for (MoteProgrammerProcess p : processes) {
				if (p.isRunning()) {
					running++;
				} else if (p.hasError()) {
					errors++;
				}
			}
			if (running == 0) {
				// All processes has finished
				isDone = true;
				final String doneMessage = "Programming finished with "
						+ errors + " errors.";
				log(doneMessage, null);

				synchronized (this) {
					notifyAll();
				}
			}
		}
	}

	protected boolean handleLogLine(
			MoteProgrammerProcess moteProgrammerProcess, String line,
			boolean stderr, final Throwable e) {
		log("Mote@" + moteProgrammerProcess.getMoteID() + "> " + line, e);
		return true;
	}

	private void log(String line, final Throwable e) {
		System.err.println(line);
		if (e != null) {
			e.printStackTrace();
			line += "\n  " + e;
		}
		final String text = line;
		System.out.println(text);
	}

	public static void main(String[] args) throws IOException {
		MoteProgrammer mp = new MoteProgrammer();
		if (args.length < 1 || args.length > 2) {
			System.err.println("Usage: MoteProgrammer <firmware> [mote]");
			System.exit(1);
		}
		mp.setFirmwareFile(args[0]);
		if (args.length == 2) {
			mp.setMotes(new String[] { args[1] });
		} else {
			mp.searchForMotes();
		}
		if (!mp.hasMotes()) {
			System.err.println("No motes connected");
			System.exit(1);
		}
		mp.programMotes();
	}

}
