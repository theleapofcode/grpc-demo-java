package com.theleapofcode.grpc.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class HeaderServerInterceptor implements ServerInterceptor {

	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> sc, Metadata md,
			ServerCallHandler<ReqT, RespT> next) {
		if (sc.getMethodDescriptor().getFullMethodName().equalsIgnoreCase("UserService/GetUserByEmail")) {
			for (String key : md.keys()) {
				System.out.println(key + ": " + md.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)));
			}
		}

		return next.startCall(sc, md);
	}

}
