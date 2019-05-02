package ch.monokellabs.threema;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatExport {

	private static Pattern MESSAGE = Pattern.compile("\\[[0-9]*/[0-9]*/[0-9]*, [0-9]*:[0-9]*\\][^\\[]*", Pattern.DOTALL);

	public static void main(String[] args)
	{
		File chatDir = new File(System.getProperty("user.dir"));
		System.out.println("updating chat in "+chatDir);
		
		File conversation = getConversationOf(chatDir);
		System.out.println("read messages in "+conversation);
		
		try {
			List<Message> mediaMsg = getMediaMessages(conversation);
			System.out.println("Found "+mediaMsg.size()+" media files to enrich.");
			updateMedia(conversation.getParentFile(), mediaMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("done: visit '"+chatDir+"/out' to examine results");
	}

	private static File getConversationOf(File chatDir) {
		File[] foundFiles = chatDir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith("messages-");
		    }
		});
		if (foundFiles.length == 0)
		{
			throw new IllegalArgumentException("no messages file found (e.g. messages-MyChat.txt");
		}
		File conversation = foundFiles[0];
		return conversation;
	}
	
	public static void updateMedia(File chatDir, List<Message> messages)
	{
		int[] update = {0};
		messages.forEach(message -> {
			MediaFile media = new MediaFile(chatDir, message);
			System.out.println("update "+media);
			if (!media.exists())
			{
				System.err.println("Skipping not existing media "+media);
				return;
			}
			update[0] = update[0]+1;
			media.setMetaData();
			media.setSentTimestamp();
		});
		System.out.println("updated "+update[0]+ "media files");
	}
	
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
			while(scanner.hasNext())
			{
				String match = scanner.findWithinHorizon(MESSAGE, 2000);
				if (match != null)
				{
					messages.add(match);
				}
				scanner.hasNext();
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
