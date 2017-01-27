package com.theleapofcode.grpc.server;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;

public class UserServiceServer {
	public static void main(String[] args) {
		try {
			UserServiceServer userServiceServer = new UserServiceServer();
			userServiceServer.start();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private Server server;

	private void start() throws Exception {
		final int port = 3000;

		// openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem
		// -days 365 -nodes
		InputStream certStream = ClassLoader.getSystemResourceAsStream("cert.pem");
		InputStream keyStream = ClassLoader.getSystemResourceAsStream("key.pem");
		File certFile = File.createTempFile("cert", "pem");
		certFile.deleteOnExit();
		FileUtils.copyInputStreamToFile(certStream, certFile);
		File keyFile = File.createTempFile("key", "pem");
		keyFile.deleteOnExit();
		FileUtils.copyInputStreamToFile(keyStream, keyFile);

		UserService userService = new UserService();

		ServerServiceDefinition serviceDef = ServerInterceptors.interceptForward(userService,
				new HeaderServerInterceptor());

		server = ServerBuilder.forPort(port).useTransportSecurity(certFile, keyFile).addService(serviceDef).build()
				.start();
		System.out.println("Listening on port " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutting down server");
				UserServiceServer.this.stop();
			}
		});

		server.awaitTermination();
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}
}
