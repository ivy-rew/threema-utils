package ch.monokellabs.threema;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TestChatImages {

	@Test
	public void parseChatMessagesFile() throws IOException
	{
		File messagesTxt = loadChatUsingZip();
		List<String> messages = ChatExport.readMessagesAsString(messagesTxt);
		List<Message> mediaMsgs = ChatExport.getMediaMessages(messagesTxt);
		Assertions.assertThat(messages).hasSize(4);
		Assertions.assertThat(mediaMsgs).hasSize(2);
	}

	private static File loadChatUsingZip() throws IOException {
		File threemaChatZip = new File(TestChatImages.class.getResource("myChat.zip").getFile());
		Path extractedChat = Files.createTempDirectory("myChat");
		Unzipper.unzip(threemaChatZip.toPath(), extractedChat);
		File messagesTxt = new File(extractedChat.toFile(), "messages-myChat.txt");
		return messagesTxt;
	}
	
	@Test
	public void writeImageMetaData() throws IOException
	{
		File messagesTxt = loadChatUsingZip();
		File chatUnderTest = messagesTxt.getParentFile();
		List<Message> mediaMsgs = ChatExport.getMediaMessages(messagesTxt);
		ChatExport.updateMedia(chatUnderTest, mediaMsgs);
		
		System.out.println(chatUnderTest);
		File rewritten = new File(chatUnderTest, "out/2019-03-14_18-16_Sandra_2e5874e5.jpg");
		Assertions.assertThat(rewritten).isFile().exists();
	}
	
	@Test
	public void parseMessage_imageWithText()
	{
		//   "[18/03/2019, 14:47] Sandra: Image: Was ist gelb? <dbc01cab-6282-44c5-b1dc-be3761c2db6f.jpg>",
		String msg = "[23/04/2019, 22:52] Sandra: Image: Hallo ich bin das kleine WIR und bin nach den Ferien auch im Chendi. <2e5874e5-c472-4fad-98bd-3109032010c5.jpg>";
		Message message = Message.parse(msg);
		Assertions.assertThat(message.date).isNotNull();
		System.out.println(message);
	}
	
	@Test
	public void parseMessage_imageWithText_multiLine() throws IOException
	{
		String chat = "[11/04/2019, 08:45] Sandra: Image: Gruppenarbeit:\n"
				+ "Was fehlt noch auf dem Plan? \n"
				+ "🤔Natürlich die Papas👷.Kinderhumor! <ebba11df-2400-4265-a6fb-6978bed4e3cf.jpg>\n"
				+ "[11/04/2019, 10:07] Me: 😍\n";
		File conversation = Files.createTempFile("messages-multiChat", ".txt").toFile();
		FileUtils.writeStringToFile(conversation, chat, StandardCharsets.UTF_8);
		
		List<String> msgs = ChatExport.readMessagesAsString(conversation);
		Assertions.assertThat(msgs).hasSize(2);
		
		String firstRaw = msgs.get(0);
		Assertions.assertThat(firstRaw).contains("<ebba11df-2400-4265-a6fb-6978bed4e3cf.jpg>");
		
		Message multiLine = Message.parse(firstRaw);
		Assertions.assertThat(multiLine.media)
			.isEqualTo("ebba11df-2400-4265-a6fb-6978bed4e3cf.jpg");
	}
	
}
