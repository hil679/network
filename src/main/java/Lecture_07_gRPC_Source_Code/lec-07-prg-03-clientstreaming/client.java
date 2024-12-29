import clientstreaming.ClientStreamingGrpc;
import clientstreaming.Clientstreaming;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public class client {
    private static Clientstreaming.Message makeMessage(String message) {
        return Clientstreaming.Message.newBuilder()
                .setMessage(message)
                .build();
    }
    private static Iterator<Clientstreaming.Message> generateMessages() {
        return new Iterator<>() {
            private final Clientstreaming.Message[] messages = {
                    makeMessage("message #1"),
                    makeMessage("message #2"),
                    makeMessage("message #3"),
                    makeMessage("message #4"),
                    makeMessage("message #5")
            };
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < messages.length;
            }

            @Override
            public Clientstreaming.Message next() {
                Clientstreaming.Message msg = messages[index++];
                System.out.println("[client to server] " + msg.getMessage());
                return msg;
            }
        };
    }
    private static void sendMessage(ClientStreamingGrpc.ClientStreamingStub stub, CountDownLatch latch) {
        StreamObserver<Clientstreaming.Message> requestObserver = stub.getServerResponse(new StreamObserver<>() {

            @Override
            public void onNext(Clientstreaming.Number value) {
                System.out.println("[server to client] "+  value.getValue());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Iterator<Clientstreaming.Message> messages = generateMessages();
        while (messages.hasNext()) {
            requestObserver.onNext(messages.next());
        }
        requestObserver.onCompleted();
    }
    public static void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ClientStreamingGrpc.ClientStreamingStub stub = ClientStreamingGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);
        sendMessage(stub, latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            channel.shutdown();
        }
    }

    public static void main(String[] args) {
        run();
    }
}
