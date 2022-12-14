package util;

import com.jcraft.jsch.SftpProgressMonitor;

import javax.swing.*;

/**
 * Class used to log file transfer progress when debugMode is true (enabled)
 * for @SftpClient
 **/
public class SftpFileTransferProgressMonitor implements SftpProgressMonitor {

	private String source = null;
	private String destination = null;
	private long totalBytesTransfered = -1;
	private long fileSize = -1;

	public SftpFileTransferProgressMonitor() {

	}

	public SftpFileTransferProgressMonitor(String source, String destination, long fileSize) {

		this.source = source;
		this.destination = destination;
		this.fileSize = fileSize;
	}

	@Override
	public void init(int op, String src, String dest, long max) {
		String operation = (op == SftpProgressMonitor.PUT) ? "UPLOAD" : "DOWNLOAD";
		System.out.println("Begin " + operation + " from " + this.source + " to " + this.destination);

	}

	@Override
	public void end() {

		if (this.fileSize > 0) {

			System.out.println("Transfer finished. Successfully transfered " + this.totalBytesTransfered + " from "
					+ this.fileSize);
		} else {

			System.out.println("Transfer finished. Successfully transfered " + this.totalBytesTransfered);
		}

	}

	@Override
	public boolean count(long count) {

		this.totalBytesTransfered += count;
		/*
		 * if the operation is UPLOAD, we know what is the file size of the file to be
		 * uploaded, so we log the total transfered bytes as a percentage from the whole
		 * file size
		 */

		if (this.fileSize > 0) {
			float transferedPercentage = (float) (this.totalBytesTransfered) / (this.fileSize) * 100.0f;

			System.out.println(Math.ceil(transferedPercentage) + " % uploaded.");
		} else {
			/*
			 * if the operation is DOWNLOAD, we do not know the file size of the file to be
			 * downloaded, so we log only the total bytes transfered from the start of the
			 * operation up to the current moment
			 */
			System.out.println(this.totalBytesTransfered + " bytes downloaded overall.");

		}
		return true;
	}

	public void setTransferMetadata(String source, String destination, long fileSize) {

		this.source = source;
		this.destination = destination;
		this.fileSize = fileSize;
	}
}