import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class server {

    // (4) 서비스 구현 클래스: gRPC 서비스를 구현하고 원격 함수들을 정의
    static class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {

        @Override
        public void myFunction(HelloGrpc.MyNumber request, StreamObserver<HelloGrpc.MyNumber> responseObserver) {
            // (5.2) proto의 메시지 클래스와 동일한 객체를 생성하여 응답용으로 사용
            HelloGrpc.MyNumber response = HelloGrpc.MyNumber.newBuilder()
                    // (5.3) 입력 데이터를 처리하고 응답 메시지에 설정
                    .setValue(myFunc(request.getValue()))
                    .build();

            // (5.4) 응답을 클라이언트에 전달하고 완료 처리
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        // 원격 함수 호출로 사용할 실제 로직
        private int myFunc(int value) {
            return value * value; // 예제 로직: 입력 값을 제곱
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // (6) gRPC 서버를 생성
        Server server = ServerBuilder.forPort(50051)
                .addService(new MyServiceImpl()) // (7) 서비스 추가
                .build();

        System.out.println("Starting server. Listening on port 50051.");
        server.start();

        // (9) 서버가 계속 실행되도록 유지
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
