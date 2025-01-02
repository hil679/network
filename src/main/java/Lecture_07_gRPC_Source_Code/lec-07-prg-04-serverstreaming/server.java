
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import serverstreaming.ServerStreamingGrpc;
import serverstreaming.Serverstreaming;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {
    private static Serverstreaming.Message makeMessage(String message) {
        return Serverstreaming.Message.newBuilder()
                .setMessage(message)
                .build();
    }
    static class ServerStreamingService extends ServerStreamingGrpc.ServerStreamingImplBase {
        @Override
        public void getServerResponse(Serverstreaming.Number request, StreamObserver<Serverstreaming.Message> responseObserver) {
            final Serverstreaming.Message[] messages = {
                    makeMessage("message #1"),
                    makeMessage("message #2"),
                    makeMessage("message #3"),
                    makeMessage("message #4"),
                    makeMessage("message #5")
            };

            System.out.printf("Server processing gRPC server-streaming {%d}.\n", request.getValue());

            // 메시지 리스트를 스트리밍으로 클라이언트에 보냅니다.
            for (Serverstreaming.Message message : messages) {
                responseObserver.onNext(message);
            }

            // 스트림 종료를 알립니다.
            responseObserver.onCompleted();
        }
    }
    private static void serve() throws InterruptedException, IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Server server = ServerBuilder.forPort(50051)
                .executor(threadPool)
                .addService(new ServerStreamingService())
                .build();
        System.out.println("Starting server. Listening on port 50051.");

        server.start();
        server.awaitTermination();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        serve();
    }
}

