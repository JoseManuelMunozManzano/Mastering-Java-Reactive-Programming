package com.jmunoz.sec04.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

public class FileReaderServiceImpl implements FileReaderService {

    private static final Logger log = LoggerFactory.getLogger(FileReaderServiceImpl.class);

    @Override
    public Flux<String> read(Path path) {
        return Flux.generate(
                () -> openFile(path),
                this::readFile,
                this::closeFile
        );
    }

    private Scanner openFile(Path path) throws FileNotFoundException {
        log.info("Opening file: {}", path);
        return new Scanner(new File(path.toUri()));
    }

    private Scanner readFile(Scanner file, SynchronousSink<String> sink) {
        try {
            if (!file.hasNext()) {
                sink.complete();
            } else {
                sink.next(file.nextLine());
            }
            return file;
        } catch (Exception e) {
            sink.error(e);
        }

        return file;
    }

    private void closeFile(Scanner file) {
        log.info("Closing file");
        if (file != null) {
            file.close();
        }
    }
}
