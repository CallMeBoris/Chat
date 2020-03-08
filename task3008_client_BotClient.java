package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BotClient extends Client {

    public class BotSocketThread extends SocketThread{
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (message.contains(":")) {
                String[] n = message.split(": ");
                String name = n[0];
                DateFormat format1 = new SimpleDateFormat("d.MM.YYYY");
                DateFormat format2 = new SimpleDateFormat("d");
                DateFormat format3 = new SimpleDateFormat("MMMM");
                DateFormat format4 = new SimpleDateFormat("YYYY");
                DateFormat format5 = new SimpleDateFormat("H:mm:ss");
                DateFormat format6 = new SimpleDateFormat("H");
                DateFormat format7 = new SimpleDateFormat("m");
                DateFormat format8 = new SimpleDateFormat("s");
                Calendar calendar = Calendar.getInstance();
                if ((n[1].equals("дата")) || (n[1].equals("день")) || (n[1].equals("месяц")) || (n[1].equals("год")) || (n[1].equals("время")) || (n[1].equals("час")) || (n[1].equals("минуты")) || (n[1].equals("секунды"))) {
                    if (n[1].equals("дата")) {
                        sendTextMessage("Информация для " + n[0] + ": " + format1.format(calendar.getTime()));
                    } else if (n[1].equals("день")) {
                        sendTextMessage("Информация для " + n[0] + ": " + format2.format(calendar.getTime()));
                    } else if (n[1].equals("месяц")) {
                        sendTextMessage("Информация для " + n[0] + ": " + format3.format(calendar.getTime()));
                    } else if (n[1].equals("год")) {
                        sendTextMessage("Информация для " + n[0] + ": " + format4.format(calendar.getTime()));
                    } else if (n[1].equals("время")) {
                        sendTextMessage("Информация для " + n[0] + ": " + format5.format(calendar.getTime()));
                    } else if (n[1].equals("час")) {
                        sendTextMessage("Информация для " + n[0] + ": " + format6.format(calendar.getTime()));
                    } else if (n[1].equals("минуты")) {
                        sendTextMessage("Информация для " + n[0] + ": " + format7.format(calendar.getTime()));
                    } else  {
                        sendTextMessage("Информация для " + n[0] + ": " + format8.format(calendar.getTime()));
                    }
                }
            }
        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_"+(int) (Math.random()*100);
    }

    public static void main(String[] args) throws InterruptedException {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
