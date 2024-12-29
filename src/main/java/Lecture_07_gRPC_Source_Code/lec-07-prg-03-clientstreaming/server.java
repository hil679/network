import clientstreaming.ClientStreamingGrpc;
import clientstreaming.Clientstreaming;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {
    static class ClientStreamingServicer extends ClientStreamingGrpc.ClientStreamingImplBase {

        @Override
        public StreamObserver<Clientstreaming.Message> getServerResponse(
                StreamObserver<Clientstreaming.Number> responseObserver) {

            System.out.println("Server processing gRPC client-streaming.");

            return new StreamObserver<>() {
                private int count = 0;

                @Override
                public void onNext(Clientstreaming.Message message) {
                    count++;
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    Clientstreaming.Number response = Clientstreaming.Number.newBuilder()
                            .setValue(count)
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            };
        }
    }
    private static void serve() throws InterruptedException, IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Server server = ServerBuilder.forPort(50051)
                .executor(threadPool)
                .addService(new ClientStreamingServicer())
                .build();
        System.out.println("Starting server. Listening on port 50051.");

        server.start();
        server.awaitTermination();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        serve();
    }
}

