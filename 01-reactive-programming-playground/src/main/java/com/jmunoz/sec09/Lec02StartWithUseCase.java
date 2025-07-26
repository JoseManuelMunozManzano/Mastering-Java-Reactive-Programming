package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import com.jmunoz.sec09.helper.NameGenerator;

public class Lec02StartWithUseCase {

    public static void main(String[] args) {
        var nameGenerator = new NameGenerator();

        // Vamos a tener varios subscribers.
        // Notar que en NameGenerator hemos usado una caché para almacenar los nombres generados,
        // ya que es un proceso que consume mucho tiempo.

        nameGenerator.generateNames()
                .take(2) // Tomamos solo los primeros 2 nombres generados
                .subscribe(Util.subscriber("sam"));

        nameGenerator.generateNames()
                .take(2) // Tomamos solo los primeros 2 nombres generados
                .subscribe(Util.subscriber("mike"));

        nameGenerator.generateNames()
                .take(3) // Tomamos solo los primeros 3 nombres generados
                .subscribe(Util.subscriber("jake"));
    }
}
