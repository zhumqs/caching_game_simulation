package com.zhumqs.thread;

/**
 * @author mingqizhu
 * @date 20191201
 */
public class Receiver implements Runnable{
    private Resource res;

    Receiver(Resource res) {
        this.res = res;
    }

    @Override
    public void run() {
        while (true) {
            res.receive();
        }
    }
}
