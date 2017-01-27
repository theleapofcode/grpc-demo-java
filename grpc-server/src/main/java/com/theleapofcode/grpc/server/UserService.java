package com.theleapofcode.grpc.server;

import com.google.protobuf.ByteString;
import com.theleapofcode.grpc.Messages.AddPhotoRequest;
import com.theleapofcode.grpc.Messages.AddPhotoResponse;
import com.theleapofcode.grpc.Messages.GetAllUsersRequest;
import com.theleapofcode.grpc.Messages.GetUserByEmailRequest;
import com.theleapofcode.grpc.Messages.User;
import com.theleapofcode.grpc.Messages.UserRequest;
import com.theleapofcode.grpc.Messages.UserResponse;
import com.theleapofcode.grpc.UserServiceGrpc;

import io.grpc.stub.StreamObserver;

public class UserService extends UserServiceGrpc.UserServiceImplBase {

	@Override
	public void getUserByEmail(GetUserByEmailRequest request, StreamObserver<UserResponse> responseObserver) {
		for (User user : Users.getInstance()) {
			if (user.getEmail().equals(request.getEmail())) {
				responseObserver.onNext(UserResponse.newBuilder().setUser(user).build());
				responseObserver.onCompleted();
				return;
			}
		}
		responseObserver.onError(new RuntimeException("No user found with email " + request.getEmail()));
	}

	@Override
	public void getAllUsers(GetAllUsersRequest request, StreamObserver<UserResponse> responseObserver) {
		for (User user : Users.getInstance()) {
			responseObserver.onNext(UserResponse.newBuilder().setUser(user).build());
		}
		responseObserver.onCompleted();
	}

	@Override
	public void saveUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
		Users.getInstance().add(request.getUser());
		responseObserver.onNext(UserResponse.newBuilder().setUser(request.getUser()).build());
		responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<UserRequest> saveAllUsers(StreamObserver<UserResponse> responseObserver) {
		return new StreamObserver<UserRequest>() {

			@Override
			public void onNext(UserRequest userRequest) {
				Users.getInstance().add(userRequest.getUser());
				responseObserver.onNext(UserResponse.newBuilder().setUser(userRequest.getUser()).build());
			}

			@Override
			public void onCompleted() {
				for (User user : Users.getInstance()) {
					System.out.println(user);
				}
				responseObserver.onCompleted();
			}

			@Override
			public void onError(Throwable err) {
				System.err.println(err);
			}

		};
	}

	@Override
	public StreamObserver<AddPhotoRequest> addPhoto(StreamObserver<AddPhotoResponse> responseObserver) {
		return new StreamObserver<AddPhotoRequest>() {

			private ByteString buffer;

			@Override
			public void onNext(AddPhotoRequest addPhotoRequest) {
				if (buffer == null) {
					buffer = addPhotoRequest.getData();
				} else {
					buffer.concat(addPhotoRequest.getData());
				}
				System.out.println("Received data of size " + addPhotoRequest.getData().size() + " bytes");
			}

			@Override
			public void onError(Throwable err) {
				System.err.println(err);
			}

			@Override
			public void onCompleted() {
				System.out.println("Total received data size " + buffer.size() + " bytes");
				responseObserver.onNext(AddPhotoResponse.newBuilder().setOk(true).build());
				responseObserver.onCompleted();
			}
		};
	}

}
