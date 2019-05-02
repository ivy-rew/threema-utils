# Threema Utilities
Utilities for the seriously secure messenger Threema

## Image Meta-Data
Gives meaning to images exported from a chat via the official share commands: https://threema.ch/es/faq/chatexport.

The functionality to export a complete chat includings its conversation and media files is great. But once you need to archive these images in a gallery you'll soon notice the limitations of the exports.
The images do not have any reasonable meta data that allow mental mappings:

 - file name is a unique id. E.g. `2e5874e5-c472-4fad-98bd-3109032010c5.jpg`
 - images have no meta, so we do not know *who* posted it and more important to me, *when* it was posted.

The chat exporter analyses the conversation files of the exported chat to re-construct this important data. The output will be a copy of the media files with re-constructed sent date and the author occuring in the name. E.g. `2018-07-24_20-26_Henry_c1fe05f3.jpg`

### How to run
1. Download the Threema-Utlis binary JAR from the [release site](https://github.com/ivy-rew/threema-utils/releases). And install a Java Runtime Environment.
2. Create a Chat.zip using Threemas official export functionality: https://threema.ch/es/faq/chatexport
3. Transfer the ZIP to your computer and unpack it.
4. Run the image within the directory containing the extracted chat.
`java -jar qualifiedPathTo/threema-utils-0.0.1-SNAPSHOT-jar-with-dependencies.jar`
5. Verify and love the media in the created `out` sub directory. 

### Limitations
Possably many. This is just a damn fast approach to re-collect the history of a years chat containing media of much value to me. But I'll share it so that you might profit from it or drive the approach further.
