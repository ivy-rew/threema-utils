package ch.monokellabs.threema;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Message
{
	public static Message parse(String raw)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy, hh:mm");
		Date date = df.parse(raw, new ParsePosition(1));
		
		String noDate = StringUtils.substringAfter(raw, "] ");
		String sender = StringUtils.substringBefore(noDate, ":");
		
		String noSender = StringUtils.substringAfter(noDate, ": ");
		String media = null;
		if (noSender.startsWith("Image"))
		{
			noSender = StringUtils.substringAfter(noSender, "Image");
			media = StringUtils.substringBetween(noSender, "<", ">");
		}
		else if (noSender.startsWith("Video"))
		{
			
		}
		return new Message(date, sender, media);
	}

	public final Date date;
	public final String sender;
	public final String media;
	
	private Message(Date sent, String sender, String media)
	{
		this.date = sent; 
		this.sender = sender;
		this.media = media;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}