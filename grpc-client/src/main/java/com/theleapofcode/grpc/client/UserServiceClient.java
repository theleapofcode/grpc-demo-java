package com.theleapofcode.grpc.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.google.protobuf.ByteString;
import com.theleapofcode.grpc.Messages.AddPhotoRequest;
import com.theleapofcode.grpc.Messages.AddPhotoResponse;
import com.theleapofcode.grpc.Messages.GetAllUsersRequest;
import com.theleapofcode.grpc.Messages.GetUserByEmailRequest;
import com.theleapofcode.grpc.Messages.User;
import com.theleapofcode.grpc.Messages.UserRequest;
import com.theleapofcode.grpc.Messages.UserResponse;
import com.theleapofcode.grpc.UserServiceGrpc;
import com.theleapofcode.grpc.UserServiceGrpc.UserServiceBlockingStub;
import com.theleapofcode.grpc.UserServiceGrpc.UserServiceStub;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;

public class UserServiceClient {

	public static void main(String[] args) throws Exception {
		InputStream certStream = ClassLoader.getSystemResourceAsStream("cert.pem");
		File certFile = File.createTempFile("cert", "pem");
		certFile.deleteOnExit();
		FileUtils.copyInputStreamToFile(certStream, certFile);

		int port = 3000;

		ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", port)
				.sslContext(GrpcSslContexts.forClient().trustManager(certFile).build()).build();

		UserServiceBlockingStub blockingClient = UserServiceGrpc.newBlockingStub(channel);
		UserServiceStub nonBlockingClient = UserServiceGrpc.newStub(channel);

		switch (args[0]) {
		case "blocking":
			switch (Integer.parseInt(args[1])) {
			case 1:
				sendMetadata(blockingClient);
				break;
			case 2:
				getUserByEmail(blockingClient);
				break;
			case 3:
				getAllUsers(blockingClient);
				break;
			case 4:
				saveUser(blockingClient);
				break;
			default:
				break;
			}
			break;
		case "nonblocking":
			switch (Integer.parseInt(args[1])) {
			case 1:
				sendMetadata(nonBlockingClient);
				break;
			case 2:
				getUserByEmail(nonBlockingClient);
				break;
			case 3:
				getAllUsers(nonBlockingClient);
				break;
			case 4:
				saveUser(nonBlockingClient);
				break;
			case 5:
				saveAllUsers(nonBlockingClient);
				break;
			case 6:
				addPhoto(nonBlockingClient);
				break;
			default:
				break;
			}
			break;
		default:
			System.err.println("arg[0] should be blocking/nonblocking");
		}

		TimeUnit.SECONDS.sleep(2);
		channel.shutdown();
		channel.awaitTermination(1, TimeUnit.SECONDS);
	}

	private static void sendMetadata(UserServiceBlockingStub blockingClient) {
		Metadata md = new Metadata();
		md.put(Metadata.Key.of("username", Metadata.ASCII_STRING_MARSHALLER), "theleapofcode");
		md.put(Metadata.Key.of("password", Metadata.ASCII_STRING_MARSHALLER), "avengersassemble");

		Channel ch = ClientInterceptors.intercept(blockingClient.getChannel(),
				MetadataUtils.newAttachHeadersInterceptor(md));

		blockingClient.withChannel(ch)
				.getUserByEmail(GetUserByEmailRequest.newBuilder().setEmail("ironman@avengers.com").build());
	}

	private static void sendMetadata(UserServiceStub nonBlockingClient) {
		Metadata md = new Metadata();
		md.put(Metadata.Key.of("username", Metadata.ASCII_STRING_MARSHALLER), "theleapofcode");
		md.put(Metadata.Key.of("password", Metadata.ASCII_STRING_MARSHALLER), "avengersassemble");

		Channel ch = ClientInterceptors.intercept(nonBlockingClient.getChannel(),
				MetadataUtils.newAttachHeadersInterceptor(md));

		nonBlockingClient.withChannel(ch).getUserByEmail(
				GetUserByEmailRequest.newBuilder().setEmail("ironman@avengers.com").build(),
				new StreamObserver<UserResponse>() {
					@Override
					public void onNext(UserResponse userResponse) {
					}

					@Override
					public void onError(Throwable err) {
					}

					@Override
					public void onCompleted() {
					}
				});
	}

	private static void getUserByEmail(UserServiceBlockingStub blockingClient) {
		UserResponse response = blockingClient
				.getUserByEmail(GetUserByEmailRequest.newBuilder().setEmail("ironman@avengers.com").build());
		System.out.println(response.getUser());
	}

