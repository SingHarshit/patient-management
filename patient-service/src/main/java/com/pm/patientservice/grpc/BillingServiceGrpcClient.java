package com.pm.patientservice.grpc;


import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

//localhost:9001/BillingService/CreatePatientAccount
    //aws.grpc
    public BillingServiceGrpcClient( @Value("${BILLING_SERVICE_ADDRESS:localhost}") String serverAddress,
                                     @Value("${BILLING_SERVICE_GRPC_PORT:9001}") int serverPort)
    {
        System.out.println(">>>> BILLING GRPC CLIENT INIT " + serverAddress + ":" + serverPort);
        log.info("Initializing BillingServiceGrpcClient at {}:{}", serverAddress, serverPort)
        ;

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);

    }
    public BillingResponse createBillingAccount(String patientId,String name,String email){
        BillingRequest request = BillingRequest.newBuilder().setPatient(patientId).setName(name).setEmail(email).build();

        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("Response from BillingServiceGrpc is {}", response);
        return response;
    }
}
