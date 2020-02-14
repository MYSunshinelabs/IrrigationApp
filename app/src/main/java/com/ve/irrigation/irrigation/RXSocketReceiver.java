package com.ve.irrigation.irrigation;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import rx.Observable;
import rx.Subscriber;


public class RXSocketReceiver {

    public Observable<String> request(final DatagramSocket socket) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData()).trim();
                    System.out.println("RXSocketReceiver.cal" + message);
                    subscriber.onNext(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(new IOException("error"));
                }

            }
        });
    }


}