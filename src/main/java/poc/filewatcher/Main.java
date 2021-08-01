package poc.filewatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.StringJoiner;

import static java.nio.file.StandardWatchEventKinds.*;

public class Main {
    public static void main(String[] args) throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        File watchmeDir = new File("watchme");
        if (!watchmeDir.exists()) {
            watchmeDir.mkdir();
        }
        Path dir = watchmeDir.toPath();

        //register a watch
        dir.register(watcher,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY);

        for (;;) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // The filename is the context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();
                Path child = dir.resolve(filename);
                StringJoiner stringJoiner = new StringJoiner(File.separator);

                for (int i = 0; i < child.getNameCount();  i++) {
                    stringJoiner.add(child.getName(i).toString());
                }
                System.out.println(stringJoiner + " " + kind.name());
            }

            // Reset the key -- this step is critical if you want to
            // receive further watch events.  If the key is no longer valid,
            // the directory is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}
