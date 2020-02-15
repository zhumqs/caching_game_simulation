package com.zhumqs.thread;

import java.util.Random;

/**
 * @author mingqizhu
 * @date 20191201
 */
public class Requestor implements Runnable{

    private Resource res;

    Requestor(Resource res) {
        this.res = res;
    }

    @Override
    public void run() {
        while (true) {
            res.request(new Random().nextInt(200));
        }
    }
}
