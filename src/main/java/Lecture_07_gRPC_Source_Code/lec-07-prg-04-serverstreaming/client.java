import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import serverstreaming.ServerStreamingGrpc;
import serverstreaming.Serverstreaming;

import java.util.concurrent.CountDownLatch;


public class client {

    private static void recv_message(ServerStreamingGrpc.ServerStreamingStub stub, CountDownLatch latch) {
        Serverstreaming.Number request = Serverstreaming.Number.newBuilder()
                .setValue(5)
                .build();
        StreamObserver<Serverstreaming.Message> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(Serverstreaming.Message message) {
                // 서버로부터 메시지를 받았을 때 출력
                System.out.println("[server to client] " + message.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                // 에러 발생 시 처리
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                // 서버가 응답을 모두 전송한 후 호출
                latch.countDown();
            }
        };

        stub.getServerResponse(request, responseObserver);
    }
    public static void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ServerStreamingGrpc.ServerStreamingStub stub = ServerStreamingGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);
        recv_message(stub, latch);

        try {
            latch.await();  // 서버가 메시지를 모두 전송하고 종료할 때까지 기다림
        } catch (InterruptedException e) {
            channel.shutdown();
        }
    }

    public static void main(String[] args) {
        run();
    }
}
