package ch.monokellabs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TestChatImages {

	private static Pattern MESSAGE = Pattern.compile("\\[[0-9]*/[0-9]*/[0-9]*, [0-9]*:[0-9]*\\].*");

	@Test
	public void readImageDates() throws IOException
	{
		File messagesTxt = new File("target/homeEducation/messages-Home Education.txt");
		List<String> messages = readMessagesAsString(messagesTxt);
		List<Message> mediaMsgs = messages.stream()
			.map(raw -> Message.parse(raw))
			.filter(msg -> msg.media != null)
			.collect(Collectors.toList());
		Assertions.assertThat(messages).hasSize(572);
		Assertions.assertThat(mediaMsgs).hasSize(364);
		
		mediaMsgs.forEach(message -> {
			File media = new File(messagesTxt.getParentFile(), message.media);
			FileTime newTime = FileTime.from(message.date.getTime(), TimeUnit.MILLISECONDS);
			try {
				Files.setLastModifiedTime(media.toPath(), newTime);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				ImageInfo info = Imaging.getImageInfo(media);
				Imaging.getMetadata(media);
			} catch (ImageReadException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}
	
	@Test
	public void parseMessage()
	{
		//   "[18/03/2019, 14:47] Sandra: Image: Was ist gelb? <dbc01cab-6282-44c5-b1dc-be3761c2db6f.jpg>",
		String msg = "[23/04/2019, 22:52] Sandra: Image: Hallo ich bin das kleine WIR und bin nach den Ferien auch im Chendi. <2e5874e5-c472-4fad-98bd-3109032010c5.jpg>";
		Message message = Message.parse(msg);
		Assertions.assertThat(message.date).isNotNull();
		System.out.println(message);
	}
	
	public static class Message
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

	private static List<String> readMessagesAsString(File messagesTxt) throws IOException {
		List<String> messages = new ArrayList<>();
		try(InputStream is = Files.newInputStream(messagesTxt.toPath());
			Scanner scanner = new Scanner(is))
		{
			;
			while(scanner.hasNext())
			{
				String match = scanner.findInLine(MESSAGE);
				if (match != null)
				{
					messages.add(match);
				}
				scanner.nextLine();
			}
		}
		return messages;
	}
	
}
