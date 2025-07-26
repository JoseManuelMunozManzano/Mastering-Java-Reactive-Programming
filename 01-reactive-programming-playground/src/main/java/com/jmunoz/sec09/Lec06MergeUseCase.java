package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import com.jmunoz.sec09.helper.Kayak;

public class Lec06MergeUseCase {

    public static void main(String[] args) {

        Kayak.getFlights()
                .subscribe(Util.subscriber());

        Util.sleepSeconds(3);
    }
}
