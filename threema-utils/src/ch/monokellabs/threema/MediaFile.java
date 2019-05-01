package ch.monokellabs.threema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.MicrosoftTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class MediaFile {

	private final File original;
	private final Message msg;
	private final File copy;

	public MediaFile(File dir, Message msg)
	{
		if (msg.media == null)
		{
			throw new IllegalArgumentException("Messages has no media: "+msg);
		}
		this.original = new File(dir, msg.media);
		File outDir = new File(original.getParentFile(), "out");
		outDir.mkdir();
		this.copy = new File(outDir, toCopyName(msg));
		this.msg = msg;
	}
	
	private static String toCopyName(Message msg)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
		String ext = StringUtils.substringAfterLast(msg.media, ".");
		return new StringBuilder()
				.append(df.format(msg.date))
				.append("_")
				.append(msg.sender)
				.append('.')
				.append(ext)
				.toString();
	}
	
	public boolean exists()
	{
		return original.exists();
	}
	
	public void setSentTimestamp()
	{
		FileTime newTime = FileTime.from(msg.date.getTime(), TimeUnit.MILLISECONDS);
		try {
			Files.setLastModifiedTime(copy.toPath(), newTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setMetaData()
	{
		try(OutputStream os = new FileOutputStream(copy))
		{
			if (copy.getName().endsWith(".jpg"))
			{
				
				TiffOutputSet set = new TiffOutputSet();
				TiffOutputDirectory exifDirectory = set.getOrCreateExifDirectory();
				exifDirectory.add(MicrosoftTagConstants.EXIF_TAG_XPAUTHOR, msg.sender);
				exifDirectory.add(MicrosoftTagConstants.EXIF_TAG_XPCOMMENT, "threema-utils");
				new ExifRewriter().updateExifMetadataLossless(original, os, set);
			}
			else
			{
				try(InputStream is = Files.newInputStream(original.toPath()))
				{ // copy unchanged
					IOUtils.copy(is, os);
				}
			}
		} catch (Exception ex)
		{
			throw new RuntimeException("failed to write meta data for "+original, ex);
		}
	}
	
	@Override
	public String toString() {
		return original.getName();
	}
	
}
