import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class client {
    public static void main(String[] args) {
        // (3) gRPC 통신 채널 생성
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // 보안되지 않은 채널 사용 (개발 환경에서는 주로 사용)
                .build();

        // (4) Stub 생성
        MyServiceGrpc.MyServiceBlockingStub stub = MyServiceGrpc.newBlockingStub(channel);

        // (5) 메시지 생성
        HelloGrpc.MyNumber request = HelloGrpc.MyNumber.newBuilder().setValue(4).build();

        // (6) 원격 함수 호출
        HelloGrpc.MyNumber response = stub.myFunction(request);

        // (7) 결과 출력
        System.out.println("gRPC result: " + response.getValue());

        // 채널 종료
        channel.shutdown();
    }
}
