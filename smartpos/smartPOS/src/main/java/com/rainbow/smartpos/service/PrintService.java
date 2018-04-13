package com.rainbow.smartpos.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.rainbow.smartpos.printer.LocalPrinter;
import com.rainbow.smartpos.Restaurant;

import java.io.IOException;

public class PrintService extends Service {

    SocketPrintServer printServer;

    public static class PrinterServiceBinder extends Binder {

        private PrintService service;

        public PrinterServiceBinder(PrintService service) {
            this.service = service;
        }

        public PrintService getService() {
            return service;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PrinterServiceBinder(this);
    }

    private Thread listenThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                printServer.listen();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    });

    @Override
    public void onCreate() {
    }

    public void onStop() {
        if (printServer != null) if (printServer.isListening())
            printServer.stop();
    }

    @Override
    public void onDestroy() {
        Restaurant.isPrintServiceRunning = false;
        if (printServer != null) if (printServer.isListening())
            printServer.stop();
        super.onDestroy();
    }


    public boolean restartServer(LocalPrinter printer) {
        if (printServer != null && printServer.isListening()) {
            printServer.setPrinter(printer);
        } else {
            try {
                printServer = new SocketPrintServer(printer, Integer.parseInt(getString(com.rainbow.smartpos.R.string.printer_server_port)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (printServer != null) {
                if (listenThread.isAlive()) {
                    listenThread.interrupt();
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                listenThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            printServer.listen();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                listenThread.start();
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (printServer.isListening()) {
            return true;
        } else {
            return false;
        }
    }
}
