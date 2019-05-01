package ch.monokellabs.threema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatExport {

	private static Pattern MESSAGE = Pattern.compile("\\[[0-9]*/[0-9]*/[0-9]*, [0-9]*:[0-9]*\\].*");

	public static List<Message> getMediaMessages(File messagesTxt) throws IOException
	{
		List<String> messages = readMessagesAsString(messagesTxt);
		List<Message> mediaMsgs = messages.stream()
			.map(raw -> Message.parse(raw))
			.filter(msg -> msg.media != null)
			.collect(Collectors.toList());
		return mediaMsgs;
	}
	
	public static List<String> readMessagesAsString(File messagesTxt) throws IOException {
		verifyMessageFile(messagesTxt);
		
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

	private static void verifyMessageFile(File messagesTxt) {
		if (messagesTxt.isDirectory())
		{
			throw new IllegalArgumentException("Expected chat export .txt file, but got a directory "+messagesTxt);
		}
		if (!messagesTxt.exists())
		{
			throw new IllegalArgumentException("Expected an existing file but got "+messagesTxt);
		}
		if (!messagesTxt.getName().startsWith("messages-") || !messagesTxt.getName().endsWith(".txt"))
		{
			throw new IllegalArgumentException("Expected chat export file with name 'messages-MyChat.txt', but got a directory "+messagesTxt);
		}
	}
	
}