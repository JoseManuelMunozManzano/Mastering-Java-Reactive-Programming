package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import com.jmunoz.sec09.client.ExternalServiceClient;

public class Lec08ZipAssignment {

    public static void main(String[] args) {
        var client = new ExternalServiceClient();

        for (int i = 0; i <= 10; i++) {
            client.getProduct(i)
                    .subscribe(Util.subscriber());
        }

        Util.sleepSeconds(2);
    }
}
