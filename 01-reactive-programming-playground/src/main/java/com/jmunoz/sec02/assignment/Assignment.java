package com.jmunoz.sec02.assignment;

import com.jmunoz.common.Util;

public class Assignment {

    public static void main(String[] args) {
        FileService fs = new FileServiceImpl();

        fs.read("comics.txt").subscribe(Util.subscriber());

        fs.write("deportes.txt", "Deportes preferidos:\n" +
                "Ajedrez\n" +
                "Baloncesto\n" +
                "Tenis").subscribe(Util.subscriber());

        fs.delete("deportes.txt")
                .subscribe(Util.subscriber());
    }
}
