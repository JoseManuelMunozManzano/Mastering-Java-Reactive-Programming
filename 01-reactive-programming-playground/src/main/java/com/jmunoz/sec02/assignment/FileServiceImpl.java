package com.jmunoz.sec02.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileServiceImpl implements FileService {

    public static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    private static final Path PATH = Path.of("src/main/resources/sec02/");

    @Override
    public Mono<String> read(String filename) {
        return Mono.fromCallable(() -> Files.readString(PATH.resolve(filename))).doOnError(e -> log.error(e.toString()));
    }

    @Override
    public Mono<Void> write(String filename, String content) {
        // Indicamos completed cuando la tarea se hace.
        // No queremos hacer el trabajo por adelantado y luego devolver empty.
        // Por eso usamos Mono.fromRunnable
        return Mono.fromRunnable(() -> writeFile(filename, content));

        // Esta es otra forma que a mi me ha funcionado.
        // return Mono.fromCallable(() -> Files.write(PATH.resolve(filename), content.getBytes())).doOnError(e -> log.error(e.toString())).then();
    }

    @Override
    public Mono<Void> delete(String filename) {
        return Mono.fromRunnable(() -> deleteFile(filename));

        // Esta es otra forma que a mi me ha funcionado.
//        return Mono.fromCallable(() -> {
//            try {
//                Files.delete(PATH.resolve(filename));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            return null;
//        });
    }

    private void writeFile(String filename, String content) {
        try {
            Files.writeString(PATH.resolve(filename), content);
            log.info("created {}", filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteFile(String filename) {
        try {
            Files.delete(PATH.resolve(filename));
            log.info("deleted {}", filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
