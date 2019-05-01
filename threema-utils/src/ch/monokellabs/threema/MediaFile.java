package ch.monokellabs.threema;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

public class MediaFile {

	private final File original;
	private final Message msg;

	public MediaFile(File dir, Message msg)
	{
		if (msg.media == null)
		{
			throw new IllegalArgumentException("Messages has no media: "+msg);
		}
		this.original = new File(dir, msg.media);
		this.msg = msg;
	}
	
	public void setSentTimestamp()
	{
		FileTime newTime = FileTime.from(msg.date.getTime(), TimeUnit.MILLISECONDS);
		try {
			Files.setLastModifiedTime(original.toPath(), newTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
