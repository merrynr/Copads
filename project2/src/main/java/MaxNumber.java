public class MaxNumber extends Thread{
    Host host;

    public MaxNumber(Host current) {
        host = current;
    }

    public void run() {

        while(true) {

            host.genNumber();
            String numberMsg = "Number " + host.getHostName() + " " + host.getSendCount() +
                    " " + host.getNumber();
            host.addMessage(numberMsg);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            host.showMaxNumber();
        }
    }
}
