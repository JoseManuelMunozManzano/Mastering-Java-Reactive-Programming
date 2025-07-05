package com.jmunoz.sec04.assignment;

import com.jmunoz.common.Util;

import java.nio.file.Path;

public class Assignment {

    public static void main(String[] args) {

        Path path = Path.of("src/main/resources/sec04/comics.txt");

        FileReaderService fs = new FileReaderServiceImpl();
        fs.read(path)
//                .take(3)
                .subscribe(Util.subscriber());
    }
}
