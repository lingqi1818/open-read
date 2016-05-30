import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;

public class Test {

    /**
     * @param args
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args) throws Exception {
        //        Lock lock = new ReentrantLock();
        //        lock.unlock();
        //        System.out.println("你好");
        //        System.out.println(1 % 7);
        //
        //        for (int i = 0; i < 1000; i++) {
        //            System.out.println((int) (new Date().getTime() % Long.valueOf(10)));
        //        }

        //        int i=0;
        //        System.out.println(i++);
        ThreadPoolExecutor cmdExecutor = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1), new DiscardOldestPolicy());
        for (int i = 0; i < 100000; i++)
            cmdExecutor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        while (true && !Thread.currentThread().isInterrupted()) {
                            //System.out.println(Thread.currentThread().isInterrupted());
                        }
                    } finally {
                        System.out.println("aaaaa");
                    }
                }
            });
        Thread.currentThread().sleep(3000);
        cmdExecutor.shutdown();
    }

}