	private static void getUserByEmail(UserServiceStub nonBlockingClient) {
		nonBlockingClient.getUserByEmail(GetUserByEmailRequest.newBuilder().setEmail("ironman@avengers.com").build(),
				new StreamObserver<UserResponse>() {
					@Override
					public void onNext(UserResponse userResponse) {
						System.out.println(userResponse.getUser());
					}

					@Override
					public void onError(Throwable err) {
						System.err.println(err);
					}

					@Override
					public void onCompleted() {
						System.out.println("getUserByEmail response complete");
					}
				});
	}

	private static void getAllUsers(UserServiceBlockingStub blockingClient) {
		Iterator<UserResponse> responseIterator = blockingClient.getAllUsers(GetAllUsersRequest.newBuilder().build());
		while (responseIterator.hasNext()) {
			System.out.println(responseIterator.next().getUser());
		}
	}

	private static void getAllUsers(UserServiceStub nonBlockingClient) {
		nonBlockingClient.getAllUsers(GetAllUsersRequest.newBuilder().build(), new StreamObserver<UserResponse>() {
			@Override
			public void onNext(UserResponse userResponse) {
				System.out.println(userResponse.getUser());
			}

			@Override
			public void onError(Throwable err) {
				System.err.println(err);
			}

			@Override
			public void onCompleted() {
				System.out.println("getAllUsers response complete");
			}
		});
	}

	private static void saveUser(UserServiceBlockingStub blockingClient) {
		UserResponse response = blockingClient.saveUser(UserRequest.newBuilder().setUser(User.newBuilder().setId(4)
				.setFirstName("Thor").setLastName("Odinson").setEmail("thor@avengers.com").build()).build());
		System.out.println(response.getUser());
	}

	private static void saveUser(UserServiceStub nonBlockingClient) {
		nonBlockingClient
				.saveUser(
						UserRequest.newBuilder()
								.setUser(User.newBuilder().setId(4).setFirstName("Thor").setLastName("Odinson")
										.setEmail("thor@avengers.com").build())
								.build(),
						new StreamObserver<UserResponse>() {
							@Override
							public void onNext(UserResponse userResponse) {
								System.out.println(userResponse.getUser());
							}

							@Override
							public void onError(Throwable err) {
								System.err.println(err);
							}

							@Override
							public void onCompleted() {
								System.out.println("saveUser response complete");
							}
						});
	}

	private static void saveAllUsers(UserServiceStub nonBlockingClient) {
		List<User> users = new ArrayList<>();
		users.add(User.newBuilder().setId(5).setFirstName("Clint").setLastName("Barton")
				.setEmail("hawkeye@avengers.com").build());
		users.add(User.newBuilder().setId(6).setFirstName("Natasha").setLastName("Romonov")
				.setEmail("blackwidow@avengers.com").build());
		StreamObserver<UserRequest> requestStream = nonBlockingClient.saveAllUsers(new StreamObserver<UserResponse>() {
			@Override
			public void onNext(UserResponse userResponse) {
				System.out.println(userResponse.getUser());
			}

			@Override
			public void onError(Throwable err) {
				System.err.println(err);
			}

			@Override
			public void onCompleted() {
				System.out.println("saveAllUsers response complete");
			}
		});

		for (User user : users) {
			requestStream.onNext(UserRequest.newBuilder().setUser(user).build());
		}
		requestStream.onCompleted();
	}

	private static void addPhoto(UserServiceStub nonBlockingClient) {
		StreamObserver<AddPhotoRequest> requestStream = nonBlockingClient
				.addPhoto(new StreamObserver<AddPhotoResponse>() {

					@Override
					public void onNext(AddPhotoResponse addPhotoResponse) {
						System.out.println(addPhotoResponse.getOk());
					}

					@Override
					public void onError(Throwable err) {
						System.err.println(err);
					}

					@Override
					public void onCompleted() {
						System.out.println("addPhoto response complete");
					}
				});

		try {
			InputStream photoStream = ClassLoader.getSystemResourceAsStream("ironman.png");
			while (true) {
				byte[] data = new byte[64 * 1024];

				int bytesRead = photoStream.read(data);
				if (bytesRead == -1) {
					break;
				}
				if (bytesRead < data.length) {
					byte[] newData = new byte[bytesRead];
					System.arraycopy(data, 0, newData, 0, bytesRead);
					data = newData;
				}

				requestStream.onNext(AddPhotoRequest.newBuilder().setData(ByteString.copyFrom(data)).build());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		requestStream.onCompleted();
	}

}
