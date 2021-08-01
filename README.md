# filewatcher-poc
A POC for the nio WatchService API

This code is a quick adaption of Oracle documentation originally posted [here](https://docs.oracle.com/javase/tutorial/essential/io/notification.html)

# Run through
At the root of this gradle project there is a folder called 'watchme'.  The code sets up a watcher on this directory.
```java
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
```

At the start the only thing that exists is a folder called '1' and a file called 'Extisting.txt'

```console
watchme\1
watchme\Existing.txt
```

## Modify the 'Existing.txt' file
This was actually quite interesting because I modified the file within the IDE, so with the auto-save feature turned on I got.

```console
watchme\Existing.txt~ ENTRY_CREATE
watchme\Existing.txt~ ENTRY_MODIFY
watchme\Existing.txt ENTRY_MODIFY
watchme\Existing.txt~ ENTRY_DELETE
```
Obviously the filenames ending with tildes are temporary files which are then deleted.

## Add a sub-directory to the root 'watchme' folder
A listing would now look like this
```console
watchme\1
watchme\1\1.2
watchme\Existing.txt
```
The output simply reports that the subdirectory '1' was modified

```console
watchme\1 ENTRY_MODIFY
```

## Adding a directory to the root 'watchme' folder
A listing would now look like this
```console
watchme\1
watchme\1\1.2
watchme\2
watchme\Existing.txt
```

This is reported as a create action on '2'
watchme\2 ENTRY_CREATE

## Adding a file to the '2' subdirectory
A listing would now look like this
```console
watchme\1
watchme\1\1.2
watchme\2
watchme\2\2.txt
watchme\Existing.txt
```
This is reported as a modify action on '2'

```console
watchme\2 ENTRY_MODIFY
```

## Adding a subdirectory to a subdirectory
A listing would now look like this
```console
watchme\1
watchme\1\1.2
watchme\1\1.2\1.2.1
watchme\2
watchme\2\2.txt
watchme\Existing.txt
```

No Change is reported.

## Adding a file to a subdirectory of a subdirectory
A listing would now look like this
```console
watchme\1
watchme\1\1.2
watchme\1\1.2\1.2.1\
watchme\1\1.2\1.2.1\1.2.1.txt
watchme\2
watchme\2\2.txt
watchme\Existing.txt
```

No change is detected

## Adding a new file to the root 'watchme' directory
A listing would now look like this
```console
watchme\1
watchme\1\1.2
watchme\1\1.2\1.2.1\
watchme\1\1.2\1.2.1\1.2.1.txt
watchme\2
watchme\2\2.txt
watchme\Existing.txt
watchme\new.txt
```

This is of course reported as a create action on the file added.
```console
watchme\new.txt ENTRY_CREATE
```

# Conclusion
Oracles [documentation](https://docs.oracle.com/javase/tutorial/essential/io/notification.html) indicates this is suitable technology for a file browsing application like an IDE.

It is, but you have to:
1. Register a watcher for every directory under the directory being watched to detect changes in files or subdirectories
2. Manage all these watchers